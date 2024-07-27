package com.solitarios.minesweeper;

import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Minesweeper minesweeper = new Minesweeper();
        Set<String> gameLevelKey = minesweeper.getGameLevelKey();
        while (true) {
            System.out.println("请选择游戏难度：");
            for (String key : gameLevelKey) {
                System.out.print(key + " ");
            }
            System.out.println();
            String chose = scanner.nextLine();
            if (!gameLevelKey.contains(chose)) {
                continue;
            }
            GameLevel gameLevel = minesweeper.getGameLevelValue(chose);
            minesweeper.setGameLevel(gameLevel);
            minesweeper.initBoard();
            GameState gameState = GameState.PLAYING;
            boolean isNewGame = true;
            while (gameState == GameState.PLAYING) {
                minesweeper.printBoard("GAME");
                System.out.println("请选择行数：");
                int row = scanner.nextInt();
                System.out.println("请选择列数：");
                int col = scanner.nextInt();
                System.out.println("请选择操作（1打开，2插旗）：");
                int select = scanner.nextInt();
                switch (select) {
                    case 1 -> {
                        if (isNewGame) {
                            minesweeper.placeBombs(row, col);
                            isNewGame = false;
                        }
                        minesweeper.exposeSquare(row, col);
                        gameState = minesweeper.getGameState();
                    }
                    case 2 -> {
                        minesweeper.placeFlag(row, col);
                    }
                }
            }
            minesweeper.printBoard("GAME");
            if (gameState == GameState.WON) {
                System.out.println("恭喜胜利！");
            } else if (gameState == GameState.FAILED) {
                System.out.println("不幸，失败了！");
            }
            System.out.println("是否继续游戏？（1继续，2结束）：");
            int isEnd = scanner.nextInt();
            if (isEnd == 2) {
                break;
            }
        }
    }
}
