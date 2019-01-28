import java.util.ArrayList;


public class Network {
	
	private Layer[] layers;
	
	public Network(Layer[] layers) {
		this.layers = layers;
	}
	
	public ArrayList<Float> generateOutputs(ArrayList<Float> in){
		ArrayList<Float> result = in;
		for(Layer l : layers)
			result = l.genOutputs(result);
		
		return result;
	}
	
	public float cost(ArrayList<Float> in, ArrayList<Float> desiredOut) {
		ArrayList<Float> out = this.generateOutputs(in);
		float cost = 0;
		for(int i = 0; i < out.size(); i++)
			cost += Math.pow(desiredOut.get(i) - out.get(i), 2);
		return cost;
	}
	
	public void backprop(ArrayList<Float> in, ArrayList<Float> desired) {
		ArrayList<Float> out = this.generateOutputs(in);
		
		double[] drvCostActivations = new double[layers[layers.length - 1].outSize()];
		for(int i = 0; i < desired.size(); i++)
			drvCostActivations[i] = 2 * (out.get(i) - desired.get(i));
		
		for(int k = layers.length - 1; k >= 0; k--) {
			Layer currLayer = layers[k];
			ArrayList<Float> input = in;
			for(int x = 0; x < k; x++)
				input = layers[x].genOutputs(input);
			
			drvCostActivations = currLayer.backpropagation(input, drvCostActivations);
		}
	}
}
