<%@ page import="com.bimalsha.ee.bank.entity.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Get user from session
    User user = (User) session.getAttribute("user");
    String userName = user != null ? user.getName() : "Guest";
%>
<html>
<head>
    <title>National Bank - Transaction History</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        'bank-blue': '#1a5276',
                        'bank-blue-dark': '#154360',
                        'bank-accent': '#3498db'
                    }
                }
            }
        }
    </script>
</head>
<body class="bg-gray-50 font-sans min-h-screen">
<!-- Navigation -->
<nav class="bg-gradient-to-r from-bank-blue to-bank-blue-dark text-white shadow-md">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
            <div class="flex items-center">
                <a href="home.jsp" class="flex items-center">
                    <svg class="w-8 h-8 mr-2" viewBox="0 0 24 24">
                        <path fill="currentColor" d="M12 2L2 8h20L12 2zm0 3l6 3H6l6-3zm-8 5v10h4v-6h8v6h4V10H4zm10 10v-4h-4v4h4z"/>
                    </svg>
                    <span class="font-bold text-xl">National Bank</span>
                </a>
            </div>
            <div class="flex items-center">
                <div class="flex items-center">
                    <span class="mr-1"><%= userName %></span>
                    <form action="logout" method="get" style="display:inline;" class="flex items-center">
                        <button type="submit" class="bg-bank-blue-dark hover:bg-bank-accent px-3 py-1 rounded-md transition-colors">
                            Logout
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</nav>

<!-- Main Content -->
<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Page Header -->
    <div class="mb-8 flex justify-between items-center">
        <div>
            <h1 class="text-2xl font-bold text-gray-800 mb-2">Transaction History</h1>
            <p class="text-gray-600">View and manage your recent transactions</p>
        </div>
        <a href="home.jsp" class="bg-bank-blue hover:bg-bank-blue-dark text-white px-4 py-2 rounded-md transition-colors">
            Back to Dashboard
        </a>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100 mb-8 p-4">
        <div class="flex flex-wrap gap-4">
            <div>
                <label for="transaction-type" class="block text-sm font-medium text-gray-700 mb-1">Transaction Type</label>
                <select id="transaction-type" class="border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-bank-accent">
                    <option value="all">All Transactions</option>
                    <option value="sent">Money Sent</option>
                    <option value="received">Money Received</option>
                </select>
            </div>
            <div>
                <label for="account-filter" class="block text-sm font-medium text-gray-700 mb-1">Account</label>
                <select id="account-filter" class="border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-bank-accent">
                    <option value="">All Accounts</option>
                    <!-- Account options will be populated via JavaScript -->
                </select>
            </div>
            <div class="self-end">
                <button id="apply-filters" class="bg-bank-blue hover:bg-bank-blue-dark text-white px-4 py-2 rounded-md transition-colors">
                    Apply Filters
                </button>
            </div>
        </div>
    </div>

    <!-- Transactions Table -->
    <div class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100">
        <div id="transaction-error" class="text-red-500 text-sm text-center py-2 hidden"></div>
        <div class="overflow-x-auto">
            <table id="transactions-table" class="w-full">
                <thead>
                <tr class="bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    <th class="px-6 py-3">Description</th>
                    <th class="px-6 py-3">Date</th>
                    <th class="px-6 py-3">Category</th>
                    <th class="px-6 py-3">Amount</th>
                    <th class="px-6 py-3">Status</th>
                </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                <!-- Transaction rows will be loaded here -->
                <tr>
                    <td colspan="5" class="px-6 py-4 text-center text-gray-500">
                        Loading transactions...
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Footer -->
<footer class="bg-gray-50 border-t border-gray-200 py-8 mt-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col md:flex-row justify-between items-center">
            <div class="flex items-center mb-4 md:mb-0">
                <svg class="w-8 h-8 mr-2 text-bank-blue" viewBox="0 0 24 24">
                    <path fill="currentColor" d="M12 2L2 8h20L12 2zm0 3l6 3H6l6-3zm-8 5v10h4v-6h8v6h4V10H4zm10 10v-4h-4v4h4z"/>
                </svg>
                <span class="font-bold text-xl text-bank-blue">National Bank</span>
            </div>
            <div class="text-sm text-gray-500">
                &copy; 2025 National Bank. All rights reserved.
            </div>
        </div>
    </div>
</footer>

