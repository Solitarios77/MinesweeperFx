package com.solitarios.model;

import com.solitarios.minesweeper.GameLevel;
import com.solitarios.minesweeper.Minesweeper;
import com.solitarios.minesweeper.GameState;
import org.junit.jupiter.api.Test;

import java.util.Scanner;
import java.util.Set;

public class Test2 {

    private Minesweeper minesweeper = new Minesweeper();

    @Test
    public void test1() {
        Scanner scanner = new Scanner(System.in);
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
//                        gameState = minesweeper.exposeSquare(row, col);
                    }
                    case 2 -> {
                        minesweeper.placeFlag(row, col);
                    }
                }
            }
            if (gameState == GameState.WON) {
                System.out.println("恭喜胜利！");
            }else if (gameState == GameState.FAILED) {
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
