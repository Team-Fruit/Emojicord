package net.teamfruit.emojicord;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UniversalVersioner {
	public static final Map<String, String> versions = new HashMap<String, String>() {
		{
			put("1.7", "1.7.10");
			put("1.8", "1.8.9");
			put("1.9", "1.9.4");
			put("1.10", "1.10.2");
			put("1.11", "1.11.2");
			put("1.12", "1.12.2");
			put("1.13", "1.13.2");
			put("1.14", "1.14.4");
		}
	};

	public static String getVersion(final String mccversion) {
		for (final Entry<String, String> entry : UniversalVersioner.versions.entrySet()) {
			final String key = entry.getKey();
			if (mccversion.startsWith(key))
				return entry.getValue();
		}
		return null;
	}

	private static Class<?> getClass(final String path) {
		Class<?> $class;
		try {
			$class = Class.forName(path);
		} catch (final ClassNotFoundException e1) {
			throw new RuntimeException("Could not load class");
		}
		return $class;
	}

	private static Class<?> getFMLClass(final String afterFmlPath) {
		Class<?> $class;
		try {
			$class = Class.forName("net.minecraftforge.fml."+afterFmlPath);
		} catch (final ClassNotFoundException e1) {
			try {
				$class = Class.forName("cpw.mods.fml."+afterFmlPath);
			} catch (final ClassNotFoundException e2) {
				throw new RuntimeException("Could not load fml class");
			}
		}
		return $class;
	}

	private static <T> T invokeFMLMethod(final Class<?> $class, final String name, final Class<?>[] types, final Object instance, final Object[] params, final boolean declared) {
		if ($class==null)
			throw new RuntimeException("Could not find fml class");
		Method $method;
		try {
			$method = declared ? $class.getDeclaredMethod(name, types) : $class.getMethod(name, types);
			$method.setAccessible(true);
		} catch (final Exception e) {
			throw new RuntimeException("Could not find or access fml method");
		}
		try {
			@SuppressWarnings("unchecked")
			final T res = (T) $method.invoke(instance, params);
			return res;
		} catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
			throw new RuntimeException("Could not invoke fml method");
		}
	}

	private static <T> T getFMLField(final Class<?> $class, final String name, final Object instance, final boolean declared) {
		if ($class==null)
			throw new RuntimeException("Could not find fml class");
		Field $field;
		try {
			$field = declared ? $class.getDeclaredField(name) : $class.getField(name);
			$field.setAccessible(true);
		} catch (final Exception e) {
			throw new RuntimeException("Could not find or access fml field");
		}
		try {
			@SuppressWarnings("unchecked")
			final T res = (T) $field.get(instance);
			return res;
		} catch (IllegalAccessException|IllegalArgumentException e) {
			throw new RuntimeException("Could not get fml field");
		}
	}

	public static Path prepareVersionFileV7(final Path modFile) {
		if (modFile==null)
			throw new RuntimeException("Could not specify mod file.");

		ZipFile file = null;
		InputStream stream = null;
		try {
			final Object[] data = invokeFMLMethod(getFMLClass("relauncher.FMLInjectionData"), "data", new Class[0], null, new Object[0], false);
			final String mccversion0 = (String) data[4];
			final String mccversion = getVersion(mccversion0);

			if (mccversion==null)
				throw new RuntimeException(String.format("Version %s is not supported! Supported version is %s.", mccversion0, versions));

			final File minecraftDir = (File) data[6];
			final File modsDir = new File(minecraftDir, "mods");

			final File canonicalModsDir = modsDir.getCanonicalFile();
			final File versionSpecificModsDir = new File(canonicalModsDir, mccversion);
			final File modVersionSpecific = new File(versionSpecificModsDir, Reference.MODID);

			final String jarname = String.format("%s.jar", mccversion);
			final File destMod = new File(modVersionSpecific, jarname);

			file = new ZipFile(modFile.toFile());
			final ZipEntry entry = file.getEntry(jarname);
			if (entry==null)
				throw new RuntimeException("Could not find version-specific file: "+jarname);
			stream = file.getInputStream(entry);

			if (!modVersionSpecific.mkdirs()&&!modVersionSpecific.isDirectory())
				throw new IOException("Directory '"+modVersionSpecific+"' could not be created");

			Files.createDirectories(modVersionSpecific.toPath());
			if (Files.isWritable(modFile))
				Files.copy(stream, destMod.toPath(), StandardCopyOption.REPLACE_EXISTING);

			return destMod.toPath();
		} catch (final IOException e) {
			throw new RuntimeException("Could not load version-specific file.", e);
		} finally {
			closeQuietly(file);
			closeQuietly(stream);
		}
	}

	public static Path prepareVersionFileV13(final Path modFile) {
		if (modFile==null)
			throw new RuntimeException("Could not specify mod file.");

		ZipFile file = null;
		InputStream stream = null;
		try {
			final Class<?> fmlLoaderClass = getFMLClass("loading.FMLLoader");
			final String mccversion0 = getFMLField(fmlLoaderClass, "mcVersion", null, true);
			final String mccversion = getVersion(mccversion0);

			if (mccversion==null)
				throw new RuntimeException(String.format("Version %s is not supported! Supported version is %s.", mccversion0, versions));

			final Class<?> pathClass = getFMLClass("loading.FMLPaths");
			final Object modsDirPath = getFMLField(pathClass, "MODSDIR", null, false);
			final Path modsDir = invokeFMLMethod(pathClass, "get", new Class[] {}, modsDirPath, new Object[] {}, false);

			final Path versionSpecificModsDir = modsDir.resolve(Paths.get(mccversion));
			final Path modVersionSpecific = versionSpecificModsDir.resolve(Paths.get(Reference.MODID));

			final String jarname = String.format("%s.jar", mccversion);
			final Path destMod = modVersionSpecific.resolve(Paths.get(jarname));

			file = new ZipFile(modFile.toFile());
			final ZipEntry entry = file.getEntry(jarname);
			if (entry==null)
				throw new RuntimeException("Could not find version-specific file: "+jarname);
			stream = file.getInputStream(entry);

			Files.createDirectories(modVersionSpecific);
			if (Files.isWritable(modFile))
				Files.copy(stream, destMod, StandardCopyOption.REPLACE_EXISTING);

			return destMod;
		} catch (final IOException e) {
			throw new RuntimeException("Could not load version-specific file.", e);
		} finally {
			closeQuietly(file);
			closeQuietly(stream);
		}
	}

	public static URLClassLoader getLaunchClassLoaderV7() {
		return getFMLField(getClass("net.minecraft.launchwrapper.Launch"), "classLoader", null, false);
	}

	public static URLClassLoader getTransformingClassLoaderV13() {
		final Class<?> fmlLoaderClass = getFMLClass("loading.FMLLoader");
		final Object classLoader = invokeFMLMethod(fmlLoaderClass, "getLaunchClassLoader", new Class[] {}, null, new Object[] {}, false);
		return getFMLField(getClass("cpw.mods.modlauncher.TransformingClassLoader"), "delegatedClassLoader", classLoader, true);
	}

	public static void loadPath(final Path file, final ClassLoader classLoader) {
		try {
			invokeFMLMethod(URLClassLoader.class, "addURL", new Class[] { URL.class }, classLoader, new Object[] { file.toUri().toURL() }, true);
		} catch (final IOException e) {
			throw new RuntimeException("Could not load version-specific file.", e);
		}
	}

	private static void closeQuietly(final Closeable closeable) {
		try {
			if (closeable!=null)
				closeable.close();
		} catch (final IOException ioe) {
			// ignore
		}
	}

	public static Path getModFileV7() {
		final Object loader = invokeFMLMethod(getFMLClass("common.Loader"), "instance", new Class[0], null, new Object[0], false);
		final Object container = invokeFMLMethod(getFMLClass("common.Loader"), "activeModContainer", new Class[0], loader, new Object[0], false);
		final File modFile = invokeFMLMethod(getFMLClass("common.ModContainer"), "getSource", new Class[0], container, new Object[0], false);
		if (modFile==null)
			return null;
		return modFile.toPath();
	}

	public static Path getCoreModFileV7(final Class<?> coreModClass) {
		File modFile = null;
		final Map<String, Object> blackboard = getFMLField(getClass("net.minecraft.launchwrapper.Launch"), "blackboard", null, false);
		final List<?> tweakers = (List<?>) blackboard.get("Tweaks");
		if (tweakers!=null) {
			final Class<?> fmlPlugin = getFMLClass("relauncher.CoreModManager$FMLPluginWrapper");
			for (final Object tweaker : tweakers)
				if (fmlPlugin.isInstance(tweaker)) {
					final Object coreModInstance = getFMLField(fmlPlugin, "coreModInstance", tweaker, true);
					if (coreModClass.isInstance(coreModInstance))
						modFile = getFMLField(fmlPlugin, "location", tweaker, true);
				}
		}
		if (modFile==null)
			return null;
		return modFile.toPath();
	}

	public static Path getCoreServiceFileV13(final Class<?> coreModClass) {
		Path modFile = null;
		try {
			final Path file = Paths.get(coreModClass.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (file.toString().endsWith(".jar"))
				if (Files.isRegularFile(file))
					modFile = file;
		} catch (final Exception e) {
			throw new RuntimeException("Error during loading version-specific file", e);
		}
		return modFile;
	}
}
