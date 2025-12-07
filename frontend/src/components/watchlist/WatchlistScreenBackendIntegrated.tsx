import React, { useState, useMemo, useEffect, useCallback } from 'react';
import {
    View,
    Text,
    StyleSheet,
    ScrollView,
    TouchableOpacity,
    useColorScheme,
    Alert,
    ActivityIndicator,
    RefreshControl,
} from 'react-native';
import { MaterialCommunityIcons } from '@expo/vector-icons';
import { getThemeColors } from '@/src/constants/colors';
import { useRouter } from 'expo-router';
import { useAuth } from '@/src/context/AuthContext';
import {
    getWatchlist,
    removeFromWatchlist,
} from '@/src/services/portfolioService';
import type { WatchlistDTO } from '@/src/types/api';

interface WatchlistStock {
    id: string;
    stockId: string;
    symbol: string;
    name: string;
    price: number;
    change: number;
    changePercent: number;
    sector: string;
    dayHigh: number;
    dayLow: number;
}

const sectorColors = {
    'Technology': { color: '#0369A1', bgLight: '#EFF6FF' },
    'Semiconductors': { color: '#B45309', bgLight: '#FEF3C7' },
    'FinTech': { color: '#15803D', bgLight: '#F0FDF4' },
    'Consumer/Tech': { color: '#6D28D9', bgLight: '#F5F3FF' },
    'Healthcare': { color: '#BE123C', bgLight: '#FFE4E6' },
    'Market': { color: '#EA580C', bgLight: '#FFEDD5' },
};

