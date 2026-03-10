<template>
  <el-carousel 
    v-if="articles && articles.length" 
    :interval="5000"
    :height="carouselHeight"
    class="article-carousel"
    indicator-position="outside"
  >
    <el-carousel-item v-for="(article, index) in displayArticles" :key="article.id || index">
      <div class="article-slide" @click="handleClick(article.id)">
        <div class="slide-image" :style="getImageStyle(article)">
          <div class="image-overlay"></div>
        </div>
        <div class="slide-content">
          <div class="article-meta">
            <span class="category" v-if="article.categoryName">
              <i class="fas fa-folder"></i>
              {{ article.categoryName }}
            </span>
            <span class="date" v-if="article.createTime">
              <i class="fas fa-calendar"></i>
              {{ formatDate(article.createTime) }}
            </span>
            <span class="views" v-if="article.views !== undefined">
              <i class="fas fa-eye"></i>
              {{ article.views }}
            </span>
          </div>
          <h3 class="article-title">{{ article.title }}</h3>
          <p class="article-summary" v-if="article.summary">{{ article.summary }}</p>
          <div class="article-footer">
            <div class="author-info" v-if="article.author">
              <el-avatar :size="32" :src="article.avatar" class="author-avatar">
                <i class="el-icon-user-solid"></i>
              </el-avatar>
              <span class="author-name">{{ article.author }}</span>
            </div>
            <button class="read-more-btn">
              阅读全文
              <i class="fas fa-arrow-right"></i>
            </button>
          </div>
        </div>
      </div>
    </el-carousel-item>
  </el-carousel>
</template>

<script>
export default {
  name: 'ArticleCarousel',
  props: {
    articles: {
      type: Array,
      required: true,
      default: () => []
    }
  },
  computed: {
    displayArticles() {
      // 只显示前5篇文章作为轮播
      return this.articles.slice(0, 5)
    },
    carouselHeight() {
      return '400px'
    }
  },
  methods: {
    handleClick(articleId) {
      this.$emit('click', articleId)
    },
    getImageStyle(article) {
      if (article.cover) {
        return {
          backgroundImage: `url(${article.cover})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center'
        }
      }
      // 如果没有封面图，使用默认渐变背景
      return {
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
      }
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      
      if (days === 0) return '今天'
      if (days === 1) return '昨天'
      if (days < 7) return `${days}天前`
      if (days < 30) return `${Math.floor(days / 7)}周前`
      if (days < 365) return `${Math.floor(days / 30)}个月前`
      return `${Math.floor(days / 365)}年前`
    }
  }
}
</script>

<style lang="scss" scoped>
.article-carousel {
  width: 100%;
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: $shadow-lg;
  margin-bottom: $spacing-xl;
  
  :deep(.el-carousel__container) {
    height: 400px;
    
    @include responsive(md) {
      height: 350px;
    }
    
    @include responsive(sm) {
      height: 300px;
    }
  }
  
  :deep(.el-carousel__indicators) {
    bottom: 20px;
  }
  
  :deep(.el-carousel__indicator) {
    button {
      background-color: rgba(255, 255, 255, 0.5);
      width: 10px;
      height: 10px;
      border-radius: 50%;
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.8);
      }
    }
    
    &.is-active button {
      background-color: #fff;
      width: 24px;
      border-radius: 5px;
    }
  }
}

.article-slide {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
  overflow: hidden;
}

.slide-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease;
  
  .article-slide:hover & {
    transform: scale(1.05);
  }
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    to bottom,
    transparent 0%,
    rgba(0, 0, 0, 0.3) 50%,
    rgba(0, 0, 0, 0.8) 100%
  );
}

.slide-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: $spacing-xl;
  color: white;
  z-index: 2;
  
  @include responsive(sm) {
    padding: $spacing-lg;
  }
}

.article-meta {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  margin-bottom: $spacing-md;
  font-size: 0.9rem;
  opacity: 0.9;
  
  @include responsive(sm) {
    font-size: 0.8rem;
    gap: $spacing-sm;
    flex-wrap: wrap;
  }
  
  span {
    display: flex;
    align-items: center;
    gap: 4px;
    
    i {
      font-size: 0.85em;
    }
  }
  
  .category {
    background: rgba(74, 222, 128, 0.2);
    padding: 4px 12px;
    border-radius: 4px;
    border: 1px solid rgba(74, 222, 128, 0.3);
  }
}

.article-title {
  font-size: 2rem;
  font-weight: 700;
  margin: 0 0 $spacing-md 0;
  line-height: 1.3;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  
  @include responsive(md) {
    font-size: 1.6rem;
  }
  
  @include responsive(sm) {
    font-size: 1.3rem;
  }
}

.article-summary {
  font-size: 1.1rem;
  line-height: 1.6;
  margin: 0 0 $spacing-lg 0;
  opacity: 0.95;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  
  @include responsive(sm) {
    font-size: 0.95rem;
    -webkit-line-clamp: 2;
  }
}

.article-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: $spacing-lg;
  
  @include responsive(sm) {
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-md;
  }
}

.author-info {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  
  .author-avatar {
    border: 2px solid rgba(255, 255, 255, 0.3);
  }
  
  .author-name {
    font-weight: 500;
  }
}

.read-more-btn {
  display: inline-flex;
  align-items: center;
  gap: $spacing-sm;
  padding: $spacing-sm $spacing-lg;
  background: rgba(74, 222, 128, 0.2);
  border: 2px solid rgba(74, 222, 128, 0.5);
  color: white;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  
  &:hover {
    background: rgba(74, 222, 128, 0.4);
    border-color: rgba(74, 222, 128, 0.8);
    transform: translateX(5px);
    box-shadow: 0 4px 12px rgba(74, 222, 128, 0.3);
  }
  
  i {
    transition: transform 0.3s ease;
  }
  
  &:hover i {
    transform: translateX(3px);
  }
}
</style>









