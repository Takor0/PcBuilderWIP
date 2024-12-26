<script lang="ts">
import budget from "@/components/steps/budget.vue";
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