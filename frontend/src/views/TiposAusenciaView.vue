<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api/axios'

const tipos = ref([])
const loading = ref(false)

// Form para novo tipo
const showForm = ref(false)
const form = ref({ nome: '', corHex: '#34D399', deduzSaldo: true, descricao: '' })

// Edição inline
const editando = ref(null)
const editForm = ref({ nome: '', corHex: '', deduzSaldo: true, descricao: '' })

onMounted(async () => {
  await carregarTipos()
})

async function carregarTipos() {
  loading.value = true
  try {
    const response = await api.get('/tipos-ausencia/todos')
    tipos.value = response.data
  } catch (e) {
    alert(e.response?.data?.message || 'Erro ao carregar tipos de ausência')
  } finally {
    loading.value = false
  }
}

// Criar
async function criarTipo() {
  if (!form.value.nome.trim()) {
    alert('Nome é obrigatório')
    return
  }
  try {
    const response = await api.post('/tipos-ausencia', form.value)
    tipos.value.push(response.data)
    form.value = { nome: '', corHex: '#34D399', deduzSaldo: true, descricao: '' }
    showForm.value = false
  } catch (e) {
    alert(e.response?.data?.message || 'Erro ao criar tipo')
  }
}

// Editar
function abrirEdicao(tipo) {
  editando.value = tipo.id
  editForm.value = {
    nome: tipo.nome,
    corHex: tipo.corHex,
    deduzSaldo: tipo.deduzSaldo,
    descricao: tipo.descricao || ''
  }
}

function cancelarEdicao() {
  editando.value = null
}

async function salvarEdicao(id) {
  if (!editForm.value.nome.trim()) {
    alert('Nome é obrigatório')
    return
  }
  try {
    const response = await api.put(`/tipos-ausencia/${id}`, editForm.value)
    const index = tipos.value.findIndex(t => t.id === id)
    if (index !== -1) tipos.value[index] = response.data
    editando.value = null
  } catch (e) {
    alert(e.response?.data?.message || 'Erro ao salvar')
  }
}

// Toggle ativo
async function toggleAtivo(id) {
  const tipo = tipos.value.find(t => t.id === id)
  const acao = tipo?.isAtivo ? 'desativar' : 'ativar'
  if (!confirm(`Tem certeza que deseja ${acao} este tipo de ausência?`)) return

  try {
    const response = await api.patch(`/tipos-ausencia/${id}/toggle-ativo`)
    const index = tipos.value.findIndex(t => t.id === id)
    if (index !== -1) tipos.value[index] = response.data
  } catch (e) {
    alert(e.response?.data?.message || 'Erro ao alterar status')
  }
}
</script>

