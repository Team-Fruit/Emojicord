package net.teamfruit.emojicord;

import net.teamfruit.emojicord.compat.CompatConfigSpec;

public class EmojicordConfig {
	private static final CompatConfigSpec.Builder BUILDER = new CompatConfigSpec.Builder();
	public static final Render RENDER = new Render(BUILDER);
	public static final Suggest SUGGEST = new Suggest(BUILDER);
	public static final Picker PICKER = new Picker(BUILDER);
	public static final Update UPDATE = new Update(BUILDER);
	public static final CompatConfigSpec spec = BUILDER.build();

	public static class Render {
		public final CompatConfigSpec.ConfigValue<Boolean> renderEnabled;

		public Render(final CompatConfigSpec.Builder builder) {
			builder.push("Render");
			this.renderEnabled = builder
					.comment("Enables/Disables emoji rendering")
					.translation("config.emojicord.render.enabled")
					.define("Enabled", true);
			builder.pop();
		}
	}

	public static class Suggest {
		public final CompatConfigSpec.ConfigValue<Boolean> autoSuggest;
		public final CompatConfigSpec.ConfigValue<Boolean> enterSuggest;

		public Suggest(final CompatConfigSpec.Builder builder) {
			builder.push("Suggest");
			this.autoSuggest = builder
					.comment("Enables/Disables auto suggest")
					.translation("config.emojicord.suggest.enabled")
					.define("Enabled", true);
			this.enterSuggest = builder
					.comment("Enter key to suggest")
					.translation("config.emojicord.suggest.enter")
					.define("EnterToSuggest", true);
			builder.pop();
		}
	}

	public static class Picker {
		public final CompatConfigSpec.ConfigValue<Integer> skinTone;

		public Picker(final CompatConfigSpec.Builder builder) {
			builder.push("Picker");
			this.skinTone = builder
					.comment("Emoji Picker Skin Tone")
					.translation("config.emojicord.picker.skintone")
					.define("SkinTone", 0);
			builder.pop();
		}
	}

	public static class Update {
		public final CompatConfigSpec.ConfigValue<Boolean> showUpdate;

		public Update(final CompatConfigSpec.Builder builder) {
			builder.push("Update");
			this.showUpdate = builder
					.comment("Show Update Notification")
					.translation("config.emojicord.update.notification")
					.define("Notification", true);
			builder.pop();
		}
	}
}