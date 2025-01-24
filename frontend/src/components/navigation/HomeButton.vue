<template>
  <Button
    :style="{ position: 'fixed', top: '20px', left: '8px', zIndex: 5 }"
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