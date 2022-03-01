import requests, time
url = "http://localhost:8082/api/response"
request_fallback = 0
N = 100
for i in range(N):
    response = requests.get(url)
    service_statuscode = response.json()['code']
    print("Service Status Code = {}".format(service_statuscode), "Time Taken = {:.3f} ms".format(response.elapsed.total_seconds()*1000))
    if service_statuscode == 500:
        request_fallback += 1
    time.sleep(1)
print("Fallback Percentage = {:.3f} %".format(request_fallback*100/N))