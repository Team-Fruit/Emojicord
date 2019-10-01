package net.teamfruit.emojicord.gui;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.teamfruit.emojicord.EmojicordConfig;
import net.teamfruit.emojicord.compat.Compat.CompatChatScreen;
import net.teamfruit.emojicord.compat.Compat.CompatFontRenderer;
import net.teamfruit.emojicord.compat.Compat.CompatMinecraft;
import net.teamfruit.emojicord.compat.Compat.CompatScreen;
import net.teamfruit.emojicord.compat.Compat.CompatTextFieldWidget;
import net.teamfruit.emojicord.compat.CompatBaseVertex;
import net.teamfruit.emojicord.compat.CompatVertex;
import net.teamfruit.emojicord.compat.OpenGL;
import net.teamfruit.emojicord.emoji.DiscordEmojiIdDictionary;
import net.teamfruit.emojicord.emoji.StandardEmojiIdDictionary;

public class SuggestionChat {
	public final CompatScreen screen;
	public final CompatChatScreen chatScreen;
	public final CompatTextFieldWidget inputField;
	public final CompatFontRenderer font;
	public int mouseX, mouseY;
	private SuggestionsList suggestions;
	private CompletableFuture<Suggestions> pendingSuggestions;
	private boolean applyingSuggestion;
	private String inputFieldTextLast;

	public SuggestionChat(final CompatChatScreen chatScreen) {
		this.screen = chatScreen.cast();
		this.chatScreen = chatScreen;
		this.font = CompatMinecraft.getMinecraft().getFontRenderer();
		this.inputField = chatScreen.getTextField();
	}

	public boolean onDraw() {
		if (this.suggestions!=null)
			this.suggestions.render();
		checkTextUpdate();
		return false;
	}

	public boolean onMouseClicked(final int button) {
		return this.suggestions!=null&&this.suggestions.mouseClicked(button);
	}

	public boolean onMouseScroll(final double scrollDelta) {
		return this.suggestions!=null&&this.suggestions.mouseScrolled(scrollDelta);
	}

	public boolean onMouseInput(final int mouseX, final int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		return false;
	}

	public boolean onKeyPressed(final int keycode) {
		if (this.suggestions!=null&&this.suggestions.keyPressed(keycode))
			return true;
		else if (keycode==258)
			updateSuggestion(true);
		return false;
	}

	public void checkTextUpdate() {
		final String inputFieldText = this.inputField.getText();
		if (!StringUtils.equals(this.inputFieldTextLast, inputFieldText)) {
			this.inputFieldTextLast = inputFieldText;
			updateSuggestion(false);
		}
	}

	public void showSuggestions() {
		if (this.pendingSuggestions!=null&&this.pendingSuggestions.isDone()) {
			int i = 0;
			final Suggestions suggestions = this.pendingSuggestions.join();
			if (!suggestions.getList().isEmpty()) {
				for (final Suggestion suggestion : suggestions.getList())
					i = Math.max(i, this.font.getStringWidth(suggestion.getText()+" "+StringUtils.substringBetween(suggestion.getText(), ":")));

				final int j = MathHelper.clamp(this.inputField.getInsertPos(suggestions.getRange().getStart()), 0, this.screen.getWidth()-i);
				this.suggestions = new SuggestionsList(j, this.screen.getHeight()-12, i, suggestions);
			}
		}
	}

	private void updateSuggestion(final boolean skipCount) {
		final String s = this.inputField.getText();

		if (!this.applyingSuggestion) {
			this.inputField.setSuggestion((String) null);
			this.suggestions = null;
		}

		final StringReader stringreader = new StringReader(s);
		if (stringreader.canRead()) {
			final int cursorPosition = this.inputField.getCursorPosition();
			final int lastWordIndex = getLastWordIndex(s);
			if ((skipCount||cursorPosition-lastWordIndex>=3)&&(this.suggestions==null||!this.applyingSuggestion)) {
				final CompletableFuture<Iterable<String>> list = CompletableFuture.supplyAsync(() -> Iterables.concat(
						StandardEmojiIdDictionary.StandardEmojiIdRepository.instance.nameDictionary.keySet(),
						DiscordEmojiIdDictionary.instance.get().keySet()));

				this.pendingSuggestions = list.thenApplyAsync(e -> suggest(e, new SuggestionsBuilder(s, lastWordIndex)));
				this.pendingSuggestions.thenRun(() -> {
					if (this.pendingSuggestions.isDone())
						updateUsageInfo();
				});
			}
		}
	}

	private void updateUsageInfo() {
		this.suggestions = null;
		if (EmojicordConfig.SUGGEST.autoSuggest.get())
			showSuggestions();
	}

