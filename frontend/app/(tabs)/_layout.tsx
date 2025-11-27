import { Tabs } from 'expo-router';
import React from 'react';
import { useColorScheme } from 'react-native';
import { Colors } from '../../src/constants/colors';
import { TabBarIcon } from '../../src/components/ui/tabBarIcon';
import { HapticTab } from '../../src/components/ui/hapticTab';


export default function TabLayout() {
    const colorScheme = useColorScheme();

    return (
        <Tabs
            screenOptions={{
                tabBarActiveTintColor: Colors[colorScheme ?? 'light'].tint,
                headerShown: false,
                tabBarButton: HapticTab,
            }}
        >
            <Tabs.Screen
                name="index"
                options={{
                    title: 'Home',
                    tabBarIcon: ({ color }) => (
                        <TabBarIcon name="home" color={color} />
                    ),
                }}
            />

            <Tabs.Screen
                name="portfolio"
                options={{
                    title: 'Portfolio',
                    tabBarIcon: ({ color }) => (
                        <TabBarIcon name="pie-chart" color={color} />
                    ),
                }}
            />

            <Tabs.Screen
                name="search"
                options={{
                    title: 'Search',
                    tabBarIcon: ({ color }) => (
                        <TabBarIcon name="search" color={color} />
                    ),
                }}
            />

            <Tabs.Screen
                name="watchlist"
                options={{
                    title: 'Watchlist',
                    tabBarIcon: ({ color }) => (
                        <TabBarIcon name="star" color={color} />
                    ),
                }}
            />

            <Tabs.Screen
                name="profile"
                options={{
                    title: 'Profile',
                    tabBarIcon: ({ color }) => (
                        <TabBarIcon name="person" color={color} />
                    ),
                }}
            />
        </Tabs>
    );
}
