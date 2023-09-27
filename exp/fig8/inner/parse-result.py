import sys
con_cnt = int(sys.argv[1])
test_type = sys.argv[2]

root_dir = "../result/%s/result-%d" %(test_type, con_cnt)

rss_arr = []
pss_arr = []
uss_arr = []
for i in range(con_cnt):
    cared_line_words = open("%s/%d.txt" %(root_dir, i)).readlines()[-1][:-1].split()
    rss = int(cared_line_words[3])
    pss = int(cared_line_words[4])
    uss = int(cared_line_words[7]) + int(cared_line_words[8])
    rss_arr.append(rss)
    pss_arr.append(pss)
    uss_arr.append(uss)

print("%d,%f,%f,%f" %(con_cnt, sum(rss_arr)/len(rss_arr), sum(pss_arr)/len(pss_arr), sum(uss_arr)/len(uss_arr)))