import React from "react";
import { View, Text, StyleSheet, useColorScheme, Dimensions } from "react-native";
import { getThemeColors } from "../../../src/constants/colors";
import Carousel from "react-native-reanimated-carousel";

const screenWidth = Dimensions.get("window").width - 48;

const topMovers = [
    { id: 1, symbol: "NVDA", change: "+8.5%" },
    { id: 2, symbol: "TSLA", change: "+6.2%" },
    { id: 3, symbol: "AMD", change: "+5.1%" },
    { id: 4, symbol: "META", change: "-4.3%" },
    { id: 5, symbol: "INTEL", change: "-3.7%" },
];

const topHoldings = [
    { id: 1, symbol: "AAPL", percentage: "32.5%" },
    { id: 2, symbol: "MSFT", percentage: "18.3%" },
    { id: 3, symbol: "GOOGL", percentage: "15.7%" },
    { id: 4, symbol: "TSLA", percentage: "12.1%" },
    { id: 5, symbol: "AMZN", percentage: "8.2%" },
];

export const TopMovers = () => {
    const colorScheme = useColorScheme();
    const Colors = getThemeColors(colorScheme);

    return (
        <View style={{ flexDirection: "row", justifyContent: "space-between", marginTop: 20 }}>
            {/* Top Movers */}
            <View style={{ flex: 1, alignItems: "center" }}>
                <Text style={[styles.header, { color: Colors.text, fontStyle: "italic" }]}>Top Movers</Text>
                <Carousel
                    width={screenWidth / 2 - 30}
                    height={88}
                    data={topMovers}
                    loop
                    vertical
                    mode="parallax"
                    modeConfig={{
                        parallaxScrollingScale: 0.75,
                        parallaxScrollingOffset: 30,
                    }}
                    renderItem={({ item }) => (
                        <View style={[styles.card, { backgroundColor: Colors.card }]}>
                            <Text style={{ color: Colors.tint, fontWeight: "700" }}>{item.symbol}</Text>
                            <Text style={{ color: item.change.startsWith("+") ? "#2E7D32" : "#C62828", fontWeight: "800" }}>
                                {item.change}
                            </Text>
                        </View>
                    )}
                />
            </View>

            {/* Top % Holdings */}
            <View style={{ flex: 1, alignItems: "center" }}>
                <Text style={[styles.header, { color: Colors.text, fontStyle: "italic" }]}>Top % Holdings</Text>
                <Carousel
                    width={screenWidth / 2 - 30}
                    height={88}
                    data={topHoldings}
                    loop
                    vertical
                    mode="parallax"
                    modeConfig={{
                        parallaxScrollingScale: 0.75,
                        parallaxScrollingOffset: 30,
                    }}
                    renderItem={({ item }) => (
                        <View style={[styles.card, { backgroundColor: Colors.card }]}>
                            <Text style={{ color: "#266EF1", fontWeight: "700" }}>{item.symbol}</Text>
                            <Text style={{ color: Colors.text, fontWeight: "800" }}>{item.percentage}</Text>
                        </View>
                    )}
                />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    header: {
        fontSize: 12,
        fontWeight: "700",
        marginBottom: 8,
    },
    card: {
        borderRadius: 12,
        padding: 8,
        justifyContent: "center",
        alignItems: "center",
        marginVertical: 3,
        height: 70,
    },
});
