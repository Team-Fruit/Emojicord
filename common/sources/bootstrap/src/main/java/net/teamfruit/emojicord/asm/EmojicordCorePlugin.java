package net.teamfruit.emojicord.asm;

import java.nio.file.Path;
import java.util.Map;

import javax.annotation.Nullable;

import net.teamfruit.emojicord.Reference;
import net.teamfruit.emojicord.UniversalVersioner;

public class EmojicordCorePlugin implements net.minecraftforge.fml.relauncher.IFMLLoadingPlugin, cpw.mods.fml.relauncher.IFMLLoadingPlugin {
	@Override
	public @Nullable String[] getASMTransformerClass() {
		UniversalVersionerInjector.check();
		UniversalVersionerInjector.setup();
		UniversalVersionerInjector.extract();
		UniversalVersionerInjector.loadToLaunchClassLoader();
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

	public static class UniversalVersionerInjector {
		private static boolean needLoading;
		private static Path coreFile;
		private static Path diffFile;

		public static void check() {
			try {
				Class.forName("net.teamfruit.emojicord.compat.Compat");
			} catch (final ClassNotFoundException e) {
				needLoading = true;
			}
		}

		public static void setup() {
			if (needLoading&&coreFile==null)
				coreFile = UniversalVersioner.getCoreModFileV7(EmojicordCorePlugin.class);
		}

		public static void extract() {
			if (needLoading&&coreFile!=null&&diffFile==null)
				diffFile = UniversalVersioner.prepareVersionFileV7(coreFile);
		}

		public static Path getCoreFile() {
			if (needLoading&&coreFile!=null)
				return coreFile;
			return null;
		}

		public static Path getDiffFile() {
			if (needLoading&&coreFile!=null&&diffFile!=null)
				return diffFile;
			return null;
		}

		public static void loadToLaunchClassLoader() {
			if (needLoading&&coreFile!=null&&diffFile!=null)
				UniversalVersioner.loadPath(diffFile, UniversalVersioner.getLaunchClassLoaderV7());
		}
	}
}