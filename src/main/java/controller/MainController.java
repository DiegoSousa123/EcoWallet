package controller;

import factory.TransacaoFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Categoria;
import model.TipoTransacao;
import model.Transacao;
import org.kordamp.ikonli.javafx.FontIcon;
import service.GerenciadorFinancas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controller principal do EcoWallet.
 * Responsável por mediar a View (MainView.fxml) e o Model (GerenciadorFinancas).
 *
 * Unificado em: 2026 — branch merge final.
 */
public class MainController {

    // calcula o arrasto da janela
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private HBox barraJanela;
    @FXML private Button btnMinimizarJanela;
    @FXML private Button btnMaximizarJanela;
    @FXML private Button btnFecharJanela;
    @FXML private FontIcon maximizeIcon;

    // ── Tabela ──────────────────────────────────────────────────────────────
    @FXML private TableView<Transacao>              tabelaTransacoes;
    @FXML private TableColumn<Transacao, String>         colDescricao;
    @FXML private TableColumn<Transacao, TipoTransacao>  colTipo;      // CORRIGIDO: era String, deve ser TipoTransacao
    @FXML private TableColumn<Transacao, Categoria>      colCategoria;
    @FXML private TableColumn<Transacao, Double>    colValor;
    @FXML private TableColumn<Transacao, LocalDate> colData;
    @FXML private ImageView logo;
    // ── Formulário ───────────────────────────────────────────────────────────
    @FXML private TextField             txtDescricao;
    @FXML private TextField             txtValor;
    @FXML private ComboBox<TipoTransacao> cbTipo;
    @FXML private ComboBox<Categoria>   cbCategoria;
    @FXML private DatePicker            dpData;
    @FXML private Label                 lblErro;

    // ── Cards de resumo ──────────────────────────────────────────────────────
    @FXML private Label lblSaldoTotal;
    @FXML private Label lblTotalReceitas;
    @FXML private Label lblTotalDespesas;
    @FXML private Label lblQtdReceitas;
    @FXML private Label lblQtdDespesas;
    @FXML private Label lblSaldoFooter;
    @FXML private Label lblQtdRegistros;

    // ── Filtros ──────────────────────────────────────────────────────────────
    @FXML private TextField             tfBusca;
    @FXML private ComboBox<String>      cbFiltroTipo;

    // ── Estado interno ───────────────────────────────────────────────────────
    private GerenciadorFinancas              gerenciador;
    private ObservableList<Transacao>        transacoesObservable;
    private FilteredList<Transacao>          transacoesFiltradas;

    private static final DateTimeFormatter FMT_BR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ════════════════════════════════════════════════════════════════════════
    //  INICIALIZAÇÃO
    // ════════════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        gerenciador = new GerenciadorFinancas();

        configurarColunas();
        configurarComboBoxes();
        carregarDados();
        configurarFiltros();
        atualizarResumo();

        // Data padrão = hoje
        dpData.setValue(LocalDate.now());

        // Oculta label de erro inicialmente
        lblErro.setVisible(false);
        lblErro.setManaged(false);

