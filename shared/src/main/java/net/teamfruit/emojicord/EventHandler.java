package net.teamfruit.emojicord;

#if MC_7_LATER
import net.minecraftforge.client.event.GuiScreenEvent.*;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
#else
import net.teamfruit.emojicord.compat.KeyboardInputEvent;
import net.teamfruit.emojicord.compat.MouseInputEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
#endif

import net.teamfruit.emojicord.compat.CompatEvents.CompatGuiScreenEvent.*;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.teamfruit.emojicord.compat.ClientChatEvent;
import net.teamfruit.emojicord.emoji.EmojiFrequently;
import net.teamfruit.emojicord.emoji.EmojiText;
import net.teamfruit.emojicord.emoji.PickerItem;
import net.teamfruit.emojicord.gui.EmojiSelectionChat;
import net.teamfruit.emojicord.gui.EmojiSettings;
import net.teamfruit.emojicord.gui.IChatOverlay;
import net.teamfruit.emojicord.gui.SuggestionChat;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.*;
import static net.teamfruit.emojicord.emoji.EmojiText.ParseFlag.*;

public class EventHandler {
	public void registerHandler() {
		#if !MC_7_LATER
		cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(this);
		#endif
		MinecraftForge.EVENT_BUS.register(this);
	}

	static final @Nonnull
	Pattern skintonePattern = Pattern.compile("\\:skin-tone-(\\d)\\:");

	private final List<Function<GuiChat, IChatOverlay>> overlayFactories = Arrays.asList(
			EmojiSettings::new,
			EmojiSelectionChat::new,
			SuggestionChat::new);
	private List<IChatOverlay> overlays = Collections.emptyList();

	private WatchService watcher;
	private AtomicBoolean changed = new AtomicBoolean(false);

