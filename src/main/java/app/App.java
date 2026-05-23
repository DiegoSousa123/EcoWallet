package app;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

	// variaveis referentes ao resizer da janela
	private Stage primaryStage;
	private BorderPane mainRoot;
	private static final double RESIZER_WIDTH = 3.0;
	private ResizeEdge currentEdge = null;
	private double initialX;
	private double initialY;
	private double initialWidth;
	private double initialHeight;
	private double initialStageX;
	private double initialStageY;

	private enum ResizeEdge {
		NONE, LEFT, RIGHT, TOP, BOTTOM, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}

	@Override
	public void start(Stage stage) throws Exception {

		stage.initStyle(StageStyle.UNDECORATED);
		primaryStage = stage;
		Parent root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));

		mainRoot = (BorderPane) root;
		
		mainRoot.setOnMouseMoved(event -> {
			if(stage.isMaximized()) {
				currentEdge = ResizeEdge.NONE;
				updateCursor();
				return;
			}
			currentEdge = getResizeEdge(event.getSceneX(), event.getSceneY());
			updateCursor();
		});

		mainRoot.setOnMouseExited(event -> {
			if (!event.isPrimaryButtonDown()) {
		        currentEdge = ResizeEdge.NONE;
		        updateCursor();
		    }
		});


		Scene scene = new Scene(root);
		
		scene.setOnMousePressed(event -> {
			root.requestFocus();
			if (currentEdge != null && currentEdge != ResizeEdge.NONE && !stage.isMaximized()) {
				// armazena o estado atual do arrasto
				initialX = event.getScreenX();
				initialY = event.getScreenY();
				initialWidth = stage.getWidth();
				initialHeight = stage.getHeight();
				
				initialStageX = primaryStage.getX();
		        initialStageY = primaryStage.getY();

				event.consume();
			}
		});

		scene.setOnMouseDragged(event -> {
			if (currentEdge != null && currentEdge != ResizeEdge.NONE && !stage.isMaximized()) {
				resizeStage(event);
				event.consume();
			}
		});
		
		scene.setOnMouseReleased(event -> {
		    currentEdge = ResizeEdge.NONE;
		    updateCursor();
		});
		stage.setTitle("EcoWallet — Controle Financeiro");
		stage.setMinWidth(900);
		stage.setMinHeight(600);
		stage.setScene(scene);
		stage.show();
	}

	//metodo principal para o redimensionamento da janela
	private void resizeStage(MouseEvent event) {
	    // deltaX é o resultado da subtração da posição no eixo x atual do mouse na tela
		// com a posição inicial dele no momento do click
		// O mesmo vale para deltaY
		double deltaX = event.getScreenX() - initialX;
	    double deltaY = event.getScreenY() - initialY;

		// limite de tamanho mínimo baseado no definido pelo Stage
	    double minWidth = primaryStage.getMinWidth();
	    double minHeight = primaryStage.getMinHeight();

	    switch(currentEdge) {
			// realiza calculos e aplica os redimensionamentos com
			// base em qual borda o mouse está atualmente e considerando o limite de tamanho mínimo
	        case RIGHT:
	            primaryStage.setWidth(Math.max(initialWidth + deltaX, minWidth));
	            break;
	            
	        case LEFT:
	            double newWidthLeft = initialWidth - deltaX;
	            if (newWidthLeft >= minWidth) {
	                primaryStage.setWidth(newWidthLeft);
	                primaryStage.setX(initialStageX + deltaX);
	            }
	            break;
	            
	        case BOTTOM:
	            primaryStage.setHeight(Math.max(initialHeight + deltaY, minHeight));
	            break;
	            
	        case TOP:
	            double newHeightTop = initialHeight - deltaY;
	            if (newHeightTop >= minHeight) {
	                primaryStage.setHeight(newHeightTop);
	                primaryStage.setY(initialStageY + deltaY);
	            }
	            break;
	            
	        case TOP_LEFT:
	            double newWidthTL = initialWidth - deltaX;
	            double newHeightTL = initialHeight - deltaY;
	            if (newWidthTL >= minWidth) {
	                primaryStage.setWidth(newWidthTL);
	                primaryStage.setX(initialStageX + deltaX);
	            }
	            if (newHeightTL >= minHeight) {
	                primaryStage.setHeight(newHeightTL);
	                primaryStage.setY(initialStageY + deltaY);
	            }
	            break;
	            
	        case TOP_RIGHT:
	            double newHeightTR = initialHeight - deltaY;
	            primaryStage.setWidth(Math.max(initialWidth + deltaX, minWidth));
	            if (newHeightTR >= minHeight) {
	                primaryStage.setHeight(newHeightTR);
	                primaryStage.setY(initialStageY + deltaY);
	            }
	            break;
	            
	        case BOTTOM_LEFT:
	            double newWidthBL = initialWidth - deltaX;
	            if (newWidthBL >= minWidth) {
	                primaryStage.setWidth(newWidthBL);
	                primaryStage.setX(initialStageX + deltaX);
	            }
	            primaryStage.setHeight(Math.max(initialHeight + deltaY, minHeight));
	            break;
	            
	        case BOTTOM_RIGHT:
	            primaryStage.setWidth(Math.max(initialWidth + deltaX, minWidth));
	            primaryStage.setHeight(Math.max(initialHeight + deltaY, minHeight));
	            break;
	            
	        default:
	            break;
	    }
	}

	private void updateCursor() { // atualiza o visual do cursor de acordo com a direção
		switch(currentEdge) {
		case LEFT, RIGHT:
			mainRoot.setCursor(Cursor.H_RESIZE); //cursor resize horizontal
			break;
		case TOP, BOTTOM:
			mainRoot.setCursor(Cursor.V_RESIZE); //cursor resize vertical
			break;
		case TOP_LEFT, BOTTOM_RIGHT:
			mainRoot.setCursor(Cursor.NW_RESIZE); //cursor resize diagonal
			break;
		case TOP_RIGHT, BOTTOM_LEFT:
			mainRoot.setCursor(Cursor.NE_RESIZE); //cursor resize diagonal
			break;
		default:
			mainRoot.setCursor(Cursor.DEFAULT); //cursor padrão
		}
	}

	//método para determinar onde o cursor está atualmente
	private ResizeEdge getResizeEdge(double mouseX, double mouseY) {
		double stageWidth = primaryStage.getWidth();
		double stageHeight = primaryStage.getHeight();
		
		//checa em qual borda da janela o mouse está aproximadamente
		boolean isLeft = mouseX <= RESIZER_WIDTH;
		boolean isRight = mouseX >= stageWidth - RESIZER_WIDTH;
		boolean isTop = mouseY <= RESIZER_WIDTH;
		boolean isBottom = mouseY >= stageHeight - RESIZER_WIDTH;
		
		//checa em qual borda está o mouse exatamente
		
		if(isTop && isLeft) return ResizeEdge.TOP_LEFT;
		if(isTop && isRight) return ResizeEdge.TOP_RIGHT;
		if(isBottom && isLeft) return ResizeEdge.BOTTOM_LEFT;
		if(isBottom && isRight) return ResizeEdge.BOTTOM_RIGHT;
		if(isLeft) return ResizeEdge.LEFT;
		if(isRight) return ResizeEdge.RIGHT;
		if(isTop) return ResizeEdge.TOP;
		if(isBottom) return ResizeEdge.BOTTOM;
		return ResizeEdge.NONE;
		
	}

	
	public static void main(String[] args) {
		launch(args);
	}
}