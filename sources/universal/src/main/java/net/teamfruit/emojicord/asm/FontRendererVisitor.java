package net.teamfruit.emojicord.asm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.DescHelper;
import net.teamfruit.emojicord.asm.lib.MethodMatcher;

public class FontRendererVisitor extends ClassVisitor {
	private static class RenderStringAtPosHookMethodVisitor extends MethodVisitor {
		private final @Nonnull MethodMatcher matcher;
		private boolean firstMatch = true;

		public RenderStringAtPosHookMethodVisitor(final @Nullable MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
			this.matcher = new MethodMatcher(ClassName.of("net.minecraft.client.gui.FontRenderer").toMappedName(), DescHelper.toDescMethod(float.class, char.class, boolean.class), ASMDeobfNames.FontRendererRenderChar);
		}

		@Override
		public void visitCode() {
			{
				/*
				 0  aload_1 [text]
				 1  invokestatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.updateEmojiContext(java.lang.String) : java.lang.String [61]
				 4  astore_1 [text]
				*/
				super.visitVarInsn(Opcodes.ALOAD, 1);
				super.visitMethodInsn(Opcodes.INVOKESTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "updateEmojiContext", DescHelper.toDescMethod(ClassName.of("java.lang.String").getBytecodeName(), ClassName.of("java.lang.String").getBytecodeName()), false);
				super.visitVarInsn(Opcodes.ASTORE, 1);
			}
			super.visitCode();
		}

		@Override
		public void visitMethodInsn(final int opcode, final @Nullable String owner, final @Nullable String name, final @Nullable String desc, final boolean itf) {
			if (name!=null&&desc!=null&&this.matcher.match(name, desc)&&this.firstMatch) {
				this.firstMatch = false;
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
				super.visitVarInsn(Opcodes.ISTORE, 20);
				super.visitVarInsn(Opcodes.ISTORE, 21);
				super.visitVarInsn(Opcodes.ASTORE, 22);

				super.visitVarInsn(Opcodes.ILOAD, 3);
				super.visitMethodInsn(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "index", DescHelper.toDesc(int.class), false);
				super.visitVarInsn(Opcodes.ILOAD, 2);
				super.visitMethodInsn(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "shadow", DescHelper.toDesc(boolean.class), false);

				super.visitVarInsn(Opcodes.ALOAD, 22);
				super.visitVarInsn(Opcodes.ILOAD, 21);
				super.visitVarInsn(Opcodes.ILOAD, 20);
				super.visitMethodInsn(opcode, owner, name, desc, itf);
				super.visitVarInsn(Opcodes.FSTORE, 23);

				super.visitInsn(Opcodes.ICONST_0);
				super.visitMethodInsn(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "shadow", DescHelper.toDesc(boolean.class), false);

				super.visitVarInsn(Opcodes.FLOAD, 23);
			} else
				super.visitMethodInsn(opcode, owner, name, desc, itf);
		}
	}

	private static class GetStringWidthHookMethodVisitor extends MethodVisitor {
		public GetStringWidthHookMethodVisitor(final @Nullable MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitCode() {
			{
				/*
				 0  aload_1 [text]
				 1  invokestatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.updateEmojiContext(java.lang.String) : java.lang.String [61]
				 4  astore_1 [text]
				*/
				super.visitVarInsn(Opcodes.ALOAD, 1);
				super.visitMethodInsn(Opcodes.INVOKESTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "updateEmojiContext", DescHelper.toDescMethod(ClassName.of("java.lang.String").getBytecodeName(), ClassName.of("java.lang.String").getBytecodeName()), false);
				super.visitVarInsn(Opcodes.ASTORE, 1);
			}
			super.visitCode();
		}
	}

