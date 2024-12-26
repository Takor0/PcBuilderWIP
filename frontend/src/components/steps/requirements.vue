<template>
  <div>
    <h1>Req</h1>
  </div>
</template>

<script>
import stepMixin from "@/components/steps/stepMixin";
export default {
  mixins: [stepMixin],
  data() {
    return {
      title: "requirements",
      content: null,
    }
  },
  mounted() {

  }
}
</script>