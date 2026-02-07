<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  ano: { type: Number, default: new Date().getFullYear() },
  solicitacoes: { type: Array, default: () => [] },
  mostrarUsuario: { type: Boolean, default: false }
})

const tooltipData = ref(null)
const tooltipPosition = ref({ x: 0, y: 0 })

const meses = computed(() => {
  // Dependência explícita em props.solicitacoes para garantir reatividade
  const _ = props.solicitacoes
  
  const nomesMeses = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
  ]
  
  return nomesMeses.map((nome, index) => ({
    nome,
    dias: gerarDiasDoMes(index)
  }))
})

function gerarDiasDoMes(mes) {
  const primeiroDia = new Date(props.ano, mes, 1)
  const ultimoDia = new Date(props.ano, mes + 1, 0)
  const diasNoMes = ultimoDia.getDate()
  const primeiroDiaSemana = primeiroDia.getDay()
  
  const dias = []
  
  // Dias vazios
  for (let i = 0; i < primeiroDiaSemana; i++) {
    dias.push({ dia: null })
  }
  
  const hoje = new Date()
  
  // Dias do mês
  for (let dia = 1; dia <= diasNoMes; dia++) {
    const data = new Date(props.ano, mes, dia)
    const ausencias = verificarAusencias(data)
    const ehHoje = data.toDateString() === hoje.toDateString()
    
    dias.push({
      dia,
      data: data.toISOString().split('T')[0],
      hoje: ehHoje,
      ausencias
    })
  }
  
  return dias
}

function verificarAusencias(data) {
  const dataStr = data.toISOString().split('T')[0]
  const ausencias = []
  
  for (const s of props.solicitacoes) {
    if (s.status === 'CANCELADO') continue
    
    if (dataStr >= s.dataInicio && dataStr <= s.dataFim) {
      ausencias.push({
        id: s.id,
        tipo: s.tipoAusenciaNome,
        cor: s.tipoAusenciaCor,
        status: s.status,
        usuario: s.usuarioNome,
        inicio: s.dataInicio,
        fim: s.dataFim,
        dias: s.diasTotal
      })
    }
  }
  
  return ausencias
}

function mostrarTooltip(event, dia) {
  if (dia.ausencias && dia.ausencias.length > 0) {
    tooltipData.value = dia
    tooltipPosition.value = {
      x: event.clientX + 10,
      y: event.clientY + 10
    }
  }
}

function esconderTooltip() {
  tooltipData.value = null
}

const diasSemana = ['D', 'S', 'T', 'Q', 'Q', 'S', 'S']
</script>

<template>
  <div class="card p-6">
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      <div v-for="mes in meses" :key="mes.nome" class="bg-dark-800/50 rounded-xl p-4">
        <h3 class="text-sm font-semibold text-white mb-3 text-center">{{ mes.nome }}</h3>
        
        <!-- Cabeçalho dias da semana -->
        <div class="grid grid-cols-7 gap-0.5 mb-1">
          <div 
            v-for="dia in diasSemana" 
            :key="dia" 
            class="text-center text-[10px] font-medium text-dark-500"
          >
            {{ dia }}
          </div>
        </div>

        <!-- Grid de dias -->
        <div class="grid grid-cols-7 gap-0.5">
          <div 
            v-for="(item, index) in mes.dias" 
            :key="index"
            @mouseenter="(e) => mostrarTooltip(e, item)"
            @mouseleave="esconderTooltip"
            :class="[
              'w-full aspect-square text-[10px] flex items-center justify-center rounded cursor-default transition-all',
              !item.dia ? 'bg-transparent' : 'bg-dark-700/30',
              item.hoje ? 'ring-1 ring-primary-500 ring-offset-1 ring-offset-dark-800' : '',
              item.ausencias?.length > 0 ? 'cursor-pointer hover:scale-110' : ''
            ]"
            :style="item.ausencias?.length > 0 ? { 
              backgroundColor: item.ausencias[0].cor + (item.ausencias[0].status === 'APROVADO' ? 'cc' : '66'),
              color: 'white'
            } : {}"
          >
            {{ item.dia }}
          </div>
        </div>
      </div>
    </div>

    <!-- Tooltip -->
    <Teleport to="body">
      <div 
        v-if="tooltipData"
        class="fixed z-50 p-3 bg-dark-800 border border-dark-600 rounded-lg shadow-xl max-w-xs"
        :style="{ left: tooltipPosition.x + 'px', top: tooltipPosition.y + 'px' }"
      >
        <div v-for="a in tooltipData.ausencias" :key="a.id" class="mb-2 last:mb-0">
          <div class="flex items-center gap-2 mb-1">
            <div 
              class="w-3 h-3 rounded-full"
              :style="{ backgroundColor: a.cor }"
            />
            <span class="font-medium text-white text-sm">{{ a.tipo }}</span>
          </div>
          <p v-if="mostrarUsuario" class="text-xs text-primary-400 mb-1">{{ a.usuario }}</p>
          <p class="text-xs text-dark-400">
            {{ new Date(a.inicio).toLocaleDateString('pt-BR') }} - 
            {{ new Date(a.fim).toLocaleDateString('pt-BR') }}
          </p>
          <p class="text-xs text-dark-500">{{ a.dias }} dias • {{ a.status }}</p>
        </div>
      </div>
    </Teleport>
  </div>
</template>
