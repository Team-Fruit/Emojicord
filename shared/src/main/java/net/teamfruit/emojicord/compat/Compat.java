package net.teamfruit.emojicord.compat;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

#if MC_7_LATER
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
#else
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
#endif

public class Compat {
	public static @Nonnull
	Minecraft getMinecraft() {
		return FMLClientHandler.instance().getClient();
	}

	public static class CompatI18n {
		public static String format(final String format, final Object... args) {
			return I18n.format(format, args);
		}

		public static boolean hasKey(final String key) {
			#if MC_7_LATER
			return I18n.hasKey(key);
			#else
			return net.minecraft.util.StatCollector.canTranslate(key);
			#endif
		}

		@SuppressWarnings("deprecation")
		public static String translateToLocal(final String text) {
			#if MC_7_LATER
			return net.minecraft.util.text.translation.I18n.translateToLocal(text);
			#else
			return net.minecraft.util.StatCollector.translateToLocal(text);
			#endif
		}
	}

	public static class CompatTexture {
		public static void uploadTexture(SimpleTexture texture, Supplier<Integer> genTextureId, final InputStream image) throws IOException {
			final BufferedImage bufferedimage = #if MC_7_LATER TextureUtil.readBufferedImage #else ImageIO.read #endif (image);
			uploadTexture(texture, genTextureId, bufferedimage);
		}

		public static void uploadTexture(SimpleTexture texture, Supplier<Integer> genTextureId, final BufferedImage bufferedimage) throws IOException {
			texture.deleteGlTexture();

			final boolean blur = true;
			final boolean clamp = false;

			if (bufferedimage != null)
				TextureUtil.uploadTextureImageAllocate(genTextureId.get(), bufferedimage, blur, clamp);
		}
	}

	public static class CompatBufferBuilder {
		public CompatBufferBuilder() {
		}
	}

	public static class CompatVersionChecker {
		public static void startVersionCheck(final String modId, final String modVersion, final String updateURL) {
		}

		public static VersionChecker.CheckResult getResult(final String modId) {
			#if MC_7_LATER
			final ModContainer container = Loader.instance().getIndexedModList().get(modId);
			return ForgeVersion.getResult(container);
			#else
			return VersionChecker.getResult();
			#endif
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
