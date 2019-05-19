package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;


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
	
	public void limiarizar() { 
		Mat image = Imgcodecs.imread(arquivo_imagem.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);  
		Mat kernel = Mat.ones(1, 3, CvType.CV_8U);
		Imgproc.morphologyEx(image, image, Imgproc.MORPH_BLACKHAT, kernel, new Point(1,0));
		Image imageToShow = Utils.mat2Image(image);
		updateImageView(img_carregada, imageToShow);	
	}
	
	public void segmentar() { 
		Mat image = Imgcodecs.imread(arquivo_imagem.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
		
		double scale =   800.0 / image.cols();
		Mat resizeImage = new Mat(( int) (image.rows() * scale), (int) (image.cols() * scale), image.type());
	    Imgproc.resize(image, image, resizeImage.size());
	    
	    new Mat();
		Mat kernel = Mat.ones(1, 3, CvType.CV_8U);
		System.out.println(kernel.dump());
	    
		Imgproc.morphologyEx(image, image, Imgproc.MORPH_BLACKHAT, kernel, new Point(1,0));
		Imgproc.threshold(image, image, 10, 255, Imgproc.THRESH_BINARY);
		
		kernel = Mat.ones(1, 5, CvType.CV_8U);
		Imgproc.morphologyEx(image, image, Imgproc.MORPH_DILATE, kernel, new Point(2,0), 2);
		Imgproc.morphologyEx(image, image, Imgproc.MORPH_CLOSE, kernel,  new Point(2,0), 2);
		
		kernel = Mat.ones(21, 35, CvType.CV_8U);
		Imgproc.morphologyEx(image, image, Imgproc.MORPH_OPEN, kernel, new Point(-1,-1), 1);
		
		//
		Mat image_out = Imgcodecs.imread(arquivo_imagem.getAbsolutePath());
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		
		double unscale = 1.0 / scale;
		if(contours != null) { 
			for(int index=0;index < contours.size();index++) { 
				if(Imgproc.contourArea(contours.get(index)) <= 2000) { 
					continue; // skip interation
				}
				
				MatOfPoint contour = contours.get(index);
				MatOfPoint2f  NewMtx = new MatOfPoint2f( contour.toArray() );
				
				RotatedRect rect = Imgproc.minAreaRect(NewMtx);
				
				Point[] p = new Point[4];
				rect.points(p);	
				for(int pointIdx =0; pointIdx < 4;pointIdx++) { 
					p[pointIdx].x = p[pointIdx].x * unscale;
					p[pointIdx].y = p[pointIdx].y * unscale;
				}	
				MatOfPoint mp = new MatOfPoint(p);

				Imgproc.drawContours(image_out, Arrays.asList(mp), 0, new Scalar(255,0,0), 2);
			}
		}
		
		Image imageToShow = Utils.mat2Image(image_out);
		updateImageView(img_carregada, imageToShow);	
	}
	
	// printar/testar os numeros da matrix 
	private void showMatrixInRange(Mat image, Range range) {
		int [] buffer = new int[(int) image.total() * image.channels()];
		Mat ROU = image.submat(range, range);
		System.out.println(ROU.dump().replace("0,   ", ""));
	}
	
	private void set_image(File file) throws FileNotFoundException {
		FileInputStream input = new FileInputStream(file);
        Image imageToShow = new Image(input);
        updateImageView(img_carregada, imageToShow);
		
	}
}