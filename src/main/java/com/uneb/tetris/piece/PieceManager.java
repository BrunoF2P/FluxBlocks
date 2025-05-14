package com.uneb.tetris.piece;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.tetris.board.GameBoard;
import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import javafx.util.Duration;

import java.util.List;

/**
 * Gerenciador das peças do Tetris, responsável por controlar o movimento,
 * rotação e posicionamento das peças no tabuleiro.
 *
 * <p>Esta classe implementa o padrão de projeto Component Controller, gerenciando
 * o ciclo de vida completo das peças do jogo (Tetrominos) e sua interação com o tabuleiro.
 * Utiliza o padrão Mediator para comunicação com outros componentes do jogo.</p>
 *
 * <p>Esta classe gerencia:
 * <ul>
 *   <li>Movimentação das peças (esquerda, direita, baixo)</li>
 *   <li>Rotação das peças com sistema de wall kick</li>
 *   <li>Sistema de soft drop e hard drop</li>
 *   <li>Visualização da peça fantasma</li>
 *   <li>Cálculo de pontuação</li>
 *   <li>Lock delay para permitir ajustes antes da peça ser fixada</li>
 *   <li>Verificação de game over quando não é possível posicionar nova peça</li>
 * </ul>
 *
 * <p>O sistema de pontuação segue as regras clássicas do Tetris, com bonificações
 * para linhas completadas simultaneamente e para soft/hard drops.</p>
 *
 * @author Bruno Bispo
 */
public class PieceManager {
    /** Mediador para comunicação com outros componentes do jogo */
    private final GameMediator mediator;

    /** Referência ao tabuleiro do jogo */
    private final GameBoard board;

    /** Peça atual em jogo */
    private Tetromino currentPiece;

    /** Próxima peça a entrar em jogo */
    private Tetromino nextPiece;

    /** Nível atual do jogo, usado para cálculos de pontuação e velocidade */
    private int currentLevel = 1;

    /** Tempo em milissegundos antes da peça ser fixada após pousar */
    private static final double LOCK_DELAY = 500.0;

    /** Tempo inicial entre rotações consecutivas (para evitar rotações muito rápidas) */
    private static final double ROTATE_INITIAL_DELAY = 100.0;

    /** Tempo entre rotações subsequentes após a primeira rotação */
    private static final double ROTATE_REPEAT_DELAY = 200.0;

    /** Timestamp da última rotação realizada */
    private double lastRotateTime = 0;

    /** Flag que indica se é a primeira rotação de uma sequência */
    private boolean isFirstRotate = true;

    /** Timer para controlar o lock delay */
    private double lockTimer = 0;

    /** Flag que indica se uma peça está aguardando para ser fixada */
    private boolean lockPending = false;

    /** Última posição Y onde a peça estava em posição de descanso */
    private int lastLandedY = -1;

    /** Flag que indica se o jogador está realizando soft drop */
    private boolean isSoftDropping = false;

    /** Distância percorrida durante o soft drop atual */
    private int softDropDistance = 0;

    /** Total de linhas eliminadas durante o jogo */
    private int linesClearedTotal = 0;

    /**
     * Cria um gerenciador de peças.
     *
     * <p>Inicializa o sistema de peças e registra os eventos necessários no mediador.
     * Este construtor também gera a primeira peça e configura o sistema de lock delay.</p>
     *
     * @param mediator O mediador para comunicação entre componentes do jogo
     * @param board O tabuleiro do jogo onde as peças serão posicionadas
     */
    public PieceManager(GameMediator mediator, GameBoard board) {
        this.mediator = mediator;
        this.board = board;

        initialize();
        registerEvents();
    }

    /**
     * Inicializa o sistema de peças, gerando a primeira peça e a próxima peça.
     */
    private void initialize() {
        nextPiece = TetrominoFactory.createRandomTetromino();
        nextPiece.setPosition(board.getWidth() / 2, 0);

        spawnNewPiece();
    }

