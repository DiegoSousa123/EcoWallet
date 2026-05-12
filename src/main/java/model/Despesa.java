package model;

public class Despesa extends Transacao {

    public Despesa(double valor, String descricao, Categoria categoria) {
        super(valor, descricao, categoria);
    }

    @Override
    public String tipoTransacao() {
        return "Despesa";
    }
}