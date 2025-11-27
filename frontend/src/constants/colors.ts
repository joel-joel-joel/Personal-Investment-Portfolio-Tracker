// constants/colors.ts

// Brand blues
const BLUE = "#266EF1";        // Primary accent (tech blue)
const BLUE_DARK = "#4C89FF";   // Slightly brighter for dark mode highlights

export const Colors = {
    light: {
        background: "#FFFFFF",    // Clean white
        text: "#0B0E11",          // Deep grey/near black for readability
        tint: BLUE,               // Primary brand color
        tabIconDefault: "#A0A9B8",// Soft muted grey
        tabIconSelected: BLUE,    // Active tab = blue
        card: "#F3F6FA",          // Soft bluish-grey card background
        border: "#E1E6ED",        // Very light blue-grey border
    },

    dark: {
        background: "#0A0F1F",    // Deep navy
        text: "#FFFFFF",          // Clean white text
        tint: BLUE_DARK,          // Blue highlight for dark mode
        tabIconDefault: "#67718A",// Cool grey for icons
        tabIconSelected: BLUE_DARK,
        card: "#111726",          // Slightly lighter navy card
        border: "#1E2638",        // Subtle blue-ish border
    },
};

export function getThemeColors(colorScheme: "light" | "dark" | null | undefined) {
    return Colors[colorScheme ?? "light"];
}
