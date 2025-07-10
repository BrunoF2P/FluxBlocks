package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.ranking.RankingEntry;
import com.uneb.fluxblocks.game.ranking.RankingManager;
import com.uneb.fluxblocks.ui.components.ButtonGame;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import com.uneb.fluxblocks.ui.components.FooterComponent;
import com.uneb.fluxblocks.user.UserManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Tela de ranking que mostra as 10 melhores pontuações gerais e a melhor pontuação do usuário logado.
 */
public class RankingScreen extends BaseScreen {
    private final StackPane root;
    private final BorderPane mainLayout;
    private final VBox titleContainer;
    private final VBox rankingContainer;
    private final FooterComponent footerContainer;
    private final ButtonGame backButton;
    private final GameMediator mediator;
    private final RankingManager rankingManager;
    private final UserManager userManager;
    private final Text mainTitleText;
    private final VBox globalEntriesContainer;
    private final VBox userBestContainer;
    
    public RankingScreen(GameMediator mediator, RankingManager rankingManager, UserManager userManager) {
        this.mediator = mediator;
        this.rankingManager = rankingManager;
        this.userManager = userManager;
        this.root = new StackPane();
        this.mainLayout = new BorderPane();
        this.titleContainer = new VBox();
        this.rankingContainer = new VBox();
        this.footerContainer = new FooterComponent(new String[][] {
            {"VOLTAR", "ESC"},
            {"ATUALIZAR", "F5"}
        });
        this.backButton = new ButtonGame("VOLTAR", ButtonGame.ButtonType.EXIT);
        this.mainTitleText = new Text();
        this.globalEntriesContainer = new VBox(10);
        this.userBestContainer = new VBox(10);
        
        root.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        initializeComponents();
        setupKeyNavigation();
        setupCache();
        loadRankingData();
        playEntryAnimations();
    }
    
    private void initializeComponents() {
        setupBackground();
        setupTitle();
        setupRanking();
        setupLayout();
    }
    
    private void setupBackground() {
        DynamicBackground dynamicBackground = new DynamicBackground(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        root.getChildren().add(dynamicBackground.getCanvas());
    }
    
    private void setupTitle() {
        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        
        Text mainTitle = new Text("RANKING GERAL");
        mainTitle.getStyleClass().add("title-flux");
        mainTitle.setStyle("-fx-font-size: 48px;");
        
        mainTitleText.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 20px;" +
            "-fx-fill: #3498db;"
        );
        
        titleBox.getChildren().addAll(mainTitle, mainTitleText);
        titleContainer.getChildren().add(titleBox);
        titleContainer.setAlignment(Pos.CENTER);
    }
    
    private void setupRanking() {
        rankingContainer.setAlignment(Pos.CENTER);
        rankingContainer.setSpacing(20);
        rankingContainer.setPadding(new Insets(20));
        
        // Seção do ranking geral
        VBox globalSection = createGlobalRankingSection();
        
        // Seção da melhor pontuação do usuário
        VBox userSection = createUserBestSection();
        
        // Botão voltar
        backButton.setOnAction(e -> goBack());
        backButton.getButton().setPrefWidth(150);
        
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(globalSection, userSection, backButton.getButton());
        
        rankingContainer.getChildren().add(contentBox);
    }
    
    private VBox createGlobalRankingSection() {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);
        
