package net.teamfruit.emojicord;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;

@Config(modid = "emojiful", name = "Emojiful")
public class EmojicordConfig {
	@Name("render.enabled")
	@LangKey("config.emojicord.render.enabled")
	public static boolean renderEnabled = true;

	@Name("render.mipmap.enabled")
	@LangKey("config.emojicord.render.mipmap.enabled")
	public static boolean renderMipmapEnabled = true;

	@Name("render.mipmap.fastresize")
	@LangKey("config.emojicord.render.mipmap.fastresize")
	public static boolean renderMipmapFastResize = false;
}