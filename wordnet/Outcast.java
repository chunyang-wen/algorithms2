/*************************************************************************
 * Author: William Schwartz
 * Compliation: javac Outcast.java
 * Testing: java Outcast synsets.csv hypernyms.csv outcasts1.txt outcasts2.txt ...
 * Dependencies: Digraph.java
 *
 * Detect an unrelated word in a set of nouns.
 *
 ************************************************************************/

/**
 * Detect an unrelated word in a set of nouns. Data type is immutable.
 * <p>
 * Given the definition of <em>distance</em> in <code>WordNet</code>, an
 * <em>outcast</em> is a word whose sum of distances to all other words in the
 * given set is the greatest.
 *
 * @author William Schwartz
 */
public class Outcast {

	/**
	 * Constructor takes a WordNet object.
	 *
	 * @param wordnet hypernym-relationship WordNet with semantic information
	 *                about input word.
	 */
	public Outcast(WordNet wordnet) { wn = wordnet; }

	// given an array of WordNet nouns, return an outcast.
	// Assume the argument array contains at least two valid WordNet nouns, and
	// no non-valid nouns.
	public String outcast(String[] nouns);

	/**
	 * The following test client takes from the command line the name of a
	 * synset file, the name of a hypernym file, followed by the names of
	 * outcast files, and prints out an outcast in each file.
	 *
	 * @author Alina Ene
	 * @author Kevin Wayne
	 */
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            String[] nouns = In.readStrings(args[t]);
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }

}
