package net.teamfruit.emojicord.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

import com.google.common.collect.Streams;

import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.DescHelper;
import net.teamfruit.emojicord.asm.lib.MethodMatcher;
import net.teamfruit.emojicord.asm.lib.NodeTransformer;

public class GuiTextFieldTransform implements NodeTransformer {
	@Override
	public ClassNode apply(final ClassNode node) {
		{
			final MethodMatcher matcher = new MethodMatcher(ClassName.fromBytecodeName(node.name), DescHelper.toDescMethod(void.class, int.class), ASMDeobfNames.GuiTextFieldDrawTextBox);
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
				}
				Streams.stream(method.instructions.iterator()).filter(e -> {
					return e instanceof InsnNode&&e.getOpcode()==Opcodes.IRETURN;
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
					}
				});
			});
		}
		return node;
	}
}