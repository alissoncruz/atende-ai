import { NavLink, useLocation, useNavigate } from 'react-router-dom'
import {
  LayoutDashboard,
  Users,
  Calendar,
  MessageSquare,
  BookOpen,
  LogOut,
  Church,
  Landmark,
  Receipt,
} from 'lucide-react'
import { cn } from '@/lib/utils'
import { useAuth } from '@/contexts/AuthContext'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'

const MODULES = {
  atendimento: {
    label: 'Atendimento',
    items: [
      { to: '/atendimento/dashboard', label: 'Dashboard', icon: LayoutDashboard },
      { to: '/atendimento/customers', label: 'Clientes', icon: Users },
      { to: '/atendimento/appointments', label: 'Agendamentos', icon: Calendar },
      { to: '/atendimento/chat', label: 'Chat', icon: MessageSquare },
      { to: '/atendimento/knowledge', label: 'Base de Conhecimento', icon: BookOpen },
    ],
  },
  dizimo: {
    label: 'Dízimo',
    items: [
      { to: '/dizimo/tithers', label: 'Dizimistas', icon: Church },
      { to: '/dizimo/tithe-payments', label: 'Pagamentos de Dízimo', icon: Receipt },
      { to: '/dizimo/churches', label: 'Igrejas', icon: Landmark },
    ],
  },
} as const

type ModuleKey = keyof typeof MODULES

export function Sidebar() {
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()

  const activeModule: ModuleKey = location.pathname.startsWith('/dizimo') ? 'dizimo' : 'atendimento'

  return (
    <aside className="flex h-screen w-60 flex-col border-r bg-card">
      {/* Logo */}
      <div className="flex h-16 items-center px-6">
        <span className="text-xl font-bold text-primary">AtendeAi</span>
      </div>

      <Separator />

      {/* Module switcher */}
      <div className="flex gap-1 p-3 pb-0">
        {(Object.keys(MODULES) as ModuleKey[]).map((key) => {
          const module = MODULES[key]
          const isActive = key === activeModule
          return (
            <button
              key={key}
              onClick={() => navigate(module.items[0].to)}
              className={cn(
                'flex-1 rounded-md px-3 py-1.5 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-primary text-primary-foreground'
                  : 'text-muted-foreground hover:bg-accent hover:text-foreground'
              )}
            >
              {module.label}
            </button>
          )
        })}
      </div>

      {/* Nav */}
      <nav className="flex flex-1 flex-col gap-1 p-3">
        {MODULES[activeModule].items.map(({ to, label, icon: Icon }) => (
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
