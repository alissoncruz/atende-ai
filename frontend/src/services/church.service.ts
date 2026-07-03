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
}
