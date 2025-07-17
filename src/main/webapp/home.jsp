<%@ page import="com.bimalsha.ee.bank.entity.User" %>
<%@ page import="com.bimalsha.ee.bank.entity.Account" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Get user from session
    User user = (User) session.getAttribute("user");
    String userName = user != null ? user.getName() : "Guest";
    String userEmail = user != null ? user.getEmail() : "";
    String userContact = user != null ? user.getContact() : "";


    // Get accounts from session
    List<Account> accounts = (List<Account>) session.getAttribute("userAccounts");
    Account primaryAccount = accounts != null && !accounts.isEmpty() ? accounts.get(0) : null;

    // Format account details
    String accountNumber = primaryAccount != null && primaryAccount.getAccountNumber() != null ?
            "**** " + primaryAccount.getAccountNumber().substring(Math.max(0, primaryAccount.getAccountNumber().length() - 4)) :
            "**** ****";
    String accountBalance = primaryAccount != null ?
            String.format("$%.2f", primaryAccount.getBalance()) :
            "$0.00";

    // Format date
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    String currentDate = dateFormat.format(new java.util.Date());
%>
<html>
<head>
    <title>National Bank - Dashboard</title>
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
<body class="bg-gray-50 font-sans min-h-screen" onload="LoadUserProfile();">
<!-- Navigation -->
<nav class="bg-gradient-to-r from-bank-blue to-bank-blue-dark text-white shadow-md">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
            <div class="flex items-center">
                <svg class="w-8 h-8 mr-2" viewBox="0 0 24 24">
                    <path fill="currentColor" d="M12 2L2 8h20L12 2zm0 3l6 3H6l6-3zm-8 5v10h4v-6h8v6h4V10H4zm10 10v-4h-4v4h4z"/>
                </svg>
                <span class="font-bold text-xl">National Bank</span>
            </div>
            <div class="flex items-center">
                <div class="mr-4 relative">
                    <button class="flex items-center focus:outline-none">
                        <svg class="w-6 h-6 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
                        </svg>
                    </button>
                </div>
                <div class="flex items-center">

                    <span class="mr-1"><%= userName %></span>
                    <form action="logout" method="get" style="display:inline;" class="flex items-center">
                        <button type="submit" class="bg-bank-blue-dark hover:bg-bank-accent px-3 py-1 rounded-md transition-colors ">
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
    <!-- Welcome and Quick Stats -->
    <div class="mb-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-2">Welcome back, <%= userName %>!</h1>
        <p class="text-gray-600">Here's your financial summary as of <%= currentDate %></p>
    </div>

    <!-- Account Overview -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <!-- Balance Card -->
        <div class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100">
            <div class="p-6">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-lg font-semibold text-gray-700">Current Balance</h3>
                    <span class="bg-blue-100 text-bank-blue text-xs px-2 py-1 rounded-full">Primary</span>
                </div>
                <p class="text-3xl font-bold text-gray-800 mb-1">LKR24,562.00</p>
                <p class="text-sm text-gray-500">Available Balance</p>
                <div class="mt-4 pt-4 border-t border-gray-100 flex justify-between">
                    <div>
                        <p class="text-xs text-gray-500">Account Number</p>
                        <p class="text-sm font-medium">424254587</p>
                    </div>
                    <div>
                        <p class="text-xs text-gray-500">Account Type</p>
                        <p class="text-sm font-medium">Checking</p>
                    </div>
                </div>
            </div>
        </div>


    </div>

    <!-- Quick Actions -->
    <div class="mb-8">
        <h2 class="text-xl font-bold text-gray-800 mb-4">Quick Actions</h2>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <a href="#" onclick="window.open('transfer.jsp', 'TransferMoney', 'width=800,height=700'); return false;" class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col items-center hover:shadow-md transition-all">
                <div class="bg-blue-100 p-3 rounded-full mb-3">
                    <svg class="w-6 h-6 text-bank-blue" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path>
                    </svg>
                </div>
                <span class="text-gray-700 font-medium">Send Money</span>
            </a>
            <a href="#" class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col items-center hover:shadow-md transition-all">
                <div class="bg-green-100 p-3 rounded-full mb-3">
                    <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3"></path>
                    </svg>
                </div>
                <span class="text-gray-700 font-medium">Receive Money</span>
            </a>
            <a href="#" class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col items-center hover:shadow-md transition-all">
                <div class="bg-purple-100 p-3 rounded-full mb-3">
                    <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
                    </svg>
                </div>
                <span class="text-gray-700 font-medium">Pay Bills</span>
            </a>
            <a href="#" class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col items-center hover:shadow-md transition-all">
                <div class="bg-yellow-100 p-3 rounded-full mb-3">
                    <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                </div>
                <span class="text-gray-700 font-medium">Investments</span>
            </a>
        </div>
    </div>

    <!-- Recent Transactions -->
    <div class="mb-8">
        <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-800">Recent Transactions</h2>
            <a href="#" class="text-bank-accent hover:underline text-sm font-medium">View All</a>
        </div>
        <div class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100">
            <div class="overflow-x-auto">
                <table class="w-full">
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
                    <tr>
                        <td class="px-6 py-4">
                            <div class="flex items-center">
                                <div class="bg-blue-100 p-2 rounded-full mr-3">
                                    <svg class="w-4 h-4 text-bank-blue" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3"></path>
                                    </svg>
                                </div>
                                <div>
                                    <div class="font-medium text-gray-900">Direct Deposit</div>
                                    <div class="text-gray-500 text-sm">Payroll</div>
                                </div>
                            </div>
                        </td>
                        <td class="px-6 py-4 text-sm text-gray-500">Jul 12, 2025</td>
                        <td class="px-6 py-4 text-sm text-gray-500">Income</td>
                        <td class="px-6 py-4 text-sm font-medium text-green-600">+$2,500.00</td>
                        <td class="px-6 py-4 text-sm">
                            <span class="bg-green-100 text-green-700 text-xs px-2 py-1 rounded-full">Completed</span>
                        </td>
                    </tr>
                    <tr>
                        <td class="px-6 py-4">
                            <div class="flex items-center">
                                <div class="bg-red-100 p-2 rounded-full mr-3">
                                    <svg class="w-4 h-4 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path>
                                    </svg>
                                </div>
                                <div>
                                    <div class="font-medium text-gray-900">Amazon Purchase</div>
                                    <div class="text-gray-500 text-sm">Shopping</div>
                                </div>
                            </div>
                        </td>
                        <td class="px-6 py-4 text-sm text-gray-500">Jul 10, 2025</td>
                        <td class="px-6 py-4 text-sm text-gray-500">Shopping</td>
                        <td class="px-6 py-4 text-sm font-medium text-red-600">-$129.99</td>
                        <td class="px-6 py-4 text-sm">
                            <span class="bg-green-100 text-green-700 text-xs px-2 py-1 rounded-full">Completed</span>
                        </td>
                    </tr>
                    <tr>
                        <td class="px-6 py-4">
                            <div class="flex items-center">
                                <div class="bg-red-100 p-2 rounded-full mr-3">
                                    <svg class="w-4 h-4 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path>
                                    </svg>
                                </div>
                                <div>
                                    <div class="font-medium text-gray-900">Electricity Bill</div>
                                    <div class="text-gray-500 text-sm">Utilities</div>
                                </div>
                            </div>
                        </td>
                        <td class="px-6 py-4 text-sm text-gray-500">Jul 8, 2025</td>
                        <td class="px-6 py-4 text-sm text-gray-500">Bills</td>
                        <td class="px-6 py-4 text-sm font-medium text-red-600">-$85.75</td>
                        <td class="px-6 py-4 text-sm">
                            <span class="bg-green-100 text-green-700 text-xs px-2 py-1 rounded-full">Completed</span>
                        </td>
                    </tr>
                    <tr>
                        <td class="px-6 py-4">
                            <div class="flex items-center">
                                <div class="bg-purple-100 p-2 rounded-full mr-3">
                                    <svg class="w-4 h-4 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"></path>
                                    </svg>
                                </div>
                                <div>
                                    <div class="font-medium text-gray-900">Transfer to Savings</div>
                                    <div class="text-gray-500 text-sm">Internal Transfer</div>
                                </div>
                            </div>
                        </td>
                        <td class="px-6 py-4 text-sm text-gray-500">Jul 5, 2025</td>
                        <td class="px-6 py-4 text-sm text-gray-500">Transfer</td>
                        <td class="px-6 py-4 text-sm font-medium text-red-600">-$500.00</td>
                        <td class="px-6 py-4 text-sm">
                            <span class="bg-green-100 text-green-700 text-xs px-2 py-1 rounded-full">Completed</span>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- User Profile & Settings -->
    <div class="mb-8">
        <h2 class="text-xl font-bold text-gray-800 mb-4">Account Information</h2>
        <div class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100">
            <div class="p-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <h3 class="text-lg font-semibold text-gray-700 mb-4">Personal Details</h3>
                        <div class="space-y-3">
                            <div>
                                <p class="text-sm text-gray-500">Full Name</p>
                                <p class="font-medium"><%=userName%></p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Email Address</p>
                                <p class="font-medium"><%=userEmail%></p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Phone Number</p>
                                <p class="font-medium"><%=userContact%></p>
                            </div>

                        </div>
                    </div>
                    <div>
                        <h3 class="text-lg font-semibold text-gray-700 mb-4">Account Details</h3>
                        <div class="space-y-3">
                            <div>
                                <p class="text-sm text-gray-500">Customer ID</p>
                                <p class="font-medium">NB1234567</p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Account Status</p>
                                <p class="font-medium text-green-600">Active</p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Account Type</p>
                                <p class="font-medium">Premier Banking</p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Branch</p>
                                <p class="font-medium">Downtown Branch (#123)</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Footer -->
<footer class="bg-gray-50 border-t border-gray-200 py-8">
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
</body>
<script>
    function LoadUserProfile() {
        // Check if user data is already loaded
        <% if (user != null && session.getAttribute("userId") != null) { %>
        // User data already loaded, don't fetch again
        console.log("User session already loaded. User ID: <%= session.getAttribute("userId") %>");
        return;
        <% } %>

        <% if (user != null) { %>
        // Pass userId as a URL parameter
        fetch('loadUserSession?userId=<%= user.getId() %>', {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        <% } else { %>
        // No user object available, try regular request
        fetch('loadUserSession', {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        <% } %>
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to load user data');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    // Instead of reloading the page, update the user info directly
                    console.log("User session loaded successfully");
                    // Only reload if necessary (first load)
                    <% if (user == null) { %>
                    location.reload();
                    <% } %>
                }
            })
            .catch(error => {
                console.error('Error loading user profile:', error);
            });
    }
</script>
</html>