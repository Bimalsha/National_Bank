<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bimalsha.ee.bank.entity.User" %>
<%@ page import="com.bimalsha.ee.bank.entity.Account" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>National Bank - Transfer Money</title>
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
<body class="bg-gray-50 font-sans p-6">
<div class="max-w-2xl mx-auto">
    <!-- Header -->
    <div class="flex items-center mb-6">
        <svg class="w-8 h-8 mr-2 text-bank-blue" viewBox="0 0 24 24">
            <path fill="currentColor" d="M12 2L2 8h20L12 2zm0 3l6 3H6l6-3zm-8 5v10h4v-6h8v6h4V10H4zm10 10v-4h-4v4h4z"/>
        </svg>
        <h1 class="text-2xl font-bold text-bank-blue">Transfer Money</h1>
    </div>

    <%-- Get current user and their accounts --%>
    <%
        User currentUser = (User) session.getAttribute("user");
        List<Account> accounts = null;

        if (currentUser != null) {
            // Get accounts from userAccounts session attribute
            accounts = (List<Account>) session.getAttribute("userAccounts");

            // Debug output
            if (accounts == null) {
                out.println("<!-- DEBUG: userAccounts is null -->");
            } else {
                out.println("<!-- DEBUG: Found " + accounts.size() + " accounts -->");
            }
        }
    %>

    <!-- Transfer Form -->
    <div class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100 p-6">
        <!-- Transfer Options Tabs -->
        <div class="border-b border-gray-200 mb-6">
            <ul class="flex -mb-px">
                <li class="mr-2">
                    <a href="#" onclick="showTab('standard')" id="standard-tab" class="inline-block py-2 px-4 text-bank-blue font-medium border-b-2 border-bank-blue">Standard Transfer</a>
                </li>
                <li>
                    <a href="#" onclick="showTab('scheduled')" id="scheduled-tab" class="inline-block py-2 px-4 text-gray-500 hover:text-bank-blue font-medium border-b-2 border-transparent">Scheduled Transfer</a>
                </li>
            </ul>
        </div>

        <!-- Standard Transfer Form -->
        <div id="standard" class="tab-content">
            <form id="standardTransferForm">
                <div class="mb-5">
                    <label for="fromAccount" class="block text-gray-700 font-medium mb-2">From Account</label>
                    <select id="fromAccount" name="fromAccount" class="w-full px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue" onchange="updateBalance('fromAccount', 'accountBalance')">
                        <option value="">Select an account</option>
                        <% if (accounts != null && !accounts.isEmpty()) {
                            for (Account account : accounts) {
                                String accountNum = account.getAccountNumber() != null ?
                                        "**** " + account.getAccountNumber().substring(Math.max(0, account.getAccountNumber().length() - 4)) :
                                        "**** ****";
                                String balance = String.format("LKR%.2f", account.getBalance());
                                String accountTypeStr = account.getAccountType() != null ? account.getAccountType().getType() : "N/A";
                        %>
                        <option value="<%= account.getAccountNumber() %>"><%= accountTypeStr %> Account (<%= accountNum %>) - <%= balance %></option>
                        <%
                            }
                        } else {
                        %>
                        <option value="" disabled>No accounts available</option>
                        <% } %>
                    </select>
                    <div id="accountBalance" class="mt-2 text-sm text-gray-600 hidden">
                        Available balance: <span id="balanceAmount" class="font-medium">LKR 0.00</span>
                    </div>
                </div>

                <div class="mb-5">
                    <label for="toAccountNumber" class="block text-gray-700 font-medium mb-2">To Account Number</label>
                    <input type="text" id="toAccountNumber" name="toAccountNumber" class="w-full px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue" placeholder="Enter recipient's account number" onblur="validateAccountNumber('toAccountNumber', 'recipientName')">
                </div>

                <div class="mb-5">
                    <label for="recipientName" class="block text-gray-700 font-medium mb-2">Account Name</label>
                    <input type="text" id="recipientName" name="recipientName" class="w-full px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue" placeholder="Enter recipient's full name" readonly>
                    <div id="recipientNameValidation" class="mt-1 text-sm hidden"></div>
                </div>

                <div class="mb-5">
                    <label for="amount" class="block text-gray-700 font-medium mb-2">Amount</label>
                    <div class="relative">
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <span class="text-gray-500">LKR</span>
                        </div>
                        <input type="text" id="amount" name="amount" class="w-full pl-12 px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue" placeholder="0.00">
                    </div>
                </div>

                <div class="mb-6">
                    <label for="transferNote" class="block text-gray-700 font-medium mb-2">Reason</label>
                    <input type="text" id="transferNote" name="transferNote" class="w-full px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue" placeholder="Add more details about this transfer">
                </div>

                <div class="flex gap-4">
                    <button type="submit" class="flex-1 py-3 bg-gradient-to-r from-bank-blue to-bank-accent text-white rounded-md text-base font-medium shadow-md hover:shadow-lg transition-all">
                        Transfer Now
                    </button>
                    <button type="button" onclick="window.close()" class="flex-1 py-3 bg-gray-200 text-gray-700 rounded-md text-base font-medium hover:bg-gray-300 transition-all">
                        Cancel
                    </button>
                </div>
            </form>
        </div>

        <!-- Scheduled Transfers Form -->
        <div id="scheduled" class="tab-content hidden">
            <form id="scheduledTransferForm">
                <div class="mb-5">
                    <label for="fromAccountSch" class="block text-gray-700 font-medium mb-2">From Account</label>
                    <select id="fromAccountSch" name="fromAccountSch" class="w-full px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue" onchange="updateBalance('fromAccountSch', 'accountBalanceSch')">
                        <option value="">Select an account</option>
                        <% if (accounts != null && !accounts.isEmpty()) {
                            for (Account account : accounts) {
                                String accountNum = account.getAccountNumber() != null ?
                                        "**** " + account.getAccountNumber().substring(Math.max(0, account.getAccountNumber().length() - 4)) :
                                        "**** ****";
                                String balance = String.format("LKR%.2f", account.getBalance());
                                String accountTypeStr = account.getAccountType() != null ? account.getAccountType().getType() : "N/A";
                        %>
                        <option value="<%= account.getAccountNumber() %>"><%= accountTypeStr %> Account (<%= accountNum %>) - <%= balance %></option>
                        <%
                            }
                        } else {
                        %>
                        <option value="" disabled>No accounts available</option>
                        <% } %>
                    </select>
                    <div id="accountBalanceSch" class="mt-2 text-sm text-gray-600 hidden">
                        Available balance: <span id="balanceAmountSch" class="font-medium">LKR 0.00</span>
                    </div>
                </div>

                <div class="mb-5">
                    <label for="toAccountNumberSch" class="block text-gray-700 font-medium mb-2">To Account Number</label>
                    <input type="text" id="toAccountNumberSch" name="toAccountNumberSch" class="w-full px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue" placeholder="Enter recipient's account number" onblur="validateAccountNumber('toAccountNumberSch', 'recipientNameSch')">
                </div>

                <div class="mb-5">
                    <label for="recipientNameSch" class="block text-gray-700 font-medium mb-2">Account Name</label>
                    <input type="text" id="recipientNameSch" name="recipientNameSch" class="w-full px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue" placeholder="Enter recipient's full name" readonly>
                    <div id="recipientNameSchValidation" class="mt-1 text-sm hidden"></div>
                </div>

                <div class="mb-5">
                    <label for="amountSch" class="block text-gray-700 font-medium mb-2">Amount</label>
                    <div class="relative">
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <span class="text-gray-500">LKR</span>
                        </div>
                        <input type="text" id="amountSch" name="amountSch" class="w-full pl-12 px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue" placeholder="0.00">
                    </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-5">
                    <div>
                        <label for="startDate" class="block text-gray-700 font-medium mb-2">Date Time</label>
                        <input type="datetime-local" id="startDate" name="startDate" class="w-full px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue">
                    </div>
                </div>

                <div class="mb-6">
                    <label for="transferNoteSch" class="block text-gray-700 font-medium mb-2">Additional Note</label>
                    <input type="text" id="transferNoteSch" name="transferNoteSch" class="w-full px-3 py-3 border border-gray-300 rounded-md text-base focus:outline-none focus:border-bank-blue" placeholder="Add more details about this transfer">
                </div>

                <div class="flex gap-4">
                    <button type="submit" class="flex-1 py-3 bg-gradient-to-r from-bank-blue to-bank-accent text-white rounded-md text-base font-medium shadow-md hover:shadow-lg transition-all">
                        Schedule Transfer
                    </button>
                    <button type="button" onclick="window.close()" class="flex-1 py-3 bg-gray-200 text-gray-700 rounded-md text-base font-medium hover:bg-gray-300 transition-all">
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Transaction Limits Notice -->
    <div class="mt-6 bg-yellow-50 border border-yellow-200 rounded-lg p-4 text-sm text-yellow-800">
        <div class="flex">
            <svg class="h-5 w-5 text-yellow-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <div>
                <p class="font-medium">Transaction Limits:</p>
                <p>Daily limit: $10,000 | Weekly limit: $50,000</p>
                <p>External transfers may take 1-3 business days to process.</p>
            </div>
        </div>
    </div>
