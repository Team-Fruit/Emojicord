package net.teamfruit.emojicord.util;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.teamfruit.emojicord.Reference;
import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.DescHelper;

public class DynamicClassUtils {
	public static final DynamicClassUtils instance = new DynamicClassUtils();

	private final DynamicClassLoader classLoader;
	private final AtomicInteger counter = new AtomicInteger();

	public DynamicClassUtils() {
		this.classLoader = new DynamicClassLoader(getClass().getClassLoader());
	}

	public Class<?> createConstructorWrappedClass(final Class<?> classBase, final Class<?> classRaw, final Class<?> classWrapped) {
		final ClassNode node = new ClassNode(Opcodes.ASM5);

		final String name = Reference.MODID.toUpperCase(Locale.ROOT)+"_ASM_"+this.counter.getAndIncrement()+"_"+classBase.getSimpleName();

		node.version = Opcodes.V1_8;
		node.access = Opcodes.ACC_PUBLIC;
		node.signature = DescHelper.toDesc(name);
		node.name = name;
		node.superName = ClassName.of(classBase.getName()).getBytecodeName();

		final MethodNode method = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", DescHelper.toDescMethod(void.class, ClassName.of(classRaw.getName()).getBytecodeName()), null, null);
		{
			/*
			  0  aload_0 [this]
			  1  new net.teamfruit.emojicord.compat.Compat$CompatScreen [8]
			  4  dup
			  5  aload_1 [parent]
			  6  invokespecial net.teamfruit.emojicord.compat.Compat$CompatScreen(net.minecraft.client.gui.GuiScreen) [10]
			  9  invokespecial net.teamfruit.emojicord.gui.config.ConfigGui(net.teamfruit.emojicord.compat.Compat$CompatScreen) [12]
			 12  return
			 */
			final InsnList insertion = new InsnList();
			insertion.add(new VarInsnNode(Opcodes.ALOAD, 0));
			insertion.add(new TypeInsnNode(Opcodes.NEW, ClassName.of(classWrapped.getName()).getBytecodeName()));
			insertion.add(new InsnNode(Opcodes.DUP));
			insertion.add(new VarInsnNode(Opcodes.ALOAD, 1));
			insertion.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ClassName.of(classWrapped.getName()).getBytecodeName(), "<init>", DescHelper.toDescMethod(void.class, ClassName.of(classRaw.getName()).getBytecodeName()), false));
			insertion.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ClassName.of(classBase.getName()).getBytecodeName(), "<init>", DescHelper.toDescMethod(void.class, ClassName.of(classWrapped.getName()).getBytecodeName()), false));
			insertion.add(new InsnNode(Opcodes.RETURN));
			method.instructions.insert(insertion);
		}
		node.methods.add(method);

		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		node.accept(cw);

		return this.classLoader.defineClass(name, cw.toByteArray());
	}

	private static class DynamicClassLoader extends ClassLoader {
		public DynamicClassLoader(final ClassLoader parent) {
			super(parent);
		}

		public Class<?> defineClass(final String name, final byte[] b) {
			return defineClass(name, b, 0, b.length);
		}
	}
}

/*
class WrappedConfigGui extends ConfigGui {
	public WrappedConfigGui(final GuiScreen parent) {
		super(new CompatScreen(parent));
	}
}
*/