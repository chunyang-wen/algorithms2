/*************************************************************************
 * Author: William Schwartz
 * Compliation: javac SAP.java
 * Testing: java SAP filename.txt
 * Dependencies: Digraph.java
 *
 * Data type to calculate the shortest ancestral path in a digraph.
 *
 ************************************************************************/

import java.lang.IndexOutOfBoundsException;

/**
 * Instances of <code>SAP</code> calculate shortest ancestral paths between two
 * verticies or two sets of verticies in a given digraph.
 * <p>
 * All methods that take integers or iterables of integers as verticies throw a
 * <code>java.lang.IndexOutOfBoundsException</code> if any of those inputs is
 * not a vertex in the digraph with which the SAP was constructed.
 * <p>
 * All methods (including the constructor) take time proportional to the sum of
 * the number of edges and verticies of the digraph with which the SAP was
 * constructed. Instances likewise take space linear in that sum.
 *
 * @author William Schwartz
 */
public class SAP {
	private final Digraph g;

	/**
	 * Constructor.
	 *
	 * @param G The <code>Digraph</code> (not necessarily a DAG) for which to
	 *          calculate common ancestors and lengths of SAPs.
	 */
	public SAP(Digraph G) {
		g = Digraph(G); // Defensive copy.
	}

	/**
	 * Run breadth-first search on the graph to find shortest path to each node
	 * from <code>from</code> and run an operation indicated by
	 * <code>sum</code>.
	 * <p>
	 * If <code>sum</code> is <code>false</code>, then the
	 * <code>distances</code> vector will be filled with the minimum of the
	 * vector's current value and the distance from <code>from</code> to the
	 * node indexed in the vector. If <code>sum</code> is <code>true</code> then
	 * fill the vector with the sum of its current value and the distance
	 * between <code>from</code> and the node indexed in the vector.
	 *
	 * @param distances node-indexed vector of distances from prior given
	 *                  <code>from</code> nodes and the indexed node.
	 * @param from      id of node from which to count distances (0 to self)
	 * @param sum       false if minimze, true if sum
	 * @return if <sum>code</code> is false, return [-1, -1] if no ancestral path or
	 *         a two-tuple of length and first ancestor. Else return the update
	 *         distances vector.
	 */
	private int[] fDistTo(int[] distances, int from, boolean sum) {
		if (distances == null)
			distances = new int[g.V()];
		assert distances.length == g.V();
		Queue<Integer> q = new Queue<Integer>();
		q.enqueue(from);
		int[] marked = new int[g.V()];
		int count = 0;
		for (int source = q.dequeue; !q.isEmpty(); source = q.dequeue;) {
			for (int target : g.adj(source)) {
				if (!marked[target]) {
					q.enqueue(target);
					marked[target] = true;
				}
				if (sum) {
					if (count < distances[target])
						distances[target] = count;
				}
				else {
					distances[target] += count;
					// if below is true, this is the first intersection. Return
					// a two-item array in length, ancestor order.
					if (distance[target] > count || target == from) {
						return [distance[target], target];
					}
				}
			}
			count++;
		}
		if (sum)
			return distances;
		else
			return [-1, -1];
	}

	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w);

	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(int v, int w);

	// length of shortest ancestral path between any vertex in v and any vertex
	// in w; -1 if no such path. Iterables must contain at least one int.
	public int length(Iterable<Integer> v, Iterable<Integer> w);

	// a common ancestor that participates in shortest ancestral path; -1 if no
	// such path. Iterables must contain at least one int.
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w);

	/**
	 * This test client takes the name of a digraph input file as as a
	 * command-line argument, constructs the digraph, reads in vertex pairs from
	 * standard input, and prints out the length of the shortest ancestral path
	 * between the two vertices and a common ancestor that participates in that
	 * path
	 *
	 * @author Alina Ene
	 * @author Kevin Wayne
	 */
	public static void main(String[] args) {
		In in = new In(args[0]);
		Digraph G = new Digraph(in);
		SAP sap = new SAP(G);
		while (!StdIn.isEmpty()) {
			int v = StdIn.readInt();
			int w = StdIn.readInt();
			int length   = sap.length(v, w);
			int ancestor = sap.ancestor(v, w);
			StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
		}
	}
}
