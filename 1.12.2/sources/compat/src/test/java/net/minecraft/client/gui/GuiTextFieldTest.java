package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamfruit.emojicord.compat.CompatGui;

@SideOnly(Side.CLIENT)
public class GuiTextFieldTest extends Gui {
	private final int id;
	private final FontRenderer fontRenderer;
	public int x;
	public int y;
	/** The width of this text field. */
	public int width;
	public int height;
	/** Has the current text being edited on the textbox. */
	private String text = "";
	private int maxStringLength = 32;
	private int cursorCounter;
	private boolean enableBackgroundDrawing = true;
	/** if true the textbox can lose focus by clicking elsewhere on the screen */
	private boolean canLoseFocus = true;
	/** If this value is true along with isEnabled, keyTyped will process the keys. */
	private boolean isFocused;
	/** If this value is true along with isFocused, keyTyped will process the keys. */
	private boolean isEnabled = true;
	/** The current character index that should be used as start of the rendered text. */
	private int lineScrollOffset;
	private int cursorPosition;
	/** other selection position, maybe the same as the cursor */
	private int selectionEnd;
	private int enabledColor = 14737632;
	private int disabledColor = 7368816;
	/** True if this textbox is visible */
	private boolean visible = true;
	private GuiPageButtonList.GuiResponder guiResponder;
	/** Called to check if the text is valid */
	private Predicate<String> validator = Predicates.<String> alwaysTrue();
	private String suggestion;

	public GuiTextFieldTest(final int componentId, final FontRenderer fontrendererObj, final int x, final int y, final int par5Width, final int par6Height) {
		this.id = componentId;
		this.fontRenderer = fontrendererObj;
		this.x = x;
		this.y = y;
		this.width = par5Width;
		this.height = par6Height;
	}

	/**
	 * Sets the GuiResponder associated with this text box.
	 */
	public void setGuiResponder(final GuiPageButtonList.GuiResponder guiResponderIn) {
		this.guiResponder = guiResponderIn;
	}

	/**
	 * Increments the cursor counter
	 */
	public void updateCursorCounter() {
		++this.cursorCounter;
	}

	/**
	 * Sets the text of the textbox, and moves the cursor to the end.
	 */
	public void setText(final String textIn) {
		if (this.validator.apply(textIn)) {
			if (textIn.length()>this.maxStringLength)
				this.text = textIn.substring(0, this.maxStringLength);
			else
				this.text = textIn;

			setCursorPositionEnd();
		}
	}

	/**
	 * Returns the contents of the textbox
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * returns the text between the cursor and selectionEnd
	 */
	public String getSelectedText() {
		final int i = this.cursorPosition<this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		final int j = this.cursorPosition<this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		return this.text.substring(i, j);
	}

	public void setValidator(final Predicate<String> theValidator) {
		this.validator = theValidator;
	}

	/**
	 * Adds the given text after the cursor, or replaces the currently selected text if there is a selection.
	 */
	public void writeText(final String textToWrite) {
		String s = "";
		final String s1 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
		final int i = this.cursorPosition<this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		final int j = this.cursorPosition<this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		final int k = this.maxStringLength-this.text.length()-(i-j);

		if (!this.text.isEmpty())
			s = s+this.text.substring(0, i);

		int l;

		if (k<s1.length()) {
			s = s+s1.substring(0, k);
			l = k;
		} else {
			s = s+s1;
			l = s1.length();
		}

		if (!this.text.isEmpty()&&j<this.text.length())
			s = s+this.text.substring(j);

		if (this.validator.apply(s)) {
			this.text = s;
			moveCursorBy(i-this.selectionEnd+l);
			setResponderEntryValue(this.id, this.text);
		}
	}

	/**
	 * Notifies this text box's {@linkplain GuiPageButtonList.GuiResponder responder} that the text has changed.
	 */
	public void setResponderEntryValue(final int idIn, final String textIn) {
		if (this.guiResponder!=null)
			this.guiResponder.setEntryValue(idIn, textIn);
	}

	/**
	 * Deletes the given number of words from the current cursor's position, unless there is currently a selection, in
	 * which case the selection is deleted instead.
	 */
	public void deleteWords(final int num) {
		if (!this.text.isEmpty())
			if (this.selectionEnd!=this.cursorPosition)
				writeText("");
			else
				deleteFromCursor(getNthWordFromCursor(num)-this.cursorPosition);
	}

