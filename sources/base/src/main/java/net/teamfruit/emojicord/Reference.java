package net.teamfruit.emojicord;

import javax.annotation.Nonnull;

public class Reference {
	public static final @Nonnull String MODID = "emojicord";
	public static final @Nonnull String NAME = "Emojicord";
	public static final @Nonnull String VERSION = "${version}";
	public static final @Nonnull String FORGE = "${forgeversion}";
	public static final @Nonnull String MINECRAFT = "${mcversion}";
	public static final @Nonnull String PROXY_SERVER = "net.teamfruit.emojicord.CommonProxy";
	public static final @Nonnull String PROXY_CLIENT = "net.teamfruit.emojicord.ClientProxy";
	public static final @Nonnull String TRANSFORMER = "net.teamfruit.emojicord.asm.EmojicordTransformer";
}
