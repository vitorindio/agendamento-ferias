<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const senha = ref('')
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  error.value = ''
  loading.value = true

  const result = await authStore.login(email.value, senha.value)
  
  loading.value = false

  if (result.success) {
    router.push({ name: 'dashboard' })
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
        <h1 class="text-2xl font-bold text-white">Sistema de Férias</h1>
        <p class="text-dark-400 mt-2">Faça login para continuar</p>
      </div>

      <!-- Form -->
      <div class="card p-8">
        <form @submit.prevent="handleLogin" class="space-y-6">
          <div v-if="error" class="p-4 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400 text-sm">
            {{ error }}
          </div>

          <div>
            <label class="block text-sm font-medium text-dark-300 mb-2">Email</label>
            <input 
              v-model="email"
              type="email" 
              class="input"
              placeholder="seu@email.com"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-dark-300 mb-2">Senha</label>
            <input 
              v-model="senha"
              type="password" 
              class="input"
              placeholder="••••••••"
              required
            />
          </div>

          <button 
            type="submit" 
            class="btn btn-primary w-full py-3"
            :disabled="loading"
          >
            <span v-if="loading">Entrando...</span>
            <span v-else>Entrar</span>
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-dark-400 text-sm">
            Não tem uma conta?
            <RouterLink to="/register" class="text-primary-400 hover:text-primary-300">
              Criar conta
            </RouterLink>
          </p>
        </div>
      </div>

      <!-- Demo credentials -->
      <div class="mt-6 p-4 bg-dark-800/50 rounded-lg border border-dark-700/50">
        <p class="text-xs text-dark-400 text-center mb-2">Credenciais de teste:</p>
        <div class="grid grid-cols-2 gap-2 text-xs text-dark-300">
          <div><strong>Gestor:</strong> gestor@empresa.com</div>
          <div><strong>Senha:</strong> 123456</div>
          <div><strong>User:</strong> joao@empresa.com</div>
          <div><strong>Senha:</strong> 123456</div>
        </div>
      </div>
    </div>
  </div>
</template>
