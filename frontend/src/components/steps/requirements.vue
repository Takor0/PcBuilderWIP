<template>
  <div>
    <h1 class="p-4">Czy masz jakieś dodatkowe wymagania?</h1>
    <div class="flex flex-col gap-2 ml-4 mt-2" v-for="(req, reqIndex) in requirements" :key="reqIndex">
      <template v-if="req.type === 'select'">
        <label class="block font-bold mb-1">{{ req.label }}</label>
        <Select
            v-model="result[req.value]"
            :options="req.options"
            optionLabel="label"
            optionValue="value"
            placeholder="Wybierz..."
            class="w-64"
        />
      </template>
      <template v-else-if="req.type === 'checkbox'">
        <label class="block font-bold mb-1">Dodatkowe opcje:</label>
        <div
            v-for="(option, optionIndex) in req.options"
            :key="optionIndex"
            class="flex items-center gap-2"
        >
          <Checkbox
              v-model="result.requirements"
              :value="option.value"
              class="mr-2"
          />
          <label>{{ option.label }}</label>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import Select from "primevue/select";
import Checkbox from "primevue/checkbox";
import stepMixin from "@/mixins/stepMixin";
import {
  CpuPreference,
  GpuPreference,
  PerformancePriority,
  AdditionalRequirement
} from "@/services/setService";

export default {
  name: "RequirementsStep",
  mixins: [stepMixin],
  components: {
    Select,
    Checkbox
  },
  data() {
    return {
      title: "requirements",
      result: {
        cpuPreference: null,
        gpuPreference: null,
        priority: null,
        requirements: []
      },
      requirements: [
        {
          type: "select",
          label: "Producent CPU",
          value: "cpuPreference",
          options: [
            { label: "Intel", value: CpuPreference.INTEL },
            { label: "AMD", value: CpuPreference.AMD },
            { label: "Brak", value: CpuPreference.NO_PREFERENCE }
          ]
        },
        {
          type: "select",
          label: "Producent GPU",
          value: "gpuPreference",
          options: [
            { label: "Nvidia", value: GpuPreference.NVIDIA },
            { label: "AMD", value: GpuPreference.AMD },
            { label: "Brak", value: GpuPreference.NO_PREFERENCE }
          ]
        },
        {
          type: "select",
          label: "Priorytet wydajności",
          value: "priority",
          options: [
            { label: "CPU", value: PerformancePriority.CPU_PRIORITY },
            { label: "GPU", value: PerformancePriority.GPU_PRIORITY },
            { label: "Balans", value: PerformancePriority.BALANCED }
          ]
        },
        {
          value: "requirements",
          type: "checkbox",
          options: [
            {
              value: AdditionalRequirement.QUIET_OPERATION,
              label: "Cicha praca"
            },
            {
              value: AdditionalRequirement.OVERCLOCKING,
              label: "Podkręcanie"
            },
            {
              value: AdditionalRequirement.RGB,
              label: "Ledy RGB"
            },
            {
              value: AdditionalRequirement.WIFI,
              label: "Karta sieciowa WiFi"
            },
            {
              value: AdditionalRequirement.SMALL_FORM_FACTOR,
              label: "Małe wymiary"
            }
          ]
        }
      ]
    };
  },
  watch: {
    result: {
      handler() {
        this.setupStore.storeStepResult(this.title, this.result);
      },
      deep: true
    }
  },
  mounted() {
    this.result = this.setupStore[this.title] || this.result;
  }
};
</script>
