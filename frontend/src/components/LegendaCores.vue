<script setup>
defineProps({
  tipos: { type: Array, default: () => [] }
})
</script>

<template>
  <div class="flex flex-wrap items-center gap-4 text-sm">
    <span class="text-dark-400">Legenda:</span>
    <div 
      v-for="tipo in tipos" 
      :key="tipo.id"
      class="flex items-center gap-2"
    >
      <div 
        class="w-4 h-4 rounded"
        :style="{ backgroundColor: tipo.corHex }"
      />
      <span class="text-dark-300">{{ tipo.nome }}</span>
    </div>
    
    <!-- Status indicators -->
    <div class="flex items-center gap-2 ml-4 pl-4 border-l border-dark-700">
      <div class="flex items-center gap-1">
        <div class="w-4 h-4 rounded bg-primary-500/80" />
        <span class="text-dark-400 text-xs">Aprovado</span>
      </div>
      <div class="flex items-center gap-1">
        <div class="w-4 h-4 rounded bg-primary-500/40 border border-dashed border-primary-400" />
        <span class="text-dark-400 text-xs">Pendente</span>
      </div>
    </div>
  </div>
</template>
