package net.teamfruit.emojicord.compat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

#if MC_7_LATER
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
#else
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
#endif

/*
 * Like {@link com.electronwill.nightconfig.core.ConfigSpec} except in builder format, and extended to acept comments, language keys,
 * and other things Forge configs would find useful.
 */

public class CompatConfigSpec {
	private final List<ConfigValue<?>> values;
	private Configuration config;

	private CompatConfigSpec(final List<ConfigValue<?>> values) {
		this.values = values;
	}

	public boolean isAvailable() {
		return this.config!=null;
	}

	public Configuration configure(final Configuration config) {
		this.values.forEach(v -> v.apply(config));
		return this.config = config;
	}

	public Configuration getConfiguration() {
		return this.config;
	}

	public File getConfigFile() {
		return this.config.getConfigFile();
	}

	public void registerConfigDefine(final Side side) {
	}

	public static interface CompatConfigHandler {
		void onConfigChanged();
	}

	public CompatConfigHandler registerConfigHandler(final Side side, final File location) {
		if (FMLCommonHandler.instance().getEffectiveSide()==side) {
			final Configuration config = new Configuration(location, null, true);
			configure(config);
			config.save();
			return config::save;
		} else
			return () -> {
			};
	}

	public void save() {
		this.config.save();
	}

	public static class Builder {
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

		private CategoryValue defineCategory(final List<String> path, final Supplier<Void> defaultSupplier) {
			final List<String> newpath = concat(this.currentPath, path);
			final CategoryValue ret = new CategoryValue(this, newpath, defaultSupplier, this.context);
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
			defineCategory(path, () -> null);
			this.currentPath.addAll(path);
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
			return new CompatConfigSpec(Lists.newArrayList(this.values));
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

		public void apply(final Property builder) {
			if (this.comment!=null)
				builder. #if MC_7_LATER setComment #else comment = #endif (LINE_JOINER.join(this.comment));
			if (this.langKey!=null)
				builder.setLanguageKey(this.langKey);
			if (this.worldRestart)
				builder.setRequiresWorldRestart(true);
		}

		public void apply(final ConfigCategory builder) {
			if (this.comment!=null)
				builder.setComment(LINE_JOINER.join(this.comment));
			if (this.langKey!=null)
				builder.setLanguageKey(this.langKey);
			if (this.worldRestart)
				builder.setRequiresWorldRestart(true);
		}
	}

	public static abstract class ConfigValue<T> {
		protected final Builder parent;
		protected final List<String> path;
		protected final Supplier<T> defaultSupplier;
		protected final BuilderContext builderContext;

		protected Property value;

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

		protected abstract T getPropertyValue();

		public T get() {
			Preconditions.checkNotNull(this.value, "Cannot get config value without assigned Config object present");
			return getPropertyValue();
		}

		protected abstract void setPropertyValue(T value);

		public void set(final T value) {
			Preconditions.checkNotNull(this.value, "Cannot set config value without assigned Config object present");
			setPropertyValue(value);
		}

		public Builder next() {
			return this.parent;
		}

		protected abstract Property applyDefine(Configuration builder);

		public void apply(final Configuration builder) {
			this.value = applyDefine(builder);
		}
	}

	public static class CategoryValue extends ConfigValue<Void> {
		CategoryValue(final Builder parent, final List<String> path, final Supplier<Void> defaultSupplier, final BuilderContext builderContext) {
			super(parent, path, defaultSupplier, builderContext);
		}

		@Override
		protected Property applyDefine(final Configuration builder) {
			final ConfigCategory ret = builder.getCategory(DOT_JOINER.join(this.path));
			this.builderContext.apply(ret);
			return null;
		}

		@Override
		protected Void getPropertyValue() {
			return null;
		}

		@Override
		protected void setPropertyValue(final Void value) {
		}
	}

	public static class StringValue extends ConfigValue<String> {
		StringValue(final Builder parent, final List<String> path, final Supplier<String> defaultSupplier, final BuilderContext builderContext) {
			super(parent, path, defaultSupplier, builderContext);
		}

		@Override
		protected Property applyDefine(final Configuration builder) {
			final Property ret = builder.get(DOT_JOINER.join(this.path.subList(0, this.path.size()-1)), this.path.get(this.path.size()-1), this.defaultSupplier.get());
			this.builderContext.apply(ret);
			return ret;
		}

		@Override
		protected String getPropertyValue() {
			return this.value.getString();
		}

		@Override
		protected void setPropertyValue(final String value) {
			this.value.set(value);
		}
	}

	public static class BooleanValue extends ConfigValue<Boolean> {
		BooleanValue(final Builder parent, final List<String> path, final Supplier<Boolean> defaultSupplier, final BuilderContext builderContext) {
			super(parent, path, defaultSupplier, builderContext);
		}

		@Override
		protected Property applyDefine(final Configuration builder) {
			final Property ret = builder.get(DOT_JOINER.join(this.path.subList(0, this.path.size()-1)), this.path.get(this.path.size()-1), this.defaultSupplier.get());
			this.builderContext.apply(ret);
			return ret;
		}

		@Override
		protected Boolean getPropertyValue() {
			return this.value.getBoolean();
		}

		@Override
		protected void setPropertyValue(final Boolean value) {
			this.value.set(value);
		}
	}

	public static class IntValue extends ConfigValue<Integer> {
		IntValue(final Builder parent, final List<String> path, final Supplier<Integer> defaultSupplier, final BuilderContext builderContext) {
			super(parent, path, defaultSupplier, builderContext);
		}

		@Override
		protected Property applyDefine(final Configuration builder) {
			final Property ret = builder.get(DOT_JOINER.join(this.path.subList(0, this.path.size()-1)), this.path.get(this.path.size()-1), this.defaultSupplier.get());
			this.builderContext.apply(ret);
			return ret;
		}

		@Override
		protected Integer getPropertyValue() {
			return this.value.getInt();
		}

		@Override
		protected void setPropertyValue(final Integer value) {
			this.value.set(value);
		}
	}

	public static class DoubleValue extends ConfigValue<Double> {
		DoubleValue(final Builder parent, final List<String> path, final Supplier<Double> defaultSupplier, final BuilderContext builderContext) {
			super(parent, path, defaultSupplier, builderContext);
		}

		@Override
		protected Property applyDefine(final Configuration builder) {
			final Property ret = builder.get(DOT_JOINER.join(this.path.subList(0, this.path.size()-1)), this.path.get(this.path.size()-1), this.defaultSupplier.get());
			this.builderContext.apply(ret);
			return ret;
		}

		@Override
		protected Double getPropertyValue() {
			return this.value.getDouble();
		}

		@Override
		protected void setPropertyValue(final Double value) {
			this.value.set(value);
		}
	}

	private static final Joiner LINE_JOINER = Joiner.on("\n");
	private static final Splitter DOT_SPLITTER = Splitter.on(".");
	private static final Joiner DOT_JOINER = Joiner.on(".");

	private static List<String> split(final String path) {
		return Lists.newArrayList(DOT_SPLITTER.split(path));
	}
}