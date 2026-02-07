<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useSolicitacoesStore } from '@/stores/solicitacoes'
import SaldoCard from '@/components/SaldoCard.vue'
import ProximasAusencias from '@/components/ProximasAusencias.vue'
import MiniCalendario from '@/components/MiniCalendario.vue'

const authStore = useAuthStore()
const solicitacoesStore = useSolicitacoesStore()

const anoAtual = new Date().getFullYear()

onMounted(async () => {
  await Promise.all([
    solicitacoesStore.carregarSaldo(anoAtual),
    solicitacoesStore.carregarMinhasSolicitacoes(anoAtual),
    solicitacoesStore.carregarTiposAusencia()
  ])
  
  if (authStore.isManager) {
    await solicitacoesStore.carregarPendentes()
  }
})

const proximasSolicitacoes = computed(() => {
  const hoje = new Date()
  return solicitacoesStore.minhasSolicitacoes
    .filter(s => new Date(s.dataInicio) >= hoje && s.status !== 'CANCELADO')
    .sort((a, b) => new Date(a.dataInicio) - new Date(b.dataInicio))
    .slice(0, 5)
})
</script>

<template>
  <div class="space-y-6 animate-fadeIn">
    <!-- Header -->
    <div>
      <h1 class="text-2xl font-bold text-white">
        Olá, {{ authStore.user?.nomeCompleto?.split(' ')[0] }}! 👋
      </h1>
      <p class="text-dark-400 mt-1">Confira seu resumo de férias e ausências</p>
    </div>

    <!-- Saldo Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <SaldoCard 
        titulo="Dias Disponíveis"
        :valor="solicitacoesStore.saldo?.diasDisponiveis || 0"
        cor="primary"
        icone="disponivel"
      />
      <SaldoCard 
        titulo="Total do Ano"
        :valor="solicitacoesStore.saldo?.diasTotais || 30"
        cor="blue"
        icone="total"
      />
      <SaldoCard 
        titulo="Dias Usados"
        :valor="solicitacoesStore.saldo?.diasUsados || 0"
        cor="amber"
        icone="usados"
      />
    </div>

    <!-- Pendentes (Gestor) -->
    <div v-if="authStore.isManager && solicitacoesStore.solicitacoesPendentes.length > 0" class="card p-6">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-semibold text-white">⏳ Solicitações Pendentes</h2>
        <RouterLink to="/aprovacoes" class="text-sm text-primary-400 hover:text-primary-300">
          Ver todas →
        </RouterLink>
      </div>
      <div class="space-y-3">
        <div 
          v-for="s in solicitacoesStore.solicitacoesPendentes.slice(0, 3)" 
          :key="s.id"
          class="flex items-center justify-between p-3 bg-dark-700/30 rounded-lg"
        >
          <div>
            <p class="font-medium text-white">{{ s.usuarioNome }}</p>
            <p class="text-sm text-dark-400">
              {{ new Date(s.dataInicio).toLocaleDateString('pt-BR') }} - 
              {{ new Date(s.dataFim).toLocaleDateString('pt-BR') }}
              ({{ s.diasTotal }} dias)
            </p>
          </div>
          <span 
            class="px-3 py-1 text-xs font-medium rounded-full"
            :style="{ backgroundColor: s.tipoAusenciaCor + '20', color: s.tipoAusenciaCor }"
          >
            {{ s.tipoAusenciaNome }}
          </span>
        </div>
      </div>
    </div>

    <!-- Main Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Mini Calendário -->
      <MiniCalendario :solicitacoes="solicitacoesStore.minhasSolicitacoes" />

      <!-- Próximas Ausências -->
      <ProximasAusencias :solicitacoes="proximasSolicitacoes" />
    </div>
  </div>
</template>
