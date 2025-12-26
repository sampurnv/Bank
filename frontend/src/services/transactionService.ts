import api from './api';
import { Transaction, TransactionRequest, TransferRequest } from '../types';

export const transactionService = {
  deposit: async (data: TransactionRequest): Promise<Transaction> => {
    const response = await api.post('/transactions/deposit', data);
    return response.data;
  },

  withdraw: async (data: TransactionRequest): Promise<Transaction> => {
    const response = await api.post('/transactions/withdraw', data);
    return response.data;
  },

  transfer: async (data: TransferRequest): Promise<Transaction> => {
    const response = await api.post('/transactions/transfer', data);
    return response.data;
  },

  getTransactionHistory: async (
    accountId: string,
    page: number = 0,
    size: number = 10
  ): Promise<any> => {
    const response = await api.get(
      `/transactions/account/${accountId}?page=${page}&size=${size}&sortBy=createdAt&direction=DESC`
    );
    return response.data;
  },
};