        Text sectionTitle = new Text("TOP 10 - MELHORES PONTUAÇÕES");
        sectionTitle.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: #2ecc71;"
        );
        
        // Container com scroll para as entradas
        ScrollPane scrollPane = new ScrollPane(globalEntriesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background: transparent;"
        );
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        section.getChildren().addAll(sectionTitle, scrollPane);
        return section;
    }
    
    private VBox createUserBestSection() {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);
        
        Text sectionTitle = new Text("SUA MELHOR PONTUAÇÃO");
        sectionTitle.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: #f39c12;"
        );
        
        // Container para a melhor pontuação do usuário
        ScrollPane scrollPane = new ScrollPane(userBestContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(150);
        scrollPane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background: transparent;"
        );
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        section.getChildren().addAll(sectionTitle, scrollPane);
        return section;
    }
    
    private void setupLayout() {
        mainLayout.setMaxSize(1920, 1080);
        
        mainLayout.setTop(footerContainer);
        mainLayout.setLeft(titleContainer);
        mainLayout.setRight(rankingContainer);
        
        BorderPane.setAlignment(titleContainer, Pos.CENTER);
        BorderPane.setAlignment(rankingContainer, Pos.CENTER);
        
        BorderPane.setMargin(titleContainer, new Insets(0, 0, 0, 100));
        BorderPane.setMargin(rankingContainer, new Insets(0, 100, 0, 50));
        
        root.getChildren().add(mainLayout);
    }
    
    private void setupKeyNavigation() {
        root.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case ESCAPE:
                    goBack();
                    break;
                case F5:
                    loadRankingData();
                    break;
                default:
                    break;
            }
        });
        root.setFocusTraversable(true);
        root.requestFocus();
    }
    
    private void loadRankingData() {
        // Limpa entradas anteriores
        globalEntriesContainer.getChildren().clear();
        userBestContainer.getChildren().clear();
        
        // Carrega ranking geral (top 10 de todos os usuários)
        loadGlobalRanking();
        
        // Carrega melhor pontuação do usuário logado
        loadUserBestScore();
    }
    
    private void loadGlobalRanking() {
        List<RankingEntry> topEntries = rankingManager.getTopRanking();
        
        if (topEntries.isEmpty()) {
            showNoGlobalEntriesMessage();
        } else {
            showGlobalRankingEntries(topEntries);
        }
    }
    
    private void loadUserBestScore() {
        // Verifica se há usuário logado
        if (userManager == null || userManager.getCurrentUser() == null) {
            mainTitleText.setText("NENHUM USUÁRIO LOGADO");
            showNoUserMessage();
            return;
        }
        
        String currentUserName = userManager.getCurrentUser().getName();
        mainTitleText.setText("USUÁRIO: " + currentUserName.toUpperCase());
        
        // Busca a melhor pontuação do usuário
        List<RankingEntry> userEntries = rankingManager.getTopEntriesByPlayer(currentUserName, 1);
        
        if (userEntries.isEmpty()) {
            showNoUserEntriesMessage();
        } else {
            showUserBestEntry(userEntries.get(0));
        }
    }
    
    private void showNoGlobalEntriesMessage() {
        VBox messageBox = new VBox(10);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(50));
        
        Text message = new Text("NENHUMA PONTUAÇÃO REGISTRADA");
        message.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 18px;" +
            "-fx-fill: #95a5a6;"
        );
        
        messageBox.getChildren().add(message);
        globalEntriesContainer.getChildren().add(messageBox);
    }
    
    private void showNoUserMessage() {
        VBox messageBox = new VBox(10);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(20));
        
        Text message = new Text("FAÇA LOGIN PARA VER SUA MELHOR PONTUAÇÃO");
        message.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 16px;" +
            "-fx-fill: #95a5a6;"
        );
        
        messageBox.getChildren().add(message);
        userBestContainer.getChildren().add(messageBox);
    }
    
    private void showNoUserEntriesMessage() {
        VBox messageBox = new VBox(10);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(20));
        
        Text message = new Text("NENHUMA PONTUAÇÃO REGISTRADA");
        message.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 16px;" +
            "-fx-fill: #95a5a6;"
        );
        
        Text subMessage = new Text("Jogue algumas partidas para aparecer aqui!");
        subMessage.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-fill: #7f8c8d;"
        );
        
        messageBox.getChildren().addAll(message, subMessage);
        userBestContainer.getChildren().add(messageBox);
    }
    
    private void showGlobalRankingEntries(List<RankingEntry> entries) {
        for (int i = 0; i < entries.size(); i++) {
            RankingEntry entry = entries.get(i);
            HBox entryBox = createRankingEntryBox(entry, i + 1, false);
            globalEntriesContainer.getChildren().add(entryBox);
        }
    }
    
    private void showUserBestEntry(RankingEntry entry) {
        HBox entryBox = createRankingEntryBox(entry, 1, true);
        userBestContainer.getChildren().add(entryBox);
    }
    
    private HBox createRankingEntryBox(RankingEntry entry, int position, boolean isUserBest) {
        HBox entryBox = new HBox(20);
        entryBox.setAlignment(Pos.CENTER_LEFT);
        entryBox.setPadding(new Insets(10, 20, 10, 20));
        
        // Estilo diferente para melhor pontuação do usuário
        if (isUserBest) {
            entryBox.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(243, 156, 18, 0.8), rgba(230, 126, 34, 0.8));" +
                "-fx-border-color: #f39c12;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 5px;"
            );
        } else {
            entryBox.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(52, 73, 94, 0.8), rgba(44, 62, 80, 0.8));" +
                "-fx-border-color: #3498db;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 5px;"
            );
        }
        
        entryBox.setPrefWidth(800);
        
        // Posição
        Text positionText = new Text(String.format("%02d", position));
        positionText.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: " + (isUserBest ? "#e74c3c" : "#f39c12") + ";"
        );
        
        // Nome do jogador
        Text playerText = new Text(entry.getPlayerName().toUpperCase());
        playerText.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: " + (isUserBest ? "#ffffff" : "#ecf0f1") + ";"
        );
        
        // Pontuação
        Text scoreText = new Text(String.format("%,d", entry.getScore()));
        scoreText.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: #2ecc71;"
        );
        
        // Nível
        Text levelText = new Text("Nível " + entry.getLevel());
        levelText.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 16px;" +
            "-fx-fill: #3498db;"
        );
        
        // Linhas
        Text linesText = new Text(entry.getLinesCleared() + " linhas");
        linesText.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 16px;" +
            "-fx-fill: #e74c3c;"
        );
        
        // Data
        String dateStr = entry.getDateTime() != null ? 
            entry.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";
        Text dateText = new Text(dateStr);
        dateText.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-fill: #95a5a6;"
        );
        
        // Layout
        VBox leftInfo = new VBox(5);
        leftInfo.setAlignment(Pos.CENTER_LEFT);
        leftInfo.getChildren().addAll(playerText, scoreText);
        
        VBox centerInfo = new VBox(5);
        centerInfo.setAlignment(Pos.CENTER);
        centerInfo.getChildren().addAll(levelText, linesText);
        
        VBox rightInfo = new VBox(5);
        rightInfo.setAlignment(Pos.CENTER_RIGHT);
        rightInfo.getChildren().add(dateText);
        
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        entryBox.getChildren().addAll(positionText, leftInfo, spacer1, centerInfo, spacer2, rightInfo);
        
        return entryBox;
    }
    
    private void goBack() {
        mediator.emit(UiEvents.BACK_TO_MENU, null);
    }
    
    private void playEntryAnimations() {
        playStandardEntryAnimations(titleContainer, rankingContainer, footerContainer);
    }
    
    @Override
    public void destroy() {
        // Cleanup se necessário
    }
    
    @Override
    public Parent getNode() {
        return root;
    }
    
    private void setupCache() {
        if (GameConfig.ENABLE_UI_CACHE) {
            root.setCache(true);
            root.setCacheHint(GameConfig.getCacheHint());
        }
    }
} 