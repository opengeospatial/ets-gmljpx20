package org.opengis.cite.gmljpx20.box;

/**
 * <p>
 * Abstract Box class.
 * </p>
 */
public abstract class Box {

	protected long length;

	/**
	 * <p>
	 * Constructor for Box.
	 * </p>
	 * @param length a long
	 */
	public Box(long length) {
		this.length = length;
	}

}
