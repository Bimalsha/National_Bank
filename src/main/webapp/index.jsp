<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>National Bank - Login</title>
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
<body class="bg-gray-50 font-sans h-screen">
<div class="flex h-screen">
    <!-- Decorative side panel -->
    <div class="hidden md:flex md:w-1/2 bg-gradient-to-br from-bank-blue to-bank-blue-dark text-white flex-col justify-center items-center p-12">
        <div class="mb-8 transform transition-all animate-pulse">
            <svg class="w-24 h-24" viewBox="0 0 24 24">
                <path fill="white" d="M12 2L2 8h20L12 2zm0 3l6 3H6l6-3zm-8 5v10h4v-6h8v6h4V10H4zm10 10v-4h-4v4h4z"/>
            </svg>
        </div>
        <h1 class="text-4xl font-bold mb-4 text-center">National Bank</h1>
        <p class="text-xl text-center text-blue-100 mb-8">Secure Banking Solutions</p>
        <div class="space-y-6 max-w-md">
            <div class="flex items-center">
                <div class="bg-white/20 p-2 rounded-full mr-4">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                    </svg>
                </div>
                <span>Bank-grade security and encryption</span>
            </div>
            <div class="flex items-center">
                <div class="bg-white/20 p-2 rounded-full mr-4">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
                    </svg>
                </div>
                <span>Fast and reliable online banking</span>
            </div>
            <div class="flex items-center">
                <div class="bg-white/20 p-2 rounded-full mr-4">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path>
                    </svg>
                </div>
                <span>24/7 access to your accounts</span>
            </div>
        </div>
    </div>

    <!-- Login form -->
    <div class="w-full md:w-1/2 flex items-center justify-center p-8">
        <div class="max-w-md w-full">
            <!-- Mobile logo only -->
            <div class="md:hidden text-center mb-8">
                <svg class="w-16 h-16 mx-auto" viewBox="0 0 24 24">
                    <path fill="#1a5276" d="M12 2L2 8h20L12 2zm0 3l6 3H6l6-3zm-8 5v10h4v-6h8v6h4V10H4zm10 10v-4h-4v4h4z"/>
                </svg>
                <h1 class="text-bank-blue text-2xl font-bold mt-2">National Bank</h1>
            </div>

            <div class="bg-white rounded-xl shadow-lg p-8 border border-gray-100">
                <h2 class="text-2xl font-bold text-gray-800 mb-6">Welcome Back</h2>
                <p class="text-gray-600 mb-8">Please sign in to access your account</p>

                <form id="loginForm" action="login" method="post">
                    <div class="mb-5">
                        <label for="username" class="block text-gray-700 font-medium mb-2">Username</label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                </svg>
                            </div>
                            <input type="text" id="username" name="username" required
                                   class="w-full pl-10 px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg text-base focus:outline-none focus:ring-2 focus:ring-bank-accent focus:border-transparent transition-all">
                        </div>
                        <div id="usernameError" class="text-red-500 text-sm mt-1 hidden">Please enter your username</div>
                    </div>

                    <div class="mb-6">
                        <div class="flex justify-between mb-2">
                            <label for="password" class="block text-gray-700 font-medium">Password</label>
                            <a href="forgot-password.jsp" class="text-bank-accent text-sm hover:underline">Forgot Password?</a>
                        </div>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                                </svg>
                            </div>
                            <input type="password" id="password" name="password" required
                                   class="w-full pl-10 px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg text-base focus:outline-none focus:ring-2 focus:ring-bank-accent focus:border-transparent transition-all">
                        </div>
                        <div id="passwordError" class="text-red-500 text-sm mt-1 hidden">Please enter your password</div>
                    </div>

                    <button type="submit"
                            class="w-full py-3 bg-gradient-to-r from-bank-blue to-bank-accent text-white rounded-lg text-base font-medium
                                       shadow-md hover:shadow-lg transform hover:-translate-y-0.5 transition-all duration-200">
                        Sign In
                    </button>
                </form>

                <div class="mt-6 text-center text-gray-500 text-sm">
                    <p>Don't have an account? <a href="#" class="text-bank-accent hover:underline">Contact your branch</a></p>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.getElementById('loginForm').addEventListener('submit', function(event) {
        let isValid = true;
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();

        if (username === '') {
            document.getElementById('usernameError').classList.remove('hidden');
            isValid = false;
        } else {
            document.getElementById('usernameError').classList.add('hidden');
        }

        if (password === '') {
            document.getElementById('passwordError').classList.remove('hidden');
            isValid = false;
        } else {
            document.getElementById('passwordError').classList.add('hidden');
        }

        if (!isValid) {
            event.preventDefault();
        }
    });
</script>
</body>
</html>