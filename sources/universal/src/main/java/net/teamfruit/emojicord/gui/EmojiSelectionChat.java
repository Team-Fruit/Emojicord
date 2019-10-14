package net.teamfruit.emojicord.gui;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.teamfruit.emojicord.EmojicordConfig;
import net.teamfruit.emojicord.compat.Compat.CompatChatScreen;
import net.teamfruit.emojicord.compat.Compat.CompatFontRenderer;
import net.teamfruit.emojicord.compat.Compat.CompatMinecraft;
import net.teamfruit.emojicord.compat.Compat.CompatScreen;
import net.teamfruit.emojicord.compat.Compat.CompatTextFieldWidget;
import net.teamfruit.emojicord.emoji.DiscordEmojiIdDictionary;
import net.teamfruit.emojicord.emoji.EmojiFrequently;
import net.teamfruit.emojicord.emoji.EmojiId;
import net.teamfruit.emojicord.emoji.PickerGroup;
import net.teamfruit.emojicord.emoji.PickerItem;
import net.teamfruit.emojicord.emoji.StandardEmojiIdPicker;

public class EmojiSelectionChat implements IChatOverlay {
	public final CompatScreen screen;
	public final CompatChatScreen chatScreen;
	public final CompatTextFieldWidget inputField;
	public final CompatFontRenderer font;
	public int mouseX, mouseY;
	private EmojiSelectionList selectionList;
	private final Rectangle2d emojiButton;

	private final List<String> faces;
	private boolean onButton;
	private String face = ":smile:";

	public EmojiSelectionChat(final CompatChatScreen chatScreen) {
		this.screen = chatScreen.cast();
		this.chatScreen = chatScreen;
		this.font = CompatMinecraft.getMinecraft().getFontRenderer();
		this.inputField = chatScreen.getTextField();
		this.emojiButton = new Rectangle2d(this.screen.getWidth()-13, this.screen.getHeight()-13, 10, 10);

		this.faces = Lists.newArrayList();
		{
			final List<PickerGroup> standardCategories = StandardEmojiIdPicker.instance.categories;
			final List<PickerItem> people = standardCategories.stream().filter(e -> "PEOPLE".equalsIgnoreCase(e.name)).limit(1).flatMap(e -> e.items.stream()).collect(Collectors.toList());
			for (final PickerItem item : people) {
				this.faces.add(item.name);
				if (StringUtils.equals(":sleeping:", item.name))
					break;
			}
		}
	}

	@Override
	public boolean onDraw() {
		if (this.selectionList!=null)
			this.selectionList.onDraw();
		else {
			final boolean onButtonLast = this.onButton;
			this.onButton = this.emojiButton.contains(this.mouseX, this.mouseY);
			if (this.onButton&&!onButtonLast)
				this.face = this.faces.get(RandomUtils.nextInt(0, this.faces.size()));
		}

		this.font.drawString(this.face, this.emojiButton.getX(), this.emojiButton.getY(), 0xFFFFFF);
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
		final int width = 8+14*10-4+8;
		final int height = width;

		//EmojiFrequently.instance.load(Locations.instance.getEmojicordDirectory());
		final List<PickerGroup> standardCategories = StandardEmojiIdPicker.instance.categories;
		final List<PickerGroup> frequently = Lists.newArrayList(EmojiFrequently.instance.getGroup());
		final List<PickerGroup> discordCategories = DiscordEmojiIdDictionary.instance.pickerGroups;
		final List<PickerGroup> categories = Streams.concat(frequently.stream(), discordCategories.stream(), standardCategories.stream()).collect(Collectors.toList());
		final List<Pair<String, PickerGroup>> buttonCategories = ((Supplier<List<Pair<String, PickerGroup>>>) () -> {
			return Lists.newArrayList(
					Pair.of("<:frequently:630652521911943191>", frequently.stream().findFirst().orElse(null)),
					Pair.of("<:custom:630652548331864085>", discordCategories.stream().findFirst().orElse(null)),
					Pair.of("<:people:630652609807515658>", standardCategories.stream().filter(e -> "PEOPLE".equalsIgnoreCase(e.name)).findFirst().orElse(null)),
					Pair.of("<:nature:630652621497171979>", standardCategories.stream().filter(e -> "NATURE".equalsIgnoreCase(e.name)).findFirst().orElse(null)),
					Pair.of("<:food:630652671510183956>", standardCategories.stream().filter(e -> "FOOD".equalsIgnoreCase(e.name)).findFirst().orElse(null)),
					Pair.of("<:activities:630652683480465408>", standardCategories.stream().filter(e -> "ACTIVITIES".equalsIgnoreCase(e.name)).findFirst().orElse(null)),
					Pair.of("<:travel:630652707631267860>", standardCategories.stream().filter(e -> "TRAVEL".equalsIgnoreCase(e.name)).findFirst().orElse(null)),
					Pair.of("<:objects:630652735083249664>", standardCategories.stream().filter(e -> "OBJECTS".equalsIgnoreCase(e.name)).findFirst().orElse(null)),
					Pair.of("<:symbols:630652764955082752>", standardCategories.stream().filter(e -> "SYMBOLS".equalsIgnoreCase(e.name)).findFirst().orElse(null)),
					Pair.of("<:flags:630652781866385490>", standardCategories.stream().filter(e -> "FLAGS".equalsIgnoreCase(e.name)).findFirst().orElse(null)));
		}).get();
		this.selectionList = new EmojiSelectionList(this.screen.getWidth(), this.screen.getHeight()-12, width, height, categories, buttonCategories);
	}

