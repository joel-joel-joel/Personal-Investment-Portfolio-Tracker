/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./App.{js,jsx,ts,tsx}",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
    theme: {
        extend: {
            colors: {
                // Main brand colors
                primary: '#FFFFFF',          // White UI background
                secondary: '#F3F6FA',        // Very soft bluish-grey

                // Fintech blue accent (main brand)
                accent: '#266EF1',           // Techy bright blue

                // Text colors
                text: {
                    dark: '#0B0E11',         // Almost-black for headings
                    subtle: '#6F7A85',       // Muted cool grey
                    light: '#FFFFFF',        // For dark mode
                },

                // Light mode palette
                light: {
                    100: '#FFFFFF',
                    200: '#F3F6FA',
                    300: '#E1E6ED',
                },

                // Dark mode palette
                dark: {
                    100: '#0A0F1F',          // Deep navy
                    200: '#050A16',          // Almost-black navy
                    300: '#00040C',          // Pure dark
                },

                // Blue gradient shades
                blue: {
                    100: '#D7E4FF',
                    200: '#A9C5FF',
                    300: '#7BA7FF',
                    400: '#4C89FF',
                    500: '#266EF1',    // main accent
                    600: '#1E56C4',
                    700: '#163F96',
                    800: '#0D2869',
                    900: '#05123D',
                }
            },
        },
    },
    plugins: [],

};
