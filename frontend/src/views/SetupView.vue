<script lang="ts">

import Stepper from 'primevue/stepper';
import StepList from 'primevue/steplist';
import StepPanels from 'primevue/steppanels';
import Step from 'primevue/step';
import StepPanel from 'primevue/steppanel';
import Button from 'primevue/button'
import SetupStep from '@/components/SetupStep.vue'

export default {
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
  data() {
    return {
      steps: [
        {label: 'Budżet', value: 1, title: 'budget'},
        {label: 'Zastosowanie', value: 2, title: 'appliance'},
        {label: 'Wymagania', value: 3, title: 'requirements'},
        {label: 'Preferencje', value: 4, title: 'preferences'},
        {label: 'Zestawy', value: 5, title: 'sets'},
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
  }
}

</script>


<template>
  <div>
    <Stepper :value="1">
      <StepList>
        <Step v-for="step in steps" :key="step.value" :value="step.value">{{ step.label }}</Step>
      </StepList>
      <StepPanels>
        <template v-for="step in steps" :key="step.value">
          <StepPanel
            v-slot="{ activateCallback }"
            :value="step.value"
          >
            <div class="flex align-items-center gap-2 justify-content-between">
              <Button
                v-if="isPreviousStep(step.value)"
                label="Wstecz"
                icon="pi pi-arrow-left"
                iconPos="left"
                @click="activateCallback(step.value-1)"
              />
              <span style="width: 100px" v-else/>
              <div>
                {{ step.label }}
              </div>
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
          <StepPanel
            :value="step.value"
            class="mt-2"
          >
            <SetupStep :title="step.title"/>
          </StepPanel>
        </template>
      </StepPanels>
    </Stepper>
  </div>
</template>

