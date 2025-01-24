<script lang="ts">
import budget from "@/components/steps/budget.vue";
import req from "@/components/steps/req.vue";
import appliance from "@/components/steps/appliance.vue";
import requirements from "@/components/steps/requirements.vue";
import priority from "@/components/steps/priority.vue";
import sets from "@/components/steps/sets.vue";

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
        case "req":
          return req
        case "appliance":
          return appliance
        case "requirements":
          return requirements
        case "priority":
          return priority
        case "sets":
          return sets;
      }
    }

  }
}
</script>

<template>
  <component :is="getStepComponent(title)"/>
</template>