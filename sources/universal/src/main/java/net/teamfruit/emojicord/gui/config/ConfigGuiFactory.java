package net.teamfruit.emojicord.gui.config;

import javax.annotation.Nullable;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.minecraft.client.Minecraft;
import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.compat.Compat.CompatModGuiFactory;
import net.teamfruit.emojicord.compat.Compat.CompatScreen;
import net.teamfruit.emojicord.util.DynamicClassUtils;

public class ConfigGuiFactory extends CompatModGuiFactory {
	private final Supplier<Class<?>> configGuiClassSupplier = Suppliers.memoize(() -> {
		try {
			return DynamicClassUtils.instance.createConstructorWrappedClass(ConfigGui.class, Class.forName("net.minecraft.client.gui.GuiScreen"), CompatScreen.class);
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
	public CompatScreen createConfigGuiCompat(final CompatScreen parentScreen) {
		return new CompatScreen(new ConfigGui(parentScreen));
	}
}