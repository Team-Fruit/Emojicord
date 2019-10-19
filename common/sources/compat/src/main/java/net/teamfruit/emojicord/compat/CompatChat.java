package net.teamfruit.emojicord.compat;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class CompatChat {

	public static class CompatTextComponent {
		public static CompatTextComponent blank = fromText("");

		public final ITextComponent component;

		public CompatTextComponent(final ITextComponent component) {
			this.component = component;
		}

		public @Nonnull List<CompatClickEvent> getLinksFromChat() {
			final List<CompatClickEvent> list = Lists.newLinkedList();
			getLinksFromChat0(list, this.component);
			return list;
		}

		private void getLinksFromChat0(final @Nonnull List<CompatClickEvent> list, final @Nonnull ITextComponent pchat) {
			final List<?> chats = pchat.getSiblings();
			for (final Object o : chats) {
				final ITextComponent chat = (ITextComponent) o;
				final ClickEvent ev = chat.getStyle().getClickEvent();
				if (ev!=null&&ev.getAction()==ClickEvent.Action.OPEN_URL)
					list.add(new CompatClickEvent(ev));
				getLinksFromChat0(list, chat);
			}
		}

		public CompatTextComponent setChatStyle(final CompatTextStyle style) {
			this.component.setStyle(style.style);
			return this;
		}

		public String getUnformattedText() {
			return this.component.getUnformattedText();
		}

		public static CompatTextComponent jsonToComponent(final String json) {
			return new CompatTextComponent(ITextComponent.Serializer.jsonToComponent(json));
		}

		public static CompatTextComponent fromText(final String text) {
			return new CompatTextComponent(new TextComponentString(text));
		}

		public static CompatTextComponent fromTranslation(final String text, final Object... params) {
			return new CompatTextComponent(new TextComponentTranslation(text, params));
		}

		public void sendClient() {
			Compat.CompatMinecraft.getMinecraft().getMinecraftObj().ingameGUI.getChatGUI().printChatMessage(this.component);
		}

		public void sendClientWithId(final int id) {
			Compat.CompatMinecraft.getMinecraft().getMinecraftObj().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(this.component, id);
		}

		public void sendPlayer(final @Nonnull ICommandSender target) {
			target.sendMessage(this.component);
		}
	}

	public static class CompatClickEvent {
		private final ClickEvent event;

		public CompatClickEvent(final ClickEvent event) {
			this.event = event;
		}

		public String getValue() {
			return this.event.getValue();
		}

		public static CompatClickEvent create(final CompatAction action, final String text) {
			return new CompatClickEvent(new ClickEvent(action.action, text));
		}

		public static enum CompatAction {
			OPEN_URL(ClickEvent.Action.OPEN_URL),
			OPEN_FILE(ClickEvent.Action.OPEN_FILE),
			RUN_COMMAND(ClickEvent.Action.RUN_COMMAND),
			SUGGEST_COMMAND(ClickEvent.Action.SUGGEST_COMMAND),
			;

			public final ClickEvent.Action action;

			private CompatAction(final ClickEvent.Action action) {
				this.action = action;
			}
		}
	}

	public static class CompatHoverEvent {
		public final HoverEvent event;

		public CompatHoverEvent(final HoverEvent event) {
			this.event = event;
		}

		public static CompatHoverEvent create(final CompatAction action, final CompatTextComponent text) {
			return new CompatHoverEvent(new HoverEvent(action.action, text.component));
		}

		public static enum CompatAction {
			SHOW_TEXT(HoverEvent.Action.SHOW_TEXT),
			SHOW_ITEM(HoverEvent.Action.SHOW_ITEM),
			;

			public final HoverEvent.Action action;

			private CompatAction(final HoverEvent.Action action) {
				this.action = action;
			}
		}
	}

	public static class CompatTextStyle {
		public final Style style;

		public CompatTextStyle(final Style style) {
			this.style = style;
		}

		public CompatTextStyle setColor(final CompatTextFormatting format) {
			this.style.setColor(format.format);
			return this;
		}

		public static CompatTextStyle create() {
			return new CompatTextStyle(new Style());
		}

		public CompatTextStyle setChatHoverEvent(final CompatHoverEvent event) {
			this.style.setHoverEvent(event.event);
			return this;
		}

		public CompatTextStyle setChatClickEvent(final CompatClickEvent event) {
			this.style.setClickEvent(event.event);
			return this;
		}
	}

	public static class CompatChatLine {
		public static CompatTextComponent getChatComponent(final ChatLine line) {
			return new CompatTextComponent(line.getChatComponent());
		}
	}

	public static enum CompatTextFormatting {
		BLACK(TextFormatting.BLACK),
		DARK_BLUE(TextFormatting.DARK_BLUE),
		DARK_GREEN(TextFormatting.DARK_GREEN),
		DARK_AQUA(TextFormatting.DARK_AQUA),
		DARK_RED(TextFormatting.DARK_RED),
		DARK_PURPLE(TextFormatting.DARK_PURPLE),
		GOLD(TextFormatting.GOLD),
		GRAY(TextFormatting.GRAY),
		DARK_GRAY(TextFormatting.DARK_GRAY),
		BLUE(TextFormatting.BLUE),
		GREEN(TextFormatting.GREEN),
		AQUA(TextFormatting.AQUA),
		RED(TextFormatting.RED),
		LIGHT_PURPLE(TextFormatting.LIGHT_PURPLE),
		YELLOW(TextFormatting.YELLOW),
		WHITE(TextFormatting.WHITE),
		OBFUSCATED(TextFormatting.OBFUSCATED),
		BOLD(TextFormatting.BOLD),
		STRIKETHROUGH(TextFormatting.STRIKETHROUGH),
		UNDERLINE(TextFormatting.UNDERLINE),
		ITALIC(TextFormatting.ITALIC),
		RESET(TextFormatting.RESET),;

		public final TextFormatting format;

		private CompatTextFormatting(final TextFormatting format) {
			this.format = format;
		}

		@Override
		public String toString() {
			return this.format.toString();
		}
	}

}
