/*************************************************************************
 * Author: William Schwartz
 * Compliation: javac WordNet.java
 * Execution: java WordNet synsets.csv hypernyms.csv
 * Dependencies: In.java StdOut.java
 *
 * Immutable datatype that models the WordNet of the is-a relationship. Nodes
 * in the graph are synonym sets ("synsets") and edges point from hyponyms to
 * hypernyms (a hyponym is an example of its hypernym). The graph is directed
 * and acyclic.
 *
 ************************************************************************/

import java.util.HashMap;

/**
 * The <code>WordNet</code> class immutably represnts a WordNet graph of
 * synonym sets ("synsets") and their is-a relationships from hyponyms to
 * hypernyms. The graph is directed and acyclic.
 * <p>
 * Uses space linear in the input size.
 * @author William Schwartz
 */
public class WordNet {
	private final SAP paths;
	private final HashMap<Integer, String> id2synset;
	private final HashMap<String, Bag<Integer>> noun2ids;

	/** Create a WordNet from a synsets and a hypernyms CSV file. Uses time
	 * linearithmic with the input size.
	 * <p>
	 * The synsets CSV file has columns <ol><li>the synset id integer, <li>the
	 * space-separated list of synonyms (inside a synonym, a space is replaced
	 * by an underscore), and <li>a definition, or gloss.</ol>
	 * <p>
	 * The hypernyms CSV file's first column is a synset ID followed by a
	 * comma-separated list of synset IDs that are hypernyms for the ID in the
	 * first column.
	 *
	 * @param synsets   the name of the CSV file containing the synsets
	 * @param hypernyms the name of the CSV file containing the hypernyms
	 * @throws java.lang.IllegalArgumentException if files do not represent a
	 * rooted DAG.
	 */
	public WordNet(String synsets, String hypernyms) {
		id2synset = new HashMap<Integer, String>();
		noun2ids = new HashMap<String, Bag<Integer>>();
		buildSynsets(synsets);
		paths = new SAP(buildHypernyms(hypernyms, id2synset.size()));
	}

	// Read synsets files. Prepare mappings among synsets, ids, and words.
	private void buildSynsets(String synsets) {
		In file = new In(synsets);
		String[] line;
		int id;
		Bag<Integer> bag;
		while (!file.isEmpty()) {
			line = file.readLine().split(",");
			id = Integer.parseInt(line[0]);
			id2synset.put(id, line[1]);
			for (String noun : line[1].split(" ")) {
				bag = noun2ids.get(noun);
				if (bag == null) {
					bag = new Bag<Integer>();
					bag.add(id);
					noun2ids.put(noun, bag);
				}
				else {
					bag.add(id);
				}
			}
		}
	}

	// Read hypernyms digraph
	private Digraph buildHypernyms(String hypernyms, int numSynsets) {
		Digraph g = new Digraph(numSynsets);
		In file = new In(hypernyms);
		String[] line;
		int id;
		while (!file.isEmpty()) {
			line = file.readLine().split(",");
			id = Integer.parseInt(line[0]);
			for (int i = 1; i < line.length; i++)
				g.addEdge(id, Integer.parseInt(line[i]));
		}
		detectCycles(g, hypernyms);
		detectRootedness(g, hypernyms);
		return g;
	}

	private void detectCycles(Digraph g, String hypernyms) {
		DirectedCycle dc = new DirectedCycle(g);
		if (dc.hasCycle()) {
			String msg = hypernyms + " does not represent a DAG";
			throw new IllegalArgumentException(msg);
		}
	}

	// Throw error if input does not have exactly one root.
	private void detectRootedness(Digraph g, String hypernyms) {
		// A root is a node with outdegree zero. A rooted DAG has exactly one
		// such node.
		int numRoots = 0;
		for (int vertex = 0; vertex < g.V(); vertex++)
			if (g.adj(vertex).size() == 0)
				numRoots++;
		if (numRoots != 1)
			throw new IllegalArgumentException(hypernyms + " is not rooted.");
	}

	/**
	 * Returns all WordNet nouns.
	 */
	public Iterable<String> nouns() { return noun2ids.keySet(); }

	/**
	 * Is the word a WordNet noun? Uses constant time.
	 */
	public boolean isNoun(String word) { return noun2ids.containsKey(word); }

	// Convenience private method to throw IllegalArgumentException for
	// distance() and sap().
	private void areBothNouns(String nounA, String nounB) {
		if (!isNoun(nounA) || !isNoun(nounB)) {
			String msg = "One is not in the WordNet: " + nounA + " " + nounB;
			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * Distance between nounA and nounB. Runs in time linear with the size of
	 * the WordNet digraph.
	 * @throws java.lang.IllegalArgumentException if either noun parameter is
	 * not in the WordNet.
	 */
	public int distance(String nounA, String nounB) {
		areBothNouns(nounA, nounB);
		return paths.length(noun2ids.get(nounA), noun2ids.get(nounB));
	}

	/**
	 * A synset (second field of synsets.txt) that is the common ancestor of
	 * nounA and nounB in a shortest ancestral path. Runs in time linear with
	 * the size of the WordNet digraph.
	 * @throws java.lang.IllegalArgumentException if either noun parameter is
	 * not in the WordNet.
	 */
	public String sap(String nounA, String nounB) {
		areBothNouns(nounA, nounB);
		return id2synset.get(paths.ancestor(noun2ids.get(nounA),
											noun2ids.get(nounB)));
	}

	// for unit testing of this class. Usage:
	// java WordNet synsets.csv hypernyms.csv
	public static void main(String[] args) {
		WordNet wn = new WordNet(args[0], args[1]);
		while (!StdIn.isEmpty()) {
			StdOut.printf("Name two nouns to query their relatedness: ");
			String v = StdIn.readString();
			String w = StdIn.readString();
			if (!wn.isNoun(v)) {
				StdOut.printf(v + " not in the word net");
				continue;
			}
			if (!wn.isNoun(w)) {
				StdOut.printf(w + " not in the word net");
				continue;
			}
			int distance = wn.distance(v, w);
			String ancestor = wn.sap(v, w);
			StdOut.printf("distance = %d, ancestor = %d\n", distance, ancestor);
		}
	}
}
