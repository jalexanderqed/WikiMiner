package binaryTree;

import JSONPackages.WikiPage;

public class PageNode implements Comparable<PageNode>{
	public String name;
	public String fileName;
	public boolean beingIndexed;
	public boolean indexed;
	public PageNode left;
	public PageNode right;

	public PageNode(String n){
		indexed = false;
		beingIndexed = false;
		name = n;
	}
	
	public PageNode(String n, String file){
		name = n;
		fileName = file;
		indexed = false;
	}
	
	public int compareTo(PageNode other){
		return name.compareTo(other.name);
	}
}