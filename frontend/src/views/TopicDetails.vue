<template>
  <div>
    <h2>{{ topic.title }}</h2>
    <p>{{ topic.content }}</p>
    <div class="flex align-items-center gap-3 mb-3">
      <h3 class="m-0">Komentarze</h3>
      <Button label="Dodaj komentarz" icon="pi pi-plus" @click="showCommentDialog = true" />
    </div>
    <Dialog header="Dodaj komentarz" v-model:visible="showCommentDialog">
      <form @submit.prevent="createComment">
        <div class="flex flex-column gap-2">
          <Textarea v-model="newComment.content" placeholder="Napisz komentarz..." rows="4" />
          <Button type="submit" label="Dodaj komentarz" class="p-button-primary align-self-end" />
        </div>
      </form>
    </Dialog>
    <ul class="list-none p-0">
      <li v-for="comment in comments" :key="comment.id" class="mb-3 p-3 surface-100 border-round">
        <div class="flex align-items-center justify-content-between">
          <span>{{ comment.content }}</span>
          <Button icon="pi pi-trash" text severity="danger" @click="deleteComment(comment.id)" />
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import ForumService from '@/services/forumService';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import Textarea from 'primevue/textarea';

export default {
  name: 'TopicDetails',
  components: {
    Button,
    Dialog,
    Textarea,
  },
  data() {
    return {
      topic: {},
      comments: [],
      newComment: { content: '', topicId: null },
      showCommentDialog: false,
    };
  },
  props: ['id'],
  methods: {
    async fetchTopic() {
      this.topic = await ForumService.fetchTopicById(this.id);
    },
    async fetchComments() {
      this.comments = await ForumService.fetchCommentsByTopicId(this.id);
    },
    async createComment() {
      console.log(11)
      try {
        this.newComment.topicId = this.id;
        await ForumService.createComment(this.newComment);
        this.fetchComments();
        this.showCommentDialog = false;
      } catch (error) {
        console.error(error);
      }
    },
    async deleteComment(commentId) {
      try {
        const success = await ForumService.deleteComment(commentId);
        if (success) {
          this.fetchComments();
        }
      } catch (error) {
        console.error(error);
      }
    },
  },
  created() {
    this.fetchTopic();
    this.fetchComments();
  },
};

</script>
