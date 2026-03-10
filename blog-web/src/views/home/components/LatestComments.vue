<template>
  <el-card class="latest-comments-card" v-if="comments.length > 0">
    <div class="comments-header">
      <i class="fas fa-comments"></i>
      <span>最新评论</span>
    </div>
    <div class="comments-list">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
        @click="goToArticle(comment.articleId)"
      >
        <div class="comment-avatar">
          <el-avatar :size="36" :src="comment.avatar">
            <i class="fas fa-user"></i>
          </el-avatar>
        </div>
        <div class="comment-content">
          <div class="comment-author">{{ comment.nickname }}</div>
          <div class="comment-text">{{ comment.content }}</div>
          <div class="comment-meta">
            <span class="comment-article">《{{ comment.articleTitle }}》</span>
            <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script>
import { getCommentsApi } from '@/api/article'
import { formatTime } from '@/utils/time'

export default {
  name: 'LatestComments',
  data() {
    return {
      comments: []
    }
  },
  methods: {
    formatTime(time) {
      return formatTime(time)
    },
    getLatestComments() {
      getCommentsApi({ pageNum: 1, pageSize: 5 }).then(res => {
        if (res.data && res.data.records) {
          this.comments = res.data.records
        }
      }).catch(() => {
        // 静默失败
      })
    },
    goToArticle(articleId) {
      if (articleId) {
        this.$router.push(`/post/${articleId}`)
      }
    }
  },
  created() {
    this.getLatestComments()
  }
}
</script>

<style lang="scss" scoped>
.latest-comments-card {
  margin-bottom: $spacing-lg;
  background: var(--card-bg);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  
  :deep(.el-card__body) {
    padding: $spacing-lg;
  }
}

.comments-header {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  margin-bottom: $spacing-md;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  
  i {
    color: #4facfe;
  }
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.comment-item {
  display: flex;
  gap: $spacing-md;
  padding: $spacing-md;
  border-radius: 8px;
  background: var(--hover-bg);
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: var(--card-bg);
    transform: translateX(4px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-author {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
  font-size: 0.9rem;
}

.comment-text {
  color: var(--text-secondary);
  font-size: 0.85rem;
  line-height: 1.5;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  font-size: 0.75rem;
  color: var(--text-secondary);
  
  .comment-article {
    color: $primary;
    font-weight: 500;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex: 1;
    min-width: 0;
  }
  
  .comment-time {
    flex-shrink: 0;
  }
}
</style>









