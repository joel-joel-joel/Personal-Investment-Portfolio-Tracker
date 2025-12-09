/**
 * Price History Service
 * Handles stock price history data retrieval
 */

import { apiFetch } from './api';
import type { PriceHistoryDTO, PriceHistoryCreateRequest } from '../types/api';

// ============================================================================
// Price History Functions
// ============================================================================

/**
 * Get all price history records for a specific stock
 */
export const getPriceHistoryForStock = async (stockId: string): Promise<PriceHistoryDTO[]> => {
  try {
    return await apiFetch<PriceHistoryDTO[]>(`/api/pricehistory/stock/${stockId}`, {
      method: 'GET',
      requireAuth: false,
    });
  } catch (error) {
    console.error('Failed to fetch price history:', error);
    throw error;
  }
};

/**
 * Get latest price for a specific stock
 */
export const getLatestPriceForStock = async (stockId: string): Promise<PriceHistoryDTO> => {
  try {
    return await apiFetch<PriceHistoryDTO>(`/api/pricehistory/stock/${stockId}/latest`, {
      method: 'GET',
      requireAuth: false,
    });
  } catch (error) {
    console.error('Failed to fetch latest price:', error);
    throw error;
  }
};

/**
 * Get all price history records
 */
export const getAllPriceHistory = async (): Promise<PriceHistoryDTO[]> => {
  try {
    return await apiFetch<PriceHistoryDTO[]>('/api/pricehistory', {
      method: 'GET',
      requireAuth: false,
    });
  } catch (error) {
    console.error('Failed to fetch all price history:', error);
    throw error;
  }
};

/**
 * Create a new price history record
 */
export const createPriceHistory = async (
  request: PriceHistoryCreateRequest
): Promise<PriceHistoryDTO> => {
  try {
    return await apiFetch<PriceHistoryDTO>('/api/pricehistory', {
      method: 'POST',
      requireAuth: true,
      body: JSON.stringify(request),
    });
  } catch (error) {
    console.error('Failed to create price history:', error);
    throw error;
  }
};

/**
 * Delete a price history record
 */
export const deletePriceHistory = async (priceHistoryId: string): Promise<void> => {
  try {
    await apiFetch<void>(`/api/pricehistory/${priceHistoryId}`, {
      method: 'DELETE',
      requireAuth: true,
    });
  } catch (error) {
    console.error('Failed to delete price history:', error);
    throw error;
  }
};

/**
 * Filter price history by time range
 * This is a client-side helper function
 */
export const filterPriceHistoryByTimeRange = (
  priceHistory: PriceHistoryDTO[],
  timeframe: '1D' | '1W' | '1M' | '3M' | '1Y'
): PriceHistoryDTO[] => {
  const now = new Date();
  let startDate: Date;

  switch (timeframe) {
    case '1D':
      startDate = new Date(now.getTime() - 24 * 60 * 60 * 1000);
      break;
    case '1W':
      startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
      break;
    case '1M':
      startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
      break;
    case '3M':
      startDate = new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000);
      break;
    case '1Y':
      startDate = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000);
      break;
    default:
      startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
  }

  return priceHistory
    .filter(ph => new Date(ph.closeDate) >= startDate)
    .sort((a, b) => new Date(a.closeDate).getTime() - new Date(b.closeDate).getTime());
};
