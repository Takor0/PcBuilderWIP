<script lang="ts">
import budget from "@/components/steps/budget.vue";

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
      }
    }

  }
}
</script>

<template>
  <component :is="getStepComponent(title)"/>
</template>