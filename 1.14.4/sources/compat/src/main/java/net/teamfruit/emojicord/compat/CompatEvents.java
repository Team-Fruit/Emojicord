package net.teamfruit.emojicord.compat;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.teamfruit.emojicord.CoreEvent;
import net.teamfruit.emojicord.compat.Compat.CompatScreen;

public class CompatEvents {
	public static abstract class CompatHandler {
		public void registerHandler() {
			// FMLCommonHandler.instance().bus().register(this);
			MinecraftForge.EVENT_BUS.register(this);
		}

		@SubscribeEvent
		public void onChat(final @Nonnull ClientChatEvent event) {
			onChat(new CompatClientChatEvent(event));
		}

		@CoreEvent
		public abstract void onChat(final @Nonnull CompatClientChatEvent event);

		@SubscribeEvent
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

		@SubscribeEvent
		public void onDraw(final @Nonnull GuiScreenEvent.DrawScreenEvent.Post event) {
			onDraw(new CompatGuiScreenEvent.CompatDrawScreenEvent.CompatPost(event));
		}

		@CoreEvent
		public abstract void onDraw(final @Nonnull CompatGuiScreenEvent.CompatDrawScreenEvent.CompatPost event);

		@SubscribeEvent
		public void onInitGui(final @Nonnull GuiScreenEvent.InitGuiEvent.Post event) {
			onInitGui(new CompatGuiScreenEvent.CompatInitGuiEvent.CompatPost(event));
		}

		@CoreEvent
		public abstract void onInitGui(final @Nonnull CompatGuiScreenEvent.CompatInitGuiEvent.CompatPost event);

		@SubscribeEvent
		public void onMouseClicked(final @Nonnull GuiScreenEvent.MouseClickedEvent.Pre event) {
			onMouseClicked(new CompatGuiScreenEvent.CompatMouseClickedEvent.CompatPre(event));
		}

		@CoreEvent
		public abstract void onMouseClicked(final @Nonnull CompatGuiScreenEvent.CompatMouseClickedEvent.CompatPre event);

		@SubscribeEvent
		public void onMouseScroll(final @Nonnull GuiScreenEvent.MouseScrollEvent.Pre event) {
			onMouseScroll(new CompatGuiScreenEvent.CompatMouseScrollEvent.CompatPre(event));
		}

		@CoreEvent
		public abstract void onMouseScroll(final @Nonnull CompatGuiScreenEvent.CompatMouseScrollEvent.CompatPre event);

		@SubscribeEvent
		public void onMouseInput(final @Nonnull GuiScreenEvent.MouseInputEvent event) {
			onMouseInput(new CompatGuiScreenEvent.CompatMouseInputEvent(event));
		}

		@CoreEvent
		public abstract void onMouseInput(final @Nonnull CompatGuiScreenEvent.CompatMouseInputEvent event);

		@SubscribeEvent
		public void onKeyPressed(final @Nonnull GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
			onKeyPressed(new CompatGuiScreenEvent.CompatKeyboardKeyPressedEvent.CompatPre(event));
		}

		@CoreEvent
		public abstract void onKeyPressed(final @Nonnull CompatGuiScreenEvent.CompatKeyboardKeyPressedEvent.CompatPre event);

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

		public CompatScreen getGui() {
			return new CompatScreen(this.event.getGui());
		}

		public static class CompatDrawScreenEvent extends CompatGuiScreenEvent<GuiScreenEvent.DrawScreenEvent> {
			public CompatDrawScreenEvent(final GuiScreenEvent.DrawScreenEvent event) {
				super(event);
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
				public CompatPost(final GuiScreenEvent.DrawScreenEvent.Post event) {
					super(event);
				}
			}
		}

		public static class CompatInitGuiEvent extends CompatGuiScreenEvent<GuiScreenEvent.InitGuiEvent> {
			public CompatInitGuiEvent(final GuiScreenEvent.InitGuiEvent event) {
				super(event);
			}

			public static class CompatPost extends CompatInitGuiEvent {
				public CompatPost(final GuiScreenEvent.InitGuiEvent.Post event) {
					super(event);
				}
			}
		}

		public static class CompatMouseClickedEvent extends CompatGuiScreenEvent<GuiScreenEvent.MouseClickedEvent> {
			public CompatMouseClickedEvent(final GuiScreenEvent.MouseClickedEvent event) {
				super(event);
			}

			public int getButton() {
				return this.event.getButton();
			}

			public static class CompatPre extends CompatMouseClickedEvent {
				public CompatPre(final GuiScreenEvent.MouseClickedEvent.Pre event) {
					super(event);
				}
			}
		}

		public static class CompatMouseScrollEvent extends CompatGuiScreenEvent<GuiScreenEvent.MouseScrollEvent> {
			public CompatMouseScrollEvent(final GuiScreenEvent.MouseScrollEvent event) {
				super(event);
			}

			public double getScrollDelta() {
				return this.event.getScrollDelta();
			}

			public static class CompatPre extends CompatMouseScrollEvent {
				public CompatPre(final GuiScreenEvent.MouseScrollEvent.Pre event) {
					super(event);
				}
			}
		}

		public static class CompatMouseInputEvent extends CompatGuiScreenEvent<GuiScreenEvent.MouseInputEvent> {
			public CompatMouseInputEvent(final GuiScreenEvent.MouseInputEvent event) {
				super(event);
			}

			public double getMouseX() {
				return this.event.getMouseX();
			}

			public double getMouseY() {
				return this.event.getMouseY();
			}
		}

		public static class CompatKeyboardKeyPressedEvent extends CompatGuiScreenEvent<GuiScreenEvent.KeyboardKeyPressedEvent> {
			public CompatKeyboardKeyPressedEvent(final GuiScreenEvent.KeyboardKeyPressedEvent event) {
				super(event);
			}

			public int getKeyCode() {
				return this.event.getKeyCode();
			}

			public static class CompatPre extends CompatKeyboardKeyPressedEvent {
				public CompatPre(final GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
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
