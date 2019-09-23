package net.teamfruit.emojicord.asm;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.BackgroundScanHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModFile.Type;

// Since FML does not load this mod that implements ITransformerService, load it manually.
public class EmojicordFMLPlugin extends AbstractJarFileLocator {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String SUFFIX = ".jar";
	private final Path modFolder;

	public EmojicordFMLPlugin() {
		this(FMLPaths.MODSDIR.get());
	}

	EmojicordFMLPlugin(final Path modFolder) {
		this.modFolder = modFolder;
	}

	@Override
	public List<IModFile> scanMods() {
		try {
			final Path path = Paths.get(Validate.notNull(getClass().getProtectionDomain().getCodeSource()).getLocation().toURI());
			final File file = path.toFile();
			Validate.isTrue(file.isFile()&&path.toString().endsWith(SUFFIX));
			final ModFile modFile = new ModFile(path, this);
			this.modJars.compute(modFile, (mf, fs) -> {
				return createFileSystem(mf);
			});
			return Lists.newArrayList(modFile);
		} catch (final Exception e) {
			throw new RuntimeException("Error during Emojicord discovery", e);
		}
	}

	@Override
	public String name() {
		return "Emojicord Locator";
	}

	@Override
	public String toString() {
		return "{EmojicordFMLPlugin locator at "+this.modFolder+"}";
	}

	@Override
	public void initArguments(final Map<String, ?> arguments) {
	}

	public LoadingModList discoverMods() {
		LOGGER.debug(LogMarkers.SCAN, "Trying locator {}", this);
		final Map<Type, List<ModFile>> modFiles = scanMods().stream().map(ModFile.class::cast).peek((mf) -> {
			LOGGER.debug(LogMarkers.SCAN, "Found mod file {} of type {} with locator {}", mf.getFileName(), mf.getType(), mf.getLocator());
		}).collect(Collectors.groupingBy(IModFile::getType));

		FMLLoader.getLanguageLoadingProvider().addAdditionalLanguages(modFiles.get(Type.LANGPROVIDER));
		final List<ModFile> mods = modFiles.getOrDefault(Type.MOD, Collections.emptyList());

		for (final Iterator<ModFile> loadingModList = mods.iterator(); loadingModList.hasNext();) {
			final ModFile mod = loadingModList.next();
			if (!(mod.getLocator().isValid(mod)&&mod.identifyMods())) {
				LOGGER.warn(LogMarkers.SCAN, "File {} has been ignored - it is invalid", mod.getFilePath());
				loadingModList.remove();
			}
		}

		LOGGER.debug(LogMarkers.SCAN, "Found {} mod files with {} mods", (Object[]) new Supplier[] { mods::size, () -> {
			return Integer.valueOf(mods.stream().mapToInt((mf) -> {
				return mf.getModInfos().size();
			}).sum());
		} });

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

		return to;
	}
}
