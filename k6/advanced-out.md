k6 run --vus 1 --duration 30s load-tests.js

          /\      |‾‾| /‾‾/   /‾‾/
     /\  /  \     |  |/  /   /  /
    /  \/    \    |     (   /   ‾‾\
   /          \   |  |\  \ |  (‾)  |
  / __________ \  |__| \__\ \_____/ .io

     execution: local
        script: load-tests.js
        output: -

     scenarios: (100.00%) 1 scenario, 1 max VUs, 1m0s max duration (incl. graceful stop):
              * default: 1 looping VUs for 30s (gracefulStop: 30s)

WARN[0060] No script iterations fully finished, consider making the test duration longer

     ✓ login successful
     ✓ GET / successful
     ✓ GET /api/v1/accounts/read_balance successful
     ✓ GET /api/v1/accounts/calculate_total_balance successful
     ✓ GET /api/v1/accounts/read_credit successful
     ✓ GET /api/v1/accounts/read_depository successful
     ✓ GET /api/v1/accounts/read_investment successful
     ✓ GET /api/v1/accounts/read_loan successful
     ✓ GET /api/v1/accounts/read_asset successful
     ✗ GET /api/v1/advice successful
      ↳  0% — ✓ 0 / ✗ 2
     ✗ GET /api/v1/financial_forecast successful
      ↳  0% — ✓ 0 / ✗ 2
     ✗ GET /api/v1/budget/analyze_budget successful
      ↳  0% — ✓ 0 / ✗ 2
     ✗ GET /api/v1/category/analysis successful
      ↳  0% — ✓ 0 / ✗ 2
     ✓ GET /api/v1/category/read successful
     ✓ GET /api/v1/monthly_report successful
     ✓ GET /api/v1/tax/read successful
     ✓ GET /api/v1/tax/calculate successful
     ✗ GET /api/v1/transaction/search successful
      ↳  0% — ✓ 0 / ✗ 2
     ✗ GET /api/v1/valuation/read successful
      ↳  50% — ✓ 1 / ✗ 1

     checks.........................: 73.80% ✓ 31       ✗ 11
     data_received..................: 12 kB  204 B/s
     data_sent......................: 15 kB  247 B/s
   ✓ errors.........................: 0.00%  ✓ 0        ✗ 0
     http_req_blocked...............: avg=63.35µs min=1.53µs   med=8.01µs  max=483.79µs p(90)=350.64µs p(95)=394.27µs
     http_req_connecting............: avg=35.25µs min=0s       med=0s      max=323.96µs p(90)=213.83µs p(95)=247.32µs
   ✓ http_req_duration..............: avg=77.08ms min=371.79µs med=68.07ms max=249ms    p(90)=195.39ms p(95)=241.91ms
       { expected_response:true }...: avg=94.88ms min=409.8µs  med=69.78ms max=249ms    p(90)=208.38ms p(95)=245.81ms
     http_req_failed................: 26.19% ✓ 11       ✗ 31
     http_req_receiving.............: avg=88.46µs min=16.55µs  med=88.3µs  max=187.32µs p(90)=138.3µs  p(95)=150.55µs
     http_req_sending...............: avg=42.21µs min=7.48µs   med=35.69µs max=132.09µs p(90)=94.12µs  p(95)=117.2µs
     http_req_tls_handshaking.......: avg=0s      min=0s       med=0s      max=0s       p(90)=0s       p(95)=0s  
     http_req_waiting...............: avg=76.95ms min=287.38µs med=67.92ms max=248.93ms p(90)=195.27ms p(95)=241.83ms
     http_reqs......................: 42     0.699993/s
     vus............................: 1      min=1      max=1
     vus_max........................: 1      min=1      max=1


running (1m00.0s), 0/1 VUs, 0 complete and 1 interrupted iterations
default ✓ [======================================] 1 VUs  30s