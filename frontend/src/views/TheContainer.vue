<script lang="ts">
import Button from 'primevue/button'
import Menu from 'primevue/menu'
import { useUserStore } from '@/stores/user'
import HomeButton from '@/components/navigation/HomeButton.vue'

export default {
  name: 'TheContainer',
  components: {
    HomeButton,
    Menu,
    Button
  },
  methods: {
    toggle(event) {
      this.$refs.menu.toggle(event);
    }
  },
  computed: {
    isUserLoggedIn() {
      const userStore = useUserStore();
      return userStore.user !== null;
    },
    profileMenuItems() {
      if (this.isUserLoggedIn) {
        return [
          {
            label: 'Wyloguj',
            icon: 'pi pi-power-off',
            command: () => {
              this.$router.push('/logout');
            }
          }
        ];
      }
      return [
        {
          label: 'Zaloguj',
          icon: 'pi pi-sign-in',
          command: () => {
            this.$router.push('/login');
          }
        }
      ];
    }
  }
}

</script>


<template>
  <div>
    <HomeButton />
    <header class="p-3">
      <span class="flex flex-row align-items-center gap-2 ml-5">
        <Button @click="$router.push({name:'setup', query: {clear: true}})" :disabled="!isUserLoggedIn">
          Nowy
        </Button>
        <Button @click="$router.push({name:'forum'})" :disabled="!isUserLoggedIn">
          Forum
        </Button>
        <div class="ml-auto">
          <Button @click="toggle" aria-haspopup="true" aria-controls="profile_menu" icon="pi pi-user"/>
          <Menu ref="menu" id="profile_menu" :model="profileMenuItems" :popup="true"/>
        </div>
      </span>
    </header>
    <main class="p-3">
      <router-view/>
    </main>
  </div>
</template>

