package net.teamfruit.emojicord;

import java.io.File;

import net.teamfruit.emojicord.compat.Compat;

public class Locations {
	public static final Locations instance = new Locations();

	public File getMinecraftDirectory() {
		final File gameDir = Compat.CompatMinecraft.getMinecraft().getGameDir();
		//try {
		//	gameDir = gameDir.getCanonicalFile();
		//} catch (final IOException e) {
		//	Log.log.error("Cannot get game directory: ", e);
		//}
		return gameDir;
	}

	public File getEmojicordDirectory() {
		return new File(getMinecraftDirectory(), Reference.MODID);
	}

	public File getCacheDirectory() {
		return new File(getEmojicordDirectory(), "cache");
	}

	public File getDictionaryDirectory() {
		return new File(getEmojicordDirectory(), "dictionary");
	}
}