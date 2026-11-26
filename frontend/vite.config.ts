import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Dev proxy: when React calls fetch('/api/...'), the Vite dev server forwards the
    // request to Spring Boot on :8080. The browser only ever talks to :5173, so there
    // is no cross-origin (CORS) problem during development.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
