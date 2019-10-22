package net.teamfruit.emojicord;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.teamfruit.emojicord.util.DataUtils;

public class EmojicordScope {
	public static EmojicordScope instance = new EmojicordScope();

	public static class EmojicordScopeModel {
		public List<String> allow_unicode_emoji;
	}

	public Set<String> allowUnicodeEmoji = Sets.newHashSet();

	public void loadAll() {
		final List<String> list = Lists.newArrayList();
		try {
			final Enumeration<URL> ress = getClass().getClassLoader().getResources("META-INF/"+Reference.MODID+"/scope.json");
			while (ress.hasMoreElements()) {
				final URL url = ress.nextElement();
				try {
					final EmojicordScopeModel scope = DataUtils.loadStream(url.openStream(), EmojicordScopeModel.class, "Emojicord Scope Config");
					if (scope!=null&&scope.allow_unicode_emoji!=null)
						list.addAll(scope.allow_unicode_emoji);
				} catch (final IOException e) {
					Log.log.info("Failed to load Emojicord Scope Config: ", e);
				}
			}
		} catch (final IOException e) {
			Log.log.info("Failed to load Emojicord Scope Config: ", e);
		}
		this.allowUnicodeEmoji = Sets.newHashSet(list);
	}

	public boolean checkUnicodeEmojiAllowed(final StackTraceElement[] stacks) {
		if (stacks==null)
			return true;
		return Stream.of(stacks).map(StackTraceElement::getClassName).anyMatch(this.allowUnicodeEmoji::contains);
	}
}
