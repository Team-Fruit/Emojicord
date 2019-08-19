package net.teamfruit.emojicord.emoji;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.teamfruit.emojicord.emoji.EmojiText.EmojiTextParser;

public class EventHandler {
	@SubscribeEvent
	public void send(final ClientChatEvent ev) {
		ev.setMessage(EmojiTextParser.encode(ev.getMessage(), false));
	}
}
