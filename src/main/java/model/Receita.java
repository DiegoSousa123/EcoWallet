package model;

import java.time.LocalDate;

public class Receita extends Transacao {

    public Receita(double valor, String descricao, Categoria categoria) {
        super(valor, descricao, categoria);
    }

    public Receita(double valor, String descricao, Categoria categoria, LocalDate data) {
        super(valor, descricao, categoria, data);
    }

    @Override
    public String tipoTransacao() {
        return "Receita";
    }
}
