package net.minecraft.client.gui;

/*
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;

import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.teamfruit.emojicord.emoji.EmojiFontRenderer;
import net.teamfruit.emojicord.emoji.EmojiFontRenderer.EmojiGlyph;

@OnlyIn(Dist.CLIENT)
public class FontRenderer implements AutoCloseable {
	private static final Logger LOGGER;
	public int FONT_HEIGHT;
	public Random fontRandom;
	private final TextureManager textureManager;
	private final Font font;
	private boolean bidiFlag;

	public FontRenderer(final TextureManager p_i49744_1_, final Font p_i49744_2_) {
		this.FONT_HEIGHT = 9;
		this.fontRandom = new Random();
		this.textureManager = p_i49744_1_;
		this.font = p_i49744_2_;
	}

	public void setGlyphProviders(final List<IGlyphProvider> p_211568_1_) {
		this.font.setGlyphProviders(p_211568_1_);
	}

	@Override
	public void close() {
		this.font.close();
	}

	public int drawStringWithShadow(final String p_175063_1_, final float p_175063_2_, final float p_175063_3_, final int p_175063_4_) {
		GlStateManager.enableAlphaTest();

		return renderString(p_175063_1_, p_175063_2_, p_175063_3_, p_175063_4_, true);
	}

	public int drawString(final String p_211126_1_, final float p_211126_2_, final float p_211126_3_, final int p_211126_4_) {
		GlStateManager.enableAlphaTest();

		return renderString(p_211126_1_, p_211126_2_, p_211126_3_, p_211126_4_, false);
	}

	private String bidiReorder(final String p_147647_1_) {
		try {
			final Bidi lvt_2_1_ = new Bidi(new ArabicShaping(8).shape(p_147647_1_), 127);
			lvt_2_1_.setReorderingMode(0);
			return lvt_2_1_.writeReordered(2);

		} catch (final ArabicShapingException ex) {
			return p_147647_1_;
		}
	}

	private int renderString(String p_180455_1_, float p_180455_2_, final float p_180455_3_, int p_180455_4_, final boolean p_180455_5_) {
		if (p_180455_1_==null)
			return 0;

		if (this.bidiFlag)
			p_180455_1_ = bidiReorder(p_180455_1_);

		if ((p_180455_4_&0xFC000000)==0x0)
			p_180455_4_ |= 0xFF000000;

		if (p_180455_5_)
			renderStringAtPos(p_180455_1_, p_180455_2_, p_180455_3_, p_180455_4_, true);

		p_180455_2_ = renderStringAtPos(p_180455_1_, p_180455_2_, p_180455_3_, p_180455_4_, false);

		return (int) p_180455_2_+(p_180455_5_ ? 1 : 0);
	}

	private float renderStringAtPos(final String p_211843_1_, float x, final float y, final int p_211843_4_, final boolean hasShadow) {
		final float lvt_6_1_ = hasShadow ? 0.25f : 1.0f;
		final float lvt_7_1_ = (p_211843_4_>>16&0xFF)/255.0f*lvt_6_1_;
		final float lvt_8_1_ = (p_211843_4_>>8&0xFF)/255.0f*lvt_6_1_;
		final float lvt_9_1_ = (p_211843_4_&0xFF)/255.0f*lvt_6_1_;

		float red = lvt_7_1_;
		float green = lvt_8_1_;
		float blue = lvt_9_1_;
		final float alpha = (p_211843_4_>>24&0xFF)/255.0f;

		final Tessellator lvt_14_1_ = Tessellator.getInstance();
		final BufferBuilder vbuilder = lvt_14_1_.getBuffer();
		ResourceLocation lastGlyphTexture = null;
		vbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

		boolean obfuscated = false;
		boolean bold = false;
		boolean shadow = false;
		boolean lvt_20_1_ = false;
		boolean lvt_21_1_ = false;

		final List<Entry> lvt_22_1_ = Lists.newArrayList();

		for (int index = 0; index<p_211843_1_.length(); ++index) {
			final char character = p_211843_1_.charAt(index);

			if (character=='§'&&index+1<p_211843_1_.length()) {
				final TextFormatting lvt_25_1_ = TextFormatting.fromFormattingCode(p_211843_1_.charAt(index+1));
				if (lvt_25_1_!=null) {
					if (lvt_25_1_.isNormalStyle()) {
						obfuscated = false;
						bold = false;
						lvt_21_1_ = false;
						lvt_20_1_ = false;
						shadow = false;
						red = lvt_7_1_;
						green = lvt_8_1_;
						blue = lvt_9_1_;

					}
					if (lvt_25_1_.getColor()!=null) {
						final int lvt_26_1_ = lvt_25_1_.getColor();
						red = (lvt_26_1_>>16&0xFF)/255.0f*lvt_6_1_;
						green = (lvt_26_1_>>8&0xFF)/255.0f*lvt_6_1_;
						blue = (lvt_26_1_&0xFF)/255.0f*lvt_6_1_;
					} else if (lvt_25_1_==TextFormatting.OBFUSCATED)
						obfuscated = true;

					else if (lvt_25_1_==TextFormatting.BOLD)
						bold = true;

					else if (lvt_25_1_==TextFormatting.STRIKETHROUGH)
						lvt_21_1_ = true;

					else if (lvt_25_1_==TextFormatting.UNDERLINE)
						lvt_20_1_ = true;

					else if (lvt_25_1_==TextFormatting.ITALIC)
						shadow = true;

				}
				++index;

			} else {
				final IGlyph glyph;
				final TexturedGlyph texturedglyph;
				final EmojiGlyph emojiGlyph = EmojiFontRenderer.getEmojiGlyph(character, index);
				if (emojiGlyph!=null) {
					glyph = emojiGlyph;
					texturedglyph = emojiGlyph;
				} else {
					glyph = this.font.findGlyph(character);
					texturedglyph = obfuscated&&character!=' ' ? this.font.obfuscate(glyph) : this.font.getGlyph(character);
				}

				final ResourceLocation glyphtexture = texturedglyph.getTextureLocation();
				if (glyphtexture!=null) {
					if (lastGlyphTexture!=glyphtexture) {
						lvt_14_1_.draw();
						this.textureManager.bindTexture(glyphtexture);
						vbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
						lastGlyphTexture = glyphtexture;

					}
					final float boldoffset = bold ? glyph.getBoldOffset() : 0.0f;
					final float shadowoffset = hasShadow ? glyph.getShadowOffset() : 0.0f;

					func_212452_a(texturedglyph, bold, shadow, boldoffset, x+shadowoffset, y+shadowoffset, vbuilder, red, green, blue, alpha);

				}
				final float lvt_28_2_ = glyph.getAdvance(bold);

				final float lvt_29_2_ = hasShadow ? 1.0f : 0.0f;
				if (lvt_21_1_)
					lvt_22_1_.add(new Entry(x+lvt_29_2_-1.0f, y+lvt_29_2_+this.FONT_HEIGHT/2.0f, x+lvt_29_2_+lvt_28_2_, y+lvt_29_2_+this.FONT_HEIGHT/2.0f-1.0f, red, green, blue, alpha));

				if (lvt_20_1_)
					lvt_22_1_.add(new Entry(x+lvt_29_2_-1.0f, y+lvt_29_2_+this.FONT_HEIGHT, x+lvt_29_2_+lvt_28_2_, y+lvt_29_2_+this.FONT_HEIGHT-1.0f, red, green, blue, alpha));

				x += lvt_28_2_;
			}
		}
		lvt_14_1_.draw();

		if (!lvt_22_1_.isEmpty()) {
			GlStateManager.disableTexture2D();
			vbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
			for (final Entry lvt_24_2_ : lvt_22_1_)
				lvt_24_2_.pipe(vbuilder);

			lvt_14_1_.draw();
			GlStateManager.enableTexture2D();
		}
		return x;
	}

	private float renderStringAtPos2(final String p_211843_1_, float x, final float y, final int p_211843_4_, final boolean hasShadow) {
		final float lvt_6_1_ = hasShadow ? 0.25f : 1.0f;
		final float lvt_7_1_ = (p_211843_4_>>16&0xFF)/255.0f*lvt_6_1_;
		final float lvt_8_1_ = (p_211843_4_>>8&0xFF)/255.0f*lvt_6_1_;
		final float lvt_9_1_ = (p_211843_4_&0xFF)/255.0f*lvt_6_1_;

		float red = lvt_7_1_;
		float green = lvt_8_1_;
		float blue = lvt_9_1_;
		final float alpha = (p_211843_4_>>24&0xFF)/255.0f;

		final Tessellator lvt_14_1_ = Tessellator.getInstance();
		final BufferBuilder vbuilder = lvt_14_1_.getBuffer();
		ResourceLocation lastGlyphTexture = null;
		vbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

		boolean obfuscated = false;
		boolean bold = false;
		boolean shadow = false;
		boolean lvt_20_1_ = false;
		boolean lvt_21_1_ = false;

		final List<Entry> lvt_22_1_ = Lists.newArrayList();

		for (int index = 0; index<p_211843_1_.length(); ++index) {
			final char character = p_211843_1_.charAt(index);

			if (character=='§'&&index+1<p_211843_1_.length()) {
				final TextFormatting lvt_25_1_ = TextFormatting.fromFormattingCode(p_211843_1_.charAt(index+1));
				if (lvt_25_1_!=null) {
					if (lvt_25_1_.isNormalStyle()) {
						obfuscated = false;
						bold = false;
						lvt_21_1_ = false;
						lvt_20_1_ = false;
						shadow = false;
						red = lvt_7_1_;
						green = lvt_8_1_;
						blue = lvt_9_1_;

					}
					if (lvt_25_1_.getColor()!=null) {
						final int lvt_26_1_ = lvt_25_1_.getColor();
						red = (lvt_26_1_>>16&0xFF)/255.0f*lvt_6_1_;
						green = (lvt_26_1_>>8&0xFF)/255.0f*lvt_6_1_;
						blue = (lvt_26_1_&0xFF)/255.0f*lvt_6_1_;
					} else if (lvt_25_1_==TextFormatting.OBFUSCATED)
						obfuscated = true;

					else if (lvt_25_1_==TextFormatting.BOLD)
						bold = true;

					else if (lvt_25_1_==TextFormatting.STRIKETHROUGH)
						lvt_21_1_ = true;

					else if (lvt_25_1_==TextFormatting.UNDERLINE)
						lvt_20_1_ = true;

					else if (lvt_25_1_==TextFormatting.ITALIC)
						shadow = true;

				}
				++index;

			} else {
				final IGlyph glyph = this.font.findGlyph(character);
				final TexturedGlyph texturedglyph = obfuscated&&character!=' ' ? this.font.obfuscate(glyph) : this.font.getGlyph(character);

				final ResourceLocation glyphtexture = texturedglyph.getTextureLocation();
				if (glyphtexture!=null) {
					if (lastGlyphTexture!=glyphtexture) {
						lvt_14_1_.draw();
						this.textureManager.bindTexture(glyphtexture);
						vbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
						lastGlyphTexture = glyphtexture;

					}
					final float boldoffset = bold ? glyph.getBoldOffset() : 0.0f;
					final float shadowoffset = hasShadow ? glyph.getShadowOffset() : 0.0f;

					func_212452_a(texturedglyph, bold, shadow, boldoffset, x+shadowoffset, y+shadowoffset, vbuilder, red, green, blue, alpha);

				}
				final float lvt_28_2_ = glyph.getAdvance(bold);

				final float lvt_29_2_ = hasShadow ? 1.0f : 0.0f;
				if (lvt_21_1_)
					lvt_22_1_.add(new Entry(x+lvt_29_2_-1.0f, y+lvt_29_2_+this.FONT_HEIGHT/2.0f, x+lvt_29_2_+lvt_28_2_, y+lvt_29_2_+this.FONT_HEIGHT/2.0f-1.0f, red, green, blue, alpha));

				if (lvt_20_1_)
					lvt_22_1_.add(new Entry(x+lvt_29_2_-1.0f, y+lvt_29_2_+this.FONT_HEIGHT, x+lvt_29_2_+lvt_28_2_, y+lvt_29_2_+this.FONT_HEIGHT-1.0f, red, green, blue, alpha));

				x += lvt_28_2_;
			}
		}
		lvt_14_1_.draw();

		if (!lvt_22_1_.isEmpty()) {
			GlStateManager.disableTexture2D();
			vbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
			for (final Entry lvt_24_2_ : lvt_22_1_)
				lvt_24_2_.pipe(vbuilder);

			lvt_14_1_.draw();
			GlStateManager.enableTexture2D();
		}
		return x;
	}

	private void func_212452_a(final TexturedGlyph texturedglyph, final boolean bold, final boolean shadow, final float offset, final float x, final float y, final BufferBuilder vbuilder, final float red, final float green, final float blue, final float alpha) {
		texturedglyph.render(this.textureManager, shadow, x, y, vbuilder, red, green, blue, alpha);
		if (bold) {
			EmojiFontRenderer.shadow = true;
			texturedglyph.render(this.textureManager, shadow, x+offset, y, vbuilder, red, green, blue, alpha);
			EmojiFontRenderer.shadow = false;
		}
	}

	public int getStringWidth(final String text) {
		if (text==null)
			return 0;

		float width = 0.0f;
		boolean bold = false;

		for (int index = 0; index<text.length(); ++index) {
			final char character = text.charAt(index);

			if (character=='§'&&index<text.length()-1) {
				final TextFormatting formatting = TextFormatting.fromFormattingCode(text.charAt(++index));

				if (formatting==TextFormatting.BOLD)
					bold = true;

				else if (formatting!=null&&formatting.isNormalStyle())
					bold = false;
			} else {
				final EmojiGlyph emojiGlyph = EmojiFontRenderer.getEmojiGlyph(character, index);
				width += (emojiGlyph!=null ? emojiGlyph : this.font.findGlyph(character)).getAdvance(bold);
			}
		}
		return MathHelper.ceil(width);
	}

	public int getStringWidth2(final String text) {
		if (text==null)
			return 0;
		float width = 0.0f;
		boolean bold = false;

		for (int index = 0; index<text.length(); ++index) {
			final char character = text.charAt(index);

			if (character=='§'&&index<text.length()-1) {
				final TextFormatting formatting = TextFormatting.fromFormattingCode(text.charAt(++index));

				if (formatting==TextFormatting.BOLD)
					bold = true;
				else if (formatting!=null&&formatting.isNormalStyle())
					bold = false;
			} else
				width += this.font.findGlyph(character).getAdvance(bold);
		}
		return MathHelper.ceil(width);
	}

	private float getCharWidth(final char p_211125_1_) {
		if (p_211125_1_=='§')
			return 0.0f;

		return MathHelper.ceil(this.font.findGlyph(p_211125_1_).getAdvance(false));
	}

	public String trimStringToWidth(final String p_78269_1_, final int p_78269_2_) {
		return this.trimStringToWidth(p_78269_1_, p_78269_2_, false);
	}

	public String trimStringToWidth(final String p_78262_1_, final int p_78262_2_, final boolean p_78262_3_) {
		final StringBuilder lvt_4_1_ = new StringBuilder();
		float lvt_5_1_ = 0.0f;
		final int lvt_6_1_ = p_78262_3_ ? p_78262_1_.length()-1 : 0;
		final int lvt_7_1_ = p_78262_3_ ? -1 : 1;
		boolean lvt_8_1_ = false;
		boolean lvt_9_1_ = false;

		for (int lvt_10_1_ = lvt_6_1_; lvt_10_1_>=0&&lvt_10_1_<p_78262_1_.length()&&lvt_5_1_<p_78262_2_; lvt_10_1_ += lvt_7_1_) {
			final char lvt_11_1_ = p_78262_1_.charAt(lvt_10_1_);

			if (lvt_8_1_) {
				lvt_8_1_ = false;
				final TextFormatting lvt_12_1_ = TextFormatting.fromFormattingCode(lvt_11_1_);

				if (lvt_12_1_==TextFormatting.BOLD)
					lvt_9_1_ = true;

				else if (lvt_12_1_!=null&&lvt_12_1_.isNormalStyle())
					lvt_9_1_ = false;
			} else if (lvt_11_1_=='§')
				lvt_8_1_ = true;

			else {
				lvt_5_1_ += getCharWidth(lvt_11_1_);
				if (lvt_9_1_)
					++lvt_5_1_;

			}
			if (lvt_5_1_>p_78262_2_)
				break;

			if (p_78262_3_)
				lvt_4_1_.insert(0, lvt_11_1_);

			else
				lvt_4_1_.append(lvt_11_1_);

		}
		return lvt_4_1_.toString();

	}

	private String trimStringNewline(String p_78273_1_) {

		while (p_78273_1_!=null&&p_78273_1_.endsWith("\n"))
			p_78273_1_ = p_78273_1_.substring(0, p_78273_1_.length()-1);

		return p_78273_1_;
	}

	public void drawSplitString(String p_78279_1_, final int p_78279_2_, final int p_78279_3_, final int p_78279_4_, final int p_78279_5_) {
		p_78279_1_ = trimStringNewline(p_78279_1_);

		renderSplitString(p_78279_1_, p_78279_2_, p_78279_3_, p_78279_4_, p_78279_5_);
	}

	private void renderSplitString(final String p_211124_1_, final int p_211124_2_, int p_211124_3_, final int p_211124_4_, final int p_211124_5_) {
		final List<String> lvt_6_1_ = listFormattedStringToWidth(p_211124_1_, p_211124_4_);
		for (final String lvt_8_1_ : lvt_6_1_) {
			float lvt_9_1_ = p_211124_2_;
			if (this.bidiFlag) {
				final int lvt_10_1_ = getStringWidth(bidiReorder(lvt_8_1_));
				lvt_9_1_ += p_211124_4_-lvt_10_1_;
			}
			renderString(lvt_8_1_, lvt_9_1_, p_211124_3_, p_211124_5_, false);
			p_211124_3_ += this.FONT_HEIGHT;
		}
	}

	public int getWordWrappedHeight(final String p_78267_1_, final int p_78267_2_) {
		return this.FONT_HEIGHT*listFormattedStringToWidth(p_78267_1_, p_78267_2_).size();
	}

	public void setBidiFlag(final boolean p_78275_1_) {
		this.bidiFlag = p_78275_1_;
	}

	public List<String> listFormattedStringToWidth(final String p_78271_1_, final int p_78271_2_) {
		return Arrays.asList(wrapFormattedStringToWidth(p_78271_1_, p_78271_2_).split("\n"));
	}

	public String wrapFormattedStringToWidth(String p_78280_1_, final int p_78280_2_) {
		String lvt_3_1_;
		int lvt_4_1_;
		String lvt_5_1_;
		boolean lvt_7_1_;
		for (lvt_3_1_ = ""; !p_78280_1_.isEmpty();

				p_78280_1_ = TextFormatting.getFormatString(lvt_5_1_)+p_78280_1_.substring(lvt_4_1_+(lvt_7_1_ ? 1 : 0)),

				lvt_3_1_ = lvt_3_1_+lvt_5_1_+"\n") {
			lvt_4_1_ = sizeStringToWidth(p_78280_1_, p_78280_2_);
			if (p_78280_1_.length()<=lvt_4_1_)
				return lvt_3_1_+p_78280_1_;
			lvt_5_1_ = p_78280_1_.substring(0, lvt_4_1_);
			final char lvt_6_1_ = p_78280_1_.charAt(lvt_4_1_);
			lvt_7_1_ = lvt_6_1_==' '||lvt_6_1_=='\n';
		}
		return lvt_3_1_;
	}

	private int sizeStringToWidth(final String p_78259_1_, final int p_78259_2_) {
		final int lvt_3_1_ = Math.max(1, p_78259_2_);
		final int lvt_4_1_ = p_78259_1_.length();
		float lvt_5_1_ = 0.0f;
		int lvt_6_1_ = 0;
		int lvt_7_1_ = -1;
		boolean lvt_8_1_ = false;
		boolean lvt_9_1_ = true;

		while (lvt_6_1_<lvt_4_1_) {
			final char lvt_10_1_ = p_78259_1_.charAt(lvt_6_1_);
			Label_0178: {
				switch (lvt_10_1_) {
					case '§': {
						if (lvt_6_1_<lvt_4_1_-1) {
							final TextFormatting lvt_11_1_ = TextFormatting.fromFormattingCode(p_78259_1_.charAt(++lvt_6_1_));
							if (lvt_11_1_==TextFormatting.BOLD)
								lvt_8_1_ = true;

							else if (lvt_11_1_!=null&&lvt_11_1_.isNormalStyle())
								lvt_8_1_ = false;
						}
						break Label_0178;
					}
					case '\n': {
						--lvt_6_1_;
						break Label_0178;
					}
					case ' ': {
						lvt_7_1_ = lvt_6_1_;
						break;
					}
				}
				if (lvt_5_1_!=0.0f)
					lvt_9_1_ = false;

				lvt_5_1_ += getCharWidth(lvt_10_1_);
				if (lvt_8_1_)
					++lvt_5_1_;

			}
			if (lvt_10_1_=='\n') {
				lvt_7_1_ = ++lvt_6_1_;
				break;

			}
			if (lvt_5_1_>lvt_3_1_) {
				if (lvt_9_1_) {
					++lvt_6_1_;
					break;
				}
				break;
			} else
				++lvt_6_1_;
		}
		if (lvt_6_1_!=lvt_4_1_&&lvt_7_1_!=-1&&lvt_7_1_<lvt_6_1_)
			return lvt_7_1_;

		return lvt_6_1_;
	}

	public boolean getBidiFlag() {
		return this.bidiFlag;
	}

	static {
		LOGGER = LogManager.getLogger();
	}

	@OnlyIn(Dist.CLIENT)
	static class Entry {
		protected final float x1;
		protected final float y1;
		protected final float x2;
		protected final float y2;
		protected final float red;
		protected final float green;
		protected final float blue;
		protected final float alpha;

		private Entry(final float p_i49707_1_, final float p_i49707_2_, final float p_i49707_3_, final float p_i49707_4_, final float p_i49707_5_, final float p_i49707_6_, final float p_i49707_7_, final float p_i49707_8_) {
			this.x1 = p_i49707_1_;
			this.y1 = p_i49707_2_;
			this.x2 = p_i49707_3_;
			this.y2 = p_i49707_4_;
			this.red = p_i49707_5_;
			this.green = p_i49707_6_;
			this.blue = p_i49707_7_;
			this.alpha = p_i49707_8_;
		}

		public void pipe(final BufferBuilder p_211168_1_) {
			p_211168_1_.pos(this.x1, this.y1, 0.0).color(this.red, this.green, this.blue, this.alpha).endVertex();
			p_211168_1_.pos(this.x2, this.y1, 0.0).color(this.red, this.green, this.blue, this.alpha).endVertex();
			p_211168_1_.pos(this.x2, this.y2, 0.0).color(this.red, this.green, this.blue, this.alpha).endVertex();
			p_211168_1_.pos(this.x1, this.y2, 0.0).color(this.red, this.green, this.blue, this.alpha).endVertex();
		}
	}
}
*/