        Image image = new Image(getClass().getResourceAsStream("/images/ecowallet_240.png"));
        logo.setImage(image);
    }

    // ── Configuração das colunas ─────────────────────────────────────────────

    private void configurarColunas() {
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Formata a coluna de valor com R$ e cor condicional
        colValor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);
                if (empty || valor == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Transacao t = getTableView().getItems().get(getIndex());
                    boolean receita = t.getTipo() == TipoTransacao.RECEITA;
                    setText(String.format(new Locale("pt", "BR"), "R$ %.2f", valor));
                    getStyleClass().removeAll("cell-receita", "cell-despesa", "cell-mono");
                    getStyleClass().addAll("cell-mono", receita ? "cell-receita" : "cell-despesa");
                }
            }
        });

        // Formata a coluna de data para dd/MM/yyyy
        colData.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate data, boolean empty) {
                super.updateItem(data, empty);
                setText((empty || data == null) ? null : data.format(FMT_BR));
            }
        });

        // Formata a coluna de tipo com cor condicional
        // CORRIGIDO: updateItem recebe TipoTransacao (nao String)
        colTipo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(TipoTransacao tipo, boolean empty) {
                super.updateItem(tipo, empty);
                if (empty || tipo == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(tipo.toString()); // usa toString() do enum: "Receita" / "Despesa"
                    getStyleClass().removeAll("cell-receita", "cell-despesa");
                    getStyleClass().add(tipo == TipoTransacao.RECEITA ? "cell-receita" : "cell-despesa");
                }
            }
        });
    }

    // ── Configuração dos ComboBoxes ──────────────────────────────────────────

    private void configurarComboBoxes() {
        cbTipo.setItems(FXCollections.observableArrayList(TipoTransacao.values()));
        cbCategoria.setItems(FXCollections.observableArrayList(Categoria.values()));

        // Filtro de tipo na tabela: "Todos" + valores do enum
        ObservableList<String> opcoesFilro = FXCollections.observableArrayList("Todos");
        for (TipoTransacao t : TipoTransacao.values()) opcoesFilro.add(t.name());
        cbFiltroTipo.setItems(opcoesFilro);
        cbFiltroTipo.getSelectionModel().selectFirst();
    }

    // ── Carregamento de dados ────────────────────────────────────────────────

    private void carregarDados() {
        transacoesObservable = FXCollections.observableArrayList(gerenciador.getTransacoes());
        transacoesFiltradas  = new FilteredList<>(transacoesObservable, p -> true);
        tabelaTransacoes.setItems(transacoesFiltradas);
    }

    // ── Filtros (busca + tipo) ───────────────────────────────────────────────

    private void configurarFiltros() {
        // O predicado é recalculado sempre que busca ou filtro de tipo mudam
        tfBusca.textProperty().addListener((obs, old, novo) -> aplicarFiltro());
        cbFiltroTipo.valueProperty().addListener((obs, old, novo) -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        String textoBusca  = tfBusca.getText() == null ? "" : tfBusca.getText().toLowerCase();
        String tipoFiltro  = cbFiltroTipo.getValue();

        transacoesFiltradas.setPredicate(t -> {
            boolean matchBusca = textoBusca.isEmpty()
                    || t.getDescricao().toLowerCase().contains(textoBusca)
                    || t.getCategoria().name().toLowerCase().contains(textoBusca);

            boolean matchTipo = tipoFiltro == null
                    || tipoFiltro.equals("Todos")
                    || t.getTipo().name().equals(tipoFiltro);

            return matchBusca && matchTipo;
        });

        lblQtdRegistros.setText(transacoesFiltradas.size() + " registros");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  HANDLERS — chamados pelo FXML
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Botão "+ Adicionar" na sidebar/cabeçalho — foca o formulário existente.
     * (Navegação para o painel de formulário pode ser expandida futuramente.)
     */
    @FXML
    public void handleAddTransacao() {
        txtDescricao.requestFocus();
    }

    /** Botão "Salvar ✓" no formulário. */
    @FXML
    public void adicionarTransacao() {
        ocultarErro();

        String  descricao  = txtDescricao.getText();
        String  valorTexto = txtValor.getText().replace(",", ".");
        TipoTransacao tipo = cbTipo.getValue();
        Categoria categoria = cbCategoria.getValue();
        LocalDate data     = dpData.getValue();

        // ── Validações ────────────────────────────────────────────────────────
        if (descricao == null || descricao.trim().isEmpty()) {
            exibirErro("O campo Descrição é obrigatório.");
            return;
        }
        if (tipo == null) {
            exibirErro("Selecione o Tipo da transação.");
            return;
        }
        if (categoria == null) {
            exibirErro("Selecione a Categoria.");
            return;
        }
        if (data == null) {
            exibirErro("Informe a Data.");
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorTexto);
            if (valor <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            exibirErro("Valor inválido. Use apenas números positivos (ex: 150,00).");
            return;
        }

        // ── Criação e persistência ────────────────────────────────────────────
        Transacao nova = TransacaoFactory.criar(tipo, valor, descricao.trim(), categoria, data);
        gerenciador.adicionarTransacao(nova);
        gerenciador.salvarNoArquivo();

        transacoesObservable.add(nova);
        aplicarFiltro();
        atualizarResumo();
        limparCampos();
    }

    /** Botão "🗑 Remover" na barra da tabela. */
    @FXML
    public void handleRemover() {
        Transacao selecionada = tabelaTransacoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta("Seleção Inválida", "Selecione uma transação na tabela para remover.");
            return;
        }
        // Confirmação antes de excluir
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja remover a transação \"" + selecionada.getDescricao() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar Remoção");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                gerenciador.removerTransacao(selecionada);
                transacoesObservable.remove(selecionada);
                gerenciador.salvarNoArquivo();
                aplicarFiltro();
                atualizarResumo();
            }
        });
    }

    /** Campo de busca — reativo (listener já configurado, handler mantido para compatibilidade FXML). */
    @FXML
    public void handleBusca() {
        aplicarFiltro();
    }

    /** ComboBox de filtro — reativo (listener já configurado, handler mantido para compatibilidade FXML). */
    @FXML
    public void handleFiltro() {
        aplicarFiltro();
    }

    /** Botão "Limpar" no formulário. */
    @FXML
    public void handleLimpar() {
        limparCampos();
        ocultarErro();
    }

    @FXML
    public void minimizarJanela(ActionEvent e){
        getStage().setIconified(true);
    }
    @FXML
    public void maximizarJanela(ActionEvent e){
        Stage stage = getStage();
        if(stage.isMaximized()){
            stage.setMaximized(false);
            maximizeIcon.setIconLiteral("remixal-checkbox-blank-line");
        }else{
            stage.setMaximized(true);
            maximizeIcon.setIconLiteral("remixal-checkbox-multiple-blank-line");
        }
    }
    @FXML
    public void fecharJanela(ActionEvent e){
        getStage().close();
    }

    @FXML
    public void aoPressionarMouse(MouseEvent mouseEvent){
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }

    @FXML
    public void aoClicarDuasVezes(MouseEvent event){
        if(event.getClickCount() == 2){
            maximizarJanela(null);
        }
    }

    @FXML
    public void aoArrastarMouse(MouseEvent mouseEvent){

        if (!mouseEvent.isPrimaryButtonDown()) {
            return;
        }

        Stage stage = getStage();

        if(stage.isMaximized()){
            maximizarJanela(null);
        }
        stage.setX(mouseEvent.getScreenX() - xOffset);
        stage.setY(mouseEvent.getScreenY() - yOffset);
    }

    private Stage getStage(){
        return (Stage) barraJanela.getScene().getWindow();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  MÉTODOS PRIVADOS DE SUPORTE
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Atualiza todos os labels de resumo (cards + rodapé da tabela).
     * Centraliza o cálculo para evitar chamadas duplicadas ao gerenciador.
     */
    private void atualizarResumo() {
        double saldo     = gerenciador.calcularSaldo();
        double receitas  = gerenciador.calcularTotalReceitas();
        double despesas  = gerenciador.calcularTotalDespesas();
        long   qtdRec    = gerenciador.contarPorTipo(TipoTransacao.RECEITA);
        long   qtdDesp   = gerenciador.contarPorTipo(TipoTransacao.DESPESA);
        int    total     = transacoesObservable.size();

        Locale ptBR = new Locale("pt", "BR");
        String fmt   = "R$ %.2f";

        lblSaldoTotal.setText(String.format(ptBR, fmt, saldo));
        lblTotalReceitas.setText(String.format(ptBR, fmt, receitas));
        lblTotalDespesas.setText(String.format(ptBR, fmt, despesas));
        lblQtdReceitas.setText(qtdRec    + " transação(ões)");
        lblQtdDespesas.setText(qtdDesp   + " transação(ões)");
        lblSaldoFooter.setText(String.format(ptBR, fmt, saldo));
        lblQtdRegistros.setText(total    + " registros");
    }

    private void limparCampos() {
        txtDescricao.clear();
        txtValor.clear();
        cbTipo.getSelectionModel().clearSelection();
        cbCategoria.getSelectionModel().clearSelection();
        dpData.setValue(LocalDate.now());
    }

    private void exibirErro(String mensagem) {
        lblErro.setText(mensagem);
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }

    private void ocultarErro() {
        lblErro.setText("");
        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}