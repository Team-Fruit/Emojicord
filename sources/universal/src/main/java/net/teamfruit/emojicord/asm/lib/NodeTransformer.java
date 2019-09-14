package net.teamfruit.emojicord.asm.lib;

import java.util.function.Function;

import org.objectweb.asm.tree.ClassNode;

public interface NodeTransformer extends Function<ClassNode, ClassNode> {
	// 引数のnodeは書き換えても構いません。
	@Override
	public abstract ClassNode apply(ClassNode node);
}
