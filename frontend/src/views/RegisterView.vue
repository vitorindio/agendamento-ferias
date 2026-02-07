<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  nomeCompleto: '',
  email: '',
  senha: '',
  cargo: '',
  dataAdmissao: ''
})

const loading = ref(false)
const error = ref('')
const success = ref('')

async function handleRegister() {
  error.value = ''
  success.value = ''
  loading.value = true

  const result = await authStore.register(form.value)
  
  loading.value = false

  if (result.success) {
    success.value = result.message
    setTimeout(() => {
      router.push({ name: 'login' })
    }, 3000)
  } else {
    error.value = result.error
  }
}
</script>

<template>
  <div class="min-h-screen bg-dark-950 flex items-center justify-center p-4">
    <div class="w-full max-w-md">
      <!-- Logo -->
      <div class="text-center mb-8">
        <div class="w-16 h-16 mx-auto rounded-2xl bg-primary-600 flex items-center justify-center mb-4">
          <svg class="w-10 h-10 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        </div>
        <h1 class="text-2xl font-bold text-white">Criar Conta</h1>
        <p class="text-dark-400 mt-2">Preencha os dados para se cadastrar</p>
      </div>

      <!-- Form -->
      <div class="card p-8">
        <form @submit.prevent="handleRegister" class="space-y-5">
          <div v-if="error" class="p-4 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400 text-sm">
            {{ error }}
          </div>

          <div v-if="success" class="p-4 bg-green-500/10 border border-green-500/20 rounded-lg text-green-400 text-sm">
            {{ success }}
          </div>

          <div>
            <label class="block text-sm font-medium text-dark-300 mb-2">Nome Completo</label>
            <input 
              v-model="form.nomeCompleto"
              type="text" 
              class="input"
              placeholder="Seu nome completo"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-dark-300 mb-2">Email</label>
            <input 
              v-model="form.email"
              type="email" 
              class="input"
              placeholder="seu@email.com"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-dark-300 mb-2">Senha</label>
            <input 
              v-model="form.senha"
              type="password" 
              class="input"
              placeholder="Mínimo 6 caracteres"
              minlength="6"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-dark-300 mb-2">Cargo</label>
            <input 
              v-model="form.cargo"
              type="text" 
              class="input"
              placeholder="Ex: Desenvolvedor"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-dark-300 mb-2">Data de Admissão</label>
            <input 
              v-model="form.dataAdmissao"
              type="date" 
              class="input"
            />
          </div>

          <button 
            type="submit" 
            class="btn btn-primary w-full py-3"
            :disabled="loading"
          >
            <span v-if="loading">Criando conta...</span>
            <span v-else>Criar Conta</span>
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-dark-400 text-sm">
            Já tem uma conta?
            <RouterLink to="/login" class="text-primary-400 hover:text-primary-300">
              Fazer login
            </RouterLink>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
