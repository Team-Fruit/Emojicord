package net.teamfruit.emojicord.emoji;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import net.teamfruit.emojicord.Locations;
import net.teamfruit.emojicord.Reference;
import net.teamfruit.emojicord.util.Base64Utils;

public abstract class EmojiId {
	public abstract String getId();

	public abstract String getType();

	public File getCache() {
		return new File(Locations.instance.getCacheDirectory(), String.format("%s/%s", getType(), getCacheName()));
	}

	public abstract String getRemote();

	public String getCacheName() {
		return getId();
	}

	public ResourceLocation getResourceLocation() {
		return new ResourceLocation(Reference.MODID, String.format("textures/emojis/%s/%s", getType(), getCacheName()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getType());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this==obj)
			return true;
		if (obj==null)
			return false;
		if (!(obj instanceof EmojiId))
			return false;
		final EmojiId other = (EmojiId) obj;
		return Objects.equals(getId(), other.getId())&&Objects.equals(getType(), other.getType());
	}

	@Override
	public String toString() {
		return "EmojiId [id="+getId()+", type="+getType()+"]";
	}

	public static class StandardEmojiId extends EmojiId {
		public static final Map<String, EmojiId> EMOJI_DICTIONARY = Maps.newHashMap();
		public static final Map<String, EmojiId> EMOJI_UTF_DICTIONARY = Maps.newHashMap();
		private static final Pattern EMOJI_SHORT_FILTER_NOT = Pattern.compile(".+\\:skin-tone-\\d");
		private static final Pattern EMOJI_SHORT_FILTER = Pattern.compile("^.*[^\\w].*$");
		public static final Supplier<Set<String>> EMOJI_SHORT = Suppliers.memoize(() -> EMOJI_DICTIONARY.keySet().stream()
				.filter(str -> {
					return !EMOJI_SHORT_FILTER_NOT.matcher(str).matches()&&EMOJI_SHORT_FILTER.matcher(str).matches();
				}).collect(Collectors.toSet()));
		public static final Supplier<Pattern> EMOJI_SHORT_PATTERN = Suppliers.memoize(() -> {
			final List<String> emoticons = Lists.newArrayList(EMOJI_SHORT.get());
			//List of emotions should be pre-processed to handle instances of subtrings like :-) :-
			//Without this pre-processing, emoticons in a string won't be processed properly
			for (int i = 0; i<emoticons.size(); i++)
				for (int j = i+1; j<emoticons.size(); j++) {
					final String o1 = emoticons.get(i);
					final String o2 = emoticons.get(j);
					if (o2.contains(o1)) {
						final String temp = o2;
						emoticons.remove(j);
						emoticons.add(i, temp);
					}
				}
			final String emojiFilter = emoticons.stream().map(Pattern::quote).collect(Collectors.joining("|"));
			return Pattern.compile(String.format("(?<=^| )(?:%s)(?= |$)", emojiFilter));
		});
		private static final Pattern EMOJI_UTF_FILTER = Pattern.compile(".+[\uD83C\uDFFB-\uD83C\uDFFF]$");
		public static final Supplier<Set<String>> EMOJI_UTF = Suppliers.memoize(() -> EMOJI_UTF_DICTIONARY.keySet().stream()
				.filter(str -> {
					return !EMOJI_UTF_FILTER.matcher(str).matches();
				}).collect(Collectors.toSet()));
		public static final Supplier<Pattern> EMOJI_UTF_PATTERN = Suppliers.memoize(() -> {
			final String emojiFilter = EMOJI_UTF.get().stream().map(Pattern::quote).collect(Collectors.joining("|"));
			final String toneFilter = "[\uD83C\uDFFB-\uD83C\uDFFF]";
			return Pattern.compile(String.format("(?:%s)%s?", emojiFilter, toneFilter));
		});

		private final String url;
		private final String cache;

		public StandardEmojiId(final String url, final String cache) {
			this.url = url;
			this.cache = cache;
		}

		@Override
		public String getId() {
			return this.url;
		}

		@Override
		public String getCacheName() {
			return this.cache;
		}

		@Override
		public String getType() {
			return "standard";
		}

		@Override
		public String getRemote() {
			return getId();
		}

		public static @Nullable EmojiId fromEndpoint(final String id) {
			return EMOJI_DICTIONARY.get(id);
		}

		public static @Nullable EmojiId fromEndpointUtf(final String surrogates) {
			return EMOJI_UTF_DICTIONARY.get(surrogates);
		}
	}

	public static class DiscordEmojiId extends EmojiId {
		private final long id;

		private DiscordEmojiId(final long id) {
			this.id = id;
		}

		@Override
		public String getId() {
			return Long.toString(this.id);
		}

		@Override
		public String getType() {
			return "discord";
		}

		@Override
		public String getRemote() {
			return "https://cdn.discordapp.com/emojis/"+getId();
		}

		public String getEncodedId() {
			return Base64Utils.encode(this.id);
		}

		public static @Nullable EmojiId fromDecimalId(final long id) {
			return new DiscordEmojiId(id);
		}

		public static @Nullable EmojiId fromDecimalId(final String id) {
			if (!NumberUtils.isCreatable(id))
				return null;
			return DiscordEmojiId.fromDecimalId(NumberUtils.toLong(id));
		}

		public static @Nullable EmojiId fromEncodedId(final String id) {
			try {
				return new DiscordEmojiId(Base64Utils.decode(id));
			} catch (final IllegalArgumentException e) {
			}
			return null;
		}
	}
}
