package net.teamfruit.emojicord.compat;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Maps;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.teamfruit.emojicord.CoreEvent;
import net.teamfruit.emojicord.CoreInvoke;

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
		public void onTick(final @Nonnull ClientTickEvent event) {
			onTick(new CompatClientTickEvent(event));
		}

		@CoreEvent
		public abstract void onTick(final @Nonnull CompatClientTickEvent event);

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
		public void onMouseClicked(final @Nonnull GuiScreenEvent.MouseInputEvent.Pre event) {
			final int button = Mouse.getEventButton();
			if (button>=0)
				if (Mouse.getEventButtonState())
					onMouseClicked(new CompatGuiScreenEvent.CompatMouseClickedEvent.CompatPre(event, button));
				else
					onMouseReleased(new CompatGuiScreenEvent.CompatMouseReleasedEvent.CompatPre(event, button));
		}

		@CoreEvent
		public abstract void onMouseClicked(final @Nonnull CompatGuiScreenEvent.CompatMouseClickedEvent.CompatPre event);

		@CoreEvent
		public abstract void onMouseReleased(final @Nonnull CompatGuiScreenEvent.CompatMouseReleasedEvent.CompatPre event);

		@SubscribeEvent
		public void onMouseScroll(final @Nonnull GuiScreenEvent.MouseInputEvent.Pre event) {
			final int dwheel = Integer.valueOf(Mouse.getEventDWheel()).compareTo(0);
			if (dwheel!=0)
				onMouseScroll(new CompatGuiScreenEvent.CompatMouseScrollEvent.CompatPre(event, dwheel));
		}

		@CoreEvent
		public abstract void onMouseScroll(final @Nonnull CompatGuiScreenEvent.CompatMouseScrollEvent.CompatPre event);

		private final Map<Integer, Integer> lwjgl2glfwKeyMappings = ((Supplier<Map<Integer, Integer>>) () -> {
			final Map<Integer, Integer> map = Maps.newHashMap();
			map.put(205, 262); //	KEY_RIGHT		205	262
			map.put(203, 263); //	KEY_LEFT		203	263
			map.put(208, 264); //	KEY_DOWN		208	264
			map.put(200, 265); //	KEY_UP			200	265
			map.put(15, 258); //	KEY_TAB			15	258
			map.put(28, 257); //	KEY_ENTER		28	257
			map.put(156, 335); //	KEY_KP_ENTER	156	335
			map.put(1, 256); //		KEY_ESC			1	256
			return map;
		}).get();

		@SubscribeEvent
		public void onKeyPressed(final @Nonnull GuiScreenEvent.KeyboardInputEvent.Pre event) {
			if (Keyboard.getEventKeyState()) {
				final char eventChar = Keyboard.getEventCharacter();
				final int eventKey = Keyboard.getEventKey();
				onCharTyped(new CompatGuiScreenEvent.CompatKeyboardCharTypedEvent.CompatPre(event, eventChar, eventKey));
				final Integer key = this.lwjgl2glfwKeyMappings.get(eventKey);
				if (key!=null)
					onKeyPressed(new CompatGuiScreenEvent.CompatKeyboardKeyPressedEvent.CompatPre(event, key));
			}
		}

		@CoreEvent
		public abstract void onKeyPressed(final @Nonnull CompatGuiScreenEvent.CompatKeyboardKeyPressedEvent.CompatPre event);

		@CoreEvent
		public abstract void onCharTyped(final @Nonnull CompatGuiScreenEvent.CompatKeyboardCharTypedEvent.CompatPre event);

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

	public static class CompatClientTickEvent extends CompatEvent<ClientTickEvent> {
		public CompatClientTickEvent(final ClientTickEvent event) {
			super(event);
		}

		public CompatPhase getPhase() {
			return CompatPhase.getPhase(this.event.phase);
		}

		public static enum CompatPhase {
			START,
			END;
			;

			public static CompatPhase getPhase(final ClientTickEvent.Phase phase) {
				switch (phase) {
					case START:
						return CompatPhase.START;
					default:
					case END:
						return CompatPhase.END;
				}
			}
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

		public CompatGui.CompatScreen getGui() {
			return new CompatGui.CompatScreen(this.event.getGui());
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

		public static class CompatMouseClickedEvent extends CompatGuiScreenEvent<GuiScreenEvent.MouseInputEvent> {
			private final int button;

			public CompatMouseClickedEvent(final GuiScreenEvent.MouseInputEvent event, final int button) {
				super(event);
				this.button = button;
			}

			public int getButton() {
				return this.button;
			}

			public static class CompatPre extends CompatMouseClickedEvent {
				public CompatPre(final GuiScreenEvent.MouseInputEvent.Pre event, final int button) {
					super(event, button);
				}
			}
		}

		public static class CompatMouseReleasedEvent extends CompatGuiScreenEvent<GuiScreenEvent.MouseInputEvent> {
			private final int button;

			public CompatMouseReleasedEvent(final GuiScreenEvent.MouseInputEvent event, final int button) {
				super(event);
				this.button = button;
			}

			public int getButton() {
				return this.button;
			}

			public static class CompatPre extends CompatMouseReleasedEvent {
				public CompatPre(final GuiScreenEvent.MouseInputEvent.Pre event, final int button) {
					super(event, button);
				}
			}
		}

		public static class CompatMouseScrollEvent extends CompatGuiScreenEvent<GuiScreenEvent.MouseInputEvent> {
			private final double scrollDelta;

			public CompatMouseScrollEvent(final GuiScreenEvent.MouseInputEvent event, final double scrollDelta) {
				super(event);
				this.scrollDelta = scrollDelta;
			}

			public double getScrollDelta() {
				return this.scrollDelta;
			}

			public static class CompatPre extends CompatMouseScrollEvent {
				public CompatPre(final GuiScreenEvent.MouseInputEvent.Pre event, final double scrollDelta) {
					super(event, scrollDelta);
				}
			}
		}

		public static class CompatKeyboardCharTypedEvent extends CompatGuiScreenEvent<GuiScreenEvent.KeyboardInputEvent> {
			private final char codePoint;
			private final int modifiers;

			public CompatKeyboardCharTypedEvent(final GuiScreenEvent.KeyboardInputEvent event, final char codePoint, final int modifiers) {
				super(event);
				this.codePoint = codePoint;
				this.modifiers = modifiers;
			}

			public char getCodePoint() {
				return this.codePoint;
			}

			public int getModifiers() {
				return this.modifiers;
			}

			public static class CompatPre extends CompatKeyboardCharTypedEvent {
				public CompatPre(final GuiScreenEvent.KeyboardInputEvent.Pre event, final char codePoint, final int modifiers) {
					super(event, codePoint, modifiers);
				}
			}
		}

		public static class CompatKeyboardKeyPressedEvent extends CompatGuiScreenEvent<GuiScreenEvent.KeyboardInputEvent> {
			private final int keycode;

			public CompatKeyboardKeyPressedEvent(final GuiScreenEvent.KeyboardInputEvent event, final int keycode) {
				super(event);
				this.keycode = keycode;
			}

			public int getKeyCode() {
				return this.keycode;
			}

			public static class CompatPre extends CompatKeyboardKeyPressedEvent {
				public CompatPre(final GuiScreenEvent.KeyboardInputEvent.Pre event, final int keycode) {
					super(event, keycode);
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
