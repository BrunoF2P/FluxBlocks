package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Classe base para todas as telas do jogo.
 * Fornece transições padronizadas e métodos utilitários comuns.
 */
public abstract class BaseScreen implements Screen {
    
    protected static final Duration FADE_DURATION = Duration.millis(600);
    protected static final Duration SLIDE_DURATION = Duration.millis(800);
    protected static final Duration FOOTER_DURATION = Duration.millis(1000);
    protected static final Duration SEQUENCE_DELAY = Duration.millis(200);
    
    protected static final double SLIDE_DISTANCE_X = 80;
    protected static final double SLIDE_DISTANCE_Y = 30;
    
    /**
     * Configura o background dinâmico padrão para uma tela.
     * @param root O StackPane raiz da tela
     * @return O DynamicBackground criado
     */
    protected DynamicBackground setupStandardBackground(StackPane root) {
        DynamicBackground dynamicBackground = new DynamicBackground(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        root.getChildren().add(dynamicBackground.getCanvas());
        return dynamicBackground;
    }
    
    /**
     * Cria um background dinâmico sem adicionar ao root.
     * Útil para telas que precisam customizar o background.
     * @return O DynamicBackground criado
     */
    protected DynamicBackground createStandardBackground() {
        return new DynamicBackground(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
    }
    
    /**
     * Configura o cache padrão para uma tela.
     * @param root O StackPane raiz da tela
     */
    protected void setupStandardCache(StackPane root) {
        root.setCache(true);
        root.setCacheHint(javafx.scene.CacheHint.SPEED);
    }
    
    /**
     * Configura a navegação por teclado padrão para uma tela.
     * @param root O StackPane raiz da tela
     */
    protected void setupStandardKeyNavigation(StackPane root) {
        root.setFocusTraversable(true);
        root.requestFocus();
    }
    
    /**
     * Cria um título padrão simples.
     * @param titleText O texto do título
     * @param styleClass A classe CSS para o título
     * @return O VBox contendo o título
     */
    protected VBox createStandardTitle(String titleText, String styleClass) {
        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);

        Text title = new Text(titleText);
        title.getStyleClass().add(styleClass);
       
        titleBox.getChildren().add(title);
        return titleBox;
    }
    
    /**
     * Cria um título padrão simples com classe CSS padrão.
     * @param titleText O texto do título
     * @return O VBox contendo o título
     */
    protected VBox createStandardTitle(String titleText) {
        return createStandardTitle(titleText, "title-flux");
    }
    
    /**
     * Executa as animações de entrada padrão para uma tela.
     * @param titleContainer Container do título
     * @param mainContainer Container principal do conteúdo
     * @param footerContainer Container do rodapé
     */
    protected void playStandardEntryAnimations(Region titleContainer, Region mainContainer, Region footerContainer) {
        ParallelTransition titleAnimation = createStandardTitleAnimation(titleContainer);
        ParallelTransition mainAnimation = createStandardMainAnimation(mainContainer);
        ParallelTransition footerAnimation = createStandardFooterAnimation(footerContainer);

        SequentialTransition sequence = new SequentialTransition(
            titleAnimation,
            new PauseTransition(SEQUENCE_DELAY),
            mainAnimation,
            new PauseTransition(SEQUENCE_DELAY),
            footerAnimation
        );

        sequence.play();
    }
    
    /**
     * Executa animações de entrada para telas com layout específico.
     * @param containers Array de containers para animar
     */
    protected void playCustomEntryAnimations(Region... containers) {
        if (containers.length == 0) return;
        
        SequentialTransition sequence = new SequentialTransition();
        
        for (int i = 0; i < containers.length; i++) {
            Region container = containers[i];
            ParallelTransition animation = createStandardMainAnimation(container);
            
            sequence.getChildren().add(animation);
            
            if (i < containers.length - 1) {
                sequence.getChildren().add(new PauseTransition(SEQUENCE_DELAY));
            }
        }
        
        sequence.play();
    }
    
    /**
     * Cria animação padrão para o título.
     */
    protected ParallelTransition createStandardTitleAnimation(Region titleContainer) {
        if (titleContainer == null) return new ParallelTransition();
        
        titleContainer.setOpacity(0);
        titleContainer.setTranslateX(-SLIDE_DISTANCE_X);

        return new ParallelTransition(
            createFadeTransition(titleContainer, FADE_DURATION),
            createSlideTransition(titleContainer, SLIDE_DURATION, -SLIDE_DISTANCE_X)
        );
    }
    
    /**
     * Cria animação padrão para o conteúdo principal.
     */
    protected ParallelTransition createStandardMainAnimation(Region mainContainer) {
        if (mainContainer == null) return new ParallelTransition();
        
        mainContainer.setOpacity(0);
        mainContainer.setTranslateX(SLIDE_DISTANCE_X);

        return new ParallelTransition(
            createFadeTransition(mainContainer, FADE_DURATION),
            createSlideTransition(mainContainer, SLIDE_DURATION, SLIDE_DISTANCE_X)
        );
    }
    
    /**
     * Cria animação padrão para o rodapé.
     */
    protected ParallelTransition createStandardFooterAnimation(Region footerContainer) {
        if (footerContainer == null) return new ParallelTransition();
        
        footerContainer.setOpacity(0);
        footerContainer.setTranslateY(SLIDE_DISTANCE_Y);

        FadeTransition fade = new FadeTransition(FOOTER_DURATION, footerContainer);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(FOOTER_DURATION, footerContainer);
        slide.setFromY(SLIDE_DISTANCE_Y);
        slide.setToY(0);

        return new ParallelTransition(fade, slide);
    }
    
    /**
     * Cria uma transição de fade padrão.
     */
    protected FadeTransition createFadeTransition(Parent node, Duration duration) {
        if (node == null) return new FadeTransition();
        
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        return fade;
    }
    
    /**
     * Cria uma transição de slide horizontal padrão.
     */
    protected TranslateTransition createSlideTransition(Parent node, Duration duration, double fromValue) {
        if (node == null) return new TranslateTransition();
        
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromX(fromValue);
        slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        return slide;
    }
    
    /**
     * Cria uma transição de slide vertical padrão.
     */
    protected TranslateTransition createSlideTransitionY(Parent node, Duration duration, double fromValue) {
        if (node == null) return new TranslateTransition();
        
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromY(fromValue);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        return slide;
    }
    
    /**
     * Cria uma animação de entrada com fade e slide customizados.
     */
    protected ParallelTransition createCustomEntryAnimation(Region container, double slideDistance, Duration duration) {
        if (container == null) return new ParallelTransition();
        
        container.setOpacity(0);
        container.setTranslateX(slideDistance);

        return new ParallelTransition(
            createFadeTransition(container, duration),
            createSlideTransition(container, duration, slideDistance)
        );
    }
    
    /**
     * Cria uma animação de entrada com fade e slide vertical customizados.
     */
    protected ParallelTransition createCustomEntryAnimationY(Region container, double slideDistance, Duration duration) {
        if (container == null) return new ParallelTransition();
        
        container.setOpacity(0);
        container.setTranslateY(slideDistance);

        return new ParallelTransition(
            createFadeTransition(container, duration),
            createSlideTransitionY(container, duration, slideDistance)
        );
    }
    
    /**
     * Método abstrato que deve ser implementado pelas subclasses.
     * Deve retornar o nó raiz da tela.
     */
    public abstract Parent getNode();
    
    /**
     * Método abstrato para limpeza de recursos.
     */
    public abstract void destroy();
    
    // Implementação dos métodos da interface Screen
    
    @Override
    public void initialize() {
        // Implementação padrão vazia - pode ser sobrescrita pelas subclasses
    }
    
    @Override
    public void show() {
        // Implementação padrão vazia - pode ser sobrescrita pelas subclasses
    }
    
    @Override
    public void hide() {
        // Implementação padrão vazia - pode ser sobrescrita pelas subclasses
    }
    
    @Override
    public void update() {
        // Implementação padrão vazia - pode ser sobrescrita pelas subclasses
    }
    
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public boolean isActive() {
        return true; // Sempre ativo por padrão
    }
} 