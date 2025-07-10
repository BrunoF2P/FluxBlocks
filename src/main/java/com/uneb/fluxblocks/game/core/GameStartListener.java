package com.uneb.fluxblocks.game.core;

import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.user.UserManager;
import com.uneb.fluxblocks.user.SessionManager;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.game.ranking.RankingManager;
import com.uneb.fluxblocks.game.ranking.dao.SQLiteRankingDAO;


public class GameStartListener {
    
    private final GameMediator mediator;
    private boolean initialized = false;
    
    public GameStartListener(GameMediator mediator) {
        this.mediator = mediator;
        registerEvents();
    }
    

    private void registerEvents() {
        mediator.receiver(UiEvents.START, this::onGameStart);
    }

    private void onGameStart(Void event) {
        if (initialized) {
            System.out.println("Jogo já inicializado, ignorando evento START");
            return;
        }
        
        System.out.println("🚀 FluxBlocks iniciando...");
        
        try {
            // Tenta inicializar os sistemas
            initializeSystems();
            initialized = true;
            System.out.println("✅ FluxBlocks pronto!");
            
        } catch (Exception e) {
            System.err.println("⚠️ Erro ao inicializar sistemas: " + e.getMessage());
            System.out.println("🎮 Continuando sem alguns recursos...");
        }
    }
    
    /**
     * Inicializa os sistemas básicos
     */
    private void initializeSystems() {
        System.out.println("📊 Tentando conectar ao banco de dados...");
        
        try {
            // Tenta criar o DAO
            SQLiteRankingDAO rankingDAO = new SQLiteRankingDAO(mediator);
            
            // Tenta inicializar
            if (rankingDAO.initialize()) {
                System.out.println("✅ Banco de dados conectado!");
                
                GameState gameState = new GameState();
                RankingManager rankingManager = new RankingManager(mediator, gameState);
                UserManager userManager = new UserManager(mediator, rankingManager);
                SessionManager sessionManager = new SessionManager(mediator, userManager);
                
                mediator.setRankingManager(rankingManager);
                mediator.setUserManager(userManager);
                
                rankingDAO.setUserManager(userManager);
                
                RankingManager mediatorRankingManager = mediator.getRankingManager();
                if (mediatorRankingManager != null && mediatorRankingManager.getRankingDAO() instanceof SQLiteRankingDAO) {
                    ((SQLiteRankingDAO) mediatorRankingManager.getRankingDAO()).setUserManager(userManager);
                    System.out.println("✅ UserManager configurado no RankingManager do mediator");
                }
                
                System.out.println("✅ Managers criados e configurados!");
                
                // Tenta carregar sessão
                boolean sessionLoaded = sessionManager.loadSavedSession();
                if (sessionLoaded) {
                    System.out.println("👤 Usuário logado: " + sessionManager.getCurrentUser().getName());
                } else {
                    System.out.println("ℹ️ Nenhum usuário logado");
                }
                
            } else {
                System.err.println("❌ Falha ao inicializar banco de dados");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro durante inicialização: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 