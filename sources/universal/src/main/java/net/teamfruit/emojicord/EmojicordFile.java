package net.teamfruit.emojicord;

import java.io.File;

import net.teamfruit.emojicord.compat.Compat;

public class EmojicordFile {
	public static final EmojicordFile instance = new EmojicordFile();

	public File getMinecraftDirectory() {
		return Compat.CompatMinecraft.getMinecraft().mcDataDir;
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
