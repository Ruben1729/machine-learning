import static java.lang.Math.exp;
import static java.lang.Math.pow;

import java.util.ArrayList;
import java.util.Random;


public class Layer {
	
	private int inSize, outSize;
	private double[][] weights;
	private double[] biases;
	
	private double[] sums;
	
	private double[][] gradient;
	private double[] gradientBiases;
	
	public Layer(int inSize, int outSize) {
		this.inSize = inSize;
		this.outSize = outSize;
		this.sums = new double[outSize];
		this.gradient = new double[outSize][inSize];
		this.gradientBiases = new double[outSize];
		this.weights = genWeights(inSize, outSize);
		this.biases = genBiases(outSize);
	}
	
	public Layer(int inSize, int outSize, double[][] weights, double[] biases) {
		this.inSize = inSize;
		this.outSize = outSize;
		this.weights = weights;
		this.biases = biases;
	}
	
	public int numInputNeurons() {
		return inSize;
	}
	
	public int numOutputNeurons() {
		return outSize;
	}
	
	public double[][] getWeights() {
		return this.weights;
	}
	
	public double[] getSums() {
		return this.sums;
	}
	
	public double[] getBiases() {
		return this.biases;
	}
	
	public int inSize() {
		return this.inSize;
	}
	
	public int outSize() {
		return this.outSize;
	}
	
	private static double[] genBiases(int numWeights){
		Random rand = new Random();
		
		double[] result = new double[numWeights];
		for(int i = 0; i < numWeights; i++) 
			result[i] = rand.nextFloat() * 10.0f - 5.0f;
		
		return result;
	}
	
	private static double[][] genWeights(int inSize, int outSize){
		Random rand = new Random();
		
		double[][] result = new double[outSize][inSize];
		for(int i = 0; i < outSize; i++) 
			for(int j = 0; j < inSize; j++)
				result[i][j] = rand.nextFloat() * 10.0f - 5.0f;
		
		return result;
	}
	
	private static float sigmoid(double input) {
		return (float)(1.0f / (1.0f + exp(-input)));
	}
	
	private static float drvSigmoid(double x) {
		return (float)(exp(x) / pow(exp(x) + 1, 2));
	}
	
	public ArrayList<Float> genOutputs(ArrayList<Float> data){
		ArrayList<Float> result = new ArrayList<Float>();
		for(int i = 0; i < outSize; i++) {
			
			double sum = 0.0D;
			for(int j = 0; j < inSize; j++)
				sum += data.get(j) * this.weights[i][j];
			
			sum += biases[i];
			
			sums[i] = sum;
			
			result.add(sigmoid(sum));
		}
		return result;
	}
	
	public double[] backpropagation(ArrayList<Float> in, double[] drvCostActivations) {
		double[] resDrvCostActivations = new double[inSize];
		for(int j = 0; j < inSize; j++) {
			double sum = 0;
			for(int k = 0; k < outSize; k++) {
				
				double drvActivationZ = drvSigmoid(sums[k]);
				double drvZWeight = in.get(j);
				
				// Change biases.
				this.gradientBiases[k] = drvCostActivations[k] * drvActivationZ;
				this.biases[k] -= drvCostActivations[k] * drvActivationZ;
			
				double result = drvCostActivations[k] * drvActivationZ * drvZWeight;
				sum += drvCostActivations[k] * drvActivationZ * this.weights[k][j];
				
				// Change weights.
				this.gradient[k][j] = result;
				this.weights[k][j] -= result;
			}
			resDrvCostActivations[j] = sum;
		}
		return resDrvCostActivations;
	}
	
}
