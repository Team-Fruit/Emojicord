package net.teamfruit.emojicord.asm;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.GuiScreen;
import net.teamfruit.emojicord.compat.KeyboardInputEvent;
import net.teamfruit.emojicord.compat.MouseInputEvent;

public class GuiScreenTest extends GuiScreen {
	/**
	 * Delegates mouse and keyboard input.
	 */
	@Override
	public void handleInput() {
		if (Mouse.isCreated())
			while (Mouse.next())
				if (!MouseInputEvent.Pre.onMouseInput(this))
					handleMouseInput();

		if (Keyboard.isCreated())
			while (Keyboard.next())
				if (!KeyboardInputEvent.Pre.onKeyboardInput(this))
					handleKeyboardInput();
	}

	/**
	 * Delegates mouse and keyboard input.
	 */
	public void handleInputContinue() {
		if (Mouse.isCreated())
			while (Mouse.next()) {
				if (MouseInputEvent.Pre.onMouseInput(this))
					continue;
				handleMouseInput();
			}

		if (Keyboard.isCreated())
			while (Keyboard.next()) {
				if (KeyboardInputEvent.Pre.onKeyboardInput(this))
					continue;
				handleKeyboardInput();
			}
	}

	/**
	 * Delegates mouse and keyboard input.
	 */
	public void handleInputOriginal() {
		if (Mouse.isCreated())
			while (Mouse.next())
				handleMouseInput();

		if (Keyboard.isCreated())
			while (Keyboard.next())
				handleKeyboardInput();
	}

	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() {
	}

	/**
	 * Handles keyboard input.
	 */
	@Override
	public void handleKeyboardInput() {
	}
}
