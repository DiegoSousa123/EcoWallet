package model;

import java.time.LocalDate;

public abstract class Transacao {

    private double valor;
    private TipoTransacao tipo;
    private String descricao;
    private Categoria categoria;
    private LocalDate data;

    public Transacao(TipoTransacao tipo, double valor, String descricao, Categoria categoria) {
        this.valor = valor;
        this.descricao = descricao;
        this.categoria = categoria;
        this.setData(LocalDate.now());
        this.setTipo(tipo);
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

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public TipoTransacao getTipo() {
		return tipo;
	}

	public void setTipo(TipoTransacao tipo) {
		this.tipo = tipo;
	}
}