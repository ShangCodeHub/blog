<template>
  <div class="terminal-container">
    <!-- 左侧个人信息面板 -->
    <div class="profile-panel">
      <div class="profile-card">
        <div class="avatar-wrapper">
          <el-avatar 
            :size="120" 
            :src="userAvatar" 
            alt="用户头像"
            class="profile-avatar"
          >
            <i class="el-icon-user-solid"></i>
          </el-avatar>
          <div class="avatar-glow"></div>
        </div>
        <h2 class="username">{{ userName }}</h2>
        <p class="signature">{{ userSignature }}</p>
        <div class="profile-tags">
          <span class="tag" v-for="(tag, index) in userTags" :key="index">
            {{ tag }}
          </span>
        </div>
      </div>
    </div>
    
    <!-- 右侧终端窗口 -->
    <div class="terminal-window">
      <div class="terminal-header">
        <div class="terminal-buttons">
          <span class="btn btn-close"></span>
          <span class="btn btn-minimize"></span>
          <span class="btn btn-maximize"></span>
        </div>
        <div class="terminal-title">SHANG@blog ~</div>
      </div>
      <div class="terminal-body" ref="terminalBody">
        <div class="terminal-content">
          <div class="welcome-message">
            <pre class="ascii-art">{{ asciiArt }}</pre>
            <div class="welcome-text">
              <p>欢迎来到 SHANG 的个人空间！</p>
              <p>可用命令：</p>
            </div>
            <div class="commands-list">
              <div 
                class="command-item" 
                v-for="(cmd, index) in availableCommands" 
                :key="index"
                @click="executeCommandDirectly(cmd.command)"
                :title="`点击执行: ${cmd.command}`"
              >
                <span class="cmd-name">{{ cmd.name }}</span>
                <span class="cmd-desc">- {{ cmd.desc }}</span>
                <span class="cmd-hint">(点击执行)</span>
              </div>
            </div>
          </div>
          
          <div 
            v-for="(line, index) in outputLines" 
            :key="index" 
            class="terminal-line"
            :class="line.type"
          >
            <span v-if="line.type === 'command'" class="prompt">
              <span class="user">shang@blog</span>
              <span class="path">~</span>
              <span class="symbol">$</span>
            </span>
            <span class="content" v-html="line.content"></span>
          </div>
          
          <div class="terminal-line input-line">
            <span class="prompt">
              <span class="user">shang@blog</span>
              <span class="path">~</span>
              <span class="symbol">$</span>
            </span>
            <input
              ref="commandInput"
              v-model="currentCommand"
              @keydown.enter="executeCommand"
              @keydown.up.prevent="historyUp"
              @keydown.down.prevent="historyDown"
              @keydown.tab.prevent="handleTab"
              @focus="handleFocus"
              @blur="handleBlur"
              class="command-input"
              spellcheck="false"
              autocomplete="off"
            />
            <span class="cursor" :class="{ blink: !isTyping }">█</span>
          </div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script>
