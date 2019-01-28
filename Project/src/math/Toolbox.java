package math;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

public class Toolbox {
	
	public static float sigmoid(double input) {
		return (float)(1.0f / (1.0f + exp(-input)));
	}
	
	public static float drvSigmoid(double x) {
		return (float)(exp(x) / pow(exp(x) + 1, 2));
	}
	
}
