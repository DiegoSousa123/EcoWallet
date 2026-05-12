package model;

import java.time.LocalDate;

public class Despesa extends Transacao {

    public Despesa(double valor, String descricao, Categoria categoria) {
        super(valor, descricao, categoria);
    }

    public Despesa(double valor, String descricao, Categoria categoria, LocalDate data) {
        super(valor, descricao, categoria, data);
    }

    @Override
    public String tipoTransacao() {
        return "Despesa";
    }
}
