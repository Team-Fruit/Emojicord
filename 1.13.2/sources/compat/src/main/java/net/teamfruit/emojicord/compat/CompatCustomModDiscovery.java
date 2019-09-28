package net.teamfruit.emojicord.compat;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.BackgroundScanHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFile.Type;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.teamfruit.emojicord.Log;

// Since FML does not load this mod that implements ITransformerService, load it manually.
public class CompatCustomModDiscovery extends AbstractJarFileLocator implements CompatBaseCustomModDiscovery {
	private List<Path> modList = Lists.newArrayList();

	@Override
	public void registerModList(final List<Path> modList) {
		this.modList.addAll(modList);
	}

	@Override
	public List<Path> getModFiles() {
		return this.modList;
	}

	@Override
	public List<ModFile> scanMods() {
		try {
			return getModFiles().stream().filter(Files::isRegularFile).map(e -> new ModFile(e, this)).peek(modFile -> {
				this.modJars.compute(modFile, (mf, fs) -> {
					return createFileSystem(mf);
				});
			}).collect(Collectors.toList());
		} catch (final Exception e) {
			throw new RuntimeException("Error during Emojicord discovery", e);
		}
	}

	@Override
	public Path findPath(final ModFile modFile, final String... path) {
		if (path.length==1&&"pack.mcmeta".equals(path[0]))
			return super.findPath(modFile, "pack_4.mcmeta");
		return super.findPath(modFile, path);
	}

	@Override
	public String name() {
		return "Emojicord Locator";
	}

	@Override
	public String toString() {
		return "{EmojicordFMLPlugin locator}";
	}

	@Override
	public void initArguments(final Map<String, ?> arguments) {
	}

	@Override
	public void discoverMods() {
		//Log.log.debug(LogMarkers.SCAN, "Trying locator {}", this);
		final Map<Type, List<ModFile>> modFiles = scanMods().stream().map(ModFile.class::cast).peek((mf) -> {
			Log.log.debug(LogMarkers.SCAN, "Found mod file {} of type {} with locator {}", mf.getFileName(), mf.getType(), mf.getLocator());
		}).collect(Collectors.groupingBy(ModFile::getType));

		FMLLoader.getLanguageLoadingProvider().addAdditionalLanguages(modFiles.get(Type.LANGPROVIDER));
		final List<ModFile> mods = modFiles.getOrDefault(Type.MOD, Collections.emptyList());

		for (final Iterator<ModFile> loadingModList = mods.iterator(); loadingModList.hasNext();) {
			final ModFile mod = loadingModList.next();
			if (!(mod.getLocator().isValid(mod)&&mod.identifyMods())) {
				Log.log.warn(LogMarkers.SCAN, "File {} has been ignored - it is invalid", mod.getFilePath());
				loadingModList.remove();
			}
		}

		//Log.log.debug(LogMarkers.SCAN, "Found {} mod files with {} mods", (Object[]) new Supplier[] { mods::size, () -> {
		//	return Integer.valueOf(mods.stream().mapToInt((mf) -> {
		//		return mf.getModInfos().size();
		//	}).sum());
		//} });

		final LoadingModList to = FMLLoader.getLoadingModList();
		final BackgroundScanHandler backgroundScanHandler = new BackgroundScanHandler();
		backgroundScanHandler.setLoadingModList(to);

		final Map<String, ModFileInfo> fileById = LamdbaExceptionUtils.uncheck(() -> {
			final Field field = LoadingModList.class.getDeclaredField("fileById");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			final Map<String, ModFileInfo> object = (Map<String, ModFileInfo>) field.get(to);
			return object;
		});

		for (final ModFile file : mods) {
			file.identifyLanguage();
			file.getCoreMods().stream().forEach(FMLLoader.getCoreModProvider()::addCoreMod);
			file.getAccessTransformer().ifPresent(path -> {
				FMLLoader.addAccessTransformer(path, file);
			});
			backgroundScanHandler.submitForScanning(file);
			to.getModFiles().add((ModFileInfo) file.getModFileInfo());
			file.getModInfos().stream().map(ModInfo.class::cast).forEach(modinfo -> {
				to.getMods().add(modinfo);
				fileById.put(modinfo.getModId(), modinfo.getOwningFile());
			});
		}

		backgroundScanHandler.getScannedFiles().stream().forEach(e -> e.getScanResult().getTargets().remove(null));
	}
}
