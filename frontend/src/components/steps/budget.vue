<template>
  <div class="slider">
    <div class="card flex justify-center">
      <div class="w-56">

        <InputText v-model.number="content" class="w-full mb-4"/>
        <Slider :min="2000" :max="15000" v-model="content" class="w-full"/>
        <br>
        <div class="button">
          <Button @click="handleSelect" type="submit" severity="secondary" label="Zatwierdź"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import stepMixin from "@/mixins/stepMixin";
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
  data() {
    return {
      title: 'budget',
      content: 2000
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