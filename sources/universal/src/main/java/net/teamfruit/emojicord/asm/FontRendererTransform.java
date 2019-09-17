package net.teamfruit.emojicord.asm;

import java.util.Optional;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
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
import net.teamfruit.emojicord.compat.CompatBaseVersion;
import net.teamfruit.emojicord.compat.CompatVersion;

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

				if (CompatVersion.version().older(CompatBaseVersion.V11)) {
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
								final AbstractInsnNode marker0 = marker.getPrevious().getPrevious().getPrevious();

								final InsnList insertion = new InsnList();

								insertion.add(new VarInsnNode(Opcodes.ILOAD, 3));
								insertion.add(new FieldInsnNode(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "index", DescHelper.toDesc(int.class)));
								insertion.add(new VarInsnNode(Opcodes.ILOAD, 2));
								insertion.add(new FieldInsnNode(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "shadow", DescHelper.toDesc(boolean.class)));

								method.instructions.insertBefore(marker0, insertion);
							}
							{
								final InsnList insertion = new InsnList();

								insertion.add(new VarInsnNode(Opcodes.FSTORE, 23));

								insertion.add(new InsnNode(Opcodes.ICONST_0));
								insertion.add(new FieldInsnNode(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "shadow", DescHelper.toDesc(boolean.class)));

								insertion.add(new VarInsnNode(Opcodes.FLOAD, 23));

								method.instructions.insert(marker, insertion);
							}
						}
					});
				} else {
					final MethodMatcher matcher0 = new MethodMatcher(ClassName.of("net.minecraft.client.gui.FontRenderer").toMappedName(), DescHelper.toDescMethod(ClassName.of("net.minecraft.client.gui.fonts.IGlyph").toMappedName(), char.class), ASMDeobfNames.FontFindGlyph);
					final Optional<AbstractInsnNode> marker0 = Streams.stream(method.instructions.iterator()).filter(e -> {
						return e instanceof MethodInsnNode
								&&matcher0.match(((MethodInsnNode) e).name, ((MethodInsnNode) e).desc);
					}).findFirst();
					final Optional<AbstractInsnNode> marker1 = Streams.stream(method.instructions.iterator()).filter(e -> {
						return e instanceof VarInsnNode&&e.getOpcode()==Opcodes.ASTORE&&((VarInsnNode) e).var==26;
					}).findFirst();
					if (marker0.isPresent()&&marker1.isPresent()) {
						/*
						 367  iload 24 [character]
						 369  iload 23 [index]
						 371  invokestatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.getEmojiGlyph(char, int) : net.teamfruit.emojicord.emoji.EmojiFontRenderer$EmojiGlyph [208]
						 374  astore 27 [emojiGlyph]

						 376  aload 27 [emojiGlyph]
						 378  ifnull 392
						 381  aload 27 [emojiGlyph]
						 383  astore 25 [glyph]
						 385  aload 27 [emojiGlyph]
						 387  astore 26 [texturedglyph]
						 389  goto 438

						 392  aload_0 [this]
						 393  getfield net.minecraft.client.gui.FontRenderer.font : net.minecraft.client.gui.fonts.Font [45]
						 396  iload 24 [character]
						 398  invokevirtual net.minecraft.client.gui.fonts.Font.findGlyph(char) : net.minecraft.client.gui.fonts.IGlyph [214]
						 401  astore 25 [glyph]
						 403  iload 17 [obfuscated]
						 405  ifeq 427
						 408  iload 24 [character]
						 410  bipush 32
						 412  if_icmpeq 427
						 415  aload_0 [this]
						 416  getfield net.minecraft.client.gui.FontRenderer.font : net.minecraft.client.gui.fonts.Font [45]
						 419  aload 25 [glyph]
						 421  invokevirtual net.minecraft.client.gui.fonts.Font.obfuscate(net.minecraft.client.gui.fonts.IGlyph) : net.minecraft.client.gui.fonts.TexturedGlyph [218]
						 424  goto 436
						 427  aload_0 [this]
						 428  getfield net.minecraft.client.gui.FontRenderer.font : net.minecraft.client.gui.fonts.Font [45]
						 431  iload 24 [character]
						 433  invokevirtual net.minecraft.client.gui.fonts.Font.getGlyph(char) : net.minecraft.client.gui.fonts.TexturedGlyph [222]
						 436  astore 26 [texturedglyph]

						 438 -
						*/
						final AbstractInsnNode marker2 = marker0.get().getPrevious().getPrevious().getPrevious();
						final AbstractInsnNode marker3 = marker1.get();
						final LabelNode label0 = new LabelNode();

						final InsnList insertion = new InsnList();

						final int index = 40;

						insertion.add(new VarInsnNode(Opcodes.ILOAD, 24));
						insertion.add(new VarInsnNode(Opcodes.ILOAD, 23));
						insertion.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "getEmojiGlyph", DescHelper.toDescMethod(ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer$EmojiGlyph").getBytecodeName(), char.class, int.class), false));
						insertion.add(new VarInsnNode(Opcodes.ASTORE, index));

						insertion.add(new VarInsnNode(Opcodes.ALOAD, index));
						final LabelNode label1 = new LabelNode();
						insertion.add(new JumpInsnNode(Opcodes.IFNULL, label1));
						insertion.add(new VarInsnNode(Opcodes.ALOAD, index));
						insertion.add(new VarInsnNode(Opcodes.ASTORE, 25));
						insertion.add(new VarInsnNode(Opcodes.ALOAD, index));
						insertion.add(new VarInsnNode(Opcodes.ASTORE, 26));
						insertion.add(new JumpInsnNode(Opcodes.GOTO, label0));

						insertion.add(label1);

						method.instructions.insertBefore(marker2, insertion);
						method.instructions.insert(marker3, label0);
					}
				}
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
					if (CompatVersion.version().older(CompatBaseVersion.V11)) {
						insertion.add(new IntInsnNode(Opcodes.BIPUSH, 10));
						insertion.add(new InsnNode(Opcodes.IRETURN));
					} else {
						insertion.add(new LdcInsnNode(10f));
						insertion.add(new InsnNode(Opcodes.FRETURN));
					}
					insertion.add(label);
					method.instructions.insert(insertion);
				}
			});
		}
		if (CompatVersion.version().older(CompatBaseVersion.V11)) {
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
		} else {
			//final MethodMatcher matcher = new MethodMatcher(ClassName.fromBytecodeName(node.name), DescHelper.toDescMethod(void.class, ClassName.of("net.minecraft.client.gui.fonts.TexturedGlyph").toMappedName(), boolean.class, boolean.class, float.class, float.class, float.class, ClassName.of("net.minecraft.client.renderer.BufferBuilder").toMappedName(), float.class, float.class, float.class, float.class), ASMDeobfNames.FontRendererRenderGlyph);
			//final MethodMatcher matcher = new MethodMatcher(ClassName.of("net.minecraft.client.gui.fonts.IGlyph net.minecraft.client.gui.fonts.Font"), DescHelper.toDescMethod(ClassName.of("net.minecraft.client.gui.fonts.IGlyph").toMappedName(), char.class), ASMDeobfNames.FontFindGlyph);
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