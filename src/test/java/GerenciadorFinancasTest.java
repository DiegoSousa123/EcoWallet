import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.TransacaoJsonDAO;
import factory.TransacaoFactory;
import model.Categoria;
import model.TipoTransacao;
import model.Transacao;
import service.GerenciadorFinancas;
@ExtendWith(MockitoExtension.class)
class GerenciadorFinancasTest {

    @Mock
    private TransacaoJsonDAO daoMock;

    @InjectMocks
    private GerenciadorFinancas gerenciador;

    private Transacao receita1;
    private Transacao receita2;
    private Transacao despesa1;

    @BeforeEach
    void setUp() {
        // Nota: Assumindo que o construtor seja Transacao(String descricao, double valor, TipoTransacao tipo)
        // Adapte os construtores abaixo conforme a implementação real da sua classe Transacao.
        receita1 = TransacaoFactory.criar(TipoTransacao.RECEITA, 5000, "Salário", Categoria.OUTROS, LocalDate.now());
        receita2 = TransacaoFactory.criar(TipoTransacao.RECEITA, 1500, "Freelance", Categoria.OUTROS, LocalDate.now());
        despesa1 = TransacaoFactory.criar(TipoTransacao.DESPESA, 2000, "Aluguel", Categoria.MORADIA, LocalDate.now());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TESTES DE CRUD E EXCEÇÕES
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve adicionar uma transação com sucesso")
    void deveAdicionarTransacao() {
        gerenciador.adicionarTransacao(receita1);

        // Para verificar se adicionou, podemos contar por tipo ou filtrar
        assertEquals(1, gerenciador.contarPorTipo(TipoTransacao.RECEITA));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar transação nula")
    void deveLancarExcecaoAoAdicionarTransacaoNula() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gerenciador.adicionarTransacao(null);
        });

        assertEquals("Transação não pode ser nula.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover uma transação existente")
    void deveRemoverTransacao() {
        gerenciador.adicionarTransacao(receita1);
        gerenciador.removerTransacao(receita1);

        assertEquals(0, gerenciador.contarPorTipo(TipoTransacao.RECEITA));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TESTES DE INTEGRAÇÃO COM O DAO (MOCK)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve carregar transações do DAO e retornar uma cópia da lista")
    void deveCarregarTransacoesDoDao() {
        List<Transacao> listaSimulada = Arrays.asList(receita1, despesa1);
        when(daoMock.carregar()).thenReturn(listaSimulada);

        List<Transacao> resultado = gerenciador.getTransacoes();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(receita1));
        assertTrue(resultado.contains(despesa1));
        verify(daoMock, times(1)).carregar();
    }

    @Test
    @DisplayName("Deve repassar a lista atual para o DAO ao salvar")
    void deveSalvarNoArquivo() {
        gerenciador.adicionarTransacao(receita1);
        gerenciador.salvarNoArquivo();

//         Verifica se o metodo salvar do DAO foi chamado com a lista contendo a receita1
        verify(daoMock, times(1)).salvar(anyList());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TESTES DE CÁLCULOS
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve calcular o total de receitas corretamente")
    void deveCalcularTotalReceitas() {
        gerenciador.adicionarTransacao(receita1); // 5000
        gerenciador.adicionarTransacao(receita2); // 1500
        gerenciador.adicionarTransacao(despesa1); // 2000 (deve ser ignorado no cálculo)

        assertEquals(6500, gerenciador.calcularTotalReceitas());
    }

    @Test
    @DisplayName("Deve calcular o total de despesas corretamente")
    void deveCalcularTotalDespesas() {
        gerenciador.adicionarTransacao(receita1);
        gerenciador.adicionarTransacao(despesa1); // 2000

        assertEquals(2000.0, gerenciador.calcularTotalDespesas());
    }

    @Test
    @DisplayName("Deve calcular o saldo final (Receitas - Despesas)")
    void deveCalcularSaldo() {
        gerenciador.adicionarTransacao(receita1); // +5000
        gerenciador.adicionarTransacao(receita2); // +1500
        gerenciador.adicionarTransacao(despesa1); // -2000

        // Saldo esperado: 6500 - 2000 = 4500
        assertEquals(4500.0, gerenciador.calcularSaldo());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TESTES DE FILTROS E BUSCA
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve filtrar transações pelo tipo especificado")
    void deveFiltrarPorTipo() {
        gerenciador.adicionarTransacao(receita1);
        gerenciador.adicionarTransacao(despesa1);

        List<Transacao> receitas = gerenciador.filtrarPorTipo(TipoTransacao.RECEITA);

        assertEquals(1, receitas.size());
        assertEquals(receita1, receitas.get(0));
    }

    @Test
    @DisplayName("Deve buscar transações por termo contido na descrição ignorando case")
    void deveBuscarPorDescricao() {
        gerenciador.adicionarTransacao(TransacaoFactory.criar(TipoTransacao.RECEITA, 5000, "Salario", Categoria.OUTROS, LocalDate.now()));
        gerenciador.adicionarTransacao(TransacaoFactory.criar(TipoTransacao.DESPESA, 143, "Conta de Luz", Categoria.OUTROS, LocalDate.now()));
        gerenciador.adicionarTransacao(TransacaoFactory.criar(TipoTransacao.DESPESA, 56, "Conta de Água", Categoria.OUTROS, LocalDate.now()));

        List<Transacao> resultadoBusca = gerenciador.buscarPorDescricao("conta");

        assertEquals(2, resultadoBusca.size());
        assertTrue(resultadoBusca.stream().anyMatch(t -> t.getDescricao().equals("Conta de Luz")));
        assertTrue(resultadoBusca.stream().anyMatch(t -> t.getDescricao().equals("Conta de Água")));
    }

    @Test
    @DisplayName("Deve retornar todas as transações se o termo de busca for vazio ou nulo")
    void deveRetornarTudoSeBuscaVazia() {
        gerenciador.adicionarTransacao(receita1);
        gerenciador.adicionarTransacao(despesa1);

        assertEquals(2, gerenciador.buscarPorDescricao("").size());
        assertEquals(2, gerenciador.buscarPorDescricao(null).size());
        assertEquals(2, gerenciador.buscarPorDescricao("   ").size());
    }
}