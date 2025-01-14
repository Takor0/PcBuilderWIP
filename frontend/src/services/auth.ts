import { request } from 'src/utils/request';
import { BASE_URL } from 'src/config';
import { ActiveUser } from 'src/services/user';

export const AUTH_ENDPOINTS = {
    login: `${BASE_URL}auth/jwt/login`,
    logout: `${BASE_URL}auth/jwt/logout`,
    me: `${BASE_URL}user/me`,
};

class Auth {
    async login({username, password}: { username: string; password: string }) {
        const formData = new FormData();
        formData.append('username', username);
        formData.append('password', password);
        const {data, res} = await request({
            url: AUTH_ENDPOINTS.login,
            method: 'POST',
            formData: formData,
        });
        if (res.status === 204) {
            await ActiveUser.set();
        }
        return {
            data,
            res,
        };
    }
}

export default new Auth();
