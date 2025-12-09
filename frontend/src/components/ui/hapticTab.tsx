import React from 'react';
import {
    TouchableOpacity,
    GestureResponderEvent,
    ViewStyle,
    StyleProp,
    View,
    Image,
    StyleSheet,
} from 'react-native';
import {icons} from "@/src/constants/icons";


interface HapticTabProps {
    children: React.ReactNode;
    onPress?: (event: GestureResponderEvent | any) => void;
    style?: StyleProp<ViewStyle>;
}

export const HapticTab: React.FC<HapticTabProps> = ({
                                                        children,
                                                        onPress,
                                                        style,
                                                    }) => {
    return (
        <TouchableOpacity
            onPress={onPress as any}
            style={style}
            activeOpacity={0.7}
        >
            {children}
        </TouchableOpacity>
    );
};

// Tab Bar Icon Component
interface TabBarIconProps {
    focused: boolean;
    icon: keyof typeof icons;
    title: string;
    color: string;
    inactiveColor: string;
}

export const TabBarIcon: React.FC<TabBarIconProps> = ({
                                                          focused,
                                                          icon,
                                                          title,
                                                          color,
                                                          inactiveColor,
                                                      }) => {
    return (
        <View style={[styles.iconContainer, focused && styles.activeIconContainer]}>
            <Image
                source={icons[icon]}
                style={[
                    styles.icon,
                    {
                        tintColor: focused ? color : inactiveColor,
                    },
                ]}
            />
        </View>
    );
};

const styles = StyleSheet.create({
    iconContainer: {
        alignItems: 'center',
        justifyContent: 'center',
        paddingHorizontal: 12,
        paddingVertical: 8,
        borderRadius: 12,
    },
    activeIconContainer: {
        backgroundColor: 'rgba(38, 110, 241, 0.15)',
    },
    icon: {
        width: 24,
        height: 24,
        resizeMode: 'contain',
    },
});

