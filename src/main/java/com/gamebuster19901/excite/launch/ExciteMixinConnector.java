package com.gamebuster19901.excite.launch;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class ExciteMixinConnector implements IMixinConnector {

	@Override
	public void connect() {
		for(int i = 0; i < 1000; i++) {
			System.out.println("x");
		}
		Mixins.addConfiguration("mixins.json");
	}

}
