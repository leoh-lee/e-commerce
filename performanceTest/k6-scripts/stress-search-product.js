import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    stages: [
        {
            target: 1000,
            duration: '30s'
        },
        {
            target: 2000,
            duration: '10s'
        },
        {
            target: 1000,
            duration: '2s'
        },
        {
            target: 1000,
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

    let res = http.get(`${BASE_URL}/api/v1/products`, params);
    check(res, { "status is 200": (res) => res.status === 200 });
    sleep(1);
}
