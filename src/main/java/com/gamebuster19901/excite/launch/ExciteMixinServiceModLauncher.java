package com.gamebuster19901.excite.launch;

import org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncher;
import org.spongepowered.asm.service.modlauncher.MixinServiceModLauncher;

public class ExciteMixinServiceModLauncher extends MixinServiceModLauncher {

	@Override
	public ContainerHandleModLauncher getPrimaryContainer() {
		return new ExciteHandleModLauncher(this.getName());
	}
	
	private static final class ExciteHandleModLauncher extends ContainerHandleModLauncher {

		public ExciteHandleModLauncher(String name) {
			super(name);
		}
		
	}
	
}