    /**
     * Registra os eventos necessários no mediador para controle das peças.
     *
     * <p>Esta classe recebe eventos de entrada do usuário para controlar
     * o movimento das peças, além de eventos do jogo para atualizar
     * o nível e outros parâmetros.</p>
     */
    private void registerEvents() {
        mediator.receiver(GameEvents.GameplayEvents.MOVE_LEFT, unused -> moveLeft());
        mediator.receiver(GameEvents.GameplayEvents.MOVE_RIGHT, unused -> moveRight());
        mediator.receiver(GameEvents.GameplayEvents.MOVE_DOWN, unused -> moveDown());
        mediator.receiver(GameEvents.GameplayEvents.ROTATE, unused -> rotate());
        mediator.receiver(GameEvents.GameplayEvents.DROP, unused -> hardDrop());
        mediator.receiver(GameEvents.InputEvents.ROTATE_RESET, unused -> resetRotateDelay());
        mediator.receiver(GameEvents.UiEvents.LEVEL_UPDATE, level -> updateLevel((int)level));
        mediator.receiver(GameEvents.UiEvents.GAME_STARTED, unused -> {
            mediator.emit(GameEvents.UiEvents.NEXT_PIECE_UPDATE, nextPiece);
        });
        FXGL.getGameTimer().runAtInterval(this::checkLockDelay, Duration.millis(16.67)); // 60 FPS (1000ms/60)
    }

    /**
     * Atualiza o nível atual do jogo.
     *
     * <p>O nível influencia a velocidade de queda das peças e o cálculo de pontuação.</p>
     *
     * @param level O novo nível do jogo
     */
    private void updateLevel(int level) {
        this.currentLevel = level;
    }

    /**
     * Verifica se o tempo de lock delay da peça atual expirou.
     *
     * <p>Este método é chamado automaticamente a cada frame (~60FPS) via
     * o timer principal do FXGL. Quando o lock delay expira, a peça é fixada
     * no tabuleiro.</p>
     */
    private void checkLockDelay() {
        if (!lockPending) return;

        double currentTime = FXGL.getGameTimer().getNow();

        if (currentTime - lockTimer >= LOCK_DELAY / 1000.0) {
            lockPiece();
            lockPending = false;
        }
    }

    /**
     * Reinicia o temporizador de lock delay quando a peça é movida.
     *
     * <p>O lock delay é um mecanismo que permite ao jogador ajustar a posição
     * da peça antes que ela seja fixada no tabuleiro após tocar em outra peça
     * ou no fundo do tabuleiro.</p>
     */
    private void resetLockDelay() {
        if (isAtValidRestingPosition()) {
            int currentY = currentPiece.getY();

            if (currentY != lastLandedY) {
                lockTimer = FXGL.getGameTimer().getNow();
                lastLandedY = currentY;
            }
        } else {
            lockPending = false;
        }
    }

    /**
     * Verifica se a peça atual está em uma posição de descanso válida
     * (i.e., sobre outra peça ou no fundo do tabuleiro).
     *
     * @return true se a peça está em uma posição de descanso, false caso contrário
     */
    private boolean isAtValidRestingPosition() {
        int originalX = currentPiece.getX();
        int originalY = currentPiece.getY();

        currentPiece.move(0, 1);
        boolean wouldCollide = !isValidPosition(currentPiece);

        currentPiece.setPosition(originalX, originalY);

        return wouldCollide;
    }

