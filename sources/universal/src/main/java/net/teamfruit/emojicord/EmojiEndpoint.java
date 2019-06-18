package net.teamfruit.emojicord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

import com.esotericsoftware.yamlbeans.YamlReader;

public class EmojiEndpoint {
	public static final String EMOJI_GATEWAY = "https://raw.githubusercontent.com/Team-Fruit/Emojicord/api/api.yml";
	public static EmojiGateway EMOJI_API;
	public static List<String> EMOJI_WEB_ENDPOINT;
	public static final Map<String, EmojiId> EMOJI_DICTIONARY = new HashMap<>();

	public static class EmojiGateway {
		public List<String> emojis;
		public List<String> api;
	}

	private static class EmojiStandard {
		public String location;
		public String name;
		public List<String> strings;
	}

	private static class EmojiStandardList {
		public List<EmojiStandard> emojis;
		public String location;
	}

	private static YamlReader getYaml(final String url)
			throws IllegalArgumentException, IllegalStateException, IOException {
		final HttpUriRequest req = new HttpGet(url);
		final HttpClientContext context = HttpClientContext.create();
		final HttpResponse response = Downloader.downloader.client.execute(req, context);
		final HttpEntity entity = response.getEntity();

		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK)
			throw new HttpResponseException(statusCode, "Invalid status code: " + url);

		return new YamlReader(
				new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8));
	}

	public static boolean loadGateway() {
		YamlReader reader = null;
		try {
			reader = getYaml(EMOJI_GATEWAY);
			EMOJI_API = reader.read(EmojiGateway.class);
			return true;
		} catch (final Exception e) {
			Log.log.warn("Failed to load Emojicord API: ", e);
		} finally {
			if (reader != null)
				IOUtils.closeQuietly(reader::close);
		}
		return false;
	}

	public static void loadStandardEmojis() {
		final Map<String, EmojiId> dict = new HashMap<>();
		for (final String emojiUrls : EMOJI_API.api) {
			YamlReader reader = null;
			try {
				reader = getYaml(emojiUrls);
				final EmojiStandardList emojiList = reader.read(EmojiStandardList.class);
				for (final EmojiStandard emoji : emojiList.emojis) {
					final EmojiId id = new EmojiId.StandardEmojiId(emojiList.location + emoji.location, emoji.name);
					for (final String string : emoji.strings)
						dict.put(string, id);
				}
			} catch (final Exception e) {
				Log.log.warn("Failed to load Standard Emojis: ", e);
			} finally {
				if (reader != null)
					IOUtils.closeQuietly(reader::close);
			}
		}
		EMOJI_DICTIONARY.putAll(dict);
	}
}
