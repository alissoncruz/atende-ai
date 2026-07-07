import { useState, FormEvent } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent } from '@/components/ui/card'
import { churchService } from '@/services/church.service'
import type { ChurchType } from '@/types'

export function ChurchFormPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const initialType = searchParams.get('type') === 'MATRIZ' ? 'MATRIZ' : 'CAPELA'

  const [type, setType] = useState<ChurchType>(initialType)
  const [name, setName] = useState('')
  const [address, setAddress] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setSaving(true)
    try {
      await churchService.create({ name: name.trim(), type, address: address.trim() || undefined })
      navigate('/dizimo/tithers/new')
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Não foi possível salvar a igreja.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-bold">Nova {type === 'MATRIZ' ? 'Matriz' : 'Capela'}</h1>
        <p className="mt-1 text-sm text-muted-foreground">Cadastre uma nova igreja para vincular aos dizimistas.</p>
      </div>

      <Card>
        <CardContent className="pt-6">
          <form onSubmit={handleSubmit} className="flex flex-col gap-6">
            <div className="flex flex-col gap-1.5">
              <Label>Tipo</Label>
              <div className="flex w-fit gap-1 rounded-md border border-input bg-background p-1">
                {(['MATRIZ', 'CAPELA'] as ChurchType[]).map((t) => (
                  <button
                    key={t}
                    type="button"
                    onClick={() => setType(t)}
                    className={`rounded px-4 py-1.5 text-sm font-medium transition-colors ${
                      type === t
                        ? 'bg-primary text-primary-foreground'
                        : 'text-muted-foreground hover:bg-accent hover:text-foreground'
                    }`}
                  >
                    {t === 'MATRIZ' ? 'Matriz' : 'Capela'}
                  </button>
                ))}
              </div>
            </div>

            <div className="flex flex-col gap-1.5">
              <Label>Nome {type === 'MATRIZ' ? 'da matriz' : 'da capela'}</Label>
              <Input
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder={type === 'MATRIZ' ? 'Ex: Igreja Sede' : 'Ex: Capela Bela Vista'}
                required
              />
            </div>

            <div className="flex flex-col gap-1.5">
              <Label>Endereço</Label>
              <Input
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                placeholder="Ex: Rua das Palmeiras, 123 - Centro"
              />
            </div>

            {error && <p className="text-sm text-destructive">{error}</p>}

            <div className="flex justify-end gap-3">
              <Button type="button" variant="outline" onClick={() => navigate('/dizimo/tithers/new')}>
                Cancelar
              </Button>
              <Button type="submit" disabled={saving}>
                {saving ? 'Salvando...' : `Salvar ${type === 'MATRIZ' ? 'Matriz' : 'Capela'}`}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
