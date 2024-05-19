import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { testCalculateBalanceEndpoint, testCreateBalanceEndpoint, testUpdateBalanceEndpoint, testReadBalanceEndpoint, testDeleteBalanceEndpoint } from './balance';

const BASE_URL = 'http://localhost:3030';


export default function () {
    // Test the root endpoint
    testRootEndpoint();

    var token;
    group('Auth', () => {
        testRegistrationEndpoint();
        token = testLoginEndpoint();
    });

    group('Account routes', () => {
        testAccountBalanceEndpoints(token);

        testAccountCreditEndpoints(token);

        testAccountInvestmentEndpoints(token);

        testAccountLoadEndpoints(token);

        testAccountAssetEndpoints(token);
    });
}

function testRootEndpoint() {
    const res = http.get(BASE_URL);
    check(res, {
        'Root endpoint status is 200': (r) => r.status === 200,
        'Root endpoint body is correct': (r) => r.body === 'Hello World!',
    });
}

function testRegistrationEndpoint() {
    const payload = JSON.stringify({ email: 'test11@example.com', password: '1234' });
    const headers = { 'Content-Type': 'application/json' };
    const res = http.post(`${BASE_URL}/api/v1/register`, payload, { headers: headers });
    check(res, {
        'Registration endpoint status is 200': (r) => r.status === 200,
    });
}

function testLoginEndpoint() {
    const payload = JSON.stringify({ email: 'jWNWCU@example.com', password: '1234' });
    const headers = { 'Content-Type': 'application/json' };
    const res = http.post(`${BASE_URL}/api/v1/login`, payload, { headers: headers });
    check(res, {
        'Login endpoint status is 200': (r) => r.status === 200,
        'Endpoint body is correct': (r) => JSON.parse(r.body)['token'] !== undefined,
    });
    return JSON.parse(res.body)['token'];
}

function testAccountBalanceEndpoints(token) {
    group('Account balance endpoints', () => {
        testCreateBalanceEndpoint(token);
        testUpdateBalanceEndpoint(token);
        testReadBalanceEndpoint(token);
        testDeleteBalanceEndpoint(token);
        testCalculateBalanceEndpoint(token);
    });
}

function testAccountCreditEndpoints(token) {
    group('Account credit endpoints', () => {
        testAccountCreditEndpoint(token);
        testAccountCreditHistoryEndpoint(token);
    });
}

function testAccountInvestmentEndpoints(token) {
    group('Account investment endpoints', () => {
        testAccountInvestmentEndpoint(token);
        testAccountInvestmentHistoryEndpoint(token);
    });
}

function testAccountLoadEndpoints(token) {
    group('Account load endpoints', () => {
        testAccountLoadEndpoint(token);
        testAccountLoadHistoryEndpoint(token);
    });
}

function testAccountAssetEndpoints(token) {
    group('Account asset endpoints', () => {
        testAccountAssetEndpoint(token);
        testAccountAssetHistoryEndpoint(token);
    });
}