package tictactoe;

public class Network {
	
	public Layer[] layers;
	
	public Network(Layer[] layers) {
		this.layers = layers;
	}
	
	public float[] generateOutputs(float[] in){
		float[] result = in;
		for(Layer l : layers)
			result = l.genOutputs(result);
		
		return result;
	}
	
	public float cost(float[] in, float[] desiredOut) {
		float[] out = this.generateOutputs(in);
		float cost = 0;
		for(int i = 0; i < out.length; i++)
			cost += Math.pow(desiredOut[i] - out[i], 2);
		return cost;
	}
	
	public void backprop(float[] in, float[] desired) {
		float[] out = this.generateOutputs(in);
		
		double[] drvCostActivations = new double[layers[layers.length - 1].outSize()];
		for(int i = 0; i < desired.length; i++)
			drvCostActivations[i] = 2 * (out[i] - desired[i]);
		
		for(int k = layers.length - 1; k >= 0; k--) {
			Layer currLayer = layers[k];
			float[] input = in;
			for(int x = 0; x < k; x++)
				input = layers[x].genOutputs(input);
			
			drvCostActivations = currLayer.backpropagation(input, drvCostActivations);
		}
	}
}