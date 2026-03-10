<template>
  <div class="study-container">
    <div class="study-header">
      <h1>📚 学习</h1>
      <p>持续学习，不断进步</p>
    </div>
    <div class="study-content">
      <div class="study-categories">
        <div 
          class="category-card" 
          v-for="(category, index) in categories" 
          :key="index"
          @click="selectCategory(category)"
        >
          <div class="category-icon">{{ category.icon }}</div>
          <h3>{{ category.name }}</h3>
          <p>{{ category.description }}</p>
          <div class="category-progress">
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: category.progress + '%' }"
              ></div>
            </div>
            <span class="progress-text">{{ category.progress }}%</span>
          </div>
        </div>
      </div>
      
      <div class="study-resources" v-if="selectedCategory">
        <h2>{{ selectedCategory.name }} - 学习资源</h2>
        <div class="resources-list">
          <div 
            class="resource-item" 
            v-for="(resource, index) in selectedCategory.resources" 
            :key="index"
          >
            <a :href="resource.url" target="_blank" class="resource-link">
              <div class="resource-icon">{{ resource.icon }}</div>
              <div class="resource-info">
                <h4>{{ resource.title }}</h4>
                <p>{{ resource.description }}</p>
              </div>
            </a>
          </div>
        </div>
      </div>
      
      <div class="empty-state" v-if="categories.length === 0">
        <p>暂无学习内容</p>
        <p class="hint">学习功能开发中...</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Study',
  data() {
    return {
      selectedCategory: null,
      categories: [
        {
          name: '前端开发',
          icon: '💻',
          description: 'Vue、React、TypeScript等前端技术',
          progress: 75,
          resources: [
            {
              title: 'Vue.js 官方文档',
              description: 'Vue.js 框架的官方文档和教程',
              url: 'https://cn.vuejs.org/',
              icon: '📖'
            },
            {
              title: 'React 官方文档',
              description: 'React 库的官方文档',
              url: 'https://react.dev/',
              icon: '⚛️'
            }
          ]
        },
        {
          name: '后端开发',
          icon: '⚙️',
          description: 'Java、Spring Boot、微服务等后端技术',
          progress: 80,
          resources: [
            {
              title: 'Spring Boot 官方文档',
              description: 'Spring Boot 框架的官方文档',
              url: 'https://spring.io/projects/spring-boot',
              icon: '🌱'
            }
          ]
        },
        {
          name: '数据库',
          icon: '🗄️',
          description: 'MySQL、Redis、MongoDB等数据库技术',
          progress: 65,
          resources: []
        },
        {
          name: '算法与数据结构',
          icon: '🧮',
          description: '算法学习、数据结构、LeetCode刷题',
          progress: 60,
          resources: []
        }
      ]
    }
  },
  methods: {
    selectCategory(category) {
      this.selectedCategory = category
    }
  }
}
</script>

<style lang="scss" scoped>
.study-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  min-height: calc(100vh - 200px);
}

.study-header {
  text-align: center;
  margin-bottom: 40px;
  
  h1 {
    font-size: 2.5rem;
    color: var(--text-primary);
    margin-bottom: 10px;
  }
  
  p {
    color: var(--text-secondary);
    font-size: 1.1rem;
  }
}

.study-categories {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
  
  @include responsive(sm) {
    grid-template-columns: 1fr;
  }
}

.category-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
  cursor: pointer;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    border-color: #667eea;
  }
}

.category-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.category-card h3 {
  font-size: 1.4rem;
  color: var(--text-primary);
  margin: 0 0 10px 0;
}

.category-card p {
  color: var(--text-secondary);
  margin: 0 0 20px 0;
  line-height: 1.6;
}

.category-progress {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: var(--hover-bg);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.9rem;
  color: var(--text-secondary);
  font-weight: 600;
  min-width: 45px;
}

.study-resources {
  margin-top: 40px;
  padding-top: 40px;
  border-top: 1px solid var(--border-color);
  
  h2 {
    font-size: 1.8rem;
    color: var(--text-primary);
    margin-bottom: 24px;
  }
}

.resources-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  
  @include responsive(sm) {
    grid-template-columns: 1fr;
  }
}

.resource-item {
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.resource-link {
  display: flex;
  align-items: center;
  padding: 20px;
  text-decoration: none;
  color: inherit;
  gap: 16px;
}

.resource-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.resource-info {
  flex: 1;
  
  h4 {
    font-size: 1.1rem;
    color: var(--text-primary);
    margin: 0 0 8px 0;
  }
  
  p {
    font-size: 0.9rem;
    color: var(--text-secondary);
    margin: 0;
    line-height: 1.5;
  }
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-secondary);
  
  .hint {
    margin-top: 10px;
    font-size: 0.9rem;
    opacity: 0.7;
  }
}
</style>









