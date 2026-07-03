import api from './api'
import type { Tither, TitherPayload, ApiResponse, PageResponse } from '@/types'

export const titherService = {
  async list(params: { q?: string; churchId?: string; page?: number; size?: number } = {}): Promise<PageResponse<Tither>> {
    const { q, churchId, page = 0, size = 20 } = params
    const search = new URLSearchParams()
    if (q) search.set('q', q)
    if (churchId) search.set('churchId', churchId)
    search.set('page', String(page))
    search.set('size', String(size))
    const { data } = await api.get<ApiResponse<PageResponse<Tither>>>(`/tithers?${search.toString()}`)
    return data.data
  },

  async get(id: string): Promise<Tither> {
    const { data } = await api.get<ApiResponse<Tither>>(`/tithers/${id}`)
    return data.data
  },

  async create(payload: TitherPayload): Promise<Tither> {
    const { data } = await api.post<ApiResponse<Tither>>('/tithers', payload)
    return data.data
  },

  async update(id: string, payload: TitherPayload): Promise<Tither> {
    const { data } = await api.put<ApiResponse<Tither>>(`/tithers/${id}`, payload)
    return data.data
  },

  async delete(id: string): Promise<void> {
    await api.delete(`/tithers/${id}`)
  },
}
