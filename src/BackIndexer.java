public class BackIndexer extends NodeOperator {
	public boolean call(PageNode node, PageTree myTree){
		MiningFuncs.indicatePagesLinkedFrom(node, myTree);
		return true;
	}
}
