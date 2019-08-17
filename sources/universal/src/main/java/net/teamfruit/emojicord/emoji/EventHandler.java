package net.teamfruit.emojicord.emoji;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
	@SubscribeEvent
	public void send(final ClientChatEvent ev) {
		ev.setMessage(TextParser.encode(ev.getMessage()));
	}
}
