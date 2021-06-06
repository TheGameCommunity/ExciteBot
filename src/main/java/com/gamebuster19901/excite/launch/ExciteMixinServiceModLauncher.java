package com.gamebuster19901.excite.launch;

import org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncher;
import org.spongepowered.asm.service.modlauncher.MixinServiceModLauncher;
import org.spongepowered.asm.util.Constants;

public class ExciteMixinServiceModLauncher extends MixinServiceModLauncher {

	@Override
	public ContainerHandleModLauncher getPrimaryContainer() {
		for(int i = 0; i < 1000; i++) {
			System.out.println("y");
		}
		return new ExciteHandleModLauncher(this.getName());
	}
	
	private static final class ExciteHandleModLauncher extends ContainerHandleModLauncher {

		public ExciteHandleModLauncher(String name) {
			super(name);
			
			this.setAttribute(Constants.ManifestAttributes.MIXINCONNECTOR, "com.gamebuster19901.excite.launch.ExciteMixinConnector");
		}
		
	}
	
}
