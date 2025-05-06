// empresa.js - Gerencia funcionalidades da empresa parceira

const EmpresaAPI = {
    // Buscar empresa por ID
    async buscarEmpresa(empresaId) {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/empresas/${empresaId}`);
            if (!response.ok) throw new Error('Erro ao buscar empresa');
            return await response.json();
        } catch (error) {
            console.error('Erro ao buscar empresa:', error);
            throw error;
        }
    },

    // Listar vantagens da empresa
    async listarVantagens(empresaId, pagina = 0, tamanho = 10) {
        try {
            const url = `${API_URL}/api/empresas/${empresaId}/vantagens?pagina=${pagina}&tamanho=${tamanho}`;
            
            const response = await Auth.fetchAuth(url);
            if (!response.ok) throw new Error('Erro ao listar vantagens');
            return await response.json();
        } catch (error) {
            console.error('Erro ao listar vantagens:', error);
            throw error;
        }
    },

    // Criar nova vantagem
    async criarVantagem(empresaId, vantagem) {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/empresas/${empresaId}/vantagens`, {
                method: 'POST',
                body: vantagem
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao criar vantagem');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao criar vantagem:', error);
            throw error;
        }
    },

    // Atualizar status de vantagem
    async atualizarStatusVantagem(vantagemId, disponivel) {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/empresas/vantagens/${vantagemId}/status?disponivel=${disponivel}`, {
                method: 'PUT'
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao atualizar status da vantagem');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao atualizar status da vantagem:', error);
            throw error;
        }
    },

    // Remover vantagem
    async removerVantagem(vantagemId) {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/empresas/vantagens/${vantagemId}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao remover vantagem');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao remover vantagem:', error);
            throw error;
        }
    },

    // Listar cupons da empresa
    async listarCupons(empresaId, pagina = 0, tamanho = 10, status = null) {
        try {
            let url = `${API_URL}/api/cupons/empresa/${empresaId}?pagina=${pagina}&tamanho=${tamanho}`;
            if (status) url += `&status=${status}`;
            
            const response = await Auth.fetchAuth(url);
            if (!response.ok) throw new Error('Erro ao listar cupons');
            return await response.json();
        } catch (error) {
            console.error('Erro ao listar cupons:', error);
            throw error;
        }
    },

    // Validar cupom
    async validarCupom(codigo) {
        try {
            const response = await Auth.fetchAuth(`${API_URL}/api/cupons/${codigo}/status?novoStatus=USADO`, {
                method: 'PUT'
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao validar cupom');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Erro ao validar cupom:', error);
            throw error;
        }
    }
};

// Funções auxiliares para a UI
const EmpresaUI = {
    // Renderizar lista de vantagens da empresa
    renderizarVantagens(vantagens) {
        const container = document.getElementById('empresa-vantagens-list');
        container.innerHTML = '';

        if (vantagens.length === 0) {
            container.innerHTML = '<p class="text-center">Nenhuma vantagem cadastrada</p>';
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
                    <div>Status: ${vantagem.disponivel ? 'Disponível' : 'Indisponível'}</div>
                    <div class="vantagem-actions">
                        <button class="editar-btn" data-id="${vantagem.id}">Editar</button>
                        <button class="toggle-btn" data-id="${vantagem.id}" data-disponivel="${vantagem.disponivel ? 'false' : 'true'}">
                            ${vantagem.disponivel ? 'Desativar' : 'Ativar'}
                        </button>
                        <button class="remover-btn" data-id="${vantagem.id}">Remover</button>
                    </div>
                </div>
            `;
            
            container.appendChild(card);
        });

        // Adicionar eventos aos botões
        document.querySelectorAll('.editar-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const vantagemId = e.target.dataset.id;
                EmpresaUI.abrirModalEditarVantagem(vantagens.find(v => v.id == vantagemId));
            });
        });

        document.querySelectorAll('.toggle-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const vantagemId = e.target.dataset.id;
                const disponivel = e.target.dataset.disponivel === 'true';
                try {
                    await EmpresaAPI.atualizarStatusVantagem(vantagemId, disponivel);
                    alert(`Vantagem ${disponivel ? 'ativada' : 'desativada'} com sucesso!`);
                    EmpresaUI.carregarDadosEmpresa(Auth.getCurrentUser().id);
                } catch (error) {
                    alert(error.message || 'Erro ao atualizar status da vantagem');
                }
            });
        });

        document.querySelectorAll('.remover-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const vantagemId = e.target.dataset.id;
                if (confirm('Tem certeza que deseja remover esta vantagem?')) {
                    try {
                        await EmpresaAPI.removerVantagem(vantagemId);
                        alert('Vantagem removida com sucesso!');
                        EmpresaUI.carregarDadosEmpresa(Auth.getCurrentUser().id);
                    } catch (error) {
                        alert(error.message || 'Erro ao remover vantagem');
                    }
                }
            });
        });
    },

    // Renderizar lista de cupons da empresa
    renderizarCupons(cupons) {
        const tbody = document.getElementById('cupons-empresa-list');
        tbody.innerHTML = '';

        if (cupons.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = '<td colspan="5" class="text-center">Nenhum cupom encontrado</td>';
            tbody.appendChild(tr);
            return;
        }

        cupons.forEach(cupom => {
            const tr = document.createElement('tr');
            const data = new Date(cupom.dataResgate).toLocaleDateString('pt-BR');
            
            let statusClass = '';
            switch (cupom.status) {
                case 'ATIVO': statusClass = 'status-ativo'; break;
                case 'USADO': statusClass = 'status-usado'; break;
                case 'EXPIRADO': statusClass = 'status-expirado'; break;
            }
            
            tr.innerHTML = `
                <td>${cupom.codigo}</td>
                <td>${cupom.vantagem.nome}</td>
                <td>${cupom.estudante.nome}</td>
                <td>${data}</td>
                <td><span class="cupom-status ${statusClass}">${cupom.status}</span></td>
            `;
            
            tbody.appendChild(tr);
        });
    },

    // Abrir modal para criar/editar vantagem
    abrirModalEditarVantagem(vantagem = null) {
        const modal = document.getElementById('vantagem-modal');
        const title = document.getElementById('modal-title');
        const form = document.getElementById('vantagem-form');
        const idInput = document.getElementById('vantagem-id');
        const nomeInput = document.getElementById('vantagem-nome');
        const descricaoInput = document.getElementById('vantagem-descricao');
        const valorInput = document.getElementById('vantagem-valor');
        const fotoInput = document.getElementById('vantagem-foto');
        const disponivelInput = document.getElementById('vantagem-disponivel');
        
        // Configurar o título e os valores do formulário
        if (vantagem) {
            title.textContent = 'Editar Vantagem';
            idInput.value = vantagem.id;
            nomeInput.value = vantagem.nome;
            descricaoInput.value = vantagem.descricao;
            valorInput.value = vantagem.valor;
            fotoInput.value = vantagem.foto || '';
            disponivelInput.checked = vantagem.disponivel;
        } else {
            title.textContent = 'Nova Vantagem';
            form.reset();
            idInput.value = '';
        }
        
        // Exibir o modal
        modal.classList.remove('hidden');
    },

    // Configurar modal e formulário de vantagem
    configurarModalVantagem(empresaId) {
        const modal = document.getElementById('vantagem-modal');
        const closeBtn = document.querySelector('.close-modal');
        const form = document.getElementById('vantagem-form');
        
        // Fechar modal ao clicar no X
        closeBtn.addEventListener('click', () => {
            modal.classList.add('hidden');
        });
        
        // Fechar modal ao clicar fora dele
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.add('hidden');
            }
        });
        
        // Configurar o formulário
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const idInput = document.getElementById('vantagem-id');
            const id = idInput.value;
            
            const vantagem = {
                nome: document.getElementById('vantagem-nome').value,
                descricao: document.getElementById('vantagem-descricao').value,
                valor: parseInt(document.getElementById('vantagem-valor').value),
                foto: document.getElementById('vantagem-foto').value,
                disponivel: document.getElementById('vantagem-disponivel').checked
            };
            
            try {
                // Se tiver ID, é edição (não implementado neste exemplo simplificado)
                if (id) {
                    // Na versão completa, aqui teria uma chamada para editar a vantagem
                    alert('Edição de vantagem não implementada neste exemplo');
                } else {
                    // Criar nova vantagem
                    await EmpresaAPI.criarVantagem(empresaId, vantagem);
                    alert('Vantagem criada com sucesso!');
                }
                
                // Fechar modal e atualizar lista
                modal.classList.add('hidden');
                EmpresaUI.carregarDadosEmpresa(empresaId);
            } catch (error) {
                alert(error.message || 'Erro ao salvar vantagem');
            }
        });
        
        // Configurar botão para abrir modal de nova vantagem
        document.getElementById('nova-vantagem-btn').addEventListener('click', () => {
            EmpresaUI.abrirModalEditarVantagem();
        });
    },

    // Configurar formulário de validação de cupom
    configurarFormValidarCupom() {
        const form = document.getElementById('validar-cupom-form');
        const resultDiv = document.getElementById('validar-result');
        
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            resultDiv.textContent = '';
            resultDiv.className = 'message';
            
            const codigo = document.getElementById('codigo-cupom').value;
            
            try {
                const cupom = await EmpresaAPI.validarCupom(codigo);
                resultDiv.textContent = `Cupom validado com sucesso! Vantagem: ${cupom.vantagem.nome}`;
                resultDiv.className = 'message success';
                form.reset();
                
                // Atualizar lista de cupons
                EmpresaUI.carregarDadosEmpresa(Auth.getCurrentUser().id);
            } catch (error) {
                resultDiv.textContent = error.message || 'Erro ao validar cupom';
                resultDiv.className = 'message error';
            }
        });
    },

    // Configurar abas
    configurarAbas() {
        const tabButtons = document.querySelectorAll('.tab-btn');
        
        tabButtons.forEach(button => {
            button.addEventListener('click', () => {
                const tabId = button.dataset.tab;
                
                // Desativar todas as abas e botões
                document.querySelectorAll('.tab-content').forEach(tab => tab.classList.add('hidden'));
                tabButtons.forEach(btn => btn.classList.remove('active'));
                
                // Ativar a aba e botão selecionados
                document.getElementById(`${tabId}-tab`).classList.remove('hidden');
                button.classList.add('active');
            });
        });
    },

    // Carregar todos os dados da empresa
    async carregarDadosEmpresa(empresaId) {
        try {
            // Carregar vantagens
            const vantagens = await EmpresaAPI.listarVantagens(empresaId);
            EmpresaUI.renderizarVantagens(vantagens.content);
            
            // Carregar cupons
            const cupons = await EmpresaAPI.listarCupons(empresaId);
            EmpresaUI.renderizarCupons(cupons.content);
            
            // Configurar modal de vantagem
            EmpresaUI.configurarModalVantagem(empresaId);
            
            // Configurar formulário de validação de cupom
            EmpresaUI.configurarFormValidarCupom();
            
            // Configurar abas
            EmpresaUI.configurarAbas();
        } catch (error) {
            console.error('Erro ao carregar dados da empresa:', error);
        }
    }
};

// Exportar as funções
window.Empresa = {
    API: EmpresaAPI,
    UI: EmpresaUI
}; 