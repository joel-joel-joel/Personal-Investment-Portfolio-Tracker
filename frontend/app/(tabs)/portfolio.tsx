import React from 'react';
import { View, Text, StyleSheet, useColorScheme } from 'react-native';
import { getThemeColors } from '../../src/constants/colors';

export default function PortfolioScreen() {
    const colorScheme = useColorScheme();
    const Colors = getThemeColors(colorScheme);

    return (
        <View style={[styles.container, { backgroundColor: Colors.background }]}>
            <Text style={[styles.text, { color: Colors.text }]}>Portfolio</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    text: {
        fontSize: 24,
        fontWeight: '600',
    },
});