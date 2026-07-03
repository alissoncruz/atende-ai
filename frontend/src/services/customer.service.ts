import api from './api'
import type { Customer, ApiResponse, PageResponse } from '@/types'

export const customerService = {
  async list(page = 0, size = 20): Promise<PageResponse<Customer>> {
    const { data } = await api.get<ApiResponse<PageResponse<Customer>>>(
      `/customers?page=${page}&size=${size}`
    )
    return data.data
  },

  async get(id: string): Promise<Customer> {
    const { data } = await api.get<ApiResponse<Customer>>(`/customers/${id}`)
    return data.data
  },

  async create(payload: Omit<Customer, 'id' | 'createdAt'>): Promise<Customer> {
    const { data } = await api.post<ApiResponse<Customer>>('/customers', payload)
    return data.data
  },

  async update(id: string, payload: Partial<Customer>): Promise<Customer> {
    const { data } = await api.put<ApiResponse<Customer>>(`/customers/${id}`, payload)
    return data.data
  },

  async delete(id: string): Promise<void> {
    await api.delete(`/customers/${id}`)
  },
}
