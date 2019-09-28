package net.teamfruit.emojicord;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.teamfruit.emojicord.compat.CompatBaseProxy;
import net.teamfruit.emojicord.compat.CompatBaseProxy.CompatFMLInitializationEvent;
import net.teamfruit.emojicord.compat.CompatBaseProxy.CompatFMLPostInitializationEvent;
import net.teamfruit.emojicord.compat.CompatBaseProxy.CompatFMLPreInitializationEvent;

public class EmojicordN {
	public static @Nullable EmojicordN instance;

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

	public EmojicordN() {
		instance = this;

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);

		if (proxy!=null)
			proxy.preInit(new CompatFMLPreInitializationEventImpl());
	}

	@SubscribeEvent
	public void preInit(final @Nonnull FMLClientSetupEvent event) {
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
		public CompatFMLPreInitializationEventImpl() {
		}

		@Override
		public File getSuggestedConfigurationFile() {
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
