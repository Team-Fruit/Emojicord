package net.teamfruit.emojicord;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.teamfruit.emojicord.compat.Compat.CompatMinecraft;
import net.teamfruit.emojicord.compat.CompatEvents.CompatClientChatEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatConfigChangedEvent.CompatOnConfigChangedEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatGuiScreenEvent;
import net.teamfruit.emojicord.compat.CompatEvents.CompatHandler;
import net.teamfruit.emojicord.compat.CompatEvents.CompatRenderGameOverlayEvent;
import net.teamfruit.emojicord.emoji.DiscordEmojiIdDictionary;
import net.teamfruit.emojicord.emoji.EmojiId;
import net.teamfruit.emojicord.emoji.EmojiText;
import net.teamfruit.emojicord.emoji.StandardEmojiIdDictionary;

public class EventHandler extends CompatHandler {
	@Override
	public void onChat(final CompatClientChatEvent event) {
		if (!event.getMessage().startsWith("/")) {
			final EmojiText emojiText = EmojiText.createEncoded(event.getMessage());
			event.setMessage(emojiText.getEncoded());
		}
	}

	@Override
	public void onDraw(final CompatRenderGameOverlayEvent.CompatPost event) {
	}

	@Override
	public void onDraw(final CompatGuiScreenEvent.CompatDrawScreenEvent.CompatPost event) {
		/*
		OpenGL.glPushMatrix();
		OpenGL.glPushAttrib();
		OpenGL.glDisable(GL11.GL_TEXTURE_2D);
		OpenGL.glColor4f(1f, 1f, 1f, 1f);
		//final CompatBaseVertex t = CompatVertex.getTessellator();
		//final Tessellator tes = Tessellator.getInstance();
		//final BufferBuilder w = tes.getBuffer();
		{
			final float x = event.getMouseX();
			final float y = event.getMouseY();
			//t.setTranslation(0, 0, 0);
			//t.begin(GL11.GL_LINE_LOOP);
			//t.pos(x, y+10, 10).color(1f, 0f, 0f, 1f);
			//t.pos(x+10, y+10, 10).color(0f, 1f, 0f, 1f);
			//t.pos(x+10, y, 10).color(0f, 0f, 0f, 1f);
			//t.pos(x, y, 10).color(1f, 1f, 0f, 1f);
			//t.draw();
			//t.begin(GL11.GL_LINE_LOOP);
			//t.pos(x, y+10, 10);
			//t.pos(x+10, y+10, 10);
			//t.pos(x+10, y, 10);
			//t.pos(x, y, 10);
			//t.draw();
			OpenGL.glBegin(GL11.GL_QUADS);
			OpenGL.glVertex3f(x, y+10, 0);
			OpenGL.glVertex3f(x+10, y+10, 0);
			OpenGL.glVertex3f(x+10, y, 0);
			OpenGL.glVertex3f(x, y, 0);
			OpenGL.glEnd();
			//w.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			//w.pos(x, y+10, 10).color(1f, 0f, 0f, 1f).endVertex();
			//w.pos(x+10, y+10, 10).color(0f, 1f, 0f, 1f).endVertex();
			//w.pos(x+10, y, 10).color(0f, 0f, 0f, 1f).endVertex();
			//w.pos(x, y, 10).color(1f, 1f, 0f, 1f).endVertex();
			//tes.draw();
		}
		OpenGL.glPopAttrib();
		OpenGL.glPopMatrix();
		*/

		//final Screen screen = event.getGui().getScreenObj();
		//if (screen instanceof ChatScreen) {
		//	final CompatFontRenderer font = CompatMinecraft.getMinecraft().getFontRenderer();
		//	try {
		//		final Field inputfield = screen.getClass().getDeclaredField("inputField");
		//		inputfield.setAccessible(true);
		//		final TextFieldWidget input = (TextFieldWidget) inputfield.get(screen);
		//		font.drawString(input.getText(), 10, 10, 0xff00ff);
		//	} catch (NoSuchFieldException|SecurityException|IllegalArgumentException|IllegalAccessException e) {
		//		// TODO 自動生成された catch ブロック
		//		e.printStackTrace();
		//	}
		//}

		final Screen screen = event.getGui().getScreenObj();
		if (screen instanceof ChatScreen) {
			final ChatScreen chatScreen = (ChatScreen) screen;
			if (this.chat==null||this.chat.chatScreen!=chatScreen)
				this.chat = new SuggestionChat(chatScreen);
			this.chat.mouseX = event.getMouseX();
			this.chat.mouseY = event.getMouseY();
			if (this.chat.suggestions!=null)
				this.chat.suggestions.render(this.chat.mouseX, this.chat.mouseY);
			this.chat.checkTextUpdate();
		} else
			this.chat = null;
	}

