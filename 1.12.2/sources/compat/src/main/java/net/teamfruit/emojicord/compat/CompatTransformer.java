package net.teamfruit.emojicord.compat;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.asm.lib.VisitorHelper;

public abstract class CompatTransformer implements IClassTransformer {
	public abstract ClassNode transform(final ClassNode input, final CompatTransformerVotingContext context);

	public abstract DeferredTransform[] deferredTransforms();

	public abstract String[] targetNames();

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

		public void transform(final String name, final String transformedName) {
			if (!this.targetloaded) {
				if (StringUtils.equals(transformedName, "$wrapper."+this.targetname))
					this.targetloaded = true;
			} else if (!this.targetinitialized) {
				this.targetinitialized = true;
				try {
					final Field $transformers = Class.forName("net.minecraft.launchwrapper.LaunchClassLoader").getDeclaredField("transformers");
					$transformers.setAccessible(true);
					final List<?> transformers = (List<?>) $transformers.get(Class.forName("net.minecraft.launchwrapper.Launch").getField("net.minecraft.launchwrapper.Launch").get(null));
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
						Log.log.info("The order of EmojicordTransformer and IntelliInputTransformer has been swapped while loading "+transformedName);
					}
				} catch (final Exception e) {
					Log.log.error(e.getMessage(), e);
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

		if (Arrays.stream(targetNames()).anyMatch(transformedName::equals))
			try {
				final ClassNode node = VisitorHelper.read(bytes, 0);

				transform(node, new CompatTransformerVotingContext());

				bytes = VisitorHelper.write(node, ClassWriter.COMPUTE_FRAMES);
			} catch (final Exception e) {
				Log.log.fatal("Could not transform: ", e);
			}

		return bytes;
	}
}
