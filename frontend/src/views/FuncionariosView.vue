<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/api/axios'

const usuarios = ref([])
const loading = ref(false)
const filtro = ref('TODOS') // TODOS, ATIVOS, INATIVOS

// Modal de edição
const editando = ref(null)
const editForm = ref({ nomeCompleto: '', email: '', cargo: '', dataAdmissao: '' })

// Modal de saldo
const saldoModal = ref(null)
const saldoData = ref(null)
const saldoAno = ref(new Date().getFullYear())
const saldoEditando = ref(false)
const novoDiasTotais = ref(30)

onMounted(async () => {
  await carregarUsuarios()
})

async function carregarUsuarios() {
  loading.value = true
  try {
    const response = await api.get('/usuarios')
    usuarios.value = response.data
  } catch (e) {
    alert(e.response?.data?.message || 'Erro ao carregar funcionários')
  } finally {
    loading.value = false
  }
}

const usuariosFiltrados = computed(() => {
  if (filtro.value === 'ATIVOS') return usuarios.value.filter(u => u.isAtivo)
  if (filtro.value === 'INATIVOS') return usuarios.value.filter(u => !u.isAtivo)
  return usuarios.value
})

// Edição
function abrirEdicao(usuario) {
  editando.value = usuario.id
  editForm.value = {
    nomeCompleto: usuario.nomeCompleto,
    email: usuario.email,
    cargo: usuario.cargo || '',
    dataAdmissao: usuario.dataAdmissao || ''
  }
}

function cancelarEdicao() {
  editando.value = null
}

async function salvarEdicao(id) {
  try {
    const response = await api.put(`/usuarios/${id}`, editForm.value)
    const index = usuarios.value.findIndex(u => u.id === id)
    if (index !== -1) usuarios.value[index] = response.data
    editando.value = null
  } catch (e) {
    alert(e.response?.data?.message || 'Erro ao salvar')
  }
}

// Toggle ativo
async function toggleAtivo(id) {
  const usuario = usuarios.value.find(u => u.id === id)
  const acao = usuario?.isAtivo ? 'desativar' : 'ativar'
  if (!confirm(`Tem certeza que deseja ${acao} este funcionário?`)) return

  try {
    const response = await api.patch(`/usuarios/${id}/toggle-ativo`)
    const index = usuarios.value.findIndex(u => u.id === id)
    if (index !== -1) usuarios.value[index] = response.data
  } catch (e) {
    alert(e.response?.data?.message || 'Erro ao alterar status')
  }
}

// Alterar role
async function alterarRole(id, novaRole) {
  const label = novaRole === 'GESTOR' ? 'promover a Gestor' : 'rebaixar a Funcionário'
  if (!confirm(`Tem certeza que deseja ${label}?`)) return

  try {
    const response = await api.patch(`/usuarios/${id}/role`, { role: novaRole })
    const index = usuarios.value.findIndex(u => u.id === id)
    if (index !== -1) usuarios.value[index] = response.data
  } catch (e) {
    alert(e.response?.data?.message || 'Erro ao alterar role')
  }
}

// Saldo
async function abrirSaldo(usuario) {
  saldoModal.value = usuario
  saldoEditando.value = false
  await carregarSaldo(usuario.id, saldoAno.value)
}

async function carregarSaldo(userId, ano) {
  try {
    const response = await api.get(`/usuarios/${userId}/saldo/${ano}`)
    saldoData.value = response.data
    novoDiasTotais.value = response.data.diasTotais
  } catch (e) {
    saldoData.value = null
  }
}

async function mudarAnoSaldo(ano) {
  saldoAno.value = ano
  saldoEditando.value = false
  if (saldoModal.value) {
    await carregarSaldo(saldoModal.value.id, ano)
  }
}

async function salvarSaldo() {
  try {
    const response = await api.put(`/usuarios/${saldoModal.value.id}/saldo/${saldoAno.value}`, {
      diasTotais: novoDiasTotais.value
    })
    saldoData.value = response.data
    saldoEditando.value = false
  } catch (e) {
    alert(e.response?.data?.message || 'Erro ao ajustar saldo')
  }
}

function fecharSaldo() {
  saldoModal.value = null
  saldoData.value = null
}

const roleConfig = {
  USER: { label: 'Funcionário', class: 'bg-blue-500/20 text-blue-400' },
  GESTOR: { label: 'Gestor', class: 'bg-purple-500/20 text-purple-400' }
}
</script>

