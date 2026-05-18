package model;

import java.time.LocalDate;

public class Despesa extends Transacao {

    public Despesa(double valor, String descricao, Categoria categoria, LocalDate data) {
        super(TipoTransacao.DESPESA, valor, descricao, categoria, data);
    }
}