import { useAuth } from '@/contexts/AuthContext'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Users, Calendar, MessageSquare, BookOpen } from 'lucide-react'

const STATS = [
  { label: 'Clientes', icon: Users, value: '—' },
  { label: 'Agendamentos hoje', icon: Calendar, value: '—' },
  { label: 'Conversas ativas', icon: MessageSquare, value: '—' },
  { label: 'Documentos KB', icon: BookOpen, value: '—' },
]

export function DashboardPage() {
  const { user } = useAuth()

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-bold">Dashboard</h1>
        <p className="text-muted-foreground text-sm">Bem-vindo, {user?.email}</p>
      </div>

      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        {STATS.map(({ label, icon: Icon, value }) => (
          <Card key={label}>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">{label}</CardTitle>
              <Icon size={16} className="text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{value}</div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )
}
