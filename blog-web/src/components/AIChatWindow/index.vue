<template>
  <div>
    <!-- 浮动按钮 -->
    <div 
      v-if="!isOpen"
      class="ai-chat-bubble-button"
      @click="toggleChat"
      title="AI 助手"
    >
      <i class="fas fa-robot"></i>
      <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
    </div>

    <!-- 聊天窗口 -->
    <transition name="chat-window">
      <div v-if="isOpen" class="ai-chat-window">
        <div class="chat-window-header">
          <div class="header-info">
            <div class="ai-avatar">
              <i class="fas fa-robot"></i>
            </div>
            <div class="header-text">
              <h4>AI 助手</h4>
              <span class="status" :class="{ online: isConnected }">
                {{ isConnected ? '在线' : '离线' }}
              </span>
            </div>
          </div>
          <div class="header-actions">
            <el-button 
              type="text" 
              icon="el-icon-minus" 
              @click="minimizeChat"
              title="最小化"
            />
            <el-button 
              type="text" 
              icon="el-icon-close" 
              @click="toggleChat"
              title="关闭"
            />
          </div>
        </div>

        <div class="chat-window-messages" ref="messagesContainer">
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

        <div class="chat-window-input">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="2"
              placeholder="输入消息..."
              @keydown.ctrl.enter="sendMessage"
              @keydown.enter.exact.prevent="sendMessage"
              :disabled="isLoading"
              resize="none"
              class="message-input"
            />
            <div class="input-actions">
              <el-button 
                type="text" 
                icon="el-icon-delete" 
                @click="clearChat"
                title="清空对话"
                size="small"
              />
              <el-button 
                type="primary" 
                :loading="isLoading"
                @click="sendMessage"
                :disabled="!inputMessage.trim()"
                size="small"
              >
                发送
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { chatStreamApi } from '@/api/ai-chat'
import { formatTime } from '@/utils/time'
import { mapState } from 'vuex'

const STORAGE_KEY = 'ai_chat_history'

