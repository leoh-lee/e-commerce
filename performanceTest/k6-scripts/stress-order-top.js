import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    stages: [
        {
            target: 2000,
            duration: '30s'
        },
        {
            target: 3000,
            duration: '10s'
        },
        {
            target: 2000,
            duration: '2m'
        },
    ]
};

const BASE_URL = "http://host.docker.internal:8080";

export default function() {
    const params = {
        headers: {
            'Content-Type': 'application/json'
        }
    }

    let res = http.get(`${BASE_URL}/api/v1/orders/top`, params);
    check(res, { "status is 200": (res) => res.status === 200 });
    sleep(1);
}