export default {
  name: 'Terminal',
  data() {
    return {
      currentCommand: '',
      outputLines: [],
      commandHistory: [],
      historyIndex: -1,
      isTyping: false,
      stats: {
        articles: 0,
        views: 0,
        visitors: 0
      },
      userTags: ['Java', 'Vue', 'AI-RAG', 'DEV', 'Fine-Tuning'],
      availableCommands: [
        { name: 'blog', command: 'blog', desc: '进入博客首页' },
        { name: 'note', command: 'note', desc: '进入笔记页面' },
        { name: 'study 或 学习', command: 'study', desc: '进入学习页面' },
        { name: 'life 或 爱好', command: 'life', desc: '进入生活/爱好页面' },
        { name: 'clear', command: 'clear', desc: '清空终端' },
        { name: 'exit', command: 'exit', desc: '退出（返回博客首页）' }
      ],
      asciiArt: `
╔═══════════════════════════════════════╗
║                                       ║
║    ███████╗██╗  ██╗ █████╗ ███╗   ██╗ ║
║    ██╔════╝██║  ██║██╔══██╗████╗  ██║ ║
║    ███████╗███████║███████║██╔██╗ ██║ ║
║    ╚════██║██╔══██║██╔══██║██║╚██╗██║ ║
║    ███████║██║  ██║██║  ██║██║ ╚████║ ║
║    ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝ ║
║                                       ║
╚═══════════════════════════════════════╝
      `,
      commands: {
        'blog': () => {
          this.addOutput('正在跳转到博客首页...', 'system')
          setTimeout(() => {
            this.$router.push('/blog')
          }, 500)
        },
        'note': () => {
          this.addOutput('正在跳转到笔记页面...', 'system')
          setTimeout(() => {
            this.$router.push('/note')
          }, 500)
        },
        '学习': () => this.executeCommand('study'),
        'study': () => {
          this.addOutput('正在跳转到学习页面...', 'system')
          setTimeout(() => {
            this.$router.push('/study')
          }, 500)
        },
        'life': () => this.executeCommand('爱好'),
        '爱好': () => {
          this.addOutput('正在跳转到生活/爱好页面...', 'system')
          setTimeout(() => {
            this.$router.push('/life')
          }, 500)
        },
        'help': () => {
          this.addOutput('可用命令：', 'system')
          this.availableCommands.forEach(cmd => {
            this.addOutput(`  ${cmd.name.padEnd(15)} - ${cmd.desc}`, 'system')
          })
        },
        'clear': () => {
          this.outputLines = []
        },
        'exit': () => {
          this.addOutput('正在退出...', 'system')
          setTimeout(() => {
            this.$router.push('/blog')
          }, 500)
        }
      }
    }
  },
  computed: {
    userAvatar() {
      return this.$store.state.userInfo?.avatar || this.$store.state.webSiteInfo?.touristAvatar || ''
    },
    userName() {
      return this.$store.state.userInfo?.nickname || this.$store.state.webSiteInfo?.name || 'SHANG'
    },
    userSignature() {
      return this.$store.state.userInfo?.signature || this.$store.state.webSiteInfo?.description || '一个普通人  爱好骑车 享受沿路的风景   瞎折腾浪费时间最在行 最爱solo'
    }
  },
  mounted() {
    this.loadStats()
    this.$nextTick(() => {
      this.focusInput()
      this.scrollToBottom()
    })
    // 确保输入框始终可以获得焦点
    document.addEventListener('click', this.handleDocumentClick)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleDocumentClick)
  },
  methods: {
    focusInput() {
      this.$nextTick(() => {
        const input = this.$refs.commandInput
        if (input) {
          input.focus()
        }
      })
    },
    handleDocumentClick(e) {
      // 如果点击的是终端区域，确保输入框获得焦点
      if (this.$el && this.$el.contains(e.target)) {
        this.focusInput()
      }
    },
    handleFocus() {
      this.isTyping = true
    },
    handleBlur() {
      this.isTyping = false
      // 延迟重新聚焦，避免与其他交互冲突
      setTimeout(() => {
        if (this.$refs.commandInput) {
          this.$refs.commandInput.focus()
        }
      }, 100)
    },
    handleTab() {
      // Tab键自动补全功能（可选）
      // 暂时不做处理
    },
    executeCommandDirectly(command) {
      // 直接执行命令，不显示命令输入
      if (this.commands[command]) {
        this.addOutput(`执行命令: ${command}`, 'system')
        this.commands[command]()
      } else {
        this.addOutput(`命令未找到: ${command}`, 'error')
      }
      this.$nextTick(() => {
        this.scrollToBottom()
        this.focusInput()
      })
    },
    async loadStats() {
      // 加载统计数据
      this.stats.views = this.$store.state.siteAccess || 0
      this.stats.visitors = this.$store.state.visitorAccess || 0
      
      // 获取文章总数
      try {
        const { getArticlesApi } = await import('@/api/article')
        const res = await getArticlesApi({ pageNum: 1, pageSize: 1 })
        if (res && res.data) {
          this.stats.articles = res.data.total || 0
        }
      } catch (error) {
        console.error('Failed to load article count:', error)
      }
    },
    executeCommand() {
      const command = this.currentCommand.trim().toLowerCase()
      
      if (!command) {
        this.addOutput('', 'command')
        this.scrollToBottom()
        return
      }
      
      // 保存命令到历史记录
      this.commandHistory.push(command)
      this.historyIndex = this.commandHistory.length
      
      // 显示命令
      this.addOutput(command, 'command')
      
      // 执行命令
      if (this.commands[command]) {
        this.commands[command]()
      } else {
        this.addOutput(`命令未找到: ${command}。输入 <span style="color: #4ade80;">help</span> 查看可用命令。`, 'error')
      }
      
      // 清空输入
      this.currentCommand = ''
      
      // 滚动到底部并重新聚焦
      this.$nextTick(() => {
        this.scrollToBottom()
        this.focusInput()
      })
    },
    addOutput(content, type = 'output') {
      this.outputLines.push({
        content,
        type
      })
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const terminalBody = this.$refs.terminalBody
        if (terminalBody) {
          terminalBody.scrollTop = terminalBody.scrollHeight
        }
      })
    },
    historyUp() {
      if (this.commandHistory.length === 0) return
      if (this.historyIndex > 0) {
        this.historyIndex--
        this.currentCommand = this.commandHistory[this.historyIndex]
      }
    },
    historyDown() {
      if (this.commandHistory.length === 0) return
      if (this.historyIndex < this.commandHistory.length - 1) {
        this.historyIndex++
        this.currentCommand = this.commandHistory[this.historyIndex]
      } else {
        this.historyIndex = this.commandHistory.length
        this.currentCommand = ''
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.terminal-container {
  width: 100%;
  height: 100vh;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
  gap: 24px;
  
  @include responsive(lg) {
    flex-direction: column;
    padding: 15px;
    gap: 20px;
  }
}

.profile-panel {
  flex-shrink: 0;
  width: 320px;
  
  @include responsive(lg) {
    width: 100%;
    max-width: 500px;
  }
  
  @include responsive(md) {
    width: 100%;
  }
}

.profile-card {
  background: rgba(30, 30, 30, 0.95);
  border-radius: 16px;
  padding: 32px 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  animation: slideInLeft 0.6s ease-out;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.avatar-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
  
  .profile-avatar {
    border: 4px solid rgba(74, 222, 128, 0.3);
    box-shadow: 0 8px 32px rgba(74, 222, 128, 0.3);
    transition: all 0.3s ease;
    
    &:hover {
      transform: scale(1.05);
      box-shadow: 0 12px 40px rgba(74, 222, 128, 0.5);
    }
  }
  
  .avatar-glow {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 140px;
    height: 140px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(74, 222, 128, 0.2) 0%, transparent 70%);
    animation: pulse 2s ease-in-out infinite;
    z-index: -1;
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.6;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.1);
  }
}

