package net.teamfruit.emojicord.gui.config;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.teamfruit.emojicord.EmojicordConfig;
import net.teamfruit.emojicord.Reference;
import net.teamfruit.emojicord.compat.CompatConfig;
import net.teamfruit.emojicord.compat.CompatGui;

public class ConfigGui extends CompatGui.CompatGuiConfig {
	public ConfigGui(final @Nullable CompatGui.CompatScreen parent) {
		super(parent, getConfigElements(), Reference.MODID, false, false, EmojicordConfig.spec.getConfigFile().getName());
	}

	private static @Nonnull List<CompatConfig.CompatConfigElement> getConfigElements() {
		final List<CompatConfig.CompatConfigElement> list = Lists.newArrayList();

		for (final String cat : EmojicordConfig.spec.getConfiguration().getCategoryNames()) {
			final CompatConfig.CompatConfigCategory cc = EmojicordConfig.spec.getConfiguration().getCategory(cat);

			if (cc.isChild())
				continue;

			list.add(CompatConfig.CompatConfigElement.fromCategory(cc));
		}

		return list;
	}
}