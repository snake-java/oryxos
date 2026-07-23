import { defineConfig } from 'vitepress'

export default defineConfig({
  base: '/oryxos/',
  title: 'OryxOS',
  description: 'Enterprise Agent OS - Java 21 + Spring Boot 3.x',
  themeConfig: {
    logo: '/images/logo.svg',
    nav: [
      { text: '中文', link: '/zh/' },
      { text: 'English', link: '/' },
      { text: 'GitHub', link: 'https://github.com/snake-java/oryxos' }
    ],
    sidebar: [
      {
        text: 'Getting Started',
        items: [
          { text: 'Introduction', link: '/' },
          { text: 'Quick Start', link: '/docs/en/quickstart' },
          { text: 'Architecture', link: '/docs/en/architecture' }
        ]
      },
      {
        text: 'Core Concepts',
        items: [
          { text: 'Agent', link: '/docs/en/concepts/agent' },
          { text: 'Provider', link: '/docs/en/concepts/provider' },
          { text: 'Memory', link: '/docs/en/concepts/memory' },
          { text: 'Tool', link: '/docs/en/concepts/tool' }
        ]
      }
    ],
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2024-2026 OryxOS Authors'
    }
  }
})
