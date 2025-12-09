import React from "react";
import { View, Image, StyleSheet, useColorScheme } from "react-native";
import { getThemeColors } from "../../../src/constants/colors";
import { icons } from "../../../src/constants/icons";

export const HeaderSection = () => {
    const colorScheme = useColorScheme();
    const Colors = getThemeColors(colorScheme);

    return (
        <View style={styles.topBar}>
            <View style={styles.centerGroup}>
                <Image
                    source={icons.pegasus}
                    style={[styles.icon, { tintColor: Colors.tint }]}
                />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    topBar: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "center",
        marginTop: -20,
        position: "relative",
    },
    centerGroup: {
        flexDirection: "row",
        alignItems: "center",
    },
    icon: {
        width: 125,
        height: 125,
        resizeMode: "contain",
    },
    rightIcon: {
        position: 'absolute',
        right: 0,
        marginTop: 5,
    },

});
