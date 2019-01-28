import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class Main implements Runnable{
	
	private static final String path = "./samples";
	
	private ArrayList<Image> imgs;
	
	private int imageSize = 0;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		this.run();
	}
	
	public void run() {
		
		Random rand = new Random();
		this.imgs = this.loadImages();
		
		Image img = this.imgs.get(rand.nextInt(200));
		
		for(int i = 0; i < img.getWidth(); i++) {
			for(int j = 0; j < img.getHeight(); j++) 
				System.out.print(img.getData()[(i * 28) + j] != 0 ? "@" : ".");
			System.out.println();
		}
		
		ArrayList<Float> in = new ArrayList<Float>();
		for(int i = 0; i < imageSize; i++)
			in.add((float)img.getData()[i] / 255.0f);
		
		Network net = new Network(new Layer[] {new Layer(imageSize, 16), new Layer(16, 16), new Layer(16, 10)});
		ArrayList<Float> out = net.generateOutputs(in);
		ArrayList<Float> desired = new ArrayList<Float>();
		for(int i = 0; i < 10; i++)
			if( i == img.getLabel())
				desired.add(1.0f);
			else
				desired.add(0.0f);
		
		System.out.println("Expected - " + img.getLabel());
		System.out.println("Cost - " + net.cost(in, desired));
		for(int i = 0; i < out.size(); i++)
			System.out.println(i + " - " + out.get(i));
		
		for(int i = 0 ; i < 10000; i++)
			net.backprop(in, desired);
		out = net.generateOutputs(in);
		
		System.out.println("\nNew Cost - " + net.cost(in, desired));
		for(int i = 0; i < out.size(); i++)
			System.out.println(i + " - " + out.get(i));
	}
	
	
	private ArrayList<Image> loadImages() {
		
		ArrayList<Image> images = new ArrayList<Image>();
		
		try {
			
			InputStream imgStream = Main.class.getResourceAsStream(path + "/train-images.idx3-ubyte");
			InputStream labelStream = Main.class.getResourceAsStream(path + "/train-labels.idx1-ubyte");
			
			int labelByte = 0;
			
			byte[] header = new byte[16];
			imgStream.read(header);
			labelStream.read(new byte[8]);
		
			int rows =  byteArrayToInt(new byte[] {header[8], header[9], header[10], header[11]});
			int columns =  byteArrayToInt(new byte[] {header[12], header[13], header[14], header[15]});
			
			final int size = rows * columns;
			byte[] buffer = new byte[size];
			this.imageSize = size;
			
			while((imgStream.read(buffer)) != -1 && (labelByte = labelStream.read()) != -1) {
				images.add(new Image(labelByte, buffer, rows, columns));
				buffer = new byte[size];
			}
			
		}catch(Exception e) {
			
			e.printStackTrace();
			System.exit(-1);
			return null;
			
		}
		
		return images;
	}
	
	public static int byteArrayToInt(byte[] b){
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}

}
