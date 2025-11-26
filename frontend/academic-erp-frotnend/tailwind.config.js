/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['"Inter var"', 'Inter', 'system-ui', 'sans-serif'],
      },
      colors: {
        brand: {
          50: '#f1f8ff',
          100: '#dbeefe',
          200: '#bdddfc',
          300: '#8cc5f9',
          400: '#55a7f3',
          500: '#2b87ea',
          600: '#1d6bd8',
          700: '#1954b5',
          800: '#184892',
          900: '#183d76',
        },
      },
    },
  },
  plugins: [],
}

