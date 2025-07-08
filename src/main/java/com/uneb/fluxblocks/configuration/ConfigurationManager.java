package com.uneb.fluxblocks.configuration;

import java.util.Map;

/**
 * Interface para gerenciadores de configuração do jogo.
 * Permite implementar diferentes fontes de configuração (arquivo, banco de dados, etc.).
 */
public interface ConfigurationManager {
    
    /**
     * Carrega a configuração da fonte especificada
     */
    void loadConfiguration();
    
    /**
     * Salva a configuração atual
     */
    void saveConfiguration();
    
    /**
     * Obtém um valor de configuração como string
     * @param key Chave da configuração
     * @return Valor da configuração
     */
    String getString(String key);
    
    /**
     * Obtém um valor de configuração como string com valor padrão
     * @param key Chave da configuração
     * @param defaultValue Valor padrão
     * @return Valor da configuração ou o padrão
     */
    String getString(String key, String defaultValue);
    
    /**
     * Obtém um valor de configuração como inteiro
     * @param key Chave da configuração
     * @return Valor da configuração
     */
    int getInt(String key);
    
    /**
     * Obtém um valor de configuração como inteiro com valor padrão
     * @param key Chave da configuração
     * @param defaultValue Valor padrão
     * @return Valor da configuração ou o padrão
     */
    int getInt(String key, int defaultValue);
    
    /**
     * Obtém um valor de configuração como double
     * @param key Chave da configuração
     * @return Valor da configuração
     */
    double getDouble(String key);
    
    /**
     * Obtém um valor de configuração como double com valor padrão
     * @param key Chave da configuração
     * @param defaultValue Valor padrão
     * @return Valor da configuração ou o padrão
     */
    double getDouble(String key, double defaultValue);
    
    /**
     * Obtém um valor de configuração como boolean
     * @param key Chave da configuração
     * @return Valor da configuração
     */
    boolean getBoolean(String key);
    
    /**
     * Obtém um valor de configuração como boolean com valor padrão
     * @param key Chave da configuração
     * @param defaultValue Valor padrão
     * @return Valor da configuração ou o padrão
     */
    boolean getBoolean(String key, boolean defaultValue);
    
    /**
     * Define um valor de configuração
     * @param key Chave da configuração
     * @param value Valor da configuração
     */
    void setValue(String key, Object value);
    
    /**
     * Remove uma configuração
     * @param key Chave da configuração
     */
    void removeValue(String key);
    
    /**
     * Verifica se uma configuração existe
     * @param key Chave da configuração
     * @return true se a configuração existir
     */
    boolean hasValue(String key);
    
    /**
     * Obtém todas as configurações como um mapa
     * @return Mapa com todas as configurações
     */
    Map<String, Object> getAllValues();
    
    /**
     * Reseta a configuração para os valores padrão
     */
    void resetToDefaults();
    
    /**
     * Retorna o nome do gerenciador
     * @return Nome do gerenciador
     */
    String getName();
    
    /**
     * Verifica se o gerenciador está ativo
     * @return true se o gerenciador estiver ativo
     */
    boolean isActive();
    
    /**
     * Ativa/desativa o gerenciador
     * @param active true para ativar, false para desativar
     */
    void setActive(boolean active);
} 