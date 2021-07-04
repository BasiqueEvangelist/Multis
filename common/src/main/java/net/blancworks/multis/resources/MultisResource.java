package net.blancworks.multis.resources;

import net.minecraft.util.Identifier;

/**
 * Multis Objects are generic object assets for Multis that are designed to be swapped out at runtime.
 * Examples of Multis Objects are:
 * Packs
 * Scripts
 * Textures
 * Models
 */
public abstract class MultisResource {
    public Identifier id;

    public abstract void onLoad();
    public abstract void onUnload();
}
