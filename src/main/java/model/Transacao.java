package model;

import java.time.LocalDate;

public abstract class Transacao {

    private double valor;
    private String descricao;
    private Categoria categoria;
    private LocalDate data;

    public Transacao(double valor, String descricao, Categoria categoria) {
        this(valor, descricao, categoria, LocalDate.now());
    }

    public Transacao(double valor, String descricao, Categoria categoria, LocalDate data) {
        this.valor = valor;
        this.descricao = descricao;
        this.categoria = categoria;
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public abstract String tipoTransacao();
}
