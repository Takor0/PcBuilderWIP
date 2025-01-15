<script lang="ts">
import Button from 'primevue/button'
import Menu from 'primevue/menu'

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
      return true
    },
    profileMenuItems() {
      const loggedItems = [
        {
          label: 'Wyloguj',
          icon: 'pi pi-power-off',
          command: () => {
            this.$router.push('/logout');
          }
        }
      ];
      const notLoggedItems = [
        {
          label: 'Zaloguj',
          icon: 'pi pi-sign-in',
          command: () => {
            this.$router.push('/login');
          }
        },
      ];
      return this.isUserLoggedIn ? loggedItems : notLoggedItems;
    }
  }
}

</script>


<template>
  <div>
    <header>
      <span class="flex flex-row align-content-center ">
        <Button variant="link">
          Nowy
        </Button>
        <Button>
          Zestawy
        </Button>
        <Button>
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

