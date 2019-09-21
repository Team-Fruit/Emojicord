package net.teamfruit.emojicord.asm;

import javax.annotation.Nonnull;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.asm.lib.ClassMatcher;
import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.VisitorHelper;
import net.teamfruit.emojicord.compat.CompatTransformer;

public class EmojicordTransformer extends CompatTransformer {
	@Override
	public ClassNode read(@Nonnull final byte[] bytes) {
		return VisitorHelper.read(bytes, ClassReader.SKIP_FRAMES);
	}

	@Override
	public byte[] write(@Nonnull final ClassNode node) {
		return VisitorHelper.write(node, ClassWriter.COMPUTE_FRAMES);
	}

	private final ClassMatcher guitextfieldmatcher = new ClassMatcher(ClassName.of("net.minecraft.client.gui.GuiTextField"));
	private final ClassMatcher fontrenderermatcher = new ClassMatcher(ClassName.of("net.minecraft.client.gui.FontRenderer"));

	@Override
	public ClassNode transform(final ClassNode input, final CompatTransformerVotingContext context) {
		try {
			if (this.guitextfieldmatcher.test(input))
				return VisitorHelper.transform(input, node -> {
					Log.log.info(String.format("Patching GuiTextField (class: %s)", node.name));
					return new GuiTextFieldTransform().apply(node);
				});

			if (this.fontrenderermatcher.test(input))
				return VisitorHelper.transform(input, node -> {
					Log.log.info(String.format("Patching FontRenderer (class: %s)", node.name));
					return new FontRendererTransform().apply(node);
				});
		} catch (final Exception e) {
			throw new RuntimeException("Could not transform: ", e);
		}

		return input;
	}

	DeferredTransform[] deferredTransforms = {
			new DeferredTransform(EmojicordTransformer.class.getName(), "com.tsoft_web.IntelliInput.asm.IntelliInputTransformer"),
	};

	@Override
	public DeferredTransform[] deferredTransforms() {
		return this.deferredTransforms;
	}

	String[] targetNames = {
			"net.minecraft.client.gui.GuiTextField",
			"net.minecraft.client.gui.FontRenderer",
	};

	@Override
	public String[] targetNames() {
		return this.targetNames;
	}
}