package net.teamfruit.emojicord.compat;

import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;

// Decorator for Forge detecting interface generic type
public class CompatTransformerDecorator implements ITransformer<ClassNode> {
	private final ITransformer<ClassNode> transformer;

	public CompatTransformerDecorator(final CompatTransformer decoratee) {
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
