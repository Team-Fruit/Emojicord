package net.teamfruit.emojicord.asm;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.teamfruit.emojicord.asm.lib.RefName;
import net.teamfruit.emojicord.compat.CompatBaseVersion;
import net.teamfruit.emojicord.compat.CompatVersion;

public class ASMDeobfNames {
	public static final @Nonnull RefName GuiScreenSendMessage = RefName.deobName("sendChatMessage", "func_175281_b");
	public static final @Nonnull RefName GuiTextFieldDrawTextBox = RefName.deobName("drawTextBox", "func_146194_f");
	public static final @Nonnull RefName TextFieldWidgetRenderButton = RefName.deobName("renderButton", "renderButton");
	public static final @Nonnull RefName FontRendererRenderStringAtPos = ((Supplier<RefName>) () -> {
		if (CompatVersion.version().older(CompatBaseVersion.V11))
			return RefName.deobName("renderStringAtPos", "func_78255_a");
		else
			return RefName.deobName("renderStringAtPos", "func_211843_b");
	}).get();
	public static final @Nonnull RefName FontRendererRenderChar = RefName.deobName("renderChar", "func_181559_a");
	public static final @Nonnull RefName FontRendererRenderGlyph = RefName.deobName("func_212452_a", "func_212452_a");
	public static final @Nonnull RefName FontRendererGetStringWidth = RefName.deobName("getStringWidth", "func_78256_a");
	public static final @Nonnull RefName FontRendererGetCharWidth = ((Supplier<RefName>) () -> {
		if (CompatVersion.version().older(CompatBaseVersion.V11))
			return RefName.deobName("getCharWidth", "func_78263_a");
		else
			return RefName.deobName("getCharWidth", "func_211125_a");
	}).get();
	public static final @Nonnull RefName FontRendererPosX = RefName.deobName("posX", "field_78295_j");
	public static final @Nonnull RefName FontRendererPosY = RefName.deobName("posY", "field_78296_k");
	public static final @Nonnull RefName FontRendererRed = RefName.deobName("red", "field_78291_n");
	public static final @Nonnull RefName FontRendererGreen = RefName.deobName("green", "field_78306_p");
	public static final @Nonnull RefName FontRendererBlue = RefName.deobName("blue", "field_78292_o");
	public static final @Nonnull RefName FontRendererAlpha = RefName.deobName("alpha", "field_78305_q");
	public static final @Nonnull RefName FontFindGlyph = RefName.deobName("findGlyph", "func_211184_b");
	public static final @Nonnull RefName TexturedGlyphRender = RefName.deobName("render", "func_211234_a");
}
