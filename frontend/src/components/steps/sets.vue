<template>
  <div>
    <Dialog v-model:visible="isSpecDialog" modal header="Dodatkowe informacje " :style="{ width: '25rem' }">
      <div v-for="val, key in currentSpec">
        <strong>
          {{ key }}:
        </strong>
        <span>{{ val }}</span>
      </div>
    </Dialog>
    <h1 class="p-4">Wygenerowane zestawy</h1>
    <DataTable :value="data" tableStyle="min-width: 50rem">
      <Column field="name"/>
      <Column field="totalPrice" header="Cena"/>
      <Column
          v-for="part in Object.values(PARTS)"
          :field="part"
          :header="HEADINGS[part]"
      >
        <template #body="{data, index, field}">
          <span v-if="Array.isArray(data[part])">
            {{ data[part].map(part => part.name).join(", ") }}
          </span>
          <span v-else>
            {{ data[part]?.name }}
          </span>
          <Button v-if="data[part]" @click="displayPartSpec(index, field)" size="small" icon="pi pi-search"></Button>
        </template>
      </Column>
      <Column field="save">
        <template #body="{index}">
          <Button @click="saveSet(index)" label="Zapisz" icon="pi pi-save" class="p-button-success"/>
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script>
import Button from "primevue/button";
import {PARTS, HEADINGS} from "@/constants/parts"
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Checkbox from "primevue/checkbox";
import stepMixin from "@/mixins/stepMixin";
import setService from "@/services/setService";
import Dialog from 'primevue/dialog';

export default {
  mixins: [stepMixin],
  components: {Dialog, Button, Checkbox, DataTable, Column},
  data() {
    return {
      PARTS,
      HEADINGS,
      isSpecDialog: false,
      currentSpec: {},
      result: [],
      title: "sets",
    }
  },
  computed: {
    data() {
      return [this.result]
    }
  },
  methods: {
    async saveSet(index) {
      const set = this.result[index]
      await setService.saveSet(set.index)
    },
    displayPartSpec(index, field) {
      this.currentSpec = this.result[field]
      this.isSpecDialog = true
    },
    async generateSet() {
      this.result = await setService.generate(this.setupStore.getRequest(this.setupStore.result))
    },
  },
  watch: {
    "$route.query.step"(val) {
      if (val === "6") this.generateSet()
    }
  },
  async mounted() {
    this.generateSet()
    console.log("Generated result:", this.result);  // Debugging result
  }
}
</script>