package net.teamfruit.emojicord;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.teamfruit.emojicord.compat.Compat.CompatMinecraft;
import net.teamfruit.emojicord.compat.Compat.CompatTextureUtil;
import net.teamfruit.emojicord.compat.OpenGL;

public abstract class DynamicImageTexture implements ImageTexture {
	public static final @Nonnull DynamicImageTexture NULL = new DynamicTexture(null, 1, 1);

	protected final int width;
	protected final int height;
	private int id = -1;

	public DynamicImageTexture(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	public @Nonnull DynamicImageTexture load() {
		if (this.id == -1) {
			this.id = OpenGL.glGenTextures();
			loadDirect();
		}
		return this;
	}

	protected abstract @Nonnull DynamicImageTexture loadDirect();

	@Override
	public abstract boolean hasMipmap();

	public int getId() {
		return this.id;
	}

	@Override
	public void bind() {
		if (this.id != -1)
			OpenGL.glBindTexture(GL_TEXTURE_2D, this.id);
	}

	public void delete() {
		if (this.id != -1)
			OpenGL.glDeleteTextures(this.id);
		this.id = -1;
	}

	public static @Nonnull DynamicImageTexture create(final @Nonnull BufferedImage image, final int width,
			final int height) {
		if (EmojicordConfig.renderMipmapEnabled)
			return MipmapDynamicTexture.createFromImage(image, width, height,
					CompatMinecraft.getMinecraft().gameSettings.mipmapLevels);
		else
			return DynamicTexture.createFromImage(image, width, height);
	}

	public static @Nonnull DynamicImageTexture createSized(final @Nonnull BufferedImage image) {
		if (EmojicordConfig.renderMipmapEnabled)
			return MipmapDynamicTexture.createFromSizedImage(image,
					CompatMinecraft.getMinecraft().gameSettings.mipmapLevels);
		else
			return DynamicTexture.createFromSizedImage(image);
	}

	public static class DynamicTexture extends DynamicImageTexture {
		private @Nullable int[] image;

		private DynamicTexture(final @Nullable int[] image, final int width, final int height) {
			super(width, height);
			this.image = image;
		}

		@Override
		protected @Nonnull DynamicImageTexture loadDirect() {
			if (this.image != null) {
				final int id = getId();
				TextureUtil.allocateTexture(id, this.width, this.height);
				TextureUtil.uploadTexture(id, this.image, this.width, this.height);
				this.image = null;
			}
			return this;
		}

		@Override
		public boolean hasMipmap() {
			return false;
		}

		@Override
		public void delete() {
			super.delete();
			if (this.image != null)
				this.image = null;
		}

		public static @Nonnull DynamicTexture createFromRawData(final @Nullable int[] image, final int width,
				final int height) {
			return new DynamicTexture(image, width, height);
		}

		public static @Nonnull DynamicTexture createFromSizedImage(final @Nonnull BufferedImage sizedImage) {
			final int width = sizedImage.getWidth();
			final int height = sizedImage.getHeight();
			return createFromRawData(sizedImage.getRGB(0, 0, width, height, null, 0, width), width, height);
		}

		public static @Nonnull DynamicTexture createFromImage(final @Nonnull BufferedImage image, final int width,
				final int height) {
			// return same size
			if (width == image.getWidth() && height == image.getHeight())
				return createFromSizedImage(image);
			// resize
			final BufferedImage s = new BufferedImage(width, height, image.getType());
			final Graphics2D g = s.createGraphics();
			g.drawImage(image.getScaledInstance(width, height,
					EmojicordConfig.renderMipmapFastResize ? java.awt.Image.SCALE_FAST : java.awt.Image.SCALE_SMOOTH),
					0, 0, null);
			g.dispose();
			return createFromSizedImage(s);
		}
	}

	public static class MipmapDynamicTexture extends DynamicImageTexture {
		private @Nullable int[][] mipdata;
		private final int miplevel;

		private MipmapDynamicTexture(final @Nullable int[][] mipdata, final int width, final int height,
				final int miplevel) {
			super(width, height);
			this.mipdata = mipdata;
			this.miplevel = miplevel;
		}

		@Override
		public @Nonnull DynamicImageTexture loadDirect() {
			final int[][] mipdata = this.mipdata;
			if (mipdata != null && mipdata.length >= 1) {
				final int id = getId();
				if (this.miplevel >= 1) {
					CompatTextureUtil.allocateTextureImpl(id, this.miplevel, this.width, this.height,
							CompatMinecraft.getSettings().getAnisotropicFiltering());
					TextureUtil.uploadTextureMipmap(mipdata, this.width, this.height, 0, 0, false, false);
				} else {
					TextureUtil.allocateTexture(id, this.width, this.height);
					TextureUtil.uploadTexture(id, mipdata[0], this.width, this.height);
				}
				this.mipdata = null;
			}
			return this;
		}

		@Override
		public boolean hasMipmap() {
			return this.miplevel >= 1;
		}

		@Override
		public void delete() {
			super.delete();
			if (this.mipdata != null)
				this.mipdata = null;
		}

		public static @Nonnull MipmapDynamicTexture createFromRawMipmap(final @Nullable int[][] mipdata,
				final int width, final int height, final int miplevel) {
			return new MipmapDynamicTexture(mipdata, width, height, miplevel);
		}

		public static @Nonnull MipmapDynamicTexture createFromRawData(final @Nonnull int[] image, final int width,
				final int height, final int miplevel) {
			final int[][] aint = new int[miplevel + 1][];
			aint[0] = image;
			final int[][] bint = generateMipmapData(miplevel, width, aint);
			return new MipmapDynamicTexture(bint, width, height, miplevel);
		}

		public static @Nonnull MipmapDynamicTexture createFromSizedImage(final @Nonnull BufferedImage sizedImage,
				final int miplevel) {
			final int width = sizedImage.getWidth();
			final int height = sizedImage.getHeight();
			return createFromRawData(sizedImage.getRGB(0, 0, width, height, null, 0, width), width, height, miplevel);
		}

		public static @Nonnull MipmapDynamicTexture createFromImage(final @Nonnull BufferedImage image, final int width,
				final int height, final int miplevel) {
			// get resized mipmap size unit
			final int unitmip = 2 << miplevel;
			final int nwidth = (int) Math.ceil(width / (double) unitmip) * unitmip;
			final int nheight = (int) Math.ceil(height / (double) unitmip) * unitmip;
			// return same size
			if (nwidth == image.getWidth() && nheight == image.getHeight())
				return createFromSizedImage(image, miplevel);
			// resize
			final BufferedImage s = new BufferedImage(nwidth, nheight, image.getType());
			final Graphics2D g = s.createGraphics();
			g.drawImage(image.getScaledInstance(nwidth, nheight,
					EmojicordConfig.renderMipmapFastResize ? java.awt.Image.SCALE_FAST : java.awt.Image.SCALE_SMOOTH),
					0, 0, null);
			g.dispose();
			return createFromSizedImage(s, miplevel);
		}

		public static @Nonnull MipmapDynamicTexture createFromImage(final @Nonnull BufferedImage image,
				final int miplevel) {
			return createFromImage(image, image.getWidth(), image.getHeight(), miplevel);
		}

		public static int[][] generateMipmapData(final int miplevel, final int width, final int[][] mipdata) {
			final int[][] aint1 = new int[miplevel + 1][];
			aint1[0] = mipdata[0];

			if (miplevel > 0) {
				boolean flag = false;
				int k;

				for (k = 0; k < mipdata.length; ++k)
					if (mipdata[0][k] >> 24 == 0) {
						flag = true;
						break;
					}

				for (k = 1; k <= miplevel; ++k)
					if (mipdata[k] != null)
						aint1[k] = mipdata[k];
					else {
						final int[] aint2 = aint1[k - 1];
						final int[] aint3 = new int[aint2.length >> 2];
						final int l = width >> k;
						final int i1 = aint3.length / l;
						final int j1 = l << 1;

						for (int k1 = 0; k1 < l; ++k1)
							for (int l1 = 0; l1 < i1; ++l1) {
								final int i2 = 2 * (k1 + l1 * j1);
								aint3[k1 + l1 * l] = calcPixel(aint2[i2 + 0], aint2[i2 + 1], aint2[i2 + 0 + j1],
										aint2[i2 + 1 + j1], flag);
							}

						aint1[k] = aint3;
					}
			}

			return aint1;
		}

		private static int calcPixel(final int pixel1, final int pixel2, final int pixel3, final int pixel4,
				final boolean isClearSize) {
			if (!isClearSize) {
				final int i2 = calcClearPixel(pixel1, pixel2, pixel3, pixel4, 24);
				final int j2 = calcClearPixel(pixel1, pixel2, pixel3, pixel4, 16);
				final int k2 = calcClearPixel(pixel1, pixel2, pixel3, pixel4, 8);
				final int l2 = calcClearPixel(pixel1, pixel2, pixel3, pixel4, 0);
				return i2 << 24 | j2 << 16 | k2 << 8 | l2;
			} else {
				final int[] pixel = new int[] { pixel1, pixel2, pixel3, pixel4 };
				float f = 0.0F;
				float f1 = 0.0F;
				float f2 = 0.0F;
				float f3 = 0.0F;

				for (int i1 = 0; i1 < 4; ++i1)
					if (pixel[i1] >> 24 != 0) {
						f += (float) Math.pow((pixel[i1] >> 24 & 255) / 255.0F, 2.2D);
						f1 += (float) Math.pow((pixel[i1] >> 16 & 255) / 255.0F, 2.2D);
						f2 += (float) Math.pow((pixel[i1] >> 8 & 255) / 255.0F, 2.2D);
						f3 += (float) Math.pow((pixel[i1] >> 0 & 255) / 255.0F, 2.2D);
					}

				int i1 = (int) (Math.pow(f / 4.0F, 0.45454545454545453D) * 255.0D);
				final int j1 = (int) (Math.pow(f1 / 4.0F, 0.45454545454545453D) * 255.0D);
				final int k1 = (int) (Math.pow(f2 / 4.0F, 0.45454545454545453D) * 255.0D);
				final int l1 = (int) (Math.pow(f3 / 4.0F, 0.45454545454545453D) * 255.0D);

				if (i1 < 96)
					i1 = 0;

				return i1 << 24 | j1 << 16 | k1 << 8 | l1;
			}
		}

		private static int calcClearPixel(final int pixel1, final int pixel2, final int pixel3, final int pixel4,
				final int color) {
			final float f = (float) Math.pow((pixel1 >> color & 255) / 255.0F, 2.2D);
			final float f1 = (float) Math.pow((pixel2 >> color & 255) / 255.0F, 2.2D);
			final float f2 = (float) Math.pow((pixel3 >> color & 255) / 255.0F, 2.2D);
			final float f3 = (float) Math.pow((pixel4 >> color & 255) / 255.0F, 2.2D);
			final float f4 = (float) Math.pow((f + f1 + f2 + f3) * 0.25D, 0.45454545454545453D);
			return (int) (f4 * 255.0D);
		}
	}
}
