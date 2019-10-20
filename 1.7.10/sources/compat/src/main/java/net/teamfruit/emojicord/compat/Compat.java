package net.teamfruit.emojicord.compat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.teamfruit.emojicord.compat.VersionChecker.CheckResult;

public class Compat {
	public static class CompatMinecraft {
		private final Minecraft mc;

		public CompatMinecraft(final Minecraft mc) {
			this.mc = mc;
		}

		public Minecraft getMinecraftObj() {
			return this.mc;
		}

		public static @Nonnull CompatMinecraft getMinecraft() {
			return new CompatMinecraft(FMLClientHandler.instance().getClient());
		}

		public @Nonnull CompatFontRenderer getFontRenderer() {
			return new CompatFontRenderer(this.mc.fontRenderer);
		}

		public @Nullable CompatSign.CompatWorld getWorld() {
			final World world = this.mc.theWorld;
			if (world!=null)
				return new CompatSign.CompatWorld(world);
			return null;
		}

		public @Nullable CompatSign.CompatEntityPlayer getPlayer() {
			final EntityPlayer player = this.mc.thePlayer;
			if (player!=null)
				return new CompatSign.CompatEntityPlayer(player);
			return null;
		}

		public @Nonnull CompatGameSettings getSettings() {
			return new CompatGameSettings(this.mc.gameSettings);
		}

		public @Nullable CompatSign.CompatNetHandlerPlayClient getConnection() {
			final NetHandlerPlayClient connection = this.mc.getNetHandler();
			return connection!=null ? new CompatSign.CompatNetHandlerPlayClient(connection) : null;
		}

		public TextureManager getTextureManager() {
			return this.mc.getTextureManager();
		}

		public File getGameDir() {
			return this.mc.mcDataDir;
		}

		public boolean isGameFocused() {
			return this.mc.inGameHasFocus;
		}

		public CompatSession getSession() {
			return new CompatSession(this.mc.getSession());
		}
	}

	public static class CompatFontRenderer {
		private final FontRenderer font;

		public CompatFontRenderer(final FontRenderer font) {
			this.font = font;
		}

		public int drawString(final String msg, final float x, final float y, final int color, final boolean shadow) {
			return this.font.drawString(msg, (int) x, (int) y, color, shadow);
		}

		public int drawString(final String msg, final float x, final float y, final int color) {
			return drawString(msg, x, y, color, false);
		}

		public int drawStringWithShadow(final String msg, final float x, final float y, final int color) {
			return drawString(msg, x, y, color, true);
		}

		public String wrapFormattedStringToWidth(final String msg, final int width) {
			return this.font.wrapFormattedStringToWidth(msg, width);
		}

		public int getStringWidth(final @Nullable String s) {
			return this.font.getStringWidth(s);
		}

		public int getStringWidthWithoutFormattingCodes(final @Nullable String s) {
			return getStringWidth(EnumChatFormatting.getTextWithoutFormattingCodes(s));
		}

		public FontRenderer getFontRendererObj() {
			return this.font;
		}
	}

	public static class CompatGameSettings {
		private final GameSettings settings;

		public CompatGameSettings(final GameSettings settings) {
			this.settings = settings;
		}

		public GameSettings getSettingsObj() {
			return this.settings;
		}

		public int getAnisotropicFiltering() {
			return this.settings.anisotropicFiltering;
		}

		public String getLanguage() {
			return this.settings.language;
		}
	}

