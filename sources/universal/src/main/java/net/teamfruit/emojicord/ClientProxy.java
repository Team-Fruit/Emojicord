package net.teamfruit.emojicord;

import javax.annotation.Nonnull;

import net.teamfruit.emojicord.compat.Compat.CompatSide;
import net.teamfruit.emojicord.compat.Compat.CompatVersionChecker;
import net.teamfruit.emojicord.emoji.DiscordEmojiIdDictionary;
import net.teamfruit.emojicord.emoji.EmojiFrequently;
import net.teamfruit.emojicord.emoji.Endpoint;

public class ClientProxy extends CommonProxy {
	public static EventHandler eventHandler;

	@Override
	public void preInit(final @Nonnull CompatFMLPreInitializationEvent event) {
		super.preInit(event);

		EmojicordConfig.spec.registerConfigDefine(CompatSide.CLIENT);
		EmojicordConfig.spec.registerConfigHandler(CompatSide.CLIENT, event.getSuggestedConfigurationFile());

		CompatVersionChecker.startVersionCheck(Reference.MODID, Reference.VERSION, Reference.NAME);
	}

	@Override
	public void init(final @Nonnull CompatFMLInitializationEvent event) {
		super.init(event);

		EmojiFrequently.instance.load(Locations.instance.getEmojicordDirectory());
		DiscordEmojiIdDictionary.instance.init(Locations.instance.getDictionaryDirectory());
		DiscordEmojiIdDictionary.instance.loadAll();
		EmojicordWeb.instance.init(Locations.instance.getEmojicordDirectory());

		if (Endpoint.loadGateway()) {
			Endpoint.loadStandardEmojis();
			Endpoint.loadStandardPicker();
		}

		eventHandler = new EventHandler();
		eventHandler.registerHandler();
		eventHandler.registerDictionaryWatcher(Locations.instance.getDictionaryDirectory());

		//MC.fontRenderer = new EmojiFontRenderer(MC);
	}

	@Override
	public void postInit(final @Nonnull CompatFMLPostInitializationEvent event) {
		super.postInit(event);

		//MinecraftForge.EVENT_BUS.register(new UTFSendTest());
	}
}