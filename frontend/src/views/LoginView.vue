<script lang="ts">

import Password from "primevue/password";
import InputText from "primevue/inputtext";
import Button from "primevue/button"
import { Form } from '@primevue/forms';
import Message from 'primevue/message';
import AuthService from '@/services/auth';

export default {
  name: 'LoginView',
  // eslint-disable-next-line vue/no-reserved-component-names
  components: {Message, Form, Button, Password, InputText},
  data() {
    return {
      isLogin: true,
      password: "",
      email: "",
      username: "",
      initialValues: {
        password: "",
        email: "",
        username: "",
      },
    }
  },
  computed: {
    switchModeLabel() {
      return this.isLogin ? 'Nie posiadasz konta? Zarejestruj się' : 'Posiadasz konto? Zaloguj się';
    },
    submitLabel() {
      return this.isLogin ? 'Zaloguj' : 'Zarejestruj';
    }
  },
  methods: {
    switchMode() {
      this.password = ""
      this.email = ""
      this.username = ""
      this.isLogin = !this.isLogin
    },
    async handleLogin() {
      const {data, res} = await AuthService.login(this.username, this.password)
      if (res.status == 200) {
        this.$router.push({name: 'setup'})
      } else {
      }
    },
    async handleRegister() {
      await AuthService.register(this.username, this.password, this.email)
    },
    async onFormSubmit() {
      if (this.isLogin) {
        await this.handleLogin()
      } else {
        await this.handleRegister()
        this.switchMode()
      }
    }
  }

}

</script>


<template>
  <div class="h-25rem flex">
    <Form  @submit="onFormSubmit" class="mt-auto align-items-center gap-3 flex flex-column" style="width: 100%">
      <InputText v-model="username" required name="username" placeholder="Login"/>
      <InputText v-if="!isLogin" v-model="email" required name="E-mail" placeholder="E-mail"/>
      <Password v-model="password" required :feedback="false" name="password" placeholder="Hasło"/>
      <Button style="max-width: 219px; width: 100%" type="submit" severity="secondary" :label="submitLabel"/>
      <Button
        style="max-width: 260px; width: 100%"
        @click="switchMode"
        :label="switchModeLabel"
        variant="text"
      />
    </Form>
  </div>
</template>

