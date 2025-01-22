<script lang="ts">

import Password from "primevue/password";
import InputText from "primevue/inputtext";
import Button from "primevue/button"
import { Form } from '@primevue/forms';
import Message from 'primevue/message';

export default {
  name: 'LoginView',
  // eslint-disable-next-line vue/no-reserved-component-names
  components: {Message, Form, Button, Password, InputText},
  data() {
    return {
      isLogin: true,
      initialValues: {
        password: "",
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
    resolver({ values }) {
      console.log(values)
      const errors = { username: [] };

      if (!values.username) {
        errors.username.push({ type: 'required', message: 'Username is required.' });
      }

      if (values.username?.length < 3) {
        errors.username.push({ type: 'minimum', message: 'Username must be at least 3 characters long.' });
      }

      return {
        errors
      };
    },
    onFormSubmit({valid}) {
      console.log(valid)
    }
  }

}

</script>


<template>
  <div class="h-25rem flex">
    <Form :resolver="resolver" :initialValues @submit="onFormSubmit" class="mt-auto align-items-center gap-3 flex flex-column" style="width: 100%">
      <InputText required name="username" placeholder="Login"/>
      <Password required :feedback="false" name="password" placeholder="Hasło"/>
      <Button style="max-width: 219px; width: 100%" type="submit" severity="secondary" :label="submitLabel"/>
      <Button
        style="max-width: 260px; width: 100%"
        @click="isLogin = !isLogin"
        :label="switchModeLabel"
        variant="text"
      />
    </Form>
  </div>
</template>