	public void hide() {
		this.selectionList = null;
	}

	private class EmojiSelectionList {
		private final Rectangle2d rectangle;
		private final Rectangle2d rectTop;
		private final Rectangle2d rectInput;
		private final Rectangle2d rectInputField;
		private final Rectangle2d rectInputButton;
		private final Rectangle2d rectBottom;
		private final Rectangle2d rectMain;
		private final Rectangle2d rectColorButton;
		private final Rectangle2d rectColor;
		private final Rectangle2d rectSettingButton;

		private final List<PickerGroup> baseCategories;
		private final List<Pair<String, PickerGroup>> buttonCategories;
		private List<PickerGroup> categories;

		private TextFieldWidget searchField;
		private float scrollY;
		private int scrollY0;
		private int selectedGroupIndex = -1;
		private int selectedIndex = -1;
		private PickerItem selecting;
		private PickerGroup selectingGroupButton = null;
		private boolean colorShown;
		private int selectingColor = -1;
		private int selectedColor = -1;

		private EmojiSelectionList(final int posX, final int posY, final int width, final int height, final List<PickerGroup> categories, final List<Pair<String, PickerGroup>> buttonCategories) {
			this.rectangle = new Rectangle2d(posX-3-width, posY-4-height, width+1, height+1);
			this.selectedColor = EmojicordConfig.PICKER.skinTone.get();

			final int marginTop = 6;
			final int paddingTop = 2;
			final int bannerTopHeight = 12+(marginTop+paddingTop)*2;
			final int bannerBottomHeight = 16;
			final int colorButtonWidth = 12;
			final int settingButtonWidth = 12;
			final int searchButtonWidth = 12;
			this.rectTop = new Rectangle2d(this.rectangle.getX(), this.rectangle.getY(), this.rectangle.getWidth(), bannerTopHeight);
			this.rectInput = this.rectTop.inner(marginTop, marginTop, 4+2+colorButtonWidth+settingButtonWidth, marginTop);
			this.rectInputField = this.rectInput.inner(paddingTop+2, paddingTop+2, searchButtonWidth, 0);
			this.rectInputButton = new Rectangle2d(this.rectInput.getX()+this.rectInput.getWidth()-searchButtonWidth, this.rectInput.getY(), searchButtonWidth, this.rectInput.getHeight());
			this.rectBottom = new Rectangle2d(this.rectangle.getX(), this.rectangle.getY()+this.rectangle.getHeight()-bannerBottomHeight, this.rectangle.getWidth(), bannerBottomHeight);
			this.rectMain = new Rectangle2d(this.rectangle.getX(), this.rectTop.getY()+this.rectTop.getHeight(), this.rectangle.getWidth(), this.rectBottom.getY()-(this.rectTop.getY()+this.rectTop.getHeight()));
			this.rectSettingButton = new Rectangle2d(this.rectTop.getX()+this.rectTop.getWidth()-colorButtonWidth-4, this.rectInput.getY(), colorButtonWidth, this.rectInput.getHeight()).inner(0, 2, 0, 2);
			this.rectColorButton = new Rectangle2d(this.rectSettingButton.getX()-colorButtonWidth, this.rectInput.getY(), colorButtonWidth, this.rectInput.getHeight()).inner(0, 2, 0, 2);
			this.rectColor = new Rectangle2d(this.rectColorButton.getX(), this.rectColorButton.getY(), this.rectColorButton.getWidth(), 14*6);
			this.baseCategories = categories;
			this.buttonCategories = buttonCategories;
			this.categories = categories;

			select(0, 0);
			this.searchField = new TextFieldWidget(EmojiSelectionChat.this.font.getFontRendererObj(), this.rectInputField.getX(), this.rectInputField.getY(), this.rectInputField.getWidth(), this.rectInputField.getHeight(), "");
			this.searchField.setMaxStringLength(256);
			this.searchField.setEnableBackgroundDrawing(false);
			this.searchField.changeFocus(true);
			//this.inputField.setText("");
			//this.inputField.setTextFormatter(this::formatMessage);
			//this.inputField.func_212954_a(this::func_212997_a);

			onTextChanged();
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
								if (EmojiId.StandardEmojiId.fromAlias(StringUtils.strip(item.name, ":")+":skin-tone-"+this.selectedColor)!=null)
									tone = ":skin-tone-"+this.selectedColor+":";
							EmojiSelectionChat.this.font.drawString(item.name+tone, rect.getX()+emojiMargin, rect.getY()+emojiMargin, 0xFFFFFFFF);
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
					posY += (group.items.size()/row+(group.items.size()%row>0 ? 1 : 0))*spanY;
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

				final int[] colorIndex = Streams.concat(IntStream.of(this.selectedColor), IntStream.rangeClosed(0, 5).filter(e -> e!=this.selectedColor)).toArray();

				for (int pindex = 0; pindex<6; pindex++) {
					final int py = posY+pindex*spanY;
					final int index = colorIndex[pindex];
					final Rectangle2d rect = new Rectangle2d(this.rectColor.getX(), py, this.rectColor.getWidth(), spanY);
					EmojiSelectionChat.this.font.drawString(":ok_hand:"+(index>0 ? ":skin-tone-"+index+":" : ""), rect.getX()+colorOffset, rect.getY()+colorOffset, 0xFFFFFFFF);
					if (rect.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY))
						this.selectingColor = index;
				}
			}
			EmojiSelectionChat.this.font.drawString(":gear:", this.rectSettingButton.getX()+colorOffset, this.rectSettingButton.getY()+colorOffset, 0xFFFFFFFF);

