package model;

import java.time.LocalDate;

public class Receita extends Transacao {

    public Receita(double valor, String descricao, Categoria categoria, LocalDate data) {
        super(TipoTransacao.RECEITA, valor, descricao, categoria, data);
    }
}