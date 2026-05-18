package service;

import dao.TransacaoJsonDAO;
import model.TipoTransacao;
import model.Transacao;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorFinancas {

    private List<Transacao> transacoes = new ArrayList<>();
    private TransacaoJsonDAO listaTransacao = new TransacaoJsonDAO();
    public void adicionarTransacao(Transacao t) {
        transacoes.add(t);
    }

    public void removerTransacao(Transacao t) {
        transacoes.remove(t);
    }

    public List<Transacao> getTransacoes() {
        this.transacoes = listaTransacao.carregar();
        return this.transacoes;
    }
    public void salvarNoArquivo(){
        listaTransacao.salvar(transacoes);
    }
    public double calcularSaldo() {
        double saldo = 0;
        for (Transacao t : transacoes) {
            if (t.getTipo().equals(TipoTransacao.RECEITA)) {
                saldo += t.getValor();
            } else {
                saldo -= t.getValor();
            }
        }
        return saldo;
    }
}