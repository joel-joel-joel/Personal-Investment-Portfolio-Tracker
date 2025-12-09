import {View, useColorScheme, ScrollView} from 'react-native';
import { getThemeColors } from '@/src/constants/colors';
import { HeaderSection } from '@/src/components/home/HeaderSection';
import { HoldingsList } from '@/src/components/portfolio/HoldingsList';
import { AllocationOverview } from '@/src/components/portfolio/AllocationOverview';
import { useState, useEffect, useCallback } from 'react';
import { useAuth } from '@/src/context/AuthContext';
import { getAccountHoldings } from '@/src/services/portfolioService';
import { getStockById } from '@/src/services/entityService';

interface Holding {
    id: string;
    symbol: string;
    company: string;
    shares: number;
    amountInvested: number;
    currentValue: number;
    returnAmount: number;
    returnPercent: number;
    sector: string;
}

export default function PortfolioScreen() {
    const colorScheme = useColorScheme();
    const Colors = getThemeColors(colorScheme);
    const { user, activeAccount } = useAuth();

    const [holdings, setHoldings] = useState<Holding[]>([]);
    const [loading, setLoading] = useState(true);
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    // Fetch holdings
    const fetchHoldings = useCallback(async () => {
        if (!user || !activeAccount) {
            setHoldings([]);
            setLoading(false);
            return;
        }

        try {
            const data = await getAccountHoldings(activeAccount.accountId);

            // Transform backend data and fetch stock details
            const transformedData: Holding[] = await Promise.all(
                data.map(async (item) => {
                    // Use currentValue from backend or calculate if needed
                    const currentValue = item.currentValue || (item.quantity * item.currentPrice);
                    const amountInvested = item.totalCostBasis;
                    const returnAmount = item.unrealizedGain;
                    const returnPercent = item.unrealizedGainPercent;

                    // Fetch stock details to get company name
                    let companyName = item.stockSymbol;
                    let sector = 'Unknown';

                    try {
                        const stockDetails = await getStockById(item.stockId);
                        companyName = stockDetails.companyName;
                        // Note: Backend StockDTO doesn't include sector, using default
                        sector = 'Technology';
                    } catch {
                        // If stock fetch fails, use fallback values
                    }

                    return {
                        id: item.holdingId,
                        symbol: item.stockSymbol,
                        company: companyName,
                        shares: item.quantity,
                        amountInvested,
                        currentValue,
                        returnAmount,
                        returnPercent,
                        sector: sector,
                    };
                })
            );

            setHoldings(transformedData);
        } catch (error: any) {
            console.error('Failed to fetch holdings:', error);
        } finally {
            setLoading(false);
        }
    }, [user, activeAccount]);

    useEffect(() => {
        fetchHoldings();
    }, [fetchHoldings, refreshTrigger]);

    // Trigger refresh callback
    const handleRefresh = () => {
        setRefreshTrigger(prev => prev + 1);
    };

    return (
        <View style={{ flex: 1, backgroundColor: Colors.background, padding: 24 }}>
            <ScrollView showsVerticalScrollIndicator={false}>
                <HeaderSection />
                <HoldingsList holdings={holdings} onRefresh={handleRefresh} />
                <AllocationOverview holdings={holdings} />
            </ScrollView>
        </View>
    );
}

