import api from './api'
import type {
  ApiResponse,
  MarkPaidPayload,
  MonthlyHistoryItem,
  MonthlySummary,
  MonthlyTitherRow,
} from '@/types'

export const tithePaymentService = {
  async monthly(params: { month: string; churchId?: string; q?: string; status?: string }): Promise<MonthlyTitherRow[]> {
    const search = new URLSearchParams({ month: params.month })
    if (params.churchId) search.set('churchId', params.churchId)
    if (params.q) search.set('q', params.q)
    if (params.status) search.set('status', params.status)
    const { data } = await api.get<ApiResponse<MonthlyTitherRow[]>>(`/tithe-payments/monthly?${search.toString()}`)
    return data.data
  },

  async summary(month: string, churchId?: string): Promise<MonthlySummary> {
    const search = new URLSearchParams({ month })
    if (churchId) search.set('churchId', churchId)
    const { data } = await api.get<ApiResponse<MonthlySummary>>(`/tithe-payments/monthly/summary?${search.toString()}`)
    return data.data
  },

  async history(months = 6, churchId?: string): Promise<MonthlyHistoryItem[]> {
    const search = new URLSearchParams({ months: String(months) })
    if (churchId) search.set('churchId', churchId)
    const { data } = await api.get<ApiResponse<MonthlyHistoryItem[]>>(`/tithe-payments/history?${search.toString()}`)
    return data.data
  },

  async markPaid(payload: MarkPaidPayload): Promise<MonthlyTitherRow> {
    const { data } = await api.post<ApiResponse<MonthlyTitherRow>>('/tithe-payments', payload)
    return data.data
  },

  async unmark(titherId: string, month: string): Promise<void> {
    await api.delete(`/tithe-payments?titherId=${titherId}&month=${month}`)
  },
}
