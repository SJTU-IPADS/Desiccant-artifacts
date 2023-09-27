import asyncio
import multiprocessing
from multiprocessing import Process, Array
import json
import os
import sys
import time

cpu_set_1 = (0,19)
cpu_set_2 = (20,39)

script_path_prefix = "../../application/"
output_path_prefix = "./result/"
warmup_path_prefix = "./result/warmup"


scale_factor = 1.0

async def invoke(script_path, output_path, interval_list, process_idx):
    cmd_pre = "sh %s/scripts/invoke.sh > %s/invoke-%d-" %(script_path,output_path, process_idx)
    cmd_post = ".txt &"
    cur_cnt = 0
    for interval_item in interval_list:
        invoke_cnt = interval_item[0]
        invoke_interval = interval_item[1]
        await asyncio.sleep(invoke_interval/scale_factor)
        for i in range(invoke_cnt):
            os.system(cmd_pre + str(cur_cnt) + cmd_post)
            print(cmd_pre + str(cur_cnt) + cmd_post)
            cur_cnt += 1
            await asyncio.sleep(invoke_interval/scale_factor)



def run_in_one_process(tasks, process_idx, ignore_output=False):
    print("run_in_one_process: ", tasks, process_idx)
    task_list = []
    for task in tasks:
        language_type = task[0]
        app_name = task[1]
        interval_list = task[2]
        if (ignore_output):
            cur_result_prefix = warmup_path_prefix
        else:
            cur_result_prefix = output_path_prefix
        os.system("mkdir -p %s/%s/%s" %(cur_result_prefix, language_type, app_name))
        if (ignore_output):
            process_idx = -1
        invokeCoro = invoke("%s/%s/%s" %(script_path_prefix, language_type, app_name),
               "%s/%s/%s" %(cur_result_prefix, language_type, app_name),
               interval_list, process_idx)
        task_list.append(invokeCoro)

    loop = asyncio.get_event_loop()
    loop.run_until_complete(asyncio.wait(task_list))



def run_in_multiple_process(tasks, process_count, wait_till_end = False):
    total_load = 0
    #divide into similar load?
    for task in tasks:
        interval_list = task[2]
        invoke_time_sum = 0
        invoke_cnt_sum = 0
        for interval_item in interval_list:
            invoke_cnt = interval_item[0]
            invoke_interval = interval_item[1]
            invoke_time_sum += invoke_interval * (invoke_cnt + 1)
            invoke_cnt_sum += invoke_cnt
        cur_load = invoke_cnt_sum / invoke_time_sum
        total_load += cur_load

    load_per_process = total_load / process_count
    print(total_load, load_per_process)

    cur_begin = 0
    splited_tasks = []
    cur_load_sum = 0
    for i in range(len(tasks)):
        interval_list = tasks[i][2]
        invoke_time_sum = 0
        invoke_cnt_sum = 0
        for interval_item in interval_list:
            invoke_cnt = interval_item[0]
            invoke_interval = interval_item[1]
            invoke_time_sum += invoke_interval * (invoke_cnt + 1)
            invoke_cnt_sum += invoke_cnt
        cur_load = invoke_cnt_sum / invoke_time_sum
        print ("cur_load: ", cur_load)
        cur_load_sum += cur_load
        print ("cur_load_sum: ", cur_load_sum)
        if (cur_load_sum >= load_per_process):
            print("splited at", cur_begin, i + 1)
            splited_tasks.append(tasks[cur_begin:i + 1])
            cur_begin = i + 1

    if (cur_begin < len(tasks)-1):
        splited_tasks.append(tasks[cur_begin:])

    #print(splited_tasks)
    process_list = []
    i = 0
    for splited_task in splited_tasks:
        p = Process(target=run_in_one_process, args=(splited_task,i, wait_till_end))
        process_list.append(p)
        i += 1
        p.start()
    if (wait_till_end):
        for p in process_list:
            p.join()
    else:
        time.sleep(180)
        for p in process_list:
            p.terminate()
        exit(1)

