<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const success = ref(false)
const message = ref('')

onMounted(async () => {
  const token = route.query.token
  
  if (!token) {
    message.value = 'Token de confirmação não fornecido'
    loading.value = false
    return
  }

  const result = await authStore.confirmEmail(token)
  loading.value = false
  success.value = result.success
  message.value = result.success ? result.message : result.error
  
  if (result.success) {
    setTimeout(() => {
      router.push({ name: 'login' })
    }, 3000)
  }
})
</script>

<template>
  <div class="min-h-screen bg-dark-950 flex items-center justify-center p-4">
    <div class="w-full max-w-md text-center">
      <div class="card p-8">
        <!-- Loading -->
        <div v-if="loading" class="py-8">
          <div class="w-16 h-16 mx-auto rounded-full border-4 border-dark-700 border-t-primary-500 animate-spin" />
          <p class="mt-4 text-dark-300">Confirmando seu email...</p>
        </div>

        <!-- Success -->
        <div v-else-if="success" class="py-8">
          <div class="w-16 h-16 mx-auto rounded-full bg-green-500/20 flex items-center justify-center">
            <svg class="w-10 h-10 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h2 class="mt-4 text-xl font-semibold text-white">Email Confirmado!</h2>
          <p class="mt-2 text-dark-400">{{ message }}</p>
          <p class="mt-4 text-sm text-dark-500">Redirecionando para o login...</p>
        </div>

        <!-- Error -->
        <div v-else class="py-8">
          <div class="w-16 h-16 mx-auto rounded-full bg-red-500/20 flex items-center justify-center">
            <svg class="w-10 h-10 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </div>
          <h2 class="mt-4 text-xl font-semibold text-white">Erro na Confirmação</h2>
          <p class="mt-2 text-dark-400">{{ message }}</p>
          <RouterLink to="/login" class="btn btn-primary mt-6 inline-block">
            Ir para Login
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>
