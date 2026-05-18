package model;

/**
 * Enum que representa o tipo de uma transação financeira.
 * O toString() em português é usado pelos ComboBoxes e pela TableView.
 */
public enum TipoTransacao {
	RECEITA("Receita"),
	DESPESA("Despesa");

	private final String label;

	TipoTransacao(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}