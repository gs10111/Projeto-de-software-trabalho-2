// app.js - Gerencia o fluxo principal da aplicação

document.addEventListener('DOMContentLoaded', () => {
    inicializarApp();
});

// Inicializar a aplicação
async function inicializarApp() {
    try {
        // Verificar se já há um usuário autenticado
        const isAuthenticated = await Auth.checkAuth();
        
        if (isAuthenticated) {
            // Se estiver autenticado, mostrar dashboard apropriado
            mostrarDashboard();
        } else {
            // Se não estiver autenticado, mostrar tela de login
            mostrarLogin();
        }

        // Configurar evento de logout
        document.getElementById('logout-btn').addEventListener('click', async () => {
            await Auth.logout();
            mostrarLogin();
        });
        
        // Configurar tabs para mudar de conteúdo
        configurarTabs();
    } catch (error) {
        console.error('Erro ao inicializar aplicação:', error);
    }
}

// Mostrar tela de login
function mostrarLogin() {
    // Esconder todas as seções
    document.querySelectorAll('.section').forEach(section => {
        section.classList.add('hidden');
    });
    
    // Mostrar seção de login
    document.getElementById('login-section').classList.remove('hidden');
    
    // Esconder informações do usuário
    document.getElementById('user-info').classList.add('hidden');
    
    // Configurar formulário de login
    configurarFormLogin();
}

// Mostrar dashboard apropriado baseado no perfil do usuário
function mostrarDashboard() {
    // Esconder todas as seções
    document.querySelectorAll('.section').forEach(section => {
        section.classList.add('hidden');
    });
    
    const user = Auth.getCurrentUser();
    
    // Mostrar nome do usuário
    document.getElementById('username').textContent = user.nome;
    document.getElementById('user-info').classList.remove('hidden');
    
    // Mostrar dashboard baseado no perfil
    switch (user.perfil) {
        case 'ALUNO':
            document.getElementById('estudante-section').classList.remove('hidden');
            Estudante.UI.carregarDadosEstudante(user.id);
            break;
        case 'PROFESSOR':
            document.getElementById('professor-section').classList.remove('hidden');
            Professor.UI.carregarDadosProfessor(user.id);
            break;
        case 'PARCEIRO':
            document.getElementById('empresa-section').classList.remove('hidden');
            Empresa.UI.carregarDadosEmpresa(user.id);
            break;
        default:
            console.error('Perfil de usuário desconhecido:', user.perfil);
    }
}

// Configurar formulário de login
function configurarFormLogin() {
    const form = document.getElementById('login-form');
    const errorMsg = document.getElementById('login-error');
    
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorMsg.textContent = '';
        
        // Usar o tipo de usuário selecionado
        const tipoUsuario = document.getElementById('tipo-usuario').value;
        let email;
        
        // Determinar email correto com base no tipo de usuário
        if (tipoUsuario === 'professor') {
            email = 'professor@teste.com';
        } else if (tipoUsuario === 'empresa') {
            email = 'empresa@teste.com';
        } else {
            email = 'estudante@teste.com';
        }
        
        // Atualizar o campo de email na UI (opcional)
        document.getElementById('email').value = email;
        
        const password = document.getElementById('password').value;
        
        try {
            console.log('Tentando login com:', email, password);
            await Auth.login(email, password);
            mostrarDashboard();
        } catch (error) {
            console.error('Erro no login:', error);
            errorMsg.textContent = 'Credenciais inválidas. Por favor, tente novamente.';
        }
    });
}

// Configurar tabs para mudar de conteúdo
function configurarTabs() {
    document.querySelectorAll('.tab-btn').forEach(button => {
        button.addEventListener('click', () => {
            const tabId = button.dataset.tab;
            const tabContainer = button.closest('.tab-container');
            
            // Desativar todas as abas e botões no mesmo container
            tabContainer.querySelectorAll('.tab-content').forEach(tab => tab.classList.add('hidden'));
            tabContainer.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
            
            // Ativar a aba e botão selecionados
            tabContainer.querySelector(`#${tabId}-tab`).classList.remove('hidden');
            button.classList.add('active');
        });
    });
} 