    /**
     * Gera uma nova peça e a posiciona no topo do tabuleiro.
     *
     * <p>Este método também verifica a condição de game over (quando não é possível
     * posicionar uma nova peça). Quando uma nova peça é gerada, o lock delay é
     * reiniciado e a interface é atualizada para mostrar a próxima peça.</p>
     */
    public void spawnNewPiece() {
        currentPiece = nextPiece;

        nextPiece = TetrominoFactory.createRandomTetromino();
        nextPiece.setPosition(board.getWidth() / 2, 0);

        mediator.emit(GameEvents.UiEvents.NEXT_PIECE_UPDATE, nextPiece);

        if (!isValidPosition(currentPiece)) {
            mediator.emit(GameEvents.GameplayEvents.GAME_OVER, null);
            return;
        }

        lockPending = false;
        lastLandedY = -1;
        isSoftDropping = false;
        softDropDistance = 0;

        updateBoardWithCurrentPiece();
    }

    /**
     * Calcula a posição da peça fantasma (shadow piece).
     *
     * <p>A peça fantasma é uma representação visual da posição onde a peça atual
     * cairá se for realizado um hard drop. Ela ajuda o jogador a planejar seus
     * movimentos.</p>
     *
     * @return Uma cópia da peça atual na posição mais baixa possível
     */
    private Tetromino calculateShadowPiece() {
        if (currentPiece == null) return null;

        Tetromino shadow = TetrominoFactory.createTetromino(Tetromino.Type.values()[currentPiece.getType()]);
        shadow.setPosition(currentPiece.getX(), currentPiece.getY());

        for (int i = 0; i < currentPiece.getCells().size(); i++) {
            Cell originalCell = currentPiece.getCells().get(i);
            Cell shadowCell = shadow.getCells().get(i);
            shadowCell.setRelativeX(originalCell.getRelativeX());
            shadowCell.setRelativeY(originalCell.getRelativeY());
        }

        while (true) {
            shadow.move(0, 1);
            if (!isValidPosition(shadow)) {
                shadow.move(0, -1);
                break;
            }
        }

        return shadow;
    }

