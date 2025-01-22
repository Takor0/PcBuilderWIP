import { defineStore } from 'pinia'
import type { UserInterface } from '@/services/user';

export const useUserStore = defineStore('user', {
    state: () => ({
        user: null as UserInterface | null,
    }),
    actions: {
        set(user: UserInterface) {
            this.user = user
        },
        clear() {
            this.user = null
        }
    },
})
