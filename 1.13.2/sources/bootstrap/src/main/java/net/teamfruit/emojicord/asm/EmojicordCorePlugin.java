package net.teamfruit.emojicord.asm;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.collect.Lists;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.moddiscovery.BackgroundScanHandler;
import net.minecraftforge.fml.loading.moddiscovery.IModLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFile.Type;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.teamfruit.emojicord.Reference;

public class EmojicordCorePlugin implements ITransformationService {
	private static final Logger LOGGER = LogManager.getLogger();

	public List<ModFile> discoverMods() {
		final List<IModLocator> locatorList = Lists.newArrayList(new EmojicordFMLPlugin());
		final Map<Type, List<ModFile>> modFiles = locatorList.stream().peek((loc) -> {
			LOGGER.debug(LogMarkers.SCAN, "Trying locator {}", loc);
		}).map(IModLocator::scanMods).flatMap(Collection::stream).peek((mf) -> {
			LOGGER.debug(LogMarkers.SCAN, "Found mod file {} of type {} with locator {}", mf.getFileName(), mf.getType(), mf.getLocator());
		}).collect(Collectors.groupingBy(ModFile::getType));

		FMLLoader.getLanguageLoadingProvider().addAdditionalLanguages(modFiles.get(Type.LANGPROVIDER));
		final List<ModFile> mods = modFiles.getOrDefault(Type.MOD, Collections.emptyList());
		final List<ModFile> brokenFiles = Lists.newArrayList();

		for (final Iterator<ModFile> loadingModList = mods.iterator(); loadingModList.hasNext();) {
			final ModFile mod = loadingModList.next();
			if (!(mod.getLocator().isValid(mod)&&mod.identifyMods())) {
				LOGGER.warn(LogMarkers.SCAN, "File {} has been ignored - it is invalid", mod.getFilePath());
				loadingModList.remove();
				brokenFiles.add(mod);
			}
		}

		LOGGER.debug(LogMarkers.SCAN, "Found {} mod files with {} mods", (Object[]) new Supplier[] { mods::size, () -> {
			return Integer.valueOf(mods.stream().mapToInt((mf) -> {
				return mf.getModInfos().size();
			}).sum());
		} });

		return mods;
	}

	@Override
	public void beginScanning(final IEnvironment environment) {
		final List<ModFile> from = discoverMods();
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
		for (final ModFile file : from) {
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
	}

	@Override
	public String name() {
		return getClass().getSimpleName();
	}

	@Override
	public void initialize(final IEnvironment environment) {
	}

	@Override
	public void onLoad(final IEnvironment env, final Set<String> otherServices) throws IncompatibleEnvironmentException {
	}

	// Decorator for Forge detecting interface generic type
	public static class TransformerDecorator implements ITransformer<ClassNode> {
		private final ITransformer<ClassNode> transformer;

		public TransformerDecorator(final ITransformer<ClassNode> decoratee) {
			this.transformer = decoratee;
		}

		@Override
		public ClassNode transform(final ClassNode input, final ITransformerVotingContext context) {
			return this.transformer.transform(input, context);
		}

		@Override
		public TransformerVoteResult castVote(final ITransformerVotingContext context) {
			return this.transformer.castVote(context);
		}

		@Override
		public Set<Target> targets() {
			return this.transformer.targets();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<ITransformer> transformers() {
		try {
			@SuppressWarnings("unchecked")
			final ITransformer<ClassNode> transformer = (ITransformer<ClassNode>) Class.forName(Reference.TRANSFORMER).newInstance();
			return Lists.newArrayList(new TransformerDecorator(transformer));
		} catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
			throw new RuntimeException("Failed to load transformer", e);
		}
	}
}