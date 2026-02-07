<script setup>
import { ref, computed, onMounted } from 'vue'
import { useSolicitacoesStore } from '@/stores/solicitacoes'

const solicitacoesStore = useSolicitacoesStore()

const filtroStatus = ref('TODOS')
const anoFiltro = ref(new Date().getFullYear())

onMounted(async () => {
  await solicitacoesStore.carregarMinhasSolicitacoes(anoFiltro.value)
})

async function mudarAno(ano) {
  anoFiltro.value = ano
  await solicitacoesStore.carregarMinhasSolicitacoes(ano)
}

const solicitacoesFiltradas = computed(() => {
  let lista = solicitacoesStore.minhasSolicitacoes
  
  if (filtroStatus.value !== 'TODOS') {
    lista = lista.filter(s => s.status === filtroStatus.value)
  }
  
  return lista.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

async function cancelar(id) {
  if (confirm('Tem certeza que deseja cancelar esta solicitação?')) {
    await solicitacoesStore.cancelarSolicitacao(id)
  }
}

const statusConfig = {
  PENDENTE: { label: 'Pendente', class: 'bg-amber-500/20 text-amber-400' },
  APROVADO: { label: 'Aprovado', class: 'bg-green-500/20 text-green-400' },
  REJEITADO: { label: 'Rejeitado', class: 'bg-red-500/20 text-red-400' },
  CANCELADO: { label: 'Cancelado', class: 'bg-dark-500/20 text-dark-400' }
}
</script>

<template>
  <div class="space-y-6 animate-fadeIn">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-white">Minhas Solicitações</h1>
        <p class="text-dark-400 mt-1">Acompanhe o status das suas solicitações</p>
      </div>

      <RouterLink to="/nova-solicitacao" class="btn btn-primary">
        + Nova Solicitação
      </RouterLink>
    </div>

    <!-- Filtros -->
    <div class="card p-4">
      <div class="flex flex-wrap items-center gap-4">
        <div class="flex items-center gap-2">
          <span class="text-sm text-dark-400">Ano:</span>
          <select v-model="anoFiltro" @change="mudarAno(anoFiltro)" class="input w-auto py-1.5">
            <option v-for="ano in [2024, 2025, 2026, 2027]" :key="ano" :value="ano">{{ ano }}</option>
          </select>
        </div>
        
        <div class="flex items-center gap-2">
          <span class="text-sm text-dark-400">Status:</span>
          <div class="flex gap-1">
            <button
              v-for="status in ['TODOS', 'PENDENTE', 'APROVADO', 'REJEITADO', 'CANCELADO']"
              :key="status"
              @click="filtroStatus = status"
              :class="[
                'px-3 py-1.5 text-xs font-medium rounded-lg transition-all',
                filtroStatus === status 
                  ? 'bg-primary-600 text-white' 
                  : 'bg-dark-700 text-dark-300 hover:bg-dark-600'
              ]"
            >
              {{ status === 'TODOS' ? 'Todos' : statusConfig[status].label }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Lista -->
    <div v-if="solicitacoesFiltradas.length === 0" class="card p-12 text-center">
      <div class="w-16 h-16 mx-auto rounded-full bg-dark-700 flex items-center justify-center mb-4">
        <svg class="w-8 h-8 text-dark-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
        </svg>
      </div>
      <p class="text-dark-400">Nenhuma solicitação encontrada</p>
      <RouterLink to="/nova-solicitacao" class="btn btn-primary mt-4 inline-block">
        Criar Primeira Solicitação
      </RouterLink>
    </div>

    <div v-else class="space-y-3">
      <div 
        v-for="s in solicitacoesFiltradas" 
        :key="s.id"
        class="card p-4 hover:border-dark-600 transition-colors"
      >
        <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div class="flex items-start gap-4">
            <div 
              class="w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0"
              :style="{ backgroundColor: s.tipoAusenciaCor + '20' }"
            >
              <span class="text-2xl">
                {{ s.tipoAusenciaNome === 'Férias' ? '🏖️' : 
                   s.tipoAusenciaNome === 'Licença Médica' ? '🏥' : 
                   s.tipoAusenciaNome === 'Day Off' ? '☀️' : '📅' }}
              </span>
            </div>
            
            <div>
              <div class="flex items-center gap-2">
                <span 
                  class="px-2 py-0.5 text-xs font-medium rounded-full"
                  :style="{ backgroundColor: s.tipoAusenciaCor + '20', color: s.tipoAusenciaCor }"
                >
                  {{ s.tipoAusenciaNome }}
                </span>
                <span :class="['px-2 py-0.5 text-xs font-medium rounded-full', statusConfig[s.status].class]">
                  {{ statusConfig[s.status].label }}
                </span>
              </div>
              
              <p class="text-white font-medium mt-1">
                {{ new Date(s.dataInicio).toLocaleDateString('pt-BR') }} 
                até 
                {{ new Date(s.dataFim).toLocaleDateString('pt-BR') }}
              </p>
              
              <p class="text-sm text-dark-400">
                {{ s.diasTotal }} dias 
                <span v-if="s.observacao">• {{ s.observacao }}</span>
              </p>
              
              <p v-if="s.motivoRejeicao" class="text-sm text-red-400 mt-1">
                Motivo: {{ s.motivoRejeicao }}
              </p>
            </div>
          </div>

          <div class="flex items-center gap-2">
            <span class="text-xs text-dark-500">
              Criado em {{ new Date(s.createdAt).toLocaleDateString('pt-BR') }}
            </span>
            
            <button 
              v-if="s.status === 'PENDENTE'"
              @click="cancelar(s.id)"
              class="btn btn-danger text-sm px-3 py-1"
            >
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
