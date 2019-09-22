package net.teamfruit.emojicord.compat;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Strings;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.teamfruit.emojicord.CoreEvent;
import net.teamfruit.emojicord.CoreInvoke;

public class CompatEvents {
	public static abstract class CompatHandler {
		public void registerHandler() {
			// FMLCommonHandler.instance().bus().register(this);
			MinecraftForge.EVENT_BUS.register(this);
		}

		@SubscribeEvent()
		public void onChat(final @Nonnull ClientChatEvent event) {
			onChat(new CompatClientChatEvent(event));
		}

		@CoreEvent
		public abstract void onChat(final @Nonnull CompatClientChatEvent event);

		@SubscribeEvent()
		public void onDraw(final @Nonnull RenderGameOverlayEvent.Post event) {
			onDraw(new CompatRenderGameOverlayEvent.CompatPost(event));
		}

		@CoreEvent
		public abstract void onDraw(final @Nonnull CompatRenderGameOverlayEvent.CompatPost event);

		@SubscribeEvent
		public void onText(final @Nonnull RenderGameOverlayEvent.Text event) {
			onText(new CompatRenderGameOverlayEvent.CompatText(event));
		}

		@CoreEvent
		public abstract void onText(final @Nonnull CompatRenderGameOverlayEvent.CompatText event);

		@SubscribeEvent()
		public void onDraw(final @Nonnull GuiScreenEvent.DrawScreenEvent.Post event) {
			onDraw(new CompatGuiScreenEvent.CompatDrawScreenEvent.CompatPost(event));
		}

		@CoreEvent
		public abstract void onDraw(final @Nonnull CompatGuiScreenEvent.CompatDrawScreenEvent.CompatPost event);

		@SubscribeEvent
		public void onConfigChanged(final @Nonnull ConfigChangedEvent.OnConfigChangedEvent event) {
			onConfigChanged(new CompatConfigChangedEvent.CompatOnConfigChangedEvent(event));
		}

		@CoreEvent
		public abstract void onConfigChanged(final @Nonnull CompatConfigChangedEvent.CompatOnConfigChangedEvent event);
	}

	public static class CompatEvent<T extends Event> {
		protected final T event;

		public CompatEvent(final T event) {
			this.event = event;
		}

		public void setCanceled(final boolean cancel) {
			this.event.setCanceled(cancel);
		}
	}

	@Cancelable
	public static class ClientChatEvent extends Event {
		private String message;
		private final String originalMessage;

		public ClientChatEvent(final String message) {
			setMessage(message);
			this.originalMessage = Strings.nullToEmpty(message);
		}

		public String getMessage() {
			return this.message;
		}

		public void setMessage(final String message) {
			this.message = Strings.nullToEmpty(message);
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

	public static class CompatClientChatEvent extends CompatEvent<ClientChatEvent> {
		public CompatClientChatEvent(final ClientChatEvent event) {
			super(event);
		}

		public String getMessage() {
			return this.event.getMessage();
		}

		public String getOriginalMessage() {
			return this.event.getOriginalMessage();
		}

		public void setMessage(final String message) {
			this.event.setMessage(message);
		}
	}

	public static class CompatRenderGameOverlayEvent<T extends RenderGameOverlayEvent> extends CompatEvent<T> {
		public CompatRenderGameOverlayEvent(final T event) {
			super(event);
		}

		public static class CompatPost extends CompatRenderGameOverlayEvent<RenderGameOverlayEvent> {
			public CompatPost(final RenderGameOverlayEvent event) {
				super(event);
			}
		}

		public static class CompatText extends CompatRenderGameOverlayEvent<RenderGameOverlayEvent.Text> {
			public CompatText(final RenderGameOverlayEvent.Text event) {
				super(event);
			}

			public List<String> getLeft() {
				return this.event.getLeft();
			}
		}

		public CompatElementType getType() {
			return CompatElementType.getType(this.event.getType());
		}

		public float getPartialTicks() {
			return this.event.getPartialTicks();
		}

		public static enum CompatElementType {
			CHAT,
			EXPERIENCE,
			OTHER,
			;

			public static CompatElementType getType(final RenderGameOverlayEvent.ElementType type) {
				if (type==null)
					return CompatElementType.OTHER;
				switch (type) {
					case CHAT:
						return CHAT;
					case EXPERIENCE:
						return EXPERIENCE;
					default:
						return OTHER;
				}
			}
		}
	}

	public static class CompatGuiScreenEvent<T extends GuiScreenEvent> extends CompatEvent<T> {
		public CompatGuiScreenEvent(final T event) {
			super(event);
		}

		public static class CompatDrawScreenEvent extends CompatGuiScreenEvent<GuiScreenEvent.DrawScreenEvent> {
			public CompatDrawScreenEvent(final GuiScreenEvent.DrawScreenEvent event) {
				super(event);
			}

			public GuiScreen getGui() {
				return this.event.getGui();
			}

			public int getMouseX() {
				return this.event.getMouseX();
			}

			public int getMouseY() {
				return this.event.getMouseY();
			}

			public float getRenderPartialTicks() {
				return this.event.getRenderPartialTicks();
			}

			public static class CompatPost extends CompatDrawScreenEvent {
				public CompatPost(final GuiScreenEvent.DrawScreenEvent event) {
					super(event);
				}
			}
		}
	}

	public static class CompatConfigChangedEvent extends CompatEvent<ConfigChangedEvent> {
		public CompatConfigChangedEvent(final ConfigChangedEvent event) {
			super(event);
		}

		public String getModId() {
			return this.event.getModID();
		}

		public static class CompatOnConfigChangedEvent extends CompatConfigChangedEvent {
			public CompatOnConfigChangedEvent(final ConfigChangedEvent event) {
				super(event);
			}
		}
	}
}
