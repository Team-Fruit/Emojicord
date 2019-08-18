package net.teamfruit.emojicord.asm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.DescHelper;
import net.teamfruit.emojicord.asm.lib.MethodMatcher;

public class GuiNewChatVisitor extends ClassVisitor {
	private static class DrawChatHookMethodVisitor extends MethodVisitor {
		public DrawChatHookMethodVisitor(final @Nullable MethodVisitor mv) {
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
				super.visitFieldInsn(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "IsNewChatRendering", DescHelper.toDesc(boolean.class));
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
				super.visitFieldInsn(Opcodes.PUTSTATIC, ClassName.of("net.teamfruit.emojicord.emoji.EmojiFontRenderer").getBytecodeName(), "IsNewChatRendering", DescHelper.toDesc(boolean.class));
			}
			super.visitInsn(opcode);
		}
	}

	private final MethodMatcher drawchatmatcher;

	public GuiNewChatVisitor(final @Nonnull String obfClassName, final @Nonnull ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
		this.drawchatmatcher = new MethodMatcher(ClassName.fromBytecodeName(obfClassName), DescHelper.toDescMethod(void.class, int.class), ASMDeobfNames.GuiNewChatDrawChat);
	}

	@Override
	public @Nullable FieldVisitor visitField(final int access, @Nullable final String name, @Nullable final String desc, @Nullable final String signature, @Nullable final Object value) {
		return super.visitField(access, name, desc, signature, value);
	}

	@Override
	public @Nullable MethodVisitor visitMethod(final int access, final @Nullable String name, final @Nullable String desc, final @Nullable String signature, final @Nullable String[] exceptions) {
		final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
		if (name==null||desc==null)
			return parent;
		if (this.drawchatmatcher.match(name, desc))
			return new DrawChatHookMethodVisitor(parent);
		return parent;
	}
}