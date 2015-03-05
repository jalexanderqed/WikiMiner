public class BackIndexer extends NodeOperator {
	int index = 0;
	
	public boolean call(PageNode node, PageTree myTree){
		index++;
		{
			System.out.println("BackIndexer at index " + index);
		}
		MiningFuncs.indicatePagesLinkedFrom(node, myTree);
		return true;
	}
}
