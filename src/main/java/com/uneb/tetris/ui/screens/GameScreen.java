package com.uneb.tetris.ui.screens;

import com.uneb.tetris.architecture.events.GameplayEvents;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.ui.components.NextPiecePreview;
import com.uneb.tetris.ui.components.TimeDisplay;
import com.uneb.tetris.ui.effects.Effects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;

import java.util.Objects;

/**
 * Classe responsável pela interface gráfica principal do jogo Tetris.
 * Gerencia a exibição do tabuleiro, pontuação, nível, tempo e próxima peça.
 *
 * <p>A tela é dividida em três painéis principais:
 * <ul>
 *   <li>Painel Esquerdo: Exibe o nível atual e progresso</li>
 *   <li>Painel Central: Contém o tabuleiro do jogo</li>
 *   <li>Painel Direito: Mostra a próxima peça, pontuação e tempo</li>
 * </ul>
 *
 * @author Bruno Bispo
 */
public class GameScreen {
    /** Contêiner principal da interface. */
    private final StackPane root;

    /** Layout organizador dos painéis. */
    private final BorderPane layout;

    /** Tela do tabuleiro de jogo. */
    private final GameBoardScreen gameBoardScreen;

    /** Mediador de eventos do jogo. */
    private final GameMediator mediator;

    /** Contêiner para visualização da próxima peça. */
    private final StackPane nextPiecePreview;

    /** Componente que renderiza a próxima peça. */
    private NextPiecePreview nextPieceComponent;

    /** Largura da tela do jogo. */
    private final double screenWidth = 1368;

    /** Altura da tela do jogo. */
    private final double screenHeight = 768;

    /** Nível atual do jogador. */
    private int currentLevel = 1;

    /** Número total de linhas eliminadas. */
    private int linesCleared = 0;

    /** Número de linhas necessárias para avançar de nível. */
    private final int LINES_PER_LEVEL = 10;

    /** Pontuação atual do jogador. */
    private int score = 0;

    /** Tempo de jogo no formato "MM:SS:mmm". */
    private String gameTime = "00:00:000";

    /** Contêiner para o tabuleiro do jogo, que será animado. */
    private StackPane centerContainer;

    /** Indica se a peça atual está encostada e empurrando a parede esquerda. */
    private boolean isPushingLeftWall = false;

    /** Indica se a peça atual está encostada e empurrando a parede direita. */
    private boolean isPushingRightWall = false;

    /** Componentes da UI cacheados para evitar lookups repetidos */
    private Text scoreTextNode;
    private Text levelTextNode;
    private Arc progressArcNode;
    private Text linesLabelNode;
    
    /** Componente otimizado para exibição de tempo */
    private TimeDisplay timeDisplay;

    /**
     * Constrói uma nova tela de jogo e configura os elementos de UI.
     *
     * @param mediator Instância do GameMediator utilizada para orquestrar a comunicação entre os componentes visuais e lógicos do jogo.
     */
    public GameScreen(GameMediator mediator) {
        this.mediator = mediator;
        this.layout = new BorderPane();
        this.gameBoardScreen = new GameBoardScreen(mediator);
        this.nextPiecePreview = new StackPane();
        this.root = new StackPane();
        this.root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/ui/style.css")).toExternalForm());
        root.getStyleClass().add("game-screen");
        root.setCursor(Cursor.NONE);
        nextPiecePreview.setId("next-piece-container");

        setupLayout();
        setupNextPiecePreview();
    }

    /**
     * Inicializa as referências de UI para evitar lookups repetidos.
     * Chame este método após a construção da interface.
     */
    private void cacheUIReferences() {
        scoreTextNode = (Text) root.lookup("#score-text");
        scoreTextNode.setCache(true);
        scoreTextNode.setCacheHint(CacheHint.SPEED);
        
        levelTextNode = (Text) root.lookup("#level-text");
        levelTextNode.setCache(true);
        levelTextNode.setCacheHint(CacheHint.SPEED);
        
        progressArcNode = (Arc) root.lookup("#progress-arc");
        linesLabelNode = (Text) root.lookup("#lines-label");
        linesLabelNode.setCache(true);
        linesLabelNode.setCacheHint(CacheHint.SPEED);
    }

    /**
     * Inicializa a tela do jogo registrando os eventos necessários.
     * Deve ser chamado após a construção da tela.
     */
    public void initialize() {
        registerEvents();
        cacheUIReferences();
    }

    /**
     * Configura o componente de visualização da próxima peça.
     * Inicializa o componente NextPiecePreview e o associa ao contêiner.
     */
    private void setupNextPiecePreview() {
        this.nextPieceComponent = new NextPiecePreview(mediator, nextPiecePreview);
        this.nextPieceComponent.initialize();
    }

