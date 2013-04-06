/**
 * Content-aware picture resizing.
 *
 * @author William Schwartz
 */
public class SeamCarver {
	private Picture picture;

	public SeamCarver(Picture picture) { this.picture = picture; }

	// current picture
	public Picture picture() { return picture; }

	// width of current picture
	public int width() { return picture.width(); }

	// height of current picture
	public int height() { return picture.height(); }

	// energy of pixel at column x and row y
	public double energy(int x, int y);

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
					p.set(col, row, picture.get(col, row));
				else
					p.set(col, row, picture.get(col, row + 1));
			}
		}
		picture = p;
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
					p.set(col, row, picture.get(col, row));
				else
					p.set(col, row, picture.get(col + 1, row));
			}
		}
		picture = p;
	}
}
