package net.teamfruit.emojicord;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Stopwatch;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class Emoji {
	public static final ResourceLocation loading_texture = new ResourceLocation("emojicord", "textures/26a0.png");
	public static final ResourceLocation noSignal_texture = new ResourceLocation("emojicord", "textures/26d4.png");
	public static final ResourceLocation error_texture = new ResourceLocation("emojicord", "textures/26d4.png");

	public static final long EMOJI_LIFETIME_SEC = 60;

	private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
	private final EmojiId id;
	private boolean deleteOldTexture;
	private SimpleTexture img;
	private ResourceLocation resourceLocation;
	private final Stopwatch lifeTime;

	public Emoji(final EmojiId id) {
		this.resourceLocation = loading_texture;
		this.id = id;
		this.lifeTime = Stopwatch.createStarted();
	}

	private void checkLoad() {
		if (this.img == null) {
			this.img = new DownloadImageData(this.id.getCache(), this.id.getRemote(), loading_texture);
			this.resourceLocation = this.id.getResourceLocation();
			Minecraft.getMinecraft().renderEngine.loadTexture(this.resourceLocation, this.img);
		}
	}

	public ResourceLocation getResourceLocationForBinding() {
		this.lifeTime.reset();
		checkLoad();
		if (this.deleteOldTexture) {
			this.img.deleteGlTexture();
			this.deleteOldTexture = false;
		}
		return this.resourceLocation;
	}

	public boolean isExpired() {
		return this.lifeTime.elapsed(TimeUnit.SECONDS) > EMOJI_LIFETIME_SEC;
	}

	public class DownloadImageData extends SimpleTexture {
		private final File cacheFile;
		private final String imageUrl;
		private BufferedImage bufferedImage;
		private Thread imageThread;
		private boolean textureUploaded;

		public DownloadImageData(final File cacheFileIn, final String imageUrlIn,
				final ResourceLocation textureResourceLocation) {
			super(textureResourceLocation);
			this.cacheFile = cacheFileIn;
			this.imageUrl = imageUrlIn;
		}

		private void checkTextureUploaded() {
			if ((!this.textureUploaded) && (this.bufferedImage != null)) {
				if (this.textureLocation != null)
					deleteGlTexture();

				TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
				this.textureUploaded = true;
			}
		}

		@Override
		public int getGlTextureId() {
			checkTextureUploaded();
			return super.getGlTextureId();
		}

		public void setBufferedImage(final BufferedImage bufferedImageIn) {
			this.bufferedImage = bufferedImageIn;
		}

		@Override
		public void loadTexture(final IResourceManager resourceManager) throws IOException {
			if ((this.bufferedImage == null) && (this.textureLocation != null))
				super.loadTexture(resourceManager);
			if (this.imageThread == null)
				if ((this.cacheFile != null) && (this.cacheFile.isFile()))
					try {
						this.bufferedImage = ImageIO.read(this.cacheFile);
					} catch (final IOException ioexception) {
						loadTextureFromServer();
					}
				else
					loadTextureFromServer();
		}

		protected void loadTextureFromServer() {
			this.imageThread = new Thread(
					"Emojicord Texture Downloader #" + Emoji.threadDownloadCounter.incrementAndGet()) {
				@Override
				public void run() {
					HttpURLConnection httpurlconnection = null;
					try {
						httpurlconnection = (HttpURLConnection) new URL(DownloadImageData.this.imageUrl)
								.openConnection(Minecraft.getMinecraft().getProxy());
						httpurlconnection.setDoInput(true);
						httpurlconnection.setDoOutput(false);
						httpurlconnection.setRequestProperty("User-Agent",
								"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36");
						httpurlconnection.setRequestProperty("Accept", "*/*");
						httpurlconnection.setRequestProperty("Accept-Encoding", "");
						httpurlconnection.setRequestProperty("Accept-Language", "ja,en-US;q=0.9,en;q=0.8");
						httpurlconnection.connect();

						if (httpurlconnection.getResponseCode() == 200) {
							//final int contentLength = httpurlconnection.getContentLength();
							BufferedImage bufferedimage;
							if (DownloadImageData.this.cacheFile != null) {
								FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(),
										DownloadImageData.this.cacheFile);
								bufferedimage = ImageIO.read(DownloadImageData.this.cacheFile);
							} else
								bufferedimage = TextureUtil.readBufferedImage(httpurlconnection.getInputStream());

							setBufferedImage(bufferedimage);
						} else {
							Emoji.this.resourceLocation = Emoji.noSignal_texture;
							Emoji.this.deleteOldTexture = true;
						}
					} catch (final Exception exception) {
						Emoji.this.resourceLocation = Emoji.error_texture;
						Emoji.this.deleteOldTexture = true;
					} finally {
						if (httpurlconnection != null)
							httpurlconnection.disconnect();
					}
				}
			};
			this.imageThread.setDaemon(true);
			this.imageThread.start();
		}
	}
}