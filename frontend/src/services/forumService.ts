// src/services/ForumService.js

import { BASE_URL } from '@/constants/common';
import { request } from '@/utils/request';

const FORUM_ENDPOINTS = {
    base: `${BASE_URL}api/forum/`,
    topics: () => `${FORUM_ENDPOINTS.base}topic`,
    topic: (id) => `${FORUM_ENDPOINTS.base}topic/${id}`,
    topicComments: (id) => `${FORUM_ENDPOINTS.base}topic/${id}/comments`,
    createTopic: () => `${FORUM_ENDPOINTS.base}topic/create`,
    deleteTopic: (id) => `${FORUM_ENDPOINTS.base}topic/delete/${id}`,
    createComment: () => `${FORUM_ENDPOINTS.base}comment/create`,
    deleteComment: (id) => `${FORUM_ENDPOINTS.base}comment/delete/${id}`,
};

class ForumService {
    async fetchTopics() {
        const { data } = await request({ url: FORUM_ENDPOINTS.topics() });
        return data;
    }

    async fetchTopicById(id) {
        const { data } = await request({ url: FORUM_ENDPOINTS.topic(id) });
        return data;
    }

    async fetchCommentsByTopicId(id) {
        const { data } = await request({ url: FORUM_ENDPOINTS.topicComments(id) });
        return data;
    }

    async createTopic(topicPayload) {
        const { data } = await request({
            url: FORUM_ENDPOINTS.createTopic(),
            method: 'POST',
            body: topicPayload,
        });
        return data;
    }

    async deleteTopic(id) {
        const { res } = await request({
            url: FORUM_ENDPOINTS.deleteTopic(id),
            method: 'DELETE',
        });
        return res.ok;
    }

    async createComment(commentPayload) {
        const { data } = await request({
            url: FORUM_ENDPOINTS.createComment(),
            method: 'POST',
            body: commentPayload,
        });
        return data;
    }

    async deleteComment(id) {
        const { res } = await request({
            url: FORUM_ENDPOINTS.deleteComment(id),
            method: 'DELETE',
        });
        return res.ok;
    }
}

export default new ForumService();
