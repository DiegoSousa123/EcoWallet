package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

public class TitleBarController {
    // calcula o arrasto da janela
    private double xOffset = 0;
    private double yOffset = 0;
    private double originalX, originalY, originalWidth, originalHeight;
    private WindowMaximizationState windowState;
    // Elementos da barra de título
    @FXML
    private HBox barraJanela;
    @FXML private Button btnMinimizarJanela;
    @FXML private Button btnMaximizarJanela;
    @FXML private Button btnFecharJanela;
    @FXML private FontIcon maximizeIcon;

    @FXML
    public void minimizarJanela(ActionEvent e){
        getStage().setIconified(true);
    }

    @FXML
    public void maximizarJanela(ActionEvent e){
        maximizar();
    }
    @FXML
    public void fecharJanela(ActionEvent e){
        getStage().close();
    }

    @FXML
    public void aoPressionarMouse(MouseEvent mouseEvent){
        //captura a posição atual do mouse ao pressionar o click sob a barra de título
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }

    @FXML
    public void aoClicarDuasVezes(MouseEvent event){
        //maxima a tela ao clicar duas vezes na barra de título
        if(event.getClickCount() == 2){
            maximizar();
        }
    }

    @FXML
    public void aoArrastarMouse(MouseEvent mouseEvent){

        if (!mouseEvent.isPrimaryButtonDown()) {
            return;
        }

        Stage stage = getStage();

        if(isWindowMaximized()){
            maximizar();
        }
        //atualiza a posição da janela (move a janela) ao arrastar o
        // mouse enquanto mantem pressionado sob a barra de título
        stage.setX(mouseEvent.getScreenX() - xOffset);
        stage.setY(mouseEvent.getScreenY() - yOffset);
    }

    private Stage getStage(){
        return (Stage) barraJanela.getScene().getWindow();
    }

    public void setWindowState(WindowMaximizationState windowState) {
        this.windowState = windowState;
    }

    // metodo utilitário para gerenciar a maximização e mudança do ícone do botão
    private void maximizar() {
        Stage stage = getStage();
        boolean maximized = isWindowMaximized();
        gerenciarMaximizacao(stage);
        maximizeIcon.setIconLiteral(maximized ? "remixal-checkbox-blank-line" : "remixal-checkbox-multiple-blank-line");
    }

    private void gerenciarMaximizacao(Stage stage) {
        if (!isWindowMaximized()) { //janela não está maximizada
            // salva o tamanho e a posição atual da janela
            originalX = stage.getX();
            originalY = stage.getY();
            originalWidth = stage.getWidth();
            originalHeight = stage.getHeight();

            // pega as dimensões da area de trabalho do monitor principal (que exclui a barra de tarefas)
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

            // aplica o novo tamanho para a janela com base nas dimensões coletadas
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            setWindowMaximized(true);
        } else { // janela está maximizada
            // restaura o tamanho anterior
            stage.setX(originalX);
            stage.setY(originalY);
            stage.setWidth(originalWidth);
            stage.setHeight(originalHeight);

            setWindowMaximized(false);
        }
    }

    private boolean isWindowMaximized() {
        if (windowState == null) {
            throw new IllegalStateException("Window state not set");
        }
        return windowState.isWindowMaximized();
    }

    private void setWindowMaximized(boolean maximized) {
        if (windowState == null) {
            throw new IllegalStateException("Window state not set");
        }
        windowState.setWindowMaximized(maximized);
    }
}
