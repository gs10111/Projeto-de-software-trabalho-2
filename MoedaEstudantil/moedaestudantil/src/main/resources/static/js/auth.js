// auth.js - Gerencia a autenticação de usuários

const API_URL = 'http://localhost:8080';
let currentUser = null;
let authToken = localStorage.getItem('authToken');

// Função para fazer login
async function login(email, password) {
    try {
        console.log('Login - Enviando requisição para:', `${API_URL}/api/auth/login`);
        console.log('Login - Dados enviados:', { email, senha: password });
        
        const response = await fetch(`${API_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                senha: password
            })
        });

        console.log('Login - Status da resposta:', response.status);
        
        if (!response.ok) {
            const error = await response.json();
            console.error('Login - Erro na resposta:', error);
            throw new Error(error.mensagem || 'Falha na autenticação');
        }

        const data = await response.json();
        console.log('Login - Resposta bem-sucedida:', data);
        
        // Salvar token e informações do usuário
        authToken = data.token;
        localStorage.setItem('authToken', authToken);
        
        // Obter dados completos do usuário
        await fetchUserInfo();
        
        return currentUser;
    } catch (error) {
        console.error('Erro ao fazer login:', error);
        throw error;
    }
}

// Buscar informações do usuário logado
async function fetchUserInfo() {
    try {
        console.log('FetchUserInfo - Buscando informações do usuário');
        const response = await fetch(`${API_URL}/api/auth/me`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            console.error('FetchUserInfo - Erro na resposta:', response.status);
            throw new Error('Não foi possível obter informações do usuário');
        }

        currentUser = await response.json();
        console.log('FetchUserInfo - Usuário carregado:', currentUser);
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        return currentUser;
    } catch (error) {
        console.error('Erro ao buscar informações do usuário:', error);
        logout();
        throw error;
    }
}

// Verificar se o usuário está autenticado
async function checkAuth() {
    if (!authToken) {
        return false;
    }

    try {
        await fetchUserInfo();
        return true;
    } catch (error) {
        return false;
    }
}

// Logout - sair do sistema
async function logout() {
    try {
        if (authToken) {
            await fetch(`${API_URL}/api/auth/logout`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${authToken}`
                }
            });
        }
    } catch (error) {
        console.error('Erro ao fazer logout:', error);
    } finally {
        // Limpar dados locais independentemente da resposta do servidor
        authToken = null;
        currentUser = null;
        localStorage.removeItem('authToken');
        localStorage.removeItem('currentUser');
    }
}

// Função para fazer requisições autenticadas
async function fetchAuth(url, options = {}) {
    if (!authToken) {
        throw new Error('Usuário não autenticado');
    }

    const headers = {
        'Authorization': `Bearer ${authToken}`,
        ...options.headers
    };

    if (options.body && typeof options.body === 'object' && !(options.body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
    }

    const response = await fetch(url, {
        ...options,
        headers
    });

    // Se receber 401 Unauthorized, fazer logout
    if (response.status === 401) {
        await logout();
        window.location.reload();
        throw new Error('Sessão expirada. Por favor, faça login novamente.');
    }

    return response;
}

// Inicialização: tentar carregar usuário do localStorage
if (localStorage.getItem('currentUser')) {
    try {
        currentUser = JSON.parse(localStorage.getItem('currentUser'));
    } catch (e) {
        localStorage.removeItem('currentUser');
    }
}

// Exportar as funções
window.Auth = {
    login,
    logout,
    checkAuth,
    fetchAuth,
    getCurrentUser: () => currentUser,
    getAuthToken: () => authToken,
    getUserRole: () => currentUser ? currentUser.perfil : null
}; 