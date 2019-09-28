package net.teamfruit.emojicord.asm;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import org.objectweb.asm.tree.ClassNode;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.INameMappingService.Domain;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.teamfruit.emojicord.Reference;
import net.teamfruit.emojicord.compat.CompatBaseCustomModDiscovery;

public class EmojicordCoreService implements ITransformationService {
	public static @Nullable BiFunction<Domain, String, String> Srg2Mcp;
	public static Set<String> TransformerServices;
	private CompatBaseCustomModDiscovery discovery;

	@Override
	public void onLoad(final IEnvironment env, final Set<String> otherServices) throws IncompatibleEnvironmentException {
		TransformerServices = otherServices;
	}

	@Override
	public void initialize(final IEnvironment environment) {
	}

	@Override
	public void beginScanning(final IEnvironment environment) {
		Srg2Mcp = environment.findNameMapping("srg").orElse(null);
		try {
			final Class<?> discoveryClass = Class.forName(Reference.CUSTOM_MOD_DISCOVERY);
			this.discovery = (CompatBaseCustomModDiscovery) discoveryClass.newInstance();
		} catch (final ReflectiveOperationException e) {
			throw new RuntimeException("Failed to load version-specific mod loader", e);
		}
		try {
			final Path file = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			if (file.toString().endsWith(".jar"))
				this.discovery.registerModList(Arrays.asList(file));
		} catch (final URISyntaxException e) {
			throw new RuntimeException("Failed to load this mod", e);
		}
	}

	@Override
	public String name() {
		return getClass().getSimpleName();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<ITransformer> transformers() {
		// Load mod manually when product environment.
		this.discovery.discoverMods();
		try {
			@SuppressWarnings("unchecked")
			final ITransformer<ClassNode> transformer = (ITransformer<ClassNode>) Class.forName(Reference.TRANSFORMER).newInstance();
			final ITransformer decorated = new TransformerDecorator(transformer);
			return Arrays.asList(decorated);
		} catch (final ReflectiveOperationException e) {
			throw new RuntimeException("Failed to load transformer", e);
		}
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
}