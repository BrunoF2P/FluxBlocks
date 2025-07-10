package com.uneb.fluxblocks.architecture.events;

import com.uneb.fluxblocks.user.User;

public abstract class UserEvents {
    
    // ===== EVENTOS DE AUTENTICAÇÃO =====
    
    /**
     * Usuário tenta fazer login
     */
    public record LoginRequestEvent(String username) {}
    
    /**
     * Login realizado com sucesso
     */
    public record LoginSuccessEvent(User user) {}
    
    /**
     * Falha no login
     */
    public record LoginFailedEvent(String username, String reason) {}
    
    /**
     * Usuário tenta fazer logout
     */
    public record LogoutRequestEvent() {}
    
    /**
     * Logout realizado com sucesso
     */
    public record LogoutSuccessEvent() {}
    
    /**
     * Sessão expirou
     */
    public record SessionExpiredEvent() {}
    
    /**
     * Verificação de sessão solicitada
     */
    public record CheckSessionRequestEvent() {}
    
    /**
     * Resposta da verificação de sessão
     */
    public record CheckSessionResponseEvent(boolean hasValidSession, User currentUser) {}
    
    // ===== EVENTOS DE GESTÃO DE USUÁRIOS =====
    
    /**
     * Tentativa de criar usuário
     */
    public record CreateUserRequestEvent(String username) {}
    
    /**
     * Usuário criado com sucesso
     */
    public record CreateUserSuccessEvent(User user) {}
    
    /**
     * Falha ao criar usuário
     */
    public record CreateUserFailedEvent(String username, String reason) {}
    
    /**
     * Tentativa de atualizar usuário
     */
    public record UpdateUserRequestEvent(User user) {}
    
    /**
     * Usuário atualizado com sucesso
     */
    public record UpdateUserSuccessEvent(User user) {}
    
    /**
     * Tentativa de deletar usuário
     */
    public record DeleteUserRequestEvent(Long userId) {}
    
    /**
     * Usuário deletado com sucesso
     */
    public record DeleteUserSuccessEvent(Long userId) {}
    
    /**
     * Usuário alterado
     */
    public record UserChangedEvent(User newUser) {}
    
    // ===== EVENTOS DE ESTATÍSTICAS =====
    
    /**
     * Estatísticas do usuário atualizadas
     */
    public record UserStatsUpdatedEvent(User user) {}
    
    /**
     * Novo recorde pessoal
     */
    public record NewPersonalBestEvent(User user, int newBestScore) {}
    
    /**
     * Usuário subiu de nível
     */
    public record UserLevelUpEvent(User user, int newLevel) {}
    
    // ===== EVENTOS DE CONVIDADO =====
    
    /**
     * Modo convidado ativado
     */
    public record GuestModeActivatedEvent(String guestName) {}
    
    /**
     * Modo convidado desativado
     */
    public record GuestModeDeactivatedEvent() {}
    
    private UserEvents() {
    }
} 