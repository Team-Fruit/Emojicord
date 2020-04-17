package net.teamfruit.emojicord.gui.config;

import javax.annotation.Nullable;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.minecraft.client.Minecraft;
import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.compat.CompatGui;
import net.teamfruit.emojicord.util.DynamicClassUtils;

public class ConfigGuiFactory extends CompatGui.CompatModGuiFactory {
	private final Supplier<Class<?>> configGuiClassSupplier = Suppliers.memoize(() -> {
		try {
			return DynamicClassUtils.instance.createConstructorWrappedClass(ConfigGui.class, Class.forName("net.minecraft.client.gui.GuiScreen"), CompatGui.CompatScreen.class);
		} catch (final Throwable e) {
			Log.log.error("Failed to create ASM wrapped class", e);
		}
		return null;
	});

	@Override
	public void initialize(final @Nullable Minecraft minecraftInstance) {
	}

	@Override
	public @Nullable Class<?> mainConfigGuiClassCompat() {
		return this.configGuiClassSupplier.get();
	}

	@Override
	public CompatGui.CompatScreen createConfigGuiCompat(final CompatGui.CompatScreen parentScreen) {
		return new CompatGui.CompatScreen(new ConfigGui(parentScreen));
	}
}