.username {
  text-align: center;
  font-size: 1.8rem;
  font-weight: 700;
  color: #4ade80;
  margin: 0 0 12px 0;
  text-shadow: 0 0 20px rgba(74, 222, 128, 0.5);
}

.signature {
  text-align: center;
  color: #9ca3af;
  font-size: 0.95rem;
  line-height: 1.6;
  margin: 0 0 24px 0;
  min-height: 48px;
}

.profile-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  margin-top: 20px;
}

.tag {
  display: inline-block;
  padding: 6px 14px;
  background: linear-gradient(135deg, rgba(74, 222, 128, 0.2) 0%, rgba(96, 165, 250, 0.2) 100%);
  border: 1px solid rgba(74, 222, 128, 0.3);
  border-radius: 20px;
  color: #4ade80;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.3s ease;
  
  &:hover {
    background: linear-gradient(135deg, rgba(74, 222, 128, 0.3) 0%, rgba(96, 165, 250, 0.3) 100%);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(74, 222, 128, 0.3);
  }
}

.terminal-window {
  flex: 1;
  max-width: 900px;
  background: #1e1e1e;
  border-radius: 8px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  animation: slideIn 0.5s ease-out;
  min-width: 0;
  
  @include responsive(lg) {
    width: 100%;
    max-width: 100%;
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.terminal-header {
  background: #2d2d2d;
  padding: 10px 15px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #3d3d3d;
}

.terminal-buttons {
  display: flex;
  gap: 8px;
}

.btn {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
  
  &.btn-close {
    background: #ff5f56;
  }
  
  &.btn-minimize {
    background: #ffbd2e;
  }
  
  &.btn-maximize {
    background: #27c93f;
  }
}

.terminal-title {
  color: #b3b3b3;
  font-size: 13px;
  font-family: 'Monaco', 'Menlo', monospace;
}

.terminal-body {
  padding: 20px;
  height: 600px;
  overflow-y: auto;
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #d4d4d4;
  background: #1e1e1e;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
  
  &::-webkit-scrollbar {
    display: none; /* Chrome, Safari, Opera */
    width: 0;
    height: 0;
  }
}

.welcome-message {
  margin-bottom: 20px;
  
  .ascii-art {
    color: #4ade80;
    font-size: 10px;
    line-height: 1.2;
    margin: 0 0 15px 0;
    white-space: pre;
    font-family: 'Monaco', 'Menlo', monospace;
  }
  
  .welcome-text {
    p {
      margin: 8px 0;
      color: #d4d4d4;
      
      &:first-child {
        color: #4ade80;
        font-weight: bold;
        font-size: 1.1rem;
      }
      
      &:last-child {
        margin-top: 16px;
        color: #60a5fa;
        font-weight: 600;
      }
    }
  }
  
  .commands-list {
    margin-top: 16px;
    padding-left: 20px;
  }
  
  .command-item {
    margin: 8px 0;
    padding: 8px 12px;
    color: #d4d4d4;
    line-height: 1.8;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    gap: 8px;
    
    &:hover {
      background: rgba(74, 222, 128, 0.1);
      transform: translateX(4px);
      
      .cmd-name {
        color: #4ade80;
        text-shadow: 0 0 8px rgba(74, 222, 128, 0.5);
      }
      
      .cmd-hint {
        opacity: 1;
      }
    }
    
    &:active {
      transform: translateX(2px);
    }
    
    .cmd-name {
      color: #4ade80;
      font-weight: 600;
      margin-right: 4px;
      transition: all 0.2s ease;
    }
    
    .cmd-desc {
      color: #9ca3af;
      flex: 1;
    }
    
    .cmd-hint {
      color: #60a5fa;
      font-size: 0.85rem;
      opacity: 0;
      transition: opacity 0.2s ease;
      white-space: nowrap;
    }
  }
}

.terminal-line {
  margin: 5px 0;
  display: flex;
  align-items: flex-start;
  
  &.command {
    .prompt {
      margin-right: 8px;
    }
  }
  
  &.system {
    color: #4ade80;
  }
  
  &.error {
    color: #f87171;
  }
  
  &.input-line {
    display: flex;
    align-items: center;
    position: relative;
    z-index: 10;
  }
}

.prompt {
  color: #4ade80;
  user-select: none;
  
  .user {
    color: #60a5fa;
  }
  
  .path {
    color: #a78bfa;
  }
  
  .symbol {
    color: #4ade80;
    margin: 0 8px 0 4px;
  }
}

.command-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #d4d4d4;
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  font-size: 14px;
  width: 100%;
  min-width: 0;
  z-index: 10;
  position: relative;
  
  &:focus {
    outline: none;
  }
}

.cursor {
  color: #4ade80;
  margin-left: 2px;
  animation: blink 1s infinite;
  
  &.blink {
    animation: blink 1s infinite;
  }
}

@keyframes blink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0;
  }
}

.content {
  flex: 1;
  word-break: break-word;
}

.help-panel {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(30, 30, 30, 0.95);
  border: 1px solid #4a4a4a;
  border-radius: 8px;
  padding: 20px;
  max-width: 300px;
  backdrop-filter: blur(10px);
  animation: fadeIn 0.3s ease-out;
  
  h3 {
    color: #4ade80;
    margin: 0 0 15px 0;
    font-size: 16px;
  }
  
  ul {
    list-style: none;
    padding: 0;
    margin: 0;
    
    li {
      margin: 10px 0;
      color: #d4d4d4;
      font-size: 13px;
      
      code {
        background: #2d2d2d;
        padding: 2px 6px;
        border-radius: 4px;
        color: #4ade80;
        font-family: 'Monaco', 'Menlo', monospace;
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .terminal-container {
    padding: 10px;
  }
  
  .terminal-body {
    height: 500px;
    padding: 15px;
    font-size: 12px;
  }
  
  .help-panel {
    position: relative;
    top: auto;
    right: auto;
    margin-top: 20px;
    max-width: 100%;
  }
  
  .welcome-message .ascii-art {
    font-size: 8px;
  }
}
</style>

