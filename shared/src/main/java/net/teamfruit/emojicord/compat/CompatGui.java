package net.teamfruit.emojicord.compat;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.teamfruit.emojicord.CoreInvoke;

public class CompatGui {

	public static class CompatScreen {
		public static boolean hasShiftDown() {
			return GuiScreen.isShiftKeyDown();
		}
	}

	public static class CompatTextFieldWidget {
		public static int getInsertPos(GuiTextField textField, FontRenderer font, final int start) {
			final String text = textField.getText();
			int x = textField. #if MC_12_OR_LATER x #else xPosition #endif ;
			if (start > text.length())
				return x;
			return x + font.getStringWidth(text.substring(0, start));
		}

		public static void setSuggestion(GuiTextField textField, final String string) {
			try {
				textField.getClass().getField("suggestion").set(textField, string);
			} catch (final ReflectiveOperationException e) {
				throw new RuntimeException("Could not set suggestion: ", e);
			}
		}

		@CoreInvoke
		public static void renderSuggestion(final FontRenderer font, final boolean flag, final String suggestion, final int posX, final int posY) {
			if (!flag && suggestion != null)
				font.drawStringWithShadow(suggestion, posX - 1, posY, 0xFF808080);
		}
	}

}
