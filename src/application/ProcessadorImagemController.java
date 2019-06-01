package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import com.sun.xml.internal.ws.util.StringUtils;

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
    
    private int pixel_inicial_cod_barra;

    
	public void abrir_imagem() throws FileNotFoundException {
		System.out.println("abriu");
		
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter =
		new FileChooser.ExtensionFilter(
				"Arquivos de imagem",
				"*.png",
				"*.jpg",
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
		//forma antiga
//		Mat kernel = Mat.ones(1, 3, CvType.CV_8U);
//		Imgproc.morphologyEx(image, image, Imgproc.MORPH_BLACKHAT, kernel, new Point(1,0));
		Imgproc.threshold(image, image, 200, 500, Imgproc.THRESH_BINARY);
		mede_tamanho_barras(image);
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
//		File file2 = new File(img1); 
		Imgcodecs.imwrite("file1.jpg", image); 

		kernel = Mat.ones(1, 5, CvType.CV_8U);
		Imgproc.morphologyEx(image, image, Imgproc.MORPH_DILATE, kernel, new Point(2,0), 2);
		Imgproc.morphologyEx(image, image, Imgproc.MORPH_CLOSE, kernel,  new Point(2,0), 2);
		
		Imgcodecs.imwrite("file2.jpg", image); 

		
		kernel = Mat.ones(21, 35, CvType.CV_8U);
		Imgproc.morphologyEx(image, image, Imgproc.MORPH_OPEN, kernel, new Point(-1,-1), 1);
		
		Imgcodecs.imwrite("file3.jpg", image); 

		
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
	
	private int mede_tamanho_barras(Mat image) {
		
		int num_colunas = image.cols();
//		int num_linhas = image.rows();

		double valores_linha[] = new double[num_colunas];
		
		//pega intensidade das barras em uma linha
		for (int i=0 ; i<num_colunas ; i++) {
			double array_temp[] = image.get(62,i); // pega na linha 62 @todo otimizar para pegar varias linhas
			valores_linha[i] = array_temp[0];
			System.out.print(array_temp[0]+", ");
		}
		
		int soma_pixels = 0;
		char cor = 'b';//assume que a primeira cor é branca
		if(valores_linha[0] < 125)//se intensidade do primeiro pixel for menor q 125 , primeiro pixel é preto
			cor = 'p';
		
		int tam_barra_branca = 0;
		int tam_barra_preta = 0;
		int bit_size = -1;
		
		
		//pega intensidade das barras
		for (int i = 1; i < valores_linha.length ; i++) {
			soma_pixels++;
			//se existe uma transição de cores
			if(valores_linha[i-1] != valores_linha[i]){				
				int tamanho_da_barra = soma_pixels;
				System.out.println(tamanho_da_barra);
				
				if(cor == 'b')
					tam_barra_branca = tamanho_da_barra;
				else
					tam_barra_preta = tamanho_da_barra;
				//confere se as 2 proximas barras tem tamanhos iguais
				if(tam_barra_branca == tam_barra_preta) {
						pixel_inicial_cod_barra = i+tamanho_da_barra;
						bit_size = tam_barra_preta;
						break;
					
				}
				soma_pixels = 0;
				cor = muda_cor(cor);
			}
		}
		
		ler_digito(valores_linha, bit_size);
		return bit_size;
		
	}
	
	private void ler_digito(double [] valores_linha, int bit_size){
		int contador_bits=0;
		String vet_bit = "";
		int digito=0;
		for(int i=pixel_inicial_cod_barra; i<valores_linha.length; i+=bit_size) {//pula o tam do bit no vetor
			
			double valores_uma_barra[] = Arrays.copyOfRange(valores_linha, i, i+bit_size);
			
			char bit = verifica_bit(valores_uma_barra);
			System.out.print(bit);
			vet_bit += bit;
			
//			teste.
//			if(valores_linha[i] > 100)
//				System.out.print(0);
//			else
//				System.out.print(1);
			
			contador_bits++;// a cada 7 bits adiciona um separador pq é um digito
			if(contador_bits == 7) {
				digito++;
				contador_bits = 0;
				System.out.print('|');
				System.out.println(digito);
//				System.out.println(vet_bit+'\n');
//				aproxima_valores(vet_bit);
				vet_bit = "";
//				if(digito==6)
			}
			
		}
		contador_bits = 0;
		System.out.println(" ---- bits detectados");
		for(int i=0; i<valores_linha.length; i++) {
			
			if(i== pixel_inicial_cod_barra)
				System.out.println(" comecei valores dos pixels em 0 ou 1 \n");
			
			if(valores_linha[i] > 100)
				System.out.print(0);
			else
				System.out.print(1);
			if(i>=pixel_inicial_cod_barra)
				contador_bits++;
			if(contador_bits == (7*bit_size)) {
				contador_bits = 0;
				System.out.print('|');
			}
			
		}
	}
	
	private char muda_cor(char cor) {
		if(cor == 'b')
			cor = 'p';
		else
			cor = 'b';
		return cor;
		
	}
	
	//retorna se a intensidade da cor vai ser interpretada como 1 (preto) ou 0 (branco)
	private char verifica_bit(double []valores_intensidade_cor) {
		//contagem de pixels
		int conta_branco =0;
		int conta_preto =0;
		
		for (double v : valores_intensidade_cor) {
			if(v > 125)
				conta_branco++;
			else
				conta_preto++;
		}
		
		if(conta_branco > conta_preto)
			return '0';
		else
			return '1';
	}
	
	public void mostrar_img(Mat mat_img) {
		Image imageToShow = Utils.mat2Image(mat_img);
		updateImageView(img_carregada, imageToShow);
	}
	
	public int tabela_lado_esquerdo(String cadeia_bits) {
//		HashMap<K, V>
		HashMap<String,Integer> tabela_hash = new HashMap<String,Integer>();
		tabela_hash.put("0001101", 0);
		tabela_hash.put("0100111", 0);
		tabela_hash.put("0011001", 1);
		tabela_hash.put("0110011", 1);
		tabela_hash.put("0010011", 2);
		tabela_hash.put("0011011", 2);
		tabela_hash.put("0111101", 3);
		tabela_hash.put("0100001", 3);
		tabela_hash.put("0100011", 4);
		tabela_hash.put("0011101", 4);
		tabela_hash.put("0110001", 5);
		tabela_hash.put("0111001", 5);
		tabela_hash.put("0101111", 6);
		tabela_hash.put("0000101", 6);
		tabela_hash.put("0111011", 7);
		tabela_hash.put("0010001", 7);
		tabela_hash.put("0110111", 8);
		tabela_hash.put("0001001", 8);
		tabela_hash.put("0001011", 9);
		tabela_hash.put("0010111", 9);
		
		return -1;
		
	}
	
	String aproxima_valores (String valor) {
		ArrayList<String> valores_possiveis = new ArrayList<String>();
		valores_possiveis.add("0001101");
		valores_possiveis.add("0100111");
		valores_possiveis.add("0011001");
		valores_possiveis.add("0110011");
		valores_possiveis.add("0010011");
		valores_possiveis.add("0011011");
		valores_possiveis.add("0111101");
		valores_possiveis.add("0100001");
		valores_possiveis.add("0100011");
		valores_possiveis.add("0011101");
		valores_possiveis.add("0110001");
		valores_possiveis.add("0111001");
		valores_possiveis.add("0101111");
		valores_possiveis.add("0000101");
		valores_possiveis.add("0111011");
		valores_possiveis.add("0010001");
		valores_possiveis.add("0110111");
		valores_possiveis.add("0001001");
		valores_possiveis.add("0001011");
		valores_possiveis.add("0010111");
		
		System.out.println("Aproximidade");
		for (String v : valores_possiveis) {
			double distancia = levenshtein_distance(valor, v, 10);
			System.out.println(v);
			System.out.println(distancia);
			
		}
		return "str";
		
	}
	
	
	//calcula proximidade
	public double levenshtein_distance(final String s1, final String s2,
            final int limit) {
if (s1 == null) {
throw new NullPointerException("s1 must not be null");
}

if (s2 == null) {
throw new NullPointerException("s2 must not be null");
}

if (s1.equals(s2)) {
return 0;
}

if (s1.length() == 0) {
return s2.length();
}

if (s2.length() == 0) {
return s1.length();
}

// create two work vectors of integer distances
int[] v0 = new int[s2.length() + 1];
int[] v1 = new int[s2.length() + 1];
int[] vtemp;

// initialize v0 (the previous row of distances)
// this row is A[0][i]: edit distance for an empty s
// the distance is just the number of characters to delete from t
for (int i = 0; i < v0.length; i++) {
v0[i] = i;
}

for (int i = 0; i < s1.length(); i++) {
// calculate v1 (current row distances) from the previous row v0
// first element of v1 is A[i+1][0]
//   edit distance is delete (i+1) chars from s to match empty t
v1[0] = i + 1;

int minv1 = v1[0];

// use formula to fill in the rest of the row
for (int j = 0; j < s2.length(); j++) {
int cost = 1;
if (s1.charAt(i) == s2.charAt(j)) {
cost = 0;
}
v1[j + 1] = Math.min(
   v1[j] + 1,              // Cost of insertion
   Math.min(
           v0[j + 1] + 1,  // Cost of remove
           v0[j] + cost)); // Cost of substitution

minv1 = Math.min(minv1, v1[j + 1]);
}

if (minv1 >= limit) {
return limit;
}

// copy v1 (current row) to v0 (previous row) for next iteration
//System.arraycopy(v1, 0, v0, 0, v0.length);

// Flip references to current and previous row
vtemp = v0;
v0 = v1;
v1 = vtemp;

}

return v0[s2.length()];
}
	
	
}