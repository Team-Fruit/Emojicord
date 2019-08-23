package net.teamfruit.emojicord;

import net.teamfruit.emojicord.compat.CompatEvents.CompatClientChatEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatConfigChangedEvent.CompatOnConfigChangedEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatHandler;
import net.teamfruit.emojicord.compat.CompatEvents.CompatRenderGameOverlayEvent.CompatPost;
import net.teamfruit.emojicord.compat.CompatEvents.CompatRenderGameOverlayEvent.CompatText;
import net.teamfruit.emojicord.emoji.EmojiText;

public class EventHandler extends CompatHandler {
	@Override
	public void onChat(final CompatClientChatEvent event) {
		final EmojiText emojiText = EmojiText.createEncoded(event.getMessage());
		event.setMessage(emojiText.getEncoded());
	}

	@Override
	public void onDraw(final CompatPost event) {
	}

	@Override
	public void onDraw(final net.teamfruit.emojicord.compat.CompatEvents.CompatGuiScreenEvent.CompatDrawScreenEvent.CompatPost event) {
	}

	@Override
	public void onText(final CompatText event) {
	}

	@Override
	public void onConfigChanged(final CompatOnConfigChangedEvent event) {
	}
}
