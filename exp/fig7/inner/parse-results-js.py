single_apps = ['clock','dynamic-html','factor','fft','fibonacci','filesystem','matrix','pi','unionfind', 'web-server']
chained_apps = ['data-analysis', 'alexa']
chained_apps_cnt = [6, 8]


def get_uss_from_pmap(pmap_dir, cnt):
    pmap_f = open("%s/pmap-%d.txt" % (pmap_dir, cnt))
    pmap_lines = pmap_f.readlines()
    total_private_clean = int(pmap_lines[-1][:-1].split()[7])
    total_private_dirty = int(pmap_lines[-1][:-1].split()[8])
    return (total_private_clean + total_private_dirty) / 1024.0

print("Memory Consumption (MB) of JavaScript functions")
print("App,Vanilla,Eager,Desiccant")

for app in single_apps:
    vanilla_memory = get_uss_from_pmap("./result/%s-vanilla/" %(app,), 100)
    eager_memory =  get_uss_from_pmap("./result/%s-eager/" %(app,), 100)
    desiccant_memory = get_uss_from_pmap("./result/%s-desiccant/" %(app,), 100)
    print("%s,%.2f,%.2f,%.2f" %(app, vanilla_memory, eager_memory, desiccant_memory))

for i in range(len(chained_apps)):
    app = chained_apps[i]
    cnt = chained_apps_cnt[i]
    cur_app_total_vanilla = 0
    cur_app_total_eager = 0
    cur_app_total_desiccant = 0

    for j in range(cnt):
        vanilla_memory = get_uss_from_pmap("./result/%s-vanilla/%d/" %(app,j), 100)
        eager_memory = get_uss_from_pmap("./result/%s-eager/%d/" % (app,j), 100)
        desiccant_memory =  get_uss_from_pmap("./result/%s-desiccant/%d/" %(app,j), 100)
        cur_app_total_vanilla += vanilla_memory
        cur_app_total_eager += eager_memory
        cur_app_total_desiccant += desiccant_memory
    print("%s,%.2f,%.2f,%.2f" %(app, cur_app_total_vanilla, cur_app_total_eager, cur_app_total_desiccant))