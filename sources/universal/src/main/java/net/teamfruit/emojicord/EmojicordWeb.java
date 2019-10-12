package net.teamfruit.emojicord;

import java.io.File;

import javax.annotation.Nonnull;

import net.teamfruit.emojicord.util.DataUtils;

public class EmojicordWeb {
	public static final @Nonnull EmojicordWeb instance = new EmojicordWeb();

	private File tokenDir;

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
}
