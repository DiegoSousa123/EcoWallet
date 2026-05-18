package model;

public class Receita extends Transacao {

    public Receita(double valor, String descricao, Categoria categoria) {
        super(TipoTransacao.RECEITA, valor, descricao, categoria);
    }

    @Override
    public String tipoTransacao() {
        return "Receita";
    }
}