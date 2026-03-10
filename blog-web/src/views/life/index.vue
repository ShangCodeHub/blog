<template>
  <div class="life-container">
    <div class="life-header">
      <h1>🎨 生活 & 爱好</h1>
      <p>记录生活中的美好瞬间</p>
    </div>
    <div class="life-content">
      <div class="life-sections">
        <div class="section-card" v-for="(section, index) in sections" :key="index">
          <div class="section-header">
            <div class="section-icon">{{ section.icon }}</div>
            <h2>{{ section.title }}</h2>
          </div>
          <div class="section-content">
            <p>{{ section.description }}</p>
            <div class="section-items" v-if="section.items && section.items.length > 0">
              <div 
                class="item-card" 
                v-for="(item, itemIndex) in section.items" 
                :key="itemIndex"
              >
                <h4>{{ item.title }}</h4>
                <p>{{ item.content }}</p>
              </div>
            </div>
            <div class="empty-section" v-else>
              <p>暂无内容</p>
            </div>
          </div>
        </div>
      </div>
      
      <div class="life-gallery" v-if="photos.length > 0">
        <h2>📸 生活相册</h2>
        <div class="gallery-grid">
          <div 
            class="gallery-item" 
            v-for="(photo, index) in photos" 
            :key="index"
            @click="viewPhoto(photo)"
          >
            <img :src="photo.url" :alt="photo.title" />
            <div class="gallery-overlay">
              <h4>{{ photo.title }}</h4>
            </div>
          </div>
        </div>
      </div>
      
      <div class="empty-state" v-if="sections.length === 0">
        <p>暂无内容</p>
        <p class="hint">生活记录功能开发中...</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Life',
  data() {
    return {
      sections: [
        {
          title: '🏀 篮球',
          icon: '🏀',
          description: '热爱篮球运动，享受在球场上挥洒汗水的时光',
          items: [
            {
              title: '最喜欢的球队',
              content: 'NBA - 湖人队'
            },
            {
              title: '打球频率',
              content: '每周2-3次'
            }
          ]
        },
        {
          title: '💻 计算机',
          icon: '💻',
          description: '对计算机技术充满热情，喜欢探索新技术',
          items: [
            {
              title: '主要方向',
              content: '全栈开发、系统架构'
            },
            {
              title: '学习方式',
              content: '实践项目 + 技术博客'
            }
          ]
        },
        {
          title: '🎵 音乐',
          icon: '🎵',
          description: '喜欢听音乐，音乐是生活的一部分',
          items: []
        },
        {
          title: '📚 阅读',
          icon: '📚',
          description: '阅读技术书籍和文学作品',
          items: []
        }
      ],
      photos: []
    }
  },
  methods: {
    viewPhoto(photo) {
      // 查看照片详情
      console.log('View photo:', photo)
    }
  }
}
</script>

<style lang="scss" scoped>
.life-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  min-height: calc(100vh - 200px);
}

.life-header {
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

.life-sections {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
  
  @include responsive(sm) {
    grid-template-columns: 1fr;
  }
}

.section-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
}

.section-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  
  .section-icon {
    font-size: 2.5rem;
  }
  
  h2 {
    font-size: 1.5rem;
    color: var(--text-primary);
    margin: 0;
  }
}

.section-content {
  p {
    color: var(--text-secondary);
    line-height: 1.6;
    margin-bottom: 20px;
  }
}

.section-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-card {
  background: var(--hover-bg);
  border-radius: 8px;
  padding: 16px;
  
  h4 {
    font-size: 1rem;
    color: var(--text-primary);
    margin: 0 0 8px 0;
  }
  
  p {
    font-size: 0.9rem;
    color: var(--text-secondary);
    margin: 0;
  }
}

.empty-section {
  text-align: center;
  padding: 20px;
  color: var(--text-secondary);
  font-size: 0.9rem;
  opacity: 0.7;
}

.life-gallery {
  margin-top: 40px;
  padding-top: 40px;
  border-top: 1px solid var(--border-color);
  
  h2 {
    font-size: 1.8rem;
    color: var(--text-primary);
    margin-bottom: 24px;
  }
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  
  @include responsive(sm) {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}

.gallery-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s ease;
  
  &:hover {
    transform: scale(1.05);
    
    .gallery-overlay {
      opacity: 1;
    }
  }
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.gallery-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  display: flex;
  align-items: flex-end;
  padding: 16px;
  opacity: 0;
  transition: opacity 0.3s ease;
  
  h4 {
    color: white;
    margin: 0;
    font-size: 0.9rem;
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









