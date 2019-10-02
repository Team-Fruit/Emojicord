package net.teamfruit.emojicord.gui;

import net.teamfruit.emojicord.compat.Compat.CompatChatScreen;
import net.teamfruit.emojicord.compat.Compat.CompatFontRenderer;
import net.teamfruit.emojicord.compat.Compat.CompatMinecraft;
import net.teamfruit.emojicord.compat.Compat.CompatScreen;
import net.teamfruit.emojicord.compat.Compat.CompatTextFieldWidget;

public class EmojiSelectionChat implements IChatOverlay {
	public final CompatScreen screen;
	public final CompatChatScreen chatScreen;
	public final CompatTextFieldWidget inputField;
	public final CompatFontRenderer font;
	public int mouseX, mouseY;

	public EmojiSelectionChat(final CompatChatScreen chatScreen) {
		this.screen = chatScreen.cast();
		this.chatScreen = chatScreen;
		this.font = CompatMinecraft.getMinecraft().getFontRenderer();
		this.inputField = chatScreen.getTextField();
	}

	@Override
	public boolean onDraw() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onMouseClicked(final int button) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onMouseScroll(final double scrollDelta) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onMouseInput(final int mouseX, final int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		return false;
	}

	@Override
	public boolean onKeyPressed(final int keycode) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}
