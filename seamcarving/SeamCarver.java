/**
 * Content-aware picture resizing.
 *
 * @author William Schwartz
 */
public class SeamCarver {
	public SeamCarver(Picture picture);

	// current picture
	public Picture picture();

	// width of current picture
	public int width();

	// height of current picture
	public int height();

	// energy of pixel at column x and row y
	public double energy(int x, int y);

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam();

	// sequence of indices for vertical seam
	public int[] findVerticalSeam();

	// remove horizontal seam from picture
	public void removeHorizontalSeam(int[] a);

	// remove vertical seam from picture
	public void removeVerticalSeam(int[] a);
}