    /**
     * Registra os receptores de eventos para atualizar a interface.
     * Gerencia eventos como linhas eliminadas, pontuação, tempo e mudança de nível.
     */
    private void registerEvents() {
        mediator.receiver(GameplayEvents.LINE_CLEARED, lines -> {
            linesCleared += lines;
            updateLevelProgress(currentLevel, linesCleared, LINES_PER_LEVEL);
        });
        mediator.receiver(UiEvents.SCORE_UPDATE, this::updateScore);
        mediator.receiver(UiEvents.TIME_UPDATE, this::updateTime);
        mediator.receiver(UiEvents.LEVEL_UPDATE, level -> {
            currentLevel = level;
            updateLevelProgress(currentLevel, linesCleared, LINES_PER_LEVEL);
        });
        
        mediator.receiver(UiEvents.PIECE_LANDED_SOFT, data ->
            Effects.applySoftLanding(centerContainer, null));
        
        mediator.receiver(UiEvents.PIECE_LANDED_NORMAL, data ->
            Effects.applyNormalLanding(centerContainer, null));
        
        mediator.receiver(UiEvents.PIECE_LANDED_HARD, data ->
            Effects.applyHardLanding(centerContainer, null));

        setupWallPushAnimationListeners();
    }

    /**
     * Configura o layout principal da tela de jogo.
     * Organiza os painéis esquerdo, central e direito na interface.
     */
    private void setupLayout() {
        this.centerContainer = new StackPane(gameBoardScreen.getNode());
        this.centerContainer.setAlignment(Pos.CENTER);
        layout.setCenter(this.centerContainer);

        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        BorderPane.setMargin(leftPanel, new Insets(20, 0, 20, 20));
        BorderPane.setMargin(rightPanel, new Insets(20, 20, 20, 0));

        layout.setLeft(leftPanel);
        layout.setRight(rightPanel);

        root.getChildren().addAll(createBackground(), layout);
    }

    /**
     * Configura os listeners para os eventos de "empurrar" e "parar de empurrar" as paredes.
     * Estes listeners atualizam a posição do tabuleiro para dar feedback visual.
     */
    private void setupWallPushAnimationListeners() {
        mediator.receiver(UiEvents.PIECE_PUSHING_WALL_LEFT, unused -> {
            if (isPushingRightWall) {
                isPushingRightWall = false;
            }
            isPushingLeftWall = true;
            updateBoardPosition();
        });

        mediator.receiver(UiEvents.PIECE_PUSHING_WALL_RIGHT, unused -> {
            if (isPushingLeftWall) {
                isPushingLeftWall = false;
            }
            isPushingRightWall = true;
            updateBoardPosition();
        });

        mediator.receiver(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, unused -> {
            isPushingLeftWall = false;
            updateBoardPosition();
        });

        mediator.receiver(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, unused -> {
            isPushingRightWall = false;
            updateBoardPosition();
        });
    }

    /**
     * Atualiza a translação horizontal do contêiner do tabuleiro usando a classe Effects.
     * O tabuleiro se move na direção da parede que está sendo empurrada.
     */
    private void updateBoardPosition() {
        // Apenas anima se houver mudança de estado
        if (isPushingLeftWall || isPushingRightWall ||
                centerContainer.getTranslateX() != 0) {

            Effects.applyWallPushEffect(
                    centerContainer,
                    isPushingLeftWall,
                    isPushingRightWall
            );
        }
    }

    /**
     * Cria o painel esquerdo da interface.
     * Contém informações sobre o nível atual e o número de linhas eliminadas.
     *
     * @return Um contêiner VBox com os componentes do painel esquerdo
     */
    private VBox createLeftPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("side-panel");
        panel.setAlignment(Pos.TOP_CENTER);

        Text levelLabel = new Text("Nível");
        levelLabel.getStyleClass().addAll("info-text", "level-label");

        StackPane progressDisplay = createLevelProgress(currentLevel);

        Text linesLabel = new Text("Linhas: 0");
        linesLabel.getStyleClass().add("info-text");
        linesLabel.setId("lines-label");

        VBox bottomContainer = new VBox(15);
        bottomContainer.getStyleClass().add("bottom-info-container");
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.getChildren().addAll(linesLabel);

