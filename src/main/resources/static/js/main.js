// Mobile Menu Toggle
document.addEventListener('DOMContentLoaded', function() {
    const mobileToggle = document.querySelector('.mobile-toggle');
    const navMenu = document.querySelector('.nav-menu');
    
    if (mobileToggle && navMenu) {
        mobileToggle.addEventListener('click', function() {
            navMenu.classList.toggle('active');
            const icon = this.querySelector('i');
            if (navMenu.classList.contains('active')) {
                icon.classList.remove('fa-bars');
                icon.classList.add('fa-times');
            } else {
                icon.classList.remove('fa-times');
                icon.classList.add('fa-bars');
            }
        });
    }
    
    // Set active navigation based on current page
    setActiveNavigation();
});

// Function to set active navigation based on current page
function setActiveNavigation() {
    const currentPage = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-menu a');
    
    // Remove active class from all links
    navLinks.forEach(link => {
        link.classList.remove('active');
    });
    
    // Add active class to current page link
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPage) {
            link.classList.add('active');
        }
    });
}

// Function to format currency in Indian Rupees
function formatCurrency(amount) {
    // Format number with commas as per Indian numbering system
    return '₹' + amount.toLocaleString('en-IN');
}

// Function to navigate to apply page
function goToApply() {
    window.location.href = '/apply';
}

// Function to navigate to loan types page
function goToLoanTypes() {
    window.location.href = '/loan-types';
}

// Function to format input fields with rupee symbols
function formatCurrencyInputs() {
    // Add event listeners to currency input fields
    const loanAmountInput = document.getElementById('loanAmount');
    const monthlyIncomeInput = document.getElementById('monthlyIncome');
    
    if (loanAmountInput) {
        loanAmountInput.placeholder = 'Enter amount in ₹';
    }
    
    if (monthlyIncomeInput) {
        monthlyIncomeInput.placeholder = 'Enter income in ₹';
    }
}

// Call formatCurrencyInputs when DOM is loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', formatCurrencyInputs);
} else {
    formatCurrencyInputs();
}

// Function to validate form before submission
function validateForm() {
    const loanAmountInput = document.getElementById('loanAmount');
    const monthlyIncomeInput = document.getElementById('monthlyIncome');
    
    if (loanAmountInput) {
        const rawLoanAmount = loanAmountInput.value.toString().replace(/[₹,\s]/g, '');
        const loanAmount = parseFloat(rawLoanAmount);
        if (isNaN(loanAmount) || loanAmount < 10000 || loanAmount > 5000000) {
            alert('Please enter a valid loan amount between ₹10,000 and ₹50,00,000');
            return false;
        }
    }
    
    if (monthlyIncomeInput) {
        const rawMonthlyIncome = monthlyIncomeInput.value.toString().replace(/[₹,\s]/g, '');
        const monthlyIncome = parseFloat(rawMonthlyIncome);
        if (isNaN(monthlyIncome) || monthlyIncome < 10000) {
            alert('Please enter a valid monthly income of at least ₹10,000');
            return false;
        }
    }
    
    // Phone validation
    const phoneInput = document.getElementById('phone');
    if (phoneInput && phoneInput.value) {
        const rawPhoneValue = phoneInput.value.toString().replace(/\D/g, '');
        const phoneRegex = /^[6-9]\d{9}$/;
        if (!phoneRegex.test(rawPhoneValue) || rawPhoneValue.length !== 10) {
            alert('Please enter a valid 10-digit Indian phone number');
            return false;
        }
    }
    
    return true;
}

// Add form validation
document.addEventListener('DOMContentLoaded', function() {
    const loanForm = document.querySelector('.loan-form');
    if (loanForm) {
        loanForm.addEventListener('submit', function(e) {
            if (!validateForm()) {
                e.preventDefault();
            }
        });
    }
});

