package net.teamfruit.emojicord.compat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.teamfruit.emojicord.compat.Compat.CompatConfiguration;
import net.teamfruit.emojicord.compat.Compat.CompatSide;

/*
 * Like {@link com.electronwill.nightconfig.core.ConfigSpec} except in builder format, and extended to acept comments, language keys,
 * and other things Forge configs would find useful.
 */

public class CompatConfigSpec {
	private final ForgeConfigSpec spec;
	private File location;

	private CompatConfigSpec(final ForgeConfigSpec spec) {
		this.spec = spec;
	}

	public boolean isAvailable() {
		return true;
	}

	public ForgeConfigSpec getSpec() {
		return this.spec;
	}

	public void registerConfigDefine(final CompatSide side) {
		ModLoadingContext.get().registerConfig(side.toModConfigType(), this.spec);
	}

	public static interface CompatConfigHandler {
		void onConfigChanged();
	}

	public CompatConfiguration configure(final CompatConfiguration config) {
		return config;
	}

	public CompatConfiguration getConfiguration() {
		return new CompatConfiguration();
	}

	public File getConfigFile() {
		return this.location;
	}

	public CompatConfigHandler registerConfigHandler(final CompatSide side, final File location) {
		this.location = location;
		return () -> {
		};
	}

	public void save() {
		//this.spec.save();
	}

	public static class Builder {
		private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		private BuilderContext context = new BuilderContext();
		private List<String> currentPath = new ArrayList<>();
		private List<ConfigValue<?>> values = new ArrayList<>();

		private List<String> concat(final List<String> lhs, final List<String> rhs) {
			final List<String> list = Lists.newArrayList();
			list.addAll(lhs);
			list.addAll(rhs);
			return list;
		}

		//string
		public StringValue define(final String path, final String defaultValue) {
			return defineString(split(path), () -> defaultValue);
		}

		public StringValue defineString(final List<String> path, final Supplier<String> defaultSupplier) {
			final List<String> newpath = concat(this.currentPath, path);
			final StringValue ret = new StringValue(this, newpath, defaultSupplier, this.context);
			this.values.add(ret);
			this.context = new BuilderContext();
			return ret;
		}

		//boolean
		public BooleanValue define(final String path, final boolean defaultValue) {
			return defineBoolean(split(path), () -> defaultValue);
		}

		private BooleanValue defineBoolean(final List<String> path, final Supplier<Boolean> defaultSupplier) {
			final List<String> newpath = concat(this.currentPath, path);
			final BooleanValue ret = new BooleanValue(this, newpath, defaultSupplier, this.context);
			this.values.add(ret);
			this.context = new BuilderContext();
			return ret;
		}

		//int
		public IntValue define(final String path, final int defaultValue) {
			return defineInt(split(path), () -> defaultValue);
		}

		private IntValue defineInt(final List<String> path, final Supplier<Integer> defaultSupplier) {
			final List<String> newpath = concat(this.currentPath, path);
			final IntValue ret = new IntValue(this, newpath, defaultSupplier, this.context);
			this.values.add(ret);
			this.context = new BuilderContext();
			return ret;
		}

		//double
		public DoubleValue define(final String path, final double defaultValue) {
			return defineDouble(split(path), () -> defaultValue);
		}

		private DoubleValue defineDouble(final List<String> path, final Supplier<Double> defaultSupplier) {
			final List<String> newpath = concat(this.currentPath, path);
			final DoubleValue ret = new DoubleValue(this, newpath, defaultSupplier, this.context);
			this.values.add(ret);
			this.context = new BuilderContext();
			return ret;
		}

		public Builder comment(final String comment) {
			this.context.setComment(comment);
			return this;
		}

		public Builder comment(final String... comment) {
			this.context.setComment(comment);
			return this;
		}

		public Builder translation(final String translationKey) {
			this.context.setTranslationKey(translationKey);
			return this;
		}

		public Builder worldRestart() {
			this.context.worldRestart();
			return this;
		}

		public Builder push(final String path) {
			return push(split(path));
		}

		public Builder push(final List<String> path) {
			this.currentPath.addAll(path);
			if (this.context.getComment()!=null)
				this.context.setComment((String[]) null);
			this.context.ensureEmpty();
			return this;
		}

		public Builder pop() {
			return pop(1);
		}

		public Builder pop(final int count) {
			if (count>this.currentPath.size())
				throw new IllegalArgumentException("Attempted to pop "+count+" elements when we only had: "+this.currentPath);
			for (int x = 0; x<count; x++)
				this.currentPath.remove(this.currentPath.size()-1);
			return this;
		}

		public <T> Pair<T, CompatConfigSpec> configure(final Function<Builder, T> consumer) {
			final T o = consumer.apply(this);
			return Pair.of(o, build());
		}

