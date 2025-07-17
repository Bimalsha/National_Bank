<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<html>
<head>
    <title>National Bank - Employee Portal</title>
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
<body class="bg-gray-100 font-sans min-h-screen" onload="loadAccountTypes();">
<!-- Header -->
<header class="bg-bank-blue text-white shadow-md">
    <div class="container mx-auto px-4 py-3 flex justify-between items-center">
        <div class="flex items-center">
            <svg class="w-8 h-8 mr-2" viewBox="0 0 24 24">
                <path fill="currentColor" d="M12 2L2 8h20L12 2zm0 3l6 3H6l6-3zm-8 5v10h4v-6h8v6h4V10H4zm10 10v-4h-4v4h4z"/>
            </svg>
            <h1 class="text-xl font-bold">National Bank Employee Portal</h1>
        </div>
        <div class="flex items-center">
            <span class="mr-4">Welcome, Admin</span>

            <form action="logout" method="get" style="display:inline;">
                <button type="submit" class="bg-bank-blue-dark hover:bg-bank-accent px-3 py-1 rounded-md transition-colors">
                    Logout
                </button>
            </form>

        </div>
    </div>
</header>

<div class="flex min-h-screen">
    <!-- Sidebar Navigation -->
    <aside class="w-64 bg-white shadow-md">
        <nav class="mt-5">
            <div class="px-4 py-3 text-xs font-semibold text-gray-500 uppercase">
                Main
            </div>
            <a href="#" onclick="showSection('dashboard')" class="flex items-center px-4 py-3 text-bank-blue hover:bg-blue-50 border-l-4 border-bank-accent">
                <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h7"></path>
                </svg>
                Dashboard
            </a>
            <div class="px-4 py-3 text-xs font-semibold text-gray-500 uppercase">
                User Management
            </div>
            <a href="#" onclick="showSection('addUser')" class="flex items-center px-4 py-3 text-gray-700 hover:bg-blue-50 hover:text-bank-blue">
                <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"></path>
                </svg>
                Add New User
            </a>
            <a href="#" onclick="showSection('openAccount')" class="flex items-center px-4 py-3 text-gray-700 hover:bg-blue-50 hover:text-bank-blue">
                <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
                Open Account
            </a>
            <a href="#" onclick="showSection('searchUsers')" class="flex items-center px-4 py-3 text-gray-700 hover:bg-blue-50 hover:text-bank-blue">
                <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
                Search Users
            </a>
            <a href="#" onclick="showSection('updateUser')" class="flex items-center px-4 py-3 text-gray-700 hover:bg-blue-50 hover:text-bank-blue">
                <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                </svg>
                Update User Details
            </a>
            <div class="px-4 py-3 text-xs font-semibold text-gray-500 uppercase">
                Transaction Management
            </div>
            <a href="#" onclick="showSection('transactions')" class="flex items-center px-4 py-3 text-gray-700 hover:bg-blue-50 hover:text-bank-blue">
                <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
                </svg>
                Transaction History
            </a>
        </nav>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 p-6">
        <!-- Dashboard Section -->
        <section id="dashboard" class="section ">
            <h2 class="text-2xl font-semibold text-bank-blue mb-6">Dashboard</h2>
            <!-- Add these alerts for success/error messages -->
            <% if (request.getAttribute("successMessage") != null) { %>
            <div class="mb-4 p-3 bg-green-100 text-green-800 rounded">
                <%= request.getAttribute("successMessage") %>
            </div>
            <% } %>

            <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="mb-4 p-3 bg-red-100 text-red-800 rounded">
                <%= request.getAttribute("errorMessage") %>
            </div>
            <% } %>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
                <!-- Total Users Card -->
                <div class="bg-white rounded-lg shadow-md p-6">
                    <div class="flex items-center">
                        <div class="bg-blue-100 p-3 rounded-full">
                            <svg class="w-8 h-8 text-bank-blue" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                            </svg>
                        </div>
                        <div class="ml-4">
                            <h3 class="text-gray-500 text-sm">Total Users</h3>
                            <p class="text-2xl font-semibold text-gray-800">1,254</p>
                        </div>
                    </div>
                </div>

                <!-- Total Accounts Card -->
                <div class="bg-white rounded-lg shadow-md p-6">
                    <div class="flex items-center">
                        <div class="bg-green-100 p-3 rounded-full">
                            <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 6l3 1m0 0l-3 9a5.002 5.002 0 006.001 0M6 7l3 9M6 7l6-2m6 2l3-1m-3 1l-3 9a5.002 5.002 0 006.001 0M18 7l3 9m-3-9l-6-2m0-2v2m0 16V5m0 16H9m3 0h3"></path>
                            </svg>
                        </div>
                        <div class="ml-4">
                            <h3 class="text-gray-500 text-sm">Total Accounts</h3>
                            <p class="text-2xl font-semibold text-gray-800">2,867</p>
                        </div>
                    </div>
                </div>

                <!-- Transactions Card -->
                <div class="bg-white rounded-lg shadow-md p-6">
                    <div class="flex items-center">
                        <div class="bg-purple-100 p-3 rounded-full">
                            <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"></path>
                            </svg>
                        </div>
                        <div class="ml-4">
                            <h3 class="text-gray-500 text-sm">Today's Transactions</h3>
                            <p class="text-2xl font-semibold text-gray-800">342</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent Activities -->
            <div class="bg-white rounded-lg shadow-md p-6">
                <h3 class="text-lg font-semibold text-gray-800 mb-4">Recent Activities</h3>
                <div class="overflow-x-auto">
                    <table class="w-full text-sm text-left">
                        <thead class="bg-gray-50 text-gray-600 uppercase">
                        <tr>
                            <th class="px-4 py-3">Activity</th>
                            <th class="px-4 py-3">User</th>
                            <th class="px-4 py-3">Time</th>
                            <th class="px-4 py-3">Status</th>
                        </tr>
                        </thead>
                        <tbody class="divide-y">
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">New account created</td>
                            <td class="px-4 py-3">John Smith</td>
                            <td class="px-4 py-3">10:23 AM</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Completed</span></td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">Deposit</td>
                            <td class="px-4 py-3">Maria Johnson</td>
                            <td class="px-4 py-3">9:41 AM</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Completed</span></td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">User information update</td>
                            <td class="px-4 py-3">Robert Williams</td>
                            <td class="px-4 py-3">9:32 AM</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Completed</span></td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">Transfer</td>
                            <td class="px-4 py-3">Sarah Davis</td>
                            <td class="px-4 py-3">9:15 AM</td>
                            <td class="px-4 py-3"><span class="bg-yellow-100 text-yellow-800 text-xs px-2 py-1 rounded">Pending</span></td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">New user registration</td>
                            <td class="px-4 py-3">James Anderson</td>
                            <td class="px-4 py-3">8:52 AM</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Completed</span></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>

        <!-- Add User Section -->
        <section id="addUser" class="section  hidden">
            <h2 class="text-2xl font-semibold text-bank-blue mb-6">Add New User</h2>

            <div class="bg-white rounded-lg shadow-md p-6">
                <!-- Replace the existing form in the addUser section -->
                <form id="addUserForm" action="registerCustomer" method="post">
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                        <div>
                            <label for="name" class="block text-gray-700 font-medium mb-2">Name</label>
                            <input type="text" id="name" name="name" required class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                        <div>
                            <label for="email" class="block text-gray-700 font-medium mb-2">Email Address</label>
                            <input type="email" id="email" name="email" required class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                        <div>
                            <label for="confirmEmail" class="block text-gray-700 font-medium mb-2">Confirm Email</label>
                            <input type="email" id="confirmEmail" name="confirmEmail" required class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                            <div id="emailMatchError" class="text-red-500 text-sm mt-1 hidden">Email addresses do not match</div>
                        </div>
                        <div>
                            <label for="phone" class="block text-gray-700 font-medium mb-2">Phone Number</label>
                            <input type="tel" id="phone" name="phone" required class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                        <div>
                            <label for="password" class="block text-gray-700 font-medium mb-2">Password</label>
                            <input type="password" id="password" name="password" required class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                    </div>



                    <div class="flex justify-end">
                        <button type="reset" class="px-4 py-2 mr-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300">Reset</button>
                        <button type="submit" class="px-4 py-2 bg-bank-blue text-white rounded-md hover:bg-bank-blue-dark">Create User</button>
                    </div>
                </form>
            </div>
        </section>

        <!-- Open Account Section -->
        <section id="openAccount" class="section hidden">
            <h2 class="text-2xl font-semibold text-bank-blue mb-6">Open New Account</h2>

            <div class="bg-white rounded-lg shadow-md p-6">
                <form id="openAccountForm" action="openAccount" method="post">
                    <div class="mb-6">
                        <label for="userSearch" class="block text-gray-700 font-medium mb-2">Search User</label>
                        <div class="flex">
                            <input type="text" id="userSearch" placeholder="Enter user name, ID, or account number" class="flex-1 px-3 py-2 border border-gray-300 rounded-l-md focus:outline-none focus:border-bank-blue">
                            <button type="button" onclick="searchUser()" class="px-4 py-2 bg-bank-blue text-white rounded-r-md hover:bg-bank-blue-dark">Search</button>
                        </div>
                    </div>

                    <div class="mb-6 p-4 border border-gray-200 rounded-md bg-gray-50">
                        <h3 class="text-lg font-medium text-gray-800 mb-2">Selected User</h3>
                        <div class="flex items-center">
                            <div class="w-12 h-12 bg-bank-blue rounded-full flex items-center justify-center text-white text-xl font-bold">
                                JS
                            </div>
                            <div class="ml-4">
                                <p class="font-medium">John Smith</p>
                                <p class="text-sm text-gray-600">ID: 10052436 • Email: john.smith@example.com</p>
                            </div>
                        </div>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                        <div>
                            <label for="accountType" class="block text-gray-700 font-medium mb-2">Account Type</label>
                            <select id="accountType" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                                <option value="">Select Account Type</option>
                                <option value="checking">Checking Account</option>
                                <option value="savings">Savings Account</option>
                                <option value="fixed">Fixed Deposit</option>
                                <option value="loan">Loan Account</option>
                            </select>
                        </div>
                        <div>
                            <label for="initialDeposit" class="block text-gray-700 font-medium mb-2">Initial Deposit</label>
                            <div class="relative">
                                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <span class="text-gray-500">LKR</span>
                                </div>
                                <input type="text" id="initialDeposit" class="w-full pl-12 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue" placeholder="0.00">
                            </div>
                        </div>


                    </div>
                    <input type="hidden" id="hiddenUserId" name="userId" value="">
                    <div class="flex justify-end">
                        <button type="reset" class="px-4 py-2 mr-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300">Reset</button>
                        <button type="submit" class="px-4 py-2 bg-bank-blue text-white rounded-md hover:bg-bank-blue-dark">Open Account</button>
                    </div>
                </form>
            </div>
        </section>

        <!-- Search Users Section -->
        <section id="searchUsers" class="section hidden">
            <h2 class="text-2xl font-semibold text-bank-blue mb-6">Search Users</h2>

            <div class="bg-white rounded-lg shadow-md p-6 mb-6">
                <div class="flex flex-col md:flex-row space-y-4 md:space-y-0 md:space-x-4">
                    <div class="flex-1">
                        <input type="text" placeholder="Search by name, email, account number, or ID" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                    </div>
                    <div>
                        <select class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                            <option value="all">All Users</option>
                            <option value="active">Active Users</option>
                            <option value="inactive">Inactive Users</option>
                            <option value="pending">Pending Approval</option>
                        </select>
                    </div>
                    <div>
                        <button class="px-4 py-2 bg-bank-blue text-white rounded-md hover:bg-bank-blue-dark">Search</button>
                    </div>
                </div>
            </div>

            <div class="bg-white rounded-lg shadow-md overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="w-full text-sm text-left">
                        <thead class="bg-gray-50 text-gray-600 uppercase">
                        <tr>
                            <th class="px-4 py-3">User ID</th>
                            <th class="px-4 py-3">Name</th>
                            <th class="px-4 py-3">Email</th>
                            <th class="px-4 py-3">Phone</th>
                            <th class="px-4 py-3">Accounts</th>
                            <th class="px-4 py-3">Status</th>
                            <th class="px-4 py-3">Actions</th>
                        </tr>
                        </thead>
                        <tbody class="divide-y">
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">10052436</td>
                            <td class="px-4 py-3">John Smith</td>
                            <td class="px-4 py-3">john.smith@example.com</td>
                            <td class="px-4 py-3">555-123-4567</td>
                            <td class="px-4 py-3">2</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Active</span></td>
                            <td class="px-4 py-3">
                                <div class="flex space-x-2">
                                    <button class="text-blue-600 hover:text-blue-800">View</button>
                                    <button class="text-green-600 hover:text-green-800">Edit</button>
                                </div>
                            </td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">10052437</td>
                            <td class="px-4 py-3">Maria Johnson</td>
                            <td class="px-4 py-3">maria.johnson@example.com</td>
                            <td class="px-4 py-3">555-234-5678</td>
                            <td class="px-4 py-3">1</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Active</span></td>
                            <td class="px-4 py-3">
                                <div class="flex space-x-2">
                                    <button class="text-blue-600 hover:text-blue-800">View</button>
                                    <button class="text-green-600 hover:text-green-800">Edit</button>
                                </div>
                            </td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">10052438</td>
                            <td class="px-4 py-3">Robert Williams</td>
                            <td class="px-4 py-3">robert.williams@example.com</td>
                            <td class="px-4 py-3">555-345-6789</td>
                            <td class="px-4 py-3">3</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Active</span></td>
                            <td class="px-4 py-3">
                                <div class="flex space-x-2">
                                    <button class="text-blue-600 hover:text-blue-800">View</button>
                                    <button class="text-green-600 hover:text-green-800">Edit</button>
                                </div>
                            </td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">10052439</td>
                            <td class="px-4 py-3">Sarah Davis</td>
                            <td class="px-4 py-3">sarah.davis@example.com</td>
                            <td class="px-4 py-3">555-456-7890</td>
                            <td class="px-4 py-3">2</td>
                            <td class="px-4 py-3"><span class="bg-yellow-100 text-yellow-800 text-xs px-2 py-1 rounded">Pending</span></td>
                            <td class="px-4 py-3">
                                <div class="flex space-x-2">
                                    <button class="text-blue-600 hover:text-blue-800">View</button>
                                    <button class="text-green-600 hover:text-green-800">Edit</button>
                                </div>
                            </td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">10052440</td>
                            <td class="px-4 py-3">James Anderson</td>
                            <td class="px-4 py-3">james.anderson@example.com</td>
                            <td class="px-4 py-3">555-567-8901</td>
                            <td class="px-4 py-3">1</td>
                            <td class="px-4 py-3"><span class="bg-red-100 text-red-800 text-xs px-2 py-1 rounded">Inactive</span></td>
                            <td class="px-4 py-3">
                                <div class="flex space-x-2">
                                    <button class="text-blue-600 hover:text-blue-800">View</button>
                                    <button class="text-green-600 hover:text-green-800">Edit</button>
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="bg-gray-50 px-4 py-3 flex items-center justify-between">
                    <div class="text-sm text-gray-700">
                        Showing <span class="font-medium">1</span> to <span class="font-medium">5</span> of <span class="font-medium">42</span> results
                    </div>
                    <div class="flex space-x-2">
                        <button class="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white hover:bg-gray-50">Previous</button>
                        <button class="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white bg-bank-blue text-white">1</button>
                        <button class="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white hover:bg-gray-50">2</button>
                        <button class="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white hover:bg-gray-50">3</button>
                        <button class="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white hover:bg-gray-50">Next</button>
                    </div>
                </div>
            </div>
        </section>

        <!-- Update User Section -->
        <section id="updateUser" class="section hidden">
            <h2 class="text-2xl font-semibold text-bank-blue mb-6">Update User Details</h2>

            <div class="bg-white rounded-lg shadow-md p-6">
                <div class="mb-6">
                    <label for="updateUserSearch" class="block text-gray-700 font-medium mb-2">Search User to Update</label>
                    <div class="flex">
                        <input type="text" id="updateUserSearch" placeholder="Enter user name, ID, or account number" class="flex-1 px-3 py-2 border border-gray-300 rounded-l-md focus:outline-none focus:border-bank-blue">
                        <button type="button" class="px-4 py-2 bg-bank-blue text-white rounded-r-md hover:bg-bank-blue-dark">Search</button>
                    </div>
                </div>

                <div class="mb-6 p-4 border border-gray-200 rounded-md bg-gray-50">
                    <h3 class="text-lg font-medium text-gray-800 mb-2">Selected User</h3>
                    <div class="flex items-center">
                        <div class="w-12 h-12 bg-bank-blue rounded-full flex items-center justify-center text-white text-xl font-bold">
                            JS
                        </div>
                        <div class="ml-4">
                            <p class="font-medium">John Smith</p>
                            <p class="text-sm text-gray-600">ID: 10052436 • Email: john.smith@example.com</p>
                        </div>
                    </div>
                </div>

                <form>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                        <div>
                            <label for="updateFirstName" class="block text-gray-700 font-medium mb-2">First Name</label>
                            <input type="text" id="updateFirstName" value="John" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                        <div>
                            <label for="updateLastName" class="block text-gray-700 font-medium mb-2">Last Name</label>
                            <input type="text" id="updateLastName" value="Smith" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                        <div>
                            <label for="updateEmail" class="block text-gray-700 font-medium mb-2">Email Address</label>
                            <input type="email" id="updateEmail" value="john.smith@example.com" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                        <div>
                            <label for="updatePhone" class="block text-gray-700 font-medium mb-2">Phone Number</label>
                            <input type="tel" id="updatePhone" value="555-123-4567" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                        <div>
                            <label for="updateAddress" class="block text-gray-700 font-medium mb-2">Address</label>
                            <input type="text" id="updateAddress" value="123 Main St" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                        <div>
                            <label for="updateCity" class="block text-gray-700 font-medium mb-2">City</label>
                            <input type="text" id="updateCity" value="Anytown" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                        <div>
                            <label for="updateState" class="block text-gray-700 font-medium mb-2">State/Province</label>
                            <input type="text" id="updateState" value="CA" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                        <div>
                            <label for="updateZipCode" class="block text-gray-700 font-medium mb-2">ZIP/Postal Code</label>
                            <input type="text" id="updateZipCode" value="90210" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                        </div>
                    </div>

                    <div class="mb-6">
                        <label for="userStatus" class="block text-gray-700 font-medium mb-2">User Status</label>
                        <select id="userStatus" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                            <option value="active" selected>Active</option>
                            <option value="inactive">Inactive</option>
                            <option value="pending">Pending Approval</option>
                            <option value="suspended">Suspended</option>
                        </select>
                    </div>

                    <div class="flex justify-end">
                        <button type="button" class="px-4 py-2 mr-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300">Cancel</button>
                        <button type="submit" class="px-4 py-2 bg-bank-blue text-white rounded-md hover:bg-bank-blue-dark">Save Changes</button>
                    </div>
                </form>
            </div>
        </section>

        <!-- Transaction History Section -->
        <section id="transactions" class="section hidden">
            <h2 class="text-2xl font-semibold text-bank-blue mb-6">Transaction History</h2>

            <div class="bg-white rounded-lg shadow-md p-6 mb-6">
                <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
                    <div>
                        <label for="transactionUserSearch" class="block text-gray-700 font-medium mb-2">User/Account</label>
                        <input type="text" id="transactionUserSearch" placeholder="Search user or account" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                    </div>
                    <div>
                        <label for="transactionType" class="block text-gray-700 font-medium mb-2">Transaction Type</label>
                        <select id="transactionType" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                            <option value="all">All Types</option>
                            <option value="deposit">Deposit</option>
                            <option value="withdrawal">Withdrawal</option>
                            <option value="transfer">Transfer</option>
                            <option value="payment">Payment</option>
                        </select>
                    </div>
                    <div>
                        <label for="dateFrom" class="block text-gray-700 font-medium mb-2">Date From</label>
                        <input type="date" id="dateFrom" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                    </div>
                    <div>
                        <label for="dateTo" class="block text-gray-700 font-medium mb-2">Date To</label>
                        <input type="date" id="dateTo" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-bank-blue">
                    </div>
                </div>
                <div class="mt-4 flex justify-end">
                    <button class="px-4 py-2 bg-bank-blue text-white rounded-md hover:bg-bank-blue-dark">Search Transactions</button>
                </div>
            </div>

            <div class="bg-white rounded-lg shadow-md overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="w-full text-sm text-left">
                        <thead class="bg-gray-50 text-gray-600 uppercase">
                        <tr>
                            <th class="px-4 py-3">Transaction ID</th>
                            <th class="px-4 py-3">Date & Time</th>
                            <th class="px-4 py-3">User</th>
                            <th class="px-4 py-3">Account</th>
                            <th class="px-4 py-3">Type</th>
                            <th class="px-4 py-3">Amount</th>
                            <th class="px-4 py-3">Balance</th>
                            <th class="px-4 py-3">Status</th>
                        </tr>
                        </thead>
                        <tbody class="divide-y">
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3 font-medium">TRX-2023-07-14-001</td>
                            <td class="px-4 py-3">Jul 14, 2023 10:23 AM</td>
                            <td class="px-4 py-3">John Smith</td>
                            <td class="px-4 py-3">**** 4587</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Deposit</span></td>
                            <td class="px-4 py-3 text-green-600">+$1,500.00</td>
                            <td class="px-4 py-3">$24,562.00</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Completed</span></td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3 font-medium">TRX-2023-07-14-002</td>
                            <td class="px-4 py-3">Jul 14, 2023 09:41 AM</td>
                            <td class="px-4 py-3">Maria Johnson</td>
                            <td class="px-4 py-3">**** 8921</td>
                            <td class="px-4 py-3"><span class="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">Transfer</span></td>
                            <td class="px-4 py-3 text-red-600">-$250.00</td>
                            <td class="px-4 py-3">$12,756.00</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Completed</span></td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3 font-medium">TRX-2023-07-14-003</td>
                            <td class="px-4 py-3">Jul 14, 2023 09:32 AM</td>
                            <td class="px-4 py-3">Robert Williams</td>
                            <td class="px-4 py-3">**** 7654</td>
                            <td class="px-4 py-3"><span class="bg-red-100 text-red-800 text-xs px-2 py-1 rounded">Withdrawal</span></td>
                            <td class="px-4 py-3 text-red-600">-$500.00</td>
                            <td class="px-4 py-3">$6,736.00</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Completed</span></td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3 font-medium">TRX-2023-07-14-004</td>
                            <td class="px-4 py-3">Jul 14, 2023 09:15 AM</td>
                            <td class="px-4 py-3">Sarah Davis</td>
                            <td class="px-4 py-3">**** 5432</td>
                            <td class="px-4 py-3"><span class="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">Transfer</span></td>
                            <td class="px-4 py-3 text-red-600">-$1,200.00</td>
                            <td class="px-4 py-3">$8,432.00</td>
                            <td class="px-4 py-3"><span class="bg-yellow-100 text-yellow-800 text-xs px-2 py-1 rounded">Pending</span></td>
                        </tr>
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3 font-medium">TRX-2023-07-14-005</td>
                            <td class="px-4 py-3">Jul 14, 2023 08:52 AM</td>
                            <td class="px-4 py-3">James Anderson</td>
                            <td class="px-4 py-3">**** 1234</td>
                            <td class="px-4 py-3"><span class="bg-purple-100 text-purple-800 text-xs px-2 py-1 rounded">Payment</span></td>
                            <td class="px-4 py-3 text-red-600">-$850.00</td>
                            <td class="px-4 py-3">$4,321.00</td>
                            <td class="px-4 py-3"><span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">Completed</span></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="bg-gray-50 px-4 py-3 flex items-center justify-between">
                    <div class="text-sm text-gray-700">
                        Showing <span class="font-medium">1</span> to <span class="font-medium">5</span> of <span class="font-medium">128</span> transactions
                    </div>
                    <div class="flex space-x-2">
                        <button class="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white hover:bg-gray-50">Previous</button>
                        <button class="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white bg-bank-blue text-white">1</button>
                        <button class="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white hover:bg-gray-50">2</button>
                        <button class="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white hover:bg-gray-50">3</button>
                        <button class="px-3 py-1 border border-gray-300 rounded-md text-sm bg-white hover:bg-gray-50">Next</button>
                    </div>
                </div>
            </div>
        </section>
    </main>
