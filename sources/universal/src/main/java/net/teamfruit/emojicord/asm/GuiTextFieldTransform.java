package net.teamfruit.emojicord.asm;

import java.util.Optional;
import java.util.function.Supplier;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.teamfruit.emojicord.asm.lib.ASMValidate;
import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.DescHelper;
import net.teamfruit.emojicord.asm.lib.FieldMatcher;
import net.teamfruit.emojicord.asm.lib.INodeTreeTransformer;
import net.teamfruit.emojicord.asm.lib.MethodMatcher;
import net.teamfruit.emojicord.asm.lib.RefName;
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
		validator.test("drawTextBox.return");
		validator.test("drawTextBox.suggestion", !CompatVersion.version().newer(CompatBaseVersion.V13));

		if (!CompatVersion.version().newer(CompatBaseVersion.V13)) {
			final FieldMatcher matcher = new FieldMatcher(getClassName(), DescHelper.toDesc(ClassName.of("java.lang.String")), RefName.name("suggestion"));
			if (node.fields.stream().noneMatch(matcher))
				node.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "suggestion", DescHelper.toDesc(ClassName.of("java.lang.String")), null, null));
		}

		{
			final MethodMatcher matcher = ((Supplier<MethodMatcher>) () -> {
				if (!CompatVersion.version().newer(CompatBaseVersion.V13))
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
						validator.checks("drawTextBox.return");
					}
				});
				if (!CompatVersion.version().newer(CompatBaseVersion.V13)) {
					final MethodMatcher matcher0 = new MethodMatcher(ClassName.of("net.minecraft.client.gui.FontRenderer"), DescHelper.toDescMethod(int.class, ClassName.of("java.lang.String"), float.class, float.class, int.class), ASMDeobfNames.FontRendererDrawStringWithShadow);
					final Optional<AbstractInsnNode> marker1 = VisitorHelper.stream(method.instructions).filter(e -> {
						return e instanceof VarInsnNode&&e.getOpcode()==Opcodes.ISTORE&&((VarInsnNode) e).var==10;
					}).findFirst();
					VisitorHelper.stream(method.instructions)
							.filter(matcher0.insnMatcher())
							.filter(e -> !marker1.isPresent()||method.instructions.indexOf(e)>method.instructions.indexOf(marker1.get()))
							.findFirst().ifPresent(marker -> {
								final AbstractInsnNode marker0 = marker.getNext().getNext().getNext();
								{
									/*
									 433  iload_1 [i]
									 434  invokevirtual net.minecraft.client.gui.FontRenderer.drawStringWithShadow(java.lang.String, float, float, int) : int [320]
									 437  istore 9 [j1]
									 438  [Label Node]
									
									 439  aload_0 [this]
									 440  getfield net.minecraft.client.gui.GuiTextField.fontRenderer : net.minecraft.client.gui.FontRenderer [71]
									 443  iload 10 [flag2]
									 445  aload_0 [this]
									 446  getfield net.minecraft.client.gui.GuiTextField.suggestion : java.lang.String [327]
									 449  iload 11 [k1]
									 451  iload 8 [i1]
									 453  invokestatic net.teamfruit.emojicord.compat.Compat$CompatTextFieldWidget.renderSuggestion(net.minecraft.client.gui.FontRenderer, boolean, java.lang.String, int, int) : void [329]
									*/
									final InsnList insertion = new InsnList();
									insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
									insertion.add(new FieldInsnNode(Opcodes.GETFIELD, getClassName().getBytecodeName(), ASMDeobfNames.GuiTextFieldFontRenderer.name(), DescHelper.toDesc(ClassName.of("net.minecraft.client.gui.FontRenderer"))));
									insertion.add(new VarInsnNode(Opcodes.ILOAD, 10));
									insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
									insertion.add(new FieldInsnNode(Opcodes.GETFIELD, getClassName().getBytecodeName(), "suggestion", DescHelper.toDesc(ClassName.of("java.lang.String"))));
									insertion.add(new VarInsnNode(Opcodes.ILOAD, 11));
									insertion.add(new VarInsnNode(Opcodes.ILOAD, 8));
									insertion.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ClassName.of("net.teamfruit.emojicord.compat.Compat$CompatTextFieldWidget").getBytecodeName(), "renderSuggestion", DescHelper.toDescMethod(void.class, ClassName.of("net.minecraft.client.gui.FontRenderer"), boolean.class, ClassName.of("java.lang.String"), int.class, int.class), false));
									method.instructions.insert(marker0, insertion);
									validator.check("drawTextBox.suggestion");
								}
							});
				}
			});
		}
		validator.validate();
		return node;
	}
}