const WatchlistCard = ({
    stock,
    colors,
    sectorColor,
    onRemove,
}: {
    stock: WatchlistStock;
    colors: any;
    sectorColor: any;
    onRemove: (stockId: string) => void;
}) => {
    const router = useRouter();
    const isPositive = stock.changePercent >= 0;
    const [expanded, setExpanded] = useState(false);

    const handleNavigateToStock = () => {
        const stockData = {
            symbol: stock.symbol,
            name: stock.name,
            price: stock.price,
            change: stock.change,
            changePercent: stock.changePercent,
            sector: stock.sector,
            marketCap: '0',
            peRatio: '0',
            dividend: '0',
            dayHigh: stock.dayHigh,
            dayLow: stock.dayLow,
            yearHigh: 0,
            yearLow: 0,
            description: '',
            employees: '',
            founded: '',
            website: '',
            nextEarningsDate: '',
            nextDividendDate: '',
            earningsPerShare: '',
        };

        router.push({
            pathname: '/stock/[ticker]',
            params: {
                ticker: stock.symbol,
                stock: JSON.stringify(stockData),
            },
        });
    };

    const handleInvest = () => {
        const stockData = {
            symbol: stock.symbol,
            name: stock.name,
            price: stock.price,
            change: stock.change,
            changePercent: stock.changePercent,
            sector: stock.sector,
            marketCap: '0',
            peRatio: '0',
            dividend: '0',
            dayHigh: stock.dayHigh,
            dayLow: stock.dayLow,
            yearHigh: 0,
            yearLow: 0,
            description: '',
            employees: '',
            founded: '',
            website: '',
            nextEarningsDate: '',
            nextDividendDate: '',
            earningsPerShare: '',
        };

        router.push({
            pathname: '/transaction/buy',
            params: {
                stock: JSON.stringify(stockData),
            },
        });
    };

    return (
        <TouchableOpacity
            onPress={handleNavigateToStock}
            onLongPress={() => setExpanded(!expanded)}
            style={[
                styles.watchlistCard,
                {
                    backgroundColor: colors.card,
                    borderColor: colors.border,
                    maxHeight: expanded ? 280 : 100,
                }
            ]}
            activeOpacity={0.7}
        >
            <View style={styles.cardHeader}>
                <View style={styles.cardLeft}>
                    <View style={[styles.sectorBadge, { backgroundColor: sectorColor.bgLight }]}>
                        <Text style={[styles.sectorBadgeText, { color: sectorColor.color }]}>
                            {stock.sector.slice(0, 1)}
                        </Text>
                    </View>
                    <View style={styles.stockInfo}>
                        <Text style={[styles.symbol, { color: sectorColor.color }]}>
                            {stock.symbol}
                        </Text>
                        <Text style={[styles.name, { color: colors.text, opacity: 0.7 }]} numberOfLines={1}>
                            {stock.name}
                        </Text>
                    </View>
                </View>

                <View style={styles.cardRight}>
                    <Text style={[styles.price, { color: colors.text }]}>
                        A${stock.price.toFixed(2)}
                    </Text>
                    <View style={[styles.changeBadge, { backgroundColor: isPositive ? '#E7F5E7' : '#FCE4E4' }]}>
                        <MaterialCommunityIcons
                            name={isPositive ? 'trending-up' : 'trending-down'}
                            size={14}
                            color={isPositive ? '#2E7D32' : '#C62828'}
                        />
                        <Text style={[styles.changeText, { color: isPositive ? '#2E7D32' : '#C62828' }]}>
                            {isPositive ? '+' : ''}{stock.changePercent.toFixed(2)}%
                        </Text>
                    </View>
                </View>
            </View>

            {expanded && (
                <View style={[styles.expandedContent, { borderTopColor: colors.border }]}>
                    <View style={styles.rangeSection}>
                        <View style={styles.rangeItem}>
                            <Text style={[styles.rangeLabel, { color: colors.text, opacity: 0.6 }]}>
                                Day Low
                            </Text>
                            <Text style={[styles.rangeValue, { color: colors.text }]}>
                                A${stock.dayLow.toFixed(2)}
                            </Text>
                        </View>
                        <View style={styles.rangeItem}>
                            <Text style={[styles.rangeLabel, { color: colors.text, opacity: 0.6 }]}>
                                Day High
                            </Text>
                            <Text style={[styles.rangeValue, { color: colors.text }]}>
                                A${stock.dayHigh.toFixed(2)}
                            </Text>
                        </View>
                        <View style={styles.rangeItem}>
                            <Text style={[styles.rangeLabel, { color: colors.text, opacity: 0.6 }]}>
                                Change
                            </Text>
                            <Text style={[styles.rangeValue, { color: isPositive ? '#2E7D32' : '#C62828' }]}>
                                {isPositive ? '+' : ''}A${stock.change.toFixed(2)}
                            </Text>
                        </View>
                    </View>

                    <View style={styles.actionButtons}>
                        <TouchableOpacity
                            onPress={handleInvest}
                            style={[styles.actionButton, { backgroundColor: colors.tint }]}
                        >
                            <MaterialCommunityIcons name="plus" size={16} color="white" />
                            <Text style={styles.actionButtonText}>Invest</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            onPress={() => onRemove(stock.stockId)}
                            style={[styles.actionButton, { backgroundColor: '#FCE4E4' }]}
                        >
                            <MaterialCommunityIcons name="trash-can-outline" size={16} color="#C62828" />
                            <Text style={[styles.actionButtonText, { color: '#C62828' }]}>Remove</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            )}

            {!expanded && (
                <MaterialCommunityIcons
                    name="chevron-down"
                    size={20}
                    color={colors.text}
                    style={styles.chevron}
                />
            )}
        </TouchableOpacity>
    );
};

