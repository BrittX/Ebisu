import java.util.*;
import java.io.*;
public class main {
	static float MAX_DIST = 0.0f;
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
		ArrayList map = new ArrayList<Cluster>();
		//initialize network
		//retrieve training data set
		ArrayList train = new ArrayList<Vector>();
		ArrayList testingData = new ArrayList<Vector>();
		ArrayList dict = new ArrayList<String>();
		initConfig(dict,config);
		retrieveData(train, trnSet, numIn);
		retrieveData(testingData, testSet, numIn);
		
		for (Object data : train){
			System.out.println(feed(map,(Vector)data));
		}
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
	
	public static void retrieveData(ArrayList data, String setFile, int numInputs){
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(setFile)));
			int numLines = Integer.parseInt(br.readLine());
			for(int i = 0; i < numLines; i++){
				String[] linearr = br.readLine().split(",");
				Vector temp = new Vector();
				for (int x = 0; x < linearr.length; x++){
					temp.add(Float.parseFloat(linearr[x]));
				}
				data.add(temp);
			}
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
	public static int feed(ArrayList<Cluster> map, Vector data){
		Cluster near = null;
		float dist = -1.0f;
		for (Object cl : map){
			
			Cluster cur = (Cluster)cl;
			float temp = cur.clusterDist(data);
			if (dist < 0 || dist < temp){
				dist = temp;
				near = cur;
			}
		}
		if (dist > 0 && dist < MAX_DIST){
			near.add(data);
		}else{
			System.out.println(dist);
			near = new Cluster(data,map.size());
			map.add(near);
		}
		return near.getId();
	}
	
	public static void Dbscan(ArrayList<dataPoint> data, float eps, int minPts){
		int C = 0;
		for (dataPoint p : data){
			if (p.visited)
				continue;
			p.visited = true;
			ArrayList neighbors = rQuery(p, eps, data);
			if (neighbors.size() < minPts)
				p.noise = true;
			else{
				C++;
				expandCluster(p, neighbors, C, eps, minPts);
			}
		}
	}
	public static void expandCluster(dataPoint p, ArrayList<dataPoint> neighbors, int C, float eps, int minPts){
		p.cluster = C; //add p to cluster C
		for (dataPoint dp : neighbors){
			if (!dp.visited){
				dp.visited = true;
				ArrayList primeNeighbors = rQuery(dp, eps, data);
				if (primeNeighbors.size() >= minPts)
					neighbors.addAll(primeNeighbors);
			}
			if (dp.cluster == 0)
				dp.cluster = c;
		}
	}
	public static ArrayList<dataPoint>(dataPoint dp, float eps, ArrayList<dataPoint> data){
		ArrayList neighbors = new ArrayList<dataPoint>();
		for (dataPoint p : data){
			if (p.dist(dp) < eps)
				neighbors.add(p);
		}
		return neighbors;
	}
}
