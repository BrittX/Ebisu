public class gesture{
	ArrayList<dataObject> nodes;
	public gesture(dataObject head){
		nodes = new ArrayList<dataObject>();
		nodes.add(head);
	}
	public void add(dataObject cur){
		nodes.add(cur);
	}
	public int comp(ArrayList<dataObject> data, int order){
		for (dp : data){
			if (!nodes.contains(dp))
				return -1; //indicates the two gestures are deffinitely different.
			if (nodes(order) == data(order)){
				if (order == nodes.size - 1)
					return 1; //indicates gestures match
				else
					return 0; //maybe, come back with order++
			}
			else
				return -1
		}
	}
}