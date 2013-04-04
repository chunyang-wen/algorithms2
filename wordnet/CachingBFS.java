/*************************************************************************
 * Compilation:  javac CachingBFS.java
 * Execution:    java CachingBFS V E
 * Dependencies: Digraph.java Queue.java Stack.java
 *
 * Run breadth first search on a digraph. Runs in O(E + V) time. Supports
 * caching of its main data strucutres to avoid reallocation when running BFS on
 * the same graph multiple times.
 *
 * Based on BreadthFirstDirectedPaths by Kevin Wayne and Robert Sedgwick of
 * Princeton University. The main BFS algorithms are theirs. The idea for the
 * cache comes from their assignment instructions, but I built it.
 *************************************************************************/

class CachingBFS {
	private static final int INFINITY = Integer.MAX_VALUE;
	private boolean[] marked;  // marked[v] = is there an s->v path?
	private int[] edgeTo;      // edgeTo[v] = last edge on shortest s->v path
	private int[] distTo;      // distTo[v] = length of shortest s->v path
	private final CachedArrays cachedArrays;

	public static class CachedArrays {
		private final boolean[] marked;
		private final int[] distTo;
		private final int[] edgeTo;
		private final Queue<Integer> changed;

		public CachedArrays(int size) {
			marked = new boolean[size];
			distTo = new int[size];
			edgeTo = new int[size];
			for (int v = 0; v < size; v++)
				distTo[v] = INFINITY;
			changed = new Queue<Integer>();
		}

		// Clear this cache entry for reuse.
		public void clear() {
			int i;
			while (!changed.isEmpty()) {
				i = changed.dequeue();
				marked[i] = false;
				distTo[i] = INFINITY;
				edgeTo[i] = 0;
			}
		}

		// For testing that this cache is an appropriate size.
		public int size() { return marked.length; }

		// Mark that an index in the arrays has changed so it can be cleared
		// later for reuse.
		public void markChanged(int index) { changed.enqueue(index); }

		// Accessor methods for the cached arrays.
		public boolean[] marked() { return marked; }
		public int[] distTo() { return distTo; }
		public int[] edgeTo() { return edgeTo; }
	}


	// single source
	public CachingBFS(Digraph G, int s, CachedArrays c) {
		cachedArrays = instantiate(c, G.V());
		bfs(G, s);
	}

	// multiple sources
	public CachingBFS(Digraph G, Iterable<Integer> sources, CachedArrays c) {
		cachedArrays = instantiate(c, G.V());
		bfs(G, sources);
	}

	private CachedArrays instantiate(CachedArrays c, int size) {
		CachedArrays cc;
		if (c == null)
			cc = new CachedArrays(size);
		else {
			assert c.size() == size;
			c.clear();
			cc = c;
		}
		marked = cc.marked();
		distTo = cc.distTo();
		edgeTo = cc.edgeTo();
		return cc;
	}

	// BFS from single source
	private void bfs(Digraph G, int s) {
		Queue<Integer> q = new Queue<Integer>();
		marked[s] = true;
		distTo[s] = 0;
		cachedArrays.markChanged(s);
		q.enqueue(s);
		while (!q.isEmpty()) {
			int v = q.dequeue();
			for (int w : G.adj(v)) {
				if (!marked[w]) {
					edgeTo[w] = v;
					distTo[w] = distTo[v] + 1;
					marked[w] = true;
					cachedArrays.markChanged(w);
					q.enqueue(w);
				}
			}
		}
	}

	// BFS from multiple sources
	private void bfs(Digraph G, Iterable<Integer> sources) {
		Queue<Integer> q = new Queue<Integer>();
		for (int s : sources) {
			marked[s] = true;
			distTo[s] = 0;
			q.enqueue(s);
			cachedArrays.markChanged(s);
		}
		while (!q.isEmpty()) {
			int v = q.dequeue();
			for (int w : G.adj(v)) {
				if (!marked[w]) {
					edgeTo[w] = v;
					distTo[w] = distTo[v] + 1;
					marked[w] = true;
					cachedArrays.markChanged(w);
					q.enqueue(w);
				}
			}
		}
	}

	// length of shortest path from s (or sources) to v
	public int distTo(int v) { return distTo[v]; }

	// is there a directed path from s (or sources) to v?
	public boolean hasPathTo(int v) { return marked[v]; }
}
