package net.teamfruit.emojicord.emoji;

import java.io.File;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.google.common.collect.Queues;

import net.teamfruit.emojicord.util.DataUtils;

public class EmojiFrequently {
	public static final @Nonnull EmojiFrequently instance = new EmojiFrequently();

	public static final int storeCount = 100;
	public static final int showCount = 40;

	public static class EmojiHistoryModel {
		public String[] history;
	}

	private Deque<PickerItem> frequently = Queues.newArrayDeque();
	private File save;
	private boolean changed;

	public void add(final PickerItem item) {
		this.frequently.addLast(item);
		if (this.frequently.size()>storeCount)
			this.frequently.removeFirst();
		this.changed = true;
	}

	public void use(final PickerItem item) {
		add(item);
	}

	public boolean hasChanged() {
		return this.changed;
	}

	public PickerGroup getGroup() {
		final Map<EmojiId, List<PickerItem>> group = this.frequently.stream().collect(Collectors.groupingBy(e -> e.id));
		final List<PickerItem> list = group.values().stream().filter(e -> !e.isEmpty()).sorted((a, b) -> Integer.compare(b.size(), a.size()))
				.limit(showCount).map(e -> e.get(0)).collect(Collectors.toList());
		return new PickerGroup("FREQUENTLY USED", list);
	}

	public void load(final File dictDir) {
		this.save = new File(dictDir, "history.json");
		;
		final EmojiHistoryModel model = DataUtils.loadFileIfExists(this.save, EmojiHistoryModel.class, "Emoji Frequently History");
		if (model!=null&&model.history!=null)
			Stream.of(model.history).flatMap(e -> PickerItem.fromText(EmojiText.createParsed(e)).stream()).forEach(this::use);
	}

	public void save() {
		if (this.save!=null) {
			final EmojiHistoryModel model = new EmojiHistoryModel();
			model.history = this.frequently.stream().map(e -> e.text).toArray(e -> new String[e]);
			DataUtils.saveFile(this.save, EmojiHistoryModel.class, model, "Emoji Frequently History");
		}
	}
}
