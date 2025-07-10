package com.uneb.fluxblocks.user;

import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.ranking.RankingEntry;
import com.uneb.fluxblocks.game.ranking.RankingManager;
import com.uneb.fluxblocks.user.dao.UserDAO;
import com.uneb.fluxblocks.user.dao.SQLiteUserDAO;
import com.uneb.fluxblocks.architecture.events.UserEvents;
import com.uneb.fluxblocks.architecture.events.UserEventTypes;

import java.util.List;
import java.util.Optional;

/**
 * Gerenciador do sistema de usuários
 */
public class UserManager {
    
    private final GameMediator mediator;
    private final UserDAO userDAO;
    private final RankingManager rankingManager;
    private User currentUser;
    
    public UserManager(GameMediator mediator, RankingManager rankingManager) {
        this.mediator = mediator;
        this.rankingManager = rankingManager;
        this.userDAO = new SQLiteUserDAO();
        
        initialize();
        registerEvents();
    }
    
    private void initialize() {
        if (userDAO.initialize()) {
            System.out.println("Sistema de usuários inicializado com sucesso");
        } else {
            System.err.println("Falha ao inicializar sistema de usuários");
        }
    }
    
    private void registerEvents() {
        mediator.receiver(UserEventTypes.LOGIN_REQUEST, this::onLoginRequest);
        mediator.receiver(UserEventTypes.LOGOUT_REQUEST, this::onLogoutRequest);
        mediator.receiver(UserEventTypes.CREATE_USER_REQUEST, this::onCreateUserRequest);
    }
    
