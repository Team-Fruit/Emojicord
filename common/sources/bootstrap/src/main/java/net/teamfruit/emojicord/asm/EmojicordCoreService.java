package net.teamfruit.emojicord.asm;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.INameMappingService.Domain;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import net.teamfruit.emojicord.Reference;
import net.teamfruit.emojicord.UniversalVersioner;
import net.teamfruit.emojicord.compat.CompatBaseCustomModDiscovery;

public class EmojicordCoreService implements ITransformationService {
	public static class UniversalVersionerInjector {
		private static File result;

		static {
			try {
				Class.forName("net.teamfruit.emojicord.compat.Compat");
			} catch (final ClassNotFoundException e) {
				result = UniversalVersioner.loadVersionFromCoreService(EmojicordCoreService.class);
			}
		}

		public static File inject() {
			return result;
		}
	}

	public static @Nullable BiFunction<Domain, String, String> Srg2Mcp;
	public static Set<String> TransformerServices;
	private CompatBaseCustomModDiscovery discovery;

	@Override
	public void onLoad(final IEnvironment env, final Set<String> otherServices) throws IncompatibleEnvironmentException {
		TransformerServices = otherServices;
	}

	@Override
	public void initialize(final IEnvironment environment) {
		final File result = UniversalVersionerInjector.inject();
		try {
			final Class<?> discoveryClass = Class.forName(Reference.CUSTOM_MOD_DISCOVERY);
			this.discovery = (CompatBaseCustomModDiscovery) discoveryClass.newInstance();
		} catch (final ReflectiveOperationException e) {
			throw new RuntimeException("Failed to load transformer", e);
		}
		if (result!=null)
			this.discovery.registerModList(Arrays.asList(result));
		else {
			final String file = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
			if (file.endsWith(".jar"))
				this.discovery.registerModNameList(Arrays.asList(file));
		}
	}

	@Override
	public void beginScanning(final IEnvironment environment) {
		Srg2Mcp = environment.findNameMapping("srg").orElse(null);
		// Load mod manually when product environment.
		this.discovery.discoverMods();
	}

	@Override
	public String name() {
		return getClass().getSimpleName();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<ITransformer> transformers() {
		try {
			final Object transformer = Class.forName(Reference.TRANSFORMER).newInstance();
			final ITransformer decorated = (ITransformer) Class.forName(Reference.TRANSFORMER_DECORATOR)
					.getConstructor(Class.forName(Reference.TRANSFORMER_DECORATEE)).newInstance(transformer);
			return Arrays.asList(decorated);
		} catch (final ReflectiveOperationException e) {
			throw new RuntimeException("Failed to load transformer", e);
		}
	}
}