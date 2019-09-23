package net.teamfruit.emojicord.gui.config;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.teamfruit.emojicord.compat.Compat.CompatModGuiFactory;
import net.teamfruit.emojicord.compat.Compat.CompatScreen;

public class ConfigGuiFactory extends CompatModGuiFactory {
	@Override
	public void initialize(final @Nullable Minecraft minecraftInstance) {

	}

	@Override
	public @Nullable Class<?> mainConfigGuiClassCompat() {
		return ConfigGui.class;
	}

	@Override
	public CompatScreen createConfigGuiCompat(final CompatScreen parentScreen) {
		return new CompatScreen(new ConfigGui(parentScreen));
	}
}