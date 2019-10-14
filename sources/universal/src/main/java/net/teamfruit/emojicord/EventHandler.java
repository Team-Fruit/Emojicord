package net.teamfruit.emojicord;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.teamfruit.emojicord.compat.Compat.CompatChatScreen;
import net.teamfruit.emojicord.compat.CompatEvents.CompatClientChatEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatClientTickEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatConfigChangedEvent.CompatOnConfigChangedEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatGuiScreenEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatGuiScreenEvent.CompatMouseReleasedEvent.CompatPre;
import net.teamfruit.emojicord.compat.CompatEvents.CompatHandler;
import net.teamfruit.emojicord.compat.CompatEvents.CompatRenderGameOverlayEvent;
import net.teamfruit.emojicord.emoji.EmojiFrequently;
import net.teamfruit.emojicord.emoji.EmojiText;
import net.teamfruit.emojicord.emoji.PickerItem;
import net.teamfruit.emojicord.gui.EmojiSelectionChat;
import net.teamfruit.emojicord.gui.EmojiSettings;
import net.teamfruit.emojicord.gui.IChatOverlay;
import net.teamfruit.emojicord.gui.SuggestionChat;

public class EventHandler extends CompatHandler {
	static final @Nonnull Pattern skintonePattern = Pattern.compile("\\:skin-tone-(\\d)\\:");

	private final List<Function<CompatChatScreen, IChatOverlay>> overlayFactories = Lists.newArrayList(
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
		if (this.watcher!=null) {
			final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat(Reference.MODID+"-directory-watch-%d").build());
			executor.submit(() -> {
				while (true)
					try {
						final WatchKey watchKey = this.watcher.take();
						for (final WatchEvent<?> event : watchKey.pollEvents()) {
							if (event.kind()==OVERFLOW)
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
		if (this.watcher==null)
			return true;
		else
			return this.changed.getAndSet(false);
	}

	@Override
	public void onChat(final CompatClientChatEvent event) {
		final String message = event.getMessage();
		if (!message.startsWith("/")) {
			final EmojiText emojiText = EmojiText.createEncoded(message);
			final String untoned = skintonePattern.matcher(message).replaceAll("");
			PickerItem.fromText(EmojiText.createParsed(untoned)).forEach(EmojiFrequently.instance::use);
			if (EmojiFrequently.instance.hasChanged())
				EmojiFrequently.instance.save();
			event.setMessage(emojiText.getEncoded());
		}
	}

	@Override
	public void onTick(final CompatClientTickEvent event) {
		if (event.getPhase()==CompatClientTickEvent.CompatPhase.START)
			for (final IChatOverlay overlay : this.overlays)
				overlay.onTick();
	}

	@Override
	public void onDraw(final CompatRenderGameOverlayEvent.CompatPost event) {
	}

	@Override
	public void onText(final CompatRenderGameOverlayEvent.CompatText event) {
	}

	@Override
	public void onConfigChanged(final CompatOnConfigChangedEvent event) {
	}

	@Override
	public void onInitGui(final CompatGuiScreenEvent.CompatInitGuiEvent.CompatPost event) {
		final CompatChatScreen chatScreen = CompatChatScreen.cast(event.getGui());
		if (chatScreen!=null)
			this.overlays = this.overlayFactories.stream().map(e -> e.apply(chatScreen)).collect(Collectors.toList());
		else
			this.overlays = Collections.emptyList();
	}

	@Override
	public void onDraw(final CompatGuiScreenEvent.CompatDrawScreenEvent.CompatPost event) {
		for (final ListIterator<IChatOverlay> itr = this.overlays.listIterator(this.overlays.size()); itr.hasPrevious();) {
			final IChatOverlay overlay = itr.previous();
			overlay.onMouseInput(event.getMouseX(), event.getMouseY());
			if (overlay.onDraw())
				event.setCanceled(true);
		}
	}

	@Override
	public void onMouseClicked(final CompatGuiScreenEvent.CompatMouseClickedEvent.CompatPre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onMouseClicked(event.getButton())) {
				event.setCanceled(true);
				break;
			}
	}

	@Override
	public void onMouseReleased(final CompatPre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onMouseReleased(event.getButton())) {
				event.setCanceled(true);
				break;
			}
	}

	@Override
	public void onMouseScroll(final CompatGuiScreenEvent.CompatMouseScrollEvent.CompatPre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onMouseScroll(event.getScrollDelta())) {
				event.setCanceled(true);
				break;
			}
	}

	@Override
	public void onCharTyped(final CompatGuiScreenEvent.CompatKeyboardCharTypedEvent.CompatPre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onCharTyped(event.getCodePoint(), event.getModifiers())) {
				event.setCanceled(true);
				break;
			}
	}

	@Override
	public void onKeyPressed(final CompatGuiScreenEvent.CompatKeyboardKeyPressedEvent.CompatPre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onKeyPressed(event.getKeyCode())) {
				event.setCanceled(true);
				break;
			}
	}
}
