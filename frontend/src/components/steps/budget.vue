<template>
  <div class="slider">
    <div class="card flex justify-center">
      <div class="w-56">

        <InputText v-model.number="content" class="w-full mb-4"/>
        <Slider :min="500" :max="100000" v-model="content" class="w-full"/>
        <br>
        <div class="button">
          <Button @click="handleSelect" type="submit" severity="secondary" label="Zatwierdź"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import stepMixin from "@/components/steps/stepMixin";
import Slider from 'primevue/slider';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';

export default {
  components: {
    Slider,
    InputText,
    Button

  },
  mixins: [stepMixin],
  props: {
    title: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      content: 500
    }
  },
  methods: {
    handleSelect() {
      this.setupStore.storeStepResult(this.title, this.content)
      this.nextStep()
    }
  }
}
</script>
<style>
.slider, .button {
  max-width: fit-content;
  margin-left: auto;
  margin-right: auto;
  width: 500px;
}

</style>