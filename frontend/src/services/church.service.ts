import api from './api'
import type { Church, ApiResponse } from '@/types'

export const churchService = {
  async list(): Promise<Church[]> {
    const { data } = await api.get<ApiResponse<Church[]>>('/churches')
    return data.data
  },

  async create(payload: Omit<Church, 'id'>): Promise<Church> {
    const { data } = await api.post<ApiResponse<Church>>('/churches', payload)
    return data.data
  },

  async update(id: string, payload: Omit<Church, 'id'>): Promise<Church> {
    const { data } = await api.put<ApiResponse<Church>>(`/churches/${id}`, payload)
    return data.data
  },

  async delete(id: string): Promise<void> {
    await api.delete(`/churches/${id}`)
  },
}
