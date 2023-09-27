// Copyright 2012 the V8 project authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.


#include "src/init/v8.h"

#include <fstream>

#include "src/api/api.h"
#include "src/base/atomicops.h"
#include "src/base/once.h"
#include "src/base/platform/platform.h"
#include "src/codegen/cpu-features.h"
#include "src/codegen/interface-descriptors.h"
#include "src/debug/debug.h"
#include "src/deoptimizer/deoptimizer.h"
#include "src/execution/frames.h"
#include "src/execution/isolate.h"
#include "src/execution/runtime-profiler.h"
#include "src/execution/simulator.h"
#include "src/init/bootstrapper.h"
#include "src/libsampler/sampler.h"
#include "src/objects/elements.h"
#include "src/objects/objects-inl.h"
#include "src/profiler/heap-profiler.h"
#include "src/snapshot/snapshot.h"
#include "src/tracing/tracing-category-observer.h"
#include "src/wasm/wasm-engine.h"

// <zzm>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <sys/errno.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <time.h>
// </zzm>

namespace v8 {
namespace internal {

V8_DECLARE_ONCE(init_once);

#ifdef V8_USE_EXTERNAL_STARTUP_DATA
V8_DECLARE_ONCE(init_natives_once);
V8_DECLARE_ONCE(init_snapshot_once);
#endif

v8::Platform* V8::platform_ = nullptr;

bool V8::Initialize() {
  InitializeOncePerProcess();
  return true;
}

void V8::TearDown() {
  wasm::WasmEngine::GlobalTearDown();
#if defined(USE_SIMULATOR)
  Simulator::GlobalTearDown();
#endif
  CallDescriptors::TearDown();
  ElementsAccessor::TearDown();
  RegisteredExtension::UnregisterAll();
  FlagList::ResetAllFlags();  // Frees memory held by string arguments.
}

// <zzm>
typedef struct CleanMapping_t {
  void *start;
  void *end;
  char *file_name;
  long offset;
  int prot;
  int flags;
} CleanMapping;

static CleanMapping *parse_smap_mapping(char *buffer) {
  char *root_pos = strchr(buffer, '/');
  if (root_pos != NULL) {
    CleanMapping *mapping = (CleanMapping *)malloc(sizeof(CleanMapping));
    int file_name_len = strlen(root_pos) - 1;
    mapping->file_name = (char *)malloc(file_name_len + 1);
    memcpy(mapping->file_name, root_pos, file_name_len);
    mapping->file_name[file_name_len] = '\0';

    char *start_pos = buffer, *end_pos;
    mapping->start = (void *) strtoull(start_pos, &end_pos, 16);
    start_pos = end_pos + 1;
    mapping->end = (void *) strtoull(start_pos, &end_pos, 16);
    mapping->flags = MAP_FIXED | (end_pos[4] == 'p' ? MAP_PRIVATE : MAP_SHARED);    // " rwxp"
    mapping->prot = (PROT_READ * (end_pos[1] == 'r')) | (PROT_WRITE * (end_pos[2] == 'w')) | (PROT_EXEC * (end_pos[3] == 'x'));

    end_pos = strchr(end_pos + 1, ' ');
    mapping->offset = strtoul(end_pos + 1, NULL, 16);
    return mapping;
  }
  return NULL;
}

static void clear_clean_mapping() {
#define ZZM_MAX_LINE_LEN 1024
  char buffer[ 2 * ZZM_MAX_LINE_LEN ];
  FILE *fp = fopen("/proc/self/smaps", "r");
  char *prev_line = buffer, *cur_line = &buffer[ZZM_MAX_LINE_LEN], *map_line = NULL;
  CleanMapping *mapping = NULL;
  bool not_shared = true;

  while (fgets(cur_line, ZZM_MAX_LINE_LEN, fp)) {
    if (strncmp(cur_line, "Size:", strlen("Size:")) == 0) {
      map_line = prev_line; prev_line = cur_line;
      not_shared = true;
    } else if (strncmp(cur_line, "Shared_Clean:", strlen("Shared_Clean:")) == 0) {
      char *pos = cur_line + strlen("Shared_Clean: "), *endpos;
      while (*pos == ' ') pos++;
      long num = strtol(pos, &endpos, 10);
      if (endpos == pos) {
        printf("[BUGGY] convert error, %s", cur_line);
      } else if ( num != 0) {
        not_shared = false;
      }
    } else if (not_shared && strncmp(cur_line, "Private_Dirty:", strlen("Private_Dirty:")) == 0) {
      char *pos = cur_line + strlen("Private_Dirty: "), *endpos;
      while (*pos == ' ') pos++;
      long num = strtol(pos, &endpos, 10);
      if (endpos == pos) {
        printf("[BUGGY] convert error, %s", cur_line);
      } else if ( num == 0 && (mapping = parse_smap_mapping(map_line)) != NULL ){
        int fd = open(mapping->file_name, O_RDONLY | O_CLOEXEC);
        if (fd == -1) {
          printf("file open %s, error: %d, %s\n", mapping->file_name, errno, strerror(errno));
        }
        void *remap_ret = mmap(mapping->start, ((char *)mapping->end - (char *)mapping->start), mapping->prot, mapping->flags, fd, mapping->offset);
        if (remap_ret == (void *)-1) {
          printf("mapping error: %d, %s\n", errno, strerror(errno));
        }
        free(mapping->file_name); free(mapping);
      }
      prev_line = buffer; cur_line = &buffer[ZZM_MAX_LINE_LEN];
    }
    char *tmp = prev_line; prev_line = cur_line; cur_line = tmp;
  }
  fclose(fp);
#undef ZZM_MAX_LINE_LEN
}

void send_response(int client_socket, const char *body, const char *content_type) {
  char buffer[4096];
  size_t content_length = strlen(body);
  snprintf(buffer, 4096, "HTTP/1.1 200 OK\r\nContent-Type: %s\r\nContent-Length: %zu\r\nConnection: close\r\n\r\n%s", content_type, content_length, body);
  send(client_socket, buffer, strlen(buffer), 0);
}

static void *listen_thread(void *arg) {
#define ZZM_RECLAIM_PORT 10086
  struct sockaddr_in servaddr;
  int sockfd = socket(AF_INET, SOCK_STREAM, 0);

  memset(&servaddr, 0, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
  servaddr.sin_port = htons(ZZM_RECLAIM_PORT);

  if (bind(sockfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) == -1) {
    fprintf(stderr, "Error: bind() failed\n");
    exit(EXIT_FAILURE);
  }

  if (listen(sockfd, 5) == -1) {
    fprintf(stderr, "Error: listen() failed\n");
    exit(EXIT_FAILURE);
  }
  char response[] = "HTTP/1.1 200 OK\r\nContent-Length: 0\r\nConnection: close\r\n\r\n";
  while (1) {
    int connfd = accept(sockfd, NULL, 0);
    if (connfd == -1) {
      fprintf(stderr, "Error: accept() failed\n");
      continue;
    }
    struct timespec start_time, end_time;
    long elapsed_ns;
    clock_gettime(CLOCK_MONOTONIC, &start_time);

    clear_clean_mapping();   // merge later if needed, not for now due to the trick thread and safepoint problem. left this thread totally background
    clock_gettime(CLOCK_MONOTONIC, &end_time);
    elapsed_ns = (end_time.tv_sec - start_time.tv_sec) * 1000000000 + (end_time.tv_nsec - start_time.tv_nsec);

    char tmpbuffer[64];
    snprintf(tmpbuffer, sizeof(tmpbuffer), "%ld", elapsed_ns);
    send_response(connfd, tmpbuffer, "text/plain");
    close(connfd);
  }
}

static void start_reclaim_server() {
  pthread_t tid;
  int ret = pthread_create(&tid, NULL, listen_thread, NULL);
  if (ret != 0) {
    fprintf(stderr, "Error: pthread_create() failed\n");
    exit(EXIT_FAILURE);
  }
  pthread_detach(tid);
#undef ZZM_RECLAIM_PORT
}
// </zzm>


void V8::InitializeOncePerProcessImpl() {
  FlagList::EnforceFlagImplications();

  if (FLAG_predictable && FLAG_random_seed == 0) {
    // Avoid random seeds in predictable mode.
    FLAG_random_seed = 12347;
  }

  if (FLAG_stress_compaction) {
    FLAG_force_marking_deque_overflows = true;
    FLAG_gc_global = true;
    FLAG_max_semi_space_size = 1;
  }

  if (FLAG_trace_turbo) {
    // Create an empty file shared by the process (e.g. the wasm engine).
    std::ofstream(Isolate::GetTurboCfgFileName(nullptr).c_str(),
                  std::ios_base::trunc);
  }

  // Do not expose wasm in jitless mode.
  //
  // Even in interpreter-only mode, wasm currently still creates executable
  // memory at runtime. Unexpose wasm until this changes.
  // The correctness fuzzers are a special case: many of their test cases are
  // built by fetching a random property from the the global object, and thus
  // the global object layout must not change between configs. That is why we
  // continue exposing wasm on correctness fuzzers even in jitless mode.
  // TODO(jgruber): Remove this once / if wasm can run without executable
  // memory.
  if (FLAG_jitless && !FLAG_correctness_fuzzer_suppressions) {
    FLAG_expose_wasm = false;
  }

  if (FLAG_regexp_interpret_all && FLAG_regexp_tier_up) {
    // Turning off the tier-up strategy, because the --regexp-interpret-all and
    // --regexp-tier-up flags are incompatible.
    FLAG_regexp_tier_up = false;
  }

  // The --jitless and --interpreted-frames-native-stack flags are incompatible
  // since the latter requires code generation while the former prohibits code
  // generation.
  CHECK_WITH_MSG(!FLAG_interpreted_frames_native_stack || !FLAG_jitless,
                 "The --jitless and --interpreted-frames-native-stack flags "
                 "are incompatible.");

  base::OS::Initialize(FLAG_hard_abort, FLAG_gc_fake_mmap);

  if (FLAG_random_seed) SetRandomMmapSeed(FLAG_random_seed);

#if defined(V8_USE_PERFETTO)
  TrackEvent::Register();
#endif
  Isolate::InitializeOncePerProcess();

#if defined(USE_SIMULATOR)
  Simulator::InitializeOncePerProcess();
#endif
  CpuFeatures::Probe(false);
  ElementsAccessor::InitializeOncePerProcess();
  Bootstrapper::InitializeOncePerProcess();
  CallDescriptors::InitializeOncePerProcess();
  wasm::WasmEngine::InitializeOncePerProcess();

  // <zzm>
  if (getenv("ZZM_START_RECLAIM_SERVER") != NULL)
    start_reclaim_server();
  // </zzm>
}

void V8::InitializeOncePerProcess() {
  base::CallOnce(&init_once, &InitializeOncePerProcessImpl);
}

void V8::InitializePlatform(v8::Platform* platform) {
  CHECK(!platform_);
  CHECK(platform);
  platform_ = platform;
  v8::base::SetPrintStackTrace(platform_->GetStackTracePrinter());
  v8::tracing::TracingCategoryObserver::SetUp();
}

void V8::ShutdownPlatform() {
  CHECK(platform_);
  v8::tracing::TracingCategoryObserver::TearDown();
  v8::base::SetPrintStackTrace(nullptr);
  platform_ = nullptr;
}

v8::Platform* V8::GetCurrentPlatform() {
  v8::Platform* platform = reinterpret_cast<v8::Platform*>(
      base::Relaxed_Load(reinterpret_cast<base::AtomicWord*>(&platform_)));
  DCHECK(platform);
  return platform;
}

void V8::SetPlatformForTesting(v8::Platform* platform) {
  base::Relaxed_Store(reinterpret_cast<base::AtomicWord*>(&platform_),
                      reinterpret_cast<base::AtomicWord>(platform));
}

void V8::SetSnapshotBlob(StartupData* snapshot_blob) {
#ifdef V8_USE_EXTERNAL_STARTUP_DATA
  base::CallOnce(&init_snapshot_once, &SetSnapshotFromFile, snapshot_blob);
#else
  UNREACHABLE();
#endif
}
}  // namespace internal

// static
double Platform::SystemClockTimeMillis() {
  return base::OS::TimeCurrentMillis();
}
}  // namespace v8