	private static Suggestions suggest(final Iterable<String> collection, final SuggestionsBuilder suggestionBuilder) {
		final String s = suggestionBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (final String s1 : collection)
			if ((":"+s1.toLowerCase(Locale.ROOT)+":").startsWith(s))
				suggestionBuilder.suggest(":"+s1+":");

		final Suggestions result = suggestionBuilder.build();
		return result;
	}

	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");

	public static int getLastWordIndex(final String text) {
		if (StringUtils.isEmpty(text))
			return 0;
		else {
			int i = 0;

			for (final Matcher matcher = WHITESPACE_PATTERN.matcher(text); matcher.find(); i = matcher.end())
				;

			return i;
		}
	}

	public static String calculateSuggestionSuffix(final String text, final String textwithsuffix) {
		return textwithsuffix.startsWith(text) ? textwithsuffix.substring(text.length()) : null;
	}

	public static void fill(int x1, int y1, int x2, int y2, final int color) {
		if (x1<x2) {
			final int i = x1;
			x1 = x2;
			x2 = i;
		}

		if (y1<y2) {
			final int j = y1;
			y1 = y2;
			y2 = j;
		}

		final float f3 = (color>>24&255)/255.0F;
		final float f = (color>>16&255)/255.0F;
		final float f1 = (color>>8&255)/255.0F;
		final float f2 = (color&255)/255.0F;
		final CompatBaseVertex t = CompatVertex.getTessellator();
		OpenGL.glEnable(GL11.GL_BLEND);
		OpenGL.glDisable(GL11.GL_TEXTURE_2D);
		OpenGL.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		OpenGL.glColor4f(f, f1, f2, f3);
		t.begin(GL11.GL_QUADS);
		t.pos(x1, y2, 0.0D);
		t.pos(x2, y2, 0.0D);
		t.pos(x2, y1, 0.0D);
		t.pos(x1, y1, 0.0D);
		t.draw();
		OpenGL.glEnable(GL11.GL_TEXTURE_2D);
		OpenGL.glDisable(GL11.GL_BLEND);
	}

	public static String getEmojiDisplayText(final String text) {
		return text+" "+StringUtils.substringBetween(text, ":");
	}

	private class SuggestionsList {
		private final Rectangle2d rectangle;
		private final Suggestions suggestions;
		private final String text;
		private int scrollY;
		private int selectedIndex;
		private int lastMouseX;;
		private int lastMouseY;;
		private boolean arrowKeyUsed;

		private SuggestionsList(final int posX, final int posY, final int width, final Suggestions suggestions) {
			this.rectangle = new Rectangle2d(posX-1, posY-3-Math.min(suggestions.getList().size(), 10)*12, width+1, Math.min(suggestions.getList().size(), 10)*12);
			this.suggestions = suggestions;
			this.text = SuggestionChat.this.inputField.getText();
			select(0);
		}

		public void render() {
			final int i = Math.min(this.suggestions.getList().size(), 10);
			final boolean isScrollTop = this.scrollY>0;
			final boolean isScrollBottom = this.suggestions.getList().size()>this.scrollY+i;
			final boolean isScroll = isScrollTop||isScrollBottom;
			final boolean isMouseMoved = this.lastMouseX!=SuggestionChat.this.mouseX||this.lastMouseY!=SuggestionChat.this.mouseY;
			if (isMouseMoved) {
				this.lastMouseX = SuggestionChat.this.mouseX;
				this.lastMouseY = SuggestionChat.this.mouseY;
			}

			if (isScroll) {
				fill(this.rectangle.getX(), this.rectangle.getY()-1, this.rectangle.getX()+this.rectangle.getWidth(), this.rectangle.getY(), 0xD0000000);
				fill(this.rectangle.getX(), this.rectangle.getY()+this.rectangle.getHeight(), this.rectangle.getX()+this.rectangle.getWidth(), this.rectangle.getY()+this.rectangle.getHeight()+1, 0xD0000000);
				if (isScrollTop)
					for (int k = 0; k<this.rectangle.getWidth(); ++k)
						if (k%2==0)
							fill(this.rectangle.getX()+k, this.rectangle.getY()-1, this.rectangle.getX()+k+1, this.rectangle.getY(), -1);

				if (isScrollBottom)
					for (int i1 = 0; i1<this.rectangle.getWidth(); ++i1)
						if (i1%2==0)
							fill(this.rectangle.getX()+i1, this.rectangle.getY()+this.rectangle.getHeight(), this.rectangle.getX()+i1+1, this.rectangle.getY()+this.rectangle.getHeight()+1, -1);
			}

			boolean flag4 = false;

			for (int l = 0; l<i; ++l) {
				final Suggestion suggestion = this.suggestions.getList().get(l+this.scrollY);
				fill(this.rectangle.getX(), this.rectangle.getY()+12*l, this.rectangle.getX()+this.rectangle.getWidth(), this.rectangle.getY()+12*l+12, 0xD0000000);
				if (SuggestionChat.this.mouseX>this.rectangle.getX()&&SuggestionChat.this.mouseX<this.rectangle.getX()+this.rectangle.getWidth()&&SuggestionChat.this.mouseY>this.rectangle.getY()+12*l&&SuggestionChat.this.mouseY<this.rectangle.getY()+12*l+12) {
					if (isMouseMoved)
						select(l+this.scrollY);

					flag4 = true;
				}

				SuggestionChat.this.font.drawStringWithShadow(getEmojiDisplayText(suggestion.getText()), this.rectangle.getX()+1, this.rectangle.getY()+2+12*l, l+this.scrollY==this.selectedIndex ? 0xFFFFFF00 : 0xFFAAAAAA);
			}

			if (flag4) {
				final Message message = this.suggestions.getList().get(this.selectedIndex).getTooltip();
				if (message!=null)
					;//renderTooltip(TextComponentUtils.toTextComponent(message).getFormattedText(), p_198500_1_, p_198500_2_);
			}
		}

