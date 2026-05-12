package factory;
import model.Categoria;
import model.Despesa;
import model.Receita;
import model.Transacao;

public class TransacaoFactory {
    public static Transacao criar(String tipo, double valor, String descricao, Categoria categoria) {
        if (tipo.equalsIgnoreCase("Receita")) {
            return new Receita(valor, descricao, categoria);
        } else if (tipo.equalsIgnoreCase("Despesa")) {
            return new Despesa(valor, descricao, categoria);
        } else {
            throw new IllegalArgumentException("Tipo inválido: " + tipo);
        }
    }
}