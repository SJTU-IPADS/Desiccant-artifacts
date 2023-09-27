import subprocess
import time
import requests
import sys
import json

num_executions = int(sys.argv[1])
url = "http://172.17.0.5:8080/run"
data={"action_name":"/guest/sort","action_version":"0.0.3","activation_id":"412b4f89c8e74120ab4f89c8e7e12091","deadline":"1690801432041","namespace":"guest","transaction_id":"WWxDsO6iuJ10TPNzC7GijCJZft29vLaf","value":{"seed":1}}
header = {"Content-Type":'application/json'}
encoded_data = json.dumps(data).encode('utf-8')

total_time = []



for i in range(1, num_executions + 1):
    #print(f"Running raw-invoke.sh - Execution {i}")
    
    r = requests.post(url,data=encoded_data,headers=header)
    print(r.elapsed.total_seconds())
    total_time.append(r.elapsed.total_seconds())
    #time.sleep(0.5)

    
    #print(f"Execution {i} time: {execution_time:.6f} seconds")

print("avg:", sum(total_time)/len(total_time))

