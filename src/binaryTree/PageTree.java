package binaryTree;

public class PageTree {
	private PageNode top;
	
	public PageTree(PageNode add){
		top = add;
	}
	
	public PageNode addPage(PageNode toAdd){
		addPage(toAdd, top);
		return toAdd;
	}
	
	private PageNode addPage(PageNode toAdd, PageNode addFrom){
		if(toAdd.compareTo(addFrom.left) < 0){
			
		}
		else if(toAdd.compareTo(addFrom.right) > 0){
			
		}
	}
}
