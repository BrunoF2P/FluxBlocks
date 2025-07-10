package com.uneb.fluxblocks.user;

import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.architecture.events.UserEvents;
import com.uneb.fluxblocks.architecture.events.UserEventTypes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gerenciador de sessão que persiste o usuário logado entre sessões.
 */
public class SessionManager {
    
    private static final String SESSION_FILE = "session.dat";
    private static final String SESSION_DIR = "data";
    private final GameMediator mediator;
    private final UserManager userManager;
    
    public SessionManager(GameMediator mediator, UserManager userManager) {
        this.mediator = mediator;
        this.userManager = userManager;
        registerEvents();
    }
    

    private void registerEvents() {
        mediator.receiver(UserEventTypes.LOGIN_SUCCESS, this::onUserLogin);
        mediator.receiver(UserEventTypes.LOGOUT_SUCCESS, this::onUserLogout);
        mediator.receiver(UserEventTypes.USER_CHANGED, this::onUserChanged);
        
        mediator.receiver(UserEventTypes.CHECK_SESSION_REQUEST, this::onCheckSessionRequest);
    }
    
    /**
     * Carrega a sessão salva
     * @return true se conseguiu carregar uma sessão válida
     */
    public boolean loadSavedSession() {
        try {
            Path sessionPath = getSessionFilePath();
            
            if (!Files.exists(sessionPath)) {
                System.out.println("Nenhuma sessão salva encontrada");
                return false;
            }
            
            // Lê os dados da sessão
            String sessionData = Files.readString(sessionPath);
            String[] parts = sessionData.split("\\|");
            
            if (parts.length < 2) {
                System.out.println("Dados de sessão inválidos");
                return false;
            }
            
            String userName = parts[0];
            String lastLoginStr = parts[1];
            
            // Verifica se o usuário ainda existe
            var userOpt = userManager.findUserByName(userName);
            if (userOpt.isEmpty()) {
                System.out.println("Usuário da sessão não existe mais: " + userName);
                clearSession();
                return false;
            }
            
            User user = userOpt.get();
            
            // Define como usuário atual
            userManager.setCurrentUser(user);
            
            System.out.println("Sessão carregada: " + userName);
            System.out.println("Último login: " + lastLoginStr);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar sessão: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Salva a sessão atual
     */
    private void saveSession(User user) {
        try {
            Path sessionPath = getSessionFilePath();
            Path sessionDir = sessionPath.getParent();
            
            // Cria o diretório se não existir
            if (!Files.exists(sessionDir)) {
                Files.createDirectories(sessionDir);
            }
            
            // Formato: userName|lastLogin
            String sessionData = user.getName() + "|" + 
                               LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            Files.writeString(sessionPath, sessionData);
            System.out.println("Sessão salva para: " + user.getName());
            
        } catch (Exception e) {
            System.err.println("Erro ao salvar sessão: " + e.getMessage());
        }
    }
    
    /**
     * Limpa a sessão salva
     */
    private void clearSession() {
        try {
            Path sessionPath = getSessionFilePath();
            if (Files.exists(sessionPath)) {
                Files.delete(sessionPath);
                System.out.println("Sessão limpa");
            }
        } catch (Exception e) {
            System.err.println("Erro ao limpar sessão: " + e.getMessage());
        }
    }
    
    /**
     * Retorna o caminho do arquivo de sessão
     */
    private Path getSessionFilePath() {
        return Paths.get(SESSION_DIR, SESSION_FILE);
    }
    
    /**
     * Handler para evento de login
     */
    private void onUserLogin(UserEvents.LoginSuccessEvent event) {
        saveSession(event.user());
    }
    
    /**
     * Handler para evento de logout
     */
    private void onUserLogout(UserEvents.LogoutSuccessEvent event) {
        clearSession();
    }
    
    /**
     * Handler para evento de mudança de usuário
     */
    private void onUserChanged(UserEvents.UserChangedEvent event) {
        saveSession(event.newUser());
    }
    
    /**
     * Handler para evento de verificação de sessão
     */
    private void onCheckSessionRequest(UserEvents.CheckSessionRequestEvent event) {
        boolean hasValidSession = hasValidSession();
        User currentUser = getCurrentUser();
        
        mediator.emit(UserEventTypes.CHECK_SESSION_RESPONSE, 
                     new UserEvents.CheckSessionResponseEvent(hasValidSession, currentUser));
    }
    
    /**
     * Verifica se há uma sessão válida
     * @return true se há usuário logado
     */
    public boolean hasValidSession() {
        return userManager.getCurrentUser() != null;
    }
    
    /**
     * Obtém o usuário atual da sessão
     * @return Usuário atual ou null
     */
    public User getCurrentUser() {
        return userManager.getCurrentUser();
    }
} 