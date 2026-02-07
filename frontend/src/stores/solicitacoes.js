import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/axios'

export const useSolicitacoesStore = defineStore('solicitacoes', () => {
  const minhasSolicitacoes = ref([])
  const solicitacoesEquipe = ref([])
  const solicitacoesPendentes = ref([])
  const saldo = ref(null)
  const tiposAusencia = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function carregarMinhasSolicitacoes(ano = new Date().getFullYear()) {
    loading.value = true
    error.value = null
    try {
      const response = await api.get(`/solicitacoes/minhas/ano/${ano}`)
      minhasSolicitacoes.value = response.data
    } catch (e) {
      error.value = e.response?.data?.message || 'Erro ao carregar solicitações'
    } finally {
      loading.value = false
    }
  }

  async function carregarSaldo(ano = new Date().getFullYear()) {
    try {
      const response = await api.get(`/usuarios/me/saldo/${ano}`)
      saldo.value = response.data
    } catch (e) {
      console.error('Erro ao carregar saldo:', e)
    }
  }

  async function carregarTiposAusencia() {
    try {
      const response = await api.get('/tipos-ausencia')
      tiposAusencia.value = response.data
    } catch (e) {
      console.error('Erro ao carregar tipos de ausência:', e)
    }
  }

  async function criarSolicitacao(dados) {
    loading.value = true
    error.value = null
    try {
      const response = await api.post('/solicitacoes', dados)
      minhasSolicitacoes.value.push(response.data)
      await carregarSaldo()
      return { success: true, data: response.data }
    } catch (e) {
      error.value = e.response?.data?.message || 'Erro ao criar solicitação'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  async function cancelarSolicitacao(id) {
    try {
      const response = await api.post(`/solicitacoes/${id}/cancelar`)
      const index = minhasSolicitacoes.value.findIndex(s => s.id === id)
      if (index !== -1) {
        minhasSolicitacoes.value[index] = response.data
      }
      await carregarSaldo()
      return { success: true }
    } catch (e) {
      return { success: false, error: e.response?.data?.message || 'Erro ao cancelar' }
    }
  }

  async function carregarSolicitacoesEquipe() {
    loading.value = true
    try {
      const response = await api.get('/solicitacoes/equipe')
      solicitacoesEquipe.value = response.data
    } catch (e) {
      console.error('Erro ao carregar solicitações da equipe:', e)
    } finally {
      loading.value = false
    }
  }

  async function carregarPendentes() {
    try {
      const response = await api.get('/solicitacoes/equipe/pendentes')
      solicitacoesPendentes.value = response.data
    } catch (e) {
      console.error('Erro ao carregar pendentes:', e)
    }
  }

  async function aprovarSolicitacao(id) {
    try {
      const response = await api.post(`/solicitacoes/${id}/aprovar`)
      atualizarSolicitacao(response.data)
      return { success: true }
    } catch (e) {
      return { success: false, error: e.response?.data?.message || 'Erro ao aprovar' }
    }
  }

  async function rejeitarSolicitacao(id, motivo) {
    try {
      const response = await api.post(`/solicitacoes/${id}/rejeitar`, { motivo })
      atualizarSolicitacao(response.data)
      return { success: true }
    } catch (e) {
      return { success: false, error: e.response?.data?.message || 'Erro ao rejeitar' }
    }
  }

  function atualizarSolicitacao(solicitacao) {
    // Atualizar em solicitacoesPendentes
    const indexPendente = solicitacoesPendentes.value.findIndex(s => s.id === solicitacao.id)
    if (indexPendente !== -1) {
      solicitacoesPendentes.value.splice(indexPendente, 1)
    }
    
    // Atualizar em solicitacoesEquipe
    const indexEquipe = solicitacoesEquipe.value.findIndex(s => s.id === solicitacao.id)
    if (indexEquipe !== -1) {
      solicitacoesEquipe.value[indexEquipe] = solicitacao
    }
  }

  return {
    minhasSolicitacoes,
    solicitacoesEquipe,
    solicitacoesPendentes,
    saldo,
    tiposAusencia,
    loading,
    error,
    carregarMinhasSolicitacoes,
    carregarSaldo,
    carregarTiposAusencia,
    criarSolicitacao,
    cancelarSolicitacao,
    carregarSolicitacoesEquipe,
    carregarPendentes,
    aprovarSolicitacao,
    rejeitarSolicitacao
  }
})
