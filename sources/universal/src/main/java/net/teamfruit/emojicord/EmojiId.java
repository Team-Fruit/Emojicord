package net.teamfruit.emojicord;

import java.io.File;
import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;

import net.minecraft.util.ResourceLocation;

public abstract class EmojiId {
	public abstract String getId();

	public abstract String getType();

	public File getCache() {
		return new File(String.format("%s/cache/%s/%s", Reference.MODID, getType(), getId()));
	}

	public abstract String getRemote();

	public ResourceLocation getResourceLocation() {
		return new ResourceLocation(Reference.MODID, String.format("textures/emojis/%s/%s", getType(), getId()));
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
		private final String id;

		private StandardEmojiId(final String id) {
			this.id = id;
		}

		@Override
		public String getId() {
			return this.id;
		}

		@Override
		public String getType() {
			return "standard";
		}

		@Override
		public String getRemote() {
			return "https://cdn.discordapp.com/emojis/" + getId();
		}

		public static DiscordEmojiId fromDecimalId(final String id) {
			return new DiscordEmojiId(NumberUtils.toLong(id));
		}

		public static DiscordEmojiId fromBase62Id(final String id) {
			return new DiscordEmojiId(Base62.decode(id));
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

		public static DiscordEmojiId fromDecimalId(final String id) {
			return new DiscordEmojiId(NumberUtils.toLong(id));
		}

		public static DiscordEmojiId fromBase62Id(final String id) {
			return new DiscordEmojiId(Base62.decode(id));
		}
	}
}
