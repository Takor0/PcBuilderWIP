import Aura from '@primevue/themes/aura';
import PrimeVue from 'primevue/config';
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import helpers from "@/mixins/helpers";
import { createPinia } from 'pinia'

const app = createApp(App)
const pinia = createPinia()

app.mixin(helpers)
app.use(router)
app.use(pinia)
app.use(PrimeVue, {
    theme: {
        preset: Aura,
        options: {
            darkModeSelector: '.my-app-dark',
        }
    }
});

app.mount('#app')
