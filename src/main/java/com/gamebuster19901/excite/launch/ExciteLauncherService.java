package com.gamebuster19901.excite.launch;

import java.io.File;
import java.io.IOError;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import cpw.mods.gross.Java9ClassLoaderUtil;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;

public class ExciteLauncherService implements ILaunchHandlerService {

	public ExciteLauncherService() {
		System.out.println("ExciteLauncherService instantiated");
	}
	
	@Override
	public String name() {
		return "excitebot";
	}

	@Override
	public void configureTransformationClassLoader(ITransformingClassLoaderBuilder builder) {
		for (final URL url : Java9ClassLoaderUtil.getSystemClassPathURLs()) {
			try {
				if(new File(url.toURI()).isDirectory()) {
					continue;
				}
				builder.addTransformationPath(Path.of(url.toURI()));
			} catch (Throwable t) {
				throw new IOError(new Error("Could not start ExciteLauncher service!", t));
			}
		}
	}

	@Override
	public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader) {
		return () -> {
			Class<?> mainClass = launchClassLoader.getInstance().loadClass("com.gamebuster19901.excite.Main");
			System.out.println("Obtained Main.class with " + launchClassLoader.getInstance());
			final Method mainMethod = mainClass.getMethod("main", String[].class);
			System.out.println("Launching main with classloader " + mainClass.getClassLoader());
			mainMethod.invoke(null, new Object[] {arguments});
			return null;
		};
	}
	
}