		public boolean mouseClicked(final int clickButton) {
			if (!this.rectangle.contains(SuggestionChat.this.mouseX, SuggestionChat.this.mouseY))
				return false;
			else {
				final int i = (SuggestionChat.this.mouseY-this.rectangle.getY())/12+this.scrollY;
				if (i>=0&&i<this.suggestions.getList().size()) {
					select(i);
					useSuggestion();
				}

				return true;
			}
		}

		public boolean mouseScrolled(final double wheelScroll) {
			if (this.rectangle.contains(SuggestionChat.this.mouseX, SuggestionChat.this.mouseY)) {
				this.scrollY = MathHelper.clamp((int) (this.scrollY-wheelScroll), 0, Math.max(this.suggestions.getList().size()-10, 0));
				return true;
			} else
				return false;
		}

		public boolean keyPressed(final int keyTyped) {
			if (keyTyped==265) {
				cycle(-1);
				this.arrowKeyUsed = false;
				return true;
			} else if (keyTyped==264) {
				cycle(1);
				this.arrowKeyUsed = false;
				return true;
			} else if (keyTyped==258) {
				if (this.arrowKeyUsed)
					cycle(CompatScreen.hasShiftDown() ? -1 : 1);
				useSuggestion();
				return true;
			} else if ((keyTyped==257||keyTyped==335)&&EmojicordConfig.SUGGEST.enterSuggest.get()) {
				useSuggestion();
				return true;
			} else if (keyTyped==256) {
				hide();
				return true;
			} else
				return false;
		}

		public void cycle(final int deltaIndex) {
			select(this.selectedIndex+deltaIndex);
			final int i = this.scrollY;
			final int j = this.scrollY+10-1;
			if (this.selectedIndex<i)
				this.scrollY = MathHelper.clamp(this.selectedIndex, 0, Math.max(this.suggestions.getList().size()-10, 0));
			else if (this.selectedIndex>j)
				this.scrollY = MathHelper.clamp(this.selectedIndex+1-10, 0, Math.max(this.suggestions.getList().size()-10, 0));
		}

		public void select(final int index) {
			this.selectedIndex = index;
			if (this.selectedIndex<0)
				this.selectedIndex += this.suggestions.getList().size();

			if (this.selectedIndex>=this.suggestions.getList().size())
				this.selectedIndex -= this.suggestions.getList().size();

			final Suggestion suggestion = this.suggestions.getList().get(this.selectedIndex);
			SuggestionChat.this.inputField.setSuggestion(calculateSuggestionSuffix(SuggestionChat.this.inputField.getText(), suggestion.apply(this.text)));
		}

		public void useSuggestion() {
			final Suggestion suggestion = this.suggestions.getList().get(this.selectedIndex);
			SuggestionChat.this.applyingSuggestion = true;
			SuggestionChat.this.inputField.setText(suggestion.apply(this.text));
			final int i = suggestion.getRange().getStart()+suggestion.getText().length();
			SuggestionChat.this.inputField.setCursorPosition(i);
			SuggestionChat.this.inputField.setSelectionPos(i);
			select(this.selectedIndex);
			SuggestionChat.this.applyingSuggestion = false;
			this.arrowKeyUsed = true;
		}

		public void hide() {
			SuggestionChat.this.suggestions = null;
		}
	}
}