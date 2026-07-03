import api from './api'
import type { Appointment, AppointmentStatus, ApiResponse, PageResponse } from '@/types'

export const appointmentService = {
  async list(params?: {
    date?: string
    customerId?: string
    page?: number
    size?: number
  }): Promise<PageResponse<Appointment>> {
    const { data } = await api.get<ApiResponse<PageResponse<Appointment>>>('/appointments', {
      params,
    })
    return data.data
  },

  async get(id: string): Promise<Appointment> {
    const { data } = await api.get<ApiResponse<Appointment>>(`/appointments/${id}`)
    return data.data
  },

  async create(
    payload: Omit<Appointment, 'id' | 'customerName' | 'createdAt'>
  ): Promise<Appointment> {
    const { data } = await api.post<ApiResponse<Appointment>>('/appointments', payload)
    return data.data
  },

  async updateStatus(id: string, status: AppointmentStatus): Promise<Appointment> {
    const { data } = await api.put<ApiResponse<Appointment>>(`/appointments/${id}/status`, {
      status,
    })
    return data.data
  },

  async getSchedule(from: string, to: string): Promise<Appointment[]> {
    const { data } = await api.get<ApiResponse<Appointment[]>>(
      `/appointments/schedule?from=${from}&to=${to}`
    )
    return data.data
  },

  async checkAvailability(date: string, service: string): Promise<string[]> {
    const { data } = await api.get<ApiResponse<string[]>>(
      `/appointments/availability?date=${date}&service=${service}`
    )
    return data.data
  },
}
