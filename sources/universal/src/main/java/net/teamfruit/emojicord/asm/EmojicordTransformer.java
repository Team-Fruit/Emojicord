package net.teamfruit.emojicord.asm;

import org.objectweb.asm.tree.ClassNode;

import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.VisitorHelper;
import net.teamfruit.emojicord.compat.CompatTransformer;

public class EmojicordTransformer extends CompatTransformer {
	@Override
	public ClassNode transform(final ClassNode input, final CompatTransformerVotingContext context) {
		final String transformedName = ClassName.BytecodeToSourcecodeName(input.name);

		try {
			if (transformedName.equals("net.minecraft.client.gui.GuiTextField"))
				return VisitorHelper.apply(input, (name, cv) -> {
					Log.log.info(String.format("Patching GuiTextField (class: %s)", name));
					return new GuiTextFieldVisitor(name, cv);
				});

			if (transformedName.equals("net.minecraft.client.gui.FontRenderer"))
				return VisitorHelper.apply(input, (name, cv) -> {
					Log.log.info(String.format("Patching FontRenderer (class: %s)", name));
					return new FontRendererVisitor(name, cv);
				});
		} catch (final Exception e) {
			Log.log.fatal("Could not transform: ", e);
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