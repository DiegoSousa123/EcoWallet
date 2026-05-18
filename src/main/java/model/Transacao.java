package model;

import java.time.LocalDate;

/**
 * Classe abstrata base para todas as transações financeiras.
 * Subclasses: {@link Receita}, {@link Despesa}.
 */
public abstract class Transacao {

    private double        valor;
    private TipoTransacao tipo;
    private String        descricao;
    private Categoria     categoria;
    private LocalDate     data;

    protected Transacao(TipoTransacao tipo, double valor, String descricao,
                        Categoria categoria, LocalDate data) {
        this.tipo      = tipo;
        this.valor     = valor;
        this.descricao = descricao;
        this.categoria = categoria;
        this.data      = data;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public double getValor()                { return valor; }
    public void   setValor(double valor)    { this.valor = valor; }

    public String getDescricao()            { return descricao; }
    public void   setDescricao(String d)    { this.descricao = d; }

    public Categoria getCategoria()               { return categoria; }
    public void      setCategoria(Categoria c)    { this.categoria = c; }

    public LocalDate getData()              { return data; }
    public void      setData(LocalDate d)   { this.data = d; }

    public TipoTransacao getTipo()               { return tipo; }
    public void          setTipo(TipoTransacao t){ this.tipo = t; }

    @Override
    public String toString() {
        return String.format("[%s] %s — R$ %.2f (%s)", tipo, descricao, valor, categoria);
    }
}