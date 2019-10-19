package net.teamfruit.emojicord.compat;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraftforge.fml.client.config.IConfigElement;

public class CompatConfig {

	public static class CompatConfiguration {
		public CompatConfiguration() {
		}

		public Set<String> getCategoryNames() {
			return Sets.newHashSet();
		}

		public CompatConfigCategory getCategory(final String category) {
			return new CompatConfigCategory();
		}
	}

	public static class CompatConfigCategory {
		public CompatConfigCategory() {
		}

		public boolean isChild() {
			return true;
		}
	}

	public static class CompatConfigProperty {
		public CompatConfigProperty() {
		}
	}

	public static class CompatConfigElement {
		public CompatConfigElement() {
		}

		public static List<IConfigElement> getConfigElements(final List<CompatConfigElement> elements) {
			return Lists.newArrayList();
		}

		public static CompatConfigElement fromCategory(final CompatConfigCategory category) {
			return new CompatConfigElement();
		}

		public static CompatConfigElement fromProperty(final CompatConfigProperty prop) {
			return new CompatConfigElement();
		}
	}

}
