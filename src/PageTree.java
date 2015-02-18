


public class PageTree {
	private PageNode top;
	private boolean isNewPage;
	private long calls;
	private long runs;

	public PageTree(PageNode add){
		top = add;
	}
	
	public PageTree(){
		top = null;
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
		if(top == null){
			top = toAdd;
			return true;
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
	
	public boolean iterateWithCallTo(NodeOperator op, PageNode from){
		if(from == null) return true;
		if(!op.call(from, this)) return false;
		if(!iterateWithCallTo(op, from.left)) return false;
		if(!iterateWithCallTo(op, from.right)) return false;
		return true;
	}
	
	public void iterateWithCallTo(NodeOperator op){
		iterateWithCallTo(op, top);
	}
	
	public PageNode getRandUnindexed(){
		long myCall = calls;
		calls++;
		while(myCall != runs){
			try{
				Thread.sleep(1);
			} catch (InterruptedException e){
				System.out.println("Sleep interrupted: " + e.getMessage());
			}
		}
		PageNode unindexed = getRandUnindexed(top);
		runs++;
		return unindexed;
	}
	
	/* Returns a random unindexed node from the linked list
	 * by calling recursively with a 1/15 chance of returning
	 * the node it was called with each time. Leaf nodes return
	 * themselves automatically.
	 * NOTE: this does mean that at first, leaf nodes will have
	 * a higher probability of being selected.
	 */
	private PageNode getRandUnindexed(PageNode getFrom){
		if(getFrom == null) return null;
		
		if(!getFrom.indexed && !getFrom.beingIndexed){
			if((getFrom.left == null && getFrom.right == null)
					|| (int)(Math.random() * 15) == 0) return getFrom;
		}
		
		int initialSide = (int)(Math.random() * 2);
		
		PageNode result;
		
		if(initialSide == 0){
			if((result = getRandUnindexed(getFrom.left)) == null){
				result = getRandUnindexed(getFrom.right);
			}
		}
		else{
			if((result = getRandUnindexed(getFrom.right)) == null){
				result = getRandUnindexed(getFrom.left);
			}
		}
		
		if(result == null){
			if(!getFrom.indexed && !getFrom.beingIndexed)
				return getFrom;
			else
				return null;
		}
		else
			return result;
	}
	
	private int size(PageNode start){
		if(start == null) return 0;
		return size(start.left) + size(start.right) + 1;
	}
	
	public int size(){
		return size(top);
	}
	
	// Returns 0 for an empty tree and 1 for a tree with one element
	public int depth(){
		return depth(top);
	}
	
	private int depth(PageNode start){
		if(start == null) return 0;
		return Math.max(depth(start.left), depth(start.right)) + 1;
	}
}
