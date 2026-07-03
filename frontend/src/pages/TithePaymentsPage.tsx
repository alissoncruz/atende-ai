import { useEffect, useState, useCallback } from 'react'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { churchService } from '@/services/church.service'
import { tithePaymentService } from '@/services/tithePayment.service'
import type { Church, MonthlySummary, MonthlyTitherRow } from '@/types'

const MONTH_LABELS = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro',
]

function currentMonth(): string {
  const now = new Date()
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
}

function monthLabel(month: string): string {
  const [year, m] = month.split('-').map(Number)
  return `${MONTH_LABELS[m - 1]} de ${year}`
}

function shiftMonth(month: string, delta: number): string {
  const [year, m] = month.split('-').map(Number)
  const date = new Date(year, m - 1 + delta, 1)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

function formatCurrency(value?: number): string {
  return (value ?? 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export function TithePaymentsPage() {
  const [month, setMonth] = useState(currentMonth())
  const [churches, setChurches] = useState<Church[]>([])
  const [churchId, setChurchId] = useState('')
  const [rows, setRows] = useState<MonthlyTitherRow[]>([])
  const [summary, setSummary] = useState<MonthlySummary | null>(null)
  const [loading, setLoading] = useState(true)
  const [busyId, setBusyId] = useState<string | null>(null)

  useEffect(() => {
    churchService.list().then(setChurches).catch(() => setChurches([]))
  }, [])

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const [rowsData, summaryData] = await Promise.all([
        tithePaymentService.monthly({ month, churchId: churchId || undefined }),
        tithePaymentService.summary(month, churchId || undefined),
      ])
      setRows(rowsData)
      setSummary(summaryData)
    } finally {
      setLoading(false)
    }
  }, [month, churchId])

  useEffect(() => {
    load()
  }, [load])

  async function handleMarkPaid(titherId: string) {
    setBusyId(titherId)
    try {
      await tithePaymentService.markPaid({ titherId, referenceMonth: month })
      await load()
    } finally {
      setBusyId(null)
    }
  }

  async function handleUnmark(titherId: string) {
    if (!confirm('Desfazer este pagamento?')) return
    setBusyId(titherId)
    try {
      await tithePaymentService.unmark(titherId, month)
      await load()
    } finally {
      setBusyId(null)
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Controle de Pagamentos</h1>
          <p className="mt-1 text-sm text-muted-foreground">Acompanhe quem já pagou o dízimo no mês.</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1 rounded-md border border-input bg-background px-2 py-1">
            <button onClick={() => setMonth((m) => shiftMonth(m, -1))} className="rounded p-1 hover:bg-accent">
              <ChevronLeft size={16} />
            </button>
            <span className="w-36 text-center text-sm font-semibold">{monthLabel(month)}</span>
            <button onClick={() => setMonth((m) => shiftMonth(m, 1))} className="rounded p-1 hover:bg-accent">
              <ChevronRight size={16} />
            </button>
          </div>
          <select
            className="h-10 rounded-md border border-input bg-background px-3 text-sm"
            value={churchId}
            onChange={(e) => setChurchId(e.target.value)}
          >
            <option value="">Todas as igrejas</option>
            {churches.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <SummaryCard label="Dizimistas Ativos" value={String(summary?.totalActive ?? '—')} />
        <SummaryCard
          label={`Pagos em ${monthLabel(month).split(' de ')[0]}`}
          value={summary ? `${summary.paidCount} (${pct(summary.paidCount, summary.totalActive)}%)` : '—'}
          tone="success"
        />
        <SummaryCard
          label={`Pendentes em ${monthLabel(month).split(' de ')[0]}`}
          value={summary ? `${summary.pendingCount} (${pct(summary.pendingCount, summary.totalActive)}%)` : '—'}
          tone="danger"
        />
        <SummaryCard label="Total Arrecadado" value={formatCurrency(summary?.totalAmount)} tone="primary" />
      </div>

      <Card className="overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b bg-muted/40 text-left text-xs font-semibold text-muted-foreground">
              <th className="px-4 py-3">Dizimista</th>
              <th className="px-4 py-3">Igreja</th>
              <th className="px-4 py-3">Valor Referência</th>
              <th className="px-4 py-3">Status</th>
              <th className="px-4 py-3">Data do Pagamento</th>
              <th className="px-4 py-3 text-right">Ação</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => {
              const isPaid = row.status === 'PAGO'
              return (
                <tr key={row.titherId} className={`border-b last:border-0 ${isPaid ? '' : 'bg-destructive/5'}`}>
                  <td className="px-4 py-3 font-medium">{row.titherName}</td>
                  <td className="px-4 py-3">{row.churchName}</td>
                  <td className="px-4 py-3 text-muted-foreground">{formatCurrency(row.referenceAmount)}</td>
                  <td className="px-4 py-3">
                    <span
                      className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
                        isPaid ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'
                      }`}
                    >
                      {isPaid ? 'Pago' : 'Pendente'}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-muted-foreground">
                    {row.paidAt ? new Date(row.paidAt).toLocaleDateString('pt-BR') : '—'}
                  </td>
                  <td className="px-4 py-3 text-right">
                    {isPaid ? (
                      <button
                        onClick={() => handleUnmark(row.titherId)}
                        disabled={busyId === row.titherId}
                        className="text-sm font-medium text-muted-foreground hover:underline"
                      >
                        Desfazer
                      </button>
                    ) : (
                      <Button size="sm" onClick={() => handleMarkPaid(row.titherId)} disabled={busyId === row.titherId}>
                        {busyId === row.titherId ? 'Salvando...' : 'Marcar pago'}
                      </Button>
                    )}
                  </td>
                </tr>
              )
            })}
            {!loading && rows.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-muted-foreground">
                  Nenhum dizimista encontrado para este filtro.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </Card>
    </div>
  )
}

function pct(part: number, total: number): number {
  if (!total) return 0
  return Math.round((part / total) * 100)
}

function SummaryCard({ label, value, tone = 'default' }: { label: string; value: string; tone?: 'default' | 'success' | 'danger' | 'primary' }) {
  const toneClass = {
    default: 'text-foreground',
    success: 'text-emerald-600',
    danger: 'text-red-600',
    primary: 'text-primary',
  }[tone]

  return (
    <Card className="p-5">
      <p className="text-sm font-medium text-muted-foreground">{label}</p>
      <p className={`mt-1 text-2xl font-bold ${toneClass}`}>{value}</p>
    </Card>
  )
}
