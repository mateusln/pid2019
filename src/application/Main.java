package application;
	
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import application.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


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
			primaryStage.setTitle("JavaFX meets OpenCV");
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
	

	
	
	public void getImage() {
		// load the OpenCV native library
//	      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//	      
//	   // prepare to convert a RGB image in gray scale
//	      String location = "resources/Poli.jpg";
////	      System.out.print("Convert the image at " + location + " in gray scale... ");
//	      // get the jpeg image from the internal resource folder
//	      Mat image = Imgcodecs.imread(location);
////	      Mat test = new Mat
//	      Image imageToShow = Utils.mat2Image(image);
////	      img_carregada.setImage(imageToShow);
//	      // convert the image in gray scale
////	      Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
//	      updateImageView(img_carregada, imageToShow);
	      // write the new image on disk
//	      Imgcodecs.imwrite("resources/Poli-gray.jpg", image);
	      System.out.println("Done!");
	}
	
	
}

