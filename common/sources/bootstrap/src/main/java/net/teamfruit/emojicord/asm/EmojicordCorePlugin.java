package net.teamfruit.emojicord.asm;

import java.util.Map;

import javax.annotation.Nullable;

import net.teamfruit.emojicord.Reference;
import net.teamfruit.emojicord.UniversalVersioner;

public class EmojicordCorePlugin implements net.minecraftforge.fml.relauncher.IFMLLoadingPlugin, cpw.mods.fml.relauncher.IFMLLoadingPlugin {
	public static class UniversalVersionerInjector {
		static {
			try {
				Class.forName("net.teamfruit.emojicord.compat.Compat");
			} catch (final ClassNotFoundException e) {
				UniversalVersioner.loadVersionFromCoreMod(EmojicordCorePlugin.class);
			}
		}

		public static void inject() {
		}
	}

	@Override
	public @Nullable String[] getASMTransformerClass() {
		UniversalVersionerInjector.inject();
		return new String[] {
				Reference.TRANSFORMER
		};
	}

	@Override
	public @Nullable String getModContainerClass() {
		return null;
	}

	@Override
	public @Nullable String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(final @Nullable Map<String, Object> data) {
	}

	@Override
	public @Nullable String getAccessTransformerClass() {
		return null;
	}
}