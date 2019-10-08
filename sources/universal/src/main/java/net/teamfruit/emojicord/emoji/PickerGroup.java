package net.teamfruit.emojicord.emoji;

import java.util.List;

public class PickerGroup {
	public final String name;
	public final List<PickerItem> items;

	public PickerGroup(final String name, final List<PickerItem> items) {
		this.name = name;
		this.items = items;
	}
}