package net.teamfruit.emojicord.gui;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.teamfruit.emojicord.EmojicordConfig;
import net.teamfruit.emojicord.compat.Compat.CompatChatScreen;
import net.teamfruit.emojicord.compat.Compat.CompatFontRenderer;
import net.teamfruit.emojicord.compat.Compat.CompatMinecraft;
import net.teamfruit.emojicord.compat.Compat.CompatScreen;
import net.teamfruit.emojicord.compat.Compat.CompatTextFieldWidget;
import net.teamfruit.emojicord.emoji.EmojiId;
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
		private final Rectangle2d rectColorButton;
		private final Rectangle2d rectColor;
		private TextFieldWidget searchField;
		private final List<PickerGroup> baseCategories;
		private List<PickerGroup> categories;
		private float scrollY;
		private int scrollY0;
		private int selectedGroupIndex = -1;
		private int selectedIndex = -1;
		private PickerItem selecting;
		private int selectingGroupButton = -1;
		private boolean colorShown;
		private int selectingColor = -1;
		private int selectedColor = -1;

		/*
		public class Layout {
			private final int row = 10;
			private final int emojiSize = 10;
			private final int emojiMargin = 2;
		
			private final int paddingLeft = 8;
			private final int spanX = 14;
			private final int spanY = 14;
			private final int titleSpanY = 4;
			private final int titleSpanY2 = 12;
		
			public final Rectangle2d rectLayout;
		
			public class Group {
				public final Rectangle2d rectGroup;
				public final PickerGroup group;
				public final List<Item> children;
		
				public Group(final PickerGroup group) {
					this.children = Streams.mapWithIndex(group.items.stream(), (i, e) -> new Item((int) e, i)).collect(Collectors.toList());
					this.rectGroup = new Rectangle2d(Layout.this.rectLayout.getX(), posY, Layout.this.rectLayout.getWidth(), Layout.this.emojiSize+Layout.this.emojiMargin*2);
				}
		
				public class Item {
					public final Rectangle2d rectItem;
					public final PickerItem item;
		
					public Item(final int index, final PickerItem item) {
						final int ix = index%Layout.this.row;
						final int px = ix*Layout.this.spanX;
						this.rectItem = new Rectangle2d(px-Layout.this.emojiMargin, Group.this.rectGroup.getY(), Layout.this.emojiSize+Layout.this.emojiMargin*2, Group.this.rectGroup.getHeight());
						this.item = item;
					}
				}
			}
		}
		*/

		private EmojiSelectionList(final int posX, final int posY, final int width, final int height, final List<PickerGroup> categories) {
			this.rectangle = new Rectangle2d(posX-3-width, posY-4-height, width+1, height+1);
			this.selectedColor = EmojicordConfig.PICKER.skinTone.get();

			final int marginTop = 6;
			final int paddingTop = 2;
			final int bannerTopHeight = 12+(marginTop+paddingTop)*2;
			final int bannerBottomHeight = 16;
			final int colorButtonWidth = 12;
			this.rectTop = new Rectangle2d(this.rectangle.getX(), this.rectangle.getY(), this.rectangle.getWidth(), bannerTopHeight);
			this.rectInput = this.rectTop.inner(marginTop, marginTop, 4+2+colorButtonWidth, marginTop);
			this.rectBottom = new Rectangle2d(this.rectangle.getX(), this.rectangle.getY()+this.rectangle.getHeight()-bannerBottomHeight, this.rectangle.getWidth(), bannerBottomHeight);
			this.rectMain = new Rectangle2d(this.rectangle.getX(), this.rectTop.getY()+this.rectTop.getHeight(), this.rectangle.getWidth(), this.rectBottom.getY()-(this.rectTop.getY()+this.rectTop.getHeight()));
			this.rectColorButton = new Rectangle2d(this.rectTop.getX()+this.rectTop.getWidth()-colorButtonWidth-4, this.rectInput.getY(), colorButtonWidth, this.rectInput.getHeight()).inner(0, 2, 0, 2);
			this.rectColor = new Rectangle2d(this.rectColorButton.getX(), this.rectColorButton.getY(), this.rectColorButton.getWidth(), 14*6);

			this.baseCategories = categories;
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
			IChatOverlay.fill(this.rectangle, 0xFFFFFFFF);

			final int row = 10;
			final int emojiSize = 10;
			final int emojiMargin = 2;

			final int paddingLeft = 8;
			final int spanX = 14;
			final int spanY = 14;
			final int titleSpanY = 4;
			final int titleSpanY2 = 12;

			final int scrollbarWidth = 6;

			{
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
								IChatOverlay.fill(rect, 0xFFEBEBEB);
							String tone = "";
							if (this.selectedColor>0)
								if (EmojiId.StandardEmojiId.fromAlias(item.name+":skin-tone-"+this.selectedColor)!=null)
									tone = ":skin-tone-"+this.selectedColor+":";
							EmojiSelectionChat.this.font.drawString(":"+item.name+":"+tone, rect.getX()+emojiMargin, rect.getY()+emojiMargin, 0xFFFFFFFF);
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
					posY += ((group.items.size()-1)/row+1)*spanY;
					++groupIndex;
					if (posY>this.rectMain.getY()+this.rectMain.getHeight())
						break;
				}
				final int height = this.categories.stream().mapToInt(e -> titleSpanY+titleSpanY2+((e.items.size()-1)/row+1)*spanY).sum();
				this.scrollY0 = height<=this.rectMain.getHeight() ? 0 : -MathHelper.clamp(-this.scrollY0, 0, height-this.rectMain.getHeight());

				final Rectangle2d rectScroll0 = new Rectangle2d(this.rectMain.getX()+this.rectMain.getWidth()-scrollbarWidth, this.rectMain.getY(), scrollbarWidth, this.rectMain.getHeight());
				final Rectangle2d rectScroll = rectScroll0.inner(1, 2, 1, 2);
				final int scrollbarHeight = Math.min(rectScroll.getHeight(), rectScroll.getHeight()*rectScroll.getHeight()/height);
				IChatOverlay.fill(rectScroll, 0xFFEBEBEB);
				if (height>this.rectMain.getHeight()) {
					IChatOverlay.fill(new Rectangle2d(
							rectScroll.getX(),
							rectScroll.getY()+(int) MathHelper.lerp(0, rectScroll.getHeight()-scrollbarHeight, -this.scrollY/(height-this.rectMain.getHeight())),
							rectScroll.getWidth(),
							scrollbarHeight), 0xFFABABAB);
					if (rectScroll0.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY))
						if (GLFW.glfwGetMouseButton(CompatMinecraft.getMinecraft().getMinecraftObj().mainWindow.getHandle(), GLFW.GLFW_MOUSE_BUTTON_1)==GLFW.GLFW_PRESS)
							this.scrollY0 = (int) -MathHelper.lerp(0, height-this.rectMain.getHeight(), ((float) EmojiSelectionChat.this.mouseY-rectScroll.getY())/rectScroll.getHeight());
				}
			}

			IChatOverlay.fill(this.rectTop, 0xFFFFFFFF);
			IChatOverlay.fill(this.rectInput, 0xFFABABAB);
			IChatOverlay.fill(this.rectBottom, 0xFFFFFFFF);
			IChatOverlay.fill(this.rectTop.getX(), this.rectTop.getY()+this.rectTop.getHeight(), this.rectTop.getX()+this.rectTop.getWidth(), this.rectTop.getY()+this.rectTop.getHeight()+1, 0xFFEBEBEB);
			IChatOverlay.fill(this.rectBottom.getX(), this.rectBottom.getY()-1, this.rectBottom.getX()+this.rectBottom.getWidth(), this.rectBottom.getY(), 0xFFEBEBEB);

			final int colorOffset = 1;
			EmojiSelectionChat.this.font.drawString(":ok_hand:"+(this.selectedColor>0 ? ":skin-tone-"+this.selectedColor+":" : ""), this.rectColorButton.getX()+colorOffset, this.rectColorButton.getY()+colorOffset, 0xFFFFFFFF);
			if (this.colorShown) {
				IChatOverlay.fill(this.rectColor, 0xFFEFEFEF);

				final int posY = this.rectColor.getY();
				this.selectingColor = -1;
				for (int index = 0; index<6; index++) {
					final int py = posY+index*spanY;
					final Rectangle2d rect = new Rectangle2d(this.rectColor.getX(), py, this.rectColor.getWidth(), spanY);
					EmojiSelectionChat.this.font.drawString(":ok_hand:"+(index>0 ? ":skin-tone-"+index+":" : ""), rect.getX()+colorOffset, rect.getY()+colorOffset, 0xFFFFFFFF);
					if (rect.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY))
						this.selectingColor = index;
				}
			}

			{
				int count = -1;
				{
					int index = 0;
					int height = 0;
					for (final PickerGroup group : this.categories) {
						height += titleSpanY+titleSpanY2+((group.items.size()-1)/row+1)*spanY;
						if (height+this.scrollY0>0) {
							count = index;
							break;
						}
						++index;
					}
				}

				final int posX = this.rectBottom.getX()+paddingLeft;
				final int posY = this.rectBottom.getY()+2;
				int index = 0;
				this.selectingGroupButton = -1;
				for (final PickerGroup group : this.categories) {
					final int px = posX+index*spanX;
					final Rectangle2d rect = new Rectangle2d(px, posY, emojiSize, emojiSize).outer(emojiMargin, emojiMargin, emojiMargin, emojiMargin);
					{
						if (this.selectingGroupButton==index)
							IChatOverlay.fill(rect, 0xFFEBEBEB);
						if (count==index)
							IChatOverlay.fill(new Rectangle2d(rect.getX(), rect.getY()+rect.getHeight(), rect.getWidth(), 2), 0xFF0000FF);
						EmojiSelectionChat.this.font.drawString(":"+"sushi"+":", rect.getX()+emojiMargin, rect.getY()+emojiMargin, 0xFFFFFFFF);
						if (rect.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY))
							//if (!(this.selectingGroupButton==index))
							this.selectingGroupButton = index;
					}
					++index;
				}
			}

			final float partialTicks = 0.066f;
			this.searchField.render(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY, partialTicks);

			return false;
		}

		public boolean onMouseClicked(final int button) {
			if (!this.rectangle.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
				hide();
				return true;
			}
			if (!this.colorShown) {
				if (this.rectColorButton.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
					this.colorShown = true;
					return true;
				}
			} else {
				if (this.rectColor.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
					if (this.selectingColor>=0)
						this.selectedColor = this.selectingColor;
					EmojicordConfig.PICKER.skinTone.set(this.selectedColor);
				}
				this.colorShown = false;
				return true;
			}
			if (this.searchField.mouseClicked(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY, button))
				return true;
			if (this.rectMain.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)&&this.selecting!=null) {
				String tone = "";
				if (this.selectedColor>0)
					if (EmojiId.StandardEmojiId.fromAlias(this.selecting.name+":skin-tone-"+this.selectedColor)!=null)
						tone = ":skin-tone-"+this.selectedColor+":";
				EmojiSelectionChat.this.inputField.getTextFieldWidgetObj().writeText(":"+this.selecting.name+":"+tone+" ");
				hide();
				return true;
			}
			if (this.rectBottom.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)&&this.selectingGroupButton>=0) {
				{
					final int row = 10;
					final int spanY = 14;
					final int titleSpanY = 4;
					final int titleSpanY2 = 12;

					{
						int index = 0;
						int height = 0;
						for (final PickerGroup group : this.categories) {
							if (index==this.selectingGroupButton) {
								this.scrollY0 = -height;
								break;
							}
							height += titleSpanY+titleSpanY2+((group.items.size()-1)/row+1)*spanY;
							++index;
						}
					}
				}
				return true;
			}
			return false;
		}

		public boolean onMouseScroll(final double scrollDelta) {
			if (this.rectMain.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
				this.scrollY0 += scrollDelta*10;
				return true;
			}
			return false;
		}

		private void onTextChanged() {
			this.selectedGroupIndex = -1;
			this.selectedIndex = -1;
			if (StringUtils.isNotEmpty(this.searchField.getText())) {
				this.searchField.setSuggestion("");
				final String searchText = StringUtils.strip(this.searchField.getText(), ":");
				final List<PickerItem> candidates = this.baseCategories.stream().flatMap(e -> e.items.stream()).filter(e -> e.alias.stream().anyMatch(s -> s.contains(searchText))).collect(Collectors.toList());
				this.categories = Lists.newArrayList(new PickerGroup("Search", candidates));
			} else {
				this.searchField.setSuggestion("Find the perfect emoji");
				this.categories = this.baseCategories;
			}
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
