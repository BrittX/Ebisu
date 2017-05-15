
public class dataPoint {
	public int cluster = 0;
	public boolean visited;
	public boolean noise;
	int numInputs;
	int strength;
	public double[] inputs;
	public dataPoint(int numInputs){
		this.numInputs = numInputs;
		this.strength = 1;
		inputs = new double[numInputs];
	}
	public double dist(dataPoint p){
		double distance = 0.0;
		for (int i = 0; i < numInputs; i++){
			double temp = (inputs[i] - p.inputs[i]);
			distance += temp*temp;
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	public void stup(){
		strength++;
	}
}
