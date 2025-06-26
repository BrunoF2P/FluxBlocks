package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import com.uneb.fluxblocks.ui.components.FooterComponent;
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
public class GameModeScreen extends BaseScreen {
    private final GameMediator mediator;
    private final StackPane root;
    private final VBox mainLayout;
    private final VBox titleContainer;
    private final HBox cardsContainer;
    private final FooterComponent footerContainer;
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
        this.footerContainer = new FooterComponent(new String[][] {
            {"VOLTAR", "ESC"},
            {"SELECIONAR", "ENTER"}
        }, Pos.CENTER);

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
        dynamicBackground = setupStandardBackground(root);
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
        VBox.setVgrow(footerContainer, Priority.NEVER);
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
        setupStandardKeyNavigation(root);
        
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
                    activateSelectedCard();
                    break;
                case ESCAPE:
                    mediator.emit(UiEvents.BACK_TO_MENU, null);
                    break;
                default:
                    break;
            }
        });
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
        playStandardEntryAnimations(titleContainer, cardsContainer, footerContainer);
    }

    @Override
    public void destroy() {
        if (cardScaleTransition != null) {
            cardScaleTransition.stop();
            cardScaleTransition = null;
        }

        if (dynamicBackground != null) {
            dynamicBackground = null;
        }

        if (titleText != null) {
            titleText = null;
        }

        if (root != null) {
            root.getChildren().clear();
        }

        modeCards.clear();
        cardActions.clear();
    }

    @Override
    public Parent getNode() {
        return root;
    }

    private void setupCache() {
        setupStandardCache(root);
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
            this.selectedBorder = new Rectangle(280, 360);
            this.iconBackground = new Rectangle(80, 80);
            this.textContent = createTextContent();

            setupCard();
            setupAnimation();
        }

        private void setupCard() {
            setPrefSize(280, 360);
            setMaxSize(280, 360);
            setMinSize(280, 360);
            getStyleClass().add("game-mode-card");

            Rectangle background = new Rectangle(280, 360);
            background.setArcWidth(20);
            background.setArcHeight(20);
            background.setFill(createCardGradient());

            // Borda de seleção
            selectedBorder.setFill(Color.TRANSPARENT);
            selectedBorder.setStroke(Color.web(data.accentColor));
            selectedBorder.setStrokeWidth(3);
            selectedBorder.setArcWidth(20);
            selectedBorder.setArcHeight(20);
            selectedBorder.setVisible(false);

            // Background do ícone
            iconBackground.setFill(createIconGradient());
            iconBackground.setArcWidth(15);
            iconBackground.setArcHeight(15);
            iconBackground.setEffect(new DropShadow(10, Color.web(data.accentColor, 0.4)));
            iconBackground.getStyleClass().add("game-mode-card-icon");

            VBox mainContent = new VBox(20);
            mainContent.setAlignment(Pos.CENTER);
            mainContent.setPadding(new Insets(30, 20, 30, 20));

            StackPane iconContainer = new StackPane();
            iconContainer.getChildren().addAll(iconBackground);
            iconContainer.setAlignment(Pos.CENTER);

            Text iconText = new Text(data.icon);
            iconText.getStyleClass().add("game-mode-card-icon-text");
            iconContainer.getChildren().add(iconText);

            mainContent.getChildren().addAll(iconContainer, textContent);

            getChildren().addAll(background, selectedBorder, mainContent);
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