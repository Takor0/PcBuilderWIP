<template>
  <div>
    <h1 class="p-4">Wygenerowane zestawy</h1>
    <DataTable :value="products" tableStyle="min-width: 50rem">
      <Column field="code" header="Code"></Column>
      <Column field="name" header="Name"></Column>
      <Column field="category" header="Category"></Column>
      <Column field="quantity" header="Quantity"></Column>
    </DataTable>
  </div>
</template>

<script>
import Checkbox from "primevue/checkbox";
import stepMixin from "@/components/steps/stepMixin";
export default {
  mixins: [stepMixin],
  components: {Checkbox},
  data() {
    return {
      products: [

      ],
      title: "sets",
    }
  },
  mounted() {

  }
}
</script>