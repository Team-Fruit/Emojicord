package net.teamfruit.emojicord;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.teamfruit.emojicord.compat.Compat.CompatMinecraft;

public class ClientProxy extends CommonProxy {
	public static final Minecraft MC = CompatMinecraft.getMinecraft();

	public static final Map<String, String> EMOJI_NAME_MAP = Maps.newHashMap();
	public static final LoadingCache<String, Emoji> EMOJI_ID_MAP = CacheBuilder.newBuilder()
			.build(new CacheLoader<String, Emoji>() {
				@Override
				public Emoji load(final String key) throws Exception {
					return new Emoji(key);
				}
			});
	boolean error = false;

	@Override
	public void preInit(final @Nonnull CompatFMLPreInitializationEvent event) {
		super.preInit(event);

		Log.log = event.getModLog();
	}

	@Override
	public void init(final @Nonnull CompatFMLInitializationEvent event) {
		super.init(event);

		if (EmojiEndpoint.loadGateway())
			EmojiEndpoint.loadStandardEmojis();

		if (!this.error)
			MC.fontRenderer = new EmojiFontRenderer(MC);
	}

	@Override
	public void postInit(final @Nonnull CompatFMLPostInitializationEvent event) {
		super.postInit(event);
	}
}