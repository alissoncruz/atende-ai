import { NavLink } from 'react-router-dom'
import {
  LayoutDashboard,
  Users,
  Calendar,
  MessageSquare,
  BookOpen,
  LogOut,
  Church,
  Receipt,
} from 'lucide-react'
import { cn } from '@/lib/utils'
import { useAuth } from '@/contexts/AuthContext'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/customers', label: 'Clientes', icon: Users },
  { to: '/appointments', label: 'Agendamentos', icon: Calendar },
  { to: '/chat', label: 'Chat', icon: MessageSquare },
  { to: '/knowledge', label: 'Base de Conhecimento', icon: BookOpen },
  { to: '/tithers', label: 'Dizimistas', icon: Church },
  { to: '/tithe-payments', label: 'Pagamentos de Dízimo', icon: Receipt },
]

export function Sidebar() {
  const { user, logout } = useAuth()

  return (
    <aside className="flex h-screen w-60 flex-col border-r bg-card">
      {/* Logo */}
      <div className="flex h-16 items-center px-6">
        <span className="text-xl font-bold text-primary">AtendeAi</span>
      </div>

      <Separator />

      {/* Nav */}
      <nav className="flex flex-1 flex-col gap-1 p-3">
        {NAV_ITEMS.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              cn(
                'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-primary text-primary-foreground'
                  : 'text-muted-foreground hover:bg-accent hover:text-foreground'
              )
            }
          >
            <Icon size={16} />
            {label}
          </NavLink>
        ))}
      </nav>

      <Separator />

      {/* User + logout */}
      <div className="p-4">
        <div className="mb-2 px-1 text-xs text-muted-foreground">{user?.email}</div>
        <Button variant="ghost" size="sm" className="w-full justify-start gap-2" onClick={logout}>
          <LogOut size={14} />
          Sair
        </Button>
      </div>
    </aside>
  )
}
