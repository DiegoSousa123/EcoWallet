package service;

import model.Transacao;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorFinancas {

    private List<Transacao> transacoes = new ArrayList<>();

    public void adicionarTransacao(Transacao t) {
        transacoes.add(t);
    }

    public void removerTransacao(Transacao t) {
        transacoes.remove(t);
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public double calcularSaldo() {
        double saldo = 0;
        for (Transacao t : transacoes) {
            if (t.tipoTransacao().equals("Receita")) {
                saldo += t.getValor();
            } else {
                saldo -= t.getValor();
            }
        }
        return saldo;
    }
}