        panel.getChildren().addAll(levelLabel, progressDisplay, bottomContainer);
        return panel;
    }

    /**
     * Cria o painel direito da interface.
     * Contém a visualização da próxima peça, pontuação e tempo de jogo.
     *
     * @return Um contêiner VBox com os componentes do painel direito
     */
    private VBox createRightPanel() {
        VBox panel = new VBox();
        panel.getStyleClass().add("side-panel");
        panel.setAlignment(Pos.TOP_CENTER);

        VBox nextPieceContainer = new VBox(5);
        nextPieceContainer.setAlignment(Pos.CENTER);

        Text nextPieceLabel = new Text("Próxima peça");
        nextPieceLabel.getStyleClass().add("info-text");

        nextPiecePreview.getStyleClass().add("next-piece-preview");
        nextPiecePreview.setMinSize(120, 120);
        nextPiecePreview.setPrefSize(120, 120);
        nextPiecePreview.setMaxSize(120, 120);
        nextPiecePreview.setId("next-piece-container");

        nextPieceContainer.getChildren().addAll(nextPieceLabel, nextPiecePreview);

        VBox bottomContainer = new VBox(15);
        bottomContainer.setAlignment(Pos.CENTER);

        VBox scoreBox = new VBox(5);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.getStyleClass().add("info-box");

        Text scoreLabel = new Text("Pontuação");
        scoreLabel.getStyleClass().add("info-text");

        Text scoreText = new Text("0");
        scoreText.getStyleClass().add("score-text");
        scoreText.setId("score-text");

        scoreBox.getChildren().addAll(scoreLabel, scoreText);

        VBox timeBox = new VBox(5);
        timeBox.setAlignment(Pos.CENTER);
        timeBox.getStyleClass().add("info-box");
        timeBox.setMinWidth(150);

        Text timeLabel = new Text("Tempo");
        timeLabel.getStyleClass().add("info-text");

        timeDisplay = new TimeDisplay(150, 40);
        timeDisplay.setColors(Color.web("#fcd34d"), Color.color(0, 0, 0, 0.6));

        timeBox.getChildren().addAll(timeLabel, timeDisplay);

        bottomContainer.getChildren().addAll(scoreBox, timeBox);

        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        panel.getChildren().addAll(nextPieceContainer, spacer, bottomContainer);
        return panel;
    }

    /**
     * Cria o componente visual de progresso do nível.
     * Consiste em um círculo que mostra o progresso para o próximo nível.
     *
     * @param currentLevel Nível atual do jogo
     * @return Um contêiner StackPane com os elementos visuais do indicador de progresso
     */
    private StackPane createLevelProgress(int currentLevel) {
        Circle backgroundCircle = new Circle(50);
        backgroundCircle.getStyleClass().add("progress-track-circle");

        Arc progressArc = new Arc(0, 0, 50, 50, 90, 0);
        progressArc.setStrokeLineCap(StrokeLineCap.ROUND);
        progressArc.setFill(Color.TRANSPARENT);
        progressArc.getStyleClass().add("progress-arc");
        progressArc.setId("progress-arc");

        Text levelText = new Text(String.valueOf(currentLevel));
        levelText.getStyleClass().add("level-text");
        levelText.setId("level-text");

        StackPane progressPane = new StackPane();
        progressPane.setPrefSize(120, 120);

        Group progressGroup = new Group(backgroundCircle, progressArc);
        progressPane.getChildren().addAll(progressGroup, levelText);
        return progressPane;
    }

    /**
     * Atualiza o progresso do nível atual, incluindo a barra de progresso circular
     * e o número de linhas completadas.
     *
     * @param currentLevel Nível atual do jogo
     * @param linesCleared Número total de linhas completadas
     * @param LINES_PER_LEVEL Número de linhas necessárias para subir de nível
     */
    public void updateLevelProgress(int currentLevel, int linesCleared, int LINES_PER_LEVEL) {
        int linesInCurrentLevel = linesCleared % LINES_PER_LEVEL;
        double progress = 360 * ((double) linesInCurrentLevel / LINES_PER_LEVEL);

        if (progressArcNode != null) {
            progressArcNode.setLength(-progress);
        }

        if (levelTextNode != null) {
            levelTextNode.setText(String.valueOf(currentLevel));
        }

        if (linesLabelNode != null) {
            linesLabelNode.setText(String.format("Linhas: %d", linesCleared));
        }
    }

    /**
     * Atualiza a pontuação exibida na interface.
     *
     * @param score Nova pontuação a ser exibida
     */
    public void updateScore(int score) {
        this.score = score;
        if (scoreTextNode != null) {
            scoreTextNode.setText(String.valueOf(score));
        }
    }

    /**
     * Atualiza o tempo de jogo exibido na interface.
     * Usa o componente TimeDisplay otimizado para atualizações frequentes.
     *
     * @param time Tempo de jogo no formato "MM:SS:mmm"
     */
    public void updateTime(String time) {
        this.gameTime = time;
        if (timeDisplay != null) {
            timeDisplay.updateTime(time);
        }
    }

    /**
     * Cria o plano de fundo da tela do jogo.
     *
     * @return Um painel Pane configurado como fundo da tela
     */
    private Pane createBackground() {
        Pane bg = new Pane();
        bg.getStyleClass().add("game-bg");
        bg.setPrefSize(screenWidth, screenHeight);

        // Cria os quadrados
        for (int i = 0; i < 5; i++) {
            Effects.createSquareParticle(bg, screenWidth, screenHeight);
        }

        // Cria as partículas
        for (int i = 0; i < 12; i++) {
            Effects.createFireflyParticle(bg, screenWidth, screenHeight);
        }

        return bg;
    }

    /**
     * Retorna o nó raiz da interface gráfica do jogo.
     * Este nó contém todos os elementos visuais da tela.
     *
     * @return O componente raiz da interface
     */
    public Parent getNode() {
        return root;
    }
}

