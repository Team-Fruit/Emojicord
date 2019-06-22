package net.teamfruit.emojicord;

import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public class EmojiParser {
	static final @Nonnull Pattern pg = Pattern.compile("\\((?:([^\\)]*?)~)?(.*?)\\)");
	static final @Nonnull Pattern pp = Pattern.compile("(?:([^\\d-\\+Ee\\.]?)([\\d-\\+Ee\\.]*)?)+?");
	static final @Nonnull Pattern p = Pattern.compile("<a?\\:(\\w+?)\\:([a-zA-Z0-9+/]+?)>|\\:(\\w+?)\\:"); // <a?\:(\w+?)\:([a-zA-Z0-9+/]+?)>|\:(\w+?)\:

}
