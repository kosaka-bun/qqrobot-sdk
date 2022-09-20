import { createApp } from 'vue'
import page from './index.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './index.css'

createApp(page).use(ElementPlus).mount('#app');
