import java.util.*;
import java.io.*;
public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String trnSet;
		String testSet;
		String config;
		int numIn;
		int numClasses;
		ArrayList<Neuron> Net = new ArrayList<Neuron>();
		if (args.length < 5){
			System.out.println("Invalid Parameters.");
			System.out.println("Correct Parameters: Training Set (File), Testing Set (File), No. of Inputs(Int), No. of Starting Classes (int), Config (File)");
			System.exit(1);
		}
		//assign starting variables
		trnSet = args[0];
		testSet = args[1];
		numIn = Integer.parseInt(args[2]);
		numClasses = Integer.parseInt(args[3]);
		config = args[4];
		System.out.println("Starting variables assigned");
		//initialize network
		initNet(Net,numIn,numClasses);
		//retrieve training data set
		ArrayList train = new ArrayList<dataObject>();
		ArrayList testingData = new ArrayList<dataObject>();
		ArrayList dict = new ArrayList<String>();
		initConfig(dict,config);
		//newClassProt
		retrieveData(train, trnSet, numIn);
		retrieveData(testingData, testSet, numIn);
		learn(Net,train);
		test(Net,testingData,dict);
	}
	public static void initConfig(ArrayList<String> dict, String config){
			try{
				BufferedReader br = new BufferedReader(new FileReader(new File(config)));
				String temp = br.readLine();
				while (temp != null){
					dict.add(temp);
					temp = br.readLine();
				}
			}
			catch (Exception e){
				System.out.println(e.toString());
			}
			System.out.println("Configuration complete");
	}
	public static void initNet(ArrayList<Neuron> Net, int numIn, int numClasses){
		//initialize input layer net
		
		for(int i = 0; i < numIn; i++){
			Net.add(new Neuron(numClasses));
		}
		System.out.println("Neural Network Initialized");
	}
	public static void retrieveData(ArrayList data, String setFile, int numInputs){
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(setFile)));
			int numLines = Integer.parseInt(br.readLine());
			for(int i =0; i < numLines; i++){
				String[] linearr = br.readLine().split(",");
				dataObject temp = new dataObject(Integer.parseInt(linearr[numInputs]),numInputs);
				for(int y = 0; y < numInputs; y++){
					temp.inputs[y] = Double.parseDouble(linearr[y]);
				}
				data.add(temp);
			}
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
	public static void learn(ArrayList<Neuron> Net, ArrayList<dataObject> data){
		boolean perfect = false;
		int c = 0;
		while(!perfect){
			System.out.println("epoch number: " + c);
			perfect = true;
			for(dataObject sample : data){
				int ans = feed(Net,sample);
				//System.out.println(ans);
				if (ans != sample.tag){
					perfect = false;
					int i = 0;
					for(Neuron n : Net){
						n.Weights[ans] -= 0.0125*sample.inputs[i];
						n.Weights[sample.tag] += 0.0125*sample.inputs[i];
						i++;
					}
				}
			}
			c++;
		}
		System.out.println("learning completed");
	}
	public static void test(ArrayList<Neuron> Net, ArrayList<dataObject> data, ArrayList<String> dict){
			float accuracy = 0.0f;
			int numSamp = 0;
			for(dataObject sample : data){
				int ans = feed(Net,sample);
				System.out.println(dict.get(ans));
				if(ans == sample.tag)
					accuracy += 1.0f;
				numSamp++;
				
			}
			accuracy = accuracy/numSamp;
			System.out.println("Testing complete with " + accuracy*100 + "% accuracy.");
		}
	public static int feed(ArrayList<Neuron> Net, dataObject sample){
		double[] sums = new double[Net.get(0).Weights.length];
		int i = 0;
		for(Neuron n : Net){
			for(int y = 0; y < n.Weights.length; y++){
				sums[y] += (sample.inputs[i])*n.Weights[y];
			}
			i++;
		}
		int maxid = 0;
		for(int x = 0; x < Net.get(0).Weights.length; x++){
			if (sums[maxid] < sums[x])
				maxid = x;
		}
		return maxid;
	}
}
