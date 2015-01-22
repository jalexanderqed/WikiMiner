package binaryTree;

public class PageTree {
	private PageNode top;
	private boolean isNewPage;

	public PageTree(PageNode add){
		top = add;
	}

	public boolean addPage(PageNode toAdd){
		isNewPage = true;
		addPage(toAdd, top);
		return isNewPage;
	}

	private PageNode addPage(PageNode toAdd, PageNode addFrom){
		if(addFrom == null){
			return toAdd;
		}
		else if(toAdd.compareTo(addFrom) == 0){
			isNewPage = false;
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
	
	public PageNode getPage(PageNode toGet){
		return getPage(toGet, top);
	}
	
	private PageNode getPage(PageNode toGet, PageNode getFrom){
		if(getFrom == null){
			return null;
		}
		else if(toGet.compareTo(getFrom) == 0){
			return getFrom;
		}
		else if(toGet.compareTo(getFrom) < 0){
			return getPage(toGet, getFrom.left);
		}
		else{
			return getPage(toGet, getFrom.right);
		}
	}
	
	public boolean contains(PageNode toGet){
		return contains(toGet, top);
	}
	
	private boolean contains(PageNode toGet, PageNode getFrom){
		if(getFrom == null){
			return false;
		}
		else if(toGet.compareTo(getFrom) == 0){
			return true;
		}
		else if(toGet.compareTo(getFrom) < 0){
			return contains(toGet, getFrom.left);
		}
		else{
			return contains(toGet, getFrom.right);
		}
	}
	
	public PageNode getUnindexed(){
		return getUnindexed(top);
	}
	
	private PageNode getUnindexed(PageNode getFrom){
		if(getFrom == null) return null;
		
		if(!getFrom.indexed) return getFrom;
		
		int initialSide = (int)(Math.random() * 2);
		
		PageNode result;
		if(initialSide == 0){
			if((result = getUnindexed(getFrom.left)) == null){
				result = getUnindexed(getFrom.right);
			}
		}
		else{
			if((result = getUnindexed(getFrom.right)) == null){
				result = getUnindexed(getFrom.left);
			}
		}
		
		return result;
	}
}
