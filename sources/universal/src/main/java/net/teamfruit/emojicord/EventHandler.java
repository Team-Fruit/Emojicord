package net.teamfruit.emojicord;

import net.teamfruit.emojicord.compat.Compat.CompatChatScreen;
import net.teamfruit.emojicord.compat.CompatEvents.CompatClientChatEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatConfigChangedEvent.CompatOnConfigChangedEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatGuiScreenEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatHandler;
import net.teamfruit.emojicord.compat.CompatEvents.CompatRenderGameOverlayEvent;
import net.teamfruit.emojicord.emoji.EmojiText;
import net.teamfruit.emojicord.gui.SuggestionChat;

public class EventHandler extends CompatHandler {
	private SuggestionChat chat;

	@Override
	public void onChat(final CompatClientChatEvent event) {
		if (!event.getMessage().startsWith("/")) {
			final EmojiText emojiText = EmojiText.createEncoded(event.getMessage());
			event.setMessage(emojiText.getEncoded());
		}
	}

	@Override
	public void onDraw(final CompatRenderGameOverlayEvent.CompatPost event) {
	}

	@Override
	public void onText(final CompatRenderGameOverlayEvent.CompatText event) {
	}

	@Override
	public void onConfigChanged(final CompatOnConfigChangedEvent event) {
	}

	@Override
	public void onInitGui(final CompatGuiScreenEvent.CompatInitGuiEvent.CompatPost event) {
		final CompatChatScreen chatScreen = CompatChatScreen.cast(event.getGui());
		if (chatScreen!=null)
			this.chat = new SuggestionChat(chatScreen);
		else
			this.chat = null;
	}

	@Override
	public void onDraw(final CompatGuiScreenEvent.CompatDrawScreenEvent.CompatPost event) {
		if (this.chat!=null&&this.chat.onDraw())
			event.setCanceled(true);
	}

	@Override
	public void onMouseClicked(final CompatGuiScreenEvent.CompatMouseClickedEvent.CompatPre event) {
		if (this.chat!=null&&this.chat.onMouseClicked(event.getButton()))
			event.setCanceled(true);
	}

	@Override
	public void onMouseScroll(final CompatGuiScreenEvent.CompatMouseScrollEvent.CompatPre event) {
		if (this.chat!=null&&this.chat.onMouseScroll(event.getScrollDelta()))
			event.setCanceled(true);
	}

	@Override
	public void onMouseInput(final CompatGuiScreenEvent.CompatMouseInputEvent event) {
		if (this.chat!=null&&this.chat.onMouseInput(event.getMouseX(), event.getMouseY()))
			event.setCanceled(true);
	}

	@Override
	public void onKeyPressed(final CompatGuiScreenEvent.CompatKeyboardKeyPressedEvent.CompatPre event) {
		if (this.chat!=null&&this.chat.onKeyPressed(event.getKeyCode()))
			event.setCanceled(true);
	}
}