// Loan type selection functionality
document.addEventListener('DOMContentLoaded', function() {
    // Handle loan type links in navigation
    const loanTypeLinks = document.querySelectorAll('.loan-type-link');
    
    loanTypeLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const loanType = this.getAttribute('data-loan');
            alert(`You selected: ${loanType}\nThis feature will be implemented in the next phase.`);
            
            // In a real implementation, this would filter loan options or redirect
            // window.location.href = `/apply?loanType=${encodeURIComponent(loanType)}`;
        });
    });
    
    // Form validation and submission
    const loanForm = document.querySelector('.loan-form');
    if (loanForm) {
        loanForm.addEventListener('submit', function(e) {
            if (!validateForm()) {
                e.preventDefault();
            }
        });
    }
    
    // Phone number formatting for Indian numbers
    const phoneInput = document.getElementById('phone');
    if (phoneInput) {
        let isFormatting = false;
        
        phoneInput.addEventListener('input', function(e) {
            if (isFormatting) return;
            
            const start = e.target.selectionStart;
            const oldValue = e.target.value;
            
            // Remove all non-digits and format
            let value = oldValue.toString().replace(/\D/g, '');
            if (value.length >= 5) {
                isFormatting = true;
                value = value.replace(/(\d{5})(\d{0,5})/, '$1-$2');
                e.target.value = value;
                
                // Calculate new cursor position based on formatted value
                const newLength = e.target.value.length;
                const oldLength = oldValue.length;
                const newCursorPos = start + (newLength - oldLength);
                
                e.target.setSelectionRange(newCursorPos, newCursorPos);
                isFormatting = false;
            } else if (value) {
                isFormatting = true;
                e.target.value = value;
                
                // Calculate new cursor position based on formatted value
                const newLength = e.target.value.length;
                const oldLength = oldValue.length;
                const newCursorPos = start + (newLength - oldLength);
                
                e.target.setSelectionRange(newCursorPos, newCursorPos);
                isFormatting = false;
            } else if (oldValue === '') {
                isFormatting = true;
                e.target.value = '';
                isFormatting = false;
            }
        });
    }
    
    // Pincode validation (assuming 6 digit pincode)
    const pincodeInput = document.getElementById('pincode');
    if (pincodeInput) {
        pincodeInput.addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/\D/g, '').slice(0, 6);
        });
    }
    
    // Loan amount formatting
    const loanAmountInput = document.getElementById('loanAmount');
    if (loanAmountInput) {
        let isFormatting = false;
        
        loanAmountInput.addEventListener('input', function(e) {
            if (isFormatting) return;
            
            const start = e.target.selectionStart;
            const oldValue = e.target.value;
            
            // Remove all non-digits and format
            let value = oldValue.toString().replace(/[₹,\s]/g, '');
            if (value) {
                isFormatting = true;
                e.target.value = '₹' + new Intl.NumberFormat('en-IN').format(value);
                
                // Calculate new cursor position based on formatted value
                const newLength = e.target.value.length;
                const oldLength = oldValue.length;
                const newCursorPos = start + (newLength - oldLength);
                
                e.target.setSelectionRange(newCursorPos, newCursorPos);
                isFormatting = false;
            } else if (oldValue === '' || oldValue === '₹') {
                isFormatting = true;
                e.target.value = '';
                isFormatting = false;
            }
        });
    }
    
    // Auto-select loan type from URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const loanTypeParam = urlParams.get('loanType');
    if (loanTypeParam && document.getElementById('loanType')) {
        document.getElementById('loanType').value = loanTypeParam;
    }
    
    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
    
    // Form reset confirmation
    const resetButton = document.querySelector('button[type="reset"]');
    if (resetButton) {
        resetButton.addEventListener('click', function(e) {
            if (!confirm('Are you sure you want to reset the form? All entered data will be lost.')) {
                e.preventDefault();
            }
        });
    }
});

