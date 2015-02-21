
public class CallRunChecker extends NodeOperator {
	public boolean call(PageNode node, PageTree myTree){
		if(node.linkedFrom.getCalls() != node.linkedFrom.getRuns()){
			System.out.println("Node " + node.name + " has calls = " +
					node.linkedFrom.getCalls() + " and runs = " + node.linkedFrom.getRuns());
		}
		return true;
	}
}
