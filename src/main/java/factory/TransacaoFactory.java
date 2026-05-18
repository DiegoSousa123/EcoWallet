package factory;
import java.time.LocalDate;
import model.Categoria;
import model.Despesa;
import model.Receita;
import model.TipoTransacao;
import model.Transacao;

public class TransacaoFactory {
    public static Transacao criar(TipoTransacao tipo, double valor, String descricao, Categoria categoria) {
        if (tipo == TipoTransacao.RECEITA) {
            return new Receita(valor, descricao, categoria);
        } else if (tipo == TipoTransacao.DESPESA) {
            return new Despesa(valor, descricao, categoria);
        } else {
            throw new IllegalArgumentException("Tipo inválido: " + tipo);
        }
    }
}