import json
single_apps = ['time', 'sort', 'file-hash', 'image-resize']
chained_apps = ['hotel-seraching', 'mapreduce']
chained_apps_cnt = [3, 2]

root_dir_name = "./result"

def get_uss_for_app(app_name):
    vanilla_output_lines = open("%s/vanilla/java-%s.log" %(root_dir_name, app_name), "r").readlines()
    desiccant_output_lines = open("%s/desiccant/java-%s.log" % (root_dir_name, app_name), "r").readlines()

    vanilla_uss = int(vanilla_output_lines[-1].split("\t")[-2])
    desiccant_uss = int(json.loads(desiccant_output_lines[-1].strip())["uss"])
    return(vanilla_uss, desiccant_uss)


print("Memory Consumption (MB) of Java functions")
print("App,Corretto,Desiccant")

for app in single_apps:
    results = get_uss_for_app(app)
    print("%s,%.2f,%.2f" %(app, results[0]/1024.0, results[1]/1024.0))

for i in range(len(chained_apps)):
    app_name = chained_apps[i]
    chain_cnt = chained_apps_cnt[i]
    total_results = [0,0]
    for j in range(chain_cnt):
        cur_result = get_uss_for_app("%s-%d" %(app_name, j))
        total_results[0] += cur_result[0]
        total_results[1] += cur_result[1]
    print("%s,%.2f,%.2f" %(app_name, total_results[0]/1024.0, total_results[1]/1024.0))

