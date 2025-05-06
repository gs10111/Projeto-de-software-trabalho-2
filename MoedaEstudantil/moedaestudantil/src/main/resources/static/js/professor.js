// professor.js - Gerencia funcionalidades do professor

const ProfessorAPI = {
    // Buscar saldo do professor
    async buscarSaldo(professorId) {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/professores/${professorId}/saldo`);
            if (!response.ok) throw new Error('Erro ao buscar saldo do professor');
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar saldo do professor:', error);
            throw error;
        }
    },

    // Buscar extrato de transações do professor
    async buscarExtrato(professorId) {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/professores/${professorId}/extrato`);
            if (!response.ok) throw new Error('Erro ao buscar extrato do professor');
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar extrato do professor:', error);
            throw error;
        }
    },

    // Listar todos os estudantes
    async listarEstudantes() {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/estudantes`);
            if (!response.ok) throw new Error('Erro ao listar estudantes');
            return await response.json();
        } catch (error) {
            console.error('Erro ao listar estudantes:', error);
            return [];
        }
    },

    // Enviar moedas para um estudante
    async enviarMoedas(professorId, estudanteId, quantidade, motivo) {
        try {
            const url = `${API_URL}/api/professores/${professorId}/enviar-moedas?estudanteId=${estudanteId}&quantidade=${quantidade}&motivo=${encodeURIComponent(motivo)}`;
            
            const response = await Auth.fetchAuth(url, {
                method: 'POST'
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao enviar moedas');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao enviar moedas:', error);
            throw error;
        }
    }
};

// Funções auxiliares para a UI
const ProfessorUI = {
    // Exibir o saldo do professor na interface
    exibirSaldo(saldo) {
        document.getElementById('professor-saldo').textContent = saldo;
    },

    // Renderizar lista de transações na tabela
    renderizarTransacoes(transacoes) {
        const tbody = document.getElementById('prof-transacoes-list');
        tbody.innerHTML = '';

        if (transacoes.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = '<td colspan="4" class="text-center">Nenhuma transação encontrada</td>';
            tbody.appendChild(tr);
            return;
        }

        transacoes.forEach(transacao => {
            const tr = document.createElement('tr');
            const data = new Date(transacao.dataHora).toLocaleDateString('pt-BR');
            
            tr.innerHTML = `
                <td>${data}</td>
                <td>${transacao.descricao}</td>
                <td>${transacao.valor}</td>
                <td>${transacao.estudanteNome || '-'}</td>
            `;
            
            tbody.appendChild(tr);
        });
    },

    // Preencher o select de estudantes
    async preencherSelectEstudantes() {
        try {
            const estudantes = await ProfessorAPI.listarEstudantes();
            const select = document.getElementById('estudante-select');
            select.innerHTML = '<option value="">Selecione um estudante</option>';
            
            estudantes.forEach(estudante => {
                const option = document.createElement('option');
                option.value = estudante.id;
                option.textContent = `${estudante.nome} (${estudante.email})`;
                select.appendChild(option);
            });
        } catch (error) {
            console.error('Erro ao preencher select de estudantes:', error);
        }
    },

    // Configurar o formulário de envio de moedas
    configurarFormEnvioMoedas(professorId) {
        const form = document.getElementById('enviar-moedas-form');
        const mensagem = document.getElementById('enviar-moedas-message');
        
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            mensagem.textContent = '';
            mensagem.className = 'message';
            
            const estudanteId = form.querySelector('#estudante-select').value;
            const quantidade = form.querySelector('#quantidade').value;
            const motivo = form.querySelector('#motivo').value;
            
            if (!estudanteId) {
                mensagem.textContent = 'Selecione um estudante';
                mensagem.className = 'message error';
                return;
            }
            
            try {
                await ProfessorAPI.enviarMoedas(professorId, estudanteId, quantidade, motivo);
                mensagem.textContent = 'Moedas enviadas com sucesso!';
                mensagem.className = 'message success';
                form.reset();
                
                // Atualizar saldo e transações
                ProfessorUI.carregarDadosProfessor(professorId);
            } catch (error) {
                mensagem.textContent = error.message || 'Erro ao enviar moedas';
                mensagem.className = 'message error';
            }
        });
    },

    // Carregar todos os dados do professor
    async carregarDadosProfessor(professorId) {
        try {
            // Carregar saldo
            const saldo = await ProfessorAPI.buscarSaldo(professorId);
            ProfessorUI.exibirSaldo(saldo);
            
            // Carregar transações
            const transacoes = await ProfessorAPI.buscarExtrato(professorId);
            ProfessorUI.renderizarTransacoes(transacoes);
            
            // Preencher select de estudantes
            await ProfessorUI.preencherSelectEstudantes();
            
            // Configurar formulário
            ProfessorUI.configurarFormEnvioMoedas(professorId);
        } catch (error) {
            console.error('Erro ao carregar dados do professor:', error);
        }
    }
};

// Exportar as funções
window.Professor = {
    API: ProfessorAPI,
    UI: ProfessorUI
}; 