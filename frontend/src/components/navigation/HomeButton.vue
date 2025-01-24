<template>
  <Button
    class="fixed top-4 left-2 z-5"
    icon="pi pi-home"
    rounded
    text
    severity="secondary"
    @click="$router.push({name: 'home'})"
    aria-label="Strona główna"
  />
</template>

<script lang="ts">
import Button from "primevue/button"

export default {
  name: 'HomeButton',
  components: { Button }
}
</script> 