	/**
	 * Deletes the given number of characters from the current cursor's position, unless there is currently a selection,
	 * in which case the selection is deleted instead.
	 */
	public void deleteFromCursor(final int num) {
		if (!this.text.isEmpty())
			if (this.selectionEnd!=this.cursorPosition)
				writeText("");
			else {
				final boolean flag = num<0;
				final int i = flag ? this.cursorPosition+num : this.cursorPosition;
				final int j = flag ? this.cursorPosition : this.cursorPosition+num;
				String s = "";

				if (i>=0)
					s = this.text.substring(0, i);

				if (j<this.text.length())
					s = s+this.text.substring(j);

				if (this.validator.apply(s)) {
					this.text = s;

					if (flag)
						moveCursorBy(num);

					setResponderEntryValue(this.id, this.text);
				}
			}
	}

	public int getId() {
		return this.id;
	}

	/**
	 * Gets the starting index of the word at the specified number of words away from the cursor position.
	 */
	public int getNthWordFromCursor(final int numWords) {
		return getNthWordFromPos(numWords, getCursorPosition());
	}

	/**
	 * Gets the starting index of the word at a distance of the specified number of words away from the given position.
	 */
	public int getNthWordFromPos(final int n, final int pos) {
		return getNthWordFromPosWS(n, pos, true);
	}

	/**
	 * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
	 */
	public int getNthWordFromPosWS(final int n, final int pos, final boolean skipWs) {
		int i = pos;
		final boolean flag = n<0;
		final int j = Math.abs(n);

		for (int k = 0; k<j; ++k)
			if (!flag) {
				final int l = this.text.length();
				i = this.text.indexOf(32, i);

				if (i==-1)
					i = l;
				else
					while (skipWs&&i<l&&this.text.charAt(i)==' ')
						++i;
			} else {
				while (skipWs&&i>0&&this.text.charAt(i-1)==' ')
					--i;

				while (i>0&&this.text.charAt(i-1)!=' ')
					--i;
			}

		return i;
	}

	/**
	 * Moves the text cursor by a specified number of characters and clears the selection
	 */
	public void moveCursorBy(final int num) {
		setCursorPosition(this.selectionEnd+num);
	}

	/**
	 * Sets the current position of the cursor.
	 */
	public void setCursorPosition(final int pos) {
		this.cursorPosition = pos;
		final int i = this.text.length();
		this.cursorPosition = MathHelper.clamp(this.cursorPosition, 0, i);
		setSelectionPos(this.cursorPosition);
	}

	/**
	 * Moves the cursor to the very start of this text box.
	 */
	public void setCursorPositionZero() {
		setCursorPosition(0);
	}

	/**
	 * Moves the cursor to the very end of this text box.
	 */
	public void setCursorPositionEnd() {
		setCursorPosition(this.text.length());
	}

	/**
	 * Call this method from your GuiScreen to process the keys into the textbox
	 */
	public boolean textboxKeyTyped(final char typedChar, final int keyCode) {
		if (!this.isFocused)
			return false;
		else if (GuiScreen.isKeyComboCtrlA(keyCode)) {
			setCursorPositionEnd();
			setSelectionPos(0);
			return true;
		} else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
			GuiScreen.setClipboardString(getSelectedText());
			return true;
		} else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
			if (this.isEnabled)
				writeText(GuiScreen.getClipboardString());

