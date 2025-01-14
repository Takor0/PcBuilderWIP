<template>
  <div>
    <h1 class="p-4">Jakie będzie zastosowanie?</h1>
  </div>
  <div>
    <Card class="mt-3" v-for="appliance in appliances">
      <template #title>{{appliance.name}}</template>
      <template #content>
        <p class="m-0">
          {{appliance.description}}
        </p>
      </template>
      <template #footer>
        <Button @click="handleSelect(appliance.name)" size="small" severity="secondary" outlined>
          Wybierz
        </Button>
      </template>
    </Card>
  </div>
</template>

<script>
import stepMixin from "@/mixins/stepMixin";
import Card from "primevue/card"
import Button from "primevue/button"
export default {
  mixins: [stepMixin],
  components: {
    Card,
    Button
  },
  data() {
    return {
      appliances: [
        {
          name: "Oprogramowanie biurowe",
          description: "lorem ipsum"
        },
        {
          name: "Gry komputerowe",
          description: "lorem ipsum"
        },
      ],
      title: "appliance",
      content: null,
    }
  },
  methods: {
    handleSelect(name) {
      this.content = name
      this.setupStore.storeStepResult(this.title, name)
      this.nextStep()
    }
  },
}
</script>