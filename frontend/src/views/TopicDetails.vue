<template>
  <div>
    <h2>{{ topic.title }}</h2>
    <p>{{ topic.content }}</p>
    <h3>Comments</h3>
    <Button label="Add Comment" icon="pi pi-plus" @click="showCommentDialog = true" />
    <Dialog header="Add Comment" v-model:visible="showCommentDialog">
      <form @submit.prevent="createComment">
        <Textarea v-model="newComment.content" placeholder="Write a comment..." rows="4" />
        <Button label="Submit" class="p-button-primary" />
      </form>
    </Dialog>
    <ul>
      <li v-for="comment in comments" :key="comment.id">
        {{ comment.content }}
        <Button icon="pi pi-trash" class="p-button-text" @click="deleteComment(comment.id)" />
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
