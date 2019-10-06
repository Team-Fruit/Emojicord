package net.teamfruit.emojicord.gui;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.teamfruit.emojicord.compat.Compat.CompatChatScreen;
import net.teamfruit.emojicord.compat.Compat.CompatFontRenderer;
import net.teamfruit.emojicord.compat.Compat.CompatMinecraft;
import net.teamfruit.emojicord.compat.Compat.CompatScreen;
import net.teamfruit.emojicord.compat.Compat.CompatTextFieldWidget;
import net.teamfruit.emojicord.emoji.StandardEmojiIdPicker;
import net.teamfruit.emojicord.emoji.StandardEmojiIdPicker.PickerGroup;
import net.teamfruit.emojicord.emoji.StandardEmojiIdPicker.PickerItem;

public class EmojiSelectionChat implements IChatOverlay {
	public final CompatScreen screen;
	public final CompatChatScreen chatScreen;
	public final CompatTextFieldWidget inputField;
	public final CompatFontRenderer font;
	public int mouseX, mouseY;
	private EmojiSelectionList selectionList;
	private final Rectangle2d emojiButton;

	public EmojiSelectionChat(final CompatChatScreen chatScreen) {
		this.screen = chatScreen.cast();
		this.chatScreen = chatScreen;
		this.font = CompatMinecraft.getMinecraft().getFontRenderer();
		this.inputField = chatScreen.getTextField();
		this.emojiButton = new Rectangle2d(this.screen.getWidth()-13, this.screen.getHeight()-13, 10, 10);
	}

	@Override
	public boolean onDraw() {
		if (this.selectionList!=null)
			this.selectionList.onDraw();
		this.font.drawString(":smile:", this.emojiButton.getX(), this.emojiButton.getY(), 0xFFFFFF);
		return false;
	}

	@Override
	public boolean onMouseClicked(final int button) {
		if (this.selectionList==null&&this.emojiButton.contains(this.mouseX, this.mouseY)) {
			show();
			return true;
		}
		return this.selectionList!=null&&this.selectionList.onMouseClicked(button);
	}

	@Override
	public boolean onMouseScroll(final double scrollDelta) {
		return this.selectionList!=null&&this.selectionList.onMouseScroll(scrollDelta);
	}

	@Override
	public boolean onMouseInput(final int mouseX, final int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		return false;
	}

	@Override
	public boolean onCharTyped(final char typed, final int keycode) {
		return this.selectionList!=null&&this.selectionList.onCharTyped(typed, keycode);
	}

	@Override
	public boolean onKeyPressed(final int keycode) {
		return this.selectionList!=null&&this.selectionList.onKeyPressed(keycode);
	}

	@Override
	public void onTick() {
		if (this.selectionList!=null)
			this.selectionList.onTick();
	}

	public void show() {
		final List<PickerGroup> categories = StandardEmojiIdPicker.instance.categories;
		final int width = 8+14*10-4+8;
		final int height = width;
		this.selectionList = new EmojiSelectionList(this.screen.getWidth(), this.screen.getHeight()-12, width, height, categories);
	}

	public void hide() {
		this.selectionList = null;
	}

	private class EmojiSelectionList {
		private final Rectangle2d rectangle;
		private final Rectangle2d rectTop;
		private final Rectangle2d rectInput;
		private final Rectangle2d rectBottom;
		private final Rectangle2d rectMain;
		private final List<PickerGroup> categories;
		private float scrollY;
		private int scrollY0;
		private int selectedGroupIndex;
		private int selectedIndex;
		private PickerItem selecting;
		private TextFieldWidget searchField;

		private EmojiSelectionList(final int posX, final int posY, final int width, final int height, final List<PickerGroup> categories) {
			this.rectangle = new Rectangle2d(posX-3-width, posY-4-height, width+1, height+1);

			final int marginTop = 6;
			final int paddingTop = 2;
			final int bannerTopHeight = 12+(marginTop+paddingTop)*2;
			final int bannerBottomHeight = 16;
			this.rectTop = new Rectangle2d(this.rectangle.getX(), this.rectangle.getY(), this.rectangle.getWidth(), bannerTopHeight);
			this.rectInput = new Rectangle2d(this.rectTop.getX()+marginTop, this.rectTop.getY()+marginTop, this.rectTop.getWidth()-marginTop*2, this.rectTop.getHeight()-marginTop*2);
			this.rectBottom = new Rectangle2d(this.rectangle.getX(), this.rectangle.getY()+this.rectangle.getHeight()-bannerBottomHeight, this.rectangle.getWidth(), bannerBottomHeight);
			this.rectMain = new Rectangle2d(this.rectangle.getX(), this.rectTop.getY()+this.rectTop.getHeight(), this.rectangle.getWidth(), this.rectBottom.getY()-(this.rectTop.getY()+this.rectTop.getHeight()));

			this.categories = categories;
			select(0, 0);
			this.searchField = new TextFieldWidget(EmojiSelectionChat.this.font.getFontRendererObj(), this.rectInput.getX()+paddingTop+2, this.rectInput.getY()+paddingTop+2, this.rectInput.getWidth()-paddingTop-2, this.rectInput.getHeight()-paddingTop-2, "");
			this.searchField.setMaxStringLength(256);
			this.searchField.setEnableBackgroundDrawing(false);
			this.searchField.changeFocus(true);
			//this.inputField.setText("");
			//this.inputField.setTextFormatter(this::formatMessage);
			//this.inputField.func_212954_a(this::func_212997_a);
		}

