package net.teamfruit.emojicord;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.Nonnull;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy {
	public static final Minecraft MC = Minecraft.getMinecraft();

	public static final Map<String, String> EMOJI_NAME_MAP = Maps.newHashMap();
	public static final LoadingCache<String, Emoji> EMOJI_ID_MAP = CacheBuilder.newBuilder()
			.build(new CacheLoader<String, Emoji>() {
				@Override
				public Emoji load(final String key) throws Exception {
					final Emoji emoji = new Emoji();
					emoji.name = key;
					return emoji;
				}
			});
	boolean error = false;

	public static void main(final String[] s) throws YamlException {
		final YamlReader reader = new YamlReader(new StringReader(
				readStringFromURL("https://raw.githubusercontent.com/HrznStudio/Emojiful/master/Categories.yml")));
		@SuppressWarnings("unchecked")
		final Map<String, String> names = (Map<String, String>) reader.read();
		EMOJI_NAME_MAP.putAll(names);
	}

	public static List<Emoji> readCategory(final String cat) throws YamlException {
		final YamlReader categoryReader = new YamlReader(new StringReader(
				readStringFromURL("https://raw.githubusercontent.com/HrznStudio/Emojiful/master/" + cat)));
		return Lists.newArrayList(categoryReader.read(Emoji[].class));
	}

	public static String readStringFromURL(final String requestURL) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new URL(requestURL).openStream(), StandardCharsets.UTF_8.toString());
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
		}
		return "";
	}

	@Override
	public void preInit(final @Nonnull CompatFMLPreInitializationEvent event) {
		super.preInit(event);

		Log.log = event.getModLog();

		//		try {
		//			final YamlReader reader = new YamlReader(new StringReader(
		//					readStringFromURL("https://raw.githubusercontent.com/HrznStudio/Emojiful/master/Categories.yml")));
		//			@SuppressWarnings("unchecked")
		//			final ArrayList<String> categories = (ArrayList<String>) reader.read();
		//			final Iterator<String> var4 = categories.iterator();
		//			while (var4.hasNext()) {
		//				final String category = var4.next();
		//				final List<Emoji> emojis = readCategory(category);
		//				EMOJI_LIST.addAll(emojis);
		//				EMOJI_MAP.put(category, emojis);
		//			}
		//		} catch (final YamlException var7) {
		//			this.error = true;
		//		}
	}

	@Override
	public void init(final @Nonnull CompatFMLInitializationEvent event) {
		super.init(event);

		if (!this.error)
			MC.fontRenderer = new EmojiFontRenderer(MC);
	}

	@Override
	public void postInit(final @Nonnull CompatFMLPostInitializationEvent event) {
		super.postInit(event);
	}
}