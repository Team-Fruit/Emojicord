package net.teamfruit.emojicord;

import java.util.Optional;
import java.util.jar.Attributes;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class VersionReference {
	public static @Nonnull String VERSION = "${version}";
	public static @Nonnull String FORGE = "${forgeversion}";
	public static @Nonnull String MINECRAFT = "${mcversion}";

	static {
		ModList.get().getModContainerById(Reference.MODID)
				.map(e -> e.getModInfo())
				.ifPresent(e -> {
					VERSION = e.getVersion().toString();
					if (e instanceof ModInfo) {
						final ModFile modFile = ((ModInfo) e).getOwningFile().getFile();
						modFile.getLocator().findManifest(modFile.getFilePath()).ifPresent(s -> {
							final Attributes attributes = s.getMainAttributes();
							Optional.ofNullable(attributes.getValue("ForgeVersion")).ifPresent(v -> FORGE = v);
							Optional.ofNullable(attributes.getValue("MinecraftVersion")).ifPresent(v -> MINECRAFT = v);
						});
					}
				});
	}
}
