export default {
    data() {
        return {
            isDarkMode: localStorage.getItem('theme') == 'dark'
        }
    },
    methods: {
        setMode(isDarkMode: Boolean) {
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
