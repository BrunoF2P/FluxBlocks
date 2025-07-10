package com.uneb.fluxblocks.user;

import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.ranking.RankingManager;

import java.util.Random;

/**
 * Implementação online do UserManager.
 */
public class OnlineUserManager extends UserManager {
    
    private boolean guestMode;
    private String guestName;
    private static final String GUEST_PREFIX = "Convidado_";
    private static final Random random = new Random();
    
    public OnlineUserManager(GameMediator mediator, RankingManager rankingManager) {
        super(mediator, rankingManager);
        this.guestMode = false;
        this.guestName = null;
    }
    
    /**
     * Ativa o modo convidado
     */
    public void enableGuestMode() {
        this.guestMode = true;
        this.guestName = generateGuestName();
        setCurrentUser(null);
        System.out.println("Modo convidado ativado: " + guestName);
    }
    
    /**
     * Desativa o modo convidado
     */
    public void disableGuestMode() {
        this.guestMode = false;
        this.guestName = null;
        System.out.println("Modo convidado desativado");
    }
    
    /**
     * Verifica se está no modo convidado
     * @return true se está no modo convidado
     */
    public boolean isGuestMode() {
        return guestMode;
    }
    
    /**
     * Verifica se o nome é de um usuário convidado
     * @param name Nome a ser verificado
     * @return true se é nome de convidado
     */
    public static boolean isGuestName(String name) {
        return name != null && name.startsWith(GUEST_PREFIX);
    }
    
    /**
     * Gera um nome aleatório para convidado
     * @return Nome de convidado único
     */
    private String generateGuestName() {
        int randomNumber = random.nextInt(999999) + 1;
        return GUEST_PREFIX + String.format("%06d", randomNumber);
    }
    
    /**
     * Obtém o nome do convidado atual
     * @return Nome do convidado ou null se não estiver no modo convidado
     */
    public String getGuestName() {
        return guestMode ? guestName : null;
    }
    
    @Override
    public boolean canPlay() {
        return hasCurrentUser() || isGuestMode();
    }
    
    @Override
    public String getCurrentPlayerName() {
        if (hasCurrentUser()) {
            return getCurrentUser().getName();
        } else if (isGuestMode()) {
            return guestName;
        } else {
            return "Jogador";
        }
    }
    
    /**
     * Remove apenas pontuações do usuário atual
     * @param entryId ID da entrada a ser removida
     * @return true se removida com sucesso
     */
    public boolean deleteOwnRankingEntry(Long entryId) {
        if (entryId == null) {
            System.err.println("ID da entrada não pode ser nulo");
            return false;
        }
        
        if (!hasCurrentUser() && !isGuestMode()) {
            System.err.println("Usuário não logado - não pode excluir pontuações");
            return false;
        }
        
        // Convidados não podem excluir pontuações
        if (isGuestMode()) {
            System.err.println("Convidados não podem excluir pontuações");
            return false;
        }
        
        return false; // Placeholder
    }
    
    /**
     * Remove usuário e suas pontuações com verificação de segurança (RF010/RN006)
     * @param userId ID do usuário a ser removido
     * @return true se removido com sucesso
     */
    @Override
    public boolean deleteUserById(Long userId) {
        if (!hasCurrentUser()) {
            System.err.println("Usuário não logado - não pode excluir outros usuários");
            return false;
        }
        
        // Convidados não podem excluir usuários
        if (isGuestMode()) {
            System.err.println("Convidados não podem excluir usuários");
            return false;
        }
        
        // Verifica se está tentando excluir o próprio usuário
        if (getCurrentUser().getId().equals(userId)) {
            return super.deleteUserById(userId);
        } else {
            System.err.println("Não é possível excluir outros usuários");
            return false;
        }
    }
    
    /**
     * Convidados não podem criar usuários
     */
    @Override
    public Long createUser(String name) {
        if (isGuestMode()) {
            System.err.println("Convidados não podem criar usuários");
            return null;
        }
        return super.createUser(name);
    }
    
    /**
     * Convidados não podem alterar usuários
     */
    @Override
    public boolean changeUser(String userName) {
        if (isGuestMode()) {
            System.err.println("Convidados não podem alterar usuários");
            return false;
        }
        return super.changeUser(userName);
    }
    
    /**
     * Convidados não podem alterar usuários por ID
     */
    @Override
    public boolean changeUserById(Long userId) {
        if (isGuestMode()) {
            System.err.println("Convidados não podem alterar usuários");
            return false;
        }
        return super.changeUserById(userId);
    }
    
    /**
     * Convidados não podem excluir usuários por nome
     */
    @Override
    public boolean deleteUserByName(String name) {
        if (isGuestMode()) {
            System.err.println("Convidados não podem excluir usuários");
            return false;
        }
        return super.deleteUserByName(name);
    }
} 