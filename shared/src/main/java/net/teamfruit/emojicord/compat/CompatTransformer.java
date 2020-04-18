package net.teamfruit.emojicord.compat;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;

public abstract class CompatTransformer implements IClassTransformer {
	private static final Logger LOGGER = LogManager.getLogger();

	public abstract ClassNode read(@Nonnull byte[] bytes);

	public abstract byte[] write(@Nonnull ClassNode node);

	public abstract ClassNode transform(final ClassNode input, final CompatTransformerVotingContext context);

	public abstract DeferredTransform[] deferredTransforms();

	public abstract Set<String> targetNames();

	public static class CompatTransformerVotingContext {
	}

	public static class DeferredTransform {
		private final String thisname;
		private final String targetname;

		public DeferredTransform(final String thisname, final String targetname) {
			this.thisname = thisname;
			this.targetname = targetname;
		}

		private boolean targetloaded;
		private boolean targetinitialized;
		private boolean targetfound;

		public boolean hasTarget() {
			return this.targetfound;
		}

		public void transform(final String name, final String transformedName) {
			if (StringUtils.equals(transformedName, "$wrapper."+this.targetname))
				this.targetfound = true;

			if (!this.targetloaded) {
				if (StringUtils.equals(transformedName, "$wrapper."+this.targetname))
					this.targetloaded = true;
			} else if (!this.targetinitialized) {
				this.targetinitialized = true;
				try {
					final Field $transformers = Class.forName("net.minecraft.launchwrapper.LaunchClassLoader").getDeclaredField("transformers");
					$transformers.setAccessible(true);
					final List<?> transformers = (List<?>) $transformers.get(Class.forName("net.minecraft.launchwrapper.Launch").getField("classLoader").get(null));
					int thistransformer = -1, targettransformer = -1;
					for (final ListIterator<?> itr = transformers.listIterator(); itr.hasNext();) {
						final int index = itr.nextIndex();
						final Object transformer = itr.next();
						final String tname = transformer.getClass().getName();
						if (StringUtils.equals(tname, "$wrapper."+this.thisname))
							thistransformer = index;
						else if (StringUtils.equals(tname, "$wrapper."+this.targetname))
							targettransformer = index;
					}
					if (thistransformer>=0&&targettransformer>=0&&targettransformer>thistransformer) {
						Collections.swap(transformers, thistransformer, targettransformer);
						LOGGER.info("The order of EmojicordTransformer and IntelliInputTransformer has been swapped while loading "+transformedName);
					}
				} catch (final Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public byte[] transform(final String name, final String transformedName, byte[] bytes) {
		if (bytes==null||name==null||transformedName==null)
			return bytes;

		for (final DeferredTransform transform : deferredTransforms())
			transform.transform(name, transformedName);

		if (targetNames().contains(transformedName))
			try {
				ClassNode node = read(bytes);

				node = transform(node, new CompatTransformerVotingContext());

				bytes = write(node);
			} catch (final Exception e) {
				LOGGER.fatal("Could not transform: ", e);
			}

		return bytes;
	}
}