	@Override
	public void onText(final CompatRenderGameOverlayEvent.CompatText event) {
	}

	@Override
	public void onConfigChanged(final CompatOnConfigChangedEvent event) {
	}

	@SubscribeEvent
	public void click(final GuiScreenEvent.MouseClickedEvent e) {
		if (this.chat!=null&&this.chat.suggestions!=null&&this.chat.suggestions.mouseClicked(this.chat.mouseX, this.chat.mouseY, e.getButton()))
			e.setCanceled(true);
	}

	@SubscribeEvent
	public void click(final GuiScreenEvent.MouseScrollEvent e) {
		if (this.chat!=null&&this.chat.suggestions!=null&&this.chat.suggestions.mouseScrolled(e.getScrollDelta(), this.chat.mouseX, this.chat.mouseY))
			e.setCanceled(true);
	}

	@SubscribeEvent
	public void mouse(final GuiScreenEvent.MouseInputEvent e) {

	}

	@SubscribeEvent
	public void press(final GuiScreenEvent.KeyboardKeyPressedEvent e) {
		if (this.chat!=null&&this.chat.suggestions!=null&&this.chat.suggestions.keyPressed(e.getKeyCode(), this.chat.mouseX, this.chat.mouseY))
			e.setCanceled(true);
	}

	@SubscribeEvent
	public void type(final GuiScreenEvent.KeyboardCharTypedEvent e) {

	}

	private SuggestionChat chat;

	public static interface IEmojiSuggestionProvider {
		EmojiId getEmojiId();
	}

	public static class EmojiSuggestionProvider implements IEmojiSuggestionProvider {
		private final EmojiId emojiId;

		public EmojiSuggestionProvider(final EmojiId emojiId) {
			this.emojiId = emojiId;
		}

		@Override
		public EmojiId getEmojiId() {
			return this.emojiId;
		}
	}

	private static class SuggestionChat {
		private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
		public ChatScreen chatScreen;
		public TextFieldWidget inputField;
		public FontRenderer font;
		public Minecraft minecraft;
		public int mouseX, mouseY;
		public Supplier<Boolean> hasEdits;
		private SuggestionsList suggestions;
		private CompletableFuture<Suggestions> pendingSuggestions;
		private ParseResults<IEmojiSuggestionProvider> currentParse;
		private boolean applyingSuggestion;
		protected final List<String> commandUsage = Lists.newArrayList();
		private String inputFieldTextLast;
		private CommandDispatcher<IEmojiSuggestionProvider> emojiSuggestionDispatcher = new CommandDispatcher<>();
		private IEmojiSuggestionProvider emojiSuggestionProvider = new EmojiSuggestionProvider(null);

