import http from 'k6/http';
import { sleep, check } from 'k6';

// peak test
export const options = {
    stages: [
        {
            target: 2000,
            iterations: 1,
            duration: '1s'
        },
        {
            target: 10000,
            iterations: 1,
            duration: '10s'
        }
    ]
};

const BASE_URL = "http://host.docker.internal:8080";

export default function() {
    let userId = ( (__VU - 1) * 100 ) + __ITER + 30000;

    const payload = JSON.stringify({
        userId: userId,
        couponId: 1
    });

    const params = {
        headers: {
            'Content-Type': 'application/json'
        }
    }

    let res = http.post(`${BASE_URL}/api/v1/coupons`, payload, params);
    check(res, { "status is 200": (res) => res.status === 200 });
    sleep(1);
}
