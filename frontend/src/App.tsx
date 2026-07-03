import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from '@/contexts/AuthContext'
import { AppLayout } from '@/components/layout/AppLayout'
import { LoginPage } from '@/pages/LoginPage'
import { DashboardPage } from '@/pages/DashboardPage'
import { CustomersPage } from '@/pages/CustomersPage'
import { AppointmentsPage } from '@/pages/AppointmentsPage'
import { ChatPage } from '@/pages/ChatPage'
import { KnowledgePage } from '@/pages/KnowledgePage'
import { TithersPage } from '@/pages/TithersPage'
import { TitherFormPage } from '@/pages/TitherFormPage'
import { TithePaymentsPage } from '@/pages/TithePaymentsPage'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Público */}
          <Route path="/login" element={<LoginPage />} />

          {/* Privado — todas as rotas dentro do AppLayout */}
          <Route element={<AppLayout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/customers" element={<CustomersPage />} />
            <Route path="/appointments" element={<AppointmentsPage />} />
            <Route path="/chat" element={<ChatPage />} />
            <Route path="/knowledge" element={<KnowledgePage />} />
            <Route path="/tithers" element={<TithersPage />} />
            <Route path="/tithers/new" element={<TitherFormPage />} />
            <Route path="/tithers/:id/edit" element={<TitherFormPage />} />
            <Route path="/tithe-payments" element={<TithePaymentsPage />} />
          </Route>

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
