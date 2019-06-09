package net.teamfruit.emojicord;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class Emoji implements Predicate<String> {
	public static final ResourceLocation loading_texture = new ResourceLocation("emojicord", "textures/26a0.png");
	public static final ResourceLocation noSignal_texture = new ResourceLocation("emojicord", "textures/26d4.png");
	public static final ResourceLocation error_texture = new ResourceLocation("emojicord", "textures/26d4.png");
	private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
	public String name;
	public List<String> strings;
	public String location;
	public int version = 1;
	public boolean deleteOldTexture;
	public SimpleTexture img;
	public ResourceLocation resourceLocation;

	public Emoji() {
		this.resourceLocation = loading_texture;
	}

	public void checkLoad() {
		if (this.img == null) {
			this.img = new DownloadImageData(new File("emojicord/cache/" + this.name),
					"https://cdn.discordapp.com/emojis/" + this.name, loading_texture);
			this.resourceLocation = new ResourceLocation("emojicord", "textures/emoji/" + this.name);
			Minecraft.getMinecraft().renderEngine.loadTexture(this.resourceLocation, this.img);
		}
	}

	public ResourceLocation getResourceLocationForBinding() {
		checkLoad();
		if (this.deleteOldTexture) {
			this.img.deleteGlTexture();
			this.deleteOldTexture = false;
		}
		return this.resourceLocation;
	}

	@Override
	public boolean test(final String s) {
		for (final String text : this.strings)
			if (s.equalsIgnoreCase(text))
				return true;
		return false;
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
			if ((!this.textureUploaded) &&
					(this.bufferedImage != null)) {
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

						if (httpurlconnection.getResponseCode() / 100 == 2) {
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