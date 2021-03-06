

import JSONPackages.WikiPage;

public class PageNode implements Comparable<PageNode>{
	public String name;
	public String fileName;
	public boolean beingIndexed;
	public boolean indexed;
	public PageTree linkedFrom;
	public PageNode left;
	public PageNode right;

	public PageNode(String n){
		indexed = false;
		beingIndexed = false;
		name = n;
		linkedFrom = new PageTree();
	}
	
	public PageNode(String n, boolean needLinkedFrom){
		indexed = false;
		beingIndexed = false;
		name = n;
		if(needLinkedFrom) linkedFrom = new PageTree();
		else linkedFrom = null;
	}
	
	public PageNode(String n, String file){
		name = n;
		fileName = file;
		indexed = false;
		linkedFrom = new PageTree();
	}
	
	public int compareTo(PageNode other){
		return name.compareTo(other.name);
	}
}