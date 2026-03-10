<template>
  <div class="statistics-container">
    <div class="stats-grid">
      <div 
        class="stat-card" 
        v-for="(stat, index) in statistics" 
        :key="index"
        :style="{ '--delay': index * 0.1 + 's' }"
      >
        <div class="card-background" :style="{ background: stat.gradient }"></div>
        <div class="card-content">
          <div class="stat-icon-wrapper">
            <div class="icon-circle" :style="{ background: stat.color }">
              <i :class="stat.icon"></i>
            </div>
            <div class="icon-glow" :style="{ background: stat.color }"></div>
          </div>
          <div class="stat-info">
            <div class="stat-value-wrapper">
              <span class="stat-value" :data-value="stat.value">{{ animatedValue[index] || 0 }}</span>
              <span class="stat-unit" v-if="stat.unit">{{ stat.unit }}</span>
            </div>
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-trend" v-if="stat.trend">
              <i :class="stat.trend > 0 ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
              <span>{{ Math.abs(stat.trend) }}%</span>
            </div>
          </div>
        </div>
        <div class="card-decoration">
          <div class="decoration-circle" :style="{ background: stat.color }"></div>
          <div class="decoration-circle small" :style="{ background: stat.color }"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StatisticsCard',
  props: {
    statistics: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      animatedValue: []
    }
  },
  watch: {
    statistics: {
      handler(newVal) {
        if (newVal.length > 0) {
          this.animateValues()
        }
      },
      immediate: true
    }
  },
  methods: {
    animateValues() {
      this.statistics.forEach((stat, index) => {
        const targetValue = stat.value || 0
        const duration = 1500
        const steps = 60
        const increment = targetValue / steps
        let current = 0
        
        const timer = setInterval(() => {
          current += increment
          if (current >= targetValue) {
            this.$set(this.animatedValue, index, targetValue)
            clearInterval(timer)
          } else {
            this.$set(this.animatedValue, index, Math.floor(current))
          }
        }, duration / steps)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.statistics-container {
  margin-bottom: $spacing-xl;
  padding: $spacing-lg;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.05) 0%, rgba(139, 92, 246, 0.05) 100%);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(99, 102, 241, 0.1);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: $spacing-lg;
  
  @include responsive(md) {
    grid-template-columns: repeat(2, 1fr);
    gap: $spacing-md;
  }
  
  @include responsive(sm) {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  position: relative;
  height: 140px;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: slideInUp 0.6s ease-out;
  animation-delay: var(--delay);
  animation-fill-mode: both;
  
  &:hover {
    transform: translateY(-8px) scale(1.02);
    
    .card-background {
      opacity: 1;
    }
    
    .icon-glow {
      opacity: 0.6;
      transform: scale(1.5);
    }
    
    .decoration-circle {
      transform: scale(1.2);
    }
  }
}

.card-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.3;
  transition: opacity 0.4s ease;
  z-index: 0;
}

.card-content {
  position: relative;
  z-index: 1;
  height: 100%;
  padding: $spacing-lg;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: var(--card-bg);
  backdrop-filter: blur(20px);
}

.stat-icon-wrapper {
  position: relative;
  width: 56px;
  height: 56px;
  margin-bottom: $spacing-md;
}

.icon-circle {
  position: relative;
  z-index: 2;
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.5rem;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.icon-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 56px;
  height: 56px;
  border-radius: 50%;
  opacity: 0;
  filter: blur(20px);
  transition: all 0.4s ease;
  z-index: 1;
}

.stat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.stat-value-wrapper {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 2rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
  font-family: 'Arial', sans-serif;
}

.stat-unit {
  font-size: 0.9rem;
  color: var(--text-secondary);
  font-weight: 500;
}

.stat-label {
  font-size: 0.9rem;
  color: var(--text-secondary);
  font-weight: 500;
  margin-bottom: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.75rem;
  color: #10b981;
  margin-top: 4px;
  
  i {
    font-size: 0.7rem;
  }
}

.card-decoration {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 80px;
  height: 80px;
  z-index: 0;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  transition: transform 0.4s ease;
  
  &.small {
    width: 40px;
    height: 40px;
    top: 20px;
    right: 20px;
  }
  
  &:not(.small) {
    width: 80px;
    height: 80px;
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 暗色主题适配
@media (prefers-color-scheme: dark) {
  .statistics-container {
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);
    border-color: rgba(99, 102, 241, 0.2);
  }
  
  .stat-value {
    background: linear-gradient(135deg, #818cf8 0%, #a78bfa 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}
</style>

