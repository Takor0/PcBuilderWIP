<template>
  <div>
    <h1 class="p-4">Czy masz jakieś dodatkowe wymagania?</h1>
    <div class="flex items-center gap-2 ml-4 mt-2" v-for="req in requirements">
      <Checkbox
        :inputId="req.value"
        :name="req.value"
        :value="req.value"
        v-model="selected"
      />
      <label :for="req.value"> {{req.label}} </label>
    </div>
  </div>
</template>

<script>
import Checkbox from "primevue/checkbox";
import stepMixin from "@/mixins/stepMixin";
export default {
  mixins: [stepMixin],
  components: {Checkbox},
  data() {
    return {
      selected: [],
      requirements: [
        {
          label: "Szybki dysk ssd",
          value: "fast-ssd"
        },
        {
          label: "Duży dysk",
          value: "big-disc"
        },
        {
          label: "Dużo rdzeni",
          value: "a-lot-of-cores"
        },
      ],
      title: "requirements",
      content: null,
    }
  },
  mounted() {

  }
}
</script>