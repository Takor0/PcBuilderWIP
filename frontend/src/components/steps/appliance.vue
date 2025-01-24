<template>
  <div>
    <h1 class="p-4 text-center">Jakie będzie zastosowanie?</h1>
    <div class="flex flex-wrap gap-4 justify-center items-center mt-3">
      <Card
          v-for="appliance in appliances"
          :key="appliance.value"
          class="w-64"
      >
        <template #title>
          {{ appliance.label }}
        </template>
        <template #footer>
          <Button
              @click="handleSelect(appliance.value)"
              size="small"
              severity="secondary"
              outlined
          >
            Wybierz
          </Button>
        </template>
      </Card>
    </div>
  </div>
</template>

<script>
import { ComputerUsage } from "@/services/setService";
import stepMixin from "@/mixins/stepMixin";
import Card from "primevue/card";
import Button from "primevue/button";

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
          label: "Gry komputerowe esport",
          value: ComputerUsage.GAMING_ESPORT,
        },
        {
          label: "Gry komputerowe AAA",
          value: ComputerUsage.GAMING_AAA,
        },
        {
          label: "Programowanie",
          value: ComputerUsage.PROGRAMMING,
        },
        {
          label: "Renderowanie grafiki",
          value: ComputerUsage.GRAPHICS_RENDERING,
        },
        {
          label: "Streamowanie",
          value: ComputerUsage.STREAMING,
        },
        {
          label: "Praca biurowa",
          value: ComputerUsage.OFFICE_WORK,
        }
      ],
      title: "appliance",
      content: null,
    };
  },
  methods: {
    handleSelect(name) {
      this.content = name;
      this.setupStore.storeStepResult(this.title, name);
      this.nextStep();
    }
  }
};
</script>
