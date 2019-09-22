package net.teamfruit.emojicord.asm.lib;

import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;

public interface NodeTransformer extends Function<ClassNode, ClassNode> {
	default String getTransformClassName() {
		return StringUtils.substringBeforeLast(getClass().getSimpleName(), "Transform");
	}

	default <T> Stream<AbstractInsnNode> stream(final InsnList instructions) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(instructions.iterator(), 0), false);
	}

	// 引数のnodeは書き換えても構いません。
	@Override
	public abstract ClassNode apply(ClassNode node);
}
