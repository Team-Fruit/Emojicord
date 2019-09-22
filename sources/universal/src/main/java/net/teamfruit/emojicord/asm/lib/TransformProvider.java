package net.teamfruit.emojicord.asm.lib;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassVisitor;

public interface TransformProvider {
	default String getTransformClassName() {
		return StringUtils.substringBeforeLast(getClass().getSimpleName(), "Visitor");
	}

	@Nonnull
	ClassVisitor createVisitor(@Nonnull String name, @Nonnull ClassVisitor cv) throws StopTransforming;
}
