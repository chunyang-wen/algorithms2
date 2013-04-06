/**
 * Content-aware picture resizing.
 *
 * @author William Schwartz
 */
public class SeamCarver {
	private static final double BORDER_ENERGY = 3 * (255 * 255);
	private Picture pic;
	public SeamCarver(Picture picture) { this.pic = picture; }

	// Copy of current picture
	public Picture picture() { return pic; }

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
		if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1)
			return BORDER_ENERGY;
		return gradient(pic.get(x - 1, y), pic.get(x + 1, y)) +
		       gradient(pic.get(x, y - 1), pic.get(x, y + 1));
	}

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam();

	// sequence of indices for vertical seam
	public int[] findVerticalSeam();

	// remove horizontal seam from picture
	public void removeHorizontalSeam(int[] a) {
		if (height() == 0) {
			String msg = "Cannot remove horizontal seam from 0-height picture";
			throw new java.lang.IllegalArgumentException(msg);
		}
		if (a.length != width()) {
			String msg = "Horizontal seam narrower than width of picture";
			throw new java.lang.IllegalArgumentException(msg);
		}
		Picture p = new Picture(width(), height() - 1);
		for (int col = 0; col < width(); col++) {
			for (int row = 0; row < height() - 1; row++) {
				if (row < a[col])
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
			throw new java.lang.IllegalArgumentException(msg);
		}
		if (a.length != height()) {
			String msg = "Vertical seam shorter than height of picture";
			throw new java.lang.IllegalArgumentException(msg);
		}
		Picture p = new Picture(width() - 1, height());
		for (int row = 0; row < height(); row++) {
			for (int col = 0; col < width() - 1; col++) {
				if (col < a[row])
					p.set(col, row, pic.get(col, row));
				else
					p.set(col, row, pic.get(col + 1, row));
			}
		}
		pic = p;
	}
}