    /**
     * Handler para evento de login
     */
    private void onLoginRequest(UserEvents.LoginRequestEvent event) {
        String userName = event.username();
        System.out.println("Tentando login para: " + userName);
        
        Optional<User> userOpt = findUserByName(userName);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            setCurrentUser(user);
            System.out.println("Login realizado com sucesso: " + userName);
        } else {
            System.err.println("Usuário não encontrado: " + userName);
            mediator.emit(UserEventTypes.LOGIN_FAILED, 
                new UserEvents.LoginFailedEvent(userName, "Usuário não encontrado"));
        }
    }
    
    /**
     * Handler para evento de criação de usuário
     */
    private void onCreateUserRequest(UserEvents.CreateUserRequestEvent event) {
        String userName = event.username();
        System.out.println("Tentando criar usuário: " + userName);
        
        Long userId = createUser(userName);
        if (userId == null) {
            System.err.println("Falha ao criar usuário: " + userName);
        }
    }
    
    /**
     * Handler para evento de logout
     */
    private void onLogoutRequest(UserEvents.LogoutRequestEvent event) {
        System.out.println("Recebido evento de logout");
        logout();
    }
    
    /**
     * Cria um novo usuário
     * @param name Nome do usuário
     * @return ID do usuário criado ou null se falhar
     */
    public Long createUser(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Nome de usuário inválido");
            mediator.emit(UserEventTypes.CREATE_USER_FAILED, 
                new UserEvents.CreateUserFailedEvent(name, "Nome de usuário inválido"));
            return null;
        }
        
        name = name.trim();
        
        // Verifica se o usuário já existe
        if (userDAO.existsByName(name)) {
            System.err.println("Usuário com este nome já existe: " + name);
            mediator.emit(UserEventTypes.CREATE_USER_FAILED, 
                new UserEvents.CreateUserFailedEvent(name, "Usuário já existe"));
            return null;
        }
        
        User user = new User(name);
        Long id = userDAO.addUser(user);
        
        if (id != null) {
            System.out.println("Usuário criado com sucesso: " + name + " (ID: " + id + ")");
            mediator.emit(UserEventTypes.CREATE_USER_SUCCESS, 
                new UserEvents.CreateUserSuccessEvent(user));
            return id;
        } else {
            System.err.println("Falha ao criar usuário: " + name);
            mediator.emit(UserEventTypes.CREATE_USER_FAILED, 
                new UserEvents.CreateUserFailedEvent(name, "Erro interno"));
            return null;
        }
    }
    
    /**
     * Busca um usuário por nome
     * @param name Nome do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<User> findUserByName(String name) {
        return userDAO.findByName(name);
    }
    
    /**
     * Busca um usuário por ID
     * @param id ID do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<User> findUserById(Long id) {
        return userDAO.findById(id);
    }
    
    /**
     * Define o usuário atual
     * @param user Usuário a ser definido como atual
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("Usuário atual definido: " + user.getName());
        
        // Emite evento de login
        mediator.emit(UserEventTypes.LOGIN_SUCCESS, 
            new UserEvents.LoginSuccessEvent(user));
    }
    
    /**
     * Obtém o usuário atual
     * @return Usuário atual ou null se não houver
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Atualiza as estatísticas do usuário atual após um jogo
     * @param score Pontuação do jogo
     */
    public void updateUserStats(int score) {
        if (currentUser == null) {
            System.err.println("Nenhum usuário atual definido");
            return;
        }
        
        int oldBestScore = currentUser.getBestScore();
        currentUser.updateStats(score);
        
        if (userDAO.updateUser(currentUser)) {
            System.out.println("Estatísticas do usuário atualizadas: " + currentUser.getName());
            
            // Emite evento de estatísticas atualizadas
            mediator.emit(UserEventTypes.USER_STATS_UPDATED, 
                new UserEvents.UserStatsUpdatedEvent(currentUser));
            
            // Verifica se bateu recorde pessoal
            if (score > oldBestScore) {
                mediator.emit(UserEventTypes.NEW_PERSONAL_BEST, 
                    new UserEvents.NewPersonalBestEvent(currentUser, score));
            }
        } else {
            System.err.println("Falha ao atualizar estatísticas do usuário");
        }
    }
    
    /**
     * Salva o resultado de um jogo no ranking usando o usuário atual
     * @param score Pontuação do jogo
     * @param level Nível alcançado
     * @param linesCleared Linhas eliminadas
     * @param gameTimeMs Tempo de jogo em milissegundos
     */
    public void saveGameResult(int score, int level, int linesCleared, long gameTimeMs) {
        if (currentUser == null) {
            System.err.println("Nenhum usuário atual definido");
            return;
        }
        

        
        // Atualiza estatísticas do usuário
        updateUserStats(score);
        
        // Salva no ranking respeitando o limite de 10 entradas por usuário
        RankingEntry entry = new RankingEntry(
            currentUser.getName(),
            score,
            level,
            linesCleared,
            gameTimeMs,
            "Single Player"
        );
        

        
        Long rankingId = rankingManager.addRankingEntry(
            currentUser.getName(), 
            score, 
            level, 
            linesCleared,
            gameTimeMs
        );
        
        if (rankingId == null) {
            System.err.println("❌ Falha ao salvar resultado no ranking");
        }
    }
    
    /**
     * Lista todos os usuários ordenados por nome
     * @return Lista de usuários
     */
    public List<User> getAllUsers() {
        return userDAO.findAllOrderedByName();
    }
    
    /**
     * Lista usuários ordenados por melhor pontuação
     * @return Lista de usuários ordenados por pontuação
     */
    public List<User> getUsersByBestScore() {
        return userDAO.findAllOrderedByBestScore();
    }
    
    /**
     * Lista usuários ordenados por data de criação
     * @return Lista de usuários ordenados por data
     */
    public List<User> getUsersByCreatedAt() {
        return userDAO.findAllOrderedByCreatedAt();
    }
    
    /**
     * Lista usuários ordenados por último jogo
     * @return Lista de usuários ordenados por último jogo
     */
    public List<User> getUsersByLastPlayed() {
        return userDAO.findAllOrderedByLastPlayed();
    }
    
    /**
     * Obtém os top N usuários por pontuação
     * @param limit Número máximo de usuários
     * @return Lista dos top N usuários
     */
    public List<User> getTopUsers(int limit) {
        return userDAO.findTopUsers(limit);
    }
    
    /**
     * Remove um usuário por ID
     * @param id ID do usuário a ser removido
     * @return true se removido com sucesso
     */
    public boolean deleteUserById(Long id) {
        // Primeiro busca o usuário para obter o nome
        Optional<User> userOpt = userDAO.findById(id);
        if (userOpt.isEmpty()) {
            System.err.println("Usuário não encontrado com ID: " + id);
            return false;
        }
        
        User user = userOpt.get();
        String userName = user.getName();
        
        boolean deleted = userDAO.deleteById(id);
        if (deleted) {
            System.out.println("Usuário removido com ID: " + id);
            
            // Remove todas as pontuações do usuário
            if (rankingManager != null) {
                int removedEntries = rankingManager.deletePlayerEntries(userName);
                System.out.println("Removidas " + removedEntries + " pontuações do usuário: " + userName);
            }
            
            // Se o usuário removido era o atual, limpa a referência
            if (currentUser != null && currentUser.getId().equals(id)) {
                currentUser = null;
            }
        }
        return deleted;
    }
    
    /**
     * Remove um usuário por nome
     * @param name Nome do usuário a ser removido
     * @return true se removido com sucesso
     */
    public boolean deleteUserByName(String name) {
        boolean deleted = userDAO.deleteByName(name);
        if (deleted) {
            System.out.println("Usuário removido: " + name);
            
            // Remove todas as pontuações do usuário
            if (rankingManager != null) {
                int removedEntries = rankingManager.deletePlayerEntries(name);
                System.out.println("Removidas " + removedEntries + " pontuações do usuário: " + name);
            }
            
            // Se o usuário removido era o atual, limpa a referência
            if (currentUser != null && currentUser.getName().equals(name)) {
                currentUser = null;
            }
        }
        return deleted;
    }
    
    /**
     * Remove todos os usuários
     * @return Número de usuários removidos
     */
    public int deleteAllUsers() {
        int deleted = userDAO.deleteAll();
        if (deleted > 0) {
            System.out.println("Todos os usuários removidos: " + deleted);
            currentUser = null; // Limpa usuário atual
        }
        return deleted;
    }
    
    /**
     * Conta o número total de usuários
     * @return Total de usuários
     */
    public long getUserCount() {
        return userDAO.count();
    }
    
    /**
     * Verifica se existe um usuário com o nome especificado
     * @param name Nome do usuário
     * @return true se o usuário existe
     */
    public boolean userExists(String name) {
        return userDAO.existsByName(name);
    }
    
    /**
     * Verifica se há um usuário logado
     * @return true se há usuário atual
     */
    public boolean hasCurrentUser() {
        return currentUser != null;
    }
    
    /**
     * Desconecta o usuário atual
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("Usuário desconectado: " + currentUser.getName());
            User loggedOutUser = currentUser;
            currentUser = null;
            
            // Emite evento de logout
            mediator.emit(UserEventTypes.LOGOUT_SUCCESS, 
                new UserEvents.LogoutSuccessEvent());
        }
    }
    
    /**
     * Verifica se o sistema está conectado
     * @return true se conectado
     */
    public boolean isConnected() {
        return userDAO.isConnected();
    }
    
    /**
     * Fecha a conexão com o banco de dados
     */
    public void close() {
        userDAO.close();
        System.out.println("Sistema de usuários fechado");
    }
    
    /**
     * Obtém o nome da implementação do DAO
     * @return Nome da implementação
     */
    public String getDAOImplementationName() {
        return userDAO.getImplementationName();
    }
    
    /**
     * Altera o usuário atual (RF045)
     * @param userName Nome do novo usuário
     * @return true se alterado com sucesso
     */
    public boolean changeUser(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            System.err.println("Nome de usuário inválido");
            return false;
        }
        
        // Busca o usuário
        Optional<User> userOpt = findUserByName(userName);
        if (userOpt.isEmpty()) {
            System.err.println("Usuário não encontrado: " + userName);
            return false;
        }
        
        User newUser = userOpt.get();
        setCurrentUser(newUser);
        
        // Emite evento de mudança de usuário
        mediator.emit(UserEventTypes.USER_CHANGED, 
            new UserEvents.UserChangedEvent(newUser));
        
        System.out.println("Usuário alterado para: " + userName);
        return true;
    }
    
    /**
     * Altera o usuário atual por ID (RF045)
     * @param userId ID do novo usuário
     * @return true se alterado com sucesso
     */
    public boolean changeUserById(Long userId) {
        if (userId == null) {
            System.err.println("ID de usuário inválido");
            return false;
        }
        
        // Busca o usuário
        Optional<User> userOpt = findUserById(userId);
        if (userOpt.isEmpty()) {
            System.err.println("Usuário não encontrado com ID: " + userId);
            return false;
        }
        
        User newUser = userOpt.get();
        setCurrentUser(newUser);
        
        System.out.println("Usuário alterado para: " + newUser.getName());
        return true;
    }
    
    /**
     * Lista todos os usuários disponíveis para alteração
     * @return Lista de usuários
     */
    public List<User> getAvailableUsers() {
        return getAllUsers();
    }
    
    /**
     * Verifica se pode jogar (sempre true no modo local)
     * @return true se pode jogar
     */
    public boolean canPlay() {
        return true;
    }
    
    /**
     * Obtém o nome do jogador atual
     * @return Nome do jogador
     */
    public String getCurrentPlayerName() {
        if (hasCurrentUser()) {
            return currentUser.getName();
        } else {
            return "Jogador Local";
        }
    }
} 