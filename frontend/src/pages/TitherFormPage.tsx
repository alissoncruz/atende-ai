import { useEffect, useState, FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent } from '@/components/ui/card'
import { titherService } from '@/services/tither.service'
import { churchService } from '@/services/church.service'
import type { Church, TitherPayload } from '@/types'

const EMPTY: TitherPayload = {
  name: '',
  cpf: '',
  phone: '',
  email: '',
  birthDate: '',
  zipCode: '',
  street: '',
  number: '',
  neighborhood: '',
  city: '',
  state: '',
  churchId: '',
  startDate: '',
  referenceAmount: undefined,
}

export function TitherFormPage() {
  const { id } = useParams()
  const isEdit = Boolean(id)
  const navigate = useNavigate()

  const [churches, setChurches] = useState<Church[]>([])
  const [form, setForm] = useState<TitherPayload>(EMPTY)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)
  const [loading, setLoading] = useState(isEdit)

  useEffect(() => {
    churchService.list().then(setChurches).catch(() => setChurches([]))
  }, [])

  useEffect(() => {
    if (!id) return
    titherService.get(id).then((t) => {
      setForm({
        name: t.name,
        cpf: t.cpf,
        phone: t.phone ?? '',
        email: t.email ?? '',
        birthDate: t.birthDate ?? '',
        zipCode: t.zipCode ?? '',
        street: t.street ?? '',
        number: t.number ?? '',
        neighborhood: t.neighborhood ?? '',
        city: t.city ?? '',
        state: t.state ?? '',
        churchId: t.churchId,
        startDate: t.startDate ?? '',
        referenceAmount: t.referenceAmount,
      })
      setLoading(false)
    })
  }, [id])

  function update<K extends keyof TitherPayload>(key: K, value: TitherPayload[K]) {
    setForm((f) => ({ ...f, [key]: value }))
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setSaving(true)
    try {
      const payload: TitherPayload = {
        ...form,
        referenceAmount: form.referenceAmount ? Number(form.referenceAmount) : undefined,
      }
      if (isEdit && id) {
        await titherService.update(id, payload)
      } else {
        await titherService.create(payload)
      }
      navigate('/tithers')
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Não foi possível salvar o dizimista.')
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return <p className="text-sm text-muted-foreground">Carregando...</p>
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-bold">{isEdit ? 'Editar Dizimista' : 'Novo Dizimista'}</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          {isEdit ? 'Atualize os dados do dizimista.' : 'Cadastre um novo membro dizimista da igreja.'}
        </p>
      </div>

      <Card>
        <CardContent className="pt-6">
          <form onSubmit={handleSubmit} className="flex flex-col gap-8">
            <section className="flex flex-col gap-4">
              <h2 className="text-sm font-semibold">Dados Pessoais</h2>
              <div className="grid grid-cols-3 gap-4">
                <Field label="Nome completo" className="col-span-2">
                  <Input value={form.name} onChange={(e) => update('name', e.target.value)} placeholder="Ex: Maria da Silva Souza" required />
                </Field>
                <Field label="CPF">
                  <Input value={form.cpf} onChange={(e) => update('cpf', e.target.value)} placeholder="000.000.000-00" required />
                </Field>
                <Field label="Telefone">
                  <Input value={form.phone} onChange={(e) => update('phone', e.target.value)} placeholder="(00) 00000-0000" />
                </Field>
                <Field label="E-mail" className="col-span-1">
                  <Input type="email" value={form.email} onChange={(e) => update('email', e.target.value)} placeholder="exemplo@email.com" />
                </Field>
                <Field label="Data de nascimento">
                  <Input type="date" value={form.birthDate} onChange={(e) => update('birthDate', e.target.value)} />
                </Field>
              </div>
            </section>

            <div className="border-t" />

            <section className="flex flex-col gap-4">
              <h2 className="text-sm font-semibold">Endereço</h2>
              <div className="grid grid-cols-3 gap-4">
                <Field label="CEP">
                  <Input value={form.zipCode} onChange={(e) => update('zipCode', e.target.value)} placeholder="00000-000" />
                </Field>
                <Field label="Rua / Avenida" className="col-span-2">
                  <Input value={form.street} onChange={(e) => update('street', e.target.value)} placeholder="Ex: Rua das Palmeiras" />
                </Field>
                <Field label="Número">
                  <Input value={form.number} onChange={(e) => update('number', e.target.value)} placeholder="123" />
                </Field>
                <Field label="Bairro">
                  <Input value={form.neighborhood} onChange={(e) => update('neighborhood', e.target.value)} placeholder="Ex: Centro" />
                </Field>
                <Field label="Cidade">
                  <Input value={form.city} onChange={(e) => update('city', e.target.value)} placeholder="Ex: Belo Horizonte" />
                </Field>
                <Field label="Estado">
                  <Input value={form.state} onChange={(e) => update('state', e.target.value.toUpperCase())} placeholder="MG" maxLength={2} />
                </Field>
              </div>
            </section>

            <div className="border-t" />

            <section className="flex flex-col gap-4">
              <h2 className="text-sm font-semibold">Vínculo com a Igreja</h2>
              <div className="grid grid-cols-3 gap-4">
                <Field label="Igreja / Congregação">
                  <select
                    className="h-10 rounded-md border border-input bg-background px-3 text-sm"
                    value={form.churchId}
                    onChange={(e) => update('churchId', e.target.value)}
                    required
                  >
                    <option value="" disabled>Selecione a igreja</option>
                    {churches.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </Field>
                <Field label="Data de início como dizimista">
                  <Input type="date" value={form.startDate} onChange={(e) => update('startDate', e.target.value)} />
                </Field>
                <Field label="Valor de referência mensal">
                  <Input
                    type="number"
                    step="0.01"
                    min="0"
                    value={form.referenceAmount ?? ''}
                    onChange={(e) => update('referenceAmount', e.target.value ? Number(e.target.value) : undefined)}
                    placeholder="0,00"
                  />
                </Field>
              </div>
            </section>

            {error && <p className="text-sm text-destructive">{error}</p>}

            <div className="flex justify-end gap-3">
              <Button type="button" variant="outline" onClick={() => navigate('/tithers')}>
                Cancelar
              </Button>
              <Button type="submit" disabled={saving}>
                {saving ? 'Salvando...' : 'Salvar Dizimista'}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}

function Field({ label, className, children }: { label: string; className?: string; children: React.ReactNode }) {
  return (
    <div className={`flex flex-col gap-1.5 ${className ?? ''}`}>
      <Label>{label}</Label>
      {children}
    </div>
  )
}
