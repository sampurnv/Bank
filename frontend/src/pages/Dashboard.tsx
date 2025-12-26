import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { accountService } from '../services/accountService';
import { transactionService } from '../services/transactionService';
import { Account, Transaction } from '../types';
import '../styles/Dashboard.css';

const Dashboard: React.FC = () => {
  const { user, logout } = useAuth();
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [selectedAccount, setSelectedAccount] = useState<Account | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreateAccount, setShowCreateAccount] = useState(false);
  const [accountType, setAccountType] = useState('SAVINGS');
  const [showDepositForm, setShowDepositForm] = useState(false);
  const [showWithdrawForm, setShowWithdrawForm] = useState(false);
  const [showTransferForm, setShowTransferForm] = useState(false);
  const [amount, setAmount] = useState('');
  const [toAccountId, setToAccountId] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const loadData = async () => {
      if (!user) return;
      try {
        const data = await accountService.getAccountsByUserId(user.id);
        setAccounts(data);
        if (data.length > 0 && !selectedAccount) {
          setSelectedAccount(data[0]);
        }
      } catch (err) {
        console.error('Failed to load accounts', err);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [user, selectedAccount]);

  useEffect(() => {
    if (selectedAccount) {
      loadTransactions(selectedAccount.id);
    }
  }, [selectedAccount]);

  const loadAccounts = async () => {
    if (!user) return;
    try {
      const data = await accountService.getAccountsByUserId(user.id);
      setAccounts(data);
      if (data.length > 0) {
        const current = data.find(acc => acc.id === selectedAccount?.id) || data[0];
        setSelectedAccount(current);
      }
    } catch (err) {
      console.error('Failed to load accounts', err);
    }
  };

  const loadTransactions = async (accountId: string) => {
    try {
      const data = await transactionService.getTransactionHistory(accountId, 0, 20);
      setTransactions(data.content || []);
    } catch (err) {
      console.error('Failed to load transactions', err);
    }
  };

  const handleCreateAccount = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;
    try {
      await accountService.createAccount(user.id, accountType);
      setShowCreateAccount(false);
      loadAccounts();
      alert('Account created successfully!');
    } catch (err) {
      alert('Failed to create account');
    }
  };

  const handleDeposit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAccount) return;
    setError('');
    try {
      await transactionService.deposit({
        accountId: selectedAccount.id,
        amount: parseFloat(amount),
        description,
      });
      setShowDepositForm(false);
      setAmount('');
      setDescription('');
      loadAccounts();
      loadTransactions(selectedAccount.id);
      alert('Deposit successful!');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Deposit failed');
    }
  };

  const handleWithdraw = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAccount) return;
    setError('');
    try {
      await transactionService.withdraw({
        accountId: selectedAccount.id,
        amount: parseFloat(amount),
        description,
      });
      setShowWithdrawForm(false);
      setAmount('');
      setDescription('');
      loadAccounts();
      loadTransactions(selectedAccount.id);
      alert('Withdrawal successful!');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Withdrawal failed');
    }
  };

  const handleTransfer = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAccount) return;
    setError('');
    try {
      await transactionService.transfer({
        fromAccountId: selectedAccount.id,
        toAccountId,
        amount: parseFloat(amount),
        description,
      });
      setShowTransferForm(false);
      setAmount('');
      setToAccountId('');
      setDescription('');
      loadAccounts();
      loadTransactions(selectedAccount.id);
      alert('Transfer successful!');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Transfer failed');
    }
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>Bank Dashboard</h1>
        <div className="user-info">
          <span>Welcome, {user?.username}!</span>
          <button onClick={logout} className="btn btn-secondary">
            Logout
          </button>
        </div>
      </header>

      <div className="dashboard-content">
        <div className="accounts-section">
          <div className="section-header">
            <h2>My Accounts</h2>
            <button
              onClick={() => setShowCreateAccount(!showCreateAccount)}
              className="btn btn-primary"
            >
              + New Account
            </button>
          </div>

          {showCreateAccount && (
            <form onSubmit={handleCreateAccount} className="create-account-form">
              <select
                value={accountType}
                onChange={(e) => setAccountType(e.target.value)}
              >
                <option value="SAVINGS">Savings</option>
                <option value="CHECKING">Checking</option>
              </select>
              <button type="submit" className="btn btn-primary">
                Create
              </button>
            </form>
          )}

          <div className="accounts-list">
            {accounts.map((account) => (
              <div
                key={account.id}
                className={`account-card ${
                  selectedAccount?.id === account.id ? 'selected' : ''
                }`}
                onClick={() => setSelectedAccount(account)}
              >
                <div className="account-type">{account.accountType}</div>
                <div className="account-number">****{account.accountNumber.slice(-4)}</div>
                <div className="account-balance">{formatCurrency(account.balance)}</div>
              </div>
            ))}
          </div>
        </div>

        {selectedAccount && (
          <>
            <div className="transactions-section">
              <div className="section-header">
                <h2>Transactions</h2>
                <div className="transaction-actions">
                  <button
                    onClick={() => setShowDepositForm(!showDepositForm)}
                    className="btn btn-success"
                  >
                    Deposit
                  </button>
                  <button
                    onClick={() => setShowWithdrawForm(!showWithdrawForm)}
                    className="btn btn-warning"
                  >
                    Withdraw
                  </button>
                  <button
                    onClick={() => setShowTransferForm(!showTransferForm)}
                    className="btn btn-info"
                  >
                    Transfer
                  </button>
                </div>
              </div>

              {error && <div className="error-message">{error}</div>}

              {showDepositForm && (
                <form onSubmit={handleDeposit} className="transaction-form">
                  <h3>Deposit Money</h3>
                  <input
                    type="number"
                    placeholder="Amount"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    step="0.01"
                    min="0.01"
                    required
                  />
                  <input
                    type="text"
                    placeholder="Description (optional)"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                  />
                  <button type="submit" className="btn btn-success">
                    Deposit
                  </button>
                </form>
              )}

              {showWithdrawForm && (
                <form onSubmit={handleWithdraw} className="transaction-form">
                  <h3>Withdraw Money</h3>
                  <input
                    type="number"
                    placeholder="Amount"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    step="0.01"
                    min="0.01"
                    required
                  />
                  <input
                    type="text"
                    placeholder="Description (optional)"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                  />
                  <button type="submit" className="btn btn-warning">
                    Withdraw
                  </button>
                </form>
              )}

              {showTransferForm && (
                <form onSubmit={handleTransfer} className="transaction-form">
                  <h3>Transfer Money</h3>
                  <select
                    value={toAccountId}
                    onChange={(e) => setToAccountId(e.target.value)}
                    required
                  >
                    <option value="">Select destination account</option>
                    {accounts
                      .filter((acc) => acc.id !== selectedAccount.id)
                      .map((acc) => (
                        <option key={acc.id} value={acc.id}>
                          {acc.accountType} - ****{acc.accountNumber.slice(-4)}
                        </option>
                      ))}
                  </select>
                  <input
                    type="number"
                    placeholder="Amount"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    step="0.01"
                    min="0.01"
                    required
                  />
                  <input
                    type="text"
                    placeholder="Description (optional)"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                  />
                  <button type="submit" className="btn btn-info">
                    Transfer
                  </button>
                </form>
              )}

              <div className="transactions-list">
                <table>
                  <thead>
                    <tr>
                      <th>Date</th>
                      <th>Type</th>
                      <th>Amount</th>
                      <th>Description</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {transactions.map((transaction) => (
                      <tr key={transaction.id}>
                        <td>{formatDate(transaction.createdAt)}</td>
                        <td className={`type-${transaction.type.toLowerCase()}`}>
                          {transaction.type}
                        </td>
                        <td
                          className={
                            transaction.type === 'DEPOSIT' ? 'positive' : 'negative'
                          }
                        >
                          {transaction.type === 'DEPOSIT' ? '+' : '-'}
                          {formatCurrency(transaction.amount)}
                        </td>
                        <td>{transaction.description || '-'}</td>
                        <td>{transaction.status}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
