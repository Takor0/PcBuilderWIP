<template>
<div class="slider">
    <div class="card flex justify-center">
        <div class="w-56">

            <InputText v-model.number="content" class="w-full mb-4" />
            <Slider :max= "10000" v-model="content"  class="w-full" />
          <br>
          <div class="button">
          <Button type="submit" severity="secondary" label="Zatwierdź" />
          </div>
          </div>
    </div>
</div>
</template>

<script>
import Slider from 'primevue/slider';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
export default {
  components:{
    Slider,
    InputText,
    Button

  },
  props: {
    title: {
      type: String,
      required: true
    }
  },
  data(){
    return{
      content:1
    }
  }
}
</script>
<style>
.slider, .button{
  max-width: fit-content;
  margin-left: auto;
  margin-right: auto;
  width: 500px;
}

</style>