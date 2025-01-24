<template>
  <div>
    <h1 class="p-4">Czy masz jakieś dodatkowe wymagania?</h1>
    <div class="flex flex-col gap-2 ml-4 mt-2" v-for="(req, reqIndex) in requirements" :key="reqIndex">
      <template v-if="req.type === 'buttons'">
        <label class="block font-bold mb-1">{{ req.label }}</label>
        <div class="flex gap-2">
          <button
              v-for="option in req.options"
              :key="option.value"
              class="px-4 py-2 border rounded"
              :class="{'bg-blue-500 text-white': result[req.value] === option.value, 'bg-gray-200': result[req.value] !== option.value}"
              @click="() => result[req.value] = option.value"
          >
            {{ option.label }}
          </button>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import stepMixin from "@/mixins/stepMixin";
import { PerformancePriority } from "@/services/setService";

export default {
  name: "priorityStep",
  mixins: [stepMixin],
  data() {
    return {
      title: "priority",
      result: {
        priority: null
      },
      requirements: [
        {
          type: "buttons",
          label: "Priorytet wydajności",
          value: "priority",
          options: [
            { label: "CPU", value: PerformancePriority.CPU_PRIORITY },
            { label: "GPU", value: PerformancePriority.GPU_PRIORITY },
            { label: "Balans", value: PerformancePriority.BALANCED }
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