<script>
    // Initialize when the DOM is ready
    document.addEventListener("DOMContentLoaded", function() {
        // Set userId as a JavaScript variable
        var currentUserId = <%= session.getAttribute("userId") != null ? session.getAttribute("userId") : "null" %>;

        // Load accounts for filter dropdown
        loadUserAccounts();

        // Load all transactions initially
        loadTransactions();

        // Set up filter button event listener
        document.getElementById('apply-filters').addEventListener('click', function() {
            loadTransactions();
        });
    });

    function loadUserAccounts() {
        fetch('accounts', {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to load account data');
                }
                return response.json();
            })
            .then(data => {
                const accountSelect = document.getElementById('account-filter');
                // Keep the "All Accounts" option
                accountSelect.innerHTML = '<option value="">All Accounts</option>';

                if (data.accounts && data.accounts.length > 0) {
                    data.accounts.forEach(account => {
                        const option = document.createElement('option');
                        option.value = account.accountNumber;
                        option.textContent = account.accountType + ' (' + account.accountNumber + ')';
                        accountSelect.appendChild(option);
                    });
                }
            })
            .catch(error => {
                console.error('Error loading accounts:', error);
            });
    }

    function loadTransactions() {
        // Get filter values
        const transactionType = document.getElementById('transaction-type').value;
        const accountNumber = document.getElementById('account-filter').value;

        // Show loading state
        const transactionsTable = document.getElementById('transactions-table');
        const tbody = transactionsTable.querySelector('tbody');
        tbody.innerHTML = '<tr><td colspan="5" class="px-6 py-4 text-center text-gray-500">Loading transactions...</td></tr>';

        // Hide any previous errors
        document.getElementById('transaction-error').classList.add('hidden');

        // Build query parameters
        let queryParams = [];
        if (transactionType && transactionType !== 'all') {
            queryParams.push('type=' + transactionType);
        }
        if (accountNumber) {
            queryParams.push('accountNumber=' + accountNumber);
        }

        const queryString = queryParams.length > 0 ? '?' + queryParams.join('&') : '';

        fetch('transactionHistory' + queryString, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to load transaction data');
                }
                return response.json();
            })
            .then(data => {
                tbody.innerHTML = '';
                console.log("Transaction data received:", data);

                const currentUserId = data.currentUserId;

                if (data.transactions && data.transactions.length > 0) {
                    data.transactions.forEach(transaction => {
                        const row = createTransactionRow(transaction, currentUserId);
                        tbody.appendChild(row);
                    });
                } else {
                    // No transactions found
                    const emptyRow = document.createElement('tr');
                    emptyRow.innerHTML = '<td colspan="5" class="px-6 py-4 text-center text-gray-500">No transactions found</td>';
                    tbody.appendChild(emptyRow);
                }
            })
            .catch(error => {
                console.error('Error loading transactions:', error);
                document.getElementById('transaction-error').textContent = 'Error loading transaction data';
                document.getElementById('transaction-error').classList.remove('hidden');
            });
    }

    function createTransactionRow(transaction, currentUserId) {
        const row = document.createElement('tr');

        // Determine if this is an outgoing or incoming transaction
        const isOutgoing = transaction.fromAccount.user &&
            transaction.fromAccount.user.id === currentUserId;

        // Description cell with account information and reason
        const descCell = document.createElement('td');
        descCell.className = 'px-6 py-4';

        // Get the other party name or account number
        const otherParty = isOutgoing ?
            (transaction.toAccount.user ? transaction.toAccount.user.name : 'Account #' + transaction.toAccount.accountNumber) :
            (transaction.fromAccount.user ? transaction.fromAccount.user.name : 'Account #' + transaction.fromAccount.accountNumber);

        // Create description HTML content
        let descHtml = '<div class="flex flex-col">';
        descHtml += '<div class="text-sm font-medium text-gray-900">';
        descHtml += (isOutgoing ? 'To: ' : 'From: ') + otherParty;
        descHtml += '</div>';
        descHtml += '<div class="text-xs text-gray-500 mt-1">';
        descHtml += '<div>From: ' + transaction.fromAccount.accountNumber + '</div>';
        descHtml += '<div>To: ' + transaction.toAccount.accountNumber + '</div>';
        descHtml += '</div>';

        // Add reason if available
        if (transaction.reason) {
            descHtml += '<div class="text-xs italic text-gray-500 mt-1">' + transaction.reason + '</div>';
        }

        descHtml += '</div>';
        descCell.innerHTML = descHtml;

        // Date cell
        const dateCell = document.createElement('td');
        dateCell.className = 'px-6 py-4';
        const date = new Date(transaction.dateTime);
        const formattedDate = date.toLocaleDateString();
        const formattedTime = date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});

        let dateHtml = '<div class="text-sm font-medium text-gray-900">' + formattedDate + '</div>';
        dateHtml += '<div class="text-xs text-gray-500">' + formattedTime + '</div>';
        dateCell.innerHTML = dateHtml;

        // Type cell
        const typeCell = document.createElement('td');
        typeCell.className = 'px-6 py-4';
        typeCell.innerHTML = '<span class="text-sm font-medium">Transfer</span>';

        // Amount cell with proper formatting
        const amountCell = document.createElement('td');
        amountCell.className = 'px-6 py-4';
        const amountFormatted = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'LKR',
            minimumFractionDigits: 2
        }).format(transaction.amount);

        let amountHtml = '<div class="text-sm ' + (isOutgoing ? 'text-red-600' : 'text-green-600') + ' font-bold">';
        amountHtml += (isOutgoing ? '-' : '+') + ' ' + amountFormatted;
        amountHtml += '</div>';
        amountCell.innerHTML = amountHtml;

        // Status cell
        const statusCell = document.createElement('td');
        statusCell.className = 'px-6 py-4';
        statusCell.innerHTML = '<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">Completed</span>';

        // Add all cells to the row
        row.appendChild(descCell);
        row.appendChild(dateCell);
        row.appendChild(typeCell);
        row.appendChild(amountCell);
        row.appendChild(statusCell);

        return row;
    }
</script>
</body>
</html>