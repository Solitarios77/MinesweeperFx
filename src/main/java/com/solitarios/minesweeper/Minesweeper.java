package com.solitarios.minesweeper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Minesweeper {
    private static final Map<String, GameLevel> GAME_LEVEL_MAP = new HashMap<>() {
        {
            put("EASY", new GameLevel(9, 9, 10));
            put("MEDIUM", new GameLevel(16, 16, 40));
            put("HARD", new GameLevel(16, 30, 99));
        }
    }; // 游戏难度选集
    private Square[][] board; // 棋盘
    private GameLevel level; // 当前游戏难度
    private int flags; // 当前棋子数
    private GameState state; // 当前状态

    public Minesweeper() {
        level = GAME_LEVEL_MAP.get("EASY");
        initBoard();
    }

    public void initBoard() { // 初始化棋盘
        flags = level.getBombs();
        state = GameState.PLAYING;
        board = new Square[level.getRows() + 2][level.getColumns() + 2];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new Square();
            }
        }
    }

    public void printBoard(String pattern) {// 打印棋盘
        System.out.println("-------------------");
        for (int i = 1; i < board.length - 1; i++) {
            for (int j = 1; j < board[i].length - 1; j++) {
                System.out.print("|");
                Square square = board[i][j];
                switch (pattern) {
                    case "BOMB" -> {
                        System.out.print(getCharacter(square.getBombs()));
                    }
                    case "GAME" -> {
                        if (square.getState() == SquareState.COVERED) {
                            System.out.print("C");
                        } else if (square.getState() == SquareState.FLAGGED) {
                            System.out.print("F");
                        } else {
                            System.out.print(getCharacter(square.getBombs()));
                        }
                    }
                }
                if (j == board[i].length - 2) {
                    System.out.print("|");
                }
            }
            System.out.println();
            System.out.println("-------------------");
        }
    }

    private String getCharacter(int bombs) {
        if (bombs == Square.BOMB_ID) {
            return "*";
        } else if (bombs == Square.BLANK_ID) {
            return " ";
        } else {
            return String.valueOf(bombs);
        }
    }

    public void placeBombs(int row, int col) {
        // 判断值合不合法
        if (row <= 0 || row >= level.getRows() - 1 || col <= 0 || col >= level.getColumns() - 1) {
            throw new IllegalArgumentException("The row " + row + " or the col " + col + " is invalid value!");
        }
        // 放置炸弹，参数周围不放置炸弹
        int i = 0;
        int j = 0;
        int count = 0;

        Random random = new Random();

        while (count < level.getBombs()) {
            i = random.nextInt(level.getRows()) + 1;
            j = random.nextInt(level.getColumns()) + 1;
            // 如果在指定行、指定列附近，则跳过当前循环
            if ((i >= row - 1 && i <= row + 1) && (j >= col - 1 && j <= col + 1)) {
                continue;
            }
            // 如果已经放置炸弹了，则跳过当前循环
            if (board[i][j].getBombs() == Square.BOMB_ID) {
                continue;
            }
            // 放置炸弹
            board[i][j].setBombs(Square.BOMB_ID);
            count++;
            // 将周围的数加1
            addAroundNumber(i, j);
        }
    }

    // 对指定行、指定列周围的格子加1
    private void addAroundNumber(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                Square square = board[i][j];
                if (square.getBombs() == Square.BOMB_ID) {
                    continue;
                }
                square.setBombs(square.getBombs() + 1);
            }
        }
    }

    public void placeFlag(int row, int col) {
        // 给方格插旗
        Square square = board[row][col];
        if (square.getState() == SquareState.FLAGGED) {
            square.setState(SquareState.COVERED);
            flags++;
        } else if (flags > 0 && square.getState() == SquareState.COVERED) {
            square.setState(SquareState.FLAGGED);
            flags--;
        }
    }

    public void exposeSquare(int row, int col) {
        // 数值不合格，则直接返回
        if (row < 1 || row > level.getRows() || col < 1 || col > level.getColumns()) {
            return;
        }
        // 如果游戏已经结束，则直接返回
        if (state != GameState.PLAYING) {
            return;
        }
        Square square = board[row][col];
        // 如果已经插旗了，则直接返回
        if (square.getState() == SquareState.FLAGGED) {
            return;
        }
        // 如果是数字，判断周围的旗子数是否与该数字相等，相等则翻开周围的格子。
        if (square.getState() == SquareState.EXPOSED && square.getBombs() <= countAroundFlags(row, col)) {
            exposeAroundSquare(row, col);
            return;
        }
        // 翻开指定格子
        square.setState(SquareState.EXPOSED);
        // 结束条件3：如果是地雷，则返回失败；
        if (square.getBombs() == Square.BOMB_ID) {
            state = GameState.FAILED;
            gameFailed();
            return;
        }
        // 如果是空地，则翻开附近的格子（递归）；
        if (square.getBombs() == Square.BLANK_ID) {
            exposeAroundSquare(row, col);
            return;
        }

        // 如果剩下的方格数与剩下的旗子数相同，并且符合胜利条件，则返回胜利。
        if (flags == countRemainingCoveredSquare()) {
            state = GameState.WON;
            flaggedLeft();
        }
    }

    private void exposeAroundSquare(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i == row && j == col) {
                    continue;
                }
                Square square = board[i][j];
                if (square.getState() == SquareState.COVERED) {
                    exposeSquare(i, j);
                }
            }
        }
    }

    private int countAroundFlags(int row, int col) {
        // 计算方格周围的旗子数量
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i == row && j == col) {
                    continue;
                }
                if (board[i][j].getState() == SquareState.FLAGGED) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countRemainingCoveredSquare() {
        int count = 0;
        for (int i = 1; i < board.length - 1; i++) {
            for (int j = 1; j < board[i].length - 1; j++) {
                if (board[i][j].getState() == SquareState.COVERED) {
                    count++;
                }
            }
        }
        return count;
    }

    private void gameFailed() {
        for (int i = 1; i < board.length - 1; i++) {
            for (int j = 1; j < board[i].length - 1; j++) {
                Square square = board[i][j];
                if (square.getState() == SquareState.COVERED && square.getBombs() == Square.BOMB_ID) {
                    square.setState(SquareState.EXPOSED);
                }
            }
        }
    }

    private void flaggedLeft() {
        for (int i = 1; i < board.length - 1; i++) {
            for (int j = 1; j < board[i].length - 1; j++) {
                Square square = board[i][j];
                if (square.getState() == SquareState.COVERED) {
                    square.setState(SquareState.FLAGGED);
                }
            }
        }
    }

    public GameLevel getGameLevel() {
        return level;
    }

    public Set<String> getGameLevelKey() {
        return GAME_LEVEL_MAP.keySet();
    }

    public GameLevel getGameLevelValue(String key) {
        return GAME_LEVEL_MAP.get(key);
    }

    public void setGameLevel(GameLevel level) {
        this.level = level;
    }

    public void setGameLevel(String level) {
        if (GAME_LEVEL_MAP.containsKey(level)) {
            this.level = GAME_LEVEL_MAP.get(level);
        } else {
            this.level = GAME_LEVEL_MAP.get("EASY");
        }
    }

    public GameState getGameState() {
        return state;
    }

    public Square[][] getBoard() {
        return board;
    }

    public int getFlags() {
        return flags;
    }
}
