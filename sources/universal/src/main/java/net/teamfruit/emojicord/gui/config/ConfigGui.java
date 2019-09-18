package net.teamfruit.emojicord.gui.config;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiScreen;
import net.teamfruit.emojicord.EmojicordConfig;
import net.teamfruit.emojicord.Reference;
import net.teamfruit.emojicord.compat.Compat.CompatConfigCategory;
import net.teamfruit.emojicord.compat.Compat.CompatConfigElement;
import net.teamfruit.emojicord.compat.Compat.CompatGuiConfig;

public class ConfigGui extends CompatGuiConfig {
	public ConfigGui(final @Nullable GuiScreen parent) {
		super(parent, getConfigElements(), Reference.MODID, false, false, EmojicordConfig.spec.getConfigFile().getName());
	}

	private static @Nonnull List<CompatConfigElement> getConfigElements() {
		final List<CompatConfigElement> list = Lists.newArrayList();

		for (final String cat : EmojicordConfig.spec.getConfiguration().getCategoryNames()) {
			final CompatConfigCategory cc = EmojicordConfig.spec.getConfiguration().getCategory(cat);

			if (cc.isChild())
				continue;

			list.add(CompatConfigElement.fromCategory(cc));
		}

		return list;
	}
}