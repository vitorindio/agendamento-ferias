<script setup>
import { ref, computed, onMounted } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useSolicitacoesStore } from '@/stores/solicitacoes'

const route = useRoute()
const authStore = useAuthStore()
const solicitacoesStore = useSolicitacoesStore()

const sidebarOpen = ref(true)

// Carregar pendentes ao montar se for gestor
onMounted(async () => {
  if (authStore.isManager) {
    await solicitacoesStore.carregarPendentes()
  }
})

const menuItems = computed(() => {
  const items = [
    { name: 'Dashboard', route: 'dashboard', icon: 'dashboard' },
    { name: 'Minha Agenda', route: 'minha-agenda', icon: 'calendar' },
    { name: 'Nova Solicitação', route: 'nova-solicitacao', icon: 'add' },
    { name: 'Minhas Solicitações', route: 'minhas-solicitacoes', icon: 'list' }
  ]
  
  if (authStore.isManager) {
    items.push(
      { name: 'Minha Equipe', route: 'equipe', icon: 'group' },
      { name: 'Aprovações', route: 'aprovacoes', icon: 'check', badge: solicitacoesStore.solicitacoesPendentes.length },
      { name: 'Funcionários', route: 'funcionarios', icon: 'users' },
      { name: 'Tipos de Ausência', route: 'tipos-ausencia', icon: 'tag' }
    )
  }
  
  return items
})

const icons = {
  dashboard: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />`,
  calendar: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />`,
  add: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />`,
  list: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />`,
  group: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />`,
  check: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />`,
  users: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />`,
  tag: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />`
}

function handleLogout() {
  authStore.logout()
}
</script>

<template>
  <div class="min-h-screen bg-dark-950 flex">
    <!-- Sidebar -->
    <aside 
      :class="[
        'fixed inset-y-0 left-0 z-50 flex flex-col bg-dark-900 border-r border-dark-700/50 transition-all duration-300',
        sidebarOpen ? 'w-64' : 'w-20'
      ]"
    >
      <!-- Logo -->
      <div class="flex items-center h-16 px-4 border-b border-dark-700/50">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-primary-600 flex items-center justify-center">
            <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
          <span v-if="sidebarOpen" class="font-semibold text-lg text-white">Sistema Férias</span>
        </div>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 py-6 px-3 space-y-1 overflow-y-auto">
        <RouterLink
          v-for="item in menuItems"
          :key="item.route"
          :to="{ name: item.route }"
          :class="[
            'sidebar-link',
            route.name === item.route ? 'active' : ''
          ]"
        >
          <svg class="w-5 h-5 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" v-html="icons[item.icon]" />
          <span v-if="sidebarOpen" class="flex-1">{{ item.name }}</span>
          <span 
            v-if="sidebarOpen && item.badge" 
            class="px-2 py-0.5 text-xs font-medium bg-red-500 text-white rounded-full"
          >
            {{ item.badge }}
          </span>
        </RouterLink>
      </nav>

      <!-- User Menu -->
      <div class="border-t border-dark-700/50 p-4">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-full bg-primary-600/20 flex items-center justify-center text-primary-400 font-medium">
            {{ authStore.user?.nomeCompleto?.charAt(0) || 'U' }}
          </div>
          <div v-if="sidebarOpen" class="flex-1 min-w-0">
            <p class="text-sm font-medium text-white truncate">{{ authStore.user?.nomeCompleto }}</p>
            <p class="text-xs text-dark-400 truncate">{{ authStore.user?.email }}</p>
          </div>
          <button 
            @click="handleLogout"
            class="p-2 text-dark-400 hover:text-red-400 transition-colors"
            title="Sair"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
          </button>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <main :class="['flex-1 transition-all duration-300', sidebarOpen ? 'ml-64' : 'ml-20']">
      <!-- Topbar -->
      <header class="h-16 border-b border-dark-700/50 bg-dark-900/50 backdrop-blur-sm flex items-center px-6 sticky top-0 z-40">
        <button 
          @click="sidebarOpen = !sidebarOpen"
          class="p-2 text-dark-400 hover:text-white transition-colors"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
        
        <div class="flex-1" />
        
        <div class="flex items-center gap-4">
          <span class="text-sm text-dark-400">
            {{ new Date().toLocaleDateString('pt-BR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}
          </span>
        </div>
      </header>

      <!-- Page Content -->
      <div class="p-6">
        <RouterView />
      </div>
    </main>
  </div>
</template>
