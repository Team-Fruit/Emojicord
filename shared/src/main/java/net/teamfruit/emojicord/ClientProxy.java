package net.teamfruit.emojicord;

#if MC_12_LATER
import net.minecraftforge.api.distmarker.Dist;
#elif MC_7_LATER
import net.minecraftforge.fml.relauncher.Side;
#else
import cpw.mods.fml.relauncher.Side;
#endif

import net.teamfruit.emojicord.compat.Compat.CompatVersionChecker;
import net.teamfruit.emojicord.emoji.DiscordEmojiIdDictionary;
import net.teamfruit.emojicord.emoji.EmojiFrequently;
import net.teamfruit.emojicord.emoji.Endpoint;

import javax.annotation.Nonnull;

public class ClientProxy extends CommonProxy {
	public static EventHandler eventHandler;

	@Override
	public void preInit(final @Nonnull CompatFMLPreInitializationEvent event) {
		super.preInit(event);

		EmojicordConfig.spec.registerConfigDefine( #if MC_12_LATER Dist #else Side #endif .CLIENT);
		EmojicordConfig.spec.registerConfigHandler( #if MC_12_LATER Dist #else Side #endif .CLIENT, event.getSuggestedConfigurationFile());

		CompatVersionChecker.startVersionCheck(Reference.MODID, VersionReference.VERSION, Reference.UPDATE_JSON);
	}

	@Override
	public void init(final @Nonnull CompatFMLInitializationEvent event) {
		super.init(event);

		EmojiFrequently.instance.load(Locations.instance.getEmojicordDirectory());
		DiscordEmojiIdDictionary.instance.init(Locations.instance.getDictionaryDirectory());
		DiscordEmojiIdDictionary.instance.loadAll();

		EmojicordScope.instance.loadAll();

		if (Endpoint.loadGateway()) {
			Endpoint.loadStandardEmojis();
			Endpoint.loadStandardPicker();
			Analytics.instance.startAnalytics();
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