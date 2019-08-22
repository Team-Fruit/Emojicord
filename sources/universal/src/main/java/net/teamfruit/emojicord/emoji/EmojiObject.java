package net.teamfruit.emojicord.emoji;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BasicHeader;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;

import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.teamfruit.emojicord.compat.Compat;
import net.teamfruit.emojicord.util.Downloader;
import net.teamfruit.emojicord.util.ThreadUtils;

public class EmojiObject {
	public static final ResourceLocation loading_texture = new ResourceLocation("emojicord", "textures/26a0.png");
	public static final ResourceLocation noSignal_texture = new ResourceLocation("emojicord", "textures/26d4.png");
	public static final ResourceLocation error_texture = new ResourceLocation("emojicord", "textures/26d4.png");

	private static final @Nonnull ExecutorService threadpool = ThreadUtils
			.newFixedCachedThreadPool(16, "emojicord-http-%d");

	private final EmojiId id;
	private boolean deleteOldTexture;
	private SimpleTexture img;
	private ResourceLocation resourceLocation;

	public EmojiObject(final EmojiId id) {
		this.resourceLocation = loading_texture;
		this.id = id;
	}

	private void checkLoad() {
		if (this.img==null) {
			this.img = new DownloadImageData(this.id.getCache(), this.id.getRemote(), loading_texture);
			this.resourceLocation = this.id.getResourceLocation();
			Compat.CompatMinecraft.getMinecraft().renderEngine.loadTexture(this.resourceLocation, this.img);
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

	public void delete() {
		if (this.img!=null) {
			this.img.deleteGlTexture();
			this.deleteOldTexture = false;
		}
	}

	public class DownloadImageData extends SimpleTexture {
		private final File cacheFile;
		private final String imageUrl;
		private BufferedImage bufferedImage;
		private boolean downloading;
		private boolean textureUploaded;

		public DownloadImageData(
				final File cacheFileIn, final String imageUrlIn,
				final ResourceLocation textureResourceLocation
		) {
			super(textureResourceLocation);
			this.cacheFile = cacheFileIn;
			this.imageUrl = imageUrlIn;
		}

		private void checkTextureUploaded() {
			if (!this.textureUploaded&&this.bufferedImage!=null) {
				if (this.textureLocation!=null)
					deleteGlTexture();

				//final DynamicImageTexture texture = DynamicImageTexture.createSized(this.bufferedImage);
				//texture.load();
				//this.glTextureId = texture.getId();
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
			if (this.bufferedImage==null&&this.textureLocation!=null)
				super.loadTexture(resourceManager);
			if (!this.downloading)
				if (this.cacheFile!=null&&this.cacheFile.isFile())
					try {
						this.bufferedImage = TextureUtil.readBufferedImage(FileUtils.openInputStream(this.cacheFile));
					} catch (final IOException ioexception) {
						loadTextureFromServer();
					}
				else
					loadTextureFromServer();
		}

		protected void loadTextureFromServer() {
			this.downloading = true;
			threadpool.execute(() -> {
				CloseableHttpResponse response = null;
				try {
					final HttpUriRequest req = new HttpGet(DownloadImageData.this.imageUrl);
					req.setHeaders(new Header[] {
							new BasicHeader("User-Agent",
									"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36"),
							new BasicHeader("Accept", "*/*"),
							new BasicHeader("Accept-Encoding", ""),
							new BasicHeader("Accept-Language", "ja,en-US;q=0.9,en;q=0.8"),
					});
					final HttpClientContext context = HttpClientContext.create();
					response = Downloader.downloader.client.execute(req, context);
					final HttpEntity entity = response.getEntity();

					final int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode==HttpStatus.SC_OK) {
						BufferedImage bufferedimage;
						if (DownloadImageData.this.cacheFile!=null) {
							FileUtils.copyInputStreamToFile(entity.getContent(),
									DownloadImageData.this.cacheFile);
							bufferedimage = TextureUtil
									.readBufferedImage(FileUtils.openInputStream(DownloadImageData.this.cacheFile));
						} else
							bufferedimage = TextureUtil.readBufferedImage(entity.getContent());

						setBufferedImage(bufferedimage);
					} else {
						EmojiObject.this.resourceLocation = EmojiObject.noSignal_texture;
						EmojiObject.this.deleteOldTexture = true;
					}
				} catch (final Exception exception) {
					EmojiObject.this.resourceLocation = EmojiObject.error_texture;
					EmojiObject.this.deleteOldTexture = true;
				} finally {
					IOUtils.closeQuietly(response);
				}
			});
		}
	}

	public static class EmojiObjectCache {
		public static final long EMOJI_LIFETIME_SEC = 60;

		public static final EmojiObjectCache instance = new EmojiObjectCache();

		private EmojiObjectCache() {
		}

		private final LoadingCache<EmojiId, EmojiObject> EMOJI_ID_MAP = CacheBuilder.newBuilder()
				.expireAfterAccess(EMOJI_LIFETIME_SEC, TimeUnit.SECONDS)
				.removalListener(
						(final RemovalNotification<EmojiId, EmojiObject> notification) -> notification.getValue().delete())
				.build(new CacheLoader<EmojiId, EmojiObject>() {
					@Override
					public EmojiObject load(final EmojiId key) throws Exception {
						return new EmojiObject(key);
					}
				});

		public @Nonnull EmojiObject getEmojiObject(final @Nonnull EmojiId name) {
			return this.EMOJI_ID_MAP.getUnchecked(name);
		}
	}
}