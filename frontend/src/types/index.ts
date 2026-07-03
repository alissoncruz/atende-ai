// Auth
export interface User {
  id: string
  email: string
  role: 'ADMIN' | 'ATTENDANT' | 'CLIENT'
}

export interface LoginRequest {
  email: string
  password: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  user: User
}

// Customer
export interface Customer {
  id: string
  name: string
  email: string
  phone: string
  address?: string
  notes?: string
  createdAt: string
}

// Appointment
export type AppointmentStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED'

export interface Appointment {
  id: string
  customerId: string
  customerName: string
  serviceType: string
  title: string
  scheduledAt: string
  durationMinutes: number
  status: AppointmentStatus
  notes?: string
  createdAt: string
}

// Chat
export interface Conversation {
  id: string
  customerId: string
  startedAt: string
  endedAt?: string
  summary?: string
}

export interface Message {
  id: string
  conversationId: string
  role: 'user' | 'assistant'
  content: string
  createdAt: string
}

// Knowledge
export interface KnowledgeDocument {
  id: string
  customerId?: string
  title: string
  content: string
  createdAt: string
}

// Church
export interface Church {
  id: string
  name: string
  address?: string
}

// Tither (Dizimista)
export interface Tither {
  id: string
  name: string
  cpf: string
  phone?: string
  email?: string
  birthDate?: string
  zipCode?: string
  street?: string
  number?: string
  neighborhood?: string
  city?: string
  state?: string
  churchId: string
  churchName: string
  startDate?: string
  referenceAmount?: number
  active: boolean
  createdAt: string
}

export interface TitherPayload {
  name: string
  cpf: string
  phone?: string
  email?: string
  birthDate?: string
  zipCode?: string
  street?: string
  number?: string
  neighborhood?: string
  city?: string
  state?: string
  churchId: string
  startDate?: string
  referenceAmount?: number
}

// Tithe Payments (Controle mensal)
export type MonthlyStatus = 'PAGO' | 'PENDENTE'

export interface MonthlyTitherRow {
  titherId: string
  titherName: string
  churchId: string
  churchName: string
  referenceAmount?: number
  status: MonthlyStatus
  paymentId?: string
  amountPaid?: number
  paidAt?: string
}

export interface MonthlySummary {
  totalActive: number
  paidCount: number
  pendingCount: number
  totalAmount: number
}

export interface MonthlyHistoryItem {
  month: string // yyyy-MM
  totalAmount: number
}

export interface MarkPaidPayload {
  titherId: string
  referenceMonth: string // yyyy-MM
  amount?: number
  paidAt?: string
  notes?: string
}

// API Generic
export interface ApiResponse<T> {
  success: boolean
  data: T
  message?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
