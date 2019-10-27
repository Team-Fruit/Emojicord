package net.teamfruit.emojicord.compat;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;
import net.teamfruit.emojicord.CoreInvoke;

@Cancelable
public class ClientChatEvent extends Event {
	private String message;
	private final String originalMessage;

	public ClientChatEvent(final String message) {
		setMessage(message);
		this.originalMessage = StringUtils.defaultString(message);
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(final String message) {
		this.message = StringUtils.defaultString(message);
	}

	public String getOriginalMessage() {
		return this.originalMessage;
	}

	@CoreInvoke
	@Nonnull
	public static String onClientSendMessage(final String message) {
		final ClientChatEvent event = new ClientChatEvent(message);
		return MinecraftForge.EVENT_BUS.post(event) ? "" : event.getMessage();
	}
}