	public void registerDictionaryWatcher(final File dictDir) {
		try {
			final Path dir = dictDir.toPath();
			final WatchService watcher = FileSystems.getDefault().newWatchService();
			dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			this.watcher = watcher;
		} catch (final IOException e) {
			Log.log.warn("Could not watch the dictionary directory: ", e);
		}
		if (this.watcher != null) {
			final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat(Reference.MODID + "-directory-watch-%d").build());
			executor.submit(() -> {
				while (true)
					try {
						final WatchKey watchKey = this.watcher.take();
						for (final WatchEvent<?> event : watchKey.pollEvents()) {
							if (event.kind() == OVERFLOW)
								continue;
							this.changed.set(true);
						}
						watchKey.reset();
					} catch (final InterruptedException e) {
						this.changed.set(true);
					}
			});
			executor.shutdown();
		}
	}

	public boolean hasDictionaryDirectoryChanged() {
		if (this.watcher == null)
			return true;
		else
			return this.changed.getAndSet(false);
	}

	@SubscribeEvent
	public void onChat(final ClientChatEvent event) {
		final String message = event.getMessage();
		if (!message.startsWith("/")) {
			{
				final String untoned = skintonePattern.matcher(message).replaceAll("");
				final EmojiText emojiText = EmojiText.create(untoned, EnumSet.of(ESCAPE, ENCODE, ENCODE_ALIAS, ENCODE_UTF, PARSE));
				PickerItem.fromText(emojiText).forEach(EmojiFrequently.instance::use);
				if (EmojiFrequently.instance.hasChanged())
					EmojiFrequently.instance.save();
			}
			{
				final EmojiText emojiText = EmojiText.create(message, EnumSet.of(ESCAPE, ENCODE, ENCODE_ALIAS, ENCODE_UTF));
				event.setMessage(emojiText.getEncoded());
			}
		}
	}

	@SubscribeEvent
	public void onTick(final TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.ClientTickEvent.Phase.START)
			for (final IChatOverlay overlay : this.overlays)
				overlay.onTick();
	}

	@SubscribeEvent
	public void onInitGui(final GuiScreenEvent.InitGuiEvent.Post event) {
		final GuiScreen chatScreen = event. #if MC_7_LATER getGui() #else gui #endif ;
		if (chatScreen instanceof GuiChat)
			this.overlays = this.overlayFactories.stream().map(e -> e.apply((GuiChat) chatScreen)).collect(Collectors.toList());
		else
			this.overlays = Collections.emptyList();
	}

	@SubscribeEvent
	public void onDraw(final GuiScreenEvent.DrawScreenEvent.Post event) {
		for (final ListIterator<IChatOverlay> itr = this.overlays.listIterator(this.overlays.size()); itr.hasPrevious(); ) {
			final IChatOverlay overlay = itr.previous();
			overlay.onMouseInput(event. #if MC_7_LATER getMouseX() #else mosueX #endif , event. #if MC_7_LATER getMouseY() #else mouseY #endif );
			if (overlay.onDraw())
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onConfigChanged(final @Nonnull ConfigChangedEvent.OnConfigChangedEvent event) {
		// EmojicordConfig.spec.onConfigChanged();
	}

	@SubscribeEvent
	public void onMouseClicked(final @Nonnull MouseInputEvent event) {
		final int button = Mouse.getEventButton();
		if (button >= 0)
			if (Mouse.getEventButtonState())
				if (MinecraftForge.EVENT_BUS.post(new MouseClickedEvent.Pre(event. #if MC_7_LATER getGui() #else gui #endif , button)))
					event.setCanceled(true);
			else
				if (MinecraftForge.EVENT_BUS.post(new MouseReleasedEvent.Pre(event. #if MC_7_LATER getGui() #else gui #endif , button)))
					event.setCanceled(true);
	}

	@SubscribeEvent
	public void onMouseClicked(final MouseClickedEvent.Pre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onMouseClicked(event.getButton())) {
				event.setCanceled(true);
				break;
			}
	}

	@SubscribeEvent
	public void onMouseReleased(final MouseReleasedEvent.Pre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onMouseReleased(event.getButton())) {
				event.setCanceled(true);
				break;
			}
	}

	@SubscribeEvent
	public void onMouseScroll(final @Nonnull MouseInputEvent event) {
		final int dwheel = Integer.valueOf(Mouse.getEventDWheel()).compareTo(0);
		if (dwheel != 0)
			if (MinecraftForge.EVENT_BUS.post(new MouseScrollEvent.Pre(event. #if MC_7_LATER getGui() #else gui #endif , dwheel)))
				event.setCanceled(true);
	}

	@SubscribeEvent
	public void onMouseScroll(final MouseScrollEvent.Pre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onMouseScroll(event.getScrollDelta())) {
				event.setCanceled(true);
				break;
			}
	}

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

	#if !MC_12_LATER
	@SubscribeEvent
	public void onKeyPressed(final @Nonnull KeyboardInputEvent.Pre event) {
		if (Keyboard.getEventKeyState()) {
			final char eventChar = Keyboard.getEventCharacter();
			final int eventKey = Keyboard.getEventKey();
			if (MinecraftForge.EVENT_BUS.post(new KeyboardCharTypedEvent.Pre(event. #if MC_7_LATER getGui() #else gui #endif , eventChar, eventKey)))
				event.setCanceled(true);
			else {
				final Integer key = this.lwjgl2glfwKeyMappings.get(eventKey);
				if (key != null)
					if (MinecraftForge.EVENT_BUS.post(new KeyboardKeyPressedEvent.Pre(event. #if MC_7_LATER getGui() #else gui #endif , key)))
						event.setCanceled(true);
			}
		}
	}
	#endif

	@SubscribeEvent
	public void onCharTyped(final KeyboardCharTypedEvent.Pre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onCharTyped(event.getCodePoint(), event.getModifiers())) {
				event.setCanceled(true);
				break;
			}
	}

	@SubscribeEvent
	public void onKeyPressed(final KeyboardKeyPressedEvent.Pre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onKeyPressed(event.getKeyCode())) {
				event.setCanceled(true);
				break;
			}
	}
}