	private static class GetCharWidthHookMethodVisitor extends MethodVisitor {
		public GetCharWidthHookMethodVisitor(final @Nullable MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitCode() {
			{
				/*
				 0  iload_1 [character]
				 1  bipush 63
				 3  if_icmpne 9
				 6  bipush 10
				 8  ireturn
				 9  -
				*/
				super.visitVarInsn(Opcodes.ILOAD, 1);
				super.visitIntInsn(Opcodes.BIPUSH, '\u0000');
				final Label label = new Label();
				super.visitJumpInsn(Opcodes.IF_ICMPNE, label);
				super.visitIntInsn(Opcodes.BIPUSH, 10);
				super.visitInsn(Opcodes.IRETURN);
				super.visitLabel(label);
			}
			super.visitCode();
		}
	}

	private static class RenderCharHookMethodVisitor extends MethodVisitor {
		public RenderCharHookMethodVisitor(final @Nullable MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitCode() {
			{
				/*
				 0  aload_0 [this]
				 1  iload_1 [c]
				 2  iload_2 [italic]
				 3  invokestatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.renderEmojiChar(net.minecraft.client.gui.FontRenderer, char, boolean) : boolean [206]
				 6  ifeq 12
				 9  ldc <Float 10.0> [21]
				11  freturn
				12  -
				*/
				super.visitVarInsn(Opcodes.ALOAD, 0);
				super.visitVarInsn(Opcodes.ILOAD, 1);
				super.visitVarInsn(Opcodes.ILOAD, 2);
				super.visitMethodInsn(Opcodes.INVOKESTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "renderEmojiChar", DescHelper.toDescMethod(boolean.class, ClassName.of("net.minecraft.client.gui.FontRenderer").getBytecodeName(), char.class, boolean.class), false);
				final Label label = new Label();
				super.visitJumpInsn(Opcodes.IFEQ, label);
				super.visitLdcInsn(10.0F);
				super.visitInsn(Opcodes.FRETURN);
				super.visitLabel(label);
			}
			super.visitCode();
		}
	}

	private final MethodMatcher renderstringatposmatcher;
	private final MethodMatcher getstringwidthmatcher;
	private final MethodMatcher getcharwidthmatcher;
	private final MethodMatcher rendercharmatcher;

	public FontRendererVisitor(final @Nonnull String obfClassName, final @Nonnull ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
		this.renderstringatposmatcher = new MethodMatcher(ClassName.fromBytecodeName(obfClassName), DescHelper.toDescMethod(void.class, ClassName.of("java.lang.String").getBytecodeName(), boolean.class), ASMDeobfNames.FontRendererRenderStringAtPos);
		this.getstringwidthmatcher = new MethodMatcher(ClassName.fromBytecodeName(obfClassName), DescHelper.toDescMethod(int.class, ClassName.of("java.lang.String").getBytecodeName()), ASMDeobfNames.FontRendererGetStringWidth);
		this.getcharwidthmatcher = new MethodMatcher(ClassName.fromBytecodeName(obfClassName), DescHelper.toDescMethod(int.class, char.class), ASMDeobfNames.FontRendererGetCharWidth);
		this.rendercharmatcher = new MethodMatcher(ClassName.fromBytecodeName(obfClassName), DescHelper.toDescMethod(float.class, char.class, boolean.class), ASMDeobfNames.FontRendererRenderChar);
	}

	@Override
	public @Nullable MethodVisitor visitMethod(final int access, final @Nullable String name, final @Nullable String desc, final @Nullable String signature, final @Nullable String[] exceptions) {
		final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
		if (name==null||desc==null)
			return parent;
		if (this.renderstringatposmatcher.match(name, desc))
			return new RenderStringAtPosHookMethodVisitor(parent);
		if (this.getstringwidthmatcher.match(name, desc))
			return new GetStringWidthHookMethodVisitor(parent);
		if (this.getcharwidthmatcher.match(name, desc))
			return new GetCharWidthHookMethodVisitor(parent);
		if (this.rendercharmatcher.match(name, desc))
			return new RenderCharHookMethodVisitor(parent);
		return parent;
	}
}