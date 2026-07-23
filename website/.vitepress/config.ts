import { defineConfig } from 'vitepress'

export default defineConfig({
  base: '/oryxos/',
  title: 'OryxOS',
  description: 'Enterprise Agent OS - Java 21 + Spring Boot 3.x',
  themeConfig: {
    logo: '/images/logo.svg',
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Docs', link: '/docs/' },
      { text: 'Guide', link: '/guide/' },
      { text: 'GitHub', link: 'https://github.com/snake-java/oryxos' }
    ],
    sidebar: [
      {
        text: 'Getting Started',
        items: [
          { text: 'Introduction', link: '/docs/' },
          { text: 'Quick Start', link: '/docs/quickstart' },
          { text: 'Architecture', link: '/docs/architecture' }
        ]
      },
      {
        text: 'Core Concepts',
        items: [
          { text: 'Agent', link: '/docs/concepts/agent' },
          { text: 'Provider', link: '/docs/concepts/provider' },
          { text: 'Memory', link: '/docs/concepts/memory' },
          { text: 'Tool', link: '/docs/concepts/tool' }
        ]
      }
    ],
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2024-2026 OryxOS Authors'
    }
  }
})
