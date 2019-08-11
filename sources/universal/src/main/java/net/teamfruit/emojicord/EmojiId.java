package net.teamfruit.emojicord;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang3.math.NumberUtils;

import net.minecraft.util.ResourceLocation;

public abstract class EmojiId {
	public abstract String getId();

	public abstract String getType();

	public File getCache() {
		return new File(EmojicordFile.instance.getCacheDirectory(), String.format("%s/%s", getType(), getCacheName()));
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EmojiId))
			return false;
		final EmojiId other = (EmojiId) obj;
		return Objects.equals(getId(), other.getId()) && Objects.equals(getType(), other.getType());
	}

	@Override
	public String toString() {
		return "EmojiId [id=" + getId() + ", type=" + getType() + "]";
	}

	public static class StandardEmojiId extends EmojiId {
		public static final Map<String, EmojiId> EMOJI_DICTIONARY = new HashMap<>();

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
			return "https://cdn.discordapp.com/emojis/" + getId();
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
