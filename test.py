import requests, time
url = "http://localhost:8082/api/response"
request_fallback = 0
N = 20
for i in range(N):
    response = requests.get(url)
    service_statuscode = response.json()['code']
    print("Service Status Code = {}".format(service_statuscode), "Time Taken = {} ms".format(response.elapsed.total_seconds()*1000))
    if service_statuscode == 500:
        request_fallback += 1
    time.sleep(4)
print("Fallback Percentage = {} %".format(request_fallback*100/N))
