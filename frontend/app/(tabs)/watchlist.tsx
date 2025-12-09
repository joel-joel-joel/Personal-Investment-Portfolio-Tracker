import {View, useColorScheme, ScrollView} from 'react-native';
import { getThemeColors } from '@/src/constants/colors';
import { HeaderSection } from '@/src/components/home/HeaderSection';
import WatchListScreenBackendIntegrated from '@/src/components/watchlist/WatchlistScreenBackendIntegrated';



export default function WatchList() {
    const colorScheme = useColorScheme();
    const Colors = getThemeColors(colorScheme);

    return (
        <View style={{ flex: 1, backgroundColor: Colors.background, padding: 24 }}>
            <ScrollView showsVerticalScrollIndicator={false}>
                <HeaderSection />
                <WatchListScreenBackendIntegrated/>
            </ScrollView>
        </View>
    );
}