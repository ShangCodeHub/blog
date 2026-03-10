<template>
  <div class="ai-chat-container">
    <div class="chat-header">
      <div class="header-info">
        <div class="ai-avatar">
          <i class="fas fa-robot"></i>
        </div>
        <div class="header-text">
          <h3>AI 助手</h3>
          <span class="status" :class="{ online: isConnected }">
            {{ isConnected ? '在线' : '离线' }}
          </span>
        </div>
      </div>
      <div class="header-actions">
        <el-button 
          type="text" 
          icon="el-icon-delete" 
          @click="clearChat"
          title="清空对话"
        >
          清空
        </el-button>
      </div>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <div 
        v-for="(msg, index) in messages" 
        :key="index"
        class="message-item"
        :class="{ 'user-message': msg.role === 'user', 'ai-message': msg.role === 'assistant' }"
      >
        <div class="message-avatar">
          <img v-if="msg.role === 'user'" :src="userAvatar" />
          <i v-else class="fas fa-robot"></i>
        </div>
        <div class="message-content">
          <div class="message-text" v-html="formatMessage(msg.content)"></div>
          <!-- CITATIONS 引用资源 -->
          <div v-if="msg.role === 'assistant' && msg.citations && msg.citations.length > 0" class="citations-section">
            <div class="citations-header">
              <span>CITATIONS</span>
              <div class="citations-divider"></div>
            </div>
            <div class="citations-list">
              <div 
                v-for="(citation, idx) in msg.citations" 
                :key="idx"
                class="citation-item"
                :title="citation.document_name || citation.content"
              >
                <i class="fas fa-file-alt citation-icon"></i>
                <span class="citation-name">{{ citation.document_name || citation.content || '未命名文档' }}</span>
              </div>
            </div>
          </div>
          <!-- 调试信息（临时） -->
          <div v-if="msg.role === 'assistant'" style="font-size: 10px; color: #999; margin-top: 4px;">
            Debug: citations={{ msg.citations ? JSON.stringify(msg.citations) : 'null' }}
          </div>
          <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
        </div>
      </div>
      
      <div v-if="isLoading" class="message-item ai-message">
        <div class="message-avatar">
          <i class="fas fa-robot"></i>
        </div>
        <div class="message-content">
          <div class="typing-indicator">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-input-container">
      <div class="input-wrapper">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入消息..."
          @keydown.ctrl.enter="sendMessage"
          @keydown.enter.exact.prevent="sendMessage"
          :disabled="isLoading"
          resize="none"
          class="message-input"
        />
        <div class="input-actions">
          <el-button 
            type="primary" 
            :loading="isLoading"
            @click="sendMessage"
            :disabled="!inputMessage.trim()"
          >
            <i class="fas fa-paper-plane"></i>
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { chatStreamApi } from '@/api/ai-chat'
import { formatTime } from '@/utils/time'
import { mapState } from 'vuex'

const STORAGE_KEY = 'ai_chat_history'

