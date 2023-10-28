def get_uss_from_pmap(pmap_dir, cnt):
    pmap_f = open("%s/pmap-%d.txt" % (pmap_dir, cnt))
    pmap_lines = pmap_f.readlines()
    if len(pmap_lines) == 0:
        print("Warning: %s data is None, please check it." % pmap_dir)
        return 0
    total_private_clean = int(pmap_lines[-1][:-1].split()[7])
    total_private_dirty = int(pmap_lines[-1][:-1].split()[8])
    return (total_private_clean + total_private_dirty) / 1024.0

def parse_single_memory_config(memory, single_apps, chained_apps, chained_apps_cnt):
    tmp_results = {}
    for app in single_apps:
        vanilla_memory = get_uss_from_pmap("./result-%d/%s-vanilla/" %(memory, app), 100)
        eager_memory =  get_uss_from_pmap("./result-%d/%s-eager/" %(memory, app), 100)
        desiccant_memory = get_uss_from_pmap("./result-%d/%s-desiccant/" %(memory, app), 100)
        tmp_results[app] = [vanilla_memory, eager_memory, desiccant_memory]

    for i in range(len(chained_apps)):
        app = chained_apps[i]
        cnt = chained_apps_cnt[i]
        cur_app_total_vanilla = 0
        cur_app_total_eager = 0
        cur_app_total_desiccant = 0

        for j in range(cnt):
            vanilla_memory = get_uss_from_pmap("./result-%d/%s-vanilla/%d/" %(memory, app,j), 100)
            eager_memory = get_uss_from_pmap("./result-%d/%s-eager/%d/" % (memory, app,j), 100)
            desiccant_memory =  get_uss_from_pmap("./result-%d/%s-desiccant/%d/" %(memory, app,j), 100)
            cur_app_total_vanilla += vanilla_memory
            cur_app_total_eager += eager_memory
            cur_app_total_desiccant += desiccant_memory
        tmp_results[app] = [cur_app_total_vanilla, cur_app_total_eager, cur_app_total_desiccant]
        return tmp_results

java_single_apps = ['time', 'sort', 'file-hash', 'image-resize']
java_chained_apps = ['hotel-seraching', 'image-pipeline', 'mapreduce']
java_chained_apps_cnt = [3, 4, 2]

js_single_apps = ['clock','dynamic-html','factor','fft','fibonacci','filesystem','matrix','pi','unionfind', 'web-server']
js_chained_apps = ['data-analysis', 'alexa']
js_chained_apps_cnt = [6, 8]

memorys = [256, 512, 1024]
js_results = {}
java_results = {}

def get_average(apps):
    total_cnt = 0
    vanilla_total = 0.0
    eager_total = 0.0
    desiccant_total = 0.0
    for app in apps:
        app_memory = apps[app]
        total_cnt += 1
        vanilla_total += app_memory[0]
        eager_total += app_memory[1]
        desiccant_total += app_memory[2]
    return [vanilla_total/total_cnt, eager_total/total_cnt, desiccant_total/total_cnt]


def show_results(mem, results):
    print("%d,%s,%f" %(mem, "Vanilla", results[0]))
    print("%d,%s,%f" %(mem, "Eager", results[1]))
    print("%d,%s,%f" %(mem, "Desiccant", results[2]))

for memory in memorys:
    java_results[memory] = parse_single_memory_config(memory, java_single_apps, java_chained_apps, java_chained_apps_cnt)
    js_results[memory] = parse_single_memory_config(memory, js_single_apps, js_chained_apps, js_chained_apps_cnt)

print("Average (Java)")
print("Memory Budget,Type,Memory Consumption(MB)")
for memory in memorys:
    average_memorys = get_average(java_results[memory])
    show_results(memory, average_memorys)

print("Average (JavaScript)")
print("Memory Budget,Type,Memory Consumption(MB)")
for memory in memorys:
    average_memorys = get_average(js_results[memory])
    show_results(memory, average_memorys)

print("clock")
print("Memory Budget,Type,Memory Consumption(MB)")
for memory in memorys:
    show_results(memory, js_results[memory]['clock'])

print("fft")
print("Memory Budget,Type,Memory Consumption(MB)")
for memory in memorys:
    show_results(memory, js_results[memory]['fft'])

