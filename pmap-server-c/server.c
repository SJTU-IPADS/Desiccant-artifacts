#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>

#define THREAD_POOL_SIZE 80 
#define BUFFER_SIZE 2048 
#define PORT 13579 

typedef struct {
    int client_socket; 
} task_t;

typedef struct {
    pthread_t *threads; 
    int thread_count; 
    task_t *tasks; 
    int task_count; 
    int head; 
    int tail; 
    int shutdown; 
    pthread_mutex_t lock; 
    pthread_cond_t not_empty; 
    pthread_cond_t not_full; 
} thread_pool_t;

void *thread_pool_worker(thread_pool_t *pool);
void handle_request(int client_socket);
void send_response(int client_socket, const char *body, const char *content_type);
void send_error_response(int client_socket, const char *message);

void thread_pool_init(thread_pool_t *pool, int thread_count, int task_count) {
    pool->threads = (pthread_t *)malloc(sizeof(pthread_t) * thread_count);
    pool->thread_count = thread_count;
    pool->tasks = (task_t *)malloc(sizeof(task_t) * task_count);
    pool->task_count = task_count;
    pool->head = 0;
    pool->tail = 0;
    pool->shutdown = 0;
    pthread_mutex_init(&pool->lock, NULL);
    pthread_cond_init(&pool->not_empty, NULL);
    pthread_cond_init(&pool->not_full, NULL);
    for (int i = 0; i < thread_count; i++) {
        pthread_create(&pool->threads[i], NULL, (void *(*)(void *))thread_pool_worker, pool);
    }
}

void thread_pool_destroy(thread_pool_t *pool) {
    pool->shutdown = 1;
    pthread_cond_broadcast(&pool->not_empty);
    for (int i = 0; i < pool->thread_count; i++) {
        pthread_join(pool->threads[i], NULL);
    }
    free(pool->threads);
    free(pool->tasks);
    pthread_mutex_destroy(&pool->lock);
    pthread_cond_destroy(&pool->not_empty);
    pthread_cond_destroy(&pool->not_full);
}

void thread_pool_add_task(thread_pool_t *pool, int client_socket) {
    pthread_mutex_lock(&pool->lock);
    while (pool->tail - pool->head == pool->task_count) {
        printf("tasks full");
        pthread_cond_wait(&pool->not_full, &pool->lock);
    }
    pool->tasks[pool->tail % pool->task_count].client_socket = client_socket;
    pool->tail++;
    pthread_cond_signal(&pool->not_empty);
    pthread_mutex_unlock(&pool->lock);
}

void *thread_pool_worker(thread_pool_t *pool) {
    //char buffer = malloc();
    while (1) {
        pthread_mutex_lock(&pool->lock);
        while (pool->tail == pool->head && !pool->shutdown) {
            pthread_cond_wait(&pool->not_empty, &pool->lock);
        }
        if (pool->shutdown) {
            pthread_mutex_unlock(&pool->lock);
            pthread_exit(NULL);
        }
        int client_socket = pool->tasks[pool->head % pool->task_count].client_socket;
        pool->head++;
        pthread_cond_signal(&pool->not_full);
        pthread_mutex_unlock(&pool->lock);
        handle_request(client_socket);
        close(client_socket);
    }
}

int split_string_to_ints(char* input_str, int* output_arr, int max_output_size) {
    for (int i = 0; i < max_output_size; i++) {
        output_arr[i] = -1;
    }

    int i = 0;
    while (*input_str != '\0' && i < max_output_size) {
        while (*input_str == ' ') {
            input_str++;
        }

        if (*input_str == '\0') {
            break;
        }

        char* endptr;
        int num = (int) strtol(input_str, &endptr, 10);
        if (endptr != input_str) {
            output_arr[i] = num;
            i++;
        } else {
            return i;
        }

        input_str = endptr;
    }
    return i;
}


void handle_request(int client_socket) {
//    printf("handle_request called\n");
    char buffer[BUFFER_SIZE * 2];
    recv(client_socket, buffer, BUFFER_SIZE, 0); 
    char *pid_str = strstr(buffer, "PID="); 
    if (pid_str == NULL) {
        send_error_response(client_socket, "Missing PID parameter");
        return;
    }
    int pid = atoi(pid_str + 4); 
    printf("request pid is: %d\n", pid);
    if (pid <= 0) {
        printf("Invalid PID parameter\n");
        send_error_response(client_socket, "Invalid PID parameter");
        return;
    }
    char command[BUFFER_SIZE];
    snprintf(command, sizeof(command), "sudo pmap -XX %d", pid);
    FILE *fp = popen(command, "r"); 
    if (fp == NULL) {
        printf("Failed to execute command\n");
        send_error_response(client_socket, "Failed to execute command");
        return;
    }
    int cur_line_cnt = 0;

    char *line_buffer = buffer;
    while (fgets(line_buffer, BUFFER_SIZE, fp) != NULL) { 
        size_t buffer_len = strlen(line_buffer);
        cur_line_cnt += 1;
        line_buffer = buffer + (BUFFER_SIZE * (cur_line_cnt % 2));
    }
    pclose(fp);
    line_buffer = buffer + (BUFFER_SIZE * ((cur_line_cnt - 1) % 2));

#define COLUMN_CNT 30
    int splited_line[COLUMN_CNT];
    int cnt = split_string_to_ints(line_buffer, splited_line, COLUMN_CNT);
//    for (int i = 0; i < cnt; i++) {
//        printf("%d: %d, ", i, splited_line[i]);
//    }
//    printf("\n");
    int private_clean = splited_line[7];
    int private_dirty = splited_line[8];
    int total_physical_memory = private_clean + private_dirty;
    snprintf(buffer, sizeof(buffer), "%d", total_physical_memory);
    send_response(client_socket, buffer, "text/plain"); 
}

void send_response(int client_socket, const char *body, const char *content_type) {
    char buffer[BUFFER_SIZE];
    size_t content_length = strlen(body);
    snprintf(buffer, BUFFER_SIZE, "HTTP/1.1 200 OK\r\nContent-Type: %s\r\nContent-Length: %zu\r\n\r\n%s", content_type, content_length, body);
    send(client_socket, buffer, strlen(buffer), 0);
}

void send_error_response(int client_socket, const char *message) {
    char buffer[BUFFER_SIZE];
    snprintf(buffer, BUFFER_SIZE, "HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\nContent-Length: %zu\r\n\r\n%s", strlen(message), message);
    send(client_socket, buffer, strlen(buffer), 0);
}

int main() {
    int server_socket = socket(AF_INET, SOCK_STREAM, 0); 
    struct sockaddr_in server_address = {0};
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = htonl(INADDR_ANY);
    server_address.sin_port = htons(PORT);
    bind(server_socket, (struct sockaddr *)&server_address, sizeof(server_address)); 
    listen(server_socket, 10); 
    thread_pool_t thread_pool;
    thread_pool_init(&thread_pool, THREAD_POOL_SIZE, 200); 
    while (1) {
        int client_socket = accept(server_socket, NULL, NULL); 
        thread_pool_add_task(&thread_pool, client_socket); 
    }
    thread_pool_destroy(&thread_pool); 
    close(server_socket); 
    return 0;
}
