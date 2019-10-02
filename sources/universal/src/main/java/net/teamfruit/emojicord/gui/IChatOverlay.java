package net.teamfruit.emojicord.gui;

public interface IChatOverlay {
	boolean onDraw();

	boolean onMouseClicked(final int button);

	boolean onMouseScroll(final double scrollDelta);

	boolean onMouseInput(final int mouseX, final int mouseY);

	boolean onKeyPressed(final int keycode);
}
