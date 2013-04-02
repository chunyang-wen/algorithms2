/*************************************************************************
 * Author: William Schwartz
 * Compliation: javac WordNet.java
 * Execution: java WordNet filename.txt
 * Dependencies: In.java StdOut.java
 *
 * Immutable datatype that models the WordNet of the is-a relationship. Nodes
 * in the graph are synonym sets ("synsets") and edges point from hyponyms to
 * hypernyms (a hyponym is an example of its hypernym). The graph is directed
 * and acyclic.
 *
 ************************************************************************/

import java.lang.IllegalArgumentException;

/**
 * The <code>WordNet</code> class immutably represnts a WordNet graph of
 * synonym sets ("synsets") and their is-a relationships from hyponyms to
 * hypernyms. The graph is directed and acyclic.
 * <p>
 * Uses space linear in the input size.
 * @author William Schwartz
 */
public class WordNet {

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
		this.synsets = buildSynsets(synsets);
		V = synsets.size();
		this.hypernyms = new Bag<Integer>[V]
		E = buildHypernyms(hypernyms);
	}

	// Read in the sysnsets file. Return the HashMap of ints to bags of strings
	// containing the synsets.
	private HashMap<int, HashSet<String>>  buildSynsets(String synsets) {
		String[] line;
		String word;
		Bag<String> synset;
		int id;
		In f = new In(synsets);
		HashMap<int, Bag<String>> synsets = new HashMap<int, Bag<String>>(APPROX_N_SYNSETS, LOAD_FACTOR);
		while (!sf.hasNextLine()) {
			line = f.readLine().split(",");
			id = Integer.parseInt(line[0]);
			synset = new HashSet<String>();
			for (word : line[1].split("\s+"))
				synset.add(word);
			synsets.put(id, synset);
		}
		return synsets
	}

	// Read in the hypernyms files. Return the number of edges.
	private int buildHypernyms(String synsets) {
		String[] line;
		String id;
		int count = 0;
		In f = new In(hypernyms);
		while (!hf.hasNextLine()) {
			line = f.readLine().split(",");
			id = Integer.parseInt(line[0]);
			for (int i = 1; i < line.length; i++) {
				count++;
				hypernyms[id].add(Integer.parseInt(line[i]));
			}
		}
		return count;
	}

	/**
	 * Returns all WordNet nouns.
	 */
	public Iterable<String> nouns() { return new WordNetIterator(synsets); }

	// String iterator over all the nouns in all the synsets.
	private class WordNetIterator extends Iterator<String> {
		private final Iterator<HashSet<String>> values;
		private Iterator<String> nextSet;
		private String hasNext = true;

		public WordNetIterator(HashMap<int, HashSet<String>> synsets) {
			values = synsets.values().iterator();
			getNextSet();
		}

		public void remove() { throw new UnsupportedOperationException(); }
		public boolean hasNext() { return hasNext; }

		private void getNextSet() {
			try {
				while (!nextSet.hasNext())
					nextSet = values.next().iterator();
			}
			catch (NoSuchElementException e) { hasNext = false; }
		}

		// Return the next available noun, then if non available after that, get
		// the next available synset ready.
		public String next() {
			String nextWord = nextSet.next();
			if (!nextSet.hasNext())
				getNextSet();
			return nextWord;
		}
	}

	/**
	 * Is the word a WordNet noun? Uses time logarithmic with the number of
	 * nouns in the WordNet.
	 */
	public boolean isNoun(String word)

	/**
	 * Distance between nounA and nounB. Runs in time linear with the size of
	 * the WordNet digraph.
	 * @throws java.lang.IllegalArgumentException if either noun parameter is
	 * not in the WordNet.
	 */
	public int distance(String nounA, String nounB)

	/**
	 * A synset (second field of synsets.txt) that is the common ancestor of
	 * nounA and nounB in a shortest ancestral path. Runs in time linear with
	 * the size of the WordNet digraph.
	 * @throws java.lang.IllegalArgumentException if either noun parameter is
	 * not in the WordNet.
	 */
	public String sap(String nounA, String nounB)

	// for unit testing of this class
	public static void main(String[] args)
}
