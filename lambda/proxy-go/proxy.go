package main

import (
	"bytes"
	"io"
	"io/ioutil"
	"log"
	"net/http"
	"net/url"
	"fmt"
	"os/exec"
	"strings"
	"strconv"
)

func runScript(scriptPath string) (string, error) {
	cmd := exec.Command(scriptPath)
	var stdout bytes.Buffer
	cmd.Stdout = &stdout
	err := cmd.Run()
	if err != nil {
		return "", err
	}
	return stdout.String(), nil
}

func main() {
	// 设置代理监听地址
	proxy := &http.Server{
		Addr:    ":4567",
		Handler: http.HandlerFunc(handleRequest),
	}
	log.Fatal(proxy.ListenAndServe())
}

func handleRequest(w http.ResponseWriter, r *http.Request) {
	// 解析请求的URL
	u, err := url.Parse(r.URL.String())
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	// 修改请求的URL，将host和port改为127.0.0.1:9001
	u.Host = "127.0.0.1:9001"
	u.Scheme = "http"
	r.URL = u

	// 创建一个新的 HTTP 客户端
	client := &http.Client{}

	reqBody, err := ioutil.ReadAll(r.Body)
	// 复制请求的所有头信息
	req, err := http.NewRequest(r.Method, u.String(), bytes.NewReader(reqBody))
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}
	for name, values := range r.Header {
		for _, value := range values {
			req.Header.Add(name, value)
		}
	}

	// 发送请求到目标服务器
	resp, err := client.Do(req)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer resp.Body.Close()

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	// 判断是否需要重新转发
	if r.Method == "GET" {
		if bytes.Contains(body, []byte("ZZMReclaim")) {
			// 从响应头中获取Lambda-Runtime-Aws-Request-Id
			requestID := resp.Header.Get("Lambda-Runtime-Aws-Request-Id")
			if requestID == "" {
				http.Error(w, "Lambda-Runtime-Aws-Request-Id not found in response header", http.StatusInternalServerError)
				return
			}

			urls := []string{"http://127.0.0.1:10010", "http://127.0.0.1:10086"}

			for _, url := range urls {
				resp, err := http.Get(url)
				if err != nil {
					fmt.Printf("Error fetching %s: %v\n", url, err)
				} else {
					fmt.Printf("Response from %s: %s\n", url, resp.Status)
					resp.Body.Close()
				}
			}

			output, err := runScript("/getpmap.sh")
			if err != nil {
				fmt.Println("Error:", err)
				return
			}
			words := strings.Split(output, " ")
			pc, _ := strconv.Atoi(words[7])
			pd, _ := strconv.Atoi(words[8])

			sum := pc+pd
			fmt.Println("Output:", output)


			// 发送POST请求到invocation/response网址
			responseURL := "http://127.0.0.1:9001/2018-06-01/runtime/invocation/" + requestID + "/response"
			responseReq, err := http.NewRequest("POST", responseURL, bytes.NewReader([]byte("{\"uss\": " + strconv.Itoa(sum) + "}")))
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			responseReq.Header.Set("Content-Type", "application/json")
			responseResp, err := client.Do(responseReq)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			defer responseResp.Body.Close()
			if responseResp.StatusCode != http.StatusAccepted {
				http.Error(w, "Failed to send response to "+responseURL, http.StatusInternalServerError)
				return
			}

			req.Body = ioutil.NopCloser(bytes.NewReader([]byte{})) // 重置请求的Body
			req.Header.Del("Content-Length")                      // 删除请求头中的Content-Length
			req.Header.Set("Content-Type", "")                    // 重置请求头中的Content-Type
			resp, err = client.Do(req)

			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			body, err = ioutil.ReadAll(resp.Body)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			defer resp.Body.Close()
		}
	}

	// 复制响应的所有头信息
	for name, values := range resp.Header {
		for _, value := range values {
			w.Header().Add(name, value)
		}
	}
	w.WriteHeader(resp.StatusCode)

	// 复制响应的主体内容
	_, err = io.Copy(w, bytes.NewReader(body))
	if err != nil {
		log.Println(err)
	}
}