			return true;
		} else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
			GuiScreen.setClipboardString(getSelectedText());

			if (this.isEnabled)
				writeText("");

			return true;
		} else
			switch (keyCode) {
				case 14:

					if (GuiScreen.isCtrlKeyDown()) {
						if (this.isEnabled)
							deleteWords(-1);
					} else if (this.isEnabled)
						deleteFromCursor(-1);

					return true;
				case 199:

					if (GuiScreen.isShiftKeyDown())
						setSelectionPos(0);
					else
						setCursorPositionZero();

					return true;
				case 203:

					if (GuiScreen.isShiftKeyDown()) {
						if (GuiScreen.isCtrlKeyDown())
							setSelectionPos(getNthWordFromPos(-1, getSelectionEnd()));
						else
							setSelectionPos(getSelectionEnd()-1);
					} else if (GuiScreen.isCtrlKeyDown())
						setCursorPosition(getNthWordFromCursor(-1));
					else
						moveCursorBy(-1);

					return true;
				case 205:

					if (GuiScreen.isShiftKeyDown()) {
						if (GuiScreen.isCtrlKeyDown())
							setSelectionPos(getNthWordFromPos(1, getSelectionEnd()));
						else
							setSelectionPos(getSelectionEnd()+1);
					} else if (GuiScreen.isCtrlKeyDown())
						setCursorPosition(getNthWordFromCursor(1));
					else
						moveCursorBy(1);

					return true;
				case 207:

					if (GuiScreen.isShiftKeyDown())
						setSelectionPos(this.text.length());
					else
						setCursorPositionEnd();

					return true;
				case 211:

					if (GuiScreen.isCtrlKeyDown()) {
						if (this.isEnabled)
							deleteWords(1);
					} else if (this.isEnabled)
						deleteFromCursor(1);

					return true;
				default:

					if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
						if (this.isEnabled)
							writeText(Character.toString(typedChar));

						return true;
					} else
						return false;
			}
	}

	/**
	 * Called when mouse is clicked, regardless as to whether it is over this button or not.
	 */
	public boolean mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		final boolean flag = mouseX>=this.x&&mouseX<this.x+this.width&&mouseY>=this.y&&mouseY<this.y+this.height;

		if (this.canLoseFocus)
			setFocused(flag);

		if (this.isFocused&&flag&&mouseButton==0) {
			int i = mouseX-this.x;

			if (this.enableBackgroundDrawing)
				i -= 4;

			final String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), getWidth());
			setCursorPosition(this.fontRenderer.trimStringToWidth(s, i).length()+this.lineScrollOffset);
			return true;
		} else
			return false;
	}

	/**
	 * Draws the textbox
	 */
	public void drawTextBox() {
		if (getVisible()) {
			if (getEnableBackgroundDrawing()) {
				drawRect(this.x-1, this.y-1, this.x+this.width+1, this.y+this.height+1, -6250336);
				drawRect(this.x, this.y, this.x+this.width, this.y+this.height, -16777216);
			}

			final int i = this.isEnabled ? this.enabledColor : this.disabledColor;
			final int j = this.cursorPosition-this.lineScrollOffset;
			int k = this.selectionEnd-this.lineScrollOffset;
			final String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), getWidth());
			final boolean flag = j>=0&&j<=s.length();
			final boolean flag1 = this.isFocused&&this.cursorCounter/6%2==0&&flag;
			final int l = this.enableBackgroundDrawing ? this.x+4 : this.x;
			final int i1 = this.enableBackgroundDrawing ? this.y+(this.height-8)/2 : this.y;
			int j1 = l;

			if (k>s.length())
				k = s.length();

			if (!s.isEmpty()) {
				final String s1 = flag ? s.substring(0, j) : s;
				j1 = this.fontRenderer.drawStringWithShadow(s1, l, i1, i);
			}

			final boolean flag2 = this.cursorPosition<this.text.length()||this.text.length()>=getMaxStringLength();
			int k1 = j1;

			if (!flag)
				k1 = j>0 ? l+this.width : l;
			else if (flag2) {
				k1 = j1-1;
				--j1;
			}

			if (!s.isEmpty()&&flag&&j<s.length())
				j1 = this.fontRenderer.drawStringWithShadow(s.substring(j), j1, i1, i);
			CompatGui.CompatTextFieldWidget.renderSuggestion(this.fontRenderer, flag2, this.suggestion, k1, i1);
			if (flag1)
				if (flag2)
					Gui.drawRect(k1, i1-1, k1+1, i1+1+this.fontRenderer.FONT_HEIGHT, -3092272);
				else
					this.fontRenderer.drawStringWithShadow("_", k1, i1, i);

			if (k!=j) {
				final int l1 = l+this.fontRenderer.getStringWidth(s.substring(0, k));
				drawSelectionBox(k1, i1-1, l1-1, i1+1+this.fontRenderer.FONT_HEIGHT);
			}
		}
	}

	public void drawTextBox170() {
		if (getVisible()) {
			if (getEnableBackgroundDrawing()) {
				drawRect(this.x-1, this.y-1, this.x+this.width+1, this.y+this.height+1, -6250336);
				drawRect(this.x, this.y, this.x+this.width, this.y+this.height, -16777216);
			}

			final int i = this.isEnabled ? this.enabledColor : this.disabledColor;
			final int j = this.cursorPosition-this.lineScrollOffset;
			int k = this.selectionEnd-this.lineScrollOffset;
			final String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), getWidth());
			final boolean flag = j>=0&&j<=s.length();
			final boolean flag1 = this.isFocused&&this.cursorCounter/6%2==0&&flag;
			final int l = this.enableBackgroundDrawing ? this.x+4 : this.x;
			final int i1 = this.enableBackgroundDrawing ? this.y+(this.height-8)/2 : this.y;
			int j1 = l;

			if (k>s.length())
				k = s.length();

			if (s.length()>0) {
				final String s1 = flag ? s.substring(0, j) : s;
				j1 = this.fontRenderer.drawStringWithShadow(s1, l, i1, i);
			}

			final boolean flag2 = this.cursorPosition<this.text.length()||this.text.length()>=getMaxStringLength();
			int k1 = j1;

			if (!flag)
				k1 = j>0 ? l+this.width : l;
			else if (flag2) {
				k1 = j1-1;
				--j1;
			}

			if (s.length()>0&&flag&&j<s.length())
				this.fontRenderer.drawStringWithShadow(s.substring(j), j1, i1, i);

			if (flag1)
				if (flag2)
					Gui.drawRect(k1, i1-1, k1+1, i1+1+this.fontRenderer.FONT_HEIGHT, -3092272);
				else
					this.fontRenderer.drawStringWithShadow("_", k1, i1, i);

			if (k!=j) {
				final int l1 = l+this.fontRenderer.getStringWidth(s.substring(0, k));
				drawSelectionBox(k1, i1-1, l1-1, i1+1+this.fontRenderer.FONT_HEIGHT);
			}
		}
	}

	public void drawTextBoxIntelliInput() {
		if (getVisible()) {
			if (getEnableBackgroundDrawing()) {
				drawRect(this.x-1, this.y-1, this.x+this.width+1, this.y+this.height+1, -6250336);
				drawRect(this.x, this.y, this.x+this.width, this.y+this.height, -16777216);
			}
			final String drawText = getDrawText();
			final int i = this.isEnabled ? this.enabledColor : this.enabledColor;
			final int j = this.cursorPosition-this.lineScrollOffset;
			int k = this.selectionEnd-this.lineScrollOffset;
			final String s = this.fontRenderer.trimStringToWidth(drawText.substring(this.lineScrollOffset), getWidth());
			final boolean flag = j>=0&&j<=s.length();
			final boolean flag2 = this.isFocused&&this.cursorCounter/6%2==0&&flag;
			final int l = this.enableBackgroundDrawing ? this.x+4 : this.x;
			final int i2 = this.enableBackgroundDrawing ? this.y+(this.height-8)/2 : this.y;
			int j2 = l;
			if (k>s.length())
				k = s.length();
			if (!s.isEmpty()) {
				final String s2 = flag ? s.substring(0, j) : s;
				j2 = this.fontRenderer.drawStringWithShadow(s2, l, i2, i);
			}
			final boolean flag3 = this.cursorPosition<this.text.length()||this.text.length()>=getMaxStringLength();
			int k2 = j2;
			if (!flag)
				k2 = j>0 ? l+this.width : l;
			else if (flag3) {
				k2 = j2-1;
				--j2;
			}
			if (!s.isEmpty()&&flag&&j<s.length())
				j2 = this.fontRenderer.drawStringWithShadow(s.substring(j), j2, i2, i);
			if (flag2)
				if (flag3)
					Gui.drawRect(k2, i2-1, k2+1, i2+1+this.fontRenderer.FONT_HEIGHT, -3092272);
				else
					this.fontRenderer.drawStringWithShadow("_", k2, i2, i);
			CompatGui.CompatTextFieldWidget.renderSuggestion(this.fontRenderer, flag3, this.suggestion, k2, i2);
			drawCandidateList(drawText);
			if (k!=j) {
				final int l2 = l+this.fontRenderer.getStringWidth(s.substring(0, k));
				drawSelectionBox(k2, i2-1, l2-1, i2+1+this.fontRenderer.FONT_HEIGHT);
			}
		}
	}

	private String getDrawText() {
		return null;
	}

	private void drawCandidateList(final String text) {

	}

	/**
	 * Draws the blue selection box.
	 */
	private void drawSelectionBox(int startX, int startY, int endX, int endY) {
		if (startX<endX) {
			final int i = startX;
			startX = endX;
			endX = i;
		}

		if (startY<endY) {
			final int j = startY;
			startY = endY;
			endY = j;
		}

		if (endX>this.x+this.width)
			endX = this.x+this.width;

		if (startX>this.x+this.width)
			startX = this.x+this.width;

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(startX, endY, 0.0D).endVertex();
		bufferbuilder.pos(endX, endY, 0.0D).endVertex();
		bufferbuilder.pos(endX, startY, 0.0D).endVertex();
		bufferbuilder.pos(startX, startY, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture2D();
	}

	/**
	 * Sets the maximum length for the text in this text box. If the current text is longer than this length, the
	 * current text will be trimmed.
	 */
	public void setMaxStringLength(final int length) {
		this.maxStringLength = length;

		if (this.text.length()>length)
			this.text = this.text.substring(0, length);
	}

	/**
	 * returns the maximum number of character that can be contained in this textbox
	 */
	public int getMaxStringLength() {
		return this.maxStringLength;
	}

	/**
	 * returns the current position of the cursor
	 */
	public int getCursorPosition() {
		return this.cursorPosition;
	}

	/**
	 * Gets whether the background and outline of this text box should be drawn (true if so).
	 */
	public boolean getEnableBackgroundDrawing() {
		return this.enableBackgroundDrawing;
	}

	/**
	 * Sets whether or not the background and outline of this text box should be drawn.
	 */
	public void setEnableBackgroundDrawing(final boolean enableBackgroundDrawingIn) {
		this.enableBackgroundDrawing = enableBackgroundDrawingIn;
	}

	/**
	 * Sets the color to use when drawing this text box's text. A different color is used if this text box is disabled.
	 */
	public void setTextColor(final int color) {
		this.enabledColor = color;
	}

	/**
	 * Sets the color to use for text in this text box when this text box is disabled.
	 */
	public void setDisabledTextColour(final int color) {
		this.disabledColor = color;
	}

	/**
	 * Sets focus to this gui element
	 */
	public void setFocused(final boolean isFocusedIn) {
		if (isFocusedIn&&!this.isFocused)
			this.cursorCounter = 0;

		this.isFocused = isFocusedIn;

		final GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
		if (currentScreen!=null)
			currentScreen.setFocused(isFocusedIn);
	}

	/**
	 * Getter for the focused field
	 */
	public boolean isFocused() {
		return this.isFocused;
	}

	/**
	 * Sets whether this text box is enabled. Disabled text boxes cannot be typed in.
	 */
	public void setEnabled(final boolean enabled) {
		this.isEnabled = enabled;
	}

	/**
	 * the side of the selection that is not the cursor, may be the same as the cursor
	 */
	public int getSelectionEnd() {
		return this.selectionEnd;
	}

	/**
	 * returns the width of the textbox depending on if background drawing is enabled
	 */
	public int getWidth() {
		return getEnableBackgroundDrawing() ? this.width-8 : this.width;
	}

	/**
	 * Sets the position of the selection anchor (the selection anchor and the cursor position mark the edges of the
	 * selection). If the anchor is set beyond the bounds of the current text, it will be put back inside.
	 */
	public void setSelectionPos(int position) {
		final int i = this.text.length();

		if (position>i)
			position = i;

		if (position<0)
			position = 0;

		this.selectionEnd = position;

		if (this.fontRenderer!=null) {
			if (this.lineScrollOffset>i)
				this.lineScrollOffset = i;

			final int j = getWidth();
			final String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
			final int k = s.length()+this.lineScrollOffset;

			if (position==this.lineScrollOffset)
				this.lineScrollOffset -= this.fontRenderer.trimStringToWidth(this.text, j, true).length();

			if (position>k)
				this.lineScrollOffset += position-k;
			else if (position<=this.lineScrollOffset)
				this.lineScrollOffset -= this.lineScrollOffset-position;

			this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i);
		}
	}

	/**
	 * Sets whether this text box loses focus when something other than it is clicked.
	 */
	public void setCanLoseFocus(final boolean canLoseFocusIn) {
		this.canLoseFocus = canLoseFocusIn;
	}

	/**
	 * returns true if this textbox is visible
	 */
	public boolean getVisible() {
		return this.visible;
	}

	/**
	 * Sets whether or not this textbox is visible
	 */
	public void setVisible(final boolean isVisible) {
		this.visible = isVisible;
	}
}