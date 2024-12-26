<script lang="ts">
export default {
  props: {
    title: {
      type: String,
      required: true
    }
  }
}
</script>

<template>
 <div>
   {{title}}
 </div>
</template>

<style scoped lang="scss">

</style>