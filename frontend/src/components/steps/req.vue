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
    </div>
  </div>
</template>

<script>
import Select from "primevue/select";
import stepMixin from "@/mixins/stepMixin";
import { CpuPreference, GpuPreference } from "@/services/setService";

export default {
  name: "RequirementsStep",
  mixins: [stepMixin],
  components: {
    Select
  },
  data() {
    return {
      title: "req",
      result: {
        cpuPreference: null,
        gpuPreference: null
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
