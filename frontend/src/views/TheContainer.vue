<script lang="ts">
import Button from 'primevue/button'
import Menu from 'primevue/menu'
import { useUserStore } from '@/stores/user'

export default {
  name: 'TheContainer',
  components: {Menu, Button},
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
    <header>
      <span class="flex flex-row align-content-center ">
        <Button @click="$router.push({name:'setup', query: {clear: true}})">
          Nowy
        </Button>
        <Button>
          Zestawy
        </Button>
        <Button @click="$router.push({name:'forum'})">
          Forum
        </Button>
        <div class="ml-auto">
          <Button @click="toggle" aria-haspopup="true" aria-controls="profile_menu" icon="pi pi-user"/>
          <Menu ref="menu" id="profile_menu" :model="profileMenuItems" :popup="true">
          </Menu>
        </div>
        </span>
    </header>
    <router-view/>
  </div>
</template>

