import {request} from '@/utils/request';
import {BASE_URL} from '@/constants/common';
import {User} from '@/services/user';

export const AUTH_ENDPOINTS = {
    login: `${BASE_URL}api/auth/login`,
    register: `${BASE_URL}api/users/register`,
    logout: `${BASE_URL}auth/jwt/logout`,
    me: `${BASE_URL}user/me`,
};

class Auth {
    async login(username: string, password: string) {
        const {data, res} = await request({
            url: AUTH_ENDPOINTS.login,
            method: 'POST',
            body: {
                usernameOrEmail: username,
                password: password
            },
        });
        if (res.status === 200) {
            const user = new User();
            await user.set(data);
        }
        return {
            data,
            res,
        };
    }

    async register(
        username: string,
        password: string,
        email: string
    ) {
        const {data, res} = await request({
            url: AUTH_ENDPOINTS.register,
            method: 'POST',
            body: {
                username: username,
                password: password,
                email: email,
                isVerified: true,
            },
        });
        if (res.status === 204) {
            console.log(res)
            const user = new User();
            // await user.set();
        }
        return {
            data,
            res,
        };
    }
}

export default new Auth();
