package net.blancworks.multis.resources;

import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A set of resources for Multis.
 *
 * @param <T> The type of resource to store in this set.
 */
public class MultisResourceSet<T> {
    private final Map<Identifier, MultisResource<T>> resources = new HashMap<>();

    private final Map<Identifier, List<Consumer<MultisResource<T>>>> changeListeners = new HashMap<>();


    public synchronized MultisResource<T> setResource(Identifier id, MultisResource<T> resource) {
        MultisResource<T> ret = resources.put(id, resource);
        notifyListeners(id, resource);
        return ret;
    }

    public synchronized MultisResource<T> removeResource(Identifier id) {
        MultisResource<T> ret = resources.remove(id);
        notifyListeners(id, null);
        return ret;
    }

    public synchronized MultisResource<T> getResource(Identifier id) {
        return resources.get(id);
    }


    public synchronized void addListener(Identifier id, Consumer<MultisResource<T>> listener) {
        List<Consumer<MultisResource<T>>> listenerList = changeListeners.computeIfAbsent(id, k -> new ArrayList<>());

        listenerList.add(listener);
    }

    public synchronized boolean removeListener(Identifier id, Consumer<MultisResource<T>> listener) {
        List<Consumer<MultisResource<T>>> listenerList = changeListeners.computeIfAbsent(id, k -> new ArrayList<>());

        return listenerList.remove(listener);
    }

    private synchronized void notifyListeners(Identifier id, MultisResource<T> newResource) {
        List<Consumer<MultisResource<T>>> listenerList = changeListeners.computeIfAbsent(id, k -> new ArrayList<>());

        for (Consumer<MultisResource<T>> consumer : listenerList) {
            consumer.accept(newResource);
        }
    }

    public synchronized void forEach(BiConsumer<Identifier, MultisResource<T>> consumer){
        resources.forEach(consumer);
    }
}
