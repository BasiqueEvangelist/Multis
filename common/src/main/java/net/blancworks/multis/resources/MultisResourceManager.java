package net.blancworks.multis.resources;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The collection of all resources for Multis.
 */
public class MultisResourceManager {
    private static final Map<Identifier, MultisResource> resources = new HashMap<>();

    private static final Map<Identifier, List<Consumer<MultisResource>>> changeListeners = new HashMap<>();

    public static synchronized MultisResource setResource(Identifier id, MultisResource resource) {
        MultisResource ret = resources.put(id, resource);
        notifyListeners(id, resource);
        return ret;
    }

    public static synchronized MultisResource removeResource(Identifier id) {
        MultisResource ret = resources.remove(id);
        notifyListeners(id, null);
        return ret;
    }

    public static synchronized MultisResource getResource(Identifier id) {
        return resources.get(id);
    }


    public static synchronized void addListener(Identifier id, Consumer<MultisResource> listener) {
        List<Consumer<MultisResource>> listenerList = changeListeners.computeIfAbsent(id, k -> new ArrayList<>());

        listenerList.add(listener);
    }

    public static synchronized boolean removeListener(Identifier id, Consumer<MultisResource> listener) {
        List<Consumer<MultisResource>> listenerList = changeListeners.computeIfAbsent(id, k -> new ArrayList<>());

        return listenerList.remove(listener);
    }

    private static synchronized void notifyListeners(Identifier id, MultisResource newResource) {
        List<Consumer<MultisResource>> listenerList = changeListeners.computeIfAbsent(id, k -> new ArrayList<>());

        for (Consumer<MultisResource> consumer : listenerList) {
            consumer.accept(newResource);
        }
    }

}