<template>
  <div class="space-y-6 animate-fadeIn">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-white">Tipos de Ausência</h1>
        <p class="text-dark-400 mt-1">Gerencie os tipos de ausência disponíveis no sistema</p>
      </div>
      <button @click="showForm = !showForm" class="btn btn-primary">
        {{ showForm ? '✕ Cancelar' : '+ Novo Tipo' }}
      </button>
    </div>

    <!-- Form novo tipo -->
    <div v-if="showForm" class="card p-6">
      <h2 class="text-lg font-semibold text-white mb-4">Novo Tipo de Ausência</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="text-sm text-dark-400 mb-1 block">Nome *</label>
          <input v-model="form.nome" class="input" placeholder="Ex: Licença Paternidade" />
        </div>
        <div>
          <label class="text-sm text-dark-400 mb-1 block">Cor</label>
          <div class="flex items-center gap-3">
            <input v-model="form.corHex" type="color" class="w-10 h-10 rounded-lg cursor-pointer border-0 bg-transparent" />
            <input v-model="form.corHex" class="input flex-1" placeholder="#34D399" />
          </div>
        </div>
        <div>
          <label class="text-sm text-dark-400 mb-1 block">Descrição</label>
          <input v-model="form.descricao" class="input" placeholder="Descrição opcional" />
        </div>
        <div class="flex items-center gap-3 pt-6">
          <label class="flex items-center gap-2 cursor-pointer">
            <input v-model="form.deduzSaldo" type="checkbox" class="w-4 h-4 rounded bg-dark-700 border-dark-600 text-primary-600 focus:ring-primary-500" />
            <span class="text-sm text-dark-300">Deduz do saldo de férias</span>
          </label>
        </div>
      </div>
      <div class="flex justify-end mt-4">
        <button @click="criarTipo" class="btn btn-primary">Criar Tipo</button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="card p-12 text-center">
      <p class="text-dark-400">Carregando...</p>
    </div>

    <!-- Lista -->
    <div v-else-if="tipos.length === 0" class="card p-12 text-center">
      <div class="w-16 h-16 mx-auto rounded-full bg-dark-700 flex items-center justify-center mb-4">
        <svg class="w-8 h-8 text-dark-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
        </svg>
      </div>
      <p class="text-dark-400">Nenhum tipo de ausência cadastrado</p>
    </div>

    <div v-else class="space-y-3">
      <div
        v-for="tipo in tipos"
        :key="tipo.id"
        :class="['card p-5 transition-all', !tipo.isAtivo ? 'opacity-60' : '']"
      >
        <!-- Modo visualização -->
        <template v-if="editando !== tipo.id">
          <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div class="flex items-center gap-4">
              <div
                class="w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0"
                :style="{ backgroundColor: tipo.corHex + '20' }"
              >
                <div class="w-5 h-5 rounded-full" :style="{ backgroundColor: tipo.corHex }"></div>
              </div>
              <div>
                <div class="flex items-center gap-2">
                  <h3 class="text-lg font-semibold text-white">{{ tipo.nome }}</h3>
                  <span :class="['px-2 py-0.5 text-xs font-medium rounded-full', tipo.isAtivo ? 'bg-green-500/20 text-green-400' : 'bg-red-500/20 text-red-400']">
                    {{ tipo.isAtivo ? 'Ativo' : 'Inativo' }}
                  </span>
                  <span v-if="tipo.deduzSaldo" class="px-2 py-0.5 text-xs font-medium rounded-full bg-amber-500/20 text-amber-400">
                    Deduz saldo
                  </span>
                </div>
                <p v-if="tipo.descricao" class="text-sm text-dark-400 mt-1">{{ tipo.descricao }}</p>
                <p class="text-xs text-dark-500 mt-1">
                  Cor: <span :style="{ color: tipo.corHex }">{{ tipo.corHex }}</span>
                </p>
              </div>
            </div>

            <div class="flex items-center gap-2">
              <button @click="abrirEdicao(tipo)" class="btn btn-secondary text-sm px-3 py-1.5">
                ✏️ Editar
              </button>
              <button
                @click="toggleAtivo(tipo.id)"
                :class="['btn text-sm px-3 py-1.5', tipo.isAtivo ? 'btn-danger' : 'btn-primary']"
              >
                {{ tipo.isAtivo ? '🚫 Desativar' : '✅ Ativar' }}
              </button>
            </div>
          </div>
        </template>

        <!-- Modo edição -->
        <template v-else>
          <div class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="text-sm text-dark-400 mb-1 block">Nome</label>
                <input v-model="editForm.nome" class="input" />
              </div>
              <div>
                <label class="text-sm text-dark-400 mb-1 block">Cor</label>
                <div class="flex items-center gap-3">
                  <input v-model="editForm.corHex" type="color" class="w-10 h-10 rounded-lg cursor-pointer border-0 bg-transparent" />
                  <input v-model="editForm.corHex" class="input flex-1" />
                </div>
              </div>
              <div>
                <label class="text-sm text-dark-400 mb-1 block">Descrição</label>
                <input v-model="editForm.descricao" class="input" />
              </div>
              <div class="flex items-center gap-3 pt-6">
                <label class="flex items-center gap-2 cursor-pointer">
                  <input v-model="editForm.deduzSaldo" type="checkbox" class="w-4 h-4 rounded bg-dark-700 border-dark-600 text-primary-600 focus:ring-primary-500" />
                  <span class="text-sm text-dark-300">Deduz do saldo de férias</span>
                </label>
              </div>
            </div>
            <div class="flex justify-end gap-2">
              <button @click="cancelarEdicao" class="btn btn-secondary text-sm">Cancelar</button>
              <button @click="salvarEdicao(tipo.id)" class="btn btn-primary text-sm">Salvar</button>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>
