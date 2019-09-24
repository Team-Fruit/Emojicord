package net.teamfruit.emojicord.asm;

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

public class EmojicordCoreService implements ITransformationService {
	public static @Nullable BiFunction<Domain, String, String> Srg2Mcp;
	public static Set<String> TransformerServices;

	@Override
	public void onLoad(final IEnvironment env, final Set<String> otherServices) throws IncompatibleEnvironmentException {
		UniversalVersioner.loadVersionFromCoreMod(EmojicordCoreService.class);
		TransformerServices = otherServices;
	}

	@Override
	public void initialize(final IEnvironment environment) {
	}

	@Override
	public void beginScanning(final IEnvironment environment) {
		Srg2Mcp = environment.findNameMapping("srg").orElse(null);
		// Load mod manually when product environment.
		if (Srg2Mcp==null)
			try {
				final Class<?> discoveryClass = Class.forName(Reference.CUSTOM_MOD_DISCOVERY);
				final Object discovery = discoveryClass.newInstance();
				discoveryClass.getMethod("discoverMods").invoke(discovery);
			} catch (final ReflectiveOperationException e) {
				throw new RuntimeException("Failed to load transformer", e);
			}
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