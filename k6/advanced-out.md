```
k6 run advanced-load.js

          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

     execution: local
        script: advanced-load.js
        output: -

     scenarios: (100.00%) 1 scenario, 50 max VUs, 2m30s max duration (incl. graceful stop):
              * default: Up to 50 looping VUs for 2m0s over 3 stages (gracefulRampDown: 30s, gracefulStop: 30s)


     ✓ login successful
     ✓ GET / successful
     ✓ GET /api/v1/financial_forecast successful
     ✓ GET /api/v1/budget/analyze_budget successful
     ✓ GET /api/v1/category/analysis successful
     ✓ GET /api/v1/accounts/read_balance successful
     ✓ GET /api/v1/accounts/calculate_total_balance successful
     ✓ GET /api/v1/accounts/read_credit successful
     ✓ GET /api/v1/accounts/read_depository successful
     ✓ GET /api/v1/accounts/read_investment successful
     ✓ GET /api/v1/accounts/read_loan successful
     ✓ GET /api/v1/accounts/read_asset successful
     ✓ GET /api/v1/advice successful
     ✓ GET /api/v1/category/read successful
     ✓ GET /api/v1/monthly_report successful
     ✓ GET /api/v1/tax/read successful
     ✓ GET /api/v1/tax/calculate successful
     ✓ GET /api/v1/transaction/search successful
     ✓ GET /api/v1/valuation/read successful

     checks.........................: 100.00% ✓ 3683      ✗ 0   
     data_received..................: 1.9 MB  13 kB/s
     data_sent......................: 1.3 MB  8.6 kB/s
   ✓ errors.........................: 0.00%   ✓ 0         ✗ 0   
     http_req_blocked...............: avg=3.19µs  min=928ns    med=1.92µs  max=193.38µs p(90)=2.43µs   p(95)=3.15µs  
     http_req_connecting............: avg=792ns   min=0s       med=0s      max=73.95µs  p(90)=0s       p(95)=0s      
   ✓ http_req_duration..............: avg=87.42ms min=135.51µs med=42.27ms max=496.57ms p(90)=241.77ms p(95)=246.12ms
       { expected_response:true }...: avg=87.42ms min=135.51µs med=42.27ms max=496.57ms p(90)=241.77ms p(95)=246.12ms
     http_req_failed................: 0.00%   ✓ 0         ✗ 3683
     http_req_receiving.............: avg=25.94µs min=9.38µs   med=26µs    max=192.93µs p(90)=32.91µs  p(95)=35.3µs  
     http_req_sending...............: avg=10.09µs min=4.61µs   med=9.62µs  max=196.93µs p(90)=12.81µs  p(95)=14.69µs 
     http_req_tls_handshaking.......: avg=0s      min=0s       med=0s      max=0s       p(90)=0s       p(95)=0s      
     http_req_waiting...............: avg=87.39ms min=112.91µs med=42.22ms max=496.54ms p(90)=241.74ms p(95)=246.09ms
     http_reqs......................: 3683    24.553235/s
     vus............................: 0       min=0      max=50
     vus_max........................: 50      min=50      max=50


running (2m30.0s), 00/50 VUs, 0 complete and 50 interrupted iterations
default ✓ [======================================] 00/50 VUs  2m0s
```
