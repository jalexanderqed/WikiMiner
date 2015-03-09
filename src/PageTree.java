public class PageTree {
	private PageNode top;
	private boolean isNewPage;

	public PageTree(PageNode add){
		top = add;
	}

	public PageTree(){
		top = null;
	}

	public synchronized boolean addPage(PageNode toAdd){
		isNewPage = true;
		try{
			top = addPage(toAdd, top);
		}
		catch(Exception e){
			System.out.println("Error in addPage:");
			System.out.println(e.getMessage());
			throw e;
		}
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

	public synchronized PageNode getPage(PageNode toGet){
		try{	
			PageNode toReturn = getPage(toGet, top);
			return toReturn;
		}
		catch(Exception e){
			System.out.println("Error in getPage:");
			System.out.println(e.getMessage());
			throw e;
		}
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

	public synchronized boolean contains(PageNode toGet){
		try{
			boolean contains = contains(toGet, top);
			return contains;
		}
		catch(Exception e){
			System.out.println("Error in contains:");
			System.out.println(e.getMessage());
			throw e;
		}
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
		if(!iterateWithCallTo(op, from.left)) return false;
		if(!iterateWithCallTo(op, from.right)) return false;
		if(!op.call(from, this)) return false;
		return true;
	}

	public synchronized void iterateWithCallTo(NodeOperator op){
		iterateWithCallTo(op, top);
	}

	public synchronized PageNode getRandUnindexed(){
		try{
			PageNode unindexed = getRandUnindexed(top);
			return unindexed;
		}
		catch(Exception e){
			System.out.println("Error in getRandUnindexed:");
			System.out.println(e.getMessage());
			throw e;
		}
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
