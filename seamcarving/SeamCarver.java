/**
 * Content-aware picture resizing.
 *
 * @author William Schwartz
 */
public class SeamCarver {
	private static final double BORDER_ENERGY = 3 * (255 * 255);
	private Picture pic;
	private double[] weights;
	private double[] distTo;
	private int[] edgeTo;

	public SeamCarver(Picture picture) {
		this.pic = new Picture(picture);
		int size = width() * height();
		weights = new double[size];
		distTo = new double[size];
		edgeTo = new int[size];
	}

	// Copy of current picture
	public Picture picture() { return new Picture(pic); }

	// width of current picture
	public int width() { return pic.width(); }

	// height of current picture
	public int height() { return pic.height(); }

	// Find the square color gradient in one dimension
	private int gradient(java.awt.Color a, java.awt.Color b) {
		int red   = a.getRed()   - b.getRed();
		int green = a.getGreen() - b.getGreen();
		int blue  = a.getBlue()  - b.getBlue();
		return red*red + green*green + blue*blue;
	}

	// energy of pixel at column x and row y
	public double energy(int x, int y) {
		if (x < 0 || x >= width() || y < 0 || y >= height()) {
			String msg = Integer.toString(x) + ", " + Integer.toString(y);
			throw new IndexOutOfBoundsException(msg);
		}
		if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1)
			return BORDER_ENERGY;
		return gradient(pic.get(x - 1, y), pic.get(x + 1, y))
		       + gradient(pic.get(x, y - 1), pic.get(x, y + 1));
	}

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam() {
		transpose();
		int[] seam = findVerticalSeam();
		transpose();
		return seam;
	}

	private void transpose() {
		Picture p = new Picture(height(), width());
		for (int col = 0; col < width(); col++)
			for (int row = 0; row < height(); row++)
				p.set(row, col, pic.get(col, row));
		pic = p;
	}

	// Initialize the search vectors. start, stop, and skip give the range of
	// nodes in which the search should begin (i.e., set the distance to zero).
	private void init(int start, int stop, int skip) {
		int width = width(), height = height();
		for (int v = node(0, 0); v < width * height; v++) {
			if (v >= start && v < stop && (v - start) % skip == 0)
				distTo[v] = 0.0;
			else
				distTo[v] = Double.POSITIVE_INFINITY;
			edgeTo[v] = -1;
			weights[v] = energy(col(v), row(v));
		}
	}

	// sequence of indices for vertical seam
	public int[] findVerticalSeam() {
		// The first two nested loops in the nesting here iterate through the
		// cells of the matrix (the nodes of the graph) in topological order.
		// Skip the last row because it has zero outdegree.
		int size = width() * height();
		int row, col;
		init(node(0, 0), node(width - 1, 0), 1);
		for (int startCol = width() - 1; startCol > -height(); startCol--) {
			if (startCol >= 0) { row = 0;         col = startCol; }
			else               { row = -startCol; col = 0;        }
			for ( ; row < height() - 1 && col < width(); row++) {
				int v = node(col, row);
				if (col > 0)
					relax(v, node(col - 1, row + 1));
				relax(v, node(col, row + 1));
				if (col(from) < width - 1)
					relax(v, node(col + 1, row + 1));
			}
		}
		int endOfSeam = argmin(distTo, node(0, height() - 1), size);
		return path(endOfSeam, edgeTo);
	}

	private int[] path(int end, int[] edgeTo) {
		int[] seam = new int[height()];
		seam[row(end)] = col(end);
		for (int prev = edgeTo[end]; prev >= 0; prev = edgeTo[prev])
			seam[row(prev)] = col(prev);
		return seam;
	}

	// Return the index of the least element of an array of doubles in a range.
	private int argmin(double[] a, int start, int stop, int skip) {
		if (stop <= start || start < 0 || a.length == 0)
			throw new IllegalArgumentException();
		double min = Double.POSITIVE_INFINITY;
		int argmin = start;
		if (stop > a.length)
			stop = a.length;
		for (int i = start; i < stop; i += skip) {
			if (a[i] < min) {
				min = a[i];
				argmin = i;
			}
		}
		return argmin;
	}

	private void relax(int from, int to) {
		if (distTo[to] > distTo[from] + weights[to]) {
			distTo[to] = distTo[from] + weights[to];
			edgeTo[to] = from;
		}
	}

	// Mapping between node ID numbers and (col, row) notation. No bounds
	// checking is performed so use with caution.
	private int node(int col, int row) { return row * width() + col; }
	private int col(int node) { return node % width(); }
	private int row(int node) { return node / width(); }

	// remove horizontal seam from picture
	public void removeHorizontalSeam(int[] a) {
		if (height() == 0) {
			String msg = "Cannot remove horizontal seam from 0-height picture";
			throw new IllegalArgumentException(msg);
		}
		if (a.length != width()) {
			String msg = "Horizontal seam narrower than width of picture";
			throw new IllegalArgumentException(msg);
		}
		Picture p = new Picture(width(), height() - 1);
		int lastrow = a[0];
		for (int col = 0; col < width(); col++) {
			if (a[col] < lastrow - 1 || a[col] > lastrow + 1)
				throw new IllegalArgumentException("Non-valid seam");
			if (a[col] < 0 || a[col] >= height())
				throw new IndexOutOfBoundsException(Integer.toString(a[col]));
			lastrow = a[col];
			for (int row = 0; row < height() - 1; row++) {
				if (row < lastrow)
					p.set(col, row, pic.get(col, row));
				else
					p.set(col, row, pic.get(col, row + 1));
			}
		}
		pic = p;
	}


	// remove vertical seam from picture
	public void removeVerticalSeam(int[] a) {
		if (width() == 0) {
			String msg = "Cannot remove vertical seam from zero-width picture";
			throw new IllegalArgumentException(msg);
		}
		if (a.length != height()) {
			String msg = "Vertical seam shorter than height of picture";
			throw new IllegalArgumentException(msg);
		}
		Picture p = new Picture(width() - 1, height());
		int lastcol = a[0];
		for (int row = 0; row < height(); row++) {
			if (a[row] < lastcol - 1 || a[row] > lastcol + 1)
				throw new IllegalArgumentException("Non-valid seam");
			if (a[row] < 0 || a[row] >= width())
				throw new IndexOutOfBoundsException(Integer.toString(a[row]));
			lastcol = a[row];
			for (int col = 0; col < width() - 1; col++) {
				if (col < lastcol)
					p.set(col, row, pic.get(col, row));
				else
					p.set(col, row, pic.get(col + 1, row));
			}
		}
		pic = p;
	}

	// Make this private to avoid changing the API defined in the
	// instructions. If you need to test the class, just change private to
	// public.
	private static void main(String[] args) {
		Picture p = new Picture(args[0]);
		System.out.printf("image is %d columns by %d rows\n", p.width(), p.height());
		SeamCarver s = new SeamCarver(p);
		s.printNode2Point();
		s.printEdges();
	}

	private void printTopo() {
		int count = 0;
		int[][] m = new int[width()][height()];
		for (int node : toporder())
			m[col(node)][row(node)] = count++;
		System.out.println("*************** TOPOLOGICAL ORDER *************");
		for (int row = 0; row < height(); row++) {
			for (int col = 0; col < width(); col++)
				System.out.printf(" %3d ", m[col][row]);
			System.out.println();
		}
		System.out.println();
	}

	private void printNode2Point() {
		for (int node = node(0, 0); node < width() * height(); node++) {
			System.out.printf("(%d, %d) ", col(node), row(node));
			if (col(node) == width() - 1)
				System.out.println();
		}
		System.out.println();
	}

	private void printEdges() {
		System.out.println("*************** EDGES ****************");
		System.out.printf("   |      %d            ", 0);
		for (int col = 1; col < width() - 1; col++)
			System.out.printf("|        %d              ", col);
		System.out.printf("|       %d           |\n", width() - 1);
		for (int node = node(0, 0); node < width() * height(); node++) {
			if (col(node) == 0)
				System.out.printf("%3d| ", row(node));
			System.out.printf("%3d -> {", node);
			for (int child : adj(node))
				System.out.printf("%3d,", child);
			System.out.print("} | ");
			if (col(node) == width() - 1)
				System.out.println();
		}
		System.out.println();
	}
}
