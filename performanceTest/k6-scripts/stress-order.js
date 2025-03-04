import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    stages: [
        {
            target: 400,
            duration: '10s'
        },
        {
            target: 1000,
            duration: '2s'
        },
        {
            target: 400,
            duration: '1m'
        },
        {
            target: 400,
            duration: '1m'
        },
    ]
};

const BASE_URL = "http://host.docker.internal:8080";

export default function() {
    const payload = JSON.stringify({
        userId: 1,
        products: [
            {
                "productId": 1,
                "quantity": 5
            }
        ],
        userCouponId: null
    });

    const params = {
        headers: {
            'Content-Type': 'application/json'
        }
    }

    let res = http.post(`${BASE_URL}/api/v1/orders`, payload, params);
    check(res, { "status is 200": (res) => res.status === 200 });
    sleep(1);
}
