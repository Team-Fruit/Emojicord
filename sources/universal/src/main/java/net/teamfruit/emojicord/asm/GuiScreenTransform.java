package net.teamfruit.emojicord.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.DescHelper;
import net.teamfruit.emojicord.asm.lib.MethodMatcher;
import net.teamfruit.emojicord.asm.lib.NodeTransformer;
import net.teamfruit.emojicord.compat.CompatBaseVersion;
import net.teamfruit.emojicord.compat.CompatVersion;

public class GuiScreenTransform implements NodeTransformer {
	@Override
	public ClassNode apply(final ClassNode node) {
		if (CompatVersion.version().older(CompatBaseVersion.V10)) {
			final MethodMatcher matcher = new MethodMatcher(ClassName.of("net.minecraft.client.gui.GuiScreen"), DescHelper.toDescMethod(void.class, ClassName.of("java.lang.String"), boolean.class), ASMDeobfNames.GuiScreenSendMessage);
			node.methods.stream().filter(matcher).forEach(method -> {
				{
					/*
					 0  aload_1 [text]
					 1  invokestatic net.teamfruit.emojicord.compat.CompatEvents.ClientChatEvent.onClientSendMessage(java.lang.String) : java.lang.String [61]
					 4  astore_1 [text]
					*/
					final InsnList insertion = new InsnList();
					insertion.add(new VarInsnNode(Opcodes.ALOAD, 1));
					insertion.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ClassName.of("net.teamfruit.emojicord.compat.CompatEvents$ClientChatEvent").getBytecodeName(), "onClientSendMessage", DescHelper.toDescMethod(ClassName.of("java.lang.String"), ClassName.of("java.lang.String")), false));
					insertion.add(new VarInsnNode(Opcodes.ASTORE, 1));
					method.instructions.insert(insertion);
				}
			});
		}
		return node;
	}
}