</div>

<script>
    // Function to switch between tabs
    function showTab(tabId) {
        // Hide all tab contents
        document.querySelectorAll('.tab-content').forEach(tab => {
            tab.classList.add('hidden');
        });

        // Show the selected tab content
        document.getElementById(tabId).classList.remove('hidden');

        // Update active tab styling
        document.querySelectorAll('a[id$="-tab"]').forEach(tab => {
            tab.classList.remove('text-bank-blue', 'border-bank-blue');
            tab.classList.add('text-gray-500', 'border-transparent');
        });

        document.getElementById(tabId + '-tab').classList.remove('text-gray-500', 'border-transparent');
        document.getElementById(tabId + '-tab').classList.add('text-bank-blue', 'border-bank-blue');
    }

    // Function to update balance display
    function updateBalance(selectId, balanceContainerId) {
        const selectElement = document.getElementById(selectId);
        const balanceContainer = document.getElementById(balanceContainerId);
        const balanceText = selectId === 'fromAccount' ?
            document.getElementById('balanceAmount') :
            document.getElementById('balanceAmountSch');

        if (selectElement.value === '') {
            balanceContainer.classList.add('hidden');
            return;
        }

        // Extract balance from the selected option text
        const selectedOption = selectElement.options[selectElement.selectedIndex];
        const optionText = selectedOption.text;
        // Match "LKR" followed by any digits, commas, and a decimal point with 2 digits
        const balanceMatch = optionText.match(/LKR([0-9.,]+)/);

        if (balanceMatch) {
            balanceText.textContent = 'LKR ' + balanceMatch[1].trim();
            balanceContainer.classList.remove('hidden');
        } else {
            balanceContainer.classList.add('hidden');
        }
    }

    // Function to validate account number and fetch account holder name
    function validateAccountNumber(accountFieldId, nameFieldId) {
        const accountNumber = document.getElementById(accountFieldId).value.trim();
        const nameField = document.getElementById(nameFieldId);
        const validationDiv = document.getElementById(nameFieldId + 'Validation');

        if (!accountNumber) {
            nameField.value = '';
            nameField.readOnly = true;
            validationDiv.classList.add('hidden');
            return;
        }

        // Show loading state
        nameField.value = 'Checking...';
        nameField.readOnly = true;
        validationDiv.textContent = '';
        validationDiv.classList.add('hidden');

        // Make AJAX call to validate account
        fetch('validateAccount?accountNumber=' + encodeURIComponent(accountNumber), {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data.valid) {
                    // Account found, set the name
                    nameField.value = data.accountHolderName;
                    nameField.readOnly = true;
                    nameField.classList.remove('border-red-500');
                    validationDiv.classList.add('hidden');
                } else {
                    // Account not found, allow manual entry
                    nameField.value = '';
                    nameField.readOnly = false;
                    nameField.focus();
                    validationDiv.textContent = data.message;
                    validationDiv.classList.remove('hidden');
                    validationDiv.classList.remove('text-red-500');
                    validationDiv.classList.add('text-yellow-600');
                }
            })
            .catch(error => {
                console.error('Error validating account:', error);
                nameField.value = '';
                nameField.readOnly = false;
                nameField.focus();
                validationDiv.textContent = 'Error validating account. Please enter recipient name manually.';
                validationDiv.classList.remove('hidden');
                validationDiv.classList.remove('text-red-500');
                validationDiv.classList.add('text-yellow-600');
            });
    }

    // Replace the existing DOMContentLoaded event listener with this updated version
    document.addEventListener('DOMContentLoaded', function() {
        // Set form action and method attributes for both forms
        const standardForm = document.getElementById('standardTransferForm');
        standardForm.setAttribute('action', 'transferMoney');
        standardForm.setAttribute('method', 'post');

        const scheduledForm = document.getElementById('scheduledTransferForm');
        scheduledForm.setAttribute('action', 'transferMoney');
        scheduledForm.setAttribute('method', 'post');

        // Client-side validation for standard transfer form
        standardForm.addEventListener('submit', function(e) {
            if (!validateForm('standard')) {
                e.preventDefault(); // Stop form submission if validation fails
            } else {
                // Set a flag to track form submission
                sessionStorage.setItem('transferSubmitted', 'true');
            }
        });

        // Client-side validation for scheduled transfer form
        scheduledForm.addEventListener('submit', function(e) {
            if (!validateForm('scheduled')) {
                e.preventDefault(); // Stop form submission if validation fails
            } else {
                // Set a flag to track form submission
                sessionStorage.setItem('transferSubmitted', 'true');
            }
        });

        // Display error messages from request attributes if any
        const errorMessages = <%= request.getAttribute("errors") != null ?
                           "JSON.parse('" + request.getAttribute("errors").toString().replace("'", "\\'") + "')" :
                           "null" %>;

        if (errorMessages) {
            for (const [field, message] of Object.entries(errorMessages)) {
                displayError(field, message);
            }

            // Show general error at the top if exists
            if (errorMessages.general) {
                showErrorAlert(errorMessages.general);
            }

            // Clear the submission flag if there were errors
            sessionStorage.removeItem('transferSubmitted');
        }

        // Display success message from session if any
        const transferSuccess = <%= session.getAttribute("transferSuccess") != null ?
                             session.getAttribute("transferSuccess") : "false" %>;

        if (transferSuccess) {
            const successMessage = "<%= session.getAttribute("transferMessage") != null ?
                               session.getAttribute("transferMessage") : "Transfer successful" %>";

            // Show success message
            showSuccessAlert(successMessage);

            <% session.removeAttribute("transferSuccess"); %>
            <% session.removeAttribute("transferMessage"); %>

            // Clear the submission flag
            sessionStorage.removeItem('transferSubmitted');

            // Close window and refresh parent after showing success message
            setTimeout(() => {
                // If this window was opened by another window (popup)
                if (window.opener && !window.opener.closed) {
                    // Refresh the parent/opener window
                    window.opener.location.reload();
                    // Close this window
                    window.close();
                } else {
                    // If not a popup, close window and redirect to home
                    window.location.href = 'home.jsp';
                    window.close();
                }
            }, 2000); // Wait 2 seconds to show the success message
        }

        // Check if returning from a successful form submission
        if (sessionStorage.getItem('transferSubmitted') === 'true' &&
            !errorMessages && !<%= request.getAttribute("errorMessage") != null %>) {

            // If we have the submission flag but no errors, and no success message was set in the session,
            // assume the operation succeeded but didn't set the session attributes
            if (!transferSuccess) {
                const defaultSuccessMsg = "Transfer completed successfully";
                showSuccessAlert(defaultSuccessMsg);

                // Clear the submission flag
                sessionStorage.removeItem('transferSubmitted');

                // Close window and refresh parent
                setTimeout(() => {
                    if (window.opener && !window.opener.closed) {
                        window.opener.location.reload();
                        window.close();
                    } else {
                        window.location.href = 'home.jsp';
                        window.close();
                    }
                }, 2000);
            }
        }

        // Form validation function
        function validateForm(formType) {
            let isValid = true;
            clearErrors();

            if (formType === 'standard') {
                // Validate source account
                if (!document.getElementById('fromAccount').value) {
                    displayError('fromAccount', 'Source account is required');
                    isValid = false;
                }

                // Validate destination account
                if (!document.getElementById('toAccountNumber').value) {
                    displayError('toAccountNumber', 'Destination account is required');
                    isValid = false;
                }

                // Validate amount
                const amount = document.getElementById('amount').value;
                if (!amount) {
                    displayError('amount', 'Amount is required');
                    isValid = false;
                } else if (parseFloat(amount.replace(',', '')) <= 0) {
                    displayError('amount', 'Amount must be greater than zero');
                    isValid = false;
                }
            } else {
                // Validate source account (scheduled)
                if (!document.getElementById('fromAccountSch').value) {
                    displayError('fromAccountSch', 'Source account is required');
                    isValid = false;
                }

                // Validate destination account (scheduled)
                if (!document.getElementById('toAccountNumberSch').value) {
                    displayError('toAccountNumberSch', 'Destination account is required');
                    isValid = false;
                }

                // Validate amount (scheduled)
                const amount = document.getElementById('amountSch').value;
                if (!amount) {
                    displayError('amountSch', 'Amount is required');
                    isValid = false;
                } else if (parseFloat(amount.replace(',', '')) <= 0) {
                    displayError('amountSch', 'Amount must be greater than zero');
                    isValid = false;
                }

                // Validate scheduled date
                if (!document.getElementById('startDate').value) {
                    displayError('startDate', 'Schedule date is required');
                    isValid = false;
                }
            }

            return isValid;
        }

        // Helper function to display field errors
        function displayError(fieldId, message) {
            const field = document.getElementById(fieldId);
            if (!field) return;

            field.classList.add('border-red-500');

            // Create error message element
            const errorDiv = document.createElement('div');
            errorDiv.className = 'text-red-500 text-sm mt-1 error-message';
            errorDiv.textContent = message;

            // Insert after the field
            field.parentNode.insertBefore(errorDiv, field.nextSibling);
        }

        // Helper function to clear all errors
        function clearErrors() {
            document.querySelectorAll('.error-message').forEach(el => el.remove());
            document.querySelectorAll('.border-red-500').forEach(el => {
                el.classList.remove('border-red-500');
            });
        }

        // Function to show success alert
        function showSuccessAlert(message) {
            const alertDiv = document.createElement('div');
            alertDiv.className = 'bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4';
            alertDiv.innerHTML = `
            <div class="flex">
                <svg class="h-5 w-5 text-green-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
                <span>${message}</span>
            </div>
        `;

            // Insert at the top of the form container
            const container = document.querySelector('.max-w-2xl');
            container.insertBefore(alertDiv, container.firstChild);
        }

        // Function to show error alert
        function showErrorAlert(message) {
            const alertDiv = document.createElement('div');
            alertDiv.className = 'bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4';
            alertDiv.innerHTML = `
            <div class="flex">
                <svg class="h-5 w-5 text-red-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
                <span>${message}</span>
            </div>
        `;

            // Insert at the top of the form container
            const container = document.querySelector('.max-w-2xl');
            container.insertBefore(alertDiv, container.firstChild);
        }
    });
</script>
</body>
</html>