package com.uneb.fluxblocks.architecture.mediators;

import com.uneb.fluxblocks.architecture.events.EventType;
import com.uneb.fluxblocks.ui.screens.GameBoardScreen;
import com.uneb.fluxblocks.user.UserManager;
import com.uneb.fluxblocks.game.ranking.RankingManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mediador central do jogo responsável por gerenciar a comunicação entre componentes.
 * Implementa um sistema de eventos com suporte a prioridades e tipos seguros.
 * <p>
 * Esta classe utiliza um padrão de Observer thread-safe com as seguintes características:
 * <ul>
 *     <li>Eventos tipados para garantir segurança em tempo de compilação</li>
 *     <li>Sistema de prioridades para controlar a ordem de execução dos listeners</li>
 *     <li>Thread-safe usando {@link ConcurrentHashMap} e coleções sincronizadas</li>
 * </ul>
 */
public class GameMediator {
    private final Map<EventType<?>, NavigableSet<PrioritizedListener<?>>> listeners = new ConcurrentHashMap<>();
    private GameBoardScreen gameBoardScreen;
    
    private UserManager userManager;
    private RankingManager rankingManager;

    /**
     * Registra um listener para um tipo específico de evento com prioridade padrão (0).
     *
     * @param type O tipo do evento a ser observado
     * @param listener O callback a ser executado quando o evento ocorrer
     * @param <T> O tipo de dado do evento
     */
    public <T> void receiver(EventType<T> type, Listener<T> listener) {
        receiver(type, listener, 0);
    }

    /**
     * Registra um listener para um tipo específico de evento com uma prioridade definida.
     * Prioridades maiores são executadas primeiro.
     *
     * @param type O tipo do evento a ser observado
     * @param listener O callback a ser executado quando o evento ocorrer
     * @param priority A prioridade do listener (maior = mais prioritário)
     * @param <T> O tipo de dado do evento
     */
    public <T> void receiver(EventType<T> type, Listener<T> listener, int priority) {
        listeners.computeIfAbsent(type, k -> new TreeSet<>())
                .add(new PrioritizedListener<>(listener, priority));
    }

    /**
     * Emite um evento para todos os listeners registrados do tipo especificado.
     * Os listeners são executados em ordem de prioridade.
     *
     * @param type O tipo do evento a ser emitido
     * @param payload O dado associado ao evento
     * @param <T> O tipo de dado do evento
     */
    public <T> void emit(EventType<T> type, T payload) {
        NavigableSet<PrioritizedListener<?>> set = listeners.get(type);
        if (set == null || set.isEmpty()) return;

        for (PrioritizedListener<?> prioritizedListener : set) {
            @SuppressWarnings("unchecked")
            Listener<T> typedListener = (Listener<T>) prioritizedListener.listener;
            typedListener.onEvent(payload);
        }
    }

    /**
     * Define o GameBoardScreen para ser acessado por outros componentes.
     *
     * @param gameBoardScreen A instância do GameBoardScreen
     */
    public void setGameBoardScreen(GameBoardScreen gameBoardScreen) {
        this.gameBoardScreen = gameBoardScreen;
    }

    /**
     * Retorna o GameBoardScreen associado ao mediador.
     *
     * @return A instância do GameBoardScreen
     */
    public GameBoardScreen getGameBoardScreen() {
        return gameBoardScreen;
    }
    
    /**
     * Define o UserManager principal do sistema.
     *
     * @param userManager A instância do UserManager
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Retorna o UserManager principal do sistema.
     *
     * @return A instância do UserManager ou null se não definido
     */
    public UserManager getUserManager() {
        return userManager;
    }
    
    /**
     * Define o RankingManager principal do sistema.
     *
     * @param rankingManager A instância do RankingManager
     */
    public void setRankingManager(RankingManager rankingManager) {
        this.rankingManager = rankingManager;
    }

    /**
     * Retorna o RankingManager principal do sistema.
     *
     * @return A instância do RankingManager ou null se não definido
     */
    public RankingManager getRankingManager() {
        return rankingManager;
    }


    /**
     * Interface funcional para definição de listeners de eventos.
     *
     * @param <T> O tipo de dado do evento
     */
    public interface Listener<T> {
        void onEvent(T payload);
    }

    /**
         * Wrapper interno para listeners que adiciona suporte a prioridades.
         * Implementa {@link Comparable} para ordenação baseada em prioridade.
         *
         * @param <T> O tipo de dado do evento
         */
        private record PrioritizedListener<T>(Listener<T> listener,
                                              int priority) implements Comparable<PrioritizedListener<?>> {

        @Override
            public int compareTo(PrioritizedListener<?> o) {
                int cmp = Integer.compare(o.priority, this.priority);
                if (cmp == 0) {
                    return Integer.compare(System.identityHashCode(this.listener), System.identityHashCode(o.listener));
                }
                return cmp;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                PrioritizedListener<?> that = (PrioritizedListener<?>) o;
                return listener.equals(that.listener);
            }

            @Override
            public int hashCode() {
                return Objects.hash(listener);
            }
        }


    /**
     * Remove um listener específico de um tipo de evento.
     *
     * @param type     O tipo do evento do qual remover o listener
     * @param listener O listener a ser removido
     * @param <T>      O tipo de dado do evento
     */
    public <T> void removeReceiver(EventType<T> type, Listener<T> listener) {
        NavigableSet<PrioritizedListener<?>> set = listeners.get(type);
        if (set == null || set.isEmpty()) return;

        // Encontra e remove o listener baseado na instância
        boolean removed = set.removeIf(prioritizedListener ->
                prioritizedListener.listener().equals(listener)
        );

        // Remove o conjunto vazio se não há mais listeners
        if (set.isEmpty()) {
            listeners.remove(type);
        }

    }

    /**
     * Limpa todos os listeners registrados no mediator.
     * Útil para evitar interferência entre partidas diferentes.
     */
    public void clearAllListeners() {
        listeners.clear();
    }
}