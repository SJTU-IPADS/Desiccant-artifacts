import os.path


def get_cold_rate(dir_root):
    cold_cnt = 0
    warm_cnt = 0
    f = open(dir_root + "/coldrate.log")
    for line in f:
        if ("cold" in line):
            cold_cnt += 1
        elif ("warm" in line):
            warm_cnt += 1
    return((cold_cnt + warm_cnt) / 180.0, cold_cnt * 1.0 / (cold_cnt + warm_cnt))

def get_cpu_usage(dir_root):
    f = open(dir_root + "/cpu.txt")
    f.readline()
    lines = f.readlines()
    total = 0
    count = 0
    for line in lines:
        values = line.split()
        if len(values) == 0:
            continue

        if ("CPU" in line):
            count += 1
        else:
            values = line.split()
            idle = float(values[-1])
            usage = 1 - idle / 100
            total += usage
    average = total * 1.0 / count
    return average

def print_one_type_result(type):
    root_dir = "./" + type
    for sf in range(15,100,5):
        cur_dir = "%s/result-%d" %(root_dir, sf)
        if (os.path.exists(cur_dir)):
            (throughput, cold_rate) = get_cold_rate(cur_dir)
            cpu_usage = get_cpu_usage(cur_dir)
            print("%s,%d,%f,%f,%f" %(type, sf, cold_rate, throughput, cpu_usage))


print("Type,Scale Factor,Cold Boot Rate,Throughput,CPU Utilization")
print_one_type_result("vanilla")
print_one_type_result("desiccant")





