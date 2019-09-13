/*
 * This class is from the OpenModsLib.
 * https://github.com/OpenMods/OpenModsLib
 *
 * Code Copyright (c) 2013 Open Mods
 * Code released under the MIT license
 * https://github.com/OpenMods/OpenModsLib/blob/master/LICENSE
 */
package net.teamfruit.emojicord.asm.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import net.teamfruit.emojicord.compat.Compat.CompatFMLDeobfuscatingRemapper;

public class VisitorHelper {

	public static interface TransformProvider {
		public abstract @Nonnull ClassVisitor createVisitor(@Nonnull String name, @Nonnull ClassVisitor cv);
	}

	public static ClassNode read(final @Nonnull byte[] bytes, final int readerFlags) {
		Validate.notNull(bytes);
		final ClassReader cr = new ClassReader(bytes);
		final ClassNode node = new ClassNode(Opcodes.ASM5);
		cr.accept(node, readerFlags);
		return node;
	}

	public static byte[] write(final @Nonnull ClassNode node, final int writerFlags) {
		// ASMライブラリのクラスでのgetClass().getClassLoader()はForgeのクラスを見つけることができない可能性があります。
		// インナークラスを作成してgetClass().getClassLoader()をLaunchClassLoaderにしましょう。
		final ClassWriter cw = new ClassWriter(writerFlags) {
			@Override
			protected String getCommonSuperClass(final @Nullable String type1, final @Nullable String type2) {
				if (type1==null||type2==null)
					throw new NullPointerException();
				Class<?> c, d;
				final ClassLoader classLoader = ClassWriter.class.getClassLoader();
				try {
					try {
						c = Class.forName(ClassName.BytecodeToSourcecodeName(type1), false, classLoader);
						d = Class.forName(ClassName.BytecodeToSourcecodeName(type2), false, classLoader);
					} catch (final ClassNotFoundException e) {
						final ClassLoader launchClassLoader = getClass().getClassLoader();
						try {
							c = Class.forName(ClassName.BytecodeToSourcecodeName(type1), false, launchClassLoader);
							d = Class.forName(ClassName.BytecodeToSourcecodeName(type2), false, launchClassLoader);
						} catch (final ClassNotFoundException e1) {
							throw new RuntimeException(String.format("ClassLoader: %s, LaunchClassLoader: %s", e.toString(), e1.toString()));
						}
					}
				} catch (final Exception e) {
					throw new RuntimeException(e.toString());
				}
				if (c.isAssignableFrom(d))
					return type1;
				if (d.isAssignableFrom(c))
					return type2;
				if (c.isInterface()||d.isInterface())
					return ClassName.SourcecodeToBytecodeName("java.lang.Object");
				else {
					do
						c = c.getSuperclass();
					while (!c.isAssignableFrom(d));
					return ClassName.SourcecodeToBytecodeName(c.getName());
				}
			}
		};
		node.accept(cw);
		return cw.toByteArray();
	}

	public static ClassNode apply(final @Nonnull ClassNode node, final @Nonnull TransformProvider context) {
		final ClassVisitor mod = context.createVisitor(node.name, node);
		try {
			node.accept(mod);
			return node;
		} catch (final StopTransforming e) {
			return node;
		}
	}

	public static boolean useSrgNames() {
		return CompatFMLDeobfuscatingRemapper.useSrgNames();
	}
}