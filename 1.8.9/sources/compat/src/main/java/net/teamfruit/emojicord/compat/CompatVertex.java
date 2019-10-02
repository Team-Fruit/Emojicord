package net.teamfruit.emojicord.compat;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class CompatVertex {
	private static class CompatBaseVertexImpl implements CompatBaseVertex {
		public static final @Nonnull Tessellator t = Tessellator.getInstance();
		public static final @Nonnull WorldRenderer w = t.getWorldRenderer();

		public CompatBaseVertexImpl() {
		}

		@Override
		public void draw() {
			endVertex();
			t.draw();
		}

		@Override
		public @Nonnull CompatBaseVertex begin(final int mode) {
			w.begin(mode, DefaultVertexFormats.POSITION);
			init();
			return this;
		}

		@Override
		public @Nonnull CompatBaseVertex beginTexture(final int mode) {
			w.begin(mode, DefaultVertexFormats.POSITION_TEX);
			init();
			return this;
		}

		private void init() {
			this.stack = false;
		}

		private boolean stack;

		@Override
		public @Nonnull CompatBaseVertex pos(final double x, final double y, final double z) {
			endVertex();
			w.pos(x, y, z);
			this.stack = true;
			return this;
		}

		@Override
		public @Nonnull CompatBaseVertex tex(final double u, final double v) {
			w.tex(u, v);
			return this;
		}

		@Override
		public @Nonnull CompatBaseVertex color(final float red, final float green, final float blue, final float alpha) {
			return this.color((int) (red*255.0F), (int) (green*255.0F), (int) (blue*255.0F), (int) (alpha*255.0F));
		}

		@Override
		public @Nonnull CompatBaseVertex color(final int red, final int green, final int blue, final int alpha) {
			w.putColorRGBA(0, red, green, blue, alpha);
			return this;
		}

		@Override
		public @Nonnull CompatBaseVertex normal(final float nx, final float ny, final float nz) {
			w.normal(nx, ny, nz);
			return this;
		}

		@Override
		public void setTranslation(final double x, final double y, final double z) {
			w.setTranslation(x, y, z);
		}

		private void endVertex() {
			if (this.stack) {
				this.stack = false;
				w.endVertex();
			}
		}
	}

	public static @Nonnull CompatBaseVertex getTessellator() {
		return new CompatBaseVertexImpl();
	}
}
