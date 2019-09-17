package net.teamfruit.emojicord.emoji;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.teamfruit.emojicord.CoreInvoke;
import net.teamfruit.emojicord.EmojicordConfig;
import net.teamfruit.emojicord.compat.Compat;
import net.teamfruit.emojicord.compat.Compat.CompatGlyph;
import net.teamfruit.emojicord.compat.Compat.CompatVertex;
import net.teamfruit.emojicord.compat.CompatBaseVertex;
import net.teamfruit.emojicord.compat.OpenGL;
import net.teamfruit.emojicord.emoji.EmojiText.EmojiTextElement;

@CoreInvoke
public class EmojiFontRenderer {
	@CoreInvoke
	public static boolean isTextFieldRendering;

	@CoreInvoke
	public static boolean shadow;
	@CoreInvoke
	public static int index;

	private static EmojiContext CurrentContext;

	@CoreInvoke
	public static String updateEmojiContext(final String text) {
		if (EmojicordConfig.spec.isAvailable()&&EmojicordConfig.RENDER.renderEnabled.get()) {
			CurrentContext = EmojiContext.EmojiContextCache.instance.getContext(text);
			return CurrentContext.text;
		}
		return text;
	}

	@CoreInvoke
	public static boolean renderEmojiChar(final char c, final boolean italic, final float x, final float y, final float red, final float green, final float blue, final float alpha) {
		if (EmojicordConfig.spec.isAvailable()&&EmojicordConfig.RENDER.renderEnabled.get()) {
			final EmojiTextElement emojiElement = CurrentContext.emojis.get(index);
			if (emojiElement!=null) {
				final EmojiId emojiId = emojiElement.id;
				if (emojiId!=null) {
					final EmojiObject emoji = EmojiObject.EmojiObjectCache.instance.getEmojiObject(emojiId);
					if (!shadow) {
						Compat.CompatMinecraft.getMinecraft().getTextureManager().bindTexture(emoji.getResourceLocationForBinding());
						renderEmoji(emoji, x, y, red, green, blue, alpha);
					}
					return c==EmojiContext.EMOJI_REPLACE_CHARACTOR;
				}
			}
		}
		return false;
	}

	@CoreInvoke
	public static @Nullable EmojiGlyph getEmojiGlyph(final char c, final int index) {
		if (EmojicordConfig.spec.isAvailable()&&EmojicordConfig.RENDER.renderEnabled.get()) {
			final EmojiTextElement emojiElement = CurrentContext.emojis.get(index);
			if (emojiElement!=null) {
				final EmojiId emojiId = emojiElement.id;
				if (emojiId!=null) {
					final EmojiObject emoji = EmojiObject.EmojiObjectCache.instance.getEmojiObject(emojiId);
					return new EmojiGlyph(emoji.getResourceLocationForBinding());
				}
			}
		}
		return null;
	}

	public static class EmojiGlyph extends CompatGlyph {
		private static final float GlyphWidth = 10;
		private static final float GlyphHeight = 10;

		public EmojiGlyph(final ResourceLocation texture) {
			super(texture, GlyphWidth, GlyphHeight);
		}

		@Override
		public void onRender(final TextureManager textureManager, final boolean hasShadow, final float x, final float y, final BufferBuilder vbuilder, final float red, final float green, final float blue, final float alpha) {
			if (!shadow)
				super.onRender(textureManager, hasShadow, x, y, vbuilder, red, green, blue, alpha);
		}
	}

	public static void renderEmoji(final EmojiObject emoji, final float x, final float y, final float red, final float green, final float blue, final float alpha) {
		final float textureSize = 16.0F;
		final float textureX = 0.0F/textureSize;
		final float textureY = 0.0F/textureSize;
		final float textureOffset = 16.0F/textureSize;
		final float size = 10.0F;
		final float offsetY = 1.0F;
		final float offsetX = 0.0F;

		OpenGL.glPushAttrib();

		OpenGL.glColor4f(1.0F, 1.0F, 1.0F, (OpenGL.glGetColorRGBA()>>24&0xff)/256f);

		//OpenGL.glEnable(GL11.GL_BLEND);
		//OpenGL.glEnable(GL11.GL_ALPHA_TEST);

		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		final CompatBaseVertex bufferbuilder = CompatVertex.getTessellator();
		bufferbuilder.beginTexture(GL11.GL_QUADS);
		bufferbuilder.pos(x-offsetX, y-offsetY, 0.0F).tex(textureX, textureY);
		bufferbuilder.pos(x-offsetX, y+size-offsetY, 0.0F).tex(textureX, textureY+textureOffset);
		bufferbuilder.pos(x-offsetX+size, y+size-offsetY, 0.0F).tex(textureX+textureOffset, textureY+textureOffset);
		bufferbuilder.pos(x-offsetX+size, y-offsetY, 0.0F).tex(textureX+textureOffset, textureY);
		bufferbuilder.draw();
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

		//OpenGL.glDisable(GL11.GL_ALPHA_TEST);
		//OpenGL.glDisable(GL11.GL_BLEND);

		OpenGL.glColor4f(red, green, blue, alpha);

		OpenGL.glPopAttrib();
	}
}
