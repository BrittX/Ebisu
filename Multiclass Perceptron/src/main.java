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
		// num classes will likely be unnecessary soon
		numClasses = Integer.parseInt(args[3]);
		config = args[4];
		System.out.println("Starting variables assigned");
		ArrayList map = new ArrayList<Cluster>();
		//initialize network
		//retrieve training data set
		ArrayList train = new ArrayList<dataPoint>();
		ArrayList testingData = new ArrayList<dataPoint>();
		ArrayList dict = new ArrayList<String>();
		ArrayList log = new ArrayList<dataPoint>(); 
		initConfig(dict,config);
		retrieveData(train, trnSet, numIn);
		retrieveData(testingData, testSet, numIn);
		Dbscan(train, 50, 10);
		log.add(train.get(0));
		for (int i = 1; i < train.size(); i++){
			dataPoint cur = (dataPoint)train.get(i);
			dataPoint prev = (dataPoint)train.get(i-1);
			if (cur.cluster != prev.cluster)
				log.add(cur);
			else{
				dataPoint lastLog = (dataPoint)log.get(log.size()-1);
				lastLog.stup();
			}	
		}
		for (Object data : log){
			dataPoint dt = (dataPoint)data;
			if (dt.cluster < dict.size())
				System.out.println(dict.get(dt.cluster) + " strength: " + dt.strength);
			else
				System.out.println("New Gesture " + dt.cluster + " strength: " + dt.strength);
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
	
	public static void retrieveData(ArrayList<dataPoint> data, String setFile, int numInputs){
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(setFile)));
			int numLines = Integer.parseInt(br.readLine());
			for(int i = 0; i < numLines; i++){
				String[] linearr = br.readLine().split(",");
				dataPoint temp = new dataPoint(numInputs);
				for (int x = 0; x < numInputs; x++){
					temp.inputs[x] = Float.parseFloat(linearr[x]);
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
				expandCluster(p, neighbors, C, eps, minPts, data);
			}
		}
	}
	public static void expandCluster(dataPoint p, ArrayList<dataPoint> neighbors, int C, float eps, int minPts, ArrayList<dataPoint> data){
		p.cluster = C;//add p to cluster C
		int i = 0; // counter variable
		while(i < neighbors.size()){
			if (!neighbors.get(i).visited){
				neighbors.get(i).visited = true;
				ArrayList primeNeighbors = rQuery(neighbors.get(i), eps, data);
				if (primeNeighbors.size() >= minPts)
					neighbors.addAll(primeNeighbors);
			}
			if (neighbors.get(i).cluster == 0)
				neighbors.get(i).cluster = C;
				
			i++;
		}
	}
	public static ArrayList<dataPoint> rQuery(dataPoint dp, float eps, ArrayList<dataPoint> data){
		ArrayList neighbors = new ArrayList<dataPoint>();
		for (dataPoint p : data){
			if (p.dist(dp) < eps)
				neighbors.add(p);
		}
		return neighbors;
	}
}
