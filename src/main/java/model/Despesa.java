package model;

public class Despesa extends Transacao {

    public Despesa(double valor, String descricao, Categoria categoria) {
        super(TipoTransacao.DESPESA, valor, descricao, categoria);
    }

    @Override
    public String tipoTransacao() {
        return "Despesa";
    }
}