export default {
  name: 'AIChat',
  data() {
    return {
      messages: [],
      inputMessage: '',
      isLoading: false,
      isConnected: false,
      currentStreamController: null,
      conversationId: null
    }
  },
  computed: {
    ...mapState(['userInfo']),
    userAvatar() {
      return this.userInfo?.avatar || require('@/assets/images/default-avatar.png')
    }
  },
  mounted() {
    this.loadChatHistory()
    this.scrollToBottom()
  },
  methods: {
    /**
     * 发送消息
     */
    async sendMessage() {
      if (!this.inputMessage.trim() || this.isLoading) {
        return
      }

      const userMessage = this.inputMessage.trim()
      this.inputMessage = ''

      // 添加用户消息
      this.addMessage('user', userMessage)

      // 不立即添加 AI 消息，等收到第一条数据时再添加
      let aiMessageIndex = -1 // -1 表示还没有创建 AI 消息
      this.isLoading = true
      this.isConnected = true

      let fullResponse = ''
      let currentEvent = null

      try {
        this.currentStreamController = await chatStreamApi(
          userMessage,
          // onMessage 回调
          (data) => {
            currentEvent = data.event
            
            // 处理不同类型的消息事件
            if (data.event === 'message') {
              // 如果还没有创建 AI 消息，现在创建它
              if (aiMessageIndex === -1) {
                aiMessageIndex = this.messages.length
                this.addMessage('assistant', '')
              }
              
              // 累积消息内容，Dify 返回的数据可能包含 answer 字段或直接是文本
              if (data.answer) {
                fullResponse += data.answer
                this.updateMessage(aiMessageIndex, fullResponse)
                this.scrollToBottom()
              } else if (data.text) {
                // 如果没有 answer 字段，尝试 text 字段
                fullResponse += data.text
                this.updateMessage(aiMessageIndex, fullResponse)
                this.scrollToBottom()
              } else if (data.data) {
                // 如果数据在 data 字段中
                fullResponse += data.data
                this.updateMessage(aiMessageIndex, fullResponse)
                this.scrollToBottom()
              }
              
              // 如果 message 事件中也包含 metadata，提前提取引用资源
              if (aiMessageIndex !== -1 && data.metadata && data.metadata.retriever_resources && Array.isArray(data.metadata.retriever_resources)) {
                const citations = data.metadata.retriever_resources.map(resource => ({
                  document_name: resource.document_name,
                  content: resource.content,
                  score: resource.score,
                  position: resource.position
                }))
                console.log('✅ 从 message 事件提取到引用资源:', citations)
                this.updateMessageCitations(aiMessageIndex, citations)
              }
              
              // 保存 conversation_id
              if (data.conversation_id) {
                this.conversationId = data.conversation_id
              }
            } else if (data.event === 'message_end') {
              // 消息结束，提取引用资源
              console.log('📋 message_end 事件，完整数据:', JSON.stringify(data, null, 2))
              console.log('📋 aiMessageIndex:', aiMessageIndex, 'fullResponse:', fullResponse)
              
              this.isLoading = false
              this.isConnected = false
              if (data.conversation_id) {
                this.conversationId = data.conversation_id
              }
              
              // 提取 retriever_resources 作为引用
              if (aiMessageIndex !== -1) {
                console.log('📋 找到 AI 消息索引:', aiMessageIndex)
                console.log('📋 metadata:', data.metadata)
                console.log('📋 retriever_resources:', data.metadata?.retriever_resources)
                
                if (data.metadata && data.metadata.retriever_resources && Array.isArray(data.metadata.retriever_resources)) {
                  const citations = data.metadata.retriever_resources.map(resource => ({
                    document_name: resource.document_name,
                    content: resource.content,
                    score: resource.score,
                    position: resource.position
                  }))
                  console.log('✅ 提取到引用资源:', citations)
                  this.updateMessageCitations(aiMessageIndex, citations)
                  console.log('✅ 更新后的消息:', this.messages[aiMessageIndex])
                } else {
                  console.warn('⚠️ 没有找到 retriever_resources 或格式不正确')
                }
              } else {
                console.warn('⚠️ aiMessageIndex 为 -1，无法更新引用资源')
              }
            } else if (data.event === 'workflow_finished') {
              // 工作流完成，答案在 data.data.outputs.answer 中
              console.log('🎉 工作流已完成，完整数据:', JSON.stringify(data, null, 2))
              
              // 如果还没有创建 AI 消息，现在创建它
              if (aiMessageIndex === -1) {
                aiMessageIndex = this.messages.length
                this.addMessage('assistant', '')
              }
              
              // 提取答案：data.data.outputs.answer
              let answerText = ''
              if (data.data && data.data.outputs && data.data.outputs.answer) {
                answerText = String(data.data.outputs.answer)
                console.log('✅ 从 workflow_finished 提取答案:', {
                  answer: answerText,
                  length: answerText.length,
                  charCodes: answerText.split('').map(c => c.charCodeAt(0))
                })
                
                if (answerText) {
                  fullResponse = answerText // 工作流完成时，使用完整答案
                  this.updateMessage(aiMessageIndex, fullResponse)
                  this.scrollToBottom()
                }
              } else {
                console.warn('⚠️ workflow_finished 事件但没有找到答案，完整数据:', JSON.stringify(data, null, 2))
              }
              
              // 提取 retriever_resources 作为引用（从 metadata 中）
              if (aiMessageIndex !== -1 && data.metadata && data.metadata.retriever_resources) {
                const citations = data.metadata.retriever_resources.map(resource => ({
                  document_name: resource.document_name,
                  content: resource.content,
                  score: resource.score,
                  position: resource.position
                }))
                this.updateMessageCitations(aiMessageIndex, citations)
                console.log('✅ 从 workflow_finished 提取到引用资源:', citations)
              }
              
              this.isLoading = false
              this.isConnected = false
              if (data.conversation_id) {
                this.conversationId = data.conversation_id
              }
            } else if (data.event === 'error') {
              // 错误处理
              this.isLoading = false
              this.isConnected = false
              const errorMsg = data.message || 'AI 响应出错'
              this.$message.error(errorMsg)
              // 如果 AI 消息已创建但为空，显示错误信息
              if (aiMessageIndex !== -1 && !fullResponse) {
                this.updateMessage(aiMessageIndex, '❌ ' + errorMsg)
              }
            }
            
            // 调试日志
            console.log('收到流式数据:', data)
          },
          // onError 回调
          (error) => {
            console.error('流式请求错误:', error)
            this.isLoading = false
            this.isConnected = false
            
            // 提取错误信息
            let errorMsg = '请求失败，请稍后重试'
            if (error && error.message) {
              if (error.message.includes('Connection timed out') || error.message.includes('连接中断')) {
                errorMsg = '连接超时，请检查网络连接后重试'
              } else if (error.message.includes('Failed to fetch')) {
                errorMsg = '网络连接失败，请检查网络后重试'
              } else {
                errorMsg = error.message
              }
            }
            
            this.$message.error(errorMsg)
            
            // 如果 AI 消息已创建但为空，显示错误信息
            if (aiMessageIndex !== -1 && !fullResponse) {
              this.updateMessage(aiMessageIndex, '❌ ' + errorMsg)
            }
          },
          // onComplete 回调
          () => {
            this.isLoading = false
            this.isConnected = false
            this.saveChatHistory()
          }
        )
      } catch (error) {
        console.error('发送消息失败:', error)
        this.isLoading = false
        this.isConnected = false
        this.$message.error('发送失败，请稍后重试')
        // 如果 AI 消息为空或未创建，不需要移除
      }
    },

    /**
     * 添加消息
     */
    addMessage(role, content) {
      this.messages.push({
        role,
        content,
        timestamp: Date.now()
      })
      this.scrollToBottom()
      this.saveChatHistory()
    },

    /**
     * 更新消息内容
     */
    updateMessage(index, content) {
      if (this.messages[index]) {
        this.messages[index].content = content
        this.messages[index].timestamp = Date.now()
      }
    },
    
    /**
     * 更新消息的引用资源
     */
    updateMessageCitations(index, citations) {
      if (this.messages[index]) {
        console.log('📝 更新消息引用资源，index:', index, 'citations:', citations)
        this.$set(this.messages[index], 'citations', citations)
        this.messages[index].timestamp = Date.now()
        console.log('📝 更新后的消息对象:', this.messages[index])
        // 强制更新视图
        this.$forceUpdate()
      } else {
        console.warn('⚠️ 消息索引不存在:', index, '消息总数:', this.messages.length)
      }
    },

    /**
     * 格式化消息内容（支持 Markdown）
     */
    formatMessage(content) {
      if (!content) return ''
      // 简单的换行处理
      return content.replace(/\n/g, '<br>')
    },

    /**
     * 格式化时间
     */
    formatTime(timestamp) {
      return formatTime(timestamp)
    },

    /**
     * 滚动到底部
     */
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    /**
     * 清空对话
     */
    clearChat() {
      this.$confirm('确定要清空所有对话记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.messages = []
        this.conversationId = null
        this.saveChatHistory()
        this.$message.success('已清空对话记录')
      }).catch(() => {})
    },

    /**
     * 保存对话历史到 localStorage
     */
    saveChatHistory() {
      try {
        const history = {
          messages: this.messages,
          conversationId: this.conversationId,
          timestamp: Date.now()
        }
        localStorage.setItem(STORAGE_KEY, JSON.stringify(history))
      } catch (error) {
        console.error('保存对话历史失败:', error)
      }
    },

    /**
     * 从 localStorage 加载对话历史
     */
    loadChatHistory() {
      try {
        const historyStr = localStorage.getItem(STORAGE_KEY)
        if (historyStr) {
          const history = JSON.parse(historyStr)
          // 只加载最近24小时的对话
          const oneDayAgo = Date.now() - 24 * 60 * 60 * 1000
          if (history.timestamp && history.timestamp > oneDayAgo) {
            this.messages = history.messages || []
            this.conversationId = history.conversationId || null
          } else {
            // 超过24小时，清空历史
            localStorage.removeItem(STORAGE_KEY)
          }
        }
      } catch (error) {
        console.error('加载对话历史失败:', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 80px);
  max-width: 1200px;
  margin: 0 auto;
  background: var(--bg-color);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-color);

  .header-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .ai-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 20px;
    }

    .header-text {
      h3 {
        margin: 0;
        font-size: 18px;
        color: var(--text-primary);
      }

      .status {
        font-size: 12px;
        color: var(--text-secondary);

        &.online {
          color: #67c23a;
        }
      }
    }
  }

  .header-actions {
    display: flex;
    gap: 8px;
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: var(--bg-color);

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: var(--border-color);
    border-radius: 3px;
  }

  .message-item {
    display: flex;
    gap: 12px;
    animation: fadeIn 0.3s ease-in;

    &.user-message {
      flex-direction: row-reverse;

      .message-content {
        background: var(--primary-color);
        color: white;
        border-radius: 12px 12px 4px 12px;
      }
    }

    &.ai-message {
      .message-content {
        background: var(--card-bg);
        border: 1px solid var(--border-color);
        border-radius: 12px 12px 12px 4px;
      }
    }

    .message-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      flex-shrink: 0;
      overflow: hidden;
      background: var(--card-bg);
      display: flex;
      align-items: center;
      justify-content: center;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      i {
        font-size: 18px;
        color: var(--primary-color);
      }
    }

    .message-content {
      max-width: 70%;
      padding: 12px 16px;
      word-wrap: break-word;

      .message-text {
        line-height: 1.6;
        color: var(--text-primary);
      }

      .citations-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
  
  .citations-header {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    
    span {
      font-size: 12px;
      font-weight: 600;
      color: var(--text-secondary);
      text-transform: uppercase;
      letter-spacing: 0.5px;
      margin-right: 12px;
    }
    
    .citations-divider {
      flex: 1;
      height: 1px;
      background: var(--border-color);
    }
  }
  
  .citations-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    
    .citation-item {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.2s;
      font-size: 13px;
      
      &:hover {
        background: var(--hover-bg);
        border-color: var(--primary-color);
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }
      
      .citation-icon {
        color: var(--text-secondary);
        font-size: 14px;
      }
      
      .citation-name {
        color: var(--text-primary);
        max-width: 200px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

.message-time {
        font-size: 12px;
        color: var(--text-secondary);
        margin-top: 4px;
        opacity: 0.7;
      }
    }

    .typing-indicator {
      display: flex;
      gap: 4px;
      padding: 8px 0;

      span {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: var(--text-secondary);
        animation: typing 1.4s infinite;

        &:nth-child(2) {
          animation-delay: 0.2s;
        }

        &:nth-child(3) {
          animation-delay: 0.4s;
        }
      }
    }
  }
}

.chat-input-container {
  padding: 16px 24px;
  background: var(--card-bg);
  border-top: 1px solid var(--border-color);

  .input-wrapper {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .message-input {
      ::v-deep .el-textarea__inner {
        border-radius: 8px;
        resize: none;
      }
    }

    .input-actions {
      display: flex;
      justify-content: flex-end;
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.7;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .ai-chat-container {
    height: calc(100vh - 60px);
  }

  .chat-messages {
    padding: 16px;

    .message-item {
      .message-content {
        max-width: 85%;
      }
    }
  }

  .chat-input-container {
    padding: 12px 16px;
  }
}
</style>