</div>
// Add this script at the bottom of the page
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Get email fields for the Add User form
        const emailField = document.getElementById('email');
        const confirmEmailField = document.getElementById('confirmEmail');
        const emailError = document.getElementById('emailMatchError');
        const form = document.getElementById('addUserForm');

        // Function to check if emails match
        function checkEmailsMatch() {
            if (confirmEmailField.value && emailField.value !== confirmEmailField.value) {
                emailError.classList.remove('hidden');
                return false;
            } else {
                emailError.classList.add('hidden');
                return true;
            }
        }

        // Add event listeners
        if (confirmEmailField && emailField) {
            confirmEmailField.addEventListener('input', checkEmailsMatch);
            emailField.addEventListener('input', checkEmailsMatch);

            // Add form validation
            form.addEventListener('submit', function(e) {
                if (!checkEmailsMatch()) {
                    e.preventDefault();
                }
            });
        }
    });
    function showSection(sectionId) {
        console.log('Showing section:', sectionId);

        // Hide all sections
        document.querySelectorAll('.section').forEach(section => {
            section.classList.add('hidden');
        });

        // Show the selected section
        const selectedSection = document.getElementById(sectionId);
        if (selectedSection) {
            selectedSection.classList.remove('hidden');
        } else {
            console.error('Section not found:', sectionId);
            return;
        }

        // Update active menu item
        document.querySelectorAll('nav a').forEach(item => {
            item.classList.remove('border-l-4', 'border-bank-accent', 'text-bank-blue');
            item.classList.add('text-gray-700');
        });

        // Find and activate the clicked menu item
        const menuItem = document.querySelector(`nav a[onclick*="'${sectionId}'"]`);
        if (menuItem) {
            menuItem.classList.remove('text-gray-700');
            menuItem.classList.add('border-l-4', 'border-bank-accent', 'text-bank-blue');
        }
    }

