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
	public WordNet(String synsets, String hypernyms)

	/**
	 * Returns all WordNet nouns.
	 */
	public Iterable<String> nouns()

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
