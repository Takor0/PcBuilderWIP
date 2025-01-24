<template>
  <div>
    <h2>Forum</h2>
    <Button label="Create Topic" icon="pi pi-plus" class="p-button-success" @click="showCreateDialog = true"/>
    <Dialog header="Create Topic" v-model:visible="showCreateDialog" :closable="true">
      <form @submit.prevent="createTopic">
        <InputText v-model="newTopic.title" placeholder="Topic Title" class="p-mb-2"/>
        <Textarea v-model="newTopic.content" placeholder="Topic Content" rows="4"/>
        <Button label="Submit" type="submit" class="p-button-primary"/>
      </form>
    </Dialog>
    <DataTable :value="topics" responsiveLayout="scroll">
      <Column field="id" header="ID"></Column>
      <Column field="title" header="Title"></Column>
      <Column field="commentsCount" header="Liczba komentarzy"/>
      <Column field="dateOfCreation" header="Data utworzenia"/>
      <Column field="save">
        <template #body="{data}">
          <Button @click="() => $router.push({name: 'thread', params: { id: data.id }})">Otwórz wątek</Button>
          <Button @click="deleteTopic(data.id)"><i class="pi pi-trash"></i></Button>
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script>
import ForumService from '@/services/forumService';
import Button from 'primevue/button';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import Textarea from 'primevue/textarea';

export default {
  components: {Button, DataTable, Column, Dialog, InputText, Textarea},
  data() {
    return {
      topics: [],
      newTopic: {title: '', content: '', link:''},
      showCreateDialog: false,
    };
  },
  methods: {
    async fetchTopics() {
      this.topics = await ForumService.fetchTopics();
    },
    async createTopic() {
      try {
        await ForumService.createTopic(this.newTopic);
        this.fetchTopics();
        this.showCreateDialog = false;
        this.newTopic = {title: '', content: '', link:''};
      } catch (error) {

        console.error(error);
      }
    },
    async deleteTopic(id) {
      try {
        const success = await ForumService.deleteTopic(id);
        if (success) {
          this.fetchTopics();
        }
      } catch (error) {
        console.error(error);
      }
    },
  },
  created() {
    this.fetchTopics();
  },
};
</script>
