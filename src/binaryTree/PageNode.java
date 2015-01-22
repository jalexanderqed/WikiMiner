package binaryTree;

import JSONPackages.WikiPage;

public class PageNode implements Comparable<PageNode>{
	public String name;
	public String fileName;
	public PageNode left;
	public PageNode right;

	public PageNode(String n){
		name = n;
	}
	
	public PageNode(String n, String file){
		name = n;
		fileName = file;
	}
	
	public int compareTo(PageNode other){
		return name.compareTo(other.name);
	}
}