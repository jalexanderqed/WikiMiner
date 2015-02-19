import com.google.gson.Gson;

public class MinerThread extends Thread {
	private Thread t;
	private String threadName;
	private static PageTree myTree;
	private static Gson gson = MiningFuncs.getGsonObject();
	public boolean finished = false;
	public long lastCall;

	public void run() {
		long start = System.currentTimeMillis();
		finished = false;
		while(System.currentTimeMillis() - start < MiningFuncs.PROGRAM_TIME_MS){
			try{
				PageNode next = myTree.getRandUnindexed();
				if(next == null){
					System.out.println("getUnindexed() returned null. Mischief managed.");
					System.exit(0);
				}
				lastCall = System.currentTimeMillis();
				MiningFuncs.getPagesLinkedFrom(new WikiPageStore(next), myTree);
			}
			catch(Exception e){
				System.out.println("Error in thread " + threadName);
				System.out.println(e.getMessage());
				MiningFuncs.writeTree(myTree);
				finished = true;
				break;
			}
			MiningFuncs.writeTree(myTree);
		}
		finished = true;
	}

	public void start()
	{
		System.out.println("Starting " +  threadName );
		if(t == null)
		{
			t = new Thread (this, threadName);
			t.start();
		}
		else{
			t.start();
		}
	}

	public MinerThread(String name, PageTree mT) {
		threadName = name;
		myTree = mT;
		System.out.println("Creating " +  threadName );
	}
}