    /**
     * Atualiza o tabuleiro com a posição atual da peça e sua sombra.
     *
     * <p>Este método cria uma representação temporária do tabuleiro incluindo
     * a peça atual e sua sombra, e envia essa representação para a interface
     * gráfica através do mediador.</p>
     */
    private void updateBoardWithCurrentPiece() {
        if (currentPiece == null) return;

        int[][] grid = new int[board.getHeight()][board.getWidth()];

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.isValidPosition(x, y)) {
                    grid[y][x] = board.getCell(x, y);
                }
            }
        }

        Tetromino shadowPiece = calculateShadowPiece();
        if (shadowPiece != null) {
            for (Cell cell : shadowPiece.getCells()) {
                int x = cell.getX();
                int y = cell.getY();

                if (y >= 0 && y < board.getHeight() && x >= 0 && x < board.getWidth()) {
                    grid[y][x] = 8;  // 8 é o código para células da sombra
                }
            }
        }

        for (Cell cell : currentPiece.getCells()) {
            int x = cell.getX();
            int y = cell.getY();

            if (y >= 0 && y < board.getHeight() && x >= 0 && x < board.getWidth()) {
                grid[y][x] = cell.getType();
            }
        }

        mediator.emit(GameEvents.UiEvents.BOARD_UPDATE, grid);
    }

    /**
     * Verifica se uma peça está em uma posição válida no tabuleiro.
     *
     * <p>Uma posição é válida se todas as células da peça estão dentro dos
     * limites do tabuleiro e não colidem com outras células ocupadas.</p>
     *
     * @param piece A peça a ser verificada
     * @return true se a posição é válida, false caso contrário
     */
    private boolean isValidPosition(Tetromino piece) {
        if (piece == null) return false;

        for (Cell cell : piece.getCells()) {
            int x = cell.getX();
            int y = cell.getY();

            if (!board.isValidPosition(x, y)) {
                return false;
            }

            if (board.getCell(x, y) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fixa a peça atual na posição e gera uma nova peça.
     *
     * <p>Este método finaliza o movimento da peça atual, fixando-a no tabuleiro.
     * Em seguida, verifica se há linhas completadas, calcula a pontuação
     * correspondente e gera uma nova peça.</p>
     *
     * <p>A pontuação é calculada com base no número de linhas completadas
     * e no nível atual do jogo, seguindo as regras clássicas do Tetris.</p>
     */
    public void lockPiece() {
        if (currentPiece == null) return;

        List<Cell> cells = currentPiece.getCells();
        for (Cell cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            if (board.isValidPosition(x, y)) {
                board.setCell(x, y, cell.getType());
            }
        }

        if (isSoftDropping && softDropDistance > 0) {
            int softDropScore = 20 * currentLevel;
            mediator.emit(GameEvents.GameplayEvents.SCORE_UPDATED, softDropScore);
        }

        int linesCleared = board.removeCompletedLines();

        if (linesCleared > 0) {
            linesClearedTotal += linesCleared;
            int scoreIncrease = calculateScore(linesCleared);
            mediator.emit(GameEvents.GameplayEvents.SCORE_UPDATED, scoreIncrease);
        }

        spawnNewPiece();
    }

    /**
     * Calcula a pontuação baseado no número de linhas completadas.
     *
     * <p>A pontuação segue as regras clássicas do Tetris:
     * <ul>
     *   <li>1 linha: 40 * (nível + 1) pontos</li>
     *   <li>2 linhas: 100 * (nível + 1) pontos</li>
     *   <li>3 linhas: 300 * (nível + 1) pontos</li>
     *   <li>4 linhas (Tetris): 1200 * (nível + 1) pontos</li>
     * </ul>
     * </p>
     *
     * @param linesCleared Número de linhas completadas simultaneamente
     * @return Pontuação calculada com base nas linhas e nível atual
     */
    private int calculateScore(int linesCleared) {
        return switch (linesCleared) {
            case 1 -> 40 * (currentLevel + 1);
            case 2 -> 100 * (currentLevel + 1);
            case 3 -> 300 * (currentLevel + 1);
            case 4 -> 1200 * (currentLevel + 1);
            default -> 0;
        };
    }

    /**
     * Move a peça atual para a esquerda se possível.
     *
     * <p>Se o movimento for bem-sucedido, o lock delay é reiniciado
     * para permitir que o jogador ajuste a peça antes que ela seja fixada.</p>
     */
    public void moveLeft() {
        if (tryMove(-1, 0)) {
            resetLockDelay();
        }
    }

    /**
     * Move a peça atual para a direita se possível.
     *
     * <p>Se o movimento for bem-sucedido, o lock delay é reiniciado
     * para permitir que o jogador ajuste a peça antes que ela seja fixada.</p>
     */
    public void moveRight() {
        if (tryMove(1, 0)) {
            resetLockDelay();
        }
    }

    /**
     * Move a peça atual para baixo (soft drop).
     *
     * <p>O soft drop é um movimento que acelera a queda da peça enquanto
     * o jogador mantém pressionado o botão de movimento para baixo.
     * Cada célula percorrida gera pontos adicionais.</p>
     *
     * <p>Se o movimento não for possível (colisão), inicia o lock delay
     * para fixar a peça após um breve período.</p>
     */
    public void moveDown() {
        isSoftDropping = true;
        if (tryMove(0, 1)) {
            resetLockDelay();
            softDropDistance++;
        } else {
            if (!lockPending) {
                lockPending = true;
                lockTimer = FXGL.getGameTimer().getNow();
                lastLandedY = currentPiece.getY();
            }
        }
    }

    /**
     * Realiza um hard drop da peça atual.
     *
     * <p>O hard drop faz a peça cair instantaneamente até a posição mais baixa
     * possível e a fixa imediatamente no tabuleiro. Cada célula percorrida
     * durante o hard drop gera pontos adicionais (2 pontos por célula).</p>
     */
    public void hardDrop() {
        if (currentPiece == null) return;

        int distanceDropped = 0;
        while (tryMove(0, 1)) {
            distanceDropped++;
        }

        lockPiece();

        isSoftDropping = false;
        softDropDistance = 0;

        if (distanceDropped > 0) {
            mediator.emit(GameEvents.GameplayEvents.SCORE_UPDATED, distanceDropped * 2);
        }
    }

    /**
     * Rotaciona a peça atual no sentido horário.
     *
     * <p>Este método implementa o sistema de "wall kick", que tenta
     * ajustar a posição da peça após a rotação para evitar colisões
     * com as bordas do tabuleiro ou outras peças.</p>
     *
     * <p>O algoritmo de wall kick tenta as seguintes posições em ordem:
     * <ol>
     *   <li>Posição original após rotação</li>
     *   <li>1 célula à direita</li>
     *   <li>2 células à esquerda</li>
     *   <li>1 célula à direita e 1 para cima</li>
     * </ol>
     * Se nenhuma dessas posições for válida, a rotação é cancelada
     * (a peça retorna à orientação original).</p>
     *
     * <p>O método também implementa um sistema de delay entre rotações
     * para evitar rotações muito rápidas.</p>
     */
    public void rotate() {
        if (currentPiece == null) return;

        double currentTime = FXGL.getGameTimer().getNow() * 1000;

        double requiredDelay = isFirstRotate ? ROTATE_INITIAL_DELAY : ROTATE_REPEAT_DELAY;
        if (currentTime - lastRotateTime < requiredDelay) {
            return;
        }

        int originalX = currentPiece.getX();
        int originalY = currentPiece.getY();

        currentPiece.rotate();

        if (!isValidPosition(currentPiece)) {
            currentPiece.move(1, 0);
            if (!isValidPosition(currentPiece)) {
                currentPiece.move(-2, 0);
                if (!isValidPosition(currentPiece)) {
                    currentPiece.move(1, -1);
                    if (!isValidPosition(currentPiece)) {
                        currentPiece.rotate();
                        currentPiece.rotate();
                        currentPiece.rotate();
                        currentPiece.setPosition(originalX, originalY);
                        return;
                    }
                }
            }
        }

        updateBoardWithCurrentPiece();
        resetLockDelay();

        lastRotateTime = currentTime;
        isFirstRotate = false;
    }

    /**
     * Reinicia o delay de rotação, permitindo nova sequência de rotações.
     *
     * <p>Este método é chamado quando o jogador solta o botão de rotação,
     * permitindo que a próxima rotação seja considerada como "primeira"
     * para efeitos do delay entre rotações.</p>
     */
    public void resetRotateDelay() {
        isFirstRotate = true;
    }

    /**
     * Tenta mover a peça na direção especificada.
     *
     * <p>Se o movimento for válido, a posição da peça é atualizada
     * e o tabuleiro é redesenhado. Caso contrário, a peça permanece
     * na posição original.</p>
     *
     * @param deltaX Movimento horizontal (-1 para esquerda, 1 para direita)
     * @param deltaY Movimento vertical (1 para baixo)
     * @return true se o movimento foi possível, false caso contrário
     */
    private boolean tryMove(int deltaX, int deltaY) {
        if (currentPiece == null) return false;

        int originalX = currentPiece.getX();
        int originalY = currentPiece.getY();

        currentPiece.move(deltaX, deltaY);

        if (isValidPosition(currentPiece)) {
            updateBoardWithCurrentPiece();
            return true;
        } else {
            currentPiece.setPosition(originalX, originalY);
            return false;
        }
    }

    /**
     * Retorna a peça atual em jogo.
     *
     * @return A peça atual ou null se não houver peça
     */
    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Retorna a próxima peça que entrará em jogo.
     *
     * @return A próxima peça
     */
    public Tetromino getNextPiece() {
        return nextPiece;
    }

    /**
     * Retorna o total de linhas eliminadas durante o jogo.
     *
     * @return O número total de linhas eliminadas
     */
    public int getLinesClearedTotal() {
        return linesClearedTotal;
    }
}