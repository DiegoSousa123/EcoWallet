package model;

public abstract class Transacao {

    private double valor;
    private String descricao;
    private Categoria categoria;

    public Transacao(double valor, String descricao, Categoria categoria) {
        this.valor = valor;
        this.descricao = descricao;
        this.categoria = categoria;
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

    public abstract String tipoTransacao();
}