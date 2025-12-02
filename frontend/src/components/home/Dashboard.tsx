import React from "react";
import { View, Text, StyleSheet, useColorScheme, Dimensions } from "react-native";
import { getThemeColors } from "../../../src/constants/colors";
import { LineChart } from "react-native-chart-kit";

const screenWidth = Dimensions.get("window").width - 48;
const priceData = [90, 100, 102, 101, 105, 107, 110, 108];

export const Dashboard = () => {
    const colorScheme = useColorScheme();
    const Colors = getThemeColors(colorScheme);

    return (
        <View style={styles.wrapper}>
            <View style={[styles.dashboard, { backgroundColor: "white" }]}>
                <View style={styles.dashboardTextBlock}>
                    <Text style={[styles.dashboardtitle, { color: Colors.text }]}>
                        Welcome to Pegasus!
                    </Text>
                    <Text style={[styles.subtitle, { color: Colors.text }]}>
                        Cash and Holdings
                    </Text>
                    <Text style={{ flexDirection: "row" }}>
                        <Text style={[styles.currency, { color: Colors.text }]}>A$</Text>
                        <Text style={[styles.cashamount, { color: Colors.text }]}>1,000,000.00</Text>
                    </Text>
                    <Text style={[styles.dashboarddetails, { color: Colors.text }]}>
                        Gain: A$27.68 (0.89%), Today
                    </Text>
                </View>

                <LineChart
                    data={{
                        labels: ["Jan", "Feb", "Mar"],
                        datasets: [{ data: priceData, color: () => "#266EF1", strokeWidth: 2 }],
                    }}
                    width={screenWidth}
                    height={140}
                    withDots={false}
                    withInnerLines={false}
                    withOuterLines={false}
                    withVerticalLabels={false}
                    withHorizontalLabels={false}
                    withShadow={false}
                    chartConfig={{
                        backgroundColor: "white",
                        backgroundGradientFrom: "white",
                        backgroundGradientTo: "white",
                        color: () => "#266EF1",
                    }}
                    style={{ borderRadius: 12, marginTop: 20 }}
                />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    wrapper: { marginTop: -35, justifyContent: "center", alignItems: "center" },
    dashboard: { width: "100%", padding: 10, borderRadius: 12 },
    dashboardTextBlock: { flex: 1, gap: 4 },
    dashboardtitle: { fontSize: 20, fontWeight: "800", fontStyle: "italic" },
    cashamount: { fontSize: 24, marginTop: 10, fontWeight: "bold" },
    currency: { fontSize: 18, fontWeight: "bold", marginTop: 20 },
    dashboarddetails: { fontSize: 12, marginTop: 5, opacity: 0.8 },
    subtitle: { fontSize: 12, marginTop: 5, opacity: 0.7 },
});
