
public class dataObject {
	int tag;
	int numInputs;
	public double[] inputs;
	public dataObject(int tag, int numInputs){
		this.tag = tag;
		this.numInputs = numInputs;
		inputs = new double[numInputs];
	}
}
