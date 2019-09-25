package net.teamfruit.emojicord.asm;

import java.util.function.Supplier;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

import net.teamfruit.emojicord.asm.lib.ASMValidate;
import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.DescHelper;
import net.teamfruit.emojicord.asm.lib.INodeTreeTransformer;
import net.teamfruit.emojicord.asm.lib.MethodMatcher;
import net.teamfruit.emojicord.asm.lib.VisitorHelper;
import net.teamfruit.emojicord.compat.CompatBaseVersion;
import net.teamfruit.emojicord.compat.CompatVersion;

public class GuiTextFieldTransform implements INodeTreeTransformer {
	@Override
	public ClassName getClassName() {
		if (CompatVersion.version().older(CompatBaseVersion.V13))
			return ClassName.of("net.minecraft.client.gui.GuiTextField");
		else
			return ClassName.of("net.minecraft.client.gui.widget.TextFieldWidget");
	}

	@Override
	public ClassNode apply(final ClassNode node) {
		final ASMValidate validator = ASMValidate.create(getSimpleName());
		validator.test("drawTextBox.begin");
		validator.test("drawTextBox.return", CompatVersion.version().older(CompatBaseVersion.V11));
		validator.test("drawTextBox.return");

		{
			final MethodMatcher matcher = ((Supplier<MethodMatcher>) () -> {
				if (CompatVersion.version().older(CompatBaseVersion.V11))
					return new MethodMatcher(getClassName(), DescHelper.toDescMethod(void.class, int.class), ASMDeobfNames.GuiTextFieldDrawTextBox);
				else
					return new MethodMatcher(getClassName(), DescHelper.toDescMethod(void.class, int.class, int.class, float.class), ASMDeobfNames.GuiTextFieldDrawTextField);
			}).get();
			node.methods.stream().filter(matcher).forEach(method -> {
				{
					/*
					 0  iconst_1
					 1  putstatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.IsNewChatRendering : boolean [15]
					*/
					final InsnList insertion = new InsnList();
					insertion.add(new InsnNode(Opcodes.ICONST_1));
					insertion.add(new FieldInsnNode(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "isTextFieldRendering", DescHelper.toDesc(boolean.class)));
					method.instructions.insert(insertion);
					validator.check("drawTextBox.begin");
				}
				VisitorHelper.stream(method.instructions).filter(e -> {
					return e instanceof InsnNode&&e.getOpcode()==Opcodes.RETURN;
				}).forEach(marker -> {
					{
						/*
						 0  iconst_0
						 1  putstatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.IsNewChatRendering : boolean [15]
						*/
						final InsnList insertion = new InsnList();
						insertion.add(new InsnNode(Opcodes.ICONST_0));
						insertion.add(new FieldInsnNode(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "isTextFieldRendering", DescHelper.toDesc(boolean.class)));
						method.instructions.insertBefore(marker, insertion);
						validator.check("drawTextBox.return");
					}
				});
			});
		}

		validator.validate();
		return node;
	}
}