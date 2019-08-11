package net.teamfruit.emojicord;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EmojicordHandler {
	@SubscribeEvent
	public void send(final ClientChatEvent ev) {
		ev.setMessage(EmojiParser.encode(ev.getMessage()));
	}
}
