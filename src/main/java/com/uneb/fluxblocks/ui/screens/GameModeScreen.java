package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela de seleção de modos de jogo.
 * Permite ao usuário escolher entre diferentes modos de jogo, como solo, multiplayer local e online.
 */
public class GameModeScreen {
    private final GameMediator mediator;
    private final StackPane root;
    private final VBox mainLayout;
    private final VBox titleContainer;
    private final HBox cardsContainer;
    private final HBox footerContainer;
    private final List<GameModeCard> modeCards = new ArrayList<>();
    private final List<EventHandler<MouseEvent>> cardActions = new ArrayList<>();
    
    private int selectedIndex = 0;
    private ScaleTransition cardScaleTransition;
    
    private Text titleText;
    private DynamicBackground dynamicBackground;
    
    private static final Duration CARD_SCALE_DURATION = Duration.millis(200);

    private static final GameModeData[] GAME_MODES = {
            new GameModeData("SOLO", "Jogue sozinho seu solitario", "#FF6B35", "1"),
            new GameModeData("MULTIPLAYER LOCAL", "Jogue com amigos", "#4ECDC4", "2"),
            new GameModeData("MULTIPLAYER ONLINE", "Jogue online", "#45B7D1", "3"),
    };

    public GameModeScreen(GameMediator mediator) {
        this.mediator = mediator;
        this.root = new StackPane();
        this.mainLayout = new VBox();
        this.titleContainer = new VBox();
        this.cardsContainer = new HBox();
        this.footerContainer = new HBox();

        root.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        initializeComponents();
        setupKeyNavigation();
        setupCardActions();
        setupCache();
        playEntryAnimations();
    }

    private void initializeComponents() {
        setupBackground();
        setupTitle();
        setupCards();
        setupFooter();
        setupLayout();
    }

    private void setupBackground() {
        dynamicBackground = new DynamicBackground(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        root.getChildren().add(dynamicBackground.getCanvas());
        root.getStyleClass().add("game-mode-screen");
    }

    private void setupTitle() {
        VBox titleBox = new VBox(0);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(5, 0, 5, 0));

        titleText = new Text("MODOS DE JOGO");
        titleText.getStyleClass().add("game-mode-main-title");

        titleBox.getChildren().add(titleText);
        titleContainer.getChildren().add(titleBox);
        titleContainer.setAlignment(Pos.CENTER);
        
        VBox.setVgrow(titleContainer, Priority.NEVER);
    }

    private void setupCards() {
        cardsContainer.getStyleClass().add("game-mode-cards-container");
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setSpacing(40);
        cardsContainer.setPadding(new Insets(10, 0, 10, 0));
        
        VBox.setVgrow(cardsContainer, Priority.ALWAYS);

        modeCards.clear();

        for (int i = 0; i < GAME_MODES.length; i++) {
            GameModeData mode = GAME_MODES[i];
            GameModeCard card = new GameModeCard(mode, i);

            final int index = i;
            card.setOnMouseEntered(e -> selectCard(index));
            card.setOnMouseClicked(e -> activateSelectedCard());

            modeCards.add(card);
            cardsContainer.getChildren().add(card);
        }

        updateCardSelection();
    }

    private void setupFooter() {
        footerContainer.getStyleClass().add("menu-footer");
        footerContainer.setAlignment(Pos.CENTER);
        footerContainer.setPadding(new Insets(10, 0, 10, 0));
        
        VBox.setVgrow(footerContainer, Priority.NEVER);

        footerContainer.getChildren().addAll(
                createFooterItem("VOLTAR", "ESC"),
                createFooterItem("SELECIONAR", "ENTER")

        );
    }

    private HBox createFooterItem(String label, String key) {
        HBox box = new HBox(12);
        box.getStyleClass().add("footer-item");
        box.setAlignment(Pos.CENTER);

        Text labelText = new Text(label);
        labelText.getStyleClass().add("footer-label");

        StackPane keyContainer = new StackPane();
        keyContainer.getStyleClass().add("footer-key-container");
        Text keyText = new Text(key);
        keyText.getStyleClass().add("footer-key");
        keyContainer.getChildren().add(keyText);

        box.getChildren().addAll(labelText, keyContainer);
        return box;
    }

    private void setupLayout() {
        mainLayout.setMaxSize(1920, 1080);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setSpacing(40);
        mainLayout.setPadding(new Insets(20, 40, 20, 40));

        mainLayout.getChildren().addAll(footerContainer, titleContainer, cardsContainer);

        root.getChildren().add(mainLayout);
    }