def generate_tasks_from_trace(day, begin = 0, limit = 1440):
    mapping = {
        "e40b7d1d6290f61a44edca13c0c78a83a19f773259804f160832c19785f8fdc6": ["java", "time", []],
        "41046257156e85149551095b3103c42398cfc55bac455c5d2377d5cd0fbf5708": ["java", "sort", []],
        "3ed540fcd10eef07096bb37ae8f3fabffdfbcd8b43c102eaadf60c2913641682": ["java", "file-hash", []],
        "0e2aa176f0fd8d6ea28473bc5c62088894910cb0f49aedb0cae4032b9ead69a5": ["java", "image-resize", []],
        "b3e56864b16ef852cc29c16215a0f275275c0f7b44176e9694e9bc45314f2b2e": ["java", "hotel-seraching", []],
        "535cea69965a78c25992c14e663fa3a38396df379aa9791f3652583b405d4acb": ["java", "image-pipeline", []],
        "28ff09abb2c79c9178afe72f9201ddfa9ddb3ab263a18261a0c1ea8f146b6e62": ["java", "mapreduce", []],
        "3f2122b2e3c3d57bdf5498b39fdec0303fcdfe5003284564443c09fa87a90c33": ["nodejs", "clock", []],
        "30d230e4980f3997948c73c739a22f1f026cee14ba4fd5a2d496cf2bc77f51d1": ["nodejs", "dynamic-html", []],
        "ca19b36ca2996882f6f8b92fbd96fa743de8d6ec586b5bd5c6e089efdf5e9205": ["nodejs", "factor", []],
        "97a154875cd200cb9639245497efa7d7e60564ecb147445d2f7baa801750f35d": ["nodejs", "fft", []],
        "754a84dd3bca4435ac34c014b5c8ccd6c04e4cef255068c79e8a868dc3d5ed90": ["nodejs", "fibonacci", []],
        "d3fdc9ec33413905d3d23ff7873f51339f7c883a3374b6aa05e69a748c91451d": ["nodejs", "filesystem", []],
        "6779a75f188b25a08ab51bcbd5c9c11aff098b4e66c35de47e1bc24a1806c7b1": ["nodejs", "matrix", []],
        "54037b034e3a04df68887b777343f43fd0d88d54522a0feb06ddbc4082fac10f": ["nodejs", "pi", []],
        "593715ec9104e0ac527c30f63130db1b3329450a0c496eba4510ffc214dd5f3d": ["nodejs", "unionfind", []],
        "093d160f0340e55fc723817be3573306c3eac788fa46d53dfe64b5304299783c": ["nodejs", "web-server", []],
        "9f8280f47f19c426a1ed615e75ef96c75d9b52b8abfbcea4270aeb15bdf0f663": ["nodejs", "data-analysis", []],
        "ca93289cdf061464ea5733d0d7a215aec3eb35bdb0ea2c049cd51eb5ae3a4e09": ["nodejs", "alexa", []],
    }

    filename =  "./azurefunctions-dataset2019/invocations_per_function_md.anon.d%s.csv" %(str(day).zfill(2),)
    print(filename)
    print(scale_factor)
    f = open(filename)
    f.readline()    # ignore head
    for line in f:
        words = line.split(",")
        function_hash = words[2]
        if (function_hash in mapping):
            function = mapping[function_hash]
            invocation_minutes = words[4:]
            i = begin
            cur_empty_interval = 0
            while (i < limit):
                if (invocation_minutes[i] == '0'):
                    cur_empty_interval += 60
                else:
                    function[2].append((0, cur_empty_interval))
                    cur_minute_cnt = int(invocation_minutes[i])
                    function[2].append((cur_minute_cnt, 60/(cur_minute_cnt + 1)))
                    cur_empty_interval = 0
                i += 1
    tasks = []
    for key in mapping:
        print(mapping[key])
        tasks.append(mapping[key])
    return tasks

def test_micro():
    tasks = []
    for i in range(5):
        tasks.append(["nodejs", "clock", [(1000, 0.05)]])
    run_in_multiple_process(tasks, 5)



warm_up_factor = 15
def test_azure(arg_factor):
    cpu_set = set(range(cpu_set_1[0], cpu_set_1[1]+1))
    os.sched_setaffinity(0, cpu_set)
    global scale_factor
    warm_up_tasks = generate_tasks_from_trace(1, 0, warm_up_factor)
    execution_tasks = generate_tasks_from_trace(1, warm_up_factor, 1440)
    scale_factor = warm_up_factor
    run_in_multiple_process(warm_up_tasks, 5, True)
    time.sleep(5)
    os.system("cat ~/tmp/openwhisk/invoker/logs/invoker-local_logs.log | wc -l > ./linecnt.txt")
    os.system("sar -P %s-%s  1 500 > cpu.txt &" %(cpu_set_2[0], cpu_set_2[1]))
    scale_factor = arg_factor
    run_in_multiple_process(execution_tasks, 5, False)

arg_factor = float(sys.argv[1])
test_azure(arg_factor)

