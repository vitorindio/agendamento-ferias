<script setup>
defineProps({
  titulo: String,
  valor: Number,
  cor: { type: String, default: 'primary' },
  icone: String
})

const cores = {
  primary: 'from-primary-600/20 to-primary-500/5 border-primary-500/20',
  blue: 'from-blue-600/20 to-blue-500/5 border-blue-500/20',
  amber: 'from-amber-600/20 to-amber-500/5 border-amber-500/20'
}

const textos = {
  primary: 'text-primary-400',
  blue: 'text-blue-400',
  amber: 'text-amber-400'
}

const icones = {
  disponivel: '📅',
  total: '📊',
  usados: '✓'
}
</script>

<template>
  <div :class="['card p-5 bg-gradient-to-br border', cores[cor]]">
    <div class="flex items-center justify-between">
      <div>
        <p class="text-sm text-dark-400">{{ titulo }}</p>
        <p :class="['text-3xl font-bold mt-1', textos[cor]]">{{ valor }}</p>
        <p class="text-xs text-dark-500 mt-1">dias</p>
      </div>
      <div class="w-14 h-14 rounded-xl bg-dark-700/50 flex items-center justify-center text-2xl">
        {{ icones[icone] || '📅' }}
      </div>
    </div>
  </div>
</template>
