<script lang="ts">
import budget from "@/components/steps/budget.vue";
import appliance from "@/components/steps/appliance.vue";
import requirements from "@/components/steps/requirements.vue";
import preference from "@/components/steps/preference.vue";

export default {
  props: {
    title: {
      type: String,
      required: true
    }
  },
  methods: {
    getStepComponent(title: string) {
      switch (title) {
        case "budget":
          return budget;
        case "appliance":
          return appliance
        case "requirements":
          return requirements
        case "preference":
          return preference;
      }
    }

  }
}
</script>

<template>
  <component :is="getStepComponent(title)"/>
</template>