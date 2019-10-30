package net.teamfruit.emojicord.compat;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.objectweb.asm.tree.ClassNode;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;

public abstract class CompatTransformer implements ITransformer<ClassNode> {
	public abstract ClassNode read(@Nonnull byte[] bytes);

	public abstract byte[] write(@Nonnull ClassNode node);

	public abstract ClassNode transform(final ClassNode input, final CompatTransformerVotingContext context);

	public abstract DeferredTransform[] deferredTransforms();

	public abstract Set<String> targetNames();

	public static class CompatTransformerVotingContext {
	}

	public static class DeferredTransform {
		private final String targetname;
		private Supplier<Boolean> shouldDeferSupplier;

		public DeferredTransform(final String thisname, final String targetname) {
			this.targetname = targetname;
			this.shouldDeferSupplier = Suppliers.memoize(() -> {
				try {
					Class.forName(this.targetname, false, getClass().getClassLoader());
					return true;
				} catch (final ClassNotFoundException e) {
				}
				return false;
			});
		}

		public boolean hasTarget() {
			return this.shouldDeferSupplier.get();
		}

		public boolean shouldDefer() {
			return this.shouldDeferSupplier.get();
		}
	}

	private boolean deferred = false;

	@Override
	public TransformerVoteResult castVote(final ITransformerVotingContext context) {
		if (this.deferred)
			return TransformerVoteResult.YES;
		this.deferred = true;
		return Arrays.stream(deferredTransforms()).anyMatch(DeferredTransform::shouldDefer) ? TransformerVoteResult.DEFER : TransformerVoteResult.YES;
	}

	@Override
	public Set<Target> targets() {
		return targetNames().stream().map(Target::targetClass).collect(Collectors.toSet());
	}

	@Override
	public ClassNode transform(final ClassNode input, final ITransformerVotingContext context) {
		return transform(input, new CompatTransformerVotingContext());
	}
}
