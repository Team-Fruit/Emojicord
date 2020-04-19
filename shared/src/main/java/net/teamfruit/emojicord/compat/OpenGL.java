package net.teamfruit.emojicord.compat;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

import javax.annotation.Nullable;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

#if MC_7_LATER
import org.lwjgl.opengl.GL12;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
#else
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
#endif

/**
 * OpenGL操作のラッパーです。
 * <p>
 * 主にMinecraftのバージョン間の差異を吸収します。
 *
 * @author TeamFruit
 */
public class OpenGL {
	public static void glEnable(final int attrib) {
		#if MC_7_LATER
		switch (attrib) {
		case GL11.GL_ALPHA_TEST:
			GlStateManager.enableAlpha();
			break;
		case GL11.GL_BLEND:
			GlStateManager.enableBlend();
			break;
		case GL11.GL_CULL_FACE:
			GlStateManager.enableCull();
			break;
		case GL11.GL_DEPTH_TEST:
			GlStateManager.enableDepth();
			break;
		case GL11.GL_FOG:
			GlStateManager.enableFog();
			break;
		case GL11.GL_LIGHTING:
			GlStateManager.enableLighting();
			break;
		case GL11.GL_NORMALIZE:
			GlStateManager.enableNormalize();
			break;
		case GL11.GL_POLYGON_OFFSET_FILL:
			GlStateManager.enablePolygonOffset();
			break;
		case GL12.GL_RESCALE_NORMAL:
			GlStateManager.enableRescaleNormal();
			break;
		case GL11.GL_TEXTURE_2D:
			GlStateManager.enableTexture2D();
			break;
		default:
			GL11.glEnable(attrib);
		}
		#else
		GL11.glEnable(attrib);
		#endif
	}

	public static void glDisable(final int attrib) {
		#if MC_7_LATER
		switch (attrib) {
		case GL11.GL_ALPHA_TEST:
			GlStateManager.disableAlpha();
			break;
		case GL11.GL_BLEND:
			GlStateManager.disableBlend();
			break;
		case GL11.GL_CULL_FACE:
			GlStateManager.disableCull();
			break;
		case GL11.GL_DEPTH_TEST:
			GlStateManager.disableDepth();
			break;
		case GL11.GL_FOG:
			GlStateManager.disableFog();
			break;
		case GL11.GL_LIGHTING:
			GlStateManager.disableLighting();
			break;
		case GL11.GL_NORMALIZE:
			GlStateManager.disableNormalize();
			break;
		case GL11.GL_POLYGON_OFFSET_FILL:
			GlStateManager.disablePolygonOffset();
			break;
		case GL12.GL_RESCALE_NORMAL:
			GlStateManager.disableRescaleNormal();
			break;
		case GL11.GL_TEXTURE_2D:
			GlStateManager.disableTexture2D();
			break;
		default:
			GL11.glDisable(attrib);
		}
		#else
		GL11.glDisable(attrib);
		#endif
	}

	public static boolean glIsEnabled(final int attrib) {
		return GL11.glIsEnabled(attrib);
	}

	public static boolean glEnabled(final int attrib) {
		if (!glIsEnabled(attrib)) {
			glEnable(attrib);
			return true;
		}
		return false;
	}

	public static boolean glDisabled(final int attrib) {
		if (glIsEnabled(attrib)) {
			glEnable(attrib);
			return true;
		}
		return false;
	}

	public static void glHint(final int target, final int mode) {
		GL11.glHint(target, mode);
	}

	public static void glAlphaFunc(final int func, final float ref) {
		#if MC_7_LATER
		GlStateManager.alphaFunc(func, ref);
		#else
		GL11.glAlphaFunc(func, ref);
		#endif
	}

	public static void glBlendFunc(final int sfactor, final int dfactor) {
		#if MC_7_LATER
		GlStateManager.blendFunc(sfactor, dfactor);
		#else
		GL11.glBlendFunc(sfactor, dfactor);
		#endif
	}

	public static void glBlendFuncSeparate(final int srcFactor, final int dstFactor, final int srcFactorAlpha,
			final int dstFactorAlpha) {
		#if MC_7_LATER
		GlStateManager.tryBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
		#else
		OpenGlHelper.glBlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
		#endif
	}

