package model;

/**
 * Enum de categorias disponíveis para transações.
 *
 * Adicionado toString() legível para exibição nos ComboBoxes e na TableView
 * sem necessidade de CellFactory customizada para este campo.
 */
public enum Categoria {
    ALIMENTACAO("Alimentação"),
    TRANSPORTE("Transporte"),
    LAZER("Lazer"),
    SAUDE("Saúde"),
    EDUCACAO("Educação"),
    MORADIA("Moradia"),
    OUTROS("Outros");

    private final String descricao;

    Categoria(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}