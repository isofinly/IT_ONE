k6 run --vus 100 --duration 30s basic-load.js

          /\      |‾‾| /‾‾/   /‾‾/
     /\  /  \     |  |/  /   /  /
    /  \/    \    |     (   /   ‾‾\
   /          \   |  |\  \ |  (‾)  |
  / __________ \  |__| \__\ \_____/ .io

     execution: local
        script: basic-load.js
        output: -

     scenarios: (100.00%) 1 scenario, 100 max VUs, 1m0s max duration (incl. graceful stop):
              * default: 100 looping VUs for 30s (gracefulStop: 30s)


     ✓ Login endpoint status is 200
     ✓ Endpoint body is correct

     checks.........................: 100.00% ✓ 4920      ✗ 0
     data_received..................: 752 kB  24 kB/s
     data_sent......................: 474 kB  15 kB/s
   ✓ errors.........................: 0.00%   ✓ 0         ✗ 2460
     http_req_blocked...............: avg=567.77µs min=616ns    med=1.95µs   max=31.3ms   p(90)=3.17µs   p(95)=6.69µs
     http_req_connecting............: avg=289.31µs min=0s       med=0s       max=29.69ms  p(90)=0s       p(95)=0s
   ✓ http_req_duration..............: avg=242.44ms min=203.59ms med=221.1ms  max=1s       p(90)=250.48ms p(95)=312.34ms
       { expected_response:true }...: avg=242.44ms min=203.59ms med=221.1ms  max=1s       p(90)=250.48ms p(95)=312.34ms
     http_req_failed................: 0.00%   ✓ 0         ✗ 2460
     http_req_receiving.............: avg=20.09µs  min=5.61µs   med=18.48µs  max=345.72µs p(90)=27.98µs  p(95)=33.72µs
     http_req_sending...............: avg=65.99µs  min=2.94µs   med=8.2µs    max=29.87ms  p(90)=14.58µs  p(95)=22.1µs
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s       max=0s       p(90)=0s       p(95)=0s
     http_req_waiting...............: avg=242.36ms min=203.56ms med=221.07ms max=976.91ms p(90)=250.45ms p(95)=312.32ms
     http_reqs......................: 2460    78.851626/s
     iteration_duration.............: avg=1.24s    min=1.2s     med=1.22s    max=2s       p(90)=1.25s    p(95)=1.31s
     iterations.....................: 2460    78.851626/s
     vus............................: 16      min=16      max=100
     vus_max........................: 100     min=100     max=100


running (0m31.2s), 000/100 VUs, 2460 complete and 0 interrupted iterations
default ✓ [======================================] 100 VUs  30s