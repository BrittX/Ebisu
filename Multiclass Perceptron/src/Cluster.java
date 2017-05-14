import java.util.Vector;

public class Cluster{
	Vector center;
	Vector weights;
	int count = 1;
	int id = 0;
	public Cluster(Vector center, int id){
		this.center = center;
		this.id = id;
	}
	public void add(Vector curVec){
		for (int i = 0; i < curVec.size(); i++){
			float temp = 0.0f;
			temp = (count * (float)center.get(i) + (float)curVec.get(i))/(count+1);
			count++;
		}
	}
	public float clusterDist(Vector curVec){
			float euSq = 0.0f;
			for (int i = 0; i < curVec.size(); i++){
				float temp = ((float)curVec.get(i) - (float)center.get(i));
				euSq += temp*temp;
			}
			return euSq;
		}
	public int getId(){
		return this.id;
	}
}