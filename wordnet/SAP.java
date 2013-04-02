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
import java.util.HashSet;

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
	// Last-answer cache. Will be modified, but only allocate once.
	private final Query last;
	private final int[] vdist;
	private final int[] wdist;
	private final HashSet<Integer> vChanges;
	private final HashSet<Integer> wChanges;

	/**
	 * Constructor.
	 *
	 * @param G The <code>Digraph</code> (not necessarily a DAG) for which to
	 *          calculate common ancestors and lengths of SAPs.
	 */
	public SAP(Digraph G) {
		g = new Digraph(G); // Defensive copy.
		last = new Query(-1, -1, -1, -1);
		vdist = new int[g.V()];
		wdist = new int[g.V()];
		for (int i = 0; i < g.V(); i++)
			vdist[i] = wdist[i] = -1;
		vChanges = new HashSet<Integer>();
		wChanges = new HashSet<Integer>();
	}

	/**
	 * Run breadth-first search on the graph to find shortest path to each node
	 * from <code>from</code>. Update the vector of distances in place.
	 * <p>
	 * The <code>distances</code> vector will be filled with the minimum of the
	 * vector's current value and the distance from <code>from</code> to the
	 * node indexed in the vector. This way you can run <code>minDistTo</code>
	 * on the same distances vector with different <code>from</code> nodes.
	 *
	 * @param distances node-indexed vector of distances from prior given
	 *                  <code>from</code> nodes and the indexed node. The first
	 *                  call should pass this vector in initialized with -1.
	 * @param from      id of node from which to count distances (0 to self)
	 * @param changes   <code>HashSet</code> of changed indicies (for a cache)
	 */
	private void minDistTo(int[] distances, int from, HashSet<Integer> changes) {
		assert distances.length == g.V();
		Queue<Integer> q = new Queue<Integer>();
		q.enqueue(from);
		boolean[] marked = new boolean[g.V()];
		int count = 0;
		for (int source = q.dequeue(); !q.isEmpty(); source = q.dequeue()) {
			if (count < distances[source] || distances[source] <= 0 ) {
				distances[source] = count;
				changes.add(source);
			}
			count++;
			for (int target : g.adj(source)) {
				if (!marked[target]) {
					q.enqueue(target);
					marked[target] = true;
				}
			}
		}
	}

	private class Query {
		public final HashSet<Integer> v;
		public final HashSet<Integer> w;
		public int length;
		public int ancestor;

		public Query(int v, int w, int len, int anc) {
			this.v = new HashSet<Integer>();
			this.w = new HashSet<Integer>();
			update(v, w, len, anc);
		}

		public Query(Iterable<Integer> v, Iterable<Integer> w, int len, int anc) {
			this.v = new HashSet<Integer>();
			this.w = new HashSet<Integer>();
			update(v, w, len, anc);
		}

		public void update(int v, int w, int len, int anc) {
			this.v.clear();
			this.w.clear();
			this.v.add(v);
			this.w.add(w);
			length = len;
			ancestor = anc;
		}

		public void update(Iterable<Integer> v, Iterable<Integer> w, int len, int anc) {
			this.v.clear();
			for (int i : v)
				this.v.add(i);
			this.w.clear();
			for (int i : w)
				this.w.add(i);
			length = len;
			ancestor = ancestor;
		}

		// Return whether this query matches the (v, w) query
		public boolean matches(int v, int w) {
			return this.v.size() == 1 && this.w.size() == 1 && (this.v.contains(v) && this.w.contains(w) || this.w.contains(v) && this.v.contains(w));
		}

		public boolean matches(Iterable<Integer> v, Iterable<Integer> w) {
			HashSet<Integer> vSet = new HashSet<Integer>();
			for (int i: v)
				vSet.add(i);
			HashSet<Integer> wSet = new HashSet<Integer>();
			for (int i: w)
				wSet.add(i);
			return this.v.equals(vSet) && this.w.equals(wSet) || this.v.equals(wSet) && this.v.equals(vSet);
		}
	}

	private void solve(int v, int w) {
		if (v == w) {
			last.update(v, w, 0, w);
			return;
		}
		int[] distances = vdist;
		for (int i : vChanges)
			distances[i] = -1;
		vChanges.clear();
		minDistTo(distances, v, vChanges);
		Queue<Integer> q = new Queue<Integer>();
		q.enqueue(w);
		boolean[] marked = new boolean[g.V()];
		int count = 0;
		for (int ancestor = q.dequeue(); !q.isEmpty(); ancestor = q.dequeue()) {
			if (distances[ancestor] > -1) {
				last.update(v, w, distances[ancestor] + count, ancestor);
				return;
			}
			count++;
			for (int target : g.adj(ancestor)) {
				if (!marked[target]) {
					q.enqueue(target);
					marked[target] = true;
				}
			}
		}
		last.update(v, w, -1, -1);
	}

	private void solve(Iterable<Integer> v, Iterable<Integer> w) {
		for (int i : vChanges)
			vdist[i] = -1;
		vChanges.clear();
		for (int i : wChanges)
			wdist[i] = -1;
		wChanges.clear();
		HashSet<Integer> vSet = new HashSet<Integer>();
		for (int from : v) {
			vSet.add(from);
			minDistTo(vdist, from, vChanges);
		}
		HashSet<Integer> wSet = new HashSet<Integer>();
		for (int from : w) {
			wSet.add(from);
			minDistTo(wdist, from, wChanges);
		}
		int minAncestor = -1;
		int minDist = -1;
		for (int i = 0; i < g.V(); i++) {
			if (vdist[i] > -1 && wdist[i] > -1 && minDist == -1 || minDist > vdist[i] + wdist[i]) {
					minDist = vdist[i] + wdist[i];
					minAncestor = i;
			}
		}
		last.update(vSet, wSet, minDist, minAncestor);
	}


	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {
		if (!last.matches(v, w))
			solve(v, w);
		return last.length;
	}

	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(int v, int w) {
		if (!last.matches(v, w))
			solve(v, w);
		return last.ancestor;
	}

	// length of shortest ancestral path between any vertex in v and any vertex
	// in w; -1 if no such path. Iterables must contain at least one int.
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		if (!last.matches(v, w))
			solve(v, w);
		return last.length;
	}

	// a common ancestor that participates in shortest ancestral path; -1 if no
	// such path. Iterables must contain at least one int.
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		if (!last.matches(v, w))
			solve(v, w);
		return last.ancestor;
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