	public static void glCallList(final int list) {
		#if MC_7_LATER
		GlStateManager.callList(list);
		#else
		GL11.glCallList(list);
		#endif
	}

	public static void glClear(final int mask) {
		#if MC_7_LATER
		GlStateManager.clear(mask);
		#else
		GL11.glClear(mask);
		#endif
	}

	public static void glClearColor(
			final float red, final float green, final float blue,
			final float alpha) {
		#if MC_7_LATER
		GlStateManager.clearColor(red, green, blue, alpha);
		#else
		GL11.glClearColor(red, green, blue, alpha);
		#endif
	}

	public static void glClearDepth(final double depth) {
		#if MC_7_LATER
		GlStateManager.clearDepth(depth);
		#else
		GL11.glClearDepth(depth);
		#endif
	}

	public static void glColor3f(final float red, final float green, final float blue) {
		#if MC_7_LATER
		GlStateManager.color(red, green, blue, 1.0F);
		#else
		GL11.glColor3f(red, green, blue);
		#endif
	}

	public static void glColor4f(final float red, final float green, final float blue, final float alpha) {
		#if MC_7_LATER
		GlStateManager.color(red, green, blue, alpha);
		#else
		GL11.glColor4f(red, green, blue, alpha);
		#endif
	}

	public static void glColor4i(final int red, final int green, final int blue, final int alpha) {
		glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f);
	}

	public static void glColor4ub(final byte red, final byte green, final byte blue, final byte alpha) {
		#if MC_7_LATER
		glColor4i(red & 0xff, green & 0xff, blue & 0xff, alpha & 0xff);
		#else
		GL11.glColor4ub(red, green, blue, alpha);
		#endif
	}

	public static void glColorRGB(final int rgb) {
		final int value = 0xff000000 | rgb;
		glColor4i(value >> 16 & 0xff, value >> 8 & 0xff, value >> 0 & 0xff, value >> 24 & 0xff);
	}

	public static void glColorRGBA(final int rgba) {
		glColor4i(rgba >> 16 & 0xff, rgba >> 8 & 0xff, rgba >> 0 & 0xff, rgba >> 24 & 0xff);
	}

	public static void glColor(final Color color) {
		glColor4i(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static void glColor(final org.lwjgl.util.Color color) {
		glColor4i(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	private static FloatBuffer buf = GLAllocation.createDirectFloatBuffer(16);

	private static int toColorCode(final int r, final int g, final int b, final int a) {
		return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff) << 0;
	}

	private static int toColorCode(final float r, final float g, final float b, final float a) {
		return toColorCode((int) (r * 255 + .5f), (int) (g * 255 + .5f), (int) (b * 255 + .5f), (int) (a * 255 + .5f));
	}

	public static int glGetColorRGBA() {
		buf.clear();
		GL11.glGetFloat(GL11.GL_CURRENT_COLOR, buf);
		final float r = buf.get(0);
		final float g = buf.get(1);
		final float b = buf.get(2);
		final float a = buf.get(3);
		return toColorCode(r, g, b, a);
	}

	public static Color glGetColor() {
		buf.clear();
		GL11.glGetFloat(GL11.GL_CURRENT_COLOR, buf);
		final float r = Math.min(1f, buf.get(0));
		final float g = Math.min(1f, buf.get(1));
		final float b = Math.min(1f, buf.get(2));
		final float a = Math.min(1f, buf.get(3));
		return new Color(r, g, b, a);
	}

	public static org.lwjgl.util.Color glGetLwjglColor() {
		buf.clear();
		GL11.glGetFloat(GL11.GL_CURRENT_COLOR, buf);
		final float r = buf.get(0);
		final float g = buf.get(1);
		final float b = buf.get(2);
		final float a = buf.get(3);
		return new org.lwjgl.util.Color((int) (r * 255 + 0.5) & 0xff, (int) (g * 255 + 0.5) & 0xff,
				(int) (b * 255 + 0.5) & 0xff, (int) (a * 255 + 0.5) & 0xff);
	}

	public static void glColorMask(final boolean red, final boolean green, final boolean blue, final boolean alpha) {
		#if MC_7_LATER
		GlStateManager.colorMask(red, green, blue, alpha);
		#else
		GL11.glColorMask(red, green, blue, alpha);
		#endif
	}

	public static void glColorMaterial(final int face, final int mode) {
		#if MC_7_LATER
		GlStateManager.colorMaterial(face, mode);
		#else
		GL11.glColorMaterial(face, mode);
		#endif
	}

	public static void glCullFace(final int mode) {
		// GlStateManager.cullFace(mode);
		#if MC_7_LATER
		if (mode == GlStateManager.CullFace.BACK.mode)
			GlStateManager.cullFace(GlStateManager.CullFace.BACK);
		else if (mode == GlStateManager.CullFace.FRONT.mode)
			GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
		else if (mode == GlStateManager.CullFace.FRONT_AND_BACK.mode)
			GlStateManager.cullFace(GlStateManager.CullFace.FRONT_AND_BACK);
		#else
		GL11.glCullFace(mode);
		#endif
	}

	public static void glDepthFunc(final int func) {
		#if MC_7_LATER
		GlStateManager.depthFunc(func);
		#else
		GL11.glDepthFunc(func);
		#endif
	}

	public static void glDepthMask(final boolean flag) {
		#if MC_7_LATER
		GlStateManager.depthMask(flag);
		#else
		GL11.glDepthMask(flag);
		#endif
	}

	public static void glGetFloat(final int pname, final FloatBuffer params) {
		#if MC_7_LATER
		GlStateManager.getFloat(pname, params);
		#else
		GL11.glGetFloat(pname, params);
		#endif
	}

	public static void glLoadIdentity() {
		#if MC_7_LATER
		GlStateManager.loadIdentity();
		#else
		GL11.glLoadIdentity();
		#endif
	}

	public static void glLogicOp(final int opcode) {
		#if MC_7_LATER
		GlStateManager.colorLogicOp(opcode);
		#else
		GL11.glLogicOp(opcode);
		#endif
	}

	public static void glMatrixMode(final int mode) {
		#if MC_7_LATER
		GlStateManager.matrixMode(mode);
		#else
		GL11.glMatrixMode(mode);
		#endif
	}

	public static void glMultMatrix(final FloatBuffer m) {
		#if MC_7_LATER
		GlStateManager.multMatrix(m);
		#else
		GL11.glMultMatrix(m);
		#endif
	}

	public static void glOrtho(final double left, final double right, final double bottom, final double top,
			final double zNear, final double zFar) {
		#if MC_7_LATER
		GlStateManager.ortho(left, right, bottom, top, zNear, zFar);
		#else
		GL11.glOrtho(left, right, bottom, top, zNear, zFar);
		#endif
	}

	public static void glPolygonOffset(final float factor, final float units) {
		#if MC_7_LATER
		GlStateManager.doPolygonOffset(factor, units);
		#else
		GL11.glPolygonOffset(factor, units);
		#endif
	}

	public static void glPopAttrib() {
		#if MC_7_LATER
		GlStateManager.popAttrib();
		#else
		GL11.glPopAttrib();
		#endif
	}

	public static void glPopMatrix() {
		#if MC_7_LATER
		GlStateManager.popMatrix();
		#else
		GL11.glPopMatrix();
		#endif
	}

	public static void glPushAttrib() {
		#if MC_7_LATER
		GlStateManager.pushAttrib();
		#else
		GL11.glPushAttrib(8256);
		#endif
	}

	public static void glPushMatrix() {
		#if MC_7_LATER
		GlStateManager.pushMatrix();
		#else
		GL11.glPushMatrix();
		#endif
	}

	public static void glRotatef(final float angle, final float x, final float y, final float z) {
		#if MC_7_LATER
		GlStateManager.rotate(angle, x, y, z);
		#else
		GL11.glRotatef(angle, x, y, z);
		#endif
	}

	public static void glScaled(final double x, final double y, final double z) {
		#if MC_7_LATER
		GlStateManager.scale(x, y, z);
		#else
		GL11.glScaled(x, y, z);
		#endif
	}

	public static void glScalef(final float x, final float y, final float z) {
		#if MC_7_LATER
		GlStateManager.scale(x, y, z);
		#else
		GL11.glScalef(x, y, z);
		#endif
	}

	public static void glSetActiveTextureUnit(final int texture) {
		#if MC_7_LATER
		GlStateManager.setActiveTexture(texture);
		#else
		OpenGlHelper.setActiveTexture(texture);
		#endif
	}

	public static void glShadeModel(final int mode) {
		#if MC_7_LATER
		GlStateManager.shadeModel(mode);
		#else
		GL11.glShadeModel(mode);
		#endif
	}

	public static void glTranslated(final double x, final double y, final double z) {
		#if MC_7_LATER
		GlStateManager.translate(x, y, z);
		#else
		GL11.glTranslated(x, y, z);
		#endif
	}

	public static void glTranslatef(final float x, final float y, final float z) {
		#if MC_7_LATER
		GlStateManager.translate(x, y, z);
		#else
		GL11.glTranslatef(x, y, z);
		#endif
	}

	public static void glViewport(final int x, final int y, final int width, final int height) {
		#if MC_7_LATER
		GlStateManager.viewport(x, y, width, height);
		#else
		GL11.glViewport(x, y, width, height);
		#endif
	}

	public static void glBegin(final int mode) {
		GL11.glBegin(mode);
	}

	public static void glEnd() {
		GL11.glEnd();
	}

	public static int glGenTextures() {
		#if MC_7_LATER
		return GlStateManager.generateTexture();
		#else
		return GL11.glGenTextures();
		#endif
	}

	public static int glGetTexLevelParameteri(final int target, final int level, final int pname) {
		return GL11.glGetTexLevelParameteri(target, level, pname);
	}

	public static int glGetTexParameteri(final int target, final int pname) {
		return GL11.glGetTexParameteri(target, pname);
	}

	public static void glNormal3f(final float nx, final float ny, final float nz) {
		GL11.glNormal3f(nx, ny, nz);
	}

	public static void glPushAttrib(final int mask) {
		GL11.glPushAttrib(mask);
	}

	public static void glTexImage2D(final int target, final int level, final int internalFormat, final int width,
			final int height, final int border, final int format, final int type, final IntBuffer pixels) {
		GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
	}

	public static void glTexParameteri(final int target, final int pname, final int param) {
		GL11.glTexParameteri(target, pname, param);
	}

	public static void glTexCoord2f(final float sCoord, final float tCoord) {
		#if MC_7_LATER
		GlStateManager.glTexCoord2f(sCoord, tCoord);
		#else
		GL11.glTexCoord2f(sCoord, tCoord);
		#endif
	}

	public static void glVertex2f(final float x, final float y) {
		GL11.glVertex2f(x, y);
	}

	public static void glVertex3f(final float x, final float y, final float z) {
		GL11.glVertex3f(x, y, z);
	}

	public static void glLineWidth(final float width) {
		GL11.glLineWidth(width);
	}

	public static void glStencilFunc(final int func, final int ref, final int mask) {
		GL11.glStencilFunc(func, ref, mask);
	}

	public static void glStencilMask(final int mask) {
		GL11.glStencilMask(mask);
	}

	public static void glStencilOp(final int fail, final int zfail, final int zpass) {
		GL11.glStencilOp(fail, zfail, zpass);
	}

	private static @Nullable ContextCapabilities capabilities;

	public static boolean openGl30() {
		if (capabilities == null)
			capabilities = GLContext.getCapabilities();
		final ContextCapabilities cap = capabilities;
		return cap != null && cap.OpenGL30;
	}

	public static void glGenerateMipmap(final int target) {
		GL30.glGenerateMipmap(target);
	}

	public static void glBindTexture(final int target, final int texture) {
		#if MC_7_LATER
		if (target == GL11.GL_TEXTURE_2D)
			GlStateManager.bindTexture(texture);
		else
		#endif
			GL11.glBindTexture(target, texture);
	}

	public static void glDeleteTextures(final int texture) {
		#if MC_7_LATER
		GlStateManager.deleteTexture(texture);
		#else
		TextureUtil.deleteTexture(texture);
		#endif
	}
}