import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SetupView from '../views/SetupView.vue'
import LoginView from '../views/LoginView.vue'
import TheContainer from '../views/TheContainer.vue'
import TopicsList from '../views/TopicsList.vue';
import TopicDetails from '../views/TopicDetails.vue';

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
            redirect: '/login',
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
                },
                { name: 'forum',path: '/forum', component: TopicsList },
                { name: 'thread', path: '/forum/topic/:id', component: TopicDetails, props: true },
            ]
        },
    ]
})

export default router