// Form validation function
function validateForm() {
    const requiredFields = document.querySelectorAll('[required]');
    let isValid = true;
    let firstInvalidField = null;
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            showError(field, 'This field is required');
            isValid = false;
            if (!firstInvalidField) {
                firstInvalidField = field;
            }
        } else {
            clearError(field);
        }
    });
    
    // Email validation
    const emailField = document.getElementById('email');
    if (emailField && emailField.value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailField.value)) {
            showError(emailField, 'Please enter a valid email address');
            isValid = false;
            if (!firstInvalidField) {
                firstInvalidField = emailField;
            }
        } else {
            clearError(emailField);
        }
    }
    
    // Phone validation for Indian numbers
    const phoneField = document.getElementById('phone');
    if (phoneField && phoneField.value) {
        // Remove all non-digits to get the raw number
        const rawPhoneValue = phoneField.value.toString().replace(/\D/g, '');
        // Check if it's a valid 10-digit Indian mobile number (starting with 6, 7, 8, or 9)
        const phoneRegex = /^[6-9]\d{9}$/;
        if (!phoneRegex.test(rawPhoneValue) || rawPhoneValue.length !== 10) {
            showError(phoneField, 'Please enter a valid 10-digit Indian phone number');
            isValid = false;
            if (!firstInvalidField) {
                firstInvalidField = phoneField;
            }
        } else {
            clearError(phoneField);
        }
    }
    
    // Loan amount validation
    const loanAmountField = document.getElementById('loanAmount');
    if (loanAmountField && loanAmountField.value) {
        const rawValue = loanAmountField.value.toString().replace(/[₹,\s]/g, '');
        const amount = parseFloat(rawValue);
        if (isNaN(amount) || amount < 10000 || amount > 5000000) {
            showError(loanAmountField, 'Loan amount must be between ₹10,000 and ₹50,00,000');
            isValid = false;
            if (!firstInvalidField) {
                firstInvalidField = loanAmountField;
            }
        } else {
            clearError(loanAmountField);
        }
    }
    
    // Monthly income validation
    const incomeField = document.getElementById('monthlyIncome');
    if (incomeField && incomeField.value) {
        const rawValue = incomeField.value.toString().replace(/[₹,\s]/g, '');
        const income = parseFloat(rawValue);
        if (isNaN(income) || income < 10000) {
            showError(incomeField, 'Monthly income must be at least ₹10,000');
            isValid = false;
            if (!firstInvalidField) {
                firstInvalidField = incomeField;
            }
        } else {
            clearError(incomeField);
        }
    }
    
    // Focus on first invalid field
    if (firstInvalidField) {
        firstInvalidField.focus();
        firstInvalidField.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
    
    return isValid;
}

// Show error message
function showError(field, message) {
    clearError(field);
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.style.color = '#e74c3c';
    errorDiv.style.fontSize = '0.85rem';
    errorDiv.style.marginTop = '5px';
    errorDiv.textContent = message;
    
    field.parentNode.appendChild(errorDiv);
    field.style.borderColor = '#e74c3c';
}

// Clear error message
function clearError(field) {
    const existingError = field.parentNode.querySelector('.error-message');
    if (existingError) {
        existingError.remove();
    }
    field.style.borderColor = '#ddd';
}

// Format currency input
function formatCurrency(input) {
    let value = input.value.toString().replace(/[₹,\s]/g, '');
    if (value) {
        input.value = '₹' + new Intl.NumberFormat('en-IN').format(value);
    }
}

// Calculate EMI (Example function - can be expanded)
function calculateEMI() {
    const rawLoanAmount = document.getElementById('loanAmount')?.value.toString().replace(/[₹,\s]/g, '') || 0;
    const loanAmount = parseFloat(rawLoanAmount);
    const interestRate = 10.99; // Default interest rate
    const tenure = 24; // Default tenure in months
    
    if (loanAmount > 0) {
        const monthlyRate = (interestRate / 12) / 100;
        const emi = (loanAmount * monthlyRate * Math.pow(1 + monthlyRate, tenure)) / 
                   (Math.pow(1 + monthlyRate, tenure) - 1);
        
        console.log(`Estimated EMI: ₹${emi.toFixed(2)}/month`);
        // You can display this in the UI if needed
    }
}

// Debounce function for performance
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Add debounce to loan amount input for EMI calculation
document.addEventListener('DOMContentLoaded', function() {
    const loanAmountInput = document.getElementById('loanAmount');
    if (loanAmountInput) {
        const debouncedCalculate = debounce(calculateEMI, 500);
        loanAmountInput.addEventListener('input', debouncedCalculate);
    }
});

// Mobile menu toggle (if needed in future)
function toggleMobileMenu() {
    const navMenu = document.querySelector('.nav-menu');
    navMenu.classList.toggle('active');
}