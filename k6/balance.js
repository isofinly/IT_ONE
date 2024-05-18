import http from 'k6/http';
import { check, group, sleep } from 'k6';

const BASE_URL = 'http://localhost:3030';

function testCreateBalanceEndpoint(token) {
    const headers = { "Authorization": `Bearer ${token}`, 'Content-Type': 'application/json' };
    // todo: account_id
    const body = { 'date': '2020-01-01', 'balance': 100000, 'currency': 'RUB', 'account_id': '8d6a7f3c-9554-46ab-8db2-212bdae1ae2a' };
    const response = http.post(`${BASE_URL}/api/v1/accounts/create_balance`, body, headers)
    check(response, { 'Balance creation status is 200': (r) => r.status === 200 });
}

function testUpdateBalanceEndpoint(token) {
    const headers = { "Authorization": `Bearer ${token}` };
    const body = { 'date': '2020-01-01', 'balance': 100001, 'currency': 'RUB', 'account_id': '8d6a7f3c-9554-46ab-8db2-212bdae1ae2a', 'balance_id': '?????' };
    const response = http.put(`${BASE_URL}/api/v1/accounts/update_balance`, body, headers);
    check(response, { 'Balance update status is 200': (r) => r.status === 200 });
}

function testReadBalanceEndpoint(token) {
    const headers = { "Authorization": `Bearer ${token}` };
    // todo: balance id
    const body = { 'balance_id': '?????' }
    const response = http.get(`${BASE_URL}/api/v1/accounts/read_balance`, headers, body)
    check(response, {
        'Balance read status is 200': (r) => r.status === 200,
    });
}

function testDeleteBalanceEndpoint(token) {
    const headers = { "Authorization": `Bearer ${token}` };
    // todo: balance id
    const body = { 'balance_id': '?????' }
    const response = http.del(`${BASE_URL}/api/v1/accounts/delete_balance`, headers, body)
    check(response, {
        'Balance deletion status is 200': (r) => r.status === 200
    });
}

function testCalculateBalanceEndpoint(token) {
    const headers = { "Authorization": `Bearer ${token}` };
    // todo: account_id
    const body = { 'account_id': '?????' }
    const response = http.get(`${BASE_URL}/api/v1/accounts/calculate_balance`, headers, body)
    check(response, {
        'Balance calculation status is 200': (r) => r.status === 200,
        'Balance calculation is correct': (r) => r.json().balance === 100000
    });
}

export { testCalculateBalanceEndpoint, testCreateBalanceEndpoint, testUpdateBalanceEndpoint, testReadBalanceEndpoint, testDeleteBalanceEndpoint };
