package com.terraformersmc.vistas.access;

import net.minecraft.util.Identifier;

public interface SimpleRegistryAccess {

	public void clearEntries();

	public void clearEntries(Identifier except);

	public static SimpleRegistryAccess get(Object obj) {
		return (SimpleRegistryAccess) obj;
	}

}
