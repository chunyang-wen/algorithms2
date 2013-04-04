/*************************************************************************
 * Author: William Schwartz
 * Compliation: javac SAP.java
 * Testing: java SAP filename.txt
 * Dependencies: Digraph.java
 *
 * Data type to calculate the shortest ancestral path in a digraph.
 *
 ************************************************************************/

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
	private final CachingBFS.CachedArrays vcache;
	private final CachingBFS.CachedArrays wcache;

	/**
	 * Constructor.
	 *
	 * @param G The <code>Digraph</code> (not necessarily a DAG) for which to
	 *          calculate common ancestors and lengths of SAPs.
	 */
	public SAP(Digraph G) {
		g = new Digraph(G); // Defensive copy.
		vcache = new CachingBFS.CachedArrays(g.V());
		wcache = new CachingBFS.CachedArrays(g.V());
	}

	// Return the min distance to a common node using the v/wcaches. -1 if none
	// found.
	private int min(CachingBFS pv, CachingBFS pw) {
		int min, dist;
		min = -1;
		CachingBFS.CachedArrays[] its = {vcache, wcache};
		for (Iterable<Integer> it : its ) {
			for (int node : it) {
				if (pv.hasPathTo(node) && pw.hasPathTo(node)) {
					dist = pv.distTo(node) + pw.distTo(node);
					if (min < 0 || dist < min)
						min = dist;
				}
			}
		}
		return min;
	}

	// Return the min distance ancestor to a common node using the v/wcaches. -1
	// if none found.
	private int argmin(CachingBFS pv, CachingBFS pw) {
		int min, dist, argmin;
		argmin = -1;
		min = -1;
		CachingBFS.CachedArrays[] its = {vcache, wcache};
		for (Iterable<Integer> it : its ) {
			for (int node : it) {
				if (pv.hasPathTo(node) && pw.hasPathTo(node)) {
					dist = pv.distTo(node) + pw.distTo(node);
					if (min < 0 || dist < min) {
						min = dist;
						argmin = node;
					}
				}
			}
		}
		return argmin;
	}

	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {
		return min(new CachingBFS(g, v, vcache), new CachingBFS(g, w, wcache));
	}

	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(int v, int w) {
		CachingBFS pv = new CachingBFS(g, v, vcache);
		CachingBFS pw = new CachingBFS(g, w, wcache);
		return argmin(pv, pw);
	}

	// length of shortest ancestral path between any vertex in v and any vertex
	// in w; -1 if no such path. Iterables must contain at least one int.
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		return min(new CachingBFS(g, v, vcache), new CachingBFS(g, w, wcache));
	}

	// a common ancestor that participates in shortest ancestral path; -1 if no
	// such path. Iterables must contain at least one int.
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		CachingBFS pv = new CachingBFS(g, v, vcache);
		CachingBFS pw = new CachingBFS(g, w, wcache);
		return argmin(pv, pw);
	}

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
