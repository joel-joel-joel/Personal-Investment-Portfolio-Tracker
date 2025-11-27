import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { useColorScheme } from "react-native";
import { getThemeColors } from "../../src/constants/colors";

export default function HomeScreen() {
    const colorScheme = useColorScheme();
    const Colors = getThemeColors(colorScheme);

    return (
        <View style={[styles.container, { backgroundColor: Colors.background }]}>
            <Text style={[styles.title, { color: Colors.tint }]}>
                Welcome to My App
            </Text>

            <View style={[styles.card, { backgroundColor: Colors.card, borderColor: Colors.border }]}>
                <Text style={[styles.cardText, { color: Colors.text }]}>
                    This is a dummy homepage using the tech-blue theme.
                </Text>
            </View>

            <Text style={[styles.subtitle, { color: Colors.text }]}>
                More features coming soon...
            </Text>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 24,
        justifyContent: "center",
        alignItems: "center",
    },
    title: {
        fontSize: 28,
        fontWeight: "700",
        marginBottom: 20,
    },
    subtitle: {
        fontSize: 16,
        marginTop: 20,
        opacity: 0.7,
    },
    card: {
        width: "90%",
        padding: 20,
        borderRadius: 12,
        borderWidth: 1,
    },
    cardText: {
        fontSize: 16,
    },
});
