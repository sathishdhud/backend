/**
 * Session Manager for JWT Token Handling
 * Manages 30-minute token expiration and tab closing detection
 */

class SessionManager {
    constructor() {
        this.token = null;
        this.tokenExpiry = null;
        this.refreshTimer = null;
        this.warningTimer = null;
        this.tabId = null;
        this.init();
    }

    init() {
        // Generate unique tab ID
        this.tabId = this.generateTabId();
        
        // Check for existing token on page load
        this.loadTokenFromStorage();
        
        // Set up event listeners for tab closing detection
        this.setupTabClosingDetection();
        
        // Set up periodic token validation
        this.setupTokenValidation();
        
        // Set up visibility change detection (tab switching)
        this.setupVisibilityChangeDetection();
        
        // Check if we should redirect to login (for direct URL access)
        this.checkAuthenticationOnLoad();
    }

    /**
     * Generate unique tab identifier
     */
    generateTabId() {
        return 'tab_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    /**
     * Check authentication status on page load
     */
    checkAuthenticationOnLoad() {
        // If this is not a login page and user is not authenticated, redirect to login
        const currentPath = window.location.pathname;
        if (!currentPath.includes('/login') && !this.isAuthenticated()) {
            // Check if this is a protected route
            const protectedRoutes = ['/dashboard', '/admin', '/reservations', '/checkins', '/rooms', '/reports'];
            const isProtectedRoute = protectedRoutes.some(route => currentPath.startsWith(route));
            
            if (isProtectedRoute || currentPath === '/' || currentPath === '/index.html') {
                this.redirectToLogin();
            }
        }
    }

    /**
     * Load token from localStorage
     */
    loadTokenFromStorage() {
        const storedToken = localStorage.getItem('jwtToken');
        const storedExpiry = localStorage.getItem('tokenExpiry');
        const storedTabId = sessionStorage.getItem('currentTabId');
        
        if (storedToken && storedExpiry) {
            const expiryDate = new Date(parseInt(storedExpiry));
            const now = new Date();
            
            // Check if token is still valid and belongs to current tab
            if (expiryDate > now && storedTabId === this.tabId) {
                this.token = storedToken;
                this.tokenExpiry = expiryDate;
                this.scheduleTokenCheck();
            } else {
                // Token expired or from different tab, clear storage
                this.clearToken();
            }
        }
    }

    /**
     * Save token to localStorage with 30-minute expiry
     */
    saveToken(token) {
        this.token = token;
        
        // Calculate expiry time (30 minutes from now)
        const now = new Date();
        this.tokenExpiry = new Date(now.getTime() + 30 * 60 * 1000); // 30 minutes
        
        // Save to localStorage and sessionStorage
        localStorage.setItem('jwtToken', token);
        localStorage.setItem('tokenExpiry', this.tokenExpiry.getTime().toString());
        sessionStorage.setItem('currentTabId', this.tabId);
        
        // Schedule token validation
        this.scheduleTokenCheck();
    }

    /**
     * Clear token from storage
     */
    clearToken() {
        this.token = null;
        this.tokenExpiry = null;
        
        // Clear from localStorage
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('tokenExpiry');
        sessionStorage.removeItem('currentTabId');
        
        // Clear timers
        if (this.refreshTimer) {
            clearTimeout(this.refreshTimer);
            this.refreshTimer = null;
        }
        
        if (this.warningTimer) {
            clearTimeout(this.warningTimer);
            this.warningTimer = null;
        }
    }

    /**
     * Schedule token validation checks
     */
    scheduleTokenCheck() {
        // Clear existing timers
        if (this.refreshTimer) {
            clearTimeout(this.refreshTimer);
        }
        
        if (this.warningTimer) {
            clearTimeout(this.warningTimer);
        }
        
        if (!this.tokenExpiry) return;
        
        const now = new Date();
        const timeUntilExpiry = this.tokenExpiry.getTime() - now.getTime();
        
        // Show warning 5 minutes before expiry
        if (timeUntilExpiry > 5 * 60 * 1000) {
            this.warningTimer = setTimeout(() => {
                this.showTokenExpiryWarning();
            }, timeUntilExpiry - 5 * 60 * 1000);
        }
        
        // Expire token at exact time
        this.refreshTimer = setTimeout(() => {
            this.handleTokenExpiry();
        }, Math.max(0, timeUntilExpiry)); // Ensure non-negative timeout
    }

    /**
     * Show token expiry warning
     */
    showTokenExpiryWarning() {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                title: 'Session Expiring Soon',
                text: 'Your session will expire in 5 minutes due to inactivity. Please save your work.',
                icon: 'warning',
                timer: 10000,
                timerProgressBar: true,
                confirmButtonText: 'Stay Logged In'
            }).then((result) => {
                if (result.isConfirmed) {
                    // User chose to stay logged in, refresh token
                    this.refreshToken();
                } else if (result.dismiss === Swal.DismissReason.timer) {
                    // Timer ran out, log out
                    this.handleTokenExpiry();
                }
            });
        } else {
            const shouldStay = confirm('Your session will expire in 5 minutes due to inactivity. Click OK to stay logged in.');
            if (shouldStay) {
                this.refreshToken();
            } else {
                this.handleTokenExpiry();
            }
        }
    }

    /**
     * Refresh token (extend session)
     */
    refreshToken() {
        // In a real implementation, you would call a refresh endpoint
        // For now, we'll just extend the current token's life
        if (this.token) {
            const now = new Date();
            this.tokenExpiry = new Date(now.getTime() + 30 * 60 * 1000); // Extend by 30 minutes
            
            // Update storage
            localStorage.setItem('tokenExpiry', this.tokenExpiry.getTime().toString());
            
            // Reschedule checks
            this.scheduleTokenCheck();
        }
    }

    /**
     * Handle token expiry
     */
    handleTokenExpiry() {
        this.clearToken();
        this.showSessionExpiredMessage();
        this.redirectToLogin();
    }

    /**
     * Show session expired message
     */
    showSessionExpiredMessage() {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                title: 'Session Expired',
                text: 'Your session has expired due to inactivity or browser closure. Please log in again.',
                icon: 'warning',
                confirmButtonText: 'Log In'
            }).then(() => {
                this.redirectToLogin();
            });
        } else {
            alert('Your session has expired. Please log in again.');
            this.redirectToLogin();
        }
    }

    /**
     * Redirect to login page
     */
    redirectToLogin() {
        // Clear any existing token
        this.clearToken();
        
        // Redirect to login page
        const loginPath = window.location.origin + '/login';
        if (window.location.href !== loginPath) {
            window.location.href = '/login';
        }
    }

    /**
     * Set up tab closing detection
     */
    setupTabClosingDetection() {
        // Detect when user is about to leave the page
        window.addEventListener('beforeunload', (event) => {
            // Store tab closing flag
            sessionStorage.setItem('tabClosed', 'true');
            // Clear token when tab is closed
            this.clearToken();
        });
        
        // Detect when page is hidden (tab switching)
        window.addEventListener('pagehide', (event) => {
            // Store tab closing flag
            sessionStorage.setItem('tabClosed', 'true');
            // Clear token when tab is closed or navigated away
            this.clearToken();
        });
        
        // Detect when page is unloaded
        window.addEventListener('unload', (event) => {
            // Clear token when tab is closed
            this.clearToken();
        });
    }

    /**
     * Set up visibility change detection (tab switching)
     */
    setupVisibilityChangeDetection() {
        document.addEventListener('visibilitychange', () => {
            if (document.visibilityState === 'hidden') {
                // Page is hidden, store current time
                sessionStorage.setItem('tabHiddenTime', Date.now().toString());
            } else {
                // Page is visible again, check if too much time has passed
                const hiddenTime = sessionStorage.getItem('tabHiddenTime');
                if (hiddenTime) {
                    const timeHidden = Date.now() - parseInt(hiddenTime);
                    // If tab was hidden for more than 30 minutes, expire token
                    if (timeHidden > 30 * 60 * 1000) {
                        this.handleTokenExpiry();
                    }
                }
            }
        });
    }

    /**
     * Set up periodic token validation
     */
    setupTokenValidation() {
        // Validate token every 10 seconds (more frequent for better accuracy)
        setInterval(() => {
            this.validateToken();
        }, 10 * 1000);
    }

    /**
     * Validate current token
     */
    validateToken() {
        if (!this.token) {
            // If we're on a protected page, redirect to login
            this.checkAuthenticationOnLoad();
            return;
        }
        
        const now = new Date();
        
        // If token expiry is not set or has passed, clear token
        if (!this.tokenExpiry || this.tokenExpiry <= now) {
            this.handleTokenExpiry();
            return;
        }
        
        // Additional check: if tab was closed and reopened, force logout
        const tabClosed = sessionStorage.getItem('tabClosed');
        if (tabClosed === 'true') {
            this.handleTokenExpiry();
            return;
        }
        
        // Check if token belongs to current tab
        const storedTabId = sessionStorage.getItem('currentTabId');
        if (storedTabId !== this.tabId) {
            this.handleTokenExpiry();
            return;
        }
    }

    /**
     * Get current token
     */
    getToken() {
        // Validate token before returning
        this.validateToken();
        return this.token;
    }

    /**
     * Check if user is authenticated
     */
    isAuthenticated() {
        this.validateToken();
        return this.token !== null && this.tokenExpiry !== null && new Date() < this.tokenExpiry;
    }
}

// Initialize session manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.sessionManager = new SessionManager();
});

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = SessionManager;
}