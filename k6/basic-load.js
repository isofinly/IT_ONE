import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Define custom metrics
const errorRate = new Rate('errors');

// Options for the load test
export let options = {
    stages: [
        { duration: '1m', target: 50 },  // Ramp-up to 50 virtual users
        { duration: '3m', target: 50 },   // Stay at 50 virtual users for 1 minute
        { duration: '1m', target: 0 },   // Ramp-down to 0 virtual users
    ],
    thresholds: {
        'http_req_duration': ['p(95)<500'], // 95% of requests must complete below 500ms
        'errors': ['rate<0.01'], // Error rate must be below 1%
    },
};

export let users = [
    { user_id: '338574bd-e7b6-436c-aa05-74c42b45ddb3', email: 'test@example.com', password: '1234', family_id: 'b9bf3cf5-10f0-446c-ae21-f368b5216431' },
    { user_id: '2fdcc298-2334-4195-b6f1-2b97840e889a', email: '7JFtvz@example.com', password: '1234', family_id: 'aab966cc-c11b-400b-8fe5-9700af890908' },
    { user_id: '77339790-98cf-4fbc-b3dd-ff7f21772afc', email: 'eEcejo@example.com', password: '1234', family_id: 'aab966cc-c11b-400b-8fe5-9700af890908' },
    { user_id: '799f37bf-65f6-46a5-9e3e-6d18e12cbe1c', email: 'GOr4YW@example.com', password: '1234', family_id: '39aeaf02-1cfe-408d-a781-6425f82d49c9' },
    { user_id: '9f1dcf73-5814-4da7-a07d-c0cba360cbf1', email: '7Z566y@example.com', password: '1234', family_id: '39aeaf02-1cfe-408d-a781-6425f82d49c9' },
    { user_id: '7d86e4d4-ded2-4e12-bcc4-d7ae1b5aa33b', email: 'CRCmCn@example.com', password: '1234', family_id: '39aeaf02-1cfe-408d-a781-6425f82d49c9' },
    { user_id: 'a88210e0-b0d0-411d-92ad-f60f1404a714', email: 'LB1Nad@example.com', password: '1234', family_id: 'a6170d2b-5eb9-4ffc-8b69-6571586d4c7b' },
    { user_id: '0a9ebaa1-8f21-40f1-9070-bd5786ae6f83', email: 'ZDXedK@example.com', password: '1234', family_id: '2bbb5c74-2dbd-4e11-bec7-d325cdf6feb9' },
    { user_id: '582e153e-33ca-4ef0-9733-59949e9ada2a', email: 'P4O6ex@example.com', password: '1234', family_id: '2bbb5c74-2dbd-4e11-bec7-d325cdf6feb9' },
    { user_id: 'f3d2e076-8b22-481d-a1df-59eed483e57a', email: 'BeDVA1@example.com', password: '1234', family_id: '2bbb5c74-2dbd-4e11-bec7-d325cdf6feb9' },
    { user_id: '663c62e6-25c6-4154-8997-d6884fc9a21a', email: 'Dxs46M@example.com', password: '1234', family_id: '44974565-730b-4e25-8253-852e604833df' },
    { user_id: '0b0711fd-d1fb-42f7-acc7-464f3e1445a9', email: 'YObq0P@example.com', password: '1234', family_id: '44974565-730b-4e25-8253-852e604833df' },
    { user_id: '418aeeac-5620-4b74-9337-f408f070ac2c', email: '6oFyGW@example.com', password: '1234', family_id: '44974565-730b-4e25-8253-852e604833df' },
];

export let BASE_URL = 'http://localhost:3030';

export default function () {
    let user = users[Math.floor(Math.random() * users.length)];
    let payload = JSON.stringify({ email: user.email, password: user.password });

    const headers = { 'Content-Type': 'application/json' };
    const res = http.post(`${BASE_URL}/api/v1/login`, payload, { headers: headers });
    
    const success = check(res, {
        'Login endpoint status is 200': (r) => r.status === 200,
        'Endpoint body is correct': (r) => JSON.parse(r.body)['token'] !== undefined,
    });
    
    if (!success) {
        errorRate.add(1);
    } else {
        errorRate.add(0);
    }

    sleep(1);
}
