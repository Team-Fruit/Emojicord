package net.teamfruit.emojicord.emoji;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
	@SubscribeEvent
	public void send(final ClientChatEvent ev) {
		final EmojiText emojiText = EmojiText.createEncoded(ev.getMessage());
		ev.setMessage(emojiText.getEncoded());
	}
}
