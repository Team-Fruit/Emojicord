package net.teamfruit.emojicord;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;

@Config(modid = "emojiful", name = "Emojiful")
public class EmojifulConfig {
	@Name("emoji_render")
	@LangKey("config.emojiful.emoji_render")
	public static boolean renderEmoji = true;
}