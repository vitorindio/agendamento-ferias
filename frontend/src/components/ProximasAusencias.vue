<script setup>
defineProps({
  solicitacoes: { type: Array, default: () => [] }
})

function formatarData(data) {
  return new Date(data).toLocaleDateString('pt-BR', { 
    day: '2-digit', 
    month: 'short' 
  })
}
</script>

<template>
  <div class="card p-6">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-lg font-semibold text-white">Próximas Ausências</h2>
      <RouterLink to="/minhas-solicitacoes" class="text-sm text-primary-400 hover:text-primary-300">
        Ver todas →
      </RouterLink>
    </div>

    <div v-if="solicitacoes.length === 0" class="text-center py-8">
      <div class="w-12 h-12 mx-auto rounded-full bg-dark-700 flex items-center justify-center mb-3">
        <span class="text-xl">📅</span>
      </div>
      <p class="text-dark-400 text-sm">Nenhuma ausência programada</p>
      <RouterLink to="/nova-solicitacao" class="text-primary-400 text-sm hover:text-primary-300 mt-2 inline-block">
        Agendar férias →
      </RouterLink>
    </div>

    <div v-else class="space-y-3">
      <div 
        v-for="s in solicitacoes" 
        :key="s.id"
        class="flex items-center gap-4 p-3 rounded-lg bg-dark-700/30 hover:bg-dark-700/50 transition-colors"
      >
        <div 
          class="w-12 h-12 rounded-lg flex flex-col items-center justify-center text-xs font-medium"
          :style="{ backgroundColor: s.tipoAusenciaCor + '20', color: s.tipoAusenciaCor }"
        >
          <span class="text-lg">{{ formatarData(s.dataInicio).split(' ')[0] }}</span>
          <span class="uppercase text-[10px] opacity-80">{{ formatarData(s.dataInicio).split(' ')[1] }}</span>
        </div>
        
        <div class="flex-1 min-w-0">
          <p class="font-medium text-white truncate">{{ s.tipoAusenciaNome }}</p>
          <p class="text-sm text-dark-400">
            {{ formatarData(s.dataInicio) }} - {{ formatarData(s.dataFim) }}
            <span class="text-dark-500">• {{ s.diasTotal }} dias</span>
          </p>
        </div>

        <span 
          :class="[
            'px-2 py-1 text-xs font-medium rounded-full whitespace-nowrap',
            s.status === 'APROVADO' ? 'bg-green-500/20 text-green-400' : 'bg-amber-500/20 text-amber-400'
          ]"
        >
          {{ s.status === 'APROVADO' ? '✓ Aprovado' : '⏳ Pendente' }}
        </span>
      </div>
    </div>
  </div>
</template>
