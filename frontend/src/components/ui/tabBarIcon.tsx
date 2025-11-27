import { Ionicons } from '@expo/vector-icons';
import React from "react";

export function TabBarIcon(props: {
    name: React.ComponentProps<typeof Ionicons>['name'];
    color: string;
    size?: number;
}) {
    return <Ionicons style={{ marginBottom: -3 }} {...props} size={props.size ?? 28} />;
}
