// CONFIGURATION SETTINGS FOR STAGING AND PRODUCTION APIs
const API_BASE_URL = (typeof window !== 'undefined' && window.location && window.location.origin && window.location.origin.startsWith('http'))
    ? `${window.location.origin}/api`
    : 'http://localhost:8080/api';
