package net.teamfruit.emojicord.compat;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.Tessellator;

public class CompatVertex {
	private static class CompatBaseVertexImpl implements CompatBaseVertex {
		public static final @Nonnull Tessellator t = Tessellator.instance;

		public CompatBaseVertexImpl() {
		}

		@Override
		public void draw() {
			endVertex();
			t.draw();
		}

		@Override
		public CompatBaseVertex begin(final int mode) {
			t.startDrawing(mode);
			init();
			return this;
		}

		@Override
		public CompatBaseVertex beginTexture(final int mode) {
			t.startDrawing(mode);
			init();
			return this;
		}

		private void init() {
			this.stack = false;
		}

		private boolean stack;
		private double stack_x;
		private double stack_y;
		private double stack_z;

		@Override
		public CompatBaseVertex pos(final double x, final double y, final double z) {
			endVertex();
			this.stack_x = x;
			this.stack_y = y;
			this.stack_z = z;
			this.stack = true;
			return this;
		}

		@Override
		public CompatBaseVertex tex(final double u, final double v) {
			t.setTextureUV(u, v);
			return this;
		}

		@Override
		public CompatBaseVertex color(final float red, final float green, final float blue, final float alpha) {
			return this.color((int) (red*255.0F), (int) (green*255.0F), (int) (blue*255.0F), (int) (alpha*255.0F));
		}

		@Override
		public CompatBaseVertex color(final int red, final int green, final int blue, final int alpha) {
			t.setColorRGBA(red, green, blue, alpha);
			return this;
		}

		@Override
		public CompatBaseVertex normal(final float nx, final float ny, final float nz) {
			t.setNormal(nx, ny, nz);
			return this;
		}

		@Override
		public void setTranslation(final double x, final double y, final double z) {
			t.setTranslation(x, y, z);
		}

		private void endVertex() {
			if (this.stack) {
				this.stack = false;
				t.addVertex(this.stack_x, this.stack_y, this.stack_z);
			}
		}
	}

	public static @Nonnull CompatBaseVertex getTessellator() {
		return new CompatBaseVertexImpl();
	}
}
