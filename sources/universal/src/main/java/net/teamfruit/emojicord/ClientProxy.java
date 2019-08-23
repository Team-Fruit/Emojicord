package net.teamfruit.emojicord;

import javax.annotation.Nonnull;

import net.teamfruit.emojicord.emoji.DiscordEmojiIdDictionary;
import net.teamfruit.emojicord.emoji.Endpoint;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(final @Nonnull CompatFMLPreInitializationEvent event) {
		super.preInit(event);

		Log.log = event.getModLog();
	}

	@Override
	public void init(final @Nonnull CompatFMLInitializationEvent event) {
		super.init(event);

		DiscordEmojiIdDictionary.instance.loadAll(Locations.instance.getDictionaryDirectory());

		if (Endpoint.loadGateway())
			Endpoint.loadStandardEmojis();

		new EventHandler().registerHandler();

		//MC.fontRenderer = new EmojiFontRenderer(MC);
	}

	@Override
	public void postInit(final @Nonnull CompatFMLPostInitializationEvent event) {
		super.postInit(event);

		//MinecraftForge.EVENT_BUS.register(new UTFSendTest());
	}
}