package net.teamfruit.emojicord.asm;

import java.util.Optional;
import java.util.function.Supplier;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
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

	/*
	public static String insnToString(final AbstractInsnNode insn) {
		insn.accept(mp);
		final StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();
		return sw.toString();
	}
	
	private static Printer printer = new Textifier();
	private static TraceMethodVisitor mp = new TraceMethodVisitor(printer);
	*/

	@Override
	public ClassNode apply(final ClassNode node) {
		final ASMValidate validator = ASMValidate.create(getSimpleName());
		validator.test("drawTextBox.suggestion", !CompatVersion.version().newer(CompatBaseVersion.V13));
		validator.test("drawTextBox.suggestion.field", !CompatVersion.version().newer(CompatBaseVersion.V13));

		// Compat for IntelliInput
		final int o = EmojicordTransformer.intelliInputDeferred.hasTarget() ? 1 : 0;

		if (!CompatVersion.version().newer(CompatBaseVersion.V13)) {
			final FieldMatcher matcher = new FieldMatcher(getClassName(), DescHelper.toDesc(ClassName.of("java.lang.String")), RefName.name("suggestion"));
			if (node.fields.stream().noneMatch(matcher)) {
				node.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "suggestion", DescHelper.toDesc(ClassName.of("java.lang.String")), null, null));
				validator.check("drawTextBox.suggestion.field");
			}
		}

		{
			final MethodMatcher matcher = ((Supplier<MethodMatcher>) () -> {
				if (!CompatVersion.version().newer(CompatBaseVersion.V13))
					return new MethodMatcher(getClassName(), DescHelper.toDescMethod(void.class), ASMDeobfNames.GuiTextFieldDrawTextBox);
				else
					return new MethodMatcher(getClassName(), DescHelper.toDescMethod(void.class, int.class, int.class, float.class), ASMDeobfNames.GuiTextFieldDrawTextField);
			}).get();
			node.methods.stream().filter(matcher).forEach(method -> {
				if (!CompatVersion.version().newer(CompatBaseVersion.V13)) {
					final MethodMatcher matcher0 = ((Supplier<MethodMatcher>) () -> {
						if (CompatVersion.version().older(CompatBaseVersion.V7))
							return new MethodMatcher(ClassName.of("net.minecraft.client.gui.FontRenderer"), DescHelper.toDescMethod(int.class, ClassName.of("java.lang.String"), int.class, int.class, int.class), ASMDeobfNames.FontRendererDrawStringWithShadow);
						else
							return new MethodMatcher(ClassName.of("net.minecraft.client.gui.FontRenderer"), DescHelper.toDescMethod(int.class, ClassName.of("java.lang.String"), float.class, float.class, int.class), ASMDeobfNames.FontRendererDrawStringWithShadow);
					}).get();
					final Optional<AbstractInsnNode> marker1 = VisitorHelper.stream(method.instructions).filter(e -> {
						return e instanceof VarInsnNode&&e.getOpcode()==Opcodes.ISTORE&&((VarInsnNode) e).var==o+10;
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

									/*
									final InsnList inList = method.instructions;
									for (int i = 0; i<inList.size(); i++)
										Log.log.info(insnToString(inList.get(i)));
									*/

									final InsnList insertion = new InsnList();
									insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
									insertion.add(new FieldInsnNode(Opcodes.GETFIELD, getClassName().getBytecodeName(), ASMDeobfNames.GuiTextFieldFontRenderer.name(), DescHelper.toDesc(ClassName.of("net.minecraft.client.gui.FontRenderer"))));
									insertion.add(new VarInsnNode(Opcodes.ILOAD, o+10));
									insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
									insertion.add(new FieldInsnNode(Opcodes.GETFIELD, getClassName().getBytecodeName(), "suggestion", DescHelper.toDesc(ClassName.of("java.lang.String"))));
									insertion.add(new VarInsnNode(Opcodes.ILOAD, o+11));
									insertion.add(new VarInsnNode(Opcodes.ILOAD, o+8));
									insertion.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ClassName.of("net.teamfruit.emojicord.compat.CompatGui$CompatTextFieldWidget").getBytecodeName(), "renderSuggestion", DescHelper.toDescMethod(void.class, ClassName.of("net.minecraft.client.gui.FontRenderer"), boolean.class, ClassName.of("java.lang.String"), int.class, int.class), false));
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