</script>

<script>

    function showSection(sectionId) {
        console.log('Showing section:', sectionId);

        // Hide all sections
        document.querySelectorAll('.section').forEach(section => {
            section.classList.add('hidden');
        });

        // Show the selected section
        const selectedSection = document.getElementById(sectionId);
        if (selectedSection) {
            selectedSection.classList.remove('hidden');
        } else {
            console.error('Section not found:', sectionId);
            return;
        }

        // Update active menu item
        document.querySelectorAll('nav a').forEach(item => {
            item.classList.remove('border-l-4', 'border-bank-accent', 'text-bank-blue');
            item.classList.add('text-gray-700');
        });

        // Find and activate the clicked menu item
        const menuItem = document.querySelector('nav a[onclick*="\'' + sectionId + '\'"]');
        if (menuItem) {
            menuItem.classList.remove('text-gray-700');
            menuItem.classList.add('border-l-4', 'border-bank-accent', 'text-bank-blue');
        }
    }

</script>

<script>

    function searchUser() {

        const searchInput = document.getElementById('userSearch');
        if (!searchInput) {
            console.error('Search input element not found');
            return;
        }

        const searchTerm = searchInput.value.trim();

        // Get the selected user display area
        const selectedUserSection = document.querySelector('#openAccount .mb-6.p-4.border');

        if (!searchTerm) {
            alert('Please enter a search term');
            return;
        }

        // Show loading state
        selectedUserSection.innerHTML =
            '<h3 class="text-lg font-medium text-gray-800 mb-2">Searching...</h3>' +
            '<div class="flex items-center">' +
            '<div class="animate-pulse bg-gray-300 w-12 h-12 rounded-full"></div>' +
            '<div class="ml-4 animate-pulse">' +
            '<div class="h-4 bg-gray-300 rounded w-32 mb-2"></div>' +
            '<div class="h-3 bg-gray-300 rounded w-48"></div>' +
            '</div>' +
            '</div>';

        // Send request to the servlet
        fetch('customerSearchByEmail?searchTerm=' + encodeURIComponent(searchTerm) + '&searchType=email')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                if (data && data.length > 0) {
                    // Get the first user from the results
                    const user = data[0];

                    // Calculate initials from name
                    const initials = user.name ? user.name.split(' ')
                        .map(n => n.charAt(0))
                        .join('')
                        .toUpperCase() : 'U';

                    console.log('User found:', user);

                    // Update the selected user UI - avoid template literals
                    selectedUserSection.innerHTML =
                        '<h3 class="text-lg font-medium text-gray-800 mb-2">Selected User</h3>' +
                        '<div class="flex items-center">' +
                        '<div class="w-12 h-12 bg-bank-blue rounded-full flex items-center justify-center text-white text-xl font-bold">' +
                        initials +
                        '</div>' +
                        '<div class="ml-4">' +
                        '<p class="font-medium">' + (user.name || 'Unknown') + '</p>' +
                        '<p class="text-sm text-gray-600">ID: ' + user.id + ' • Email: ' + (user.email || 'N/A') + ' • Contact: ' + (user.contact || 'N/A') + '</p>' +
                        '<input type="hidden" name="userId" value="' + user.id + '" id="userId">' +
                        '</div>' +
                        '</div>';
                } else {
                    // No user found
                    console.log('No user found for search term:', searchTerm);
                    selectedUserSection.innerHTML =
                        '<h3 class="text-lg font-medium text-gray-800 mb-2">No User Found</h3>' +
                        '<p class="text-sm text-gray-600">No user was found with the search term: ' + searchTerm + '</p>';
                }
            })
            .catch(error => {
                console.error('Error searching for user:', error);
                selectedUserSection.innerHTML =
                    '<h3 class="text-lg font-medium text-gray-800 mb-2">Error</h3>' +
                    '<p class="text-sm text-red-600">An error occurred while searching for the user: ' + error.message + '</p>';
            });
    }

    function loadAccountTypes() {
        // Get the select element
        const accountTypeSelect = document.getElementById('accountType');

        if (!accountTypeSelect) {
            console.error('Account type select element not found');
            return;
        }

        // Show loading state
        accountTypeSelect.disabled = true;
        accountTypeSelect.innerHTML = '<option value="">Loading account types...</option>';

        // Fetch account types from the server
        fetch('loadAccountTypes')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                // Reset select with the default option
                accountTypeSelect.innerHTML = '<option value="">Select Account Type</option>';

                // Add each account type as an option
                data.forEach(accountType => {
                    const option = document.createElement('option');
                    option.value = accountType.type;
                    option.textContent = accountType.displayName;
                    accountTypeSelect.appendChild(option);
                });

                // Enable the select element
                accountTypeSelect.disabled = false;
            })
            .catch(error => {
                console.error('Error loading account types:', error);
                accountTypeSelect.innerHTML = '<option value="">Error loading account types</option>';
                accountTypeSelect.disabled = false;
            });
    }


    // Fix the openAccount form by adding name attributes to the elements
    function fixOpenAccountForm() {
        // 1. Add name attribute to accountType select element
        const accountTypeSelect = document.getElementById('accountType');
        if (accountTypeSelect) {
            accountTypeSelect.setAttribute('name', 'accountType');
        }

        // 2. Add name attribute to initialDeposit input element
        const initialDepositInput = document.getElementById('initialDeposit');
        if (initialDepositInput) {
            initialDepositInput.setAttribute('name', 'initialDeposit');
        }

        // 3. Ensure the user ID field exists and is properly named
        let userIdInput = document.getElementById('userId');
        if (!userIdInput) {
            // If userId doesn't exist, update the hiddenUserId to have id="userId"
            userIdInput = document.getElementById('hiddenUserId');
            if (userIdInput) {
                userIdInput.id = 'userId';
            } else {
                // Create it if it doesn't exist at all
                const form = document.getElementById('openAccountForm');
                userIdInput = document.createElement('input');
                userIdInput.type = 'hidden';
                userIdInput.id = 'userId';
                userIdInput.name = 'userId';
                userIdInput.value = '';
                form.appendChild(userIdInput);
            }
        }
    }

    // Call this function when the page loads
    document.addEventListener('DOMContentLoaded', function() {
        fixOpenAccountForm();
        loadAccountTypes();
    });

    document.addEventListener('DOMContentLoaded', function() {
        // Get references to the form and form elements
        const form = document.getElementById('openAccountForm');
        const accountTypeSelect = document.getElementById('accountType');
        const initialDepositInput = document.getElementById('initialDeposit');

        // Add name attributes to form controls
        if (accountTypeSelect) accountTypeSelect.setAttribute('name', 'accountType');
        if (initialDepositInput) initialDepositInput.setAttribute('name', 'initialDeposit');

        // Make sure we have only one userId field
        const existingUserIds = form.querySelectorAll('input[name="userId"]');
        if (existingUserIds.length > 1) {
            // Remove extra userId fields
            for (let i = 1; i < existingUserIds.length; i++) {
                existingUserIds[i].remove();
            }
        }

        // If no userId field exists, create one
        if (existingUserIds.length === 0) {
            const userIdInput = document.createElement('input');
            userIdInput.type = 'hidden';
            userIdInput.id = 'hiddenUserId';
            userIdInput.name = 'userId';
            form.appendChild(userIdInput);
        }

        // Update the form submission to validate inputs
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            // Get all form values
            const userId = form.querySelector('input[name="userId"]').value;
            const accountType = accountTypeSelect.value;
            const initialDeposit = initialDepositInput.value;

            console.log("Submitting form with:", {userId, accountType, initialDeposit});

            // Validate inputs
            if (!userId || userId.trim() === '') {
                alert('Please select a user first');
                return false;
            }

            if (!accountType || accountType.trim() === '') {
                alert('Please select an account type');
                return false;
            }

            if (!initialDeposit || initialDeposit.trim() === '' ||
                isNaN(parseFloat(initialDeposit)) || parseFloat(initialDeposit) <= 0) {
                alert('Please enter a valid initial deposit amount');
                return false;
            }

            // If all validations pass, submit the form
            this.submit();
        });
    });


</script>

</body>
</html>