package application;
	


import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.layout.BorderPane;
import javafx.application.Application;

//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;




public class Main extends Application {
	

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("TelaPrincipal.fxml"));
			BorderPane root = (BorderPane) loader.load();
//			Main controller = loader.getController();
			
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			primaryStage.setTitle("Processador de Imagem");

			primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			// show the GUI
			primaryStage.show();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
		
	
}

