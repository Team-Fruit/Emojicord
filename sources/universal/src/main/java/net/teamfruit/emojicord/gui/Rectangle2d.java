package net.teamfruit.emojicord.gui;

public class Rectangle2d {
	private int x;
	private int y;
	private int width;
	private int height;

	public Rectangle2d(final int xIn, final int yIn, final int widthIn, final int heightIn) {
		this.x = xIn;
		this.y = yIn;
		this.width = widthIn;
		this.height = heightIn;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public boolean contains(final int x, final int y) {
		return x>=this.x&&x<=this.x+this.width&&y>=this.y&&y<=this.y+this.height;
	}
}