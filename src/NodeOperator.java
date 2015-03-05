public abstract class NodeOperator {
	/* Prototype method to be used by PageTree for iterative operations on all nodes.
	 * Returns a boolean indicating whether the tree should continue the iterative
	 * operation.
	 */
	public abstract boolean call(PageNode node, PageTree myTree);
}
