package controller;

import factory.TransacaoFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Categoria;
import model.TipoTransacao;
import model.Transacao;
import service.GerenciadorFinancas;

import java.time.LocalDate;

public class MainController {

    // Componentes da Interface (TableView para listar transações em tempo real) [cite: 13]
    @FXML private TableView<Transacao> tableTransacoes;
    @FXML private TableColumn<Transacao, String> colDescricao;
    @FXML private TableColumn<Transacao, String> colTipo; // Opcional, dependendo da sua Transacao
    @FXML private TableColumn<Transacao, Categoria> colCategoria;
    @FXML private TableColumn<Transacao, Double> colValor;
    @FXML private TableColumn<Transacao, LocalDate> colData;

    @FXML private TextField txtDescricao;
    @FXML private TextField txtValor; // TextField com validação 
    @FXML private ComboBox<TipoTransacao> cbTipo;
    @FXML private ComboBox<Categoria> cbCategoria;
    
    // Label de "Saldo Total" vinculado à lógica do Model [cite: 15]
    @FXML private Label lblSaldoTotal;

    // Gerenciador responsável por somar saldos e manter a lista [cite: 9]
    private GerenciadorFinancas gerenciador;
    private ObservableList<Transacao> transacoesObservable;

    /**
     * Ao iniciar o Controller, o sistema carrega os dados 
     */
    @FXML
    public void initialize() {
        // Inicializa o gerenciador (que já carrega do arquivo .json na sua instância)
        gerenciador = new GerenciadorFinancas();

        // Configuração das colunas da TableView
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        
        // Configuração dos ComboBoxes baseados nos Enums definidos no pacote model
        cbTipo.setItems(FXCollections.observableArrayList(TipoTransacao.values()));
        cbCategoria.setItems(FXCollections.observableArrayList(Categoria.values()));

        // Carrega a lista do gerenciador na interface
        transacoesObservable = FXCollections.observableArrayList(gerenciador.getTransacoes());
        tableTransacoes.setItems(transacoesObservable);

        atualizarSaldo();
    }

    @FXML
    public void adicionarTransacao() {
        try {
            String descricao = txtDescricao.getText();
            String valorTexto = txtValor.getText().replace(",", "."); // Prevenção de erro de locale
            
            // Tratamento de exceções NumberFormatException para garantir que não digitem letras 
            double valor = Double.parseDouble(valorTexto); 
            
            TipoTransacao tipo = cbTipo.getValue();
            Categoria categoria = cbCategoria.getValue();

            if (descricao.trim().isEmpty() || tipo == null || categoria == null) {
                mostrarAlerta("Campos Inválidos", "Por favor, preencha todos os campos corretamente.");
                return;
            }

            // A TransacaoFactory recebe os dados da interface e decide a criação [cite: 8]
            Transacao novaTransacao = TransacaoFactory.criar(tipo, valor, descricao, categoria);

            // O arquivo JSON é atualizado ao adicionar 
            gerenciador.adicionarTransacao(novaTransacao);
            gerenciador.salvarNoArquivo();

            // Atualiza a View e o Saldo
            transacoesObservable.add(novaTransacao);
            atualizarSaldo();
            limparCampos();

        } catch (NumberFormatException e) {
            // Garantir que o usuário não digite letras no campo de valor 
            mostrarAlerta("Erro de Formatação", "O campo 'Valor' deve conter apenas números válidos.");
        }
    }

    @FXML
    public void removerTransacao() {
        Transacao selecionada = tableTransacoes.getSelectionModel().getSelectedItem();
        
        if (selecionada != null) {
            // O arquivo JSON é atualizado ao remover 
            gerenciador.removerTransacao(selecionada);
            transacoesObservable.remove(selecionada);
            atualizarSaldo();
        } else {
            mostrarAlerta("Seleção Inválida", "Selecione uma transação na tabela para remover.");
        }
    }

    /**
     * Vincula a propriedade de texto do Label à lógica do Model [cite: 15]
     */
    private void atualizarSaldo() {
        // Supõe-se que GerenciadorFinancas possua um método para retornar o saldo já calculado
        double saldo = gerenciador.calcularSaldo();
        lblSaldoTotal.setText(String.format("Saldo Total: R$ %.2f", saldo));
    }

    private void limparCampos() {
        txtDescricao.clear();
        txtValor.clear();
        cbTipo.getSelectionModel().clearSelection();
        cbCategoria.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}