    private void setupCardActions() {
        cardActions.clear();
        cardActions.add(e -> mediator.emit(UiEvents.START_SINGLE_PLAYER, null));
        cardActions.add(e -> mediator.emit(UiEvents.START_LOCAL_MULTIPLAYER, null));
    }

    private void selectCard(int index) {
        if (selectedIndex != index) {
            selectedIndex = index;
            updateCardSelection();
        }
    }

    private void updateCardSelection() {
        for (int i = 0; i < modeCards.size(); i++) {
            GameModeCard card = modeCards.get(i);
            card.setSelected(i == selectedIndex);
        }
    }


    private void setupKeyNavigation() {
        root.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case LEFT:
                    navigateLeft();
                    break;
                case RIGHT:
                    navigateRight();
                    break;
                case ENTER:
                case SPACE:
                    activateSelectedCard();
                    break;
                case ESCAPE:
                    mediator.emit(UiEvents.BACK_TO_MENU, null);
                    break;
                default:
                    break;
            }
        });
        root.setFocusTraversable(true);
        root.requestFocus();
    }

    private void navigateLeft() {
        selectedIndex = (selectedIndex - 1 + modeCards.size()) % modeCards.size();
        updateCardSelection();
    }

    private void navigateRight() {
        selectedIndex = (selectedIndex + 1) % modeCards.size();
        updateCardSelection();
    }

    private void activateSelectedCard() {
        if (selectedIndex < cardActions.size()) {
            cardActions.get(selectedIndex).handle(null);
        }
    }

    private void playEntryAnimations() {
        ParallelTransition titleAnimation = createTitleEntryAnimation();
        ParallelTransition cardsAnimation = createCardsEntryAnimation();
        ParallelTransition footerAnimation = createFooterEntryAnimation();

        titleAnimation.play();
        cardsAnimation.play();
        footerAnimation.play();
    }

    private ParallelTransition createTitleEntryAnimation() {
        titleContainer.setOpacity(0);
        titleContainer.setTranslateY(-50);

        return new ParallelTransition(
                createFadeTransition(titleContainer, Duration.millis(800)),
                createSlideTransition(titleContainer, Duration.millis(800), -50)
        );
    }

    private ParallelTransition createCardsEntryAnimation() {
        cardsContainer.setOpacity(0);
        cardsContainer.setTranslateY(50);

        return new ParallelTransition(
                createFadeTransition(cardsContainer, Duration.millis(800)),
                createSlideTransition(cardsContainer, Duration.millis(800), 50)
        );
    }

    private ParallelTransition createFooterEntryAnimation() {
        footerContainer.setOpacity(0);
        footerContainer.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(1000), footerContainer);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(1000), footerContainer);
        slide.setFromY(30);
        slide.setToY(0);

        return new ParallelTransition(fade, slide);
    }

    private FadeTransition createFadeTransition(Parent node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        return fade;
    }

    private TranslateTransition createSlideTransition(Parent node, Duration duration, double fromValue) {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromY(fromValue);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        return slide;
    }

    public void destroy() {
        if (cardScaleTransition != null) {
            cardScaleTransition.stop();
            cardScaleTransition = null;
        }

        modeCards.forEach(card -> {
            card.setOnMouseClicked(null);
            card.setOnMouseEntered(null);
            card.setOnMouseExited(null);
        });

        modeCards.clear();
        cardActions.clear();
        
        if (dynamicBackground != null) {
            dynamicBackground.destroy();
            dynamicBackground = null;
        }
        
        cardsContainer.getChildren().clear();
        titleContainer.getChildren().clear();
        footerContainer.getChildren().clear();
        root.getChildren().clear();

        titleText = null;
    }

    public Parent getNode() {
        return root;
    }

    /**
     * Configura cache nos elementos principais da tela para melhorar performance
     */
    private void setupCache() {
        if (!GameConfig.ENABLE_UI_CACHE) return;

        if (titleText != null) {
            titleText.setCache(true);
            titleText.setCacheHint(GameConfig.getCacheHint());
        }

        for (GameModeCard card : modeCards) {
            if (card != null) {
                card.setCache(true);
                card.setCacheHint(GameConfig.getCacheHint());
            }
        }
    }

    private static class GameModeData {
        final String title;
        final String subtitle;
        final String description;
        final String accentColor;
        final String icon;

        GameModeData(String title, String description, String accentColor, String icon) {
            this.title = title;
            this.subtitle = "";
            this.description = description;
            this.accentColor = accentColor;
            this.icon = icon;
        }
    }

    private static class GameModeCard extends StackPane {
        private final GameModeData data;
        private final Rectangle selectedBorder;
        private final Rectangle iconBackground;
        private final VBox textContent;
        private boolean isSelected = false;
        private ScaleTransition scaleTransition;

        public GameModeCard(GameModeData data, int index) {
            this.data = data;


            setPrefSize(280, 360);
            setMaxSize(280, 360);
            setMinSize(280, 360);

            Rectangle background = new Rectangle(280, 360);
            background.setArcWidth(20);
            background.setArcHeight(20);
            background.setFill(createCardGradient());

            selectedBorder = new Rectangle(280, 360);
            selectedBorder.setArcWidth(20);
            selectedBorder.setArcHeight(20);
            selectedBorder.setFill(Color.TRANSPARENT);
            selectedBorder.setStroke(Color.web(data.accentColor));
            selectedBorder.setStrokeWidth(3);
            selectedBorder.setVisible(false);

            VBox mainContent = new VBox(20);
            mainContent.setAlignment(Pos.CENTER);
            mainContent.setPadding(new Insets(30, 20, 30, 20));

            iconBackground = new Rectangle(80, 80);
            iconBackground.setArcWidth(15);
            iconBackground.setArcHeight(15);
            iconBackground.setFill(createIconGradient());
            iconBackground.setEffect(new DropShadow(10, Color.web(data.accentColor, 0.4)));
            iconBackground.getStyleClass().add("game-mode-card-icon");

            Text iconText = new Text(data.icon);
            iconText.getStyleClass().add("game-mode-card-icon-text");

            StackPane iconContainer = new StackPane();
            iconContainer.getChildren().addAll(iconBackground, iconText);
            iconContainer.setAlignment(Pos.CENTER);

            textContent = createTextContent();

            mainContent.getChildren().addAll(iconContainer, textContent);

            getChildren().addAll(background, selectedBorder, mainContent);

            setupAnimation();
        }

        private LinearGradient createCardGradient() {
            return new LinearGradient(0, 0, 0, 1, true, null,
                    new Stop(0, Color.web("#2C3E50")),
                    new Stop(0.3, Color.web("#34495E")),
                    new Stop(0.7, Color.web("#2C3E50")),
                    new Stop(1, Color.web("#1B2631"))
            );
        }

        private LinearGradient createIconGradient() {
            return new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0, Color.web(data.accentColor)),
                    new Stop(0.5, Color.web(data.accentColor, 0.8)),
                    new Stop(1, Color.web(data.accentColor, 0.6))
            );
        }

        private VBox createTextContent() {
            VBox content = new VBox(8);
            content.setAlignment(Pos.CENTER);

            Text title = new Text(data.title);
            title.setEffect(new DropShadow(3, Color.BLACK));
            title.getStyleClass().add("game-mode-card-title");

            Text description = new Text(data.description);
            description.setWrappingWidth(200);
            description.setEffect(new DropShadow(2, Color.BLACK));
            description.getStyleClass().add("game-mode-card-description");

            Rectangle selectionIndicator = new Rectangle(40, 4);
            selectionIndicator.setFill(Color.web(data.accentColor));
            selectionIndicator.setArcWidth(2);
            selectionIndicator.setArcHeight(2);
            selectionIndicator.setVisible(false);

            content.getChildren().addAll(title, description, selectionIndicator);
            return content;
        }

        private void setupAnimation() {
            scaleTransition = new ScaleTransition(CARD_SCALE_DURATION, this);
            scaleTransition.setFromX(1.0);
            scaleTransition.setFromY(1.0);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);

            setOnMouseEntered(e -> {
                if (!isSelected) {
                    scaleTransition.setRate(1);
                    scaleTransition.play();
                }
            });

            setOnMouseExited(e -> {
                if (!isSelected) {
                    scaleTransition.setRate(-1);
                    scaleTransition.play();
                }
            });
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
            selectedBorder.setVisible(selected);

            if (textContent.getChildren().size() > 2) {
                Rectangle indicator = (Rectangle) textContent.getChildren().get(2);
                indicator.setVisible(selected);
            }

            if (selected) {
                setScaleX(1.05);
                setScaleY(1.05);
                
                ScaleTransition iconScale = new ScaleTransition(Duration.millis(300), iconBackground);
                iconScale.setToX(1.1);
                iconScale.setToY(1.1);
                iconScale.play();
            } else {
                setScaleX(1.0);
                setScaleY(1.0);
                setEffect(null);
                
                ScaleTransition iconScale = new ScaleTransition(Duration.millis(200), iconBackground);
                iconScale.setToX(1.0);
                iconScale.setToY(1.0);
                iconScale.play();
            }
        }
    }
}