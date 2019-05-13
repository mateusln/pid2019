package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ProcessadorImagemController implements Initializable {

    @FXML
    private ImageView img_carregada;
    @FXML
	private Button open_btn;
    
    private File arquivo_imagem;
    
    
	public void abrir_imagem() throws FileNotFoundException {
		System.out.println("abriu");
		
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter =
		new FileChooser.ExtensionFilter(
				"Arquivos de imagem",
				"*.png",
				"*.jpeg");
		fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Selecionar imagem");
        
        arquivo_imagem = fileChooser.showOpenDialog(open_btn.getScene().getWindow());

        if(arquivo_imagem != null) {
	        set_image(arquivo_imagem);
        }
	}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	      	  
 	      
    }
    
//    private void test_img_padrao() {
//    	 // prepare to convert a RGB image in gray scale
//	      String local = "resources/Poli.jpg";
////	      System.out.print("Convert the image at " + location + " in gray scale... ");
//	      // get the jpeg image from the internal resource folder
//	      Mat image = Imgcodecs.imread(local);
////	      Mat test = new Mat
//	      Image imageToShow = Utils.mat2Image(image);
////	      img_carregada.setImage(imageToShow);
//	      // convert the image in gray scale
////	      Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
//	      updateImageView(img_carregada, imageToShow);
//    }

	private void updateImageView(ImageView view, Image image)
	{
		Utils.onFXThread(view.imageProperty(), image);
	}
	
	public void converter_tons_cinza() {
		
		try {
			String local = arquivo_imagem.getAbsolutePath();
			// get the jpeg image from the internal resource folder
			Mat image = Imgcodecs.imread(local);
		    // convert the image in gray scale
		    Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
		    Image imageToShow = Utils.mat2Image(image);
		    updateImageView(img_carregada, imageToShow);
		}catch(Exception e ) {
			//@todo mostrar msg na tela
		}
		
	}
	
	private void set_image(File file) throws FileNotFoundException {
		FileInputStream input = new FileInputStream(file);
        Image imageToShow = new Image(input);
        updateImageView(img_carregada, imageToShow);
		
	}
}