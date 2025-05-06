// estudante.js - Gerencia funcionalidades do estudante

const EstudanteAPI = {
    // Buscar saldo atual do estudante
    async buscarSaldo(estudanteId) {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/estudantes/${estudanteId}/saldo`);
            if (!response.ok) throw new Error('Erro ao buscar saldo');
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar saldo:', error);
            throw error;
        }
    },

    // Buscar extrato de transações
    async buscarExtrato(estudanteId, filtros = {}) {
        try {
            let url = `${API_URL}/api/estudantes/${estudanteId}/extrato`;
            
            // Adicionar filtros se existirem
            const params = new URLSearchParams();
            if (filtros.dataInicio) params.append('dataInicio', filtros.dataInicio);
            if (filtros.dataFim) params.append('dataFim', filtros.dataFim);
            if (filtros.tipo) params.append('tipo', filtros.tipo);
            
            if (params.toString()) {
                url += `?${params.toString()}`;
            }
            
            const response = await Auth.fetchAuth(url);
            if (!response.ok) throw new Error('Erro ao buscar extrato');
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar extrato:', error);
            throw error;
        }
    },

    // Listar cupons do estudante
    async listarCupons(estudanteId, pagina = 0, tamanho = 10, status = null) {
        try {
            let url = `${API_URL}/api/estudantes/${estudanteId}/cupons?pagina=${pagina}&tamanho=${tamanho}`;
            if (status) url += `&status=${status}`;
            
            const response = await Auth.fetchAuth(url);
            if (!response.ok) throw new Error('Erro ao listar cupons');
            return await response.json();
        } catch (error) {
            console.error('Erro ao listar cupons:', error);
            throw error;
        }
    },

    // Resgatar uma vantagem
    async resgatarVantagem(estudanteId, vantagemId) {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/estudantes/${estudanteId}/resgatar?vantagemId=${vantagemId}`, {
                method: 'POST'
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao resgatar vantagem');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao resgatar vantagem:', error);
            throw error;
        }
    },

    // Listar empresas com vantagens
    async listarEmpresas() {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/estudantes/empresas`);
            if (!response.ok) throw new Error('Erro ao listar empresas');
            return await response.json();
        } catch (error) {
            console.error('Erro ao listar empresas:', error);
            throw error;
        }
    },

    // Listar vantagens disponíveis
    async listarVantagens(filtros = {}) {
        try {
            const params = new URLSearchParams();
            
            if (filtros.empresaId) params.append('empresaId', filtros.empresaId);
            if (filtros.valorMaximo) params.append('valorMaximo', filtros.valorMaximo);
            if (filtros.nome) params.append('nome', filtros.nome);
            if (filtros.pagina !== undefined) params.append('pagina', filtros.pagina);
            if (filtros.tamanho !== undefined) params.append('tamanho', filtros.tamanho);
            
            const url = `${API_URL}/api/estudantes/vantagens?${params.toString()}`;
            
            const response = await Auth.fetchAuth(url);
            if (!response.ok) throw new Error('Erro ao listar vantagens');
            return await response.json();
        } catch (error) {
            console.error('Erro ao listar vantagens:', error);
            throw error;
        }
    }
};

// Funções auxiliares para a UI
const EstudanteUI = {
    // Exibir o saldo do estudante na interface
    exibirSaldo(saldo) {
        document.getElementById('estudante-saldo').textContent = saldo;
    },

    // Renderizar lista de transações na tabela
    renderizarTransacoes(transacoes) {
        const tbody = document.getElementById('transacoes-list');
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
                <td>${transacao.emissorNome || '-'}</td>
            `;
            
            tbody.appendChild(tr);
        });
    },

    // Renderizar lista de cupons 
    renderizarCupons(cupons) {
        const container = document.getElementById('cupons-list');
        container.innerHTML = '';

        if (cupons.length === 0) {
            container.innerHTML = '<p class="text-center">Nenhum cupom encontrado</p>';
            return;
        }

        cupons.forEach(cupom => {
            const card = document.createElement('div');
            card.className = 'cupom-card';
            
            let statusClass = '';
            switch (cupom.status) {
                case 'ATIVO': statusClass = 'status-ativo'; break;
                case 'USADO': statusClass = 'status-usado'; break;
                case 'EXPIRADO': statusClass = 'status-expirado'; break;
            }

            card.innerHTML = `
                <div class="cupom-info">
                    <div class="cupom-nome">${cupom.vantagem.nome}</div>
                    <div class="cupom-descricao">${cupom.vantagem.descricao}</div>
                    <div class="cupom-valor">${cupom.vantagem.valor} moedas</div>
                    <div>Código: <strong>${cupom.codigo}</strong></div>
                    <div>Validade: ${new Date(cupom.dataValidade).toLocaleDateString('pt-BR')}</div>
                    <div class="cupom-status ${statusClass}">${cupom.status}</div>
                </div>
            `;
            
            container.appendChild(card);
        });
    },

    // Renderizar lista de vantagens disponíveis
    renderizarVantagens(vantagens, estudanteId) {
        const container = document.getElementById('vantagens-list');
        container.innerHTML = '';

        if (vantagens.length === 0) {
            container.innerHTML = '<p class="text-center">Nenhuma vantagem disponível</p>';
            return;
        }

        vantagens.forEach(vantagem => {
            const card = document.createElement('div');
            card.className = 'vantagem-card';
            
            let imgHtml = '';
            if (vantagem.foto) {
                imgHtml = `<img src="${vantagem.foto}" alt="${vantagem.nome}" class="vantagem-img">`;
            } else {
                imgHtml = `<div class="vantagem-img-placeholder"></div>`;
            }

            card.innerHTML = `
                ${imgHtml}
                <div class="vantagem-info">
                    <div class="vantagem-nome">${vantagem.nome}</div>
                    <div class="vantagem-descricao">${vantagem.descricao}</div>
                    <div class="vantagem-valor">${vantagem.valor} moedas</div>
                    <div>Empresa: ${vantagem.empresa.nome}</div>
                    <div class="vantagem-actions">
                        <button class="resgatar-btn" data-id="${vantagem.id}">Resgatar</button>
                    </div>
                </div>
            `;
            
            container.appendChild(card);
        });

        // Adicionar eventos aos botões de resgate
        document.querySelectorAll('.resgatar-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const vantagemId = e.target.dataset.id;
                try {
                    await EstudanteAPI.resgatarVantagem(estudanteId, vantagemId);
                    alert('Vantagem resgatada com sucesso!');
                    // Atualizar saldo e lista de cupons
                    EstudanteUI.carregarDadosEstudante(estudanteId);
                } catch (error) {
                    alert(error.message || 'Erro ao resgatar vantagem');
                }
            });
        });
    },

    // Carregar todos os dados do estudante
    async carregarDadosEstudante(estudanteId) {
        try {
            // Carregar saldo
            const saldo = await EstudanteAPI.buscarSaldo(estudanteId);
            EstudanteUI.exibirSaldo(saldo);
            
            // Carregar transações
            const transacoes = await EstudanteAPI.buscarExtrato(estudanteId);
            EstudanteUI.renderizarTransacoes(transacoes);
            
            // Carregar cupons
            const cuponsDados = await EstudanteAPI.listarCupons(estudanteId);
            EstudanteUI.renderizarCupons(cuponsDados.cupons);
            
            // Carregar vantagens
            const vantagens = await EstudanteAPI.listarVantagens({pagina: 0, tamanho: 12});
            EstudanteUI.renderizarVantagens(vantagens.content, estudanteId);
        } catch (error) {
            console.error('Erro ao carregar dados do estudante:', error);
        }
    }
};

// Exportar as funções
window.Estudante = {
    API: EstudanteAPI,
    UI: EstudanteUI
}; 