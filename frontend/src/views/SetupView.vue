<script lang="ts">

import Stepper from 'primevue/stepper';
import StepList from 'primevue/steplist';
import StepPanels from 'primevue/steppanels';
import Step from 'primevue/step';
import StepPanel from 'primevue/steppanel';
import Button from 'primevue/button'
import SetupStep from '@/components/steps/SetupStep.vue'
import stepMixin from "@/mixins/stepMixin";
import { useRouter } from 'vue-router';

export default {
  mixins: [stepMixin],
  name: 'SetupView',
  components: {
    Stepper,
    StepList,
    StepPanels,
    Step,
    StepPanel,
    Button,
    SetupStep,
  },
  mounted() {
    if (this.$route.query.clear) {
      this.clearSetup()
      this.$router.replace({query: {}})
    }
  },
  data() {
    return {
      currentStep: parseInt(this.$route.query?.step || 1),
      steps: [
        {label: 'Budżet', value: 1, title: 'budget'},
        {label: 'Wymagania', value: 2, title: 'req'},
        {label: 'Zastosowanie', value: 3, title: 'appliance'},
        {label: 'Priorytet', value: 4, title: 'priority'},
        {label: 'Wymagania dodatkowe', value: 5, title: 'requirements'},
        {label: 'Zestawy', value: 6, title: 'sets'},
      ]
    }
  },
  methods: {
    isPreviousStep(stepValue: number) {
      return stepValue > 1;
    },
    isNextStep(stepValue: number) {
      return stepValue < this.steps.length;
    }
  },
  watch: {
    currentStep(step) {
      this.$router.replace({query: {step}});
    }
  },
}

</script>


<template>
  <div>
    <Stepper @update:value="value => currentStep = value" :value="currentStep">
      <StepList>
        <Step v-for="step in steps" :key="step.value" :value="step.value">{{ step.label }}</Step>
      </StepList>
      <StepPanels>
        <template v-for="step in steps" :key="step.value">
          <StepPanel v-slot="{ activateCallback }" :value="step.value">
            <div class="flex align-items-center gap-2 justify-content-between">
              <Button
                v-if="isPreviousStep(step.value)"
                label="Wstecz"
                icon="pi pi-arrow-left"
                iconPos="left"
                @click="activateCallback(step.value-1)"
              />
              <span style="width: 100px" v-else/>
              <div>{{ step.label }}</div>
              <Button
                v-if="isNextStep(step.value)"
                label="Dalej"
                icon="pi pi-arrow-right"
                iconPos="right"
                @click="activateCallback(step.value+1)"
              />
              <span style="width: 100px" v-else/>
            </div>
          </StepPanel>
          <StepPanel v-slot="{ activateCallback }" :value="step.value" class="mt-2">
            <SetupStep @next="activateCallback(step.value+1)" :title="step.title"/>
          </StepPanel>
        </template>
      </StepPanels>
    </Stepper>
  </div>
</template>

