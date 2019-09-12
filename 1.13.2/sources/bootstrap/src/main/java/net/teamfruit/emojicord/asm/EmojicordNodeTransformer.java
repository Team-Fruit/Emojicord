package net.teamfruit.emojicord.asm;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.collect.Lists;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.asm.lib.VisitorHelper;
import net.teamfruit.emojicord.asm.lib.VisitorHelper.TransformProvider;

public class EmojicordNodeTransformer implements ITransformer<ClassNode> {
	@Override
	public ClassNode transform(final ClassNode input, final ITransformerVotingContext context) {
		try {
			if (input.name.equals("net.minecraft.client.gui.GuiTextField"))
				return VisitorHelper.apply(input, new TransformProvider(ClassWriter.COMPUTE_FRAMES) {
					@Override
					public ClassVisitor createVisitor(final String name, final ClassVisitor cv) {
						Log.log.info(String.format("Patching GuiTextField (class: %s)", name));
						return new GuiTextFieldVisitor(name, cv);
					}
				});

			if (input.name.equals("net.minecraft.client.gui.FontRenderer"))
				return VisitorHelper.apply(input, new TransformProvider(ClassWriter.COMPUTE_FRAMES) {
					@Override
					public ClassVisitor createVisitor(final String name, final ClassVisitor cv) {
						Log.log.info(String.format("Patching FontRenderer (class: %s)", name));
						return new FontRendererVisitor(name, cv);
					}
				});
		} catch (final Exception e) {
			Log.log.fatal("Could not transform: ", e);
		}

		return input;
	}

	@Override
	public TransformerVoteResult castVote(final ITransformerVotingContext context) {
		return TransformerVoteResult.YES;
	}

	@Override
	public Set<Target> targets() {
		return new HashSet<Target>(Lists.newArrayList(Target.targetClass("abc")));
	}
}