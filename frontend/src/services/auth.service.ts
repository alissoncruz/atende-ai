import api from './api'
import type { LoginRequest, TokenResponse } from '@/types'

export const authService = {
  async login(credentials: LoginRequest): Promise<TokenResponse> {
    const { data } = await api.post<{ data: TokenResponse }>('/auth/login', credentials)
    return data.data
  },

  async refresh(refreshToken: string): Promise<TokenResponse> {
    const { data } = await api.post<{ data: TokenResponse }>('/auth/refresh', { refreshToken })
    return data.data
  },

  async logout(): Promise<void> {
    await api.post('/auth/logout').catch(() => {})
  },
}
