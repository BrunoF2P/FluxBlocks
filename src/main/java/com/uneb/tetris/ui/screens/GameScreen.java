package com.uneb.tetris.ui.screens;

import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.ui.components.NextPiecePreview;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.util.Duration;

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

    /** Tempo de jogo no formato "MM:SS". */
    private String gameTime = "00:00";

    /** Contêiner para o tabuleiro do jogo, que será animado. */
    private StackPane centerContainer;

    /** Flag para controlar se uma animação está ativa. */
    private boolean isAnimationPlaying = false;

    /** Efeito do quanto o container é empurrado */
    private static final double WALL_PUSH_OFFSET = 12.0;

    /** Duração do efeito do container sendo empurrado*/
    private static final Duration WALL_PUSH_ANIMATION_DURATION = Duration.millis(140);

    /** Indica se a peça atual está encostada e empurrando a parede esquerda. */
    private boolean isPushingLeftWall = false;

    /** Indica se a peça atual está encostada e empurrando a parede direita. */
    private boolean isPushingRightWall = false;


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
     * Inicializa a tela do jogo registrando os eventos necessários.
     * Deve ser chamado após a construção da tela.
     */
    public void initialize() {
        registerEvents();
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
        mediator.receiver(GameEvents.GameplayEvents.LINE_CLEARED, lines -> {
            linesCleared += lines;
            updateLevelProgress(currentLevel, linesCleared, LINES_PER_LEVEL);
        });
        mediator.receiver(GameEvents.UiEvents.SCORE_UPDATE, this::updateScore);
        mediator.receiver(GameEvents.UiEvents.TIME_UPDATE, this::updateTime);
        mediator.receiver(GameEvents.UiEvents.LEVEL_UPDATE, level -> {
            currentLevel = level;
            updateLevelProgress(currentLevel, linesCleared, LINES_PER_LEVEL);
        });
        mediator.receiver(GameEvents.UiEvents.PIECE_LANDED_SOFT, data -> playLandedAnimation(5.0, Duration.millis(60)));
        mediator.receiver(GameEvents.UiEvents.PIECE_LANDED_NORMAL, data -> playLandedAnimation(8.0, Duration.millis(70)));
        mediator.receiver(GameEvents.UiEvents.PIECE_LANDED_HARD, data -> playLandedAnimation(12.0, Duration.millis(80)));

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
        mediator.receiver(GameEvents.UiEvents.PIECE_PUSHING_WALL_LEFT, unused -> {
            if (isPushingRightWall) {
                isPushingRightWall = false;
            }
            isPushingLeftWall = true;
            updateBoardPosition();
        });

        mediator.receiver(GameEvents.UiEvents.PIECE_PUSHING_WALL_RIGHT, unused -> {
            if (isPushingLeftWall) {
                isPushingLeftWall = false;
            }
            isPushingRightWall = true;
            updateBoardPosition();
        });

        mediator.receiver(GameEvents.UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, unused -> {
            isPushingLeftWall = false;
            updateBoardPosition();
        });

        mediator.receiver(GameEvents.UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, unused -> {
            isPushingRightWall = false;
            updateBoardPosition();
        });
    }

    /**
     * Atualiza a translação horizontal do contêiner do tabuleiro (centerContainer)
     * com base em qual parede (se houver) está sendo "empurrada", usando uma animação suave.
     * O tabuleiro se move na direção da parede que está sendo empurrada.
     */
    private void updateBoardPosition() {
        double targetX = 0;
        if (isPushingLeftWall) {
            targetX = -WALL_PUSH_OFFSET;
        } else if (isPushingRightWall) {
            targetX = WALL_PUSH_OFFSET;
        }

        TranslateTransition tt = new TranslateTransition(WALL_PUSH_ANIMATION_DURATION, centerContainer);
        tt.setToX(targetX);
        tt.play();
    }

    /**
     * Executa uma animação de "shake" vertical (para baixo) no contêiner do tabuleiro.
     *
     * @param intensity A distância do "shake" em pixels (para baixo).
     * @param duration A duração de cada parte do "shake" (ida ou volta).
     */
    private void playLandedAnimation(double intensity, Duration duration) {
        if (isAnimationPlaying) {
            return;
        }
        isAnimationPlaying = true;

        TranslateTransition tt = new TranslateTransition(duration, centerContainer);
        tt.setByY(intensity);
        tt.setCycleCount(2);
        tt.setAutoReverse(true);

        tt.setOnFinished(event -> {
            centerContainer.setTranslateY(0);
            isAnimationPlaying = false;
        });

        tt.play();
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

        Text timeText = new Text("00:00:000");
        timeText.getStyleClass().add("score-text");
        timeText.setId("time-text");

        timeBox.getChildren().addAll(timeLabel, timeText);

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

        Arc progressArc = (Arc) root.lookup("#progress-arc");
        if (progressArc != null) {
            progressArc.setLength(-progress);
        }

        Text levelText = (Text) root.lookup("#level-text");
        if (levelText != null) {
            levelText.setText(String.valueOf(currentLevel));
        }

        Text linesLabel = (Text) root.lookup("#lines-label");
        if (linesLabel != null) {
            linesLabel.setText(String.format("Linhas: %d", linesCleared));
        }
    }

    /**
     * Atualiza a pontuação exibida na interface.
     *
     * @param score Nova pontuação a ser exibida
     */
    public void updateScore(int score) {
        this.score = score;
        Text scoreText = (Text) root.lookup("#score-text");
        if (scoreText != null) {
            scoreText.setText(String.valueOf(score));
        }
    }

    /**
     * Atualiza o tempo de jogo exibido na interface.
     *
     * @param time Tempo de jogo no formato "MM:SS"
     */
    public void updateTime(String time) {
        this.gameTime = time;
        Text timeText = (Text) root.lookup("#time-text");
        if (timeText != null) {
            timeText.setText(time);
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