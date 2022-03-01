import requests
from concurrent.futures import ThreadPoolExecutor
from collections import Counter

url = "http://localhost:8082/api/response"
N = 100

def get_response_code(url):
    response = requests.get(url)
    return response.json()['code']

with ThreadPoolExecutor(max_workers = N) as pool:
    response_codes = list(pool.map(get_response_code, [url]*N))

c = Counter(response_codes)
print(c.items())
