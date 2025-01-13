<script lang="ts">

import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import Password from "primevue/password";
import InputText from "primevue/inputtext";
import Button from "primevue/button"

export default {
  name: 'LoginView',
  components: {Button,Password, InputGroup, InputGroupAddon, InputText},
  data() {
    return {
      password: "",
      login: "",
    }
  },

}

</script>


<template>
  <div class="h-21rem flex">
    <form class="mt-auto align-items-center gap-3 flex flex-column" style="width: 100%">
      <InputText v-model="login" placeholder="Login"/>
      <Password v-model="password" placeholder="Hasło"/>
      <Button style="max-width: 219px; width: 100%" type="submit" severity="secondary" label="Submit"/>
    </form>
  </div>
</template>

