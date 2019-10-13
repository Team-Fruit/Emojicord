package net.teamfruit.emojicord;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BasicHeader;

import net.teamfruit.emojicord.emoji.DiscordEmojiIdDictionary;
import net.teamfruit.emojicord.emoji.Models.EmojiDiscordList;
import net.teamfruit.emojicord.util.DataUtils;
import net.teamfruit.emojicord.util.Downloader;

public class EmojicordWeb {
	public static final @Nonnull EmojicordWeb instance = new EmojicordWeb();

	private File tokenDir;
	private int port = 0;
	private CallbackServerInstance server;
	private final String key = UUID.randomUUID().toString();
	private final AtomicBoolean callbacked = new AtomicBoolean();

	public static class EmojicordWebTokenModel {
		public String token;
	}

	public void init(final File emojicordDir) {
		this.tokenDir = new File(emojicordDir, "token.json");
	}

	public boolean setToken(final String token) {
		if (this.tokenDir!=null) {
			final EmojicordWebTokenModel model = new EmojicordWebTokenModel();
			model.token = token;
			return DataUtils.saveFile(this.tokenDir, EmojicordWebTokenModel.class, model, "Emojicord Web Token");
		}
		return false;
	}

	public String getToken() {
		final EmojicordWebTokenModel model = DataUtils.loadFileIfExists(this.tokenDir, EmojicordWebTokenModel.class, "Emojicord Web Token");
		if (model!=null)
			return model.token;
		return null;
	}

	public boolean open() {
		if (this.server==null)
			if (this.port!=0)
				try {
					this.server = new CallbackServerInstance(this::callback, this.port);
					this.port = this.server.getPort();
				} catch (final IOException e) {
					Log.log.error("Could not open the callback server with port"+this.port, e);
				}
		if (this.server==null)
			try {
				this.server = new CallbackServerInstance(this::callback);
				this.port = this.server.getPort();
			} catch (final IOException e) {
				Log.log.error("Could not open the callback server with port"+this.port, e);
			}
		if (this.server!=null) {
			pollCallbacked();
			OSUtils.getOSType().openURI(String.format("https://emojicord.teamfruit.net/connect/?key=%s&port=%s", this.key, this.port));
			return true;
		} else {
			Log.log.warn("Failed to Initialize Web");
			return false;
		}
	}

	public void close() {
		if (this.server!=null)
			this.server.close();
		this.server = null;
	}

	private boolean callback(final CallbackServerInstance.WebCallbackModel model) {
		if (!StringUtils.equals(this.key, model.key))
			return false;

		setToken(model.token);
		this.callbacked.set(true);
		return true;
	}

	public boolean pollCallbacked() {
		return this.callbacked.getAndSet(false);
	}

	/*
	public boolean checkToken() {
		final String token = getToken();
		if (StringUtils.isEmpty(token))
			return false;
	
		try {
			final HttpUriRequest req = new HttpHead("https://emojicord.teamfruit.net/api/minecraft/emojis/");
			req.addHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, "Authorization: Bearer "+token));
			final HttpClientContext context = HttpClientContext.create();
			final HttpResponse response = Downloader.downloader.client.execute(req, context);
	
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode!=HttpStatus.SC_OK)
				return false;
	
			return true;
		} catch (IllegalStateException|IOException e) {
			Log.log.error("Failed to check: ", e);
		}
		return false;
	}
	*/

	public boolean download() {
		final String token = getToken();
		if (StringUtils.isEmpty(token))
			return false;

		try {
			final HttpUriRequest req = new HttpGet("https://emojicord.teamfruit.net/api/minecraft/emojis/");
			req.addHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, "Authorization: Bearer "+token));
			final HttpClientContext context = HttpClientContext.create();
			final HttpResponse response = Downloader.downloader.client.execute(req, context);

			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode!=HttpStatus.SC_OK) {
				Log.log.error("Failed to download: Invalid status code: "+statusCode);
				return false;
			}

			final File dictDir = DiscordEmojiIdDictionary.instance.getDictionaryDirectory();
			if (dictDir==null) {
				Log.log.error("Emoji Directory not Initialized");
				return false;
			}

			final HttpEntity entity = response.getEntity();
			EmojiDiscordList list;
			list = DataUtils.loadStream(entity.getContent(), EmojiDiscordList.class, "Emoji Data Download");
			if (list==null||StringUtils.isEmpty(list.id)) {
				Log.log.error("Failed to download: Invalid Emoji Data");
				return false;
			}

			final File file = new File(dictDir, String.format("%s.json", list.id));
			return DataUtils.saveFile(file, EmojiDiscordList.class, list, "Emoji Data Save");
		} catch (IllegalStateException|IOException e) {
			Log.log.error("Failed to download: ", e);
		}

		return true;
	}
}
