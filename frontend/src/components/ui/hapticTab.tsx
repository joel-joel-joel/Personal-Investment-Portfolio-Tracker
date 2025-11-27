import * as Haptics from 'expo-haptics';
import React from "react";
import { Pressable } from 'react-native';

export function HapticTab({ children, ...props }) {
    return (
        <Pressable
            onPressIn={() => {
                Haptics.selectionAsync();
            }}
            {...props}
        >
            {children}
        </Pressable>
    );
}
