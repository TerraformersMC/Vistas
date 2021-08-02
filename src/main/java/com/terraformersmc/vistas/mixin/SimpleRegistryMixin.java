package com.terraformersmc.vistas.mixin;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.google.common.collect.BiMap;
import com.mojang.serialization.Lifecycle;
import com.terraformersmc.vistas.access.SimpleRegistryAccess;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

@Mixin(SimpleRegistry.class)
@Unique
public abstract class SimpleRegistryMixin<T> implements SimpleRegistryAccess {

	@Shadow
	@Final
	@Mutable
	private ObjectList<T> rawIdToEntry;

	@Shadow
	@Final
	@Mutable
	private Object2IntMap<T> entryToRawId;

	@Shadow
	@Final
	@Mutable
	private BiMap<Identifier, T> idToEntry;

	@Shadow
	@Final
	@Mutable
	private BiMap<RegistryKey<T>, T> keyToEntry;

	@Shadow
	@Final
	@Mutable
	private Map<T, Lifecycle> entryToLifecycle;

	@Shadow
	protected Object[] randomEntries;

	@Shadow
	private int nextId;

	@Override
	public void clearEntries() {
		this.rawIdToEntry.clear();
		this.entryToRawId.clear();
		this.idToEntry.clear();
		this.keyToEntry.clear();
		this.entryToLifecycle.clear();
		this.randomEntries = null;
		this.nextId = 0;
	}

	@Override
	public void clearEntries(Identifier except) {
		T entryExcept = this.get(except);
		Lifecycle lifecycleExcept = this.getEntryLifecycle(entryExcept);
		RegistryKey<T> keyExcept = this.getKey(entryExcept).get();
		this.clearEntries();
		this.add(keyExcept, entryExcept, lifecycleExcept);
	}

	@Nullable
	@Shadow
	public abstract T get(@Nullable Identifier id);

	@Shadow
	public abstract Lifecycle getEntryLifecycle(T entry);

	@Shadow
	public abstract Optional<RegistryKey<T>> getKey(T entry);

	@Shadow
	public abstract <V extends T> V add(RegistryKey<T> key, V entry, Lifecycle lifecycle);

}
