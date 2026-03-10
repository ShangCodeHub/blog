<template>
  <div class="note-container">
    <div class="note-header">
      <h1>📝 笔记</h1>
      <p>记录学习过程中的点点滴滴</p>
    </div>
    <div class="note-content">
      <div class="note-grid">
        <div class="note-card" v-for="(note, index) in notes" :key="index">
          <div class="note-card-header">
            <h3>{{ note.title }}</h3>
            <span class="note-date">{{ note.date }}</span>
          </div>
          <div class="note-card-body">
            <p>{{ note.content }}</p>
          </div>
          <div class="note-card-footer">
            <span class="note-tag" v-for="tag in note.tags" :key="tag">{{ tag }}</span>
          </div>
        </div>
      </div>
      <div class="empty-state" v-if="notes.length === 0">
        <p>暂无笔记内容</p>
        <p class="hint">笔记功能开发中...</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Note',
  data() {
    return {
      notes: []
    }
  },
  mounted() {
    // 这里可以调用API获取笔记数据
    // 暂时使用空数组
  }
}
</script>

<style lang="scss" scoped>
.note-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  min-height: calc(100vh - 200px);
}

.note-header {
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

.note-content {
  width: 100%;
}

.note-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  
  @include responsive(md) {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
  }
  
  @include responsive(sm) {
    grid-template-columns: 1fr;
  }
}

.note-card {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
}

.note-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  
  h3 {
    font-size: 1.3rem;
    color: var(--text-primary);
    margin: 0;
    flex: 1;
  }
  
  .note-date {
    font-size: 0.85rem;
    color: var(--text-secondary);
    white-space: nowrap;
  }
}

.note-card-body {
  margin-bottom: 16px;
  
  p {
    color: var(--text-secondary);
    line-height: 1.6;
    margin: 0;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.note-card-footer {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.note-tag {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
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









