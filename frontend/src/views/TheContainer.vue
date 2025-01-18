<script lang="ts">
import Button from 'primevue/button'

export default {
  name: 'TheContainer',
  components: {Button},
  data() {
    return {}
  },
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
        <Button>
          Forum
        </Button>
        <Button class="ml-auto" icon="pi pi-user" />
      </span>
    </header>
    <router-view/>
  </div>
</template>

