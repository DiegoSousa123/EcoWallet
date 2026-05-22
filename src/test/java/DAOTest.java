import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dao.TransacaoJsonDAO;
import factory.TransacaoFactory;
import model.Categoria;
import model.TipoTransacao;
import model.Transacao;

class DAOTest {

    private TransacaoJsonDAO dao;
    private final String CAMINHO_ARQUIVO = "transacoes.json"; 

    @BeforeEach
    void setUp() {
       dao = new TransacaoJsonDAO();
    }

    @AfterEach
    void tearDown() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }

    @Test
    @DisplayName("Deve salvar a lista de transações no arquivo json com sucesso.")
    void deveSalvarTransacoesNoArquivoComSucesso() throws Exception {
        List<Transacao> transacoes = new ArrayList<>();
        Transacao t1 = TransacaoFactory.criar(TipoTransacao.DESPESA, 240, "Teste 1", Categoria.LAZER, LocalDate.now());
        transacoes.add(t1);
        dao.salvar(transacoes);       
        File arquivoGerado = new File(CAMINHO_ARQUIVO);
      
        //checagens
        assertTrue(arquivoGerado.exists(), "O arquivo JSON deveria ter sido criado.");
        String conteudoDoArquivo = Files.readString(Path.of(CAMINHO_ARQUIVO));

        //verifica se os dados da transação estão no JSON
        assertTrue(conteudoDoArquivo.contains("Teste 1"));
        assertTrue(conteudoDoArquivo.contains("240"));
        assertTrue(conteudoDoArquivo.contains("LAZER"));
        
    }
}
