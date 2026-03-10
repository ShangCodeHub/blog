import { getToken } from '@/utils/cookie'

/**
 * 使用 fetch 处理流式数据（SSE 格式）
 * @param {string} message 用户消息
 * @param {Function} onMessage 消息回调函数
 * @param {Function} onError 错误回调函数
 * @param {Function} onComplete 完成回调函数
 * @returns {Promise} 返回 AbortController，用于取消请求
 */
export async function chatStreamApi(message, onMessage, onError, onComplete) {
  const abortController = new AbortController()
  
  try {
    const token = getToken() || ''
    const baseURL = import.meta.env.VITE_APP_BASE_API || ''
    const url = `${baseURL}/api/dify/chat/stream`
    
    console.log('发送流式请求:', { url, message })
    
    // 检查消息中的中文字符（用于调试）
    if (message) {
      const hasChinese = /[\u4e00-\u9fff]/.test(message)
      if (hasChinese) {
        console.log('✅ 发送的消息包含中文字符，长度=', message.length, '内容=', message)
        const chineseMatches = message.match(/[\u4e00-\u9fff]+/g)
        if (chineseMatches) {
          console.log('✅ 消息中的中文字符片段:', chineseMatches.join(' | '))
        }
      } else {
        console.warn('⚠️ 发送的消息不包含中文字符，可能有问题')
      }
    }
    
    // 构建请求体
    const requestBody = JSON.stringify({ message })
    console.log('📤 请求体 JSON 字符串:', requestBody)
    console.log('📤 请求体长度:', requestBody.length)
    
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json;charset=UTF-8',
        'Authorization': token
      },
      body: requestBody,
      signal: abortController.signal
    })

    if (!response.ok) {
      const errorText = await response.text()
      console.error('请求失败:', response.status, errorText)
      throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`)
    }

    console.log('开始读取流式数据...')
    console.log('响应 Content-Type:', response.headers.get('Content-Type'))
    console.log('响应编码信息:', response.headers.get('Content-Encoding'))
    
    const reader = response.body.getReader()
    // 明确指定 UTF-8 编码，避免乱码
    const decoder = new TextDecoder('utf-8')
    let buffer = ''
    
    // 调试：打印解码器信息
    console.log('✅ 使用 TextDecoder，编码:', decoder.encoding)

    try {
      while (true) {
        const { done, value } = await reader.read()
        
        if (done) {
          if (onComplete) {
            onComplete()
          }
          break
        }

        // 解码字节流为 UTF-8 字符串
        const decodedChunk = decoder.decode(value, { stream: true })
        
        // 调试：打印解码后的原始数据（前200字符）
        if (decodedChunk.length > 0) {
          console.log('📥 解码后的数据块 (前200字符):', decodedChunk.substring(0, 200))
          // 检查是否包含中文字符
          const hasChinese = /[\u4e00-\u9fff]/.test(decodedChunk)
          if (hasChinese) {
            console.log('✅ 数据块中包含中文字符，解码正常')
            const chineseMatches = decodedChunk.match(/[\u4e00-\u9fff]+/g)
            if (chineseMatches) {
              console.log('✅ 数据块中的中文字符片段:', chineseMatches.slice(0, 5).join(' | '))
            }
          }
        }
        
        buffer += decodedChunk
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          const trimmedLine = line.trim()
          if (!trimmedLine) continue
          
          // 处理 SSE 事件类型
          let eventType = null
          let jsonStr = ''
          
          // 检查是否有 event: 前缀
          if (trimmedLine.startsWith('event:')) {
            eventType = trimmedLine.substring(6).trim()
            continue
          }
          
          // 处理 SSE 格式：data: {...} 或 data:data: {...}（重复前缀的情况）
          if (trimmedLine.startsWith('data:')) {
            // 移除所有 data: 前缀（处理重复的情况）
            jsonStr = trimmedLine
            // 循环移除所有开头的 data: 前缀
            while (jsonStr.startsWith('data:')) {
              jsonStr = jsonStr.substring(5) // 移除 'data:' (5个字符)
              jsonStr = jsonStr.trim() // 移除可能的空格
            }
          } else if (trimmedLine.startsWith('{')) {
            // 如果直接是 JSON 格式
            jsonStr = trimmedLine
          } else {
            // 跳过非数据行（如空行、注释等）
            continue
          }
          
          // 确保提取的是有效的 JSON 字符串
          if (jsonStr && jsonStr.trim().startsWith('{')) {
            try {
              // 打印原始 JSON 字符串（用于调试编码问题）
              console.log('📥 原始 JSON 字符串 (完整):', jsonStr)
              console.log('📥 原始 JSON 字符串 (前200字符):', jsonStr.substring(0, 200))
              
              const data = JSON.parse(jsonStr.trim())
              
              // 如果数据中有 event 字段，使用它；否则使用解析到的事件类型
              if (!data.event && eventType) {
                data.event = eventType
              }
              
              // 打印解析后的数据（用于调试）
              console.log('✅ 解析后的数据:', JSON.stringify(data, null, 2))
              
              // 检查是否有中文字符（用于调试）
              const dataStr = JSON.stringify(data)
              const hasChinese = /[\u4e00-\u9fff]/.test(dataStr)
              if (hasChinese) {
                console.log('✅ 检测到中文字符，JSON 解析正常')
                // 提取所有中文字符片段
                const chineseMatches = dataStr.match(/[\u4e00-\u9fff]+/g)
                if (chineseMatches) {
                  console.log('✅ 提取的中文字符片段:', chineseMatches.join(' | '))
                }
              }
              
              if (onMessage) {
                onMessage(data)
              }
            } catch (e) {
              console.error('❌ 解析消息失败:', e)
              console.error('原始行 (完整):', trimmedLine)
              console.error('提取的JSON字符串 (完整):', jsonStr)
              console.error('JSON字符串长度:', jsonStr.length)
              // 打印前500字符用于调试
              console.error('提取的JSON字符串 (前500字符):', jsonStr.substring(0, 500))
            }
          } else if (jsonStr) {
            console.log('跳过非JSON数据:', jsonStr.substring(0, 50))
          }
        }
      }
    } catch (readError) {
      // 捕获读取流时的错误（如连接中断）
      console.error('读取流式数据时发生错误:', readError)
      if (onError) {
        onError(new Error('连接中断，请检查网络连接: ' + readError.message))
      }
      throw readError
    }
    
    return abortController
  } catch (error) {
    if (error.name === 'AbortError') {
      console.log('请求已取消')
      return abortController
    }
    console.error('流式请求失败:', error)
    if (onError) {
      onError(error)
    }
    throw error
  }
}

// 为了兼容性，保留别名
export const chatStreamFetchApi = chatStreamApi

