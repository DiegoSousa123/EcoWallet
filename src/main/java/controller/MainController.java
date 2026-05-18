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

    //Componentes da Interface (IMPORTANTE)
    @FXML private TableView<Transacao> tabelaTransacoes; //tabela de transacoes
    @FXML private TableColumn<Transacao, String> colDescricao; // coluna descrição
    @FXML private TableColumn<Transacao, String> colTipo; // coluna tipo
    @FXML private TableColumn<Transacao, Categoria> colCategoria; // coluna categoria
    @FXML private TableColumn<Transacao, Double> colValor; // coluna valor
    @FXML private TableColumn<Transacao, LocalDate> colData; // coluna data

    @FXML private TextField txtDescricao; //campo para descrição
    @FXML private TextField txtValor; // campo para o valor 
    @FXML private ComboBox<TipoTransacao> cbTipo; //seletor de tipo de transação
    @FXML private ComboBox<Categoria> cbCategoria; //seletor de categoria
    @FXML private DatePicker dpData;
    
    @FXML private Label lblSaldoTotal; //label para exibir saldo total


    private GerenciadorFinancas gerenciador;
    private ObservableList<Transacao> transacoesObservable;

    @FXML
    public void initialize() {
        gerenciador = new GerenciadorFinancas();

        //Configuração das colunas da TableView
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        
        //Configuração dos ComboBoxes baseados nos Enums definidos no pacote model
        cbTipo.setItems(FXCollections.observableArrayList(TipoTransacao.values()));
        cbCategoria.setItems(FXCollections.observableArrayList(Categoria.values()));

        //Carrega a lista do gerenciador na interface
        transacoesObservable = FXCollections.observableArrayList(gerenciador.getTransacoes());
        tabelaTransacoes.setItems(transacoesObservable);

        atualizarSaldo();
    }

    @FXML
    public void adicionarTransacao() {
        try {
            String descricao = txtDescricao.getText();
            String valorTexto = txtValor.getText().replace(",", "."); // Prevenção de erro de locale
            
            //Tratamento de exceções NumberFormatException para garantir que não digitem letras 
            double valor = Double.parseDouble(valorTexto); 
            
            TipoTransacao tipo = cbTipo.getValue();
            Categoria categoria = cbCategoria.getValue();
            LocalDate data = dpData.getValue();

            if (descricao.trim().isEmpty() || tipo == null || categoria == null) {
                mostrarAlerta("Campos Inválidos", "Por favor, preencha todos os campos corretamente.");
                return;
            }

            // A TransacaoFactory recebe os dados da interface e decide a criação
            Transacao novaTransacao = TransacaoFactory.criar(tipo, valor, descricao, categoria, data);

            //O arquivo JSON é atualizado ao adicionar 
            gerenciador.adicionarTransacao(novaTransacao);
            gerenciador.salvarNoArquivo();

            //Atualiza a View e o Saldo
            transacoesObservable.add(novaTransacao);
            atualizarSaldo();
            limparCampos();

        } catch (NumberFormatException e) {
            // Garantir que o usuário não digite letras no campo de valor 
            mostrarAlerta("Erro de Formatação", "O campo 'Valor' deve conter apenas números válidos.");
        }
    }

    public void removerTransacao() {
        Transacao selecionada = tabelaTransacoes.getSelectionModel().getSelectedItem();
        
        if (selecionada != null) {
            // O arquivo JSON é atualizado ao remover 
            gerenciador.removerTransacao(selecionada);
            transacoesObservable.remove(selecionada);
            gerenciador.salvarNoArquivo();
            atualizarSaldo();
        } else {
            mostrarAlerta("Seleção Inválida", "Selecione uma transação na tabela para remover.");
        }
    }

    @FXML
    public void handleAddTransacao() {
    	//A FAZER
    }
    @FXML
    public void handleLimpar() {
    	limparCampos();
    }
//    @FXML
//    public void handleSalvar() {
//    	
//    }
//    
    @FXML
    public void handleBusca() {
    	//A FAZER
    }
    
    @FXML
    public void handleFiltro() {
    	//A FAZER
    }
    
    @FXML
    public void handleRemover() {
    	removerTransacao();
    }
    
    
    /**
     * Vincula a propriedade de texto do Label à lógica do Model
     */
    private void atualizarSaldo() {
        // Supõe-se que GerenciadorFinancas possua um método para retornar o saldo já calculado
        double saldo = gerenciador.calcularSaldo();
        lblSaldoTotal.setText(String.format("Saldo Total: R$ %.2f", saldo));
    }

    private void limparCampos() {
        txtDescricao.clear();
        txtValor.clear();
        cbTipo.getSelectionModel().select(0);
        cbCategoria.getSelectionModel().select(0);
        dpData.setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}