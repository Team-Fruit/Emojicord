package net.teamfruit.emojicord.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.Streams;

import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.DescHelper;
import net.teamfruit.emojicord.asm.lib.MethodMatcher;
import net.teamfruit.emojicord.asm.lib.NodeTransformer;

public class FontRendererTransform implements NodeTransformer {
	@Override
	public ClassNode apply(final ClassNode node) {
		{
			final MethodMatcher matcher = new MethodMatcher(ClassName.fromBytecodeName(node.name), DescHelper.toDescMethod(void.class, ClassName.of("java.lang.String").getBytecodeName(), boolean.class), ASMDeobfNames.FontRendererRenderStringAtPos);
			node.methods.stream().filter(matcher).forEach(method -> {
				{
					/*
					 0  aload_1 [text]
					 1  invokestatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.updateEmojiContext(java.lang.String) : java.lang.String [61]
					 4  astore_1 [text]
					*/
					final InsnList insertion = new InsnList();
					insertion.add(new VarInsnNode(Opcodes.ALOAD, 1));
					insertion.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "updateEmojiContext", DescHelper.toDescMethod(ClassName.of("java.lang.String").getBytecodeName(), ClassName.of("java.lang.String").getBytecodeName()), false));
					insertion.add(new VarInsnNode(Opcodes.ASTORE, 1));
					method.instructions.insert(insertion);
				}

				final MethodMatcher matcher0 = new MethodMatcher(ClassName.of("net.minecraft.client.gui.FontRenderer").toMappedName(), DescHelper.toDescMethod(float.class, char.class, boolean.class), ASMDeobfNames.FontRendererRenderChar);
				Streams.stream(method.instructions.iterator()).filter(e -> {
					return e instanceof MethodInsnNode
							&&matcher0.match(((MethodInsnNode) e).name, ((MethodInsnNode) e).desc);
				}).findFirst().ifPresent(marker -> {
					{
						/*
						 454  aload_0 [this]
						 455  iload 4 [character]
						 457  aload_0 [this]
						 458  getfield net.teamfruit.emojicord.emoji.EmojiFontRenderer.italicStyle : boolean [134]
						 {
						  +   istore 20 [net.teamfruit.emojicord.emoji.EmojiFontRenderer.italicStyle]
						  +   istore 21 [character]
						  +   astore 22 [this]

						 446  iload_3 [charIndex]
						 447  putstatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.index : int [181]
						 450  iload_2 [shadow]
						 451  putstatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.shadow : boolean [183]

						  +   aload 22 [this]
						  +   iload 21 [character]
						  +   iload 20 [net.teamfruit.emojicord.emoji.EmojiFontRenderer.italicStyle]
						 461  invokespecial net.teamfruit.emojicord.emoji.EmojiFontRenderer.renderChar(char, boolean) : float [185]
						  +   fstore 23 [offset]

						 466  iconst_0
						 467  putstatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.shadow : boolean [183]

						  +   fload 23 [offset]
						 }
						 464  fstore 8 [offset]
						*/
						{
							final InsnList insertion = new InsnList();

							insertion.add(new VarInsnNode(Opcodes.ISTORE, 20));
							insertion.add(new VarInsnNode(Opcodes.ISTORE, 21));
							insertion.add(new VarInsnNode(Opcodes.ASTORE, 22));

							insertion.add(new VarInsnNode(Opcodes.ILOAD, 3));
							insertion.add(new MethodInsnNode(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "index", DescHelper.toDesc(int.class), false));
							insertion.add(new VarInsnNode(Opcodes.ILOAD, 2));
							insertion.add(new MethodInsnNode(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "shadow", DescHelper.toDesc(boolean.class), false));

							insertion.add(new VarInsnNode(Opcodes.ALOAD, 22));
							insertion.add(new VarInsnNode(Opcodes.ILOAD, 21));
							insertion.add(new VarInsnNode(Opcodes.ILOAD, 20));

							method.instructions.insertBefore(marker, insertion);
						}
						{
							final InsnList insertion = new InsnList();

							insertion.add(new VarInsnNode(Opcodes.FSTORE, 23));

							insertion.add(new InsnNode(Opcodes.ICONST_0));
							insertion.add(new MethodInsnNode(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "shadow", DescHelper.toDesc(boolean.class), false));

							insertion.add(new VarInsnNode(Opcodes.FLOAD, 23));

							method.instructions.insert(marker, insertion);
						}
					}
				});
			});
		}
		{
			final MethodMatcher matcher = new MethodMatcher(ClassName.fromBytecodeName(node.name), DescHelper.toDescMethod(int.class, ClassName.of("java.lang.String").getBytecodeName()), ASMDeobfNames.FontRendererGetStringWidth);
			node.methods.stream().filter(matcher).forEach(method -> {
				{
					/*
					 0  aload_1 [text]
					 1  invokestatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.updateEmojiContext(java.lang.String) : java.lang.String [61]
					 4  astore_1 [text]
					*/
					final InsnList insertion = new InsnList();
					insertion.add(new VarInsnNode(Opcodes.ALOAD, 1));
					insertion.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "updateEmojiContext", DescHelper.toDescMethod(ClassName.of("java.lang.String").getBytecodeName(), ClassName.of("java.lang.String").getBytecodeName()), false));
					insertion.add(new VarInsnNode(Opcodes.ASTORE, 1));
					method.instructions.insert(insertion);
				}
			});
		}
		{
			final MethodMatcher matcher = new MethodMatcher(ClassName.fromBytecodeName(node.name), DescHelper.toDescMethod(int.class, char.class), ASMDeobfNames.FontRendererGetCharWidth);
			node.methods.stream().filter(matcher).forEach(method -> {
				{
					/*
					 0  iload_1 [character]
					 1  bipush 63
					 3  if_icmpne 9
					 6  bipush 10
					 8  ireturn
					 9  -
					*/
					final InsnList insertion = new InsnList();
					insertion.add(new VarInsnNode(Opcodes.ILOAD, 1));
					insertion.add(new IntInsnNode(Opcodes.BIPUSH, '\u0000'));
					final LabelNode label = new LabelNode();
					insertion.add(new JumpInsnNode(Opcodes.IF_ICMPNE, label));
					insertion.add(new IntInsnNode(Opcodes.BIPUSH, 10));
					insertion.add(new InsnNode(Opcodes.IRETURN));
					insertion.add(label);
					method.instructions.insert(insertion);
				}
			});
		}
		{
			final MethodMatcher matcher = new MethodMatcher(ClassName.fromBytecodeName(node.name), DescHelper.toDescMethod(float.class, char.class, boolean.class), ASMDeobfNames.FontRendererRenderChar);
			node.methods.stream().filter(matcher).forEach(method -> {
				{
					/*
					 0  iload_1 [c]
					 1  iload_2 [italic]
					 2  aload_0 [fontRenderer]
					 3  getfield net.minecraft.client.gui.FontRenderer.posX : float [49]
					 6  aload_0 [fontRenderer]
					 7  getfield net.minecraft.client.gui.FontRenderer.posY : float [55]
					10  aload_0 [fontRenderer]
					11  getfield net.minecraft.client.gui.FontRenderer.red : float [58]
					14  aload_0 [fontRenderer]
					15  getfield net.minecraft.client.gui.FontRenderer.green : float [61]
					18  aload_0 [fontRenderer]
					19  getfield net.minecraft.client.gui.FontRenderer.blue : float [64]
					22  aload_0 [fontRenderer]
					23  getfield net.minecraft.client.gui.FontRenderer.alpha : float [67]
					26  invokestatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.renderEmojiChar(char, boolean, float, float, float, float, float, float) : boolean [70]
					 6  ifeq 12
					 9  ldc <Float 10.0> [21]
					11  freturn
					12  -
					*/
					final InsnList insertion = new InsnList();
					insertion.add(new VarInsnNode(Opcodes.ILOAD, 1));
					insertion.add(new VarInsnNode(Opcodes.ILOAD, 2));
					insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insertion.add(new FieldInsnNode(Opcodes.GETFIELD, ClassName.of("net.minecraft.client.gui.FontRenderer").getBytecodeName(), ASMDeobfNames.FontRendererPosX.name(), DescHelper.toDesc(float.class)));
					insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insertion.add(new FieldInsnNode(Opcodes.GETFIELD, ClassName.of("net.minecraft.client.gui.FontRenderer").getBytecodeName(), ASMDeobfNames.FontRendererPosY.name(), DescHelper.toDesc(float.class)));
					insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insertion.add(new FieldInsnNode(Opcodes.GETFIELD, ClassName.of("net.minecraft.client.gui.FontRenderer").getBytecodeName(), ASMDeobfNames.FontRendererRed.name(), DescHelper.toDesc(float.class)));
					insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insertion.add(new FieldInsnNode(Opcodes.GETFIELD, ClassName.of("net.minecraft.client.gui.FontRenderer").getBytecodeName(), ASMDeobfNames.FontRendererGreen.name(), DescHelper.toDesc(float.class)));
					insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insertion.add(new FieldInsnNode(Opcodes.GETFIELD, ClassName.of("net.minecraft.client.gui.FontRenderer").getBytecodeName(), ASMDeobfNames.FontRendererBlue.name(), DescHelper.toDesc(float.class)));
					insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insertion.add(new FieldInsnNode(Opcodes.GETFIELD, ClassName.of("net.minecraft.client.gui.FontRenderer").getBytecodeName(), ASMDeobfNames.FontRendererAlpha.name(), DescHelper.toDesc(float.class)));
					insertion.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "renderEmojiChar", DescHelper.toDescMethod(boolean.class, char.class, boolean.class, float.class, float.class, float.class, float.class, float.class, float.class), false));
					final LabelNode label = new LabelNode();
					insertion.add(new JumpInsnNode(Opcodes.IFEQ, label));
					insertion.add(new LdcInsnNode(10.0F));
					insertion.add(new InsnNode(Opcodes.FRETURN));
					insertion.add(label);
					method.instructions.insert(insertion);
				}
			});
		}
		return node;
	}
}