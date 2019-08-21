package net.teamfruit.emojicord.emoji;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.teamfruit.emojicord.CoreInvoke;
import net.teamfruit.emojicord.EmojicordConfig;
import net.teamfruit.emojicord.compat.Compat;
import net.teamfruit.emojicord.compat.OpenGL;

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
		if (EmojicordConfig.renderEnabled) {
			CurrentContext = EmojiContext.EmojiContextLoader.getEmojiFormattedString(text, isTextFieldRendering);
			return CurrentContext.text;
		}
		return text;
	}

	@CoreInvoke
	public static boolean renderEmojiChar(final FontRenderer fontRenderer, final char c, final boolean italic) {
		if (EmojicordConfig.renderEnabled) {
			final EmojiObject emoji = CurrentContext.emojis.get(index);
			if (emoji!=null)
				if (shadow)
					return true;
				else {
					Compat.CompatMinecraft.getMinecraft().renderEngine.bindTexture(emoji.getResourceLocationForBinding());
					renderEmoji(fontRenderer, emoji);
					return true;
				}
		}
		return false;
	}

	public static void renderEmoji(final FontRenderer fontRenderer, final EmojiObject emoji) {
		final float textureSize = 16.0F;
		final float textureX = 0.0F/textureSize;
		final float textureY = 0.0F/textureSize;
		final float textureOffset = 16.0F/textureSize;
		final float size = 10.0F;
		final float offsetY = 1.0F;
		final float offsetX = 0.0F;

		OpenGL.glPushAttrib();

		//OpenGL.glEnable(GL11.GL_BLEND);
		//OpenGL.glEnable(GL11.GL_ALPHA_TEST);

		OpenGL.glColor4f(1.0F, 1.0F, 1.0F, (OpenGL.glGetColorRGBA()>>24&0xff)/256f);
		OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		OpenGL.glBegin(GL11.GL_QUADS);
		OpenGL.glTexCoord2f(textureX, textureY);
		OpenGL.glVertex3f(fontRenderer.posX-offsetX, fontRenderer.posY-offsetY, 0.0F);
		OpenGL.glTexCoord2f(textureX, textureY+textureOffset);
		OpenGL.glVertex3f(fontRenderer.posX-offsetX, fontRenderer.posY+size-offsetY, 0.0F);
		OpenGL.glTexCoord2f(textureX+textureOffset, textureY+textureOffset);
		OpenGL.glVertex3f(fontRenderer.posX-offsetX+size, fontRenderer.posY+size-offsetY, 0.0F);
		OpenGL.glTexCoord2f(textureX+textureOffset, textureY/textureSize);
		OpenGL.glVertex3f(fontRenderer.posX-offsetX+size, fontRenderer.posY-offsetY, 0.0F);
		OpenGL.glEnd();
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//OpenGL.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

		//OpenGL.glDisable(GL11.GL_ALPHA_TEST);
		//OpenGL.glDisable(GL11.GL_BLEND);

		OpenGL.glColor4f(fontRenderer.red, fontRenderer.green, fontRenderer.blue, fontRenderer.alpha);

		OpenGL.glPopAttrib();
	}
}
