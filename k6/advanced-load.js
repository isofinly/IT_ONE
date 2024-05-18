import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';
import { SharedArray } from 'k6/data';

// Define custom metrics
const errorRate = new Rate('errors');

// Define the users array
export let users = new SharedArray('users', function () {
  return JSON.parse(open('./users.json'));
});

// Options for the load test
export let options = {
  stages: [
    { duration: '30s', target: 15 }, // Ramp-up to 50 virtual users
    { duration: '1m', target: 20 }, // Stay at 50 virtual users for 1 minute
    { duration: '30s', target: 0 }, // Ramp-down to 0 virtual users
  ],
  thresholds: {
    http_req_duration: ['p(95)<4000'], // 95% of requests must complete below 500ms
    errors: ['rate<0.01'], // Error rate must be below 1%
  },
};

export default function () {
  let baseUrl = 'http://localhost:3030'; // Replace with your server URL

  // Define the GET endpoints to test
  let endpoints = [
    { path: '/', params: [] },
    { path: '/api/v1/financial_forecast', params: ['date_range'] },
    { path: '/api/v1/budget/analyze_budget', params: ['budget_id', 'date_range'] },
    { path: '/api/v1/category/analysis', params: ['category_id', 'date_range'] },
    { path: '/api/v1/accounts/read_balance', params: ['balance_id'] },
    { path: '/api/v1/accounts/calculate_total_balance', params: ['account_id'] },
    { path: '/api/v1/accounts/read_credit', params: [] },
    { path: '/api/v1/accounts/read_depository', params: ['depository_id'] },
    { path: '/api/v1/accounts/read_investment', params: ['investment_id'] },
    { path: '/api/v1/accounts/read_loan', params: ['loan_id'] },
    { path: '/api/v1/accounts/read_asset', params: ['asset_id'] },
    { path: '/api/v1/advice', params: [] },
    { path: '/api/v1/category/read', params: ['category_id'] },
    { path: '/api/v1/monthly_report', params: ['month', 'year'] },
    { path: '/api/v1/tax/read', params: ['tax_id'] },
    { path: '/api/v1/tax/calculate', params: ['tax_id'] },
    { path: '/api/v1/transaction/search', params: ['amount_range', 'category_id'] },
    { path: '/api/v1/valuation/read', params: ['valuation_id', 'target_currency'] },
  ];

  users.forEach(user => {
    const payload = JSON.stringify({ email: user.email, password: user.password });
    const headers = { 'Content-Type': 'application/json' };

    let loginRes = http.post(`${baseUrl}/api/v1/login`, payload, { headers: headers });

    check(loginRes, {
      'login successful': (res) => res.status === 200,
    });

    if (loginRes.status === 200) {
      let token = loginRes.json('token');

      // Perform GET requests for each endpoint
      endpoints.forEach(endpoint => {
        let queryParams = endpoint.params.map(param => `${param}=${(user[param] + '').replace(/\s/g, '_')}`).join('&');
        let url = `${baseUrl}${endpoint.path}${queryParams.length ? `?${queryParams}` : ''}`;

        let res = http.get(url, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        check(res, {
          [`GET ${endpoint.path} successful`]: (r) => r.status === 200,
        });

        sleep(1); // Sleep for 1 second between requests
      });
    }
  });
}
