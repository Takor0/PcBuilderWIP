<template>
  <div>
    <h1 class="p-4">Czy masz jakieś dodatkowe wymagania?</h1>
    <div class="flex flex-col gap-2 ml-4 mt-2" v-for="(req, reqIndex) in requirements" :key="reqIndex">
      <template v-if="req.type === 'buttons'">
        <label class="block font-bold mb-1">{{ req.label }}</label>
        <div class="flex gap-2">
          <Button
              v-for="option in req.options"
              :key="option.value"
              :label="option.label"
              :severity="result[req.value] === option.value ? 'primary' : 'secondary'"
              @click="() => result[req.value] = option.value"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import stepMixin from "@/mixins/stepMixin";
import { PerformancePriority } from "@/services/setService";
import Button from 'primevue/button';

export default {
  name: "priorityStep",
  components: {
    Button
  },
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