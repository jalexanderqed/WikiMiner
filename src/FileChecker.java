import java.io.File;

public class FileChecker extends NodeOperator {
	public boolean call(PageNode node, PageTree myTree){
		File thisFile = new File(node.fileName);
		if(!thisFile.exists()){
			System.out.println(node.name + " does not exist.");
		}
		return true;
	}
}
