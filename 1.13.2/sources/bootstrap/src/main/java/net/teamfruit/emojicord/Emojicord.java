package net.teamfruit.emojicord;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.teamfruit.emojicord.compat.Compat.CompatSide;
import net.teamfruit.emojicord.compat.CompatBaseProxy;
import net.teamfruit.emojicord.compat.CompatBaseProxy.CompatFMLInitializationEvent;
import net.teamfruit.emojicord.compat.CompatBaseProxy.CompatFMLPostInitializationEvent;
import net.teamfruit.emojicord.compat.CompatBaseProxy.CompatFMLPreInitializationEvent;

@Mod(value = Reference.MODID)
public class Emojicord {
	public static @Nullable Emojicord instance;

	public static @Nullable CompatBaseProxy proxy = DistExecutor.<CompatBaseProxy> runForDist(() -> () -> {
		try {
			return (CompatBaseProxy) Class.forName(Reference.PROXY_CLIENT).newInstance();
		} catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
			throw new RuntimeException("Could not load proxy class: ", e);
		}
	}, () -> () -> {
		try {
			return (CompatBaseProxy) Class.forName(Reference.PROXY_SERVER).newInstance();
		} catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
			throw new RuntimeException("Could not load proxy class: ", e);
		}
	});

	public Emojicord() {
		instance = this;

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);

		EmojicordConfig.spec.registerConfigDefine(CompatSide.CLIENT);
	}

	@SubscribeEvent
	public void preInit(final @Nonnull FMLClientSetupEvent event) {
		if (proxy!=null)
			proxy.preInit(new CompatFMLPreInitializationEventImpl(event));
	}

	@SubscribeEvent
	public void init(final @Nonnull FMLClientSetupEvent event) {
		if (proxy!=null)
			proxy.init(new CompatFMLInitializationEventImpl(event));
	}

	@SubscribeEvent
	public void postInit(final @Nonnull FMLLoadCompleteEvent event) {
		if (proxy!=null)
			proxy.postInit(new CompatFMLPostInitializationEventImpl(event));
	}

	private static class CompatFMLPreInitializationEventImpl implements CompatFMLPreInitializationEvent {
		//private final @Nonnull FMLCommonSetupEvent event;

		public CompatFMLPreInitializationEventImpl(final FMLClientSetupEvent event) {
			//this.event = event;
		}

		@Override
		public Logger getModLog() {
			return Log.log;
		}

		@Override
		public File getSuggestedConfigurationFile() {
			return null;
		}

		@Override
		public File getSourceFile() {
			return null;
		}
	}

	private static class CompatFMLInitializationEventImpl implements CompatFMLInitializationEvent {
		public CompatFMLInitializationEventImpl(final FMLClientSetupEvent event) {
		}
	}

	private static class CompatFMLPostInitializationEventImpl implements CompatFMLPostInitializationEvent {
		public CompatFMLPostInitializationEventImpl(final FMLLoadCompleteEvent event) {
		}
	}
}
