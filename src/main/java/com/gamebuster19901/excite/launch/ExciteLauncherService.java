package com.gamebuster19901.excite.launch;

import java.io.File;
import java.io.IOError;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.ArrayUtils;

import cpw.mods.gross.Java9ClassLoaderUtil;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;

public class ExciteLauncherService implements ILaunchHandlerService {

	public ExciteLauncherService() {

	}
	
	@Override
	public String name() {
		return "excitebot";
	}

	@Override
	public void configureTransformationClassLoader(ITransformingClassLoaderBuilder builder) {
		grossClassTransformerHackery(); //gross classtransformer hack, THIS COULD BREAK IN FUTURE MODLAUNCHER RELEASES
		for (final URL url : Java9ClassLoaderUtil.getSystemClassPathURLs()) {
			try {
				System.out.println(url);
				builder.addTransformationPath(new File(url.toURI()).toPath());
			} catch (Throwable t) {
				throw new IOError(new Error("Could not start ExciteLauncher service!", t));
			}
		}
	}

	@Override
	public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader) {
		return () -> {
			Class<?> mainClass = launchClassLoader.getInstance().loadClass("com.gamebuster19901.excite.Main");
			final Method mainMethod = mainClass.getMethod("main", String[].class);
			mainMethod.invoke(null, new Object[] {arguments});
			return null;
		};
	}
	
	@SuppressWarnings("unchecked")
	private void grossClassTransformerHackery() {
		try {
			Field skipPrefixField = TransformingClassLoader.class.getDeclaredField("SKIP_PACKAGE_PREFIXES");
			skipPrefixField.setAccessible(true);
			List<String> disallowedPrefixList = (List<String>) skipPrefixField.get(null);
			Field listArray = disallowedPrefixList.getClass().getDeclaredField("a");
			listArray.setAccessible(true);
			String[] disallowedPrefixes = (String[]) listArray.get(disallowedPrefixList);
			disallowedPrefixes = ArrayUtils.remove(disallowedPrefixes, 1);
			listArray.set(disallowedPrefixList, disallowedPrefixes);
			System.out.println(Arrays.toString(disallowedPrefixList.toArray()));
			if(disallowedPrefixList.contains(".javax")) {
				throw new LinkageError("Modlauncher probably changed SKIP_PACKAGE_PREFIXES", new IllegalStateException("Removal of \".javax\" from disallowed packages failed."));
			}
		}
		catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}
	
}
