package net.teamfruit.emojicord;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import net.minecraftforge.client.event.GuiScreenEvent.KeyboardCharTypedEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.teamfruit.emojicord.compat.Compat.CompatChatScreen;
import net.teamfruit.emojicord.compat.CompatEvents.CompatClientChatEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatConfigChangedEvent.CompatOnConfigChangedEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatGuiScreenEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatHandler;
import net.teamfruit.emojicord.compat.CompatEvents.CompatRenderGameOverlayEvent;
import net.teamfruit.emojicord.emoji.EmojiFrequently;
import net.teamfruit.emojicord.emoji.EmojiText;
import net.teamfruit.emojicord.emoji.PickerItem;
import net.teamfruit.emojicord.gui.EmojiSelectionChat;
import net.teamfruit.emojicord.gui.IChatOverlay;
import net.teamfruit.emojicord.gui.SuggestionChat;

public class EventHandler extends CompatHandler {
	static final @Nonnull Pattern skintonePattern = Pattern.compile("\\:skin-tone-(\\d)\\:");

	private final List<Function<CompatChatScreen, IChatOverlay>> overlayFactories = Lists.newArrayList(
			SuggestionChat::new,
			EmojiSelectionChat::new);
	private List<IChatOverlay> overlays = Collections.emptyList();

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
		for (final IChatOverlay overlay : this.overlays) {
			overlay.onMouseInput(event.getMouseX(), event.getMouseY());
			if (overlay.onDraw())
				event.setCanceled(true);
		}
	}

	@Override
	public void onMouseClicked(final CompatGuiScreenEvent.CompatMouseClickedEvent.CompatPre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onMouseClicked(event.getButton()))
				event.setCanceled(true);
	}

	@Override
	public void onMouseScroll(final CompatGuiScreenEvent.CompatMouseScrollEvent.CompatPre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onMouseScroll(event.getScrollDelta()))
				event.setCanceled(true);
	}

	@SubscribeEvent
	public void onCharTyped(final KeyboardCharTypedEvent.Pre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onCharTyped(event.getCodePoint(), event.getModifiers()))
				event.setCanceled(true);
	}

	@Override
	public void onKeyPressed(final CompatGuiScreenEvent.CompatKeyboardKeyPressedEvent.CompatPre event) {
		for (final IChatOverlay overlay : this.overlays)
			if (overlay.onKeyPressed(event.getKeyCode()))
				event.setCanceled(true);
	}

	@SubscribeEvent
	public void onTick(final ClientTickEvent event) {
		if (event.phase==Phase.START)
			for (final IChatOverlay overlay : this.overlays)
				overlay.onTick();
	}
}
