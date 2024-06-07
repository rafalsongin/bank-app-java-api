import { defineStore } from 'pinia';
import axios from '../axios_auth';

export const useTransactionFetchStore = defineStore('transactions', {
    state: () => ({
        transactions: [],
    }),
    actions: {
        async fetchTransactionsByAccountIban(accountId, filters) {  // to fix issues id pushed
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    throw new Error('JWT token is missing');
                }

                const params = {
                    ...filters,
                  };

                //const response = await axios.get(`api/transactions/account/${accountIban}`, {
                const response = await axios.get(`api/transactions/accountId/${accountId}`, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                    params,
                });
                console.log(response, params);
                
                if (response.status === 200 || response.status === 201) {
                    this.transactions = response.data;
                } else if (response.status === 204) {
                    this.transactions = [];
                } else {
                    throw new Error("Error when fetching transactions:", error.message);
                }
            } catch (error) {
                this.transactions = [];
                throw new Error(error.message);
            }
        },
    },
});