		public SuggestionChat(final ChatScreen screen) {
			this.minecraft = CompatMinecraft.getMinecraft().getMinecraftObj();
			this.chatScreen = screen;
			this.font = CompatMinecraft.getMinecraft().getFontRenderer().getFontRendererObj();
			try {
				final Field inputfield = screen.getClass().getDeclaredField("inputField");
				inputfield.setAccessible(true);
				this.inputField = (TextFieldWidget) inputfield.get(screen);

				final Field hasEditsField = screen.getClass().getDeclaredField("hasEdits");
				hasEditsField.setAccessible(true);
				this.hasEdits = LamdbaExceptionUtils.rethrowSupplier(() -> (Boolean) hasEditsField.get(screen));
			} catch (final ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
			{
				final RootCommandNode<IEmojiSuggestionProvider> root = new RootCommandNode<>();
				root.addChild(LiteralArgumentBuilder.<IEmojiSuggestionProvider> literal(":sushi:").build());
				this.emojiSuggestionDispatcher = new CommandDispatcher<>(root);
			}
		}

		public void checkTextUpdate() {
			final String inputFieldText = this.inputField.getText();
			if (!StringUtils.equals(this.inputFieldTextLast, inputFieldText)) {
				this.inputFieldTextLast = inputFieldText;
				updateSuggestion();
			}
		}

		private static int getLastWordIndex(final String p_208603_0_) {
			if (Strings.isNullOrEmpty(p_208603_0_))
				return 0;
			else {
				int i = 0;

				for (final Matcher matcher = WHITESPACE_PATTERN.matcher(p_208603_0_); matcher.find(); i = matcher.end())
					;

				return i;
			}
		}

		public void showSuggestions() {
			if (this.pendingSuggestions!=null&&this.pendingSuggestions.isDone()) {
				int i = 0;
				final Suggestions suggestions = this.pendingSuggestions.join();
				if (!suggestions.getList().isEmpty()) {
					for (final Suggestion suggestion : suggestions.getList())
						i = Math.max(i, this.font.getStringWidth(":"+suggestion.getText()+": "+suggestion.getText()));

					final int j = MathHelper.clamp(this.inputField.func_195611_j(suggestions.getRange().getStart()), 0, this.chatScreen.width-i);
					this.suggestions = new SuggestionsList(j+300, this.chatScreen.height-12, i, suggestions);
				}
			}

		}

		private void updateSuggestion() {
			final String s = this.inputField.getText();
			if (this.currentParse!=null&&!this.currentParse.getReader().getString().equals(s))
				this.currentParse = null;

			if (!this.applyingSuggestion) {
				this.inputField.setSuggestion((String) null);
				this.suggestions = null;
			}

			//this.commandUsage.clear();
			final StringReader stringreader = new StringReader(s);
			if (stringreader.canRead()) {
				//stringreader.skip();
				final CommandDispatcher<IEmojiSuggestionProvider> commanddispatcher = this.emojiSuggestionDispatcher;
				if (this.currentParse==null)
					this.currentParse = commanddispatcher.parse(stringreader, this.emojiSuggestionProvider);

				final int j = this.inputField.getCursorPosition();
				if (j>=3&&(this.suggestions==null||!this.applyingSuggestion)) {
					//this.pendingSuggestions = commanddispatcher.getCompletionSuggestions(this.currentParse, j);
					final CompletableFuture<Iterable<String>> list = CompletableFuture.supplyAsync(() -> Iterables.concat(
							StandardEmojiIdDictionary.StandardEmojiIdRepository.instance.nameDictionary.keySet(),
							DiscordEmojiIdDictionary.instance.get().keySet()));

					this.pendingSuggestions = list.thenApplyAsync(e -> suggest(e, new SuggestionsBuilder(s, getLastWordIndex(s))));
					this.pendingSuggestions.thenRun(() -> {
						if (this.pendingSuggestions.isDone())
							updateUsageInfo();
					});
				}
				//} else {
				//	final int i = getLastWordIndex(s);
				//	final Collection<String> collection = this.minecraft.player.connection.getSuggestionProvider().getPlayerNames();
				//	this.pendingSuggestions = ISuggestionProvider.suggest(collection, new SuggestionsBuilder(s, i));
			}
		}

		private static Suggestions suggest(final Iterable<String> p_197005_0_, final SuggestionsBuilder p_197005_1_) {
			final String s = p_197005_1_.getRemaining().toLowerCase(Locale.ROOT);

			for (final String s1 : p_197005_0_)
				if ((":"+s1.toLowerCase(Locale.ROOT)+":").startsWith(s))
					p_197005_1_.suggest(s1);

			final Suggestions result = p_197005_1_.build();
			return result;
		}

		private void updateUsageInfo() {
			if (this.pendingSuggestions.join().isEmpty()&&!this.currentParse.getExceptions().isEmpty()&&this.inputField.getCursorPosition()==this.inputField.getText().length()) {
				int i = 0;

				for (final Entry<CommandNode<IEmojiSuggestionProvider>, CommandSyntaxException> entry : this.currentParse.getExceptions().entrySet()) {
					final CommandSyntaxException commandsyntaxexception = entry.getValue();
					if (commandsyntaxexception.getType()==CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect())
						++i;
					else
						this.commandUsage.add(commandsyntaxexception.getMessage());
				}

				if (i>0)
					this.commandUsage.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
			}

			if (this.commandUsage.isEmpty())
				fillNodeUsage(TextFormatting.GRAY);

			this.suggestions = null;
			if (this.hasEdits.get()&&this.minecraft.gameSettings.autoSuggestCommands)
				showSuggestions();

		}

		private void fillNodeUsage(final TextFormatting p_195132_1_) {
			final CommandContextBuilder<IEmojiSuggestionProvider> commandcontextbuilder = this.currentParse.getContext();
			final SuggestionContext<IEmojiSuggestionProvider> suggestioncontext = commandcontextbuilder.findSuggestionContext(this.inputField.getCursorPosition());
			final Map<CommandNode<IEmojiSuggestionProvider>, String> map = this.emojiSuggestionDispatcher.getSmartUsage(suggestioncontext.parent, this.emojiSuggestionProvider);
			final List<String> list = Lists.newArrayList();
			int i = 0;

			for (final Entry<CommandNode<IEmojiSuggestionProvider>, String> entry : map.entrySet())
				if (!(entry.getKey() instanceof LiteralCommandNode)) {
					list.add(p_195132_1_+entry.getValue());
					i = Math.max(i, this.font.getStringWidth(entry.getValue()));
				}
		}

		private static String calculateSuggestionSuffix(final String p_208602_0_, final String p_208602_1_) {
			return p_208602_1_.startsWith(p_208602_0_) ? p_208602_1_.substring(p_208602_0_.length()) : null;
		}

		private class SuggestionsList {
			private final Rectangle2d rectangle;
			private final Suggestions suggestions;
			private final String text;
			private int scrollY;
			private int selectedIndex;
			private Vec2f lastMouse = Vec2f.ZERO;
			private boolean arrowKeyUsed;

			private SuggestionsList(final int posX, final int posY, final int width, final Suggestions suggestions) {
				this.rectangle = new Rectangle2d(posX-1, posY-3-Math.min(suggestions.getList().size(), 10)*12, width+1, Math.min(suggestions.getList().size(), 10)*12);
				this.suggestions = suggestions;
				this.text = SuggestionChat.this.inputField.getText();
				select(0);
			}

			public void render(final int mouseX, final int mouseY) {
				final int i = Math.min(this.suggestions.getList().size(), 10);
				final int j = -5592406;
				final boolean flag = this.scrollY>0;
				final boolean flag1 = this.suggestions.getList().size()>this.scrollY+i;
				final boolean flag2 = flag||flag1;
				final boolean flag3 = this.lastMouse.x!=mouseX||this.lastMouse.y!=mouseY;
				if (flag3)
					this.lastMouse = new Vec2f(mouseX, mouseY);

				if (flag2) {
					AbstractGui.fill(this.rectangle.getX(), this.rectangle.getY()-1, this.rectangle.getX()+this.rectangle.getWidth(), this.rectangle.getY(), 0xD0000000);
					AbstractGui.fill(this.rectangle.getX(), this.rectangle.getY()+this.rectangle.getHeight(), this.rectangle.getX()+this.rectangle.getWidth(), this.rectangle.getY()+this.rectangle.getHeight()+1, 0xD0000000);
					if (flag)
						for (int k = 0; k<this.rectangle.getWidth(); ++k)
							if (k%2==0)
								AbstractGui.fill(this.rectangle.getX()+k, this.rectangle.getY()-1, this.rectangle.getX()+k+1, this.rectangle.getY(), -1);

					if (flag1)
						for (int i1 = 0; i1<this.rectangle.getWidth(); ++i1)
							if (i1%2==0)
								AbstractGui.fill(this.rectangle.getX()+i1, this.rectangle.getY()+this.rectangle.getHeight(), this.rectangle.getX()+i1+1, this.rectangle.getY()+this.rectangle.getHeight()+1, -1);
				}

				boolean flag4 = false;

				for (int l = 0; l<i; ++l) {
					final Suggestion suggestion = this.suggestions.getList().get(l+this.scrollY);
					AbstractGui.fill(this.rectangle.getX(), this.rectangle.getY()+12*l, this.rectangle.getX()+this.rectangle.getWidth(), this.rectangle.getY()+12*l+12, 0xD0000000);
					if (mouseX>this.rectangle.getX()&&mouseX<this.rectangle.getX()+this.rectangle.getWidth()&&mouseY>this.rectangle.getY()+12*l&&mouseY<this.rectangle.getY()+12*l+12) {
						if (flag3)
							select(l+this.scrollY);

						flag4 = true;
					}

					SuggestionChat.this.font.drawStringWithShadow(":"+suggestion.getText()+": "+suggestion.getText(), this.rectangle.getX()+1, this.rectangle.getY()+2+12*l, l+this.scrollY==this.selectedIndex ? -256 : j);
				}

				if (flag4) {
					final Message message = this.suggestions.getList().get(this.selectedIndex).getTooltip();
					if (message!=null)
						;//renderTooltip(TextComponentUtils.toTextComponent(message).getFormattedText(), p_198500_1_, p_198500_2_);
				}

			}

			public boolean mouseClicked(final int mouseX, final int mouseY, final int clickButton) {
				if (!this.rectangle.contains(mouseX, mouseY))
					return false;
				else {
					final int i = (mouseY-this.rectangle.getY())/12+this.scrollY;
					if (i>=0&&i<this.suggestions.getList().size()) {
						select(i);
						useSuggestion();
					}

					return true;
				}
			}

			public boolean mouseScrolled(final double wheelScroll, final int mouseX, final int mouseY) {
				if (this.rectangle.contains(mouseX, mouseY)) {
					this.scrollY = MathHelper.clamp((int) (this.scrollY-wheelScroll), 0, Math.max(this.suggestions.getList().size()-10, 0));
					return true;
				} else
					return false;
			}

			public boolean keyPressed(final int keyTyped, final int mouseX, final int mouseY) {
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
						cycle(Screen.hasShiftDown() ? -1 : 1);

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
				//ChatScreen.this.field_212338_z = true;
				SuggestionChat.this.inputField.setText(suggestion.apply(this.text));
				final int i = suggestion.getRange().getStart()+suggestion.getText().length();
				SuggestionChat.this.inputField.func_212422_f(i);
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
}
