package net.teamfruit.emojicord.compat;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.teamfruit.emojicord.CoreInvoke;

public class CompatGui {

	public static class CompatGuiNewChat {
		public static int getChatWidth(final GuiNewChat chat) {
			return chat.getChatWidth();
		}

		public static float getChatScale(final GuiNewChat chat) {
			return chat.getChatScale();
		}
	}

	public static class CompatScreen {
		private final GuiScreen screen;

		public CompatScreen(final GuiScreen screen) {
			this.screen = screen;
		}

		public CompatScreen(final CompatGuiConfig screen) {
			this.screen = screen;
		}

		public GuiScreen getScreenObj() {
			return this.screen;
		}

		public int getWidth() {
			return this.screen.width;
		}

		public int getHeight() {
			return this.screen.height;
		}

		public static boolean hasShiftDown() {
			return GuiScreen.isShiftKeyDown();
		}
	}

	public static class CompatChatScreen {
		private final GuiChat chatScreen;

		public CompatChatScreen(final GuiChat chatScreen) {
			this.chatScreen = chatScreen;
		}

		public CompatTextFieldWidget getTextField() {
			return new CompatTextFieldWidget(this.chatScreen.inputField);
		}

		public @Nonnull CompatScreen cast() {
			return new CompatScreen(this.chatScreen);
		}

		public static @Nullable CompatChatScreen cast(final CompatScreen screen) {
			if (screen.screen instanceof GuiChat)
				return new CompatChatScreen((GuiChat) screen.screen);
			return null;
		}
	}

	public static class CompatTextFieldWidget {
		private final GuiTextField textField;
		private final Compat.CompatFontRenderer font;

		public CompatTextFieldWidget(final GuiTextField inputField) {
			this.textField = inputField;
			this.font = Compat.CompatMinecraft.getMinecraft().getFontRenderer();
		}

		public CompatTextFieldWidget(final Compat.CompatFontRenderer font, final int x, final int y, final int width, final int height, final String title) {
			this(new GuiTextField(-1, font.getFontRendererObj(), x, y, width, height));
		}

		public GuiTextField getTextFieldWidgetObj() {
			return this.textField;
		}

		public String getText() {
			return this.textField.getText();
		}

		public void setText(final String apply) {
			this.textField.setText(apply);
		}

		public int getInsertPos(final int start) {
			final String text = this.textField.getText();
			if (start>text.length())
				return this.textField.xPosition;
			return this.textField.xPosition+this.font.getStringWidth(text.substring(0, start));
		}

		public void setSuggestion(final String string) {
			try {
				this.textField.getClass().getField("suggestion").set(this.textField, string);
			} catch (final ReflectiveOperationException e) {
				throw new RuntimeException("Could not set suggestion: ", e);
			}
		}

		public int getCursorPosition() {
			return this.textField.getCursorPosition();
		}

		public void setCursorPosition(final int i) {
			this.textField.setCursorPosition(i);
		}

		public void setSelectionPos(final int i) {
			this.textField.setSelectionPos(i);
		}

		public void setMaxStringLength(final int length) {
			this.textField.setMaxStringLength(length);
		}

		public void setEnableBackgroundDrawing(final boolean enabled) {
			this.textField.setEnableBackgroundDrawing(enabled);
		}

		public void changeFocus(final boolean active) {
			this.textField.setFocused(active);
		}

		public void setFocused(final boolean focused) {
			this.textField.setFocused(focused);
		}

		public boolean mouseClicked(final int mouseX, final int mouseY, final int button) {
			this.textField.mouseClicked(mouseX, mouseY, button);
			return this.textField.xPosition<=mouseX&&mouseX<=this.textField.xPosition+this.textField.width
					&&this.textField.yPosition<=mouseY&&mouseY<=this.textField.yPosition+this.textField.height;
		}

		public boolean charTyped(final char typed, final int keycode) {
			return this.textField.textboxKeyTyped(typed, keycode);
		}

		public boolean keyPressed(final int keycode, final int mouseX, final int mouseY) {
			return true; //this.textField.textboxKeyTyped(keycode, mouseX, mouseY);
		}

		public void render(final int mouseX, final int mouseY, final float partialTicks) {
			this.textField.drawTextBox();
		}

		public void tick() {
			this.textField.updateCursorCounter();
		}

		public void writeText(final String string) {
			this.textField.writeText(string);
		}

		@CoreInvoke
		public static void renderSuggestion(final FontRenderer font, final boolean flag, final String suggestion, final int posX, final int posY) {
			if (!flag&&suggestion!=null)
				font.drawStringWithShadow(suggestion, posX-1, posY, 0xFF808080);
		}
	}

	public static class CompatGuiConfig extends GuiConfig {
		public CompatGuiConfig(final CompatScreen parentScreen, final List<Compat.CompatConfigElement> configElements, final String modID, final boolean allRequireWorldRestart, final boolean allRequireMcRestart, final String title) {
			super(parentScreen.getScreenObj(), Compat.CompatConfigElement.getConfigElements(configElements), modID, allRequireWorldRestart, allRequireMcRestart, GuiConfig.getAbridgedConfigPath(title));
		}
	}

	public static abstract class CompatModGuiFactory implements IModGuiFactory {
		@Override
		public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
			return null;
		}

		@SuppressWarnings("deprecation")
		@Override
		public RuntimeOptionGuiHandler getHandlerFor(final RuntimeOptionCategoryElement element) {
			return null;
		}

		@Override
		public boolean hasConfigGui() {
			return mainConfigGuiClassCompat()!=null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public @Nullable Class<? extends GuiScreen> mainConfigGuiClass() {
			return (Class<? extends GuiScreen>) mainConfigGuiClassCompat();
		}

		public abstract @Nullable Class<?> mainConfigGuiClassCompat();

		@Override
		public GuiScreen createConfigGui(final GuiScreen parentScreen) {
			return createConfigGuiCompat(new CompatScreen(parentScreen)).screen;
		}

		public abstract CompatScreen createConfigGuiCompat(CompatScreen parentScreen);
	}

}