		public boolean onDraw() {
			IChatOverlay.fill(this.rectangle.getX(), this.rectangle.getY(), this.rectangle.getX()+this.rectangle.getWidth(), this.rectangle.getY()+this.rectangle.getHeight(), 0xFFFFFFFF);

			{
				final int row = 10;
				final int emojiSize = 10;
				final int emojiMargin = 2;

				final int paddingLeft = 8;
				final int spanX = 14;
				final int spanY = 14;
				final int titleSpanY = 4;
				final int titleSpanY2 = 12;

				this.scrollY = MathHelper.lerp(this.scrollY, this.scrollY0, .5f);

				final int posX = this.rectMain.getX()+paddingLeft;
				int posY = this.rectMain.getY()+(int) this.scrollY;
				this.selecting = null;
				int groupIndex = 0;
				for (final PickerGroup group : this.categories) {
					posY += titleSpanY;
					if (this.rectMain.contains(this.rectMain.getX(), posY)||this.rectMain.contains(this.rectMain.getX()+this.rectMain.getWidth(), posY+titleSpanY2))
						EmojiSelectionChat.this.font.drawString(group.name, posX, posY, 0xFFABABAB);
					posY += titleSpanY2;
					int index = 0;
					for (final PickerItem item : group.items) {
						final int ix = index%row;
						final int iy = index/row;
						final int px = posX+ix*spanX;
						final int py = posY+iy*spanY;
						final Rectangle2d rect = new Rectangle2d(px-emojiMargin, py-emojiMargin, emojiSize+emojiMargin*2, emojiSize+emojiMargin*2);
						if (this.rectMain.overlap(rect)) {
							if (this.selectedGroupIndex==groupIndex&&this.selectedIndex==index)
								IChatOverlay.fill(rect.getX(), rect.getY(), rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight(), 0xFFEBEBEB);
							EmojiSelectionChat.this.font.drawString(":"+item.name+":", rect.getX()+emojiMargin, rect.getY()+emojiMargin, 0xFFFFFFFF);
							if (rect.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
								this.selecting = item;
								if (!(this.selectedGroupIndex==groupIndex&&this.selectedIndex==index)) {
									this.selectedGroupIndex = groupIndex;
									this.selectedIndex = index;
									if (!StringUtils.isNotEmpty(this.searchField.getText()))
										this.searchField.setSuggestion(StringUtils.replace(item.text, ":", "§r:"));
								}
							}
						}
						++index;
					}
					posY += ((index-1)/row+1)*spanY;
					++groupIndex;
					if (posY>this.rectMain.getY()+this.rectMain.getHeight())
						break;
				}
			}

			IChatOverlay.fill(this.rectTop.getX(), this.rectTop.getY(), this.rectTop.getX()+this.rectTop.getWidth(), this.rectTop.getY()+this.rectTop.getHeight(), 0xFFFFFFFF);
			IChatOverlay.fill(this.rectInput.getX(), this.rectInput.getY(), this.rectInput.getX()+this.rectInput.getWidth(), this.rectInput.getY()+this.rectInput.getHeight(), 0xFFABABAB);
			IChatOverlay.fill(this.rectBottom.getX(), this.rectBottom.getY(), this.rectBottom.getX()+this.rectBottom.getWidth(), this.rectBottom.getY()+this.rectBottom.getHeight(), 0xFFFFFFFF);
			IChatOverlay.fill(this.rectTop.getX(), this.rectTop.getY()+this.rectTop.getHeight(), this.rectTop.getX()+this.rectTop.getWidth(), this.rectTop.getY()+this.rectTop.getHeight()+1, 0xFFEBEBEB);
			IChatOverlay.fill(this.rectBottom.getX(), this.rectBottom.getY()-1, this.rectBottom.getX()+this.rectBottom.getWidth(), this.rectBottom.getY(), 0xFFEBEBEB);
			final float partialTicks = 0.066f;
			this.searchField.render(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY, partialTicks);
			return false;
		}

		public boolean onMouseClicked(final int button) {
			if (!this.rectangle.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
				hide();
				return true;
			}
			if (this.searchField.mouseClicked(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY, button))
				return true;
			if (this.rectMain.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)&&this.selecting!=null) {
				EmojiSelectionChat.this.inputField.getTextFieldWidgetObj().writeText(":"+this.selecting.name+": ");
				hide();
				return true;
			}
			return false;
		}

		public boolean onMouseScroll(final double scrollDelta) {
			if (this.rectMain.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
				this.scrollY0 -= Double.valueOf(0).compareTo(scrollDelta)*10;
				return true;
			}
			return false;
		}

		private void onTextChanged() {
			this.selectedGroupIndex = -1;
			this.selectedIndex = -1;
			if (StringUtils.isNotEmpty(this.searchField.getText()))
				this.searchField.setSuggestion("");
			else
				this.searchField.setSuggestion("Find the perfect emoji");
		}

		public boolean onCharTyped(final char typed, final int keycode) {
			if (this.searchField.charTyped(typed, keycode)) {
				onTextChanged();
				return true;
			}
			return false;
		}

		public boolean onKeyPressed(final int keycode) {
			if (this.searchField.keyPressed(keycode, EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
				onTextChanged();
				return true;
			}
			return false;
		}

		public void onTick() {
			this.searchField.tick();
		}

		public void select(final int groupIndex, final int index) {
			if (this.categories.isEmpty())
				return;
			this.selectedGroupIndex = MathHelper.clamp(groupIndex, 0, this.categories.size()-1);
			final List<PickerItem> list = this.categories.get(this.selectedGroupIndex).items;
			if (list.isEmpty())
				return;
			this.selectedIndex = MathHelper.clamp(index, 0, list.size()-1);
		}
	}
}
