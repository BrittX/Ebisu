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
		initNet(Net,numIn,numClasses + 1);
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
				dataObject temp;
				if (linearr.length <= numInputs) //means node is not tagged
					temp = new dataObject(0,numInputs);
				else
					temp = new dataObject(Integer.parseInt(linearr[numInputs]),numInputs);
				
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
			System.out.println("current epoch number: " + c);
			perfect = true;
			for(dataObject sample : data){
				int ans = feed(Net,sample);
				if (ans == 0){
					System.out.println("New Node Type recognized");
					perfect = false;
					int i = 0;
					for(Neuron n : Net){
						n.Weights.add(n.Weights.get(0) + 0.0125*sample.inputs[i]);
						sample.setTag(n.Weights.size()-1);
						n.Weights.set(0,(n.Weights.get(0) - 0.0125*sample.inputs[i])); //new node recognized
						i++;
					}
				}
				else if (ans != sample.tag){
					perfect = false;
					int i = 0;
					for(Neuron n : Net){
						n.Weights.set(ans, (n.Weights.get(ans) - 0.0125*sample.inputs[i]));
						n.Weights.set(sample.tag,(n.Weights.get(sample.tag) + 0.0125*sample.inputs[i]));
						i++;
					}
				}
			}
			c++;
		}
		System.out.println("Learning completed in " + c + "epochs.");
	}
	public static void test(ArrayList<Neuron> Net, ArrayList<dataObject> data, ArrayList<String> dict){
			float accuracy = 0.0f;
			int numSamp = 0;
			for(dataObject sample : data){
				int ans = feed(Net,sample);
				System.out.println(ans);
				
				if(ans == sample.tag)
					accuracy += 1.0f;
				numSamp++;
				
			}
			accuracy = accuracy/numSamp;
			System.out.println("Testing complete with " + accuracy*100 + "% accuracy.");
		}
	public static int feed(ArrayList<Neuron> Net, dataObject sample){
		ArrayList<Double> sums = new ArrayList<Double>();//[Net.get(0).Weights.length];
		int i = 0;
		for(Neuron n : Net){
			for(int y = 0; y < n.Weights.size(); y++){
				sums.add(0.0);
				sums.set(y, (sums.get(y) + (sample.inputs[i])*n.Weights.get(y)));
			}
			i++;
		}
		int maxid = 0;
		for(int x = 0; x < Net.get(0).Weights.size(); x++){
			if (sums.get(maxid) < sums.get(x))
				maxid = x;
		}
		return maxid;
	}
	public static void preprocessingRoutine(ArrayList data, ArrayList gestureList){
		//collapse idle nodes
		int n;
		double L2 = 0.75;
		int i = 0;
		while (i < data.size() - 1){
			dataObject cur = (dataObject)data.get(i);
			dataObject next = (dataObject)data.get(i + 1);
			boolean sim = true;
			while (sim){
				double dist = 0;
				for (int x = 0; x < cur.numInputs; x++){ // calculate euclidian distance
					double temp = cur.inputs[x] - next.inputs[x];
					dist += temp*temp;
				}
				if (dist > L2 || i == data.size() - 2){ // indicated nodes are too similare
					sim = false;
				}
				else{
					cur.stup();
					data.remove(next);
					next = (dataObject)data.get(i + 1);
				}
				i++;
			}
		}
		//separate dynamic nodes using node tree on training set
		//in future editions this may need to have an edit function
		 
		
		
	}
}
