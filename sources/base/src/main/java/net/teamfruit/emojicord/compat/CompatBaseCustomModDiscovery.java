package net.teamfruit.emojicord.compat;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public interface CompatBaseCustomModDiscovery {
	void registerModNameList(List<String> modList);

	void registerModList(List<File> modList);

	List<Path> getModFiles();

	void discoverMods();
}
