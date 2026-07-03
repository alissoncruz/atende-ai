import { useEffect, useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { Search, Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card } from '@/components/ui/card'
import { titherService } from '@/services/tither.service'
import { churchService } from '@/services/church.service'
import type { Church, Tither } from '@/types'

export function TithersPage() {
  const [tithers, setTithers] = useState<Tither[]>([])
  const [churches, setChurches] = useState<Church[]>([])
  const [q, setQ] = useState('')
  const [churchId, setChurchId] = useState('')
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const page = await titherService.list({ q: q || undefined, churchId: churchId || undefined, size: 50 })
      setTithers(page.content)
      setTotalElements(page.totalElements)
    } finally {
      setLoading(false)
    }
  }, [q, churchId])

  useEffect(() => {
    churchService.list().then(setChurches).catch(() => setChurches([]))
  }, [])

  useEffect(() => {
    const timeout = setTimeout(load, 250)
    return () => clearTimeout(timeout)
  }, [load])

  async function handleDelete(id: string) {
    if (!confirm('Remover este dizimista?')) return
    await titherService.delete(id)
    load()
  }

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Dizimistas</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            {loading ? 'Carregando...' : `${totalElements} dizimistas cadastrados`}
          </p>
        </div>
        <Button asChild>
          <Link to="/dizimo/tithers/new">
            <Plus size={16} />
            Novo Dizimista
          </Link>
        </Button>
      </div>

      <div className="flex gap-3">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" size={16} />
          <Input
            className="pl-9"
            placeholder="Buscar por nome ou CPF..."
            value={q}
            onChange={(e) => setQ(e.target.value)}
          />
        </div>
        <select
          className="h-10 rounded-md border border-input bg-background px-3 text-sm"
          value={churchId}
          onChange={(e) => setChurchId(e.target.value)}
        >
          <option value="">Todas as igrejas</option>
          {churches.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
      </div>

      <Card className="overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b bg-muted/40 text-left text-xs font-semibold text-muted-foreground">
              <th className="px-4 py-3">Nome</th>
              <th className="px-4 py-3">CPF</th>
              <th className="px-4 py-3">Igreja</th>
              <th className="px-4 py-3">Telefone</th>
              <th className="px-4 py-3 text-right">Ações</th>
            </tr>
          </thead>
          <tbody>
            {tithers.map((t) => (
              <tr key={t.id} className="border-b last:border-0 hover:bg-muted/30">
                <td className="px-4 py-3 font-medium">{t.name}</td>
                <td className="px-4 py-3 text-muted-foreground">{t.cpf}</td>
                <td className="px-4 py-3">{t.churchName}</td>
                <td className="px-4 py-3 text-muted-foreground">{t.phone || '—'}</td>
                <td className="px-4 py-3 text-right">
                  <Link to={`/dizimo/tithers/${t.id}/edit`} className="text-primary text-sm font-medium hover:underline">
                    Editar
                  </Link>
                  <button
                    onClick={() => handleDelete(t.id)}
                    className="ml-4 text-destructive text-sm font-medium hover:underline"
                  >
                    Remover
                  </button>
                </td>
              </tr>
            ))}
            {!loading && tithers.length === 0 && (
              <tr>
                <td colSpan={5} className="px-4 py-8 text-center text-muted-foreground">
                  Nenhum dizimista encontrado.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </Card>
    </div>
  )
}
