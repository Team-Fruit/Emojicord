package net.teamfruit.emojicord.asm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.DescHelper;
import net.teamfruit.emojicord.asm.lib.MethodMatcher;

public class GuiTextFieldVisitor extends ClassVisitor {
	private static class HookMethodVisitor extends MethodVisitor {
		public HookMethodVisitor(final @Nullable MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitCode() {
			{
				/*
				 0  iconst_1
				 1  putstatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.IsNewChatRendering : boolean [15]
				*/
				super.visitInsn(Opcodes.ICONST_1);
				super.visitFieldInsn(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "isTextFieldRendering", DescHelper.toDesc(boolean.class));
			}
			super.visitCode();
		}

		@Override
		public void visitInsn(final int opcode) {
			if (opcode==Opcodes.RETURN) {
				/*
				 0  iconst_0
				 1  putstatic net.teamfruit.emojicord.emoji.EmojiFontRenderer.IsNewChatRendering : boolean [15]
				*/
				super.visitInsn(Opcodes.ICONST_0);
				super.visitFieldInsn(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "isTextFieldRendering", DescHelper.toDesc(boolean.class));
			}
			super.visitInsn(opcode);
		}
	}

	private final MethodMatcher matcher;

	public GuiTextFieldVisitor(final @Nonnull String obfClassName, final @Nonnull ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
		this.matcher = new MethodMatcher(ClassName.fromBytecodeName(obfClassName), DescHelper.toDescMethod(void.class, int.class), ASMDeobfNames.GuiTextFieldDrawTextBox);
	}

	@Override
	public @Nullable MethodVisitor visitMethod(final int access, final @Nullable String name, final @Nullable String desc, final @Nullable String signature, final @Nullable String[] exceptions) {
		final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
		if (name==null||desc==null)
			return parent;
		if (this.matcher.match(name, desc))
			return new HookMethodVisitor(parent);
		return parent;
	}
}