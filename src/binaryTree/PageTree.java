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
		if(addFrom == null){
			return toAdd;
		}
		else if(toAdd.compareTo(addFrom) == 0){
			return addFrom;
		}
		else if(toAdd.compareTo(addFrom) < 0){
			addFrom.left = addPage(toAdd, addFrom.left);
		}
		else{
			addFrom.right = addPage(toAdd, addFrom.right);
		}
		return addFrom;
	}
}
