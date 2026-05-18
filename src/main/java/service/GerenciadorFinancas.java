package service;

import dao.TransacaoJsonDAO;
import model.TipoTransacao;
import model.Transacao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço central de negócios do EcoWallet.
 * Gerencia a lista de transações em memória e delega persistência ao DAO.
 *
 * Unificado em: 2026 — branch merge final.
 * Adicionado: calcularTotalReceitas(), calcularTotalDespesas(), contarPorTipo()
 * para alimentar os cards de resumo da View sem expor iterações no Controller.
 */
public class GerenciadorFinancas {

    private List<Transacao>   transacoes     = new ArrayList<>();
    private TransacaoJsonDAO  dao            = new TransacaoJsonDAO();

    // ════════════════════════════════════════════════════════════════════════
    //  CRUD
    // ════════════════════════════════════════════════════════════════════════

    public void adicionarTransacao(Transacao t) {
        if (t == null) throw new IllegalArgumentException("Transação não pode ser nula.");
        transacoes.add(t);
    }

    public void removerTransacao(Transacao t) {
        transacoes.remove(t);
    }

    /**
     * Carrega transações do arquivo JSON e sincroniza a lista em memória.
     * Sempre relê o arquivo para garantir consistência com dados externos.
     */
    public List<Transacao> getTransacoes() {
        this.transacoes = dao.carregar();
        return new ArrayList<>(this.transacoes); // cópia defensiva
    }

    public void salvarNoArquivo() {
        dao.salvar(transacoes);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CÁLCULOS / RESUMO
    // ════════════════════════════════════════════════════════════════════════

    public double calcularSaldo() {
        return calcularTotalReceitas() - calcularTotalDespesas();
    }

    public double calcularTotalReceitas() {
        return transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    public double calcularTotalDespesas() {
        return transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    public long contarPorTipo(TipoTransacao tipo) {
        return transacoes.stream()
                .filter(t -> t.getTipo() == tipo)
                .count();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  FILTROS (base para extensão futura)
    // ════════════════════════════════════════════════════════════════════════

    public List<Transacao> filtrarPorTipo(TipoTransacao tipo) {
        return transacoes.stream()
                .filter(t -> t.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    public List<Transacao> buscarPorDescricao(String termo) {
        if (termo == null || termo.isBlank()) return new ArrayList<>(transacoes);
        String lower = termo.toLowerCase();
        return transacoes.stream()
                .filter(t -> t.getDescricao().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}