import java.util.Vector

public class Cluster{
	Vector center;
	int count = 1;
	public Cluster(Vector center){
		this.center = center;
	}
	public void add(Vector curVec){
		for (int i = 0; i < curVec.size(); i++){
			float temp = 0.0
			temp = (count * center.get(i) + curVec.get(i))/(count+1);
			count++;
		}
	}
	public float clusterDist(Vector curVec){
			float euSq = 0.0;
			for (int i = 0; i < curVec.size(); i++){
				float temp = (curVec.get(i) - center.get(i));
				euSq += temp*temp;
			}
			return euSq;
		}
}