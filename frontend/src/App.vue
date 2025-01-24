<script lang="ts">
import { RouterView } from 'vue-router'
import { defineComponent } from "vue";
import ToggleSwitch from 'primevue/toggleswitch';

export default defineComponent({
  name: 'App',
  components: {
    RouterView,
    ToggleSwitch
  },
  mounted() {
    this.setMode(this.isDarkMode)
  }
})

</script>

<template>
  <RouterView/>
  <ToggleSwitch class="theme-switch" :modelValue="isDarkMode" @click="setMode(!isDarkMode)"/>
</template>
<style lang="scss">
.theme-switch {
  position: absolute;
  right: 0;
}
</style>
