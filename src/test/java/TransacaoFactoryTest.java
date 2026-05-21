import factory.TransacaoFactory;
import model.Categoria;
import model.TipoTransacao;
import model.Transacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransacaoFactoryTest{

   @Test
   @DisplayName("Deve criar uma transação com sucesso.")
    void transacaoFactory(){
       Transacao t = TransacaoFactory.criar(TipoTransacao.DESPESA, 240, "Teste 1", Categoria.LAZER, LocalDate.now());
       assertNotNull(t);
       assertEquals(TipoTransacao.DESPESA, t.getTipo(), "O tipo de transação deve ser DESPESA");
       assertEquals(240, t.getValor(), "O valor deve ser 240");
       assertEquals(Categoria.LAZER, t.getCategoria(), "A categoria deve ser LAZER");
       assertEquals("Teste 1", t.getDescricao(), "A descrição deve ser 'Teste 1'");

   }
}
