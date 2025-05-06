package com.uneb.tetris.core;

import com.uneb.tetris.event.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMediator {
    private final Map<EventType<?>, List<PrioritizedListener<?>>> listeners = new HashMap<>();

    public <T> void receiver(EventType<T> type, Listener<T> listener) {
        receiver(type, listener, 0);
    }

    public <T> void receiver(EventType<T> type, Listener<T> listener, int priority) {
        List<PrioritizedListener<?>> list = listeners.computeIfAbsent(type, k -> new ArrayList<>());
        list.add(new PrioritizedListener<>(listener, priority));
        list.sort((a, b) -> Integer.compare(((PrioritizedListener<?>) b).priority, ((PrioritizedListener<?>) a).priority));
    }

    public <T> void emit(EventType<T> type, T payload) {
        List<PrioritizedListener<?>> list = listeners.get(type);
        if (list != null) {
            for (PrioritizedListener<?> pl : list) {
                @SuppressWarnings("unchecked")
                Listener<T> typedListener = (Listener<T>) pl.listener;
                typedListener.onEvent(payload);
            }
        }
    }

    public interface Listener<T> {
        void onEvent(T payload);
    }

    private record PrioritizedListener<T>(Listener<T> listener, int priority) {
    }
}

