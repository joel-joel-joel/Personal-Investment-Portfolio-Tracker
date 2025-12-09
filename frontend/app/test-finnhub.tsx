import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  ActivityIndicator,
  ScrollView,
  useColorScheme,
} from 'react-native';
import { getThemeColors } from '@/src/constants/colors';
import { getStockQuote, getCompanyProfile } from '@/src/services/entityService';
import type { FinnhubQuoteDTO, FinnhubCompanyProfileDTO } from '@/src/types/api';

export default function TestFinnhubScreen() {
  const colorScheme = useColorScheme();
  const Colors = getThemeColors(colorScheme);

  const [symbol, setSymbol] = useState('AAPL');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [quote, setQuote] = useState<FinnhubQuoteDTO | null>(null);
  const [profile, setProfile] = useState<FinnhubCompanyProfileDTO | null>(null);

  const fetchStockData = async () => {
    if (!symbol.trim()) {
      setError('Please enter a stock symbol');
      return;
    }

    setLoading(true);
    setError(null);
    setQuote(null);
    setProfile(null);

    try {
      // Fetch quote and profile in parallel
      const [quoteData, profileData] = await Promise.all([
        getStockQuote(symbol.toUpperCase()),
        getCompanyProfile(symbol.toUpperCase()),
      ]);

      setQuote(quoteData);
      setProfile(profileData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch stock data');
      console.error('Error fetching stock data:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView
      style={[styles.container, { backgroundColor: Colors.background }]}
      contentContainerStyle={styles.contentContainer}
    >
      <Text style={[styles.title, { color: Colors.text }]}>
        Finnhub API Test
      </Text>

      <View style={styles.inputContainer}>
        <TextInput
          style={[styles.input, {
            backgroundColor: Colors.card,
            color: Colors.text,
            borderColor: Colors.border,
          }]}
          value={symbol}
          onChangeText={setSymbol}
          placeholder="Enter stock symbol (e.g., AAPL)"
          placeholderTextColor={Colors.text + '80'}
          autoCapitalize="characters"
          autoCorrect={false}
        />

        <TouchableOpacity
          style={[styles.button, { backgroundColor: Colors.tint }]}
          onPress={fetchStockData}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#FFF" />
          ) : (
            <Text style={styles.buttonText}>Fetch Data</Text>
          )}
        </TouchableOpacity>
      </View>

      {error && (
        <View style={[styles.errorContainer, { backgroundColor: '#fee2e2' }]}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      )}

      {quote && (
        <View style={[styles.card, { backgroundColor: Colors.card, borderColor: Colors.border }]}>
          <Text style={[styles.cardTitle, { color: Colors.text }]}>
            Stock Quote
          </Text>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>Current Price:</Text>
            <Text style={[styles.value, { color: Colors.tint }]}>
              ${quote.c?.toFixed(2) || 'N/A'}
            </Text>
          </View>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>High:</Text>
            <Text style={[styles.value, { color: Colors.text }]}>
              ${quote.h?.toFixed(2) || 'N/A'}
            </Text>
          </View>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>Low:</Text>
            <Text style={[styles.value, { color: Colors.text }]}>
              ${quote.l?.toFixed(2) || 'N/A'}
            </Text>
          </View>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>Open:</Text>
            <Text style={[styles.value, { color: Colors.text }]}>
              ${quote.o?.toFixed(2) || 'N/A'}
            </Text>
          </View>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>Previous Close:</Text>
            <Text style={[styles.value, { color: Colors.text }]}>
              ${quote.pc?.toFixed(2) || 'N/A'}
            </Text>
          </View>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>Timestamp:</Text>
            <Text style={[styles.value, { color: Colors.text }]}>
              {quote.t ? new Date(quote.t * 1000).toLocaleString() : 'N/A'}
            </Text>
          </View>
        </View>
      )}

      {profile && (
        <View style={[styles.card, { backgroundColor: Colors.card, borderColor: Colors.border }]}>
          <Text style={[styles.cardTitle, { color: Colors.text }]}>
            Company Profile
          </Text>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>Name:</Text>
            <Text style={[styles.value, { color: Colors.text }]} numberOfLines={2}>
              {profile.name || 'N/A'}
            </Text>
          </View>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>Ticker:</Text>
            <Text style={[styles.value, { color: Colors.text }]}>
              {profile.ticker || 'N/A'}
            </Text>
          </View>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>Industry:</Text>
            <Text style={[styles.value, { color: Colors.text }]}>
              {profile.finnhubIndustry || 'N/A'}
            </Text>
          </View>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>Country:</Text>
            <Text style={[styles.value, { color: Colors.text }]}>
              {profile.country || 'N/A'}
            </Text>
          </View>
          <View style={styles.dataRow}>
            <Text style={[styles.label, { color: Colors.text }]}>Market Cap:</Text>
            <Text style={[styles.value, { color: Colors.text }]}>
              ${profile.marketCapitalization ? (profile.marketCapitalization / 1000).toFixed(2) + 'B' : 'N/A'}
            </Text>
          </View>
          {profile.weburl && (
            <View style={styles.dataRow}>
              <Text style={[styles.label, { color: Colors.text }]}>Website:</Text>
              <Text style={[styles.value, { color: Colors.tint }]} numberOfLines={1}>
                {profile.weburl}
              </Text>
            </View>
          )}
        </View>
      )}

      {!loading && !error && !quote && (
        <View style={styles.placeholderContainer}>
          <Text style={[styles.placeholderText, { color: Colors.text }]}>
            Enter a stock symbol and tap "Fetch Data" to test the Finnhub API
          </Text>
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  contentContainer: {
    padding: 24,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 24,
    textAlign: 'center',
  },
  inputContainer: {
    marginBottom: 24,
  },
  input: {
    height: 50,
    borderWidth: 1,
    borderRadius: 8,
    paddingHorizontal: 16,
    fontSize: 16,
    marginBottom: 12,
  },
  button: {
    height: 50,
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '600',
  },
  errorContainer: {
    padding: 16,
    borderRadius: 8,
    marginBottom: 16,
  },
  errorText: {
    color: '#dc2626',
    fontSize: 14,
  },
  card: {
    borderWidth: 1,
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
  },
  cardTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 16,
  },
  dataRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#e5e7eb',
  },
  label: {
    fontSize: 14,
    fontWeight: '500',
    flex: 1,
  },
  value: {
    fontSize: 14,
    fontWeight: '600',
    flex: 1,
    textAlign: 'right',
  },
  placeholderContainer: {
    padding: 32,
    alignItems: 'center',
  },
  placeholderText: {
    fontSize: 16,
    textAlign: 'center',
    opacity: 0.6,
  },
});
