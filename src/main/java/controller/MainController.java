package controller;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import factory.TransacaoFactory;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.Categoria;
import model.Transacao;
import service.GerenciadorFinancas;
import service.PersistenciaService;

public class MainController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat FORMATO_NUMERO_PT_BR = NumberFormat.getNumberInstance(new Locale("pt", "BR"));

    private final GerenciadorFinancas gerenciador = new GerenciadorFinancas();
    private final PersistenciaService persistencia = new PersistenciaService();

    @FXML
    private TableView<Transacao> tabelaTransacoes;

    @FXML
    private TableColumn<Transacao, String> colunaDescricao;

    @FXML
    private TableColumn<Transacao, Number> colunaValor;

    @FXML
    private TableColumn<Transacao, String> colunaData;

    @FXML
    private TextField campoDescricao;

    @FXML
    private TextField campoValor;

    @FXML
    private Label labelSaldoTotal;

    @FXML
    private ComboBox<Categoria> comboCategoria;

    @FXML
    public void initialize() {
        colunaDescricao.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getDescricao()));
        colunaValor.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getValor()));
        colunaData.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getData().format(FORMATO_DATA)));

        comboCategoria.setItems(FXCollections.observableArrayList(Categoria.values()));

        gerenciador.getTransacoes().addAll(persistencia.carregar());

        atualizarTabela();
        atualizarSaldo();
    }

    @FXML
    private void adicionarReceita() {
        adicionarTransacao("Receita");
    }

    @FXML
    private void adicionarDespesa() {
        adicionarTransacao("Despesa");
    }

    @FXML
    private void removerTransacaoSelecionada() {
        Transacao selecionada = tabelaTransacoes.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            mostrarAviso("Selecione uma transação para remover.");
            return;
        }

        gerenciador.removerTransacao(selecionada);
        persistencia.salvar(gerenciador.getTransacoes());

        atualizarTabela();
        atualizarSaldo();
    }

    private void adicionarTransacao(String tipo) {
        String descricao = campoDescricao.getText().trim();
        String valorTexto = campoValor.getText().trim();
        Categoria categoria = comboCategoria.getValue();

        if (descricao.isEmpty() || valorTexto.isEmpty() || categoria == null) {
            mostrarAviso("Preencha descrição, valor e categoria.");
            return;
        }

        ParsePosition parsePosition = new ParsePosition(0);
        Number numero = FORMATO_NUMERO_PT_BR.parse(valorTexto, parsePosition);
        if (numero == null || parsePosition.getIndex() != valorTexto.length()) {
            mostrarAviso("Informe um valor numérico válido.");
            return;
        }

        double valor = numero.doubleValue();
        if (valor <= 0) {
            mostrarAviso("O valor deve ser maior que zero.");
            return;
        }

        Transacao transacao = TransacaoFactory.criar(tipo, valor, descricao, categoria);
        gerenciador.adicionarTransacao(transacao);

        persistencia.salvar(gerenciador.getTransacoes());
        atualizarTabela();
        atualizarSaldo();
        limparFormulario();
    }

    private void atualizarTabela() {
        tabelaTransacoes.setItems(FXCollections.observableArrayList(gerenciador.getTransacoes()));
    }

    private void atualizarSaldo() {
        labelSaldoTotal.setText(String.format("Saldo total: R$ %.2f", gerenciador.calcularSaldo()));
    }

    private void limparFormulario() {
        campoDescricao.clear();
        campoValor.clear();
        comboCategoria.setValue(null);
    }

    private void mostrarAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
