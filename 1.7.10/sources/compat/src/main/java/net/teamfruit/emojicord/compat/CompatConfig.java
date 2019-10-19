package net.teamfruit.emojicord.compat;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.config.IConfigElement;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CompatConfig {

	public static class CompatConfiguration {
		public final Configuration config;

		public CompatConfiguration(final Configuration config) {
			this.config = config;
		}

		public Set<String> getCategoryNames() {
			return this.config.getCategoryNames();
		}

		public CompatConfigCategory getCategory(final String category) {
			return new CompatConfigCategory(this.config.getCategory(category));
		}
	}

	public static class CompatConfigCategory {
		public final ConfigCategory category;

		public CompatConfigCategory(final ConfigCategory category) {
			this.category = category;
		}

		public boolean isChild() {
			return this.category.isChild();
		}
	}

	public static class CompatConfigProperty {
		public final Property property;

		public CompatConfigProperty(final Property property) {
			this.property = property;
		}
	}

	public static class CompatConfigElement {
		@SuppressWarnings("rawtypes")
		public final IConfigElement element;

		@SuppressWarnings("rawtypes")
		public CompatConfigElement(final IConfigElement element) {
			this.element = element;
		}

		@SuppressWarnings("rawtypes")
		public static List<IConfigElement> getConfigElements(final List<CompatConfigElement> elements) {
			return Lists.transform(elements, t -> t==null ? null : t.element);
		}

		public static CompatConfigElement fromCategory(final CompatConfigCategory category) {
			return new CompatConfigElement(new ConfigElement<>(category.category));
		}

		public static CompatConfigElement fromProperty(final CompatConfigProperty prop) {
			return new CompatConfigElement(new ConfigElement<>(prop.property));
		}
	}

}
