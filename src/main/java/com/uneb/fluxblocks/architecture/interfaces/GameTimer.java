package com.uneb.fluxblocks.architecture.interfaces;

/**
 * Interface para abstrair o sistema de timer/animação do jogo.
 * Permite diferentes implementações de timer (padrão, suave, retro, etc.).
 */
public interface GameTimer {
    
    /**
     * Inicia o timer.
     */
    void start();
    
    /**
     * Para o timer completamente.
     */
    void stop();
    
    /**
     * Pausa o timer (mantém o tempo mas não executa o loop).
     */
    void pause();
    
    /**
     * Resume o timer após pausa.
     */
    void resume();
    
    /**
     * Define a velocidade do timer.
     * @param speed Multiplicador de velocidade (1.0 = normal, 2.0 = 2x mais rápido)
     */
    void setSpeed(double speed);
    
    /**
     * Verifica se o timer está rodando.
     * @return true se está rodando, false caso contrário
     */
    boolean isRunning();
    
    /**
     * Verifica se o timer está pausado.
     * @return true se está pausado, false caso contrário
     */
    boolean isPaused();
    
    /**
     * Retorna o tempo decorrido em milissegundos.
     * @return Tempo decorrido em ms
     */
    long getElapsedTime();
    
    /**
     * Retorna o tempo atual do jogo em milissegundos.
     * @return Tempo atual do jogo em ms
     */
    long getGameTime();
    
    /**
     * Define o tempo atual do jogo.
     * @param timeMs Tempo em milissegundos
     */
    void setGameTime(long timeMs);
    
    /**
     * Reseta o timer para o estado inicial.
     */
    void reset();
    
    /**
     * Limpa recursos do timer.
     */
    void cleanup();
} 