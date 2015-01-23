package binaryTree;

public class PageTree {
	private PageNode top;
	private boolean isNewPage;
	private long calls;
	private long runs;

	public PageTree(PageNode add){
		top = add;
	}
	
	public void resetCalls(){
		calls = 0;
		runs = 0;
	}

	public boolean addPage(PageNode toAdd){
		long myCall = calls;
		calls++;
		while(myCall != runs){
			try{
				Thread.sleep(1);
			} catch (InterruptedException e){
				System.out.println("Sleep interrupted: " + e.getMessage());
			}
		}
		isNewPage = true;
		addPage(toAdd, top);
		runs++;
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
		long myCall = calls;
		calls++;
		while(myCall != runs){
			try{
				Thread.sleep(1);
			} catch (InterruptedException e){
				System.out.println("Sleep interrupted: " + e.getMessage());
			}
		}
		PageNode toReturn = getPage(toGet, top);
		runs++;
		return toReturn;
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
		long myCall = calls;
		calls++;
		while(myCall != runs){
			try{
				Thread.sleep(1);
			} catch (InterruptedException e){
				System.out.println("Sleep interrupted: " + e.getMessage());
			}
		}
		boolean contains = contains(toGet, top);
		runs++;
		return contains;
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
		long myCall = calls;
		calls++;
		while(myCall != runs){
			try{
				Thread.sleep(1);
			} catch (InterruptedException e){
				System.out.println("Sleep interrupted: " + e.getMessage());
			}
		}
		PageNode unindexed = getUnindexed(top);
		runs++;
		return unindexed;
	}
	
	private PageNode getUnindexed(PageNode getFrom){
		if(getFrom == null) return null;
		
		if(!getFrom.indexed && !getFrom.beingIndexed) return getFrom;
		
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
