import os
con_counts = [1,2,4,8,16]

print("Vanilla")
print("Concurrent Cnt,RSS,PSS,USS")
for con_count in con_counts:
    os.system("python3 parse-result.py %d vanilla" %(con_count))

print("Desiccant")
print("Concurrent Cnt,RSS,PSS,USS")
for con_count in con_counts:
    os.system("python3 parse-result.py %d desiccant" %(con_count))