export default {
  name: 'AIChatWindow',
  data() {
    return {
      isOpen: false,
      isMinimized: false,
      messages: [],
      inputMessage: '',
      isLoading: false,
      isConnected: false,
      currentStreamController: null,
      conversationId: null,
      unreadCount: 0
    }
  },
  computed: {
    ...mapState(['userInfo']),
    userAvatar() {
      if (this.userInfo?.avatar) {
        return this.userInfo.avatar
      }
      // 使用默认头像或占位符
      return 'https://via.placeholder.com/32'
    }
  },
  mounted() {
    this.loadChatHistory()
    // 监听新消息（用于未读计数）
    this.$nextTick(() => {
      this.scrollToBottom()
    })
  },
  methods: {
    /**
     * 切换聊天窗口
     */
    toggleChat() {
      this.isOpen = !this.isOpen
      if (this.isOpen) {
        this.unreadCount = 0
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },

    /**
     * 最小化聊天窗口
     */
    minimizeChat() {
      this.isOpen = false
    },

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

      // 添加 AI 占位消息
      const aiMessageIndex = this.messages.length
      this.addMessage('assistant', '')
      this.isLoading = true
      this.isConnected = true

      let fullResponse = ''

      try {
        this.currentStreamController = await chatStreamApi(
          userMessage,
          // onMessage 回调
          (data) => {
            // 保存 conversation_id（所有事件都可能包含）
            if (data.conversation_id) {
              this.conversationId = data.conversation_id
            }
            
            // 根据事件类型处理
            switch (data.event) {
              case 'workflow_started':
                console.log('📋 工作流已启动')
                break
                
              case 'node_started':
                console.log('▶️ 节点已启动:', data.data?.node_type || data.data?.title || 'unknown')
                break
                
              case 'node_finished':
                console.log('✅ 节点已完成:', data.data?.node_type || data.data?.title || 'unknown')
                break
                
              case 'message':
                // message 事件包含 answer 字段，这是实际的回复内容片段
                // 需要累积多个 message 事件的 answer 来构建完整回复
                console.log('💬 message 事件完整数据:', JSON.stringify(data, null, 2))
                
                if (data.answer !== undefined && data.answer !== null) {
                  // 确保 answer 是字符串
                  let answerText = String(data.answer)
                  
                  // 调试：输出原始数据
                  console.log('💬 message 事件 - 原始 answer:', {
                    value: data.answer,
                    type: typeof data.answer,
                    stringValue: answerText,
                    length: answerText.length,
                    charCodes: answerText.split('').map(c => c.charCodeAt(0))
                  })
                  
                  // 直接添加内容，不做过滤（让用户看到实际返回的内容）
                  if (answerText) {
                    fullResponse += answerText
                    this.updateMessage(aiMessageIndex, fullResponse)
                    this.scrollToBottom()
                    console.log('✓ 更新消息，总长度:', fullResponse.length, '新增:', answerText.length, '内容:', answerText)
                  }
                } else {
                  console.log('⚠️ message 事件但没有 answer 字段，完整数据:', JSON.stringify(data, null, 2))
                }
                break
                
              case 'message_end':
                console.log('🏁 消息已结束，最终长度:', fullResponse.length)
                this.isLoading = false
                this.isConnected = false
                // 确保最终消息已保存
                if (fullResponse) {
                  this.updateMessage(aiMessageIndex, fullResponse)
                  this.saveChatHistory()
                }
                break
                
              case 'workflow_finished':
                console.log('🎉 工作流已完成')
                this.isLoading = false
                this.isConnected = false
                // 确保最终消息已保存
                if (fullResponse) {
                  this.updateMessage(aiMessageIndex, fullResponse)
                  this.saveChatHistory()
                }
                break
                
              case 'error':
                console.error('❌ 错误事件:', data)
                this.isLoading = false
                this.isConnected = false
                this.$message.error(data.message || data.error || 'AI 响应出错')
                // 如果 AI 消息为空，移除它
                if (!fullResponse) {
                  this.messages.splice(aiMessageIndex, 1)
                }
                break
                
              default:
                // 未知事件类型，尝试提取内容（兼容性处理）
                if (data.answer !== undefined && data.answer !== null) {
                  const answerText = String(data.answer)
                  if (answerText) {
                    fullResponse += answerText
                    this.updateMessage(aiMessageIndex, fullResponse)
                    this.scrollToBottom()
                    console.log('💬 从未知事件提取内容，总长度:', fullResponse.length)
                  }
                } else {
                  console.log('ℹ️ 未知事件类型:', data.event || 'no event', data)
                }
                break
            }
          },
          // onError 回调
          (error) => {
            console.error('流式请求错误:', error)
            this.isLoading = false
            this.isConnected = false
            this.$message.error('请求失败，请稍后重试')
            if (!fullResponse) {
              this.messages.splice(aiMessageIndex, 1)
            }
          },
          // onComplete 回调
          () => {
            this.isLoading = false
            this.isConnected = false
            this.saveChatHistory()
            // 如果窗口关闭，增加未读计数
            if (!this.isOpen) {
              this.unreadCount++
            }
          }
        )
      } catch (error) {
        console.error('发送消息失败:', error)
        this.isLoading = false
        this.isConnected = false
        this.$message.error('发送失败，请稍后重试')
        if (!fullResponse) {
          this.messages.splice(aiMessageIndex, 1)
        }
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
     * 格式化消息内容
     */
    formatMessage(content) {
      if (!content) return ''
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
     * 保存对话历史
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
     * 加载对话历史
     */
    loadChatHistory() {
      try {
        const historyStr = localStorage.getItem(STORAGE_KEY)
        if (historyStr) {
          const history = JSON.parse(historyStr)
          const oneDayAgo = Date.now() - 24 * 60 * 60 * 1000
          if (history.timestamp && history.timestamp > oneDayAgo) {
            this.messages = history.messages || []
            this.conversationId = history.conversationId || null
          } else {
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
// 浮动按钮
.ai-chat-bubble-button {
  position: fixed;
  bottom: 24px;
  right: 24px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 1000;
  transition: all 0.3s ease;
  color: white;
  font-size: 24px;

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.6);
  }

  .unread-badge {
    position: absolute;
    top: -4px;
    right: -4px;
    background: #f56c6c;
    color: white;
    border-radius: 10px;
    padding: 2px 6px;
    font-size: 12px;
    min-width: 18px;
    text-align: center;
    line-height: 1.2;
  }
}

// 聊天窗口
.ai-chat-window {
  position: fixed;
  bottom: 24px;
  right: 24px;
  width: 380px;
  height: 600px;
  background: var(--card-bg);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
  z-index: 1001;
  overflow: hidden;

  @media (max-width: 768px) {
    width: calc(100vw - 32px);
    height: calc(100vh - 100px);
    bottom: 16px;
    right: 16px;
  }
}

.chat-window-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-color);
  flex-shrink: 0;

  .header-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .ai-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 18px;
    }

    .header-text {
      h4 {
        margin: 0;
        font-size: 16px;
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
    gap: 4px;
  }
}

.chat-window-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: var(--bg-color);

  &::-webkit-scrollbar {
    width: 4px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: var(--border-color);
    border-radius: 2px;
  }

  .message-item {
    display: flex;
    gap: 8px;
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
      width: 32px;
      height: 32px;
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
        font-size: 16px;
        color: var(--primary-color);
      }
    }

    .message-content {
      max-width: 75%;
      padding: 8px 12px;
      word-wrap: break-word;

      .message-text {
        line-height: 1.5;
        color: var(--text-primary);
        font-size: 14px;
      }

      .message-time {
        font-size: 11px;
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
        width: 6px;
        height: 6px;
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

.chat-window-input {
  padding: 12px;
  background: var(--card-bg);
  border-top: 1px solid var(--border-color);
  flex-shrink: 0;

  .input-wrapper {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .message-input {
      ::v-deep .el-textarea__inner {
        border-radius: 8px;
        resize: none;
        font-size: 14px;
      }
    }

    .input-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}

// 动画
.chat-window-enter-active,
.chat-window-leave-active {
  transition: all 0.3s ease;
}

.chat-window-enter,
.chat-window-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.9);
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
    transform: translateY(-8px);
    opacity: 1;
  }
}
</style>

