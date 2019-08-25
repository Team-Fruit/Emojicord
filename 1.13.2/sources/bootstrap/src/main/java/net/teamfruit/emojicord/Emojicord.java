package net.teamfruit.emojicord;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.teamfruit.emojicord.compat.CompatProxy;
import net.teamfruit.emojicord.compat.CompatProxy.CompatFMLInitializationEvent;
import net.teamfruit.emojicord.compat.CompatProxy.CompatFMLPostInitializationEvent;
import net.teamfruit.emojicord.compat.CompatProxy.CompatFMLPreInitializationEvent;

@Mod(value = Reference.MODID)
public class Emojicord {
	public static @Nullable Emojicord instance;

	public static @Nullable CompatProxy proxy = DistExecutor.<CompatProxy> runForDist(() -> () -> {
		try {
			return (CompatProxy) Class.forName(Reference.PROXY_CLIENT).newInstance();
		} catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
			throw new RuntimeException("Could not load proxy class: ", e);
		}
	}, () -> () -> {
		try {
			return (CompatProxy) Class.forName(Reference.PROXY_SERVER).newInstance();
		} catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
			throw new RuntimeException("Could not load proxy class: ", e);
		}
	});

	public Emojicord() {
		instance = this;

	}

	@SubscribeEvent
	public void preInit(final @Nonnull FMLCommonSetupEvent event) {
		if (proxy!=null)
			proxy.preInit(new CompatFMLPreInitializationEventImpl(event));
	}

	@SubscribeEvent
	public void init(final @Nonnull FMLCommonSetupEvent event) {
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

		public CompatFMLPreInitializationEventImpl(final FMLCommonSetupEvent event) {
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
		public CompatFMLInitializationEventImpl(final FMLCommonSetupEvent event) {
		}
	}

	private static class CompatFMLPostInitializationEventImpl implements CompatFMLPostInitializationEvent {
		public CompatFMLPostInitializationEventImpl(final FMLLoadCompleteEvent event) {
		}
	}
}
