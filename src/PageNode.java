

import WikiPageClasses.linkObject;
import JSONPackages.WikiPage;

public class PageNode implements Comparable<PageNode>{
	public String name;
	public String fileName;
	public boolean beingIndexed;
	public boolean indexed;
	public linkObject[] links;
	public PageTree linkedFrom;
	public PageNode left;
	public PageNode right;

	public PageNode(String n){
		indexed = false;
		beingIndexed = false;
		name = n;
		linkedFrom = new PageTree();
	}
	
	public PageNode(String n, boolean needLinkedFrom, linkObject[] l){
		indexed = false;
		beingIndexed = false;
		name = n;
		if(needLinkedFrom) linkedFrom = new PageTree();
		else linkedFrom = null;
		links = l;
	}
	
	public PageNode(String n, String file, linkObject[] l){
		name = n;
		fileName = file;
		indexed = false;
		linkedFrom = new PageTree();
		links = l;
	}
	
	public int compareTo(PageNode other){
		return name.compareTo(other.name);
	}
}