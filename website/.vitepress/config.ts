import { defineConfig } from 'vitepress'

export default defineConfig({
  base: '/oryxos/',
  title: 'OryxOS',
  description: 'Enterprise Java Agent Runtime Platform - Java 21 + Spring Boot 3.x',
  darkMode: true,
  themeConfig: {
    logo: '/images/logo.svg',
    nav: [
      { text: 'Docs', link: '/docs/' },
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
      message: 'MIT License',
      copyright: 'Copyright © 2024 OryxOS Authors'
    }
  }
})
