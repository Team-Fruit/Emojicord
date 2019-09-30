package net.teamfruit.emojicord;

import net.teamfruit.emojicord.compat.CompatConfigSpec;

public class EmojicordConfig {
	private static final CompatConfigSpec.Builder BUILDER = new CompatConfigSpec.Builder();
	public static final Render RENDER = new Render(BUILDER);
	public static final Suggest SUGGEST = new Suggest(BUILDER);
	public static final CompatConfigSpec spec = BUILDER.build();

	public static class Render {
		public final CompatConfigSpec.ConfigValue<Boolean> renderEnabled;
		public final CompatConfigSpec.ConfigValue<Boolean> renderMipmapEnabled;
		public final CompatConfigSpec.ConfigValue<Boolean> renderMipmapFastResize;

		public Render(final CompatConfigSpec.Builder builder) {
			builder.push("Render");
			this.renderEnabled = builder
					.comment("Enables/Disables emoji rendering")
					.translation("config.emojicord.render.enabled")
					.define("Enabled", true);
			this.renderMipmapEnabled = builder
					.comment("Enables/Disables emoji mipmap")
					.translation("config.emojicord.render.mipmap.enabled")
					.define("Mipmap.Enabled", true);
			this.renderMipmapFastResize = builder
					.comment("Enables/Disables emoji faster resize")
					.translation("config.emojicord.render.mipmap.fastresize")
					.define("Mipmap.Fastresize", true);
			builder.pop();
		}
	}

	public static class Suggest {
		public final CompatConfigSpec.ConfigValue<Boolean> autoSuggest;

		public Suggest(final CompatConfigSpec.Builder builder) {
			builder.push("Render");
			this.autoSuggest = builder
					.comment("Enables/Disables auto suggest")
					.translation("config.emojicord.suggest.enabled")
					.define("Enabled", true);
			builder.pop();
		}
	}
}