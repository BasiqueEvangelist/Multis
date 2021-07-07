package net.blancworks.multis.resources;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A set of resources for Multis.
 *
 * @param <T> The type of resource to store in this set.
 */
public class MultisResourceSet {
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

    public static synchronized void forEach(BiConsumer<Identifier, MultisResource> consumer){
        resources.forEach(consumer);
    }
}
