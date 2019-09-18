package net.teamfruit.emojicord.asm.lib;

import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;

public interface NodeTransformer extends Function<ClassNode, ClassNode> {
	default <T> Stream<AbstractInsnNode> stream(final InsnList instructions) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(instructions.iterator(), 0), false);
	}

	// 引数のnodeは書き換えても構いません。
	@Override
	public abstract ClassNode apply(ClassNode node);
}
