<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSolicitacoesStore } from '@/stores/solicitacoes'

const router = useRouter()
const solicitacoesStore = useSolicitacoesStore()

const form = ref({
  tipoAusenciaId: '',
  dataInicio: '',
  dataFim: '',
  observacao: ''
})

const loading = ref(false)
const error = ref('')
const success = ref(false)

onMounted(async () => {
  await solicitacoesStore.carregarTiposAusencia()
  await solicitacoesStore.carregarSaldo()
})

const diasSolicitados = computed(() => {
  if (!form.value.dataInicio || !form.value.dataFim) return 0
  const inicio = new Date(form.value.dataInicio)
  const fim = new Date(form.value.dataFim)
  const diff = Math.ceil((fim - inicio) / (1000 * 60 * 60 * 24)) + 1
  return diff > 0 ? diff : 0
})

const tipoSelecionado = computed(() => {
  return solicitacoesStore.tiposAusencia.find(t => t.id == form.value.tipoAusenciaId)
})

const saldoAposAprovacao = computed(() => {
  if (!tipoSelecionado.value?.deduzSaldo) return null
  const disponivel = solicitacoesStore.saldo?.diasDisponiveis || 0
  return disponivel - diasSolicitados.value
})

async function handleSubmit() {
  error.value = ''
  loading.value = true

  const result = await solicitacoesStore.criarSolicitacao({
    tipoAusenciaId: parseInt(form.value.tipoAusenciaId),
    dataInicio: form.value.dataInicio,
    dataFim: form.value.dataFim,
    observacao: form.value.observacao
  })

  loading.value = false

  if (result.success) {
    success.value = true
    setTimeout(() => {
      router.push({ name: 'minhas-solicitacoes' })
    }, 2000)
  } else {
    error.value = result.error
  }
}
</script>

<template>
  <div class="max-w-2xl mx-auto animate-fadeIn">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-white">Nova Solicitação</h1>
      <p class="text-dark-400 mt-1">Solicite suas férias ou registre uma ausência</p>
    </div>

    <!-- Success Message -->
    <div v-if="success" class="card p-8 text-center">
      <div class="w-16 h-16 mx-auto rounded-full bg-green-500/20 flex items-center justify-center mb-4">
        <svg class="w-10 h-10 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
      </div>
      <h2 class="text-xl font-semibold text-white">Solicitação Enviada!</h2>
      <p class="text-dark-400 mt-2">Sua solicitação foi registrada e está aguardando aprovação.</p>
    </div>

    <!-- Form -->
    <div v-else class="card p-6">
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div v-if="error" class="p-4 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400 text-sm">
          {{ error }}
        </div>

        <!-- Tipo de Ausência -->
        <div>
          <label class="block text-sm font-medium text-dark-300 mb-2">Tipo de Ausência</label>
          <select v-model="form.tipoAusenciaId" class="input" required>
            <option value="">Selecione...</option>
            <option 
              v-for="tipo in solicitacoesStore.tiposAusencia" 
              :key="tipo.id" 
              :value="tipo.id"
            >
              {{ tipo.nome }}
            </option>
          </select>
          <p v-if="tipoSelecionado" class="mt-2 text-xs text-dark-400">
            {{ tipoSelecionado.descricao }}
            <span v-if="tipoSelecionado.deduzSaldo" class="text-amber-400"> • Deduz do saldo de férias</span>
          </p>
        </div>

        <!-- Datas -->
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-dark-300 mb-2">Data Início</label>
            <input 
              v-model="form.dataInicio" 
              type="date" 
              class="input"
              :min="new Date().toISOString().split('T')[0]"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-dark-300 mb-2">Data Fim</label>
            <input 
              v-model="form.dataFim" 
              type="date" 
              class="input"
              :min="form.dataInicio || new Date().toISOString().split('T')[0]"
              required
            />
          </div>
        </div>

        <!-- Preview de Dias -->
        <div v-if="diasSolicitados > 0" class="p-4 bg-dark-700/50 rounded-lg">
          <div class="flex items-center justify-between">
            <span class="text-dark-300">Dias solicitados:</span>
            <span class="text-xl font-bold text-white">{{ diasSolicitados }} dias</span>
          </div>
          <div v-if="saldoAposAprovacao !== null" class="flex items-center justify-between mt-2 pt-2 border-t border-dark-600">
            <span class="text-dark-300">Saldo após aprovação:</span>
            <span 
              :class="[
                'text-xl font-bold',
                saldoAposAprovacao >= 0 ? 'text-green-400' : 'text-red-400'
              ]"
            >
              {{ saldoAposAprovacao }} dias
            </span>
          </div>
        </div>

        <!-- Observação -->
        <div>
          <label class="block text-sm font-medium text-dark-300 mb-2">Observação (opcional)</label>
          <textarea 
            v-model="form.observacao"
            class="input resize-none"
            rows="3"
            placeholder="Informações adicionais..."
          ></textarea>
        </div>

        <!-- Saldo Atual -->
        <div class="p-4 bg-primary-500/10 border border-primary-500/20 rounded-lg">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-lg bg-primary-500/20 flex items-center justify-center">
              <span class="text-lg">📅</span>
            </div>
            <div>
              <p class="text-xs text-primary-300">Saldo Atual</p>
              <p class="text-lg font-bold text-primary-400">
                {{ solicitacoesStore.saldo?.diasDisponiveis || 0 }} dias disponíveis
              </p>
            </div>
          </div>
        </div>

        <!-- Botões -->
        <div class="flex gap-3">
          <button 
            type="button"
            @click="router.back()"
            class="btn btn-secondary flex-1"
          >
            Cancelar
          </button>
          <button 
            type="submit" 
            class="btn btn-primary flex-1"
            :disabled="loading || (saldoAposAprovacao !== null && saldoAposAprovacao < 0)"
          >
            <span v-if="loading">Enviando...</span>
            <span v-else>Enviar Solicitação</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
