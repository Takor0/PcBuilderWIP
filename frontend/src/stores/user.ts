import { defineStore } from 'pinia'
import { User } from 'src/services/users';

export const useUserStore = defineStore('user', {
    state: () => ({
        user: null as User | null,
    }),
    actions: {
        set(user: User) {
            this.user = user
        },
        clear() {
            this.user = null
        }
    },
})
