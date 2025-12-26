import api from './api';
import { Account } from '../types';

export const accountService = {
  createAccount: async (userId: string, accountType: string): Promise<Account> => {
    const response = await api.post('/accounts', {
      userId,
      accountType,
      currency: 'USD',
    });
    return response.data;
  },

  getAccountsByUserId: async (userId: string): Promise<Account[]> => {
    const response = await api.get(`/accounts/user/${userId}`);
    return response.data;
  },

  getAccountById: async (accountId: string): Promise<Account> => {
    const response = await api.get(`/accounts/${accountId}`);
    return response.data;
  },

  getBalance: async (accountId: string): Promise<number> => {
    const response = await api.get(`/accounts/${accountId}/balance`);
    return response.data;
  },
};