<template>
  <div class="space-y-6 animate-fadeIn">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-white">Gestão de Funcionários</h1>
        <p class="text-dark-400 mt-1">Gerencie os funcionários do sistema</p>
      </div>
    </div>

    <!-- Filtros -->
    <div class="card p-4">
      <div class="flex flex-wrap items-center gap-4">
        <span class="text-sm text-dark-400">Filtrar:</span>
        <div class="flex gap-1">
          <button
            v-for="f in ['TODOS', 'ATIVOS', 'INATIVOS']"
            :key="f"
            @click="filtro = f"
            :class="[
              'px-3 py-1.5 text-xs font-medium rounded-lg transition-all',
              filtro === f 
                ? 'bg-primary-600 text-white' 
                : 'bg-dark-700 text-dark-300 hover:bg-dark-600'
            ]"
          >
            {{ f === 'TODOS' ? `Todos (${usuarios.length})` : f === 'ATIVOS' ? `Ativos (${usuarios.filter(u => u.isAtivo).length})` : `Inativos (${usuarios.filter(u => !u.isAtivo).length})` }}
          </button>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="card p-12 text-center">
      <p class="text-dark-400">Carregando...</p>
    </div>

    <!-- Empty -->
    <div v-else-if="usuariosFiltrados.length === 0" class="card p-12 text-center">
      <div class="w-16 h-16 mx-auto rounded-full bg-dark-700 flex items-center justify-center mb-4">
        <svg class="w-8 h-8 text-dark-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
      </div>
      <p class="text-dark-400">Nenhum funcionário encontrado</p>
    </div>

    <!-- Tabela -->
    <div v-else class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-dark-700/50">
              <th class="text-left px-6 py-4 text-xs font-medium text-dark-400 uppercase tracking-wider">Funcionário</th>
              <th class="text-left px-6 py-4 text-xs font-medium text-dark-400 uppercase tracking-wider">Cargo</th>
              <th class="text-left px-6 py-4 text-xs font-medium text-dark-400 uppercase tracking-wider">Role</th>
              <th class="text-left px-6 py-4 text-xs font-medium text-dark-400 uppercase tracking-wider">Status</th>
              <th class="text-left px-6 py-4 text-xs font-medium text-dark-400 uppercase tracking-wider">Admissão</th>
              <th class="text-right px-6 py-4 text-xs font-medium text-dark-400 uppercase tracking-wider">Ações</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-dark-700/30">
            <tr v-for="u in usuariosFiltrados" :key="u.id" class="hover:bg-dark-700/20 transition-colors">
              <!-- Modo visualização -->
              <template v-if="editando !== u.id">
                <td class="px-6 py-4">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-primary-600/20 flex items-center justify-center text-primary-400 font-medium flex-shrink-0">
                      {{ u.nomeCompleto?.charAt(0) || '?' }}
                    </div>
                    <div>
                      <p class="text-sm font-medium text-white">{{ u.nomeCompleto }}</p>
                      <p class="text-xs text-dark-400">{{ u.email }}</p>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 text-sm text-dark-300">{{ u.cargo || '—' }}</td>
                <td class="px-6 py-4">
                  <span :class="['px-2 py-1 text-xs font-medium rounded-full', roleConfig[u.role]?.class]">
                    {{ roleConfig[u.role]?.label || u.role }}
                  </span>
                </td>
                <td class="px-6 py-4">
                  <span :class="['px-2 py-1 text-xs font-medium rounded-full', u.isAtivo ? 'bg-green-500/20 text-green-400' : 'bg-red-500/20 text-red-400']">
                    {{ u.isAtivo ? 'Ativo' : 'Inativo' }}
                  </span>
                </td>
                <td class="px-6 py-4 text-sm text-dark-300">
                  {{ u.dataAdmissao ? new Date(u.dataAdmissao + 'T00:00:00').toLocaleDateString('pt-BR') : '—' }}
                </td>
                <td class="px-6 py-4">
                  <div class="flex items-center justify-end gap-2">
                    <button @click="abrirEdicao(u)" class="p-1.5 text-dark-400 hover:text-primary-400 transition-colors" title="Editar">
                      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button @click="abrirSaldo(u)" class="p-1.5 text-dark-400 hover:text-amber-400 transition-colors" title="Ver Saldo">
                      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                    </button>
                    <button 
                      @click="alterarRole(u.id, u.role === 'GESTOR' ? 'USER' : 'GESTOR')"
                      class="p-1.5 text-dark-400 hover:text-purple-400 transition-colors"
                      :title="u.role === 'GESTOR' ? 'Rebaixar a Funcionário' : 'Promover a Gestor'"
                    >
                      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
                      </svg>
                    </button>
                    <button 
                      @click="toggleAtivo(u.id)"
                      :class="['p-1.5 transition-colors', u.isAtivo ? 'text-dark-400 hover:text-red-400' : 'text-dark-400 hover:text-green-400']"
                      :title="u.isAtivo ? 'Desativar' : 'Ativar'"
                    >
                      <svg v-if="u.isAtivo" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                      </svg>
                      <svg v-else class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                    </button>
                  </div>
                </td>
              </template>

              <!-- Modo edição inline -->
              <template v-else>
                <td class="px-6 py-3" colspan="5">
                  <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
                    <div>
                      <label class="text-xs text-dark-400 mb-1 block">Nome</label>
                      <input v-model="editForm.nomeCompleto" class="input py-1.5 text-sm" />
                    </div>
                    <div>
                      <label class="text-xs text-dark-400 mb-1 block">Email</label>
                      <input v-model="editForm.email" class="input py-1.5 text-sm" type="email" />
                    </div>
                    <div>
                      <label class="text-xs text-dark-400 mb-1 block">Cargo</label>
                      <input v-model="editForm.cargo" class="input py-1.5 text-sm" />
                    </div>
                    <div>
                      <label class="text-xs text-dark-400 mb-1 block">Admissão</label>
                      <input v-model="editForm.dataAdmissao" class="input py-1.5 text-sm" type="date" />
                    </div>
                  </div>
                </td>
                <td class="px-6 py-3">
                  <div class="flex items-center justify-end gap-2">
                    <button @click="salvarEdicao(u.id)" class="btn btn-primary text-xs px-3 py-1.5">Salvar</button>
                    <button @click="cancelarEdicao()" class="btn btn-secondary text-xs px-3 py-1.5">Cancelar</button>
                  </div>
                </td>
              </template>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal de Saldo -->
    <div v-if="saldoModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/60" @click="fecharSaldo"></div>
      <div class="relative card p-6 w-full max-w-md">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-lg font-bold text-white">Saldo de Férias</h2>
            <p class="text-sm text-dark-400">{{ saldoModal.nomeCompleto }}</p>
          </div>
          <button @click="fecharSaldo" class="p-1 text-dark-400 hover:text-white transition-colors">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Seletor de ano -->
        <div class="flex items-center gap-2 mb-4">
          <span class="text-sm text-dark-400">Ano:</span>
          <select v-model="saldoAno" @change="mudarAnoSaldo(saldoAno)" class="input w-auto py-1.5 text-sm">
            <option v-for="ano in [2024, 2025, 2026, 2027]" :key="ano" :value="ano">{{ ano }}</option>
          </select>
        </div>

        <div v-if="saldoData" class="space-y-4">
          <!-- Info do saldo -->
          <div class="grid grid-cols-3 gap-3">
            <div class="bg-dark-700/50 rounded-lg p-3 text-center">
              <p class="text-xs text-dark-400 mb-1">Total</p>
              <p class="text-xl font-bold text-white">{{ saldoData.diasTotais }}</p>
            </div>
            <div class="bg-dark-700/50 rounded-lg p-3 text-center">
              <p class="text-xs text-dark-400 mb-1">Usados</p>
              <p class="text-xl font-bold text-amber-400">{{ saldoData.diasUsados }}</p>
            </div>
            <div class="bg-dark-700/50 rounded-lg p-3 text-center">
              <p class="text-xs text-dark-400 mb-1">Disponíveis</p>
              <p class="text-xl font-bold text-green-400">{{ saldoData.diasDisponiveis }}</p>
            </div>
          </div>

          <!-- Editar saldo -->
          <div v-if="!saldoEditando">
            <button @click="saldoEditando = true" class="btn btn-secondary text-sm w-full">
              ✏️ Ajustar Saldo
            </button>
          </div>
          <div v-else class="space-y-3">
            <div>
              <label class="text-xs text-dark-400 mb-1 block">Dias Totais</label>
              <input v-model.number="novoDiasTotais" type="number" min="0" max="60" class="input py-1.5" />
            </div>
            <div class="flex gap-2">
              <button @click="salvarSaldo" class="btn btn-primary text-sm flex-1">Salvar</button>
              <button @click="saldoEditando = false" class="btn btn-secondary text-sm flex-1">Cancelar</button>
            </div>
          </div>
        </div>
        <div v-else class="text-center py-6 text-dark-400">
          Carregando saldo...
        </div>
      </div>
    </div>
  </div>
</template>
