import React from "react";
import {View, ScrollView, useColorScheme, StyleSheet} from "react-native";
import { getThemeColors } from "../../src/constants/colors";
import { HeaderSection } from "../../src/components/home/HeaderSection";
import { Dashboard } from "../../src/components/home/Dashboard";
import { WatchlistHighlights } from "../../src/components/home/WatchlistHighlights";
import { TopMovers } from "../../src/components/home/TopMovers";
import { ExpandableNewsCard } from '../../src/components/home/ExpandableNewsCard';
import { SuggestedForYou } from '../../src/components/home/SuggestedForYou';
import {StockTicker} from "../..//src/components/home/StockTicker";

export default function HomeScreen() {
    const colorScheme = useColorScheme();
    const Colors = getThemeColors(colorScheme);

    const newsItems = [
        {
            id: 1,
            title: "Fed Signals Rate Cuts Ahead",
            description: "Federal Reserve indicates potential interest rate reductions...",
            image: require('../../assets/images/apple.png'),
            content: "The Federal Reserve has signaled that interest rate cuts may be coming...",
            sector: "Markets"
        },
        {
            id: 2,
            title: "Tech Stocks Rally on AI News",
            description: "Technology sector leads market as AI developments continue...",
            image: require('../../assets/images/apple.png'),
            content: "Major technology companies announced breakthrough developments...",
            sector: "Technology"
        },
        {
            id: 3,
            title: "Earnings Season Exceeds Expectations",
            description: "Companies report stronger than expected Q3 earnings...",
            image: require('../../assets/images/apple.png'),
            content: "Corporate earnings for the third quarter have exceeded analyst expectations...",
            sector: "Healthcare"
        },
    ];

    const watchlistStocks = [
        { id: 1, symbol: "AAPL", price: "A$150.25", change: "+2.5%" },
        { id: 2, symbol: "MSFT", price: "A$380.50", change: "+1.8%" },
        { id: 3, symbol: "GOOGL", price: "A$140.75", change: "+3.2%" },
        { id: 4, symbol: "TSLA", price: "A$245.30", change: "-1.5%" },
        { id: 5, symbol: "AMZN", price: "A$170.90", change: "+2.1%" },
    ];

    return (
        <View style={{ flex: 1, backgroundColor: Colors.background, padding: 24 }}>
            <ScrollView showsVerticalScrollIndicator={false}>
                <HeaderSection />
                <Dashboard />
                <StockTicker stocks={watchlistStocks} />
                <WatchlistHighlights />
                <TopMovers />
                <ExpandableNewsCard news={newsItems} />
                <SuggestedForYou />
            </ScrollView>
        </View>
    );
}


