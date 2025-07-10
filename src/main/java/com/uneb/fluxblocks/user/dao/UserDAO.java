package com.uneb.fluxblocks.user.dao;

import com.uneb.fluxblocks.user.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO (Data Access Object) para operações de usuários.
 * Permite diferentes implementações: SQLite local, banco online, etc.
 */
public interface UserDAO {
    
    /**
     * Adiciona um novo usuário
     * @param user Usuário a ser adicionado
     * @return ID do usuário criado
     */
    Long addUser(User user);
    
    /**
     * Busca um usuário por ID
     * @param id ID do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findById(Long id);
    
    /**
     * Busca um usuário por nome
     * @param name Nome do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByName(String name);
    
    /**
     * Busca todos os usuários ordenados por nome
     * @return Lista de usuários ordenados
     */
    List<User> findAllOrderedByName();
    
    /**
     * Busca todos os usuários ordenados por melhor pontuação
     * @return Lista de usuários ordenados por pontuação
     */
    List<User> findAllOrderedByBestScore();
    
    /**
     * Busca todos os usuários ordenados por data de criação
     * @return Lista de usuários ordenados por data
     */
    List<User> findAllOrderedByCreatedAt();
    
    /**
     * Busca todos os usuários ordenados por último jogo
     * @return Lista de usuários ordenados por último jogo
     */
    List<User> findAllOrderedByLastPlayed();
    
    /**
     * Busca usuários com melhor pontuação (top N)
     * @param limit Número máximo de usuários
     * @return Lista dos top N usuários
     */
    List<User> findTopUsers(int limit);
    
    /**
     * Atualiza um usuário existente
     * @param user Usuário atualizado
     * @return true se atualizado com sucesso
     */
    boolean updateUser(User user);
    
    /**
     * Remove um usuário por ID
     * @param id ID do usuário a ser removido
     * @return true se removido com sucesso
     */
    boolean deleteById(Long id);
    
    /**
     * Remove um usuário por nome
     * @param name Nome do usuário a ser removido
     * @return true se removido com sucesso
     */
    boolean deleteByName(String name);
    
    /**
     * Remove todos os usuários
     * @return Número de usuários removidos
     */
    int deleteAll();
    
    /**
     * Conta o número total de usuários
     * @return Total de usuários
     */
    long count();
    
    /**
     * Verifica se existe um usuário com o nome especificado
     * @param name Nome do usuário
     * @return true se o usuário existe
     */
    boolean existsByName(String name);
    
    /**
     * Verifica se a conexão está ativa
     * @return true se conectado
     */
    boolean isConnected();
    
    /**
     * Inicializa a conexão
     * @return true se inicializado com sucesso
     */
    boolean initialize();
    
    /**
     * Fecha a conexão
     */
    void close();
    
    /**
     * Obtém o nome da implementação
     * @return Nome da implementação
     */
    String getImplementationName();
} 