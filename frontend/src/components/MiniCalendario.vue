<script setup>
import { computed } from 'vue'

const props = defineProps({
  solicitacoes: { type: Array, default: () => [] }
})

const hoje = new Date()
const mesAtual = hoje.getMonth()
const anoAtual = hoje.getFullYear()

const diasDoMes = computed(() => {
  const primeiroDia = new Date(anoAtual, mesAtual, 1)
  const ultimoDia = new Date(anoAtual, mesAtual + 1, 0)
  const diasNoMes = ultimoDia.getDate()
  const primeiroDiaSemana = primeiroDia.getDay()
  
  const dias = []
  
  // Dias vazios antes do primeiro dia do mês
  for (let i = 0; i < primeiroDiaSemana; i++) {
    dias.push({ dia: null })
  }
  
  // Dias do mês
  for (let dia = 1; dia <= diasNoMes; dia++) {
    const data = new Date(anoAtual, mesAtual, dia)
    const ausencia = verificarAusencia(data)
    
    dias.push({
      dia,
      hoje: dia === hoje.getDate(),
      ausencia
    })
  }
  
  return dias
})

function verificarAusencia(data) {
  const dataStr = data.toISOString().split('T')[0]
  
  for (const s of props.solicitacoes) {
    if (s.status === 'CANCELADO') continue
    
    const inicio = s.dataInicio
    const fim = s.dataFim
    
    if (dataStr >= inicio && dataStr <= fim) {
      return {
        tipo: s.tipoAusenciaNome,
        cor: s.tipoAusenciaCor,
        status: s.status
      }
    }
  }
  
  return null
}

const nomeMes = new Date(anoAtual, mesAtual).toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' })
const diasSemana = ['D', 'S', 'T', 'Q', 'Q', 'S', 'S']
</script>

<template>
  <div class="card p-6">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-lg font-semibold text-white capitalize">{{ nomeMes }}</h2>
      <RouterLink to="/minha-agenda" class="text-sm text-primary-400 hover:text-primary-300">
        Ver ano completo →
      </RouterLink>
    </div>

    <!-- Cabeçalho dos dias da semana -->
    <div class="grid grid-cols-7 gap-1 mb-2">
      <div 
        v-for="dia in diasSemana" 
        :key="dia" 
        class="text-center text-xs font-medium text-dark-400 py-2"
      >
        {{ dia }}
      </div>
    </div>

    <!-- Dias do mês -->
    <div class="grid grid-cols-7 gap-1">
      <div 
        v-for="(item, index) in diasDoMes" 
        :key="index"
        :class="[
          'calendar-day',
          item.hoje ? 'today' : '',
          item.ausencia && item.ausencia.status === 'APROVADO' ? 'vacation' : '',
          item.ausencia && item.ausencia.status === 'PENDENTE' ? 'vacation-pending' : ''
        ]"
        :style="item.ausencia ? { 
          backgroundColor: item.ausencia.cor + (item.ausencia.status === 'APROVADO' ? 'cc' : '66'),
          borderColor: item.ausencia.status === 'PENDENTE' ? item.ausencia.cor : 'transparent'
        } : {}"
        :title="item.ausencia ? `${item.ausencia.tipo} (${item.ausencia.status})` : ''"
      >
        {{ item.dia }}
      </div>
    </div>
  </div>
</template>
