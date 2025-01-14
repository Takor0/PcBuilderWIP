import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SetupView from '../views/SetupView.vue'
import LoginView from '../views/LoginView.vue'
import TheContainer from '../views/TheContainer.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: LoginView
        },
        {
            path: '',
            component: TheContainer,
            children: [
                {
                    path: '/',
                    name: 'home',
                    component: HomeView
                },
                {
                    path: '/setup',
                    name: 'setup',
                    component: SetupView
                }
            ]
        },
    ]
})

export default router