			{
				PickerGroup currentGroup = null;
				{
					int height = 0;
					for (final PickerGroup group : this.categories) {
						height += titleSpanY+titleSpanY2+((group.items.size()-1)/row+1)*spanY;
						if (height+this.scrollY0>0) {
							currentGroup = group;
							break;
						}
					}
				}

				final int posX = this.rectBottom.getX()+paddingLeft;
				final int posY = this.rectBottom.getY()+2;
				int index = 0;
				this.selectingGroupButton = null;
				for (final Pair<String, PickerGroup> group : this.buttonCategories) {
					final int px = posX+index*spanX;
					final Rectangle2d rect = new Rectangle2d(px, posY, emojiSize, emojiSize).outer(emojiMargin, emojiMargin, emojiMargin, emojiMargin);
					{
						if (this.selectingGroupButton!=null&&this.selectingGroupButton==group.getRight())
							IChatOverlay.fill(rect, 0xFFEBEBEB);
						if (currentGroup==group.getRight())
							IChatOverlay.fill(new Rectangle2d(rect.getX(), rect.getY()+rect.getHeight(), rect.getWidth(), 2), 0xFF7289DA);
						EmojiSelectionChat.this.font.drawString(group.getLeft(), rect.getX()+emojiMargin, rect.getY()+emojiMargin, 0xFFFFFFFF);
						if (rect.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY))
							//if (!(this.selectingGroupButton==index))
							this.selectingGroupButton = group.getRight();
					}
					++index;
				}
			}

			final float partialTicks = 0.066f;
			this.searchField.render(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY, partialTicks);
			EmojiSelectionChat.this.font.drawString(this.searchField.getText().isEmpty() ? "<:search:631021534705877012>" : "<:close:631021519295741973>", this.rectInputButton.getX()+1, this.rectInputButton.getY()+3, 0xFFFFFFFF);

			return false;
		}

		public boolean onMouseClicked(final int button) {
			if (!this.rectangle.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
				hide();
				return true;
			}
			if (this.rectSettingButton.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
				if (EmojiSettings.showSettings!=null) {
					hide();
					EmojiSettings.showSettings.run();
				}
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
			if (this.rectInputButton.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)) {
				if (!this.searchField.getText().isEmpty())
					this.searchField.setText("");
				this.searchField.setFocused2(true);
				onTextChanged();
				return true;
			}
			if (this.searchField.mouseClicked(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY, button))
				return true;
			if (this.rectMain.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)&&this.selecting!=null) {
				String tone = "";
				if (this.selecting.id instanceof EmojiId.StandardEmojiId)
					if (this.selectedColor>0)
						if (EmojiId.StandardEmojiId.fromAlias(StringUtils.strip(this.selecting.name, ":")+":skin-tone-"+this.selectedColor)!=null)
							tone = ":skin-tone-"+this.selectedColor+":";
				EmojiSelectionChat.this.inputField.getTextFieldWidgetObj().writeText(this.selecting.name+tone+" ");
				hide();
				return true;
			}
			if (this.rectBottom.contains(EmojiSelectionChat.this.mouseX, EmojiSelectionChat.this.mouseY)&&this.selectingGroupButton!=null) {
				{
					final int row = 10;
					final int spanY = 14;
					final int titleSpanY = 4;
					final int titleSpanY2 = 12;

					{
						int height = 0;
						for (final PickerGroup group : this.categories) {
							if (group==this.selectingGroupButton) {
								this.scrollY0 = -height;
								break;
							}
							height += titleSpanY+titleSpanY2+((group.items.size()-1)/row+1)*spanY;
						}
					}
				}
				return true;
			}
			return true;
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
