import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '@/views/home/index.vue'
import Layout from '@/layout/index.vue'
import NotFound from '@/views/404/404.vue'
import Article from '@/views/article/index.vue'
import Archive from '@/views/archives/index.vue'
import Categories from '@/views/categories/index.vue'
import Tags from '@/views/tags/index.vue'
import Messages from '@/views/messages/index.vue'
import About from '@/views/about/index.vue'
import Photos from '@/views/photos/index.vue'
import Terminal from '@/views/terminal/index.vue'
import Note from '@/views/note/index.vue'
import Study from '@/views/study/index.vue'
import Life from '@/views/life/index.vue'
import store from '@/store';

Vue.use(VueRouter)

const routes = [
    // 终端入口界面（全屏，不使用Layout）
    {
        path: '/',
        name: 'Terminal',
        component: Terminal,
        meta: {
            title: 'SHANG - 终端入口',
            fullscreen: true,
            hidden: true
        }
    },
    // 登录页面（全屏，不使用Layout）
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/index.vue'),
        meta: {
            title: '登录',
            hidden: true,
            fullscreen: true
        }
    },
    // 主应用路由（使用Layout）
    {
        path: "/",
        component: Layout,
        meta: {
            title: "SHANG的博客后台",
            loading: true
        },
        children: [
            {
                path: '/blog',
                name: 'Blog',
                component: Home,
                meta: {
                    title: '博客首页 - SHANG-BLOG',
                    transition: 'fade',
                    icon: 'fas fa-home',
                    loading: true
                 }
              },
              {
                path: '/note',
                name: 'Note',
                component: Note,
                meta: {
                    title: '笔记 - SHANG-BLOG',
                    transition: 'fade',
                    icon: 'fas fa-sticky-note',
                    hidden: true
                 }
              },
              {
                path: '/study',
                name: 'Study',
                component: Study,
                meta: {
                    title: '学习 - SHANG-BLOG',
                    transition: 'fade',
                    icon: 'fas fa-book',
                    hidden: true
                 }
              },
              {
                path: '/life',
                name: 'Life',
                component: Life,
                meta: {
                    title: '生活 & 爱好 - SHANG-BLOG',
                    transition: 'fade',
                    icon: 'fas fa-heart',
                    hidden: true
                 }
              },
              {
                path: '/archive',
                name: 'Archive',
                component: Archive,
                meta: { 
                  transition: 'fade',
                  title: '归档 - SHANG-BLOG',
                  icon: 'fas fa-archive'
                }
              },
              {
                path: '/categories',
                name: 'Categories',
                component: Categories,
                meta: {
                    transition: 'fade',
                    title: "分类 - SHANG-BLOG",
                    icon: 'fas fa-folder'
                 }
              },
              {
                path: '/tags',
                name: 'Tags',
                component: Tags,
                meta: {
                    transition: 'fade',
                    title: '标签 - SHANG-BLOG',
                    icon: 'fas fa-tags'
                }
              },
              // {
              //   path: '/moments',
              //   name: 'Moments',
              //   component: () => import('@/views/moments/index.vue'),
              //   meta: {
              //     title: '说说 - SHANG-BLOG',
              //     icon: 'fas fa-comment-dots'
              //   }
              // },
              {
                path: '/photos',
                name: 'Photos',
                component: Photos,
                meta: {
                    transition: 'fade',
                    title: '相册 - SHANG-BLOG',
                    icon: 'fas fa-images'
                }
              },
              {
                path: '/photos/:id',
                name: 'PhotoDetail',
                component: () => import('@/views/photos/detail.vue'),
                meta: {
                    transition: 'fade',
                    title: '相册详情 - SHANG-BLOG',
                    icon: 'fas fa-images',
                    hidden: true
                }
              },

              {
                path: '/resources',
                name: 'Resources',
                component: () => import('@/views/resources/index.vue'),
                meta: {
                  title: '资源'
                }
              },
              {
                path: '/messages',
                name: 'Messages',
                component: Messages,
                meta: { 
                  transition: 'fade',
                  title: '留言板 - SHANG-BLOG',
                  icon: 'fas fa-comments'
                }
              },
              {
                path: '/ai-chat',
                name: 'AIChat',
                component: () => import('@/views/ai-chat/index.vue'),
                meta: { 
                  transition: 'fade',
                  title: 'AI 对话 - SHANG-BLOG',
                  icon: 'fas fa-robot'
                }
              },
              {
                path: '/friends',
                name: 'Friends',
                component: () => import(/* webpackPrefetch: true */ '@/views/friends/index.vue'),
                meta: { 
                  transition: 'fade',
                  title: '友情链接 - SHANG-BLOG',
                  icon: 'fas fa-user-friends'
                }
              },
              // {
              //   path: '/about',
              //   name: 'About',
              //   component: About,
              //   meta: { 
              //     transition: 'fade',
              //     title: '关于本站 - SHANG-BLOG',
              //     icon: 'fas fa-info-circle'
              //   }
              // },
              {
                path: '/post/:id',
                name: 'Post',
                component: Article,
                props: true,
                meta: {
                  hidden: true
                }
              },
              {
                path: '/user/profile',
                name: 'Profile',
                component: () => import(/* webpackPrefetch: true */ '@/views/profile/index.vue'),
                meta: {
                  title: '个人主页 - SHANG-BLOG',
                  icon: 'fas fa-user',
                  hidden: true
                }
              },
              {
                path: '/editor',
                name: 'Editor',
                component: () => import(/* webpackPrefetch: true */ '@/views/editor/index.vue'),
                meta: {
                  title: '写文章 - SHANG-BLOG',
                  icon: 'fas fa-edit',
                  requireAuth: true,
                  hidden: true
                }
              },
              {
                path: '/chat',
                name: 'Chat',
                component: () => import(/* webpackPrefetch: true */ '@/views/chat/index.vue'),
                meta: {
                  title: '聊天 - SHANG-BLOG',
                  icon: 'fas fa-comments',
                  hidden: true
                }
              },

              {
                path: '/:pathMatch(.*)*',
                name: 'NotFound',
                component: NotFound,
                meta: {
                  hidden: true
                }
              }
        ]
    }
]

const router = new VueRouter({
  mode: 'history',
  base: '/',
  routes,
  scrollBehavior(to, from, savedPosition) {
    return { x: 0, y: 0 }
  }
})


// 解决重复点击导航时，控制台出现报错
const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (to) {
  return VueRouterPush.call(this, to).catch(err => err)
}


router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }
  //关闭搜索框
  store.commit('SET_SEARCH_VISIBLE', false)
  next()
})

export default router 