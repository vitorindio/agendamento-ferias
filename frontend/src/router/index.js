import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/confirmar-email',
      name: 'confirmar-email',
      component: () => import('@/views/ConfirmarEmailView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('@/views/DashboardView.vue')
        },
        {
          path: 'minha-agenda',
          name: 'minha-agenda',
          component: () => import('@/views/MinhaAgendaView.vue')
        },
        {
          path: 'nova-solicitacao',
          name: 'nova-solicitacao',
          component: () => import('@/views/NovaSolicitacaoView.vue')
        },
        {
          path: 'minhas-solicitacoes',
          name: 'minhas-solicitacoes',
          component: () => import('@/views/MinhasSolicitacoesView.vue')
        },
        {
          path: 'equipe',
          name: 'equipe',
          component: () => import('@/views/EquipeView.vue'),
          meta: { requiresManager: true }
        },
        {
          path: 'aprovacoes',
          name: 'aprovacoes',
          component: () => import('@/views/AprovacoesView.vue'),
          meta: { requiresManager: true }
        },
        {
          path: 'funcionarios',
          name: 'funcionarios',
          component: () => import('@/views/FuncionariosView.vue'),
          meta: { requiresManager: true }
        },
        {
          path: 'tipos-ausencia',
          name: 'tipos-ausencia',
          component: () => import('@/views/TiposAusenciaView.vue'),
          meta: { requiresManager: true }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login' })
  } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next({ name: 'dashboard' })
  } else if (to.meta.requiresManager && !authStore.isManager) {
    next({ name: 'dashboard' })
  } else {
    next()
  }
})

export default router
