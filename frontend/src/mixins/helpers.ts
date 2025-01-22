import {User} from '@/services/user';
export default {
    data(): { user: boolean, isDarkMode: boolean } {
        return {
            user: null,
            isDarkMode: localStorage.getItem('theme') === 'dark'
        }
    },
    async mounted() {
        const user = new User()
        this.user = await user.get();
    },
    methods: {
        setMode(isDarkMode: boolean) {
            if (isDarkMode) {
                document.documentElement.classList.add('my-app-dark');
                localStorage.setItem('theme', 'dark');
            } else {
                document.documentElement.classList.remove('my-app-dark');
                localStorage.setItem('theme', 'light');
            }
            this.isDarkMode = isDarkMode;
        }
    }
}