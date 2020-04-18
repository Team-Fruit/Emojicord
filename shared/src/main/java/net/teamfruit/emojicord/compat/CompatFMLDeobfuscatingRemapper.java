package net.teamfruit.emojicord.compat;

import javax.annotation.Nonnull;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class CompatFMLDeobfuscatingRemapper {
	public static @Nonnull String mapDesc(@Nonnull final String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapDesc(desc);
	}

	public static @Nonnull String mapMethodDesc(@Nonnull final String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc);
	}

	public static @Nonnull String mapFieldName(@Nonnull final String owner, @Nonnull final String name, @Nonnull final String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(owner, name, desc);
	}

	public static @Nonnull String mapMethodName(@Nonnull final String owner, @Nonnull final String name, @Nonnull final String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc);
	}

	public static @Nonnull String unmap(@Nonnull final String typeName) {
		return FMLDeobfuscatingRemapper.INSTANCE.unmap(typeName);
	}

	public static boolean useMcpNames() {
		final Boolean deobfuscated = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
		return deobfuscated!=null&&deobfuscated;
	}
}
