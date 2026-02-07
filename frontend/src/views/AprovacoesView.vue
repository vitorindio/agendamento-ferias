<script setup>
import { ref, onMounted } from 'vue'
import { useSolicitacoesStore } from '@/stores/solicitacoes'

const solicitacoesStore = useSolicitacoesStore()

const motivoRejeicao = ref('')
const solicitacaoRejeitando = ref(null)

onMounted(async () => {
  await solicitacoesStore.carregarPendentes()
})

async function aprovar(id) {
  if (confirm('Confirma a aprovação desta solicitação?')) {
    const result = await solicitacoesStore.aprovarSolicitacao(id)
    if (!result.success) {
      alert(result.error)
    }
  }
}

function abrirRejeicao(id) {
  solicitacaoRejeitando.value = id
  motivoRejeicao.value = ''
}

async function confirmarRejeicao() {
  if (!motivoRejeicao.value.trim()) {
    alert('Informe o motivo da rejeição')
    return
  }
  
  const result = await solicitacoesStore.rejeitarSolicitacao(solicitacaoRejeitando.value, motivoRejeicao.value)
  
  if (result.success) {
    solicitacaoRejeitando.value = null
    motivoRejeicao.value = ''
  } else {
    alert(result.error)
  }
}
</script>

<template>
  <div class="space-y-6 animate-fadeIn">
    <div>
      <h1 class="text-2xl font-bold text-white">Aprovações Pendentes</h1>
      <p class="text-dark-400 mt-1">Gerencie as solicitações da sua equipe</p>
    </div>

    <!-- Empty State -->
    <div v-if="solicitacoesStore.solicitacoesPendentes.length === 0" class="card p-12 text-center">
      <div class="w-16 h-16 mx-auto rounded-full bg-green-500/20 flex items-center justify-center mb-4">
        <svg class="w-8 h-8 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
      </div>
      <p class="text-lg font-medium text-white">Tudo em dia! 🎉</p>
      <p class="text-dark-400 mt-2">Não há solicitações pendentes de aprovação</p>
    </div>

    <!-- Lista de Pendentes -->
    <div v-else class="space-y-4">
      <div 
        v-for="s in solicitacoesStore.solicitacoesPendentes" 
        :key="s.id"
        class="card p-6"
      >
        <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div class="flex items-start gap-4">
            <div 
              class="w-14 h-14 rounded-xl flex items-center justify-center flex-shrink-0"
              :style="{ backgroundColor: s.tipoAusenciaCor + '20' }"
            >
              <span class="text-2xl">
                {{ s.tipoAusenciaNome === 'Férias' ? '🏖️' : 
                   s.tipoAusenciaNome === 'Licença Médica' ? '🏥' : 
                   s.tipoAusenciaNome === 'Day Off' ? '☀️' : '📅' }}
              </span>
            </div>
            
            <div>
              <div class="flex items-center gap-2 mb-1">
                <span class="text-lg font-semibold text-white">{{ s.usuarioNome }}</span>
                <span 
                  class="px-2 py-0.5 text-xs font-medium rounded-full"
                  :style="{ backgroundColor: s.tipoAusenciaCor + '20', color: s.tipoAusenciaCor }"
                >
                  {{ s.tipoAusenciaNome }}
                </span>
              </div>
              
              <p class="text-dark-300">
                📅 {{ new Date(s.dataInicio).toLocaleDateString('pt-BR') }} 
                até 
                {{ new Date(s.dataFim).toLocaleDateString('pt-BR') }}
              </p>
              
              <p class="text-dark-400 text-sm mt-1">
                <strong class="text-white">{{ s.diasTotal }} dias</strong> de ausência
                <span v-if="s.observacao"> • {{ s.observacao }}</span>
              </p>
              
              <p class="text-xs text-dark-500 mt-2">
                Solicitado em {{ new Date(s.createdAt).toLocaleDateString('pt-BR', { 
                  day: '2-digit', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' 
                }) }}
              </p>
            </div>
          </div>

          <div class="flex items-center gap-2">
            <button 
              @click="aprovar(s.id)"
              class="btn btn-primary"
            >
              ✓ Aprovar
            </button>
            <button 
              @click="abrirRejeicao(s.id)"
              class="btn btn-danger"
            >
              ✕ Rejeitar
            </button>
          </div>
        </div>

        <!-- Modal de Rejeição -->
        <div 
          v-if="solicitacaoRejeitando === s.id"
          class="mt-4 p-4 bg-dark-700/50 rounded-lg border border-dark-600"
        >
          <p class="text-sm text-dark-300 mb-2">Motivo da rejeição:</p>
          <textarea 
            v-model="motivoRejeicao"
            class="input resize-none"
            rows="2"
            placeholder="Informe o motivo..."
          ></textarea>
          <div class="flex justify-end gap-2 mt-3">
            <button @click="solicitacaoRejeitando = null" class="btn btn-secondary text-sm">
              Cancelar
            </button>
            <button @click="confirmarRejeicao" class="btn btn-danger text-sm">
              Confirmar Rejeição
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
