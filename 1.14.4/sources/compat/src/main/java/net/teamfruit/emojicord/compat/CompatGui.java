package net.teamfruit.emojicord.compat;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.IModGuiFactory;

public class CompatGui {

	public static class CompatGuiNewChat {
		public static int getChatWidth(final NewChatGui chat) {
			return chat.getChatWidth();
		}

		public static float getChatScale(final NewChatGui chat) {
			return (float) chat.getScale();
		}
	}

	public static class CompatScreen {
		private final Screen screen;

		public CompatScreen(final Screen screen) {
			this.screen = screen;
		}

		public CompatScreen(final CompatGuiConfig screen) {
			this.screen = screen;
		}

		public Screen getScreenObj() {
			return this.screen;
		}

		public int getWidth() {
			return this.screen.width;
		}

		public int getHeight() {
			return this.screen.height;
		}

		public static boolean hasShiftDown() {
			return Screen.hasShiftDown();
		}
	}

	public static class CompatChatScreen {
		private final ChatScreen chatScreen;

		public CompatChatScreen(final ChatScreen chatScreen) {
			this.chatScreen = chatScreen;
		}

		public CompatTextFieldWidget getTextField() {
			return new CompatTextFieldWidget(this.chatScreen.inputField);
		}

		public @Nonnull CompatScreen cast() {
			return new CompatScreen(this.chatScreen);
		}

		public static @Nullable CompatChatScreen cast(final CompatScreen screen) {
			if (screen.screen instanceof ChatScreen)
				return new CompatChatScreen((ChatScreen) screen.screen);
			return null;
		}
	}

	public static class CompatTextFieldWidget {
		private final TextFieldWidget textField;

		public CompatTextFieldWidget(final TextFieldWidget textField) {
			this.textField = textField;
		}

		public CompatTextFieldWidget(final Compat.CompatFontRenderer font, final int x, final int y, final int width, final int height, final String title) {
			this(new TextFieldWidget(font.getFontRendererObj(), x, y, width, height, title));
		}

		public TextFieldWidget getTextFieldWidgetObj() {
			return this.textField;
		}

		public String getText() {
			return this.textField.getText();
		}

		public void setText(final String apply) {
			this.textField.setText(apply);
		}

		public int getInsertPos(final int start) {
			return this.textField.func_195611_j(start);
		}

		public void setSuggestion(final String string) {
			this.textField.setSuggestion(string);
		}

		public int getCursorPosition() {
			return this.textField.getCursorPosition();
		}

		public void setCursorPosition(final int i) {
			this.textField.func_212422_f(i);
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
			this.textField.changeFocus(active);
		}

		public void setFocused(final boolean focused) {
			this.textField.setFocused2(focused);
		}

		public boolean mouseClicked(final int mouseX, final int mouseY, final int button) {
			return this.textField.mouseClicked(mouseX, mouseY, button);
		}

		public boolean charTyped(final char typed, final int keycode) {
			return this.textField.charTyped(typed, keycode);
		}

		public boolean keyPressed(final int keycode, final int mouseX, final int mouseY) {
			return this.textField.keyPressed(keycode, mouseX, mouseY);
		}

		public void render(final int mouseX, final int mouseY, final float partialTicks) {
			this.textField.render(mouseX, mouseY, partialTicks);
		}

		public void tick() {
			this.textField.tick();
		}

		public void writeText(final String string) {
			this.textField.writeText(string);
		}
	}

	public static class CompatGuiConfig extends Screen {
		public CompatGuiConfig(final CompatScreen parentScreen, final List<CompatConfig.CompatConfigElement> configElements, final String modID, final boolean allRequireWorldRestart, final boolean allRequireMcRestart, final String title) {
			super(new StringTextComponent(title));
		}
	}

	public static abstract class CompatModGuiFactory implements IModGuiFactory {
		@Override
		public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
			return null;
		}

		@Override
		public boolean hasConfigGui() {
			return mainConfigGuiClassCompat()!=null;
		}

		public abstract @Nullable Class<?> mainConfigGuiClassCompat();

		@Override
		public Screen createConfigGui(final Screen parentScreen) {
			return createConfigGuiCompat(new CompatScreen(parentScreen)).screen;
		}

		public abstract CompatScreen createConfigGuiCompat(CompatScreen parentScreen);
	}

}