	public static class CompatSoundHandler {
		public static void playSound(final @Nonnull ResourceLocation location, final float volume) {
			CompatMinecraft.getMinecraft().getMinecraftObj().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(location, volume));
		}
	}

	public static class CompatTextureUtil {
		public static final DynamicTexture missingTexture = TextureUtil.missingTexture;

		public static void processPixelValues(final int[] pixel, final int displayWidth, final int displayHeight) {
			TextureUtil.func_147953_a(pixel, displayWidth, displayHeight);
		}

		public static void allocateTextureImpl(final int id, final int miplevel, final int width, final int height, final float anisotropicFiltering) {
			TextureUtil.allocateTextureImpl(id, miplevel, width, height, anisotropicFiltering);
		}
	}

	public static class CompatMathHelper {
		public static int floor_float(final float value) {
			return MathHelper.floor_float(value);
		}

		public static int floor_double(final double value) {
			return MathHelper.floor_double(value);
		}
	}

	public static class CompatI18n {
		public static String format(final String format, final Object... args) {
			return I18n.format(format, args);
		}

		public static boolean hasKey(final String key) {
			return StatCollector.canTranslate(key);
		}

		public static String translateToLocal(final String text) {
			return StatCollector.translateToLocal(text);
		}
	}

	public static class CompatTexture {
		private final CompatSimpleTexture texture;

		public CompatTexture(final CompatSimpleTexture texture) {
			this.texture = texture;
		}

		public static CompatTexture getTexture(final CompatSimpleTexture texture) {
			return new CompatTexture(texture);
		}

		public CompatSimpleTexture getTextureObj() {
			return this.texture;
		}

		public void bindTexture() {
			OpenGL.glBindTexture(GL11.GL_TEXTURE_2D, this.texture.getGlTextureId());
		}

		public void uploadTexture(final InputStream image) throws IOException {
			this.texture.deleteGlTexture();

			final BufferedImage bufferedimage = ImageIO.read(image);
			final boolean blur = true;
			final boolean clamp = false;

			if (bufferedimage!=null)
				TextureUtil.uploadTextureImageAllocate(this.texture.getRawGlTextureId(), bufferedimage, blur, clamp);
		}
	}

	public static class CompatResourceManager {
		private final IResourceManager manager;

		public CompatResourceManager(final IResourceManager manager) {
			this.manager = manager;
		}

		public IResourceManager getManagerObj() {
			return this.manager;
		}
	}

	public static class CompatSimpleTexture extends SimpleTexture {
		public CompatSimpleTexture(final ResourceLocation textureResourceLocation) {
			super(textureResourceLocation);
		}

		public int getRawGlTextureId() {
			return super.getGlTextureId();
		}

		@Override
		public void loadTexture(final IResourceManager manager) throws IOException {
			loadTexture(new CompatResourceManager(manager));
		}

		public void loadTexture(final CompatResourceManager manager) throws IOException {
			super.loadTexture(manager.getManagerObj());
		}
	}

	public enum CompatSide {
		COMMON,
		CLIENT,
		SERVER,
		;

		public Side toSide() {
			switch (this) {
				case CLIENT:
					return Side.CLIENT;
				case SERVER:
					return Side.SERVER;
				default:
					return Side.SERVER;
			}
		}

		public static CompatSide fromSide(final Side type) {
			switch (type) {
				case CLIENT:
					return CLIENT;
				case SERVER:
					return SERVER;
				default:
					return COMMON;
			}
		}
	}

	public static class CompatBufferBuilder {
		public CompatBufferBuilder() {
		}
	}

	public static abstract class CompatGlyph {
		public CompatGlyph(final float width, final float height) {
		}
	}

	public static abstract class CompatTexturedGlyph {
		public CompatTexturedGlyph(final ResourceLocation texture, final float width, final float height) {
		}

		public void onRender(final TextureManager textureManager, final boolean hasShadow, final float x, final float y, final CompatBufferBuilder vbuilder, final float red, final float green, final float blue, final float alpha) {
		}
	}

	public static class CompatVersionChecker {
		public static void startVersionCheck(final String modId, final String modVersion, final String updateURL) {
			VersionChecker.startVersionCheck(modId, modVersion, updateURL);
		}

		public static CompatCheckResult getResult(final String modId) {
			return CompatCheckResult.from(VersionChecker.getResult());
		}

		public static class CompatCheckResult {
			@Nonnull
			public final CompatStatus status;
			@Nullable
			public final String target;
			@Nullable
			public final Map<String, String> changes;
			@Nullable
			public final String url;

			public CompatCheckResult(@Nonnull final CompatStatus status, @Nullable final String target, @Nullable final Map<String, String> changes, @Nullable final String url) {
				this.status = status;
				this.target = target;
				this.changes = changes==null ? Collections.<String, String> emptyMap() : Collections.unmodifiableMap(changes);
				this.url = url;
			}

			public static CompatCheckResult from(final CheckResult result) {
				Map<String, String> compatChanges = null;
				if (result.changes!=null)
					compatChanges = result.changes.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
				return new CompatCheckResult(CompatStatus.getStatus(result.status),
						result.target!=null ? result.target.toString() : null,
						compatChanges,
						result.url);
			}
		}

		public static enum CompatStatus {
			PENDING,
			FAILED,
			UP_TO_DATE,
			OUTDATED,
			AHEAD,
			BETA,
			BETA_OUTDATED,
			;

			public static CompatStatus getStatus(final VersionChecker.Status status) {
				switch (status) {
					default:
					case PENDING:
						return CompatStatus.PENDING;
					case FAILED:
						return CompatStatus.FAILED;
					case UP_TO_DATE:
						return CompatStatus.UP_TO_DATE;
					case OUTDATED:
						return CompatStatus.OUTDATED;
					case AHEAD:
						return CompatStatus.AHEAD;
					case BETA:
						return CompatStatus.BETA;
					case BETA_OUTDATED:
						return CompatStatus.BETA_OUTDATED;
				}
			}
		}
	}

	public static class CompatSession {
		private final Session session;

		public CompatSession(final Session session) {
			this.session = session;
		}

		public String getPlayerID() {
			return this.session.getPlayerID();
		}

		public String getUsername() {
			return this.session.getUsername();
		}

		public String getToken() {
			return this.session.getToken();
		}
	}

	public static class CompatMinecraftVersion {
		public static String getMinecraftVersion() {
			return MinecraftForge.MC_VERSION;
		}

		public static String getForgeVersion() {
			return ForgeVersion.getVersion();
		}
	}
}
