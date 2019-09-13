package net.teamfruit.emojicord.asm;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.Reference;

public class EmojicordCorePlugin implements ITransformationService {
	@Override
	public void beginScanning(final IEnvironment environment) {
	}

	@Override
	public String name() {
		return getClass().getSimpleName();
	}

	@Override
	public void initialize(final IEnvironment environment) {
	}

	@Override
	public void onLoad(final IEnvironment env, final Set<String> otherServices) throws IncompatibleEnvironmentException {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<ITransformer> transformers() {
		try {
			return Lists.newArrayList((ITransformer) Class.forName(Reference.TRANSFORMER).newInstance());
		} catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
			Log.log.error("Failed to load transformer", e);
		}
		return Lists.newArrayList();
	}
}