export default function WatchlistScreenBackendIntegrated() {
    const colorScheme = useColorScheme();
    const Colors = getThemeColors(colorScheme);
    const { user } = useAuth();

    const [watchlist, setWatchlist] = useState<WatchlistStock[]>([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [sortBy, setSortBy] = useState<'symbol' | 'price' | 'changePercent' | 'sector'>('symbol');
    const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');
    const [selectedSector, setSelectedSector] = useState<string | null>(null);

    // Fetch watchlist from backend
    const fetchWatchlist = useCallback(async () => {
        if (!user) return;

        try {
            setError(null);
            const data = await getWatchlist();

            // Transform backend data to component format
            // Note: Backend returns WatchlistDTO[] which needs to be mapped to include stock details
            // For now, using mock transformation - replace with actual stock data fetch
            const transformedData: WatchlistStock[] = data.map((item, index) => ({
                id: item.watchlistId,
                stockId: item.stockId,
                symbol: `STOCK${index + 1}`, // Replace with actual stock symbol from stock service
                name: `Company ${index + 1}`, // Replace with actual company name
                price: 100 + Math.random() * 100,
                change: (Math.random() - 0.5) * 10,
                changePercent: (Math.random() - 0.5) * 5,
                sector: 'Technology',
                dayHigh: 110,
                dayLow: 90,
            }));

            setWatchlist(transformedData);
        } catch (error: any) {
            console.error('Failed to fetch watchlist:', error);
            setError(error.message || 'Failed to load watchlist');
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    }, [user]);

    // Initial fetch
    useEffect(() => {
        fetchWatchlist();
    }, [fetchWatchlist]);

    // Pull to refresh
    const onRefresh = useCallback(() => {
        setRefreshing(true);
        fetchWatchlist();
    }, [fetchWatchlist]);

    // Remove from watchlist
    const handleRemoveFromWatchlist = async (stockId: string) => {
        Alert.alert(
            'Remove from Watchlist',
            'Are you sure you want to remove this stock?',
            [
                { text: 'Cancel', style: 'cancel' },
                {
                    text: 'Remove',
                    style: 'destructive',
                    onPress: async () => {
                        // Optimistic update
                        const previousWatchlist = [...watchlist];
                        setWatchlist(prev => prev.filter(stock => stock.stockId !== stockId));

                        try {
                            await removeFromWatchlist(stockId);
                            // Success - data already updated optimistically
                        } catch (error: any) {
                            // Rollback on error
                            setWatchlist(previousWatchlist);
                            Alert.alert('Error', error.message || 'Failed to remove from watchlist');
                        }
                    },
                },
            ]
        );
    };

    // Get unique sectors
    const sectors = Array.from(new Set(watchlist.map(s => s.sector)));

    // Filter and sort watchlist
    const filteredAndSortedWatchlist = useMemo(() => {
        let results = [...watchlist];

        if (selectedSector) {
            results = results.filter(stock => stock.sector === selectedSector);
        }

        results.sort((a, b) => {
            let aVal: any = a[sortBy];
            let bVal: any = b[sortBy];

            if (typeof aVal === 'string') {
                aVal = aVal.toLowerCase();
                bVal = bVal.toLowerCase();
            }

            if (sortOrder === 'asc') {
                return aVal > bVal ? 1 : -1;
            } else {
                return aVal < bVal ? 1 : -1;
            }
        });

        return results;
    }, [watchlist, sortBy, sortOrder, selectedSector]);

    const toggleSortOrder = (field: 'symbol' | 'price' | 'changePercent' | 'sector') => {
        if (sortBy === field) {
            setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
        } else {
            setSortBy(field);
            setSortOrder('asc');
        }
    };

    if (loading) {
        return (
            <View style={[styles.container, { backgroundColor: Colors.background }]}>
                <View style={styles.loadingContainer}>
                    <ActivityIndicator size="large" color={Colors.tint} />
                    <Text style={[styles.loadingText, { color: Colors.text }]}>
                        Loading watchlist...
                    </Text>
                </View>
            </View>
        );
    }

    if (error) {
        return (
            <View style={[styles.container, { backgroundColor: Colors.background }]}>
                <View style={styles.errorContainer}>
                    <MaterialCommunityIcons name="alert-circle-outline" size={56} color="#C62828" />
                    <Text style={[styles.errorTitle, { color: Colors.text }]}>
                        Failed to Load
                    </Text>
                    <Text style={[styles.errorMessage, { color: Colors.text, opacity: 0.7 }]}>
                        {error}
                    </Text>
                    <TouchableOpacity
                        onPress={fetchWatchlist}
                        style={[styles.retryButton, { backgroundColor: Colors.tint }]}
                    >
                        <Text style={styles.retryButtonText}>Retry</Text>
                    </TouchableOpacity>
                </View>
            </View>
        );
    }

    return (
        <View style={[styles.container, { backgroundColor: Colors.background }]}>
            <ScrollView
                refreshControl={
                    <RefreshControl
                        refreshing={refreshing}
                        onRefresh={onRefresh}
                        tintColor={Colors.tint}
                    />
                }
                showsVerticalScrollIndicator={false}
            >
                <View style={styles.header}>
                    <Text style={[styles.title, { color: Colors.text }]}>
                        My Watchlist
                    </Text>
                    <Text style={[styles.subtitle, { color: Colors.text, opacity: 0.6 }]}>
                        {filteredAndSortedWatchlist.length} stocks
                    </Text>
                </View>

                {watchlist.length > 0 && (
                    <View style={[styles.statsRow, { backgroundColor: Colors.card, borderColor: Colors.border }]}>
                        <View style={styles.statItem}>
                            <Text style={[styles.statLabel, { color: Colors.text, opacity: 0.6 }]}>
                                Watching
                            </Text>
                            <Text style={[styles.statValue, { color: Colors.text }]}>
                                {watchlist.length}
                            </Text>
                        </View>
                        <View style={[styles.statDivider, { backgroundColor: Colors.border }]} />
                        <View style={styles.statItem}>
                            <Text style={[styles.statLabel, { color: Colors.text, opacity: 0.6 }]}>
                                Avg Change
                            </Text>
                            <Text style={[styles.statValue, { color: watchlist.reduce((sum, s) => sum + s.changePercent, 0) / watchlist.length >= 0 ? '#2E7D32' : '#C62828' }]}>
                                {(watchlist.reduce((sum, s) => sum + s.changePercent, 0) / watchlist.length).toFixed(2)}%
                            </Text>
                        </View>
                    </View>
                )}

                {sectors.length > 1 && (
                    <ScrollView
                        horizontal
                        showsHorizontalScrollIndicator={false}
                        style={styles.sectorFilter}
                        contentContainerStyle={styles.sectorFilterContent}
                    >
                        <TouchableOpacity
                            onPress={() => setSelectedSector(null)}
                            style={[
                                styles.sectorFilterButton,
                                selectedSector === null && { backgroundColor: Colors.tint }
                            ]}
                        >
                            <Text
                                style={[
                                    styles.sectorFilterText,
                                    selectedSector === null && { color: 'white', fontWeight: '700' }
                                ]}
                            >
                                All
                            </Text>
                        </TouchableOpacity>
                        {sectors.map(sector => (
                            <TouchableOpacity
                                key={sector}
                                onPress={() => setSelectedSector(selectedSector === sector ? null : sector)}
                                style={[
                                    styles.sectorFilterButton,
                                    selectedSector === sector && { backgroundColor: Colors.tint }
                                ]}
                            >
                                <Text
                                    style={[
                                        styles.sectorFilterText,
                                        selectedSector === sector && { color: 'white', fontWeight: '700' }
                                    ]}
                                >
                                    {sector}
                                </Text>
                            </TouchableOpacity>
                        ))}
                    </ScrollView>
                )}

                {watchlist.length > 0 && (
                    <View style={styles.sortContainer}>
                        <TouchableOpacity
                            onPress={() => toggleSortOrder('symbol')}
                            style={[styles.sortButton, sortBy === 'symbol' && { backgroundColor: Colors.tint }]}
                        >
                            <MaterialCommunityIcons
                                name="sort-ascending"
                                size={14}
                                color={sortBy === 'symbol' ? 'white' : Colors.text}
                            />
                            <Text style={[styles.sortButtonText, sortBy === 'symbol' && { color: 'white', fontWeight: '700' }]}>
                                Symbol
                            </Text>
                        </TouchableOpacity>

                        <TouchableOpacity
                            onPress={() => toggleSortOrder('price')}
                            style={[styles.sortButton, sortBy === 'price' && { backgroundColor: Colors.tint }]}
                        >
                            <MaterialCommunityIcons
                                name="currency-usd"
                                size={14}
                                color={sortBy === 'price' ? 'white' : Colors.text}
                            />
                            <Text style={[styles.sortButtonText, sortBy === 'price' && { color: 'white', fontWeight: '700' }]}>
                                Price
                            </Text>
                        </TouchableOpacity>

                        <TouchableOpacity
                            onPress={() => toggleSortOrder('changePercent')}
                            style={[styles.sortButton, sortBy === 'changePercent' && { backgroundColor: Colors.tint }]}
                        >
                            <MaterialCommunityIcons
                                name="percent"
                                size={14}
                                color={sortBy === 'changePercent' ? 'white' : Colors.text}
                            />
                            <Text style={[styles.sortButtonText, sortBy === 'changePercent' && { color: 'white', fontWeight: '700' }]}>
                                Change
                            </Text>
                        </TouchableOpacity>

                        <TouchableOpacity
                            onPress={() => toggleSortOrder('sector')}
                            style={[styles.sortButton, sortBy === 'sector' && { backgroundColor: Colors.tint }]}
                        >
                            <MaterialCommunityIcons
                                name="tag"
                                size={14}
                                color={sortBy === 'sector' ? 'white' : Colors.text}
                            />
                            <Text style={[styles.sortButtonText, sortBy === 'sector' && { color: 'white', fontWeight: '700' }]}>
                                Sector
                            </Text>
                        </TouchableOpacity>
                    </View>
                )}

                {filteredAndSortedWatchlist.length > 0 ? (
                    <View style={styles.listContent}>
                        {filteredAndSortedWatchlist.map(stock => {
                            const sectorColor = sectorColors[stock.sector as keyof typeof sectorColors] || sectorColors['Technology'];
                            return (
                                <WatchlistCard
                                    key={stock.id}
                                    stock={stock}
                                    colors={Colors}
                                    sectorColor={sectorColor}
                                    onRemove={handleRemoveFromWatchlist}
                                />
                            );
                        })}
                    </View>
                ) : (
                    <View style={styles.emptyState}>
                        <MaterialCommunityIcons
                            name="heart-outline"
                            size={56}
                            color={Colors.text}
                            style={{ opacity: 0.3, marginBottom: 16 }}
                        />
                        <Text style={[styles.emptyStateTitle, { color: Colors.text }]}>
                            No stocks in watchlist
                        </Text>
                        <Text style={[styles.emptyStateSubtitle, { color: Colors.text, opacity: 0.6 }]}>
                            Add stocks from search to get started
                        </Text>
                    </View>
                )}
            </ScrollView>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        marginTop: -40,
    },
    loadingContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        gap: 16,
    },
    loadingText: {
        fontSize: 14,
        fontWeight: '600',
    },
    errorContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        paddingHorizontal: 32,
        gap: 12,
    },
    errorTitle: {
        fontSize: 20,
        fontWeight: '700',
        marginTop: 16,
    },
    errorMessage: {
        fontSize: 14,
        textAlign: 'center',
    },
    retryButton: {
        marginTop: 16,
        paddingHorizontal: 24,
        paddingVertical: 12,
        borderRadius: 10,
    },
    retryButtonText: {
        color: 'white',
        fontSize: 14,
        fontWeight: '700',
    },
    header: {
        paddingHorizontal: 12,
        paddingVertical: 16,
    },
    title: {
        fontSize: 28,
        fontWeight: '800',
        fontStyle: 'italic',
        marginBottom: 4,
    },
    subtitle: {
        fontSize: 13,
    },
    statsRow: {
        marginBottom: 16,
        borderWidth: 1,
        borderRadius: 12,
        paddingVertical: 12,
        flexDirection: 'row',
        justifyContent: 'space-around',
    },
    statItem: {
        alignItems: 'center',
        gap: 4,
    },
    statLabel: {
        fontSize: 11,
        fontWeight: '600',
    },
    statValue: {
        fontSize: 16,
        fontWeight: '700',
    },
    statDivider: {
        width: 1,
        height: 40,
    },
    sectorFilter: {
        marginBottom: 12,
    },
    sectorFilterContent: {
        gap: 8,
        paddingRight: 12,
    },
    sectorFilterButton: {
        paddingHorizontal: 12,
        paddingVertical: 6,
        borderRadius: 8,
        backgroundColor: '#F0F0F0',
    },
    sectorFilterText: {
        fontSize: 12,
        fontWeight: '600',
        color: '#666',
    },
    sortContainer: {
        paddingHorizontal: 24,
        marginBottom: 16,
        flexDirection: 'row',
        gap: 8,
    },
    sortButton: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        paddingVertical: 8,
        borderRadius: 8,
        backgroundColor: '#F0F0F0',
        gap: 4,
    },
    sortButtonText: {
        fontSize: 11,
        fontWeight: '600',
        color: '#666',
    },
    listContent: {
        paddingBottom: 24,
        gap: 12,
    },
    watchlistCard: {
        borderWidth: 1,
        borderRadius: 12,
        padding: 12,
    },
    cardHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    cardLeft: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        gap: 10,
    },
    sectorBadge: {
        width: 44,
        height: 44,
        borderRadius: 10,
        alignItems: 'center',
        justifyContent: 'center',
    },
    sectorBadgeText: {
        fontSize: 14,
        fontWeight: '700',
    },
    stockInfo: {
        flex: 1,
    },
    symbol: {
        fontSize: 13,
        fontWeight: '700',
        marginBottom: 2,
    },
    name: {
        fontSize: 11,
        fontWeight: '500',
    },
    cardRight: {
        alignItems: 'flex-end',
        gap: 4,
    },
    price: {
        fontSize: 13,
        fontWeight: '700',
    },
    changeBadge: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 3,
        paddingHorizontal: 8,
        paddingVertical: 4,
        borderRadius: 6,
    },
    changeText: {
        fontSize: 11,
        fontWeight: '700',
    },
    chevron: {
        position: 'absolute',
        right: 12,
        top: '50%',
        marginTop: -10,
        opacity: 0.4,
    },
    expandedContent: {
        marginTop: 12,
        paddingTop: 12,
        borderTopWidth: 1,
        gap: 12,
    },
    rangeSection: {
        flexDirection: 'row',
        justifyContent: 'space-around',
    },
    rangeItem: {
        alignItems: 'center',
        gap: 4,
    },
    rangeLabel: {
        fontSize: 10,
        fontWeight: '600',
    },
    rangeValue: {
        fontSize: 12,
        fontWeight: '700',
    },
    actionButtons: {
        flexDirection: 'row',
        gap: 10,
    },
    actionButton: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        paddingVertical: 10,
        borderRadius: 10,
        gap: 6,
    },
    actionButtonText: {
        fontSize: 12,
        fontWeight: '700',
        color: 'white',
    },
    emptyState: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        paddingHorizontal: 24,
        paddingVertical: 60,
    },
    emptyStateTitle: {
        fontSize: 18,
        fontWeight: '700',
        marginBottom: 8,
    },
    emptyStateSubtitle: {
        fontSize: 13,
        textAlign: 'center',
    },
});
