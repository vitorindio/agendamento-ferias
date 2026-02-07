<script setup>
import { ref, onMounted, computed } from 'vue'
import { useSolicitacoesStore } from '@/stores/solicitacoes'
import CalendarioAnual from '@/components/CalendarioAnual.vue'
import LegendaCores from '@/components/LegendaCores.vue'

const solicitacoesStore = useSolicitacoesStore()

const anoSelecionado = ref(new Date().getFullYear())
const anos = [2024, 2025, 2026, 2027]

onMounted(async () => {
  await Promise.all([
    solicitacoesStore.carregarMinhasSolicitacoes(anoSelecionado.value),
    solicitacoesStore.carregarSaldo(anoSelecionado.value),
    solicitacoesStore.carregarTiposAusencia()
  ])
})

async function mudarAno(ano) {
  anoSelecionado.value = ano
  await Promise.all([
    solicitacoesStore.carregarMinhasSolicitacoes(ano),
    solicitacoesStore.carregarSaldo(ano)
  ])
}
</script>

<template>
  <div class="space-y-6 animate-fadeIn">
    <!-- Header -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-white">Minha Agenda</h1>
        <p class="text-dark-400 mt-1">Visualize suas férias e ausências no calendário anual</p>
      </div>

      <!-- Seletor de Ano -->
      <div class="flex items-center gap-2">
        <span class="text-sm text-dark-400">Ano:</span>
        <div class="flex gap-1">
          <button
            v-for="ano in anos"
            :key="ano"
            @click="mudarAno(ano)"
            :class="[
              'px-3 py-1.5 text-sm font-medium rounded-lg transition-all',
              anoSelecionado === ano 
                ? 'bg-primary-600 text-white' 
                : 'bg-dark-700 text-dark-300 hover:bg-dark-600'
            ]"
          >
            {{ ano }}
          </button>
        </div>
      </div>
    </div>

    <!-- Saldo do Ano -->
    <div class="card p-4">
      <div class="flex flex-wrap items-center gap-6">
        <div class="flex items-center gap-2">
          <div class="w-10 h-10 rounded-lg bg-primary-500/20 flex items-center justify-center">
            <span class="text-lg">📅</span>
          </div>
          <div>
            <p class="text-xs text-dark-400">Disponível</p>
            <p class="text-xl font-bold text-primary-400">
              {{ solicitacoesStore.saldo?.diasDisponiveis || 0 }} dias
            </p>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <div class="w-10 h-10 rounded-lg bg-blue-500/20 flex items-center justify-center">
            <span class="text-lg">📊</span>
          </div>
          <div>
            <p class="text-xs text-dark-400">Total {{ anoSelecionado }}</p>
            <p class="text-xl font-bold text-blue-400">
              {{ solicitacoesStore.saldo?.diasTotais || 30 }} dias
            </p>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <div class="w-10 h-10 rounded-lg bg-amber-500/20 flex items-center justify-center">
            <span class="text-lg">✓</span>
          </div>
          <div>
            <p class="text-xs text-dark-400">Usados</p>
            <p class="text-xl font-bold text-amber-400">
              {{ solicitacoesStore.saldo?.diasUsados || 0 }} dias
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Legenda -->
    <LegendaCores :tipos="solicitacoesStore.tiposAusencia" />

    <!-- Calendário Anual -->
    <CalendarioAnual 
      :ano="anoSelecionado" 
      :solicitacoes="solicitacoesStore.minhasSolicitacoes"
    />
  </div>
</template>
