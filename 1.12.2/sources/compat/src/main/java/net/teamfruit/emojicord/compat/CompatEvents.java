package net.teamfruit.emojicord.compat;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Maps;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
		public void onMouseClicked(final @Nonnull GuiScreenEvent.MouseInputEvent.Pre event) {
			if (Mouse.getEventButtonState()) {
				final int button = Mouse.getEventButton();
				if (button>=0)
					onMouseClicked(new CompatGuiScreenEvent.CompatMouseClickedEvent.CompatPre(event, button));
			}
		}

		@CoreEvent
		public abstract void onMouseClicked(final @Nonnull CompatGuiScreenEvent.CompatMouseClickedEvent.CompatPre event);

		@SubscribeEvent
		public void onMouseScroll(final @Nonnull GuiScreenEvent.MouseInputEvent.Pre event) {
			final int dwheel = Mouse.getEventDWheel();
			if (dwheel!=0)
				onMouseScroll(new CompatGuiScreenEvent.CompatMouseScrollEvent.CompatPre(event, dwheel));
		}

		@CoreEvent
		public abstract void onMouseScroll(final @Nonnull CompatGuiScreenEvent.CompatMouseScrollEvent.CompatPre event);

		private final Map<Integer, Integer> lwjgl2glfwKeyMappings = ((Supplier<Map<Integer, Integer>>) () -> {
			final Map<Integer, Integer> map = Maps.newHashMap();
			map.put(200, 265); //	KEY_UP		200	265
			map.put(208, 264); //	KEY_DOWN		208	264
			map.put(15, 258); //	KEY_TAB		15	258
			map.put(28, 257); //	KEY_ENTER	28	257
			map.put(156, 335); //	KEY_KP_ENTER	156	335
			map.put(1, 256); //		KEY_ESC		1	256
			return map;
		}).get();

		@SubscribeEvent
		public void onKeyPressed(final @Nonnull GuiScreenEvent.KeyboardInputEvent.Pre event) {
			if (Keyboard.getEventKeyState()) {
				final Integer key = this.lwjgl2glfwKeyMappings.get(Keyboard.getEventKey());
				if (key!=null)
					onKeyPressed(new CompatGuiScreenEvent.CompatKeyboardKeyPressedEvent.CompatPre(event, key));
			}
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
