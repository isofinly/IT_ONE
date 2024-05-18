import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Define custom metrics
const errorRate = new Rate('errors');

// Options for the load test
export let options = {
    stages: [
        { duration: '30s', target: 50 },  // Ramp-up to 50 virtual users
        { duration: '1m', target: 50 },   // Stay at 50 virtual users for 1 minute
        { duration: '30s', target: 0 },   // Ramp-down to 0 virtual users
    ],
    thresholds: {
        'http_req_duration': ['p(95)<500'], // 95% of requests must complete below 500ms
        'errors': ['rate<0.01'], // Error rate must be below 1%
    },
};

export default function () {
    let baseUrl = 'http://localhost:3030'; // Replace with your server URL

    // Define the GET endpoints to test
    let endpoints = [
        '/',
        '/api/v1/accounts/read_balance',
        '/api/v1/accounts/calculate_total_balance',
        '/api/v1/accounts/read_credit',
        '/api/v1/accounts/calculate_credit_interest',
        '/api/v1/accounts/read_depository',
        '/api/v1/accounts/calculate_depository_interest',
        '/api/v1/accounts/read_investment',
        '/api/v1/accounts/calculate_dividends',
        '/api/v1/accounts/read_loan',
        '/api/v1/accounts/calculate_loan_interest',
        '/api/v1/accounts/check_due_date_notifications',
        '/api/v1/accounts/read_asset',
        '/api/v1/advice',
        '/api/v1/financial_forecast',
        '/api/v1/budget/analyze_budget',
        '/api/v1/category/analysis',
        '/api/v1/category/read',
        '/api/v1/exchange_rate/read',
        '/api/v1/monthly_report',
        '/api/v1/tax/read',
        '/api/v1/tax/calculate',
        '/api/v1/transaction/recurring/read',
        '/api/v1/transaction/search',
        '/api/v1/valuation/read',
        '/api/v1/visualizations',
    ];

    // Iterate over each endpoint and send a GET request
    endpoints.forEach(endpoint => {
        let res = http.get(`${baseUrl}${endpoint}`);
        
        // Check if the response status is 200
        let checkRes = check(res, {
            'status is 200': (r) => r.status === 200,
        });

        // Record error if check fails
        if (!checkRes) {
            errorRate.add(1);
        } else {
            errorRate.add(0);
        }
        
        // Sleep for a short duration between requests
        sleep(1);
    });
}
