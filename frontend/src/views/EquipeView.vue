<script setup>
import { onMounted } from 'vue'
import { useSolicitacoesStore } from '@/stores/solicitacoes'
import CalendarioAnual from '@/components/CalendarioAnual.vue'
import LegendaCores from '@/components/LegendaCores.vue'

const solicitacoesStore = useSolicitacoesStore()

onMounted(async () => {
  await Promise.all([
    solicitacoesStore.carregarSolicitacoesEquipe(),
    solicitacoesStore.carregarTiposAusencia()
  ])
})
</script>

<template>
  <div class="space-y-6 animate-fadeIn">
    <div>
      <h1 class="text-2xl font-bold text-white">Minha Equipe</h1>
      <p class="text-dark-400 mt-1">Visualize as ausências da sua equipe</p>
    </div>

    <!-- Legenda -->
    <LegendaCores :tipos="solicitacoesStore.tiposAusencia" />

    <!-- Calendário da Equipe -->
    <CalendarioAnual 
      :ano="new Date().getFullYear()" 
      :solicitacoes="solicitacoesStore.solicitacoesEquipe"
      :mostrar-usuario="true"
    />

    <!-- Lista de Solicitações -->
    <div class="card p-6">
      <h2 class="text-lg font-semibold text-white mb-4">Solicitações da Equipe</h2>
      
      <div v-if="solicitacoesStore.solicitacoesEquipe.length === 0" class="text-center py-8 text-dark-400">
        Nenhuma solicitação encontrada
      </div>

      <div v-else class="space-y-3">
        <div 
          v-for="s in solicitacoesStore.solicitacoesEquipe" 
          :key="s.id"
          class="flex items-center justify-between p-3 bg-dark-700/30 rounded-lg"
        >
          <div class="flex items-center gap-3">
            <div 
              class="w-10 h-10 rounded-full flex items-center justify-center text-white font-medium"
              :style="{ backgroundColor: s.tipoAusenciaCor }"
            >
              {{ s.usuarioNome.charAt(0) }}
            </div>
            <div>
              <p class="font-medium text-white">{{ s.usuarioNome }}</p>
              <p class="text-sm text-dark-400">
                {{ new Date(s.dataInicio).toLocaleDateString('pt-BR') }} - 
                {{ new Date(s.dataFim).toLocaleDateString('pt-BR') }}
                ({{ s.diasTotal }} dias)
              </p>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <span 
              class="px-2 py-1 text-xs font-medium rounded-full"
              :style="{ backgroundColor: s.tipoAusenciaCor + '20', color: s.tipoAusenciaCor }"
            >
              {{ s.tipoAusenciaNome }}
            </span>
            <span 
              :class="[
                'px-2 py-1 text-xs font-medium rounded-full',
                s.status === 'APROVADO' ? 'bg-green-500/20 text-green-400' :
                s.status === 'PENDENTE' ? 'bg-amber-500/20 text-amber-400' :
                s.status === 'REJEITADO' ? 'bg-red-500/20 text-red-400' :
                'bg-dark-500/20 text-dark-400'
              ]"
            >
              {{ s.status }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
