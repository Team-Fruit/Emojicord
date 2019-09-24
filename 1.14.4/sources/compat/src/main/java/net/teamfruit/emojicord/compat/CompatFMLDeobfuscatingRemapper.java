package net.teamfruit.emojicord.compat;

import javax.annotation.Nonnull;

import net.teamfruit.emojicord.asm.EmojicordCoreService;

public class CompatFMLDeobfuscatingRemapper {
	public static @Nonnull String mapMethodDesc(@Nonnull final String desc) {
		return desc;
	}

	public static @Nonnull String mapFieldName(@Nonnull final String owner, @Nonnull final String name, @Nonnull final String desc) {
		return name;
	}

	public static @Nonnull String unmap(@Nonnull final String typeName) {
		return typeName;
	}

	public static @Nonnull String mapMethodName(@Nonnull final String owner, @Nonnull final String name, @Nonnull final String desc) {
		return name;
	}

	public static boolean useMcpNames() {
		return EmojicordCoreService.Srg2Mcp!=null;
	}
}
