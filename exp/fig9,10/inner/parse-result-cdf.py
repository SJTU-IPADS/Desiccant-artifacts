import os
apps = [
    ["java", "time"],
    ["java", "sort"],
    ["java", "file-hash"],
    ["java", "image-resize"],
    ["java", "hotel-seraching"],
    ["java", "image-pipeline"],
    ["java", "mapreduce"],
    ["nodejs", "clock"],
    ["nodejs", "dynamic-html"],
    ["nodejs", "factor"],
    ["nodejs", "fft"],
    ["nodejs", "fibonacci"],
    ["nodejs", "filesystem"],
    ["nodejs", "matrix"],
    ["nodejs", "pi"],
    ["nodejs", "unionfind"],
    ["nodejs", "web-server"],
    ["nodejs", "data-analysis"],
    ["nodejs", "alexa"],
]


def get_cdf(root_dir):
    all_times = []
    cnt = 0
    for app in apps:
        cur_path_prefix = "%s/%s/%s" % (root_dir, app[0], app[1])
        files = os.listdir(cur_path_prefix)
        for file in files:
            f = open("%s/%s" % (cur_path_prefix, file))
            for line in f:
                if ("[zzm] time" in line):
                    cur_time_in_ns = int(line.split("\t")[1])
                    all_times.append(cur_time_in_ns/1000000.0)
                    cnt += 1
                    break

    all_times = sorted(all_times)
    ret = []
    for i in range(100):
        ret.append(all_times[int(cnt * (i / 100))])
    return ret

def show_one_scale_factor(sf):
    print("Scale Factor = %d" %(sf,))
    print(",Vanilla,Desiccant")
    vanilla_cdf = get_cdf("./vanilla/result-%d" %(sf,))
    desiccant_cdf = get_cdf("./desiccant/result-%d" %(sf,))
    for i in range(100):
        print("%d,%f,%f" %(i, vanilla_cdf[i], desiccant_cdf[i]))

show_one_scale_factor(15)
show_one_scale_factor(25)