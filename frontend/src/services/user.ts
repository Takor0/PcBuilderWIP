import { AUTH_ENDPOINTS } from '@/services/auth';
import { request } from '@/utils/request';
import { useUserStore } from '@/stores/user';

export interface UserInterface {
    id: string;
    email: string;
    firstname?: string;
    lastname?: string;
    is_superuser: boolean;
    is_verified: boolean;
}
export class User {
    userStore = useUserStore();

    get() {
        if (this.userStore.user) {
            return this.userStore.user;
        }
        return this.set();
    }

    async logout() {
        this.userStore.clear();
        await request({
            url: AUTH_ENDPOINTS.logout,
            method: 'POST',
        });
    }

    async set(currentData=undefined) {
        if (currentData) {
            this.userStore.set(currentData);
            return this.userStore.user;
        }
        // change after fix in backend
        return {}
        const {data, res} = await request({
            url: AUTH_ENDPOINTS.me,
            method: 'GET',
        });
        if (res.status === 401) {
            this.userStore.clear();
        } else if (res.status === 200) {
            this.userStore.set(data);
        }
        return this.userStore.user
    }
}
