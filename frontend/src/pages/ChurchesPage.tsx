import { useEffect, useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { churchService } from '@/services/church.service'
import type { Church } from '@/types'

export function ChurchesPage() {
  const [churches, setChurches] = useState<Church[]>([])
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      setChurches(await churchService.list())
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  async function handleDelete(id: string) {
    if (!confirm('Remover esta igreja?')) return
    await churchService.delete(id)
    load()
  }

  const matriz = churches.filter((c) => c.type === 'MATRIZ')
  const capelas = churches.filter((c) => c.type === 'CAPELA')

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Igrejas</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            {loading ? 'Carregando...' : `${churches.length} igrejas cadastradas`}
          </p>
        </div>
        <Button asChild>
          <Link to="/dizimo/churches/new">
            <Plus size={16} />
            Nova Igreja
          </Link>
        </Button>
      </div>

      <Card className="overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b bg-muted/40 text-left text-xs font-semibold text-muted-foreground">
              <th className="px-4 py-3">Nome</th>
              <th className="px-4 py-3">Tipo</th>
              <th className="px-4 py-3">Endereço</th>
              <th className="px-4 py-3 text-right">Ações</th>
            </tr>
          </thead>
          <tbody>
            {[...matriz, ...capelas].map((c) => (
              <tr key={c.id} className="border-b last:border-0 hover:bg-muted/30">
                <td className="px-4 py-3 font-medium">{c.name}</td>
                <td className="px-4 py-3">
                  <span
                    className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
                      c.type === 'MATRIZ' ? 'bg-blue-100 text-blue-700' : 'bg-muted text-muted-foreground'
                    }`}
                  >
                    {c.type === 'MATRIZ' ? 'Matriz' : 'Capela'}
                  </span>
                </td>
                <td className="px-4 py-3 text-muted-foreground">{c.address || '—'}</td>
                <td className="px-4 py-3 text-right">
                  <Link to={`/dizimo/churches/${c.id}/edit`} className="text-primary text-sm font-medium hover:underline">
                    Editar
                  </Link>
                  <button
                    onClick={() => handleDelete(c.id)}
                    className="ml-4 text-destructive text-sm font-medium hover:underline"
                  >
                    Remover
                  </button>
                </td>
              </tr>
            ))}
            {!loading && churches.length === 0 && (
              <tr>
                <td colSpan={4} className="px-4 py-8 text-center text-muted-foreground">
                  Nenhuma igreja encontrada.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </Card>
    </div>
  )
}