		public CompatConfigSpec build() {
			this.context.ensureEmpty();
			this.values.forEach(v -> v.apply(this.builder));
			return new CompatConfigSpec(this.builder.build());
		}

		public interface BuilderConsumer {
			void accept(Builder builder);
		}
	}

	private static class BuilderContext {
		private String[] comment;
		private String langKey;
		private boolean worldRestart = false;

		public void setComment(final String... value) {
			this.comment = value;
		}

		public String[] getComment() {
			return this.comment;
		}

		public void setTranslationKey(final String value) {
			this.langKey = value;
		}

		public void worldRestart() {
			this.worldRestart = true;
		}

		public void ensureEmpty() {
			validate(this.comment, "Non-null comment when null expected");
			validate(this.langKey, "Non-null translation key when null expected");
			validate(this.worldRestart, "Dangeling world restart value set to true");
		}

		private void validate(final Object value, final String message) {
			if (value!=null)
				throw new IllegalStateException(message);
		}

		private void validate(final boolean value, final String message) {
			if (value)
				throw new IllegalStateException(message);
		}

		public void apply(final ForgeConfigSpec.Builder builder) {
			if (this.comment!=null)
				builder.comment(this.comment);
			if (this.langKey!=null)
				builder.translation(this.langKey);
			if (this.worldRestart)
				builder.worldRestart();
		}
	}

	public static abstract class ConfigValue<T> {
		protected final Builder parent;
		protected final List<String> path;
		protected final Supplier<T> defaultSupplier;
		protected final BuilderContext builderContext;

		protected ForgeConfigSpec.ConfigValue<T> value;

		ConfigValue(final Builder parent, final List<String> path, final Supplier<T> defaultSupplier, final BuilderContext builderContext) {
			this.parent = parent;
			this.path = path;
			this.defaultSupplier = defaultSupplier;
			this.parent.values.add(this);
			this.builderContext = builderContext;
		}

		public List<String> getPath() {
			return Lists.newArrayList(this.path);
		}

		public T get() {
			Preconditions.checkNotNull(this.value, "Cannot get config value without assigned Config object present");
			return this.value.get();
		}

		public void set(final T value) {
			Preconditions.checkNotNull(this.value, "Cannot set config value without assigned Config object present");
			//this.value.set(value);
		}

		public Builder next() {
			return this.parent;
		}

		protected abstract ForgeConfigSpec.ConfigValue<T> applyDefine(ForgeConfigSpec.Builder builder);

		public void apply(final ForgeConfigSpec.Builder builder) {
			this.builderContext.apply(builder);
			this.value = applyDefine(builder);
		}
	}

	public static class StringValue extends ConfigValue<String> {
		StringValue(final Builder parent, final List<String> path, final Supplier<String> defaultSupplier, final BuilderContext builderContext) {
			super(parent, path, defaultSupplier, builderContext);
		}

		@Override
		protected ForgeConfigSpec.ConfigValue<String> applyDefine(final ForgeConfigSpec.Builder builder) {
			return builder.define(this.path, this.defaultSupplier, v -> v instanceof String);
		}
	}

	public static class BooleanValue extends ConfigValue<Boolean> {
		BooleanValue(final Builder parent, final List<String> path, final Supplier<Boolean> defaultSupplier, final BuilderContext builderContext) {
			super(parent, path, defaultSupplier, builderContext);
		}

		@Override
		protected ForgeConfigSpec.ConfigValue<Boolean> applyDefine(final ForgeConfigSpec.Builder builder) {
			return builder.define(this.path, this.defaultSupplier);
		}
	}

	public static class IntValue extends ConfigValue<Integer> {
		IntValue(final Builder parent, final List<String> path, final Supplier<Integer> defaultSupplier, final BuilderContext builderContext) {
			super(parent, path, defaultSupplier, builderContext);
		}

		@Override
		protected ForgeConfigSpec.ConfigValue<Integer> applyDefine(final ForgeConfigSpec.Builder builder) {
			return builder.define(this.path, this.defaultSupplier, v -> v instanceof Integer);
		}
	}

	public static class DoubleValue extends ConfigValue<Double> {
		DoubleValue(final Builder parent, final List<String> path, final Supplier<Double> defaultSupplier, final BuilderContext builderContext) {
			super(parent, path, defaultSupplier, builderContext);
		}

		@Override
		protected ForgeConfigSpec.ConfigValue<Double> applyDefine(final ForgeConfigSpec.Builder builder) {
			return builder.define(this.path, this.defaultSupplier, v -> v instanceof Double);
		}
	}

	private static final Splitter DOT_SPLITTER = Splitter.on(".");

	private static List<String> split(final String path) {
		return Lists.newArrayList(DOT_SPLITTER.split(path));
	}
}