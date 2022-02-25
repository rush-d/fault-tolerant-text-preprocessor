import requests;
url = "https://localhost:8082/api/response"
request_fallback = 0
for i in range(100):
    response = requests.get(url)
    if response.status_code == 200 :
        if response.json()["code"] == "500":
            request_fallback += 1

print(request_fallback/100)

