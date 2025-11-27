import { Tabs } from 'expo-router';
import React from 'react';
import { useColorScheme, Image, Text, ImageBackground, View, StyleSheet } from 'react-native';
import { Colors } from '../../src/constants/colors';
import { HapticTab } from '../../src/components/ui/hapticTab';
import { icons } from '../../src/constants/icons';
import { images } from '../../src/constants/images';

const TabBarIcon = ({ focused, icon, title, color }: any) => {
    if (focused) {
        return (
            <ImageBackground
                style={styles.activeBackground}
            >
                <Image source={icons[icon]} style={[styles.icon, { tintColor: color }]} />
                <Text style={[styles.activeTitle, { color }]}>{title}</Text>
            </ImageBackground>
        );
    } else {
        return (
            <View style={styles.inactiveContainer}>
                <Image source={icons[icon]} style={[styles.icon, { tintColor: color }]} />
            </View>
        );
    }
};

const styles = StyleSheet.create({
    activeBackground: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        minWidth: 112,
        minHeight: 56,
        borderRadius: 28,
        overflow: 'hidden',
        marginTop: 4,
        paddingHorizontal: 12,
    },
    inactiveContainer: {
        marginTop: 4,
        alignItems: 'center',
        justifyContent: 'center',
        borderRadius: 28,
        padding: 8,
    },
    icon: {
        width: 24,
        height: 24,
        resizeMode: 'contain',
    },
    activeTitle: {
        marginLeft: 8,
        fontSize: 14,
        fontWeight: '600',
    },
});

export default function TabLayout() {
    const colorScheme = useColorScheme();

    const themeColors = Colors[colorScheme ?? 'light'];

    return (
        <Tabs
            screenOptions={{
                headerShown: false,
                tabBarActiveTintColor: themeColors.tint,
                tabBarButton: HapticTab,
            }}
        >
            <Tabs.Screen
                name="index"
                options={{
                    title: 'Home',
                    tabBarIcon: ({ focused }) => (
                        <TabBarIcon focused={focused} icon="home" title="Home" color={themeColors.tint} />
                    ),
                }}
            />
            <Tabs.Screen
                name="portfolio"
                options={{
                    title: 'Portfolio',
                    tabBarIcon: ({ focused }) => (
                        <TabBarIcon focused={focused} icon="portfolio" title="Portfolio" color={themeColors.tint} />
                    ),
                }}
            />
            <Tabs.Screen
                name="search"
                options={{
                    title: 'Search',
                    tabBarIcon: ({ focused }) => (
                        <TabBarIcon focused={focused} icon="search" title="Search" color={themeColors.tint} />
                    ),
                }}
            />
            <Tabs.Screen
                name="watchlist"
                options={{
                    title: 'Watchlist',
                    tabBarIcon: ({ focused }) => (
                        <TabBarIcon focused={focused} icon="watchlist" title="Watchlist" color={themeColors.tint} />
                    ),
                }}
            />
            <Tabs.Screen
                name="profile"
                options={{
                    title: 'Profile',
                    tabBarIcon: ({ focused }) => (
                        <TabBarIcon focused={focused} icon="profile" title="Profile" color={themeColors.tint} />
                    ),
                }}
            />
        </Tabs>
    );
}
