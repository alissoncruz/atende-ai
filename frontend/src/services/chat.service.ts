import api from './api'
import type { Conversation, Message, ApiResponse } from '@/types'

export const chatService = {
  async startConversation(customerId: string): Promise<Conversation> {
    const { data } = await api.post<ApiResponse<Conversation>>('/chat/start', { customerId })
    return data.data
  },

  async sendMessage(conversationId: string, content: string): Promise<Message> {
    const { data } = await api.post<ApiResponse<Message>>(`/chat/${conversationId}/message`, {
      content,
    })
    return data.data
  },

  async getMessages(conversationId: string): Promise<Message[]> {
    const { data } = await api.get<ApiResponse<Message[]>>(`/chat/${conversationId}/messages`)
    return data.data
  },

  async endConversation(conversationId: string): Promise<void> {
    await api.post(`/chat/${conversationId}/end`)
  },
}
