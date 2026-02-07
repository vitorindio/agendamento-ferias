import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api/axios'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user')) || null)
  const token = ref(localStorage.getItem('token') || null)

  const isAuthenticated = computed(() => !!token.value)
  const isManager = computed(() => user.value?.role === 'GESTOR')

  async function login(email, senha) {
    try {
      const response = await api.post('/auth/login', { email, senha })
      const data = response.data
      
      token.value = data.token
      user.value = {
        id: data.id,
        nomeCompleto: data.nomeCompleto,
        email: data.email,
        role: data.role
      }
      
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(user.value))
      
      return { success: true }
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Erro ao fazer login'
      }
    }
  }

  async function register(dados) {
    try {
      // Limpar campos vazios antes de enviar
      const payload = { ...dados }
      if (!payload.cargo || payload.cargo.trim() === '') {
        delete payload.cargo
      }
      if (!payload.dataAdmissao || payload.dataAdmissao.trim() === '') {
        delete payload.dataAdmissao
      }
      
      const response = await api.post('/auth/register', payload)
      return { success: true, message: response.data.message }
    } catch (error) {
      // Tratar erros de validação
      if (error.response?.data?.errors) {
        const errors = error.response.data.errors
        const errorMessages = Object.values(errors).join(', ')
        return { 
          success: false, 
          error: errorMessages || 'Erro de validação'
        }
      }
      return { 
        success: false, 
        error: error.response?.data?.message || error.message || 'Erro ao criar conta'
      }
    }
  }

  async function confirmEmail(tokenConfirmacao) {
    try {
      const response = await api.get(`/auth/confirm?token=${tokenConfirmacao}`)
      return { success: true, message: response.data.message }
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Erro ao confirmar email'
      }
    }
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return {
    user,
    token,
    isAuthenticated,
    isManager,
    login,
    register,
    confirmEmail,
    logout
  }
})
