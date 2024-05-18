import http from 'k6/http';
import { check, sleep } from 'k6';


const BASE_URL = 'http://localhost:3030';


export default function () {
    // Test the root endpoint
    testRootEndpoint();

    // Test the registration endpoint
    testRegistrationEndpoint();

    const token = testLoginEndpoint();

    testAutoCategorizeEndpoint(token);

}

function testRootEndpoint() {
    const res = http.get(BASE_URL);
    check(res, {
        'Root endpoint status is 200': (r) => r.status === 200,
        'Root endpoint body is correct': (r) => r.body === 'Hello World!',
    });
}

function testRegistrationEndpoint() {
    const payload = JSON.stringify({ email: 'test@example.com', password: 'password123' });
    const headers = { 'Content-Type': 'application/json' };
    const res = http.post(`${BASE_URL}/api/v1/register`, payload, { headers: headers });
    check(res, {
        'Registration endpoint status is 200': (r) => r.status === 200,
    });
}

function testLoginEndpoint() {
    const payload = JSON.stringify({ email: 'test@example.com', password: 'password123' });
    const headers = { 'Content-Type': 'application/json' };
    const res = http.post(`${BASE_URL}/api/v1/login`, payload, { headers: headers });
    check(res, {
        'Login endpoint status is 200': (r) => r.status === 200,
        'Endpoint body is correct': (r) => JSON.parse(r.body)['token'] !== undefined,
    });
    return JSON.parse(res.body)['token'];
}

function testAutoCategorizeEndpoint(token) {
    const headers = { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` };
    const res = http.post(`${BASE_URL}/api/v1/transactions/auto-categorize`, {}, { headers: headers });
    check(res, {
        'Auto-categorize endpoint status is 200': (r) => r.status === 200,
    });
}

function testAccountTransferEndpoint(token) {
    
}
