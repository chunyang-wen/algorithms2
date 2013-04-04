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
	private final WordNet wordnet;

	/**
	 * Constructor takes a WordNet object.
	 *
	 * @param wordnet hypernym-relationship WordNet with semantic information
	 *                about input word.
	 */
	public Outcast(WordNet wordnet) { this.wordnet = wordnet; }

	/**
	 * Return the word from the input array that is least like the others.
	 * <p>
	 * Given an array of WordNet nouns, return an outcast. Assume the argument
	 * array contains at least two valid WordNet nouns, and no non-valid nouns.
	 *
	 * @param nouns An array of strings containing at least two nouns, all of
	 *              which are in the WordNet that was passed to the constructor.
	 * @return The string in the input array whose word is least like the
	 * others.
	 */
	public String outcast(String[] nouns) {
		int[][] distances = new int[nouns.length][nouns.length];
		for (int i = 0; i < nouns.length; i++)
			for (int j = i + 1; j < nouns.length; j++)
				distances[i][j] = wordnet.distance(nouns[i], nouns[j]);
		return nouns[argMaxUpperTriangular(distances)];
	}

	/**
	 * Reflect a upper triangular matrix across its diagonal and return the row
	 * index whose sum is greatest, without modifying the input matrix.
	 * <p>
	 * The non-mutating reflection is accomplished by adding down column
	 * <em>i</em> from 0 to <em>i</em> - 1 and adding across row <em>i</em> from
	 * <em>i</em> to <code>a[i].length</code>.
	 *
	 * @param a An array of arrays of integers in row-major form. The arrays
	 *          should but do not have to be equal lengths.
	 * @return The row index whose sum after reflection is greatest.
	 */
	private int argMaxUpperTriangular(int[][] a) {
		int sum, max, argmax;
		max = 0;
		argmax = 0;
		for (int i = 0; i < a.length; i++) {
			sum = 0;
			for (int j = 0; j < a[i].length; j++) {
				if (j < i)
					sum += a[j][i];
				else
					sum += a[i][j];
			}
			if (i == 0 || sum > max) {
				max = sum;
				argmax = i;
			}
		}
		return argmax;
	}

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
