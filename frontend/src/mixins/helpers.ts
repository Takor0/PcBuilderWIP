export default {
    data(): { isDarkMode: boolean } {
        return {
            isDarkMode: localStorage.getItem('theme') === 'dark'
        }
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