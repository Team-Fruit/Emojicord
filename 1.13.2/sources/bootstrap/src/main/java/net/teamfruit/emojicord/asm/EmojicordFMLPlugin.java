package net.teamfruit.emojicord.asm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.utils.InputStreamReader;

import com.google.common.base.Charsets;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import cpw.mods.modlauncher.serviceapi.ITransformerDiscoveryService;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

public class EmojicordFMLPlugin extends AbstractJarFileLocator {
	private static final String SUFFIX = ".jar";
	private static final Logger LOGGER = LogManager.getLogger();
	private final Path modFolder;

	public EmojicordFMLPlugin() {
		this(FMLPaths.MODSDIR.get());
	}

	EmojicordFMLPlugin(final Path modFolder) {
		this.modFolder = modFolder;
	}

	@Override
	public List<ModFile> scanMods() {
		LOGGER.debug(LogMarkers.SCAN, "Scanning mods dir {} for Emojicord", this.modFolder);
		final List<Path> excluded = new ModDirTransformerSearchDiscoverer(EmojicordCorePlugin.class.getName())
				.candidates(FMLPaths.GAMEDIR.get());
		return excluded.stream().sorted(Comparator.comparing((path) -> {
			return StringUtils.toLowerCase(path.getFileName().toString());
		})).filter(p -> {
			return StringUtils.toLowerCase(p.getFileName().toString()).endsWith(SUFFIX);
		}).map(p -> {
			return new ModFile(p, this);
		}).peek(f -> {
			this.modJars.compute(f, (mf, fs) -> {
				return createFileSystem(mf);
			});
		}).collect(Collectors.toList());
	}

	@Override
	public String name() {
		return "Emojicord in mods folder";
	}

	@Override
	public String toString() {
		return "{EmojicordFMLPlugin locator at "+this.modFolder+"}";
	}

	@Override
	public void initArguments(final Map<String, ?> arguments) {
	}

	public class ModDirTransformerSearchDiscoverer implements ITransformerDiscoveryService {
		private final String serchService;

		public ModDirTransformerSearchDiscoverer(final String serchService) {
			this.serchService = serchService;
		}

		// Parse a single line from the given configuration file, adding the name
		// on the line to the names list.
		//
		private String parseLine(String ln) {
			if (ln==null)
				return null;
			final int ci = ln.indexOf('#');
			if (ci>=0)
				ln = ln.substring(0, ci);
			ln = ln.trim();
			final int n = ln.length();
			if (n!=0) {
				if (ln.indexOf(' ')>=0||ln.indexOf('\t')>=0)
					return null;
				int cp = ln.codePointAt(0);
				if (!Character.isJavaIdentifierStart(cp))
					return null;
				for (int i = Character.charCount(cp); i<n; i += Character.charCount(cp)) {
					cp = ln.codePointAt(i);
					if (!Character.isJavaIdentifierPart(cp)&&cp!='.')
						return null;
				}
				return ln;
			}
			return null;
		}

		@Override
		public List<Path> candidates(final Path gameDirectory) {
			final Path modsDir = gameDirectory.resolve(FMLPaths.MODSDIR.relative());
			final List<Path> paths = Lists.newArrayList();
			try {
				Files.walk(modsDir, 1, new FileVisitOption[0]).forEach(p -> {
					if (Files.isRegularFile(p)&&p.toString().endsWith(".jar"))
						try {
							if (LamdbaExceptionUtils.uncheck(() -> Files.size(p))!=0L)
								try (ZipFile ioe = new ZipFile(new File(p.toUri()))) {
									final ZipEntry entry = ioe.getEntry("META-INF/services/cpw.mods.modlauncher.api.ITransformationService");
									if (entry!=null)
										try (BufferedReader reader = new BufferedReader(new InputStreamReader(ioe.getInputStream(entry), Charsets.UTF_8))) {
											if (
												reader.lines()
														.map(this::parseLine)
														.filter(Predicates.notNull())
														.anyMatch(e -> Objects.equals(e, this.serchService))
											)
												paths.add(p);
										}
								}
						} catch (final IOException e) {
							LogManager.getLogger().error("Zip Error when loading jar file {}", p, e);
						}
				});
			} catch (IllegalStateException|IOException e) {
				LogManager.getLogger().error("Error during early discovery", e);
			}
			return paths;
		}
	}
}
