package com.solitarios.controller;

import com.solitarios.minesweeper.*;
import com.solitarios.model.GameTimerModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class MainController implements Initializable {
    /*
     * 默认图片
     * */

    // 棋子
    private static final Image FLAG_IMAGE = new Image("file:src/main/resources/com/solitarios/image/flag.png",
            14, 14, true, false);
    // 炸弹
    private static final Image BOMB_IMAGE = new Image("file:src/main/resources/com/solitarios/image/bomb.png",
            14, 14, true, false);
    // 笑脸
    private static final Image SMILE_IMAGE = new Image("file:src/main/resources/com/solitarios/image/smile.png");
    // 哭脸
    private static final Image CRY_IMAGE = new Image("file:src/main/resources/com/solitarios/image/cry.png");
    // 酷脸
    private static final Image COOL_IMAGE = new Image("file:src/main/resources/com/solitarios/image/cool.png");

    /*
     * 扫雷相关
     * */
    // 扫雷服务
    private Minesweeper minesweeper = new Minesweeper();
    // 计时器模型
    private GameTimerModel gameTimerModel = new GameTimerModel();
    // 判断是否为一局新游戏
    private boolean isNewGame = true;
    /*
     * 界面组件
     * */
    // 游戏操作区
    @FXML
    private GridPane gameArea;
    // 用来阻止用户操作
    @FXML
    private Label coverLabel;
    // 游戏状态按钮
    @FXML
    private Button faceButton;
    // 棋子数量标签
    @FXML
    private Label flagNumberLabel;
    // 计时器标签
    @FXML
    private Label timeNumberLabel;
    // 游戏难度组
    @FXML
    private ToggleGroup gameDifficultyGroup;
    // 自定义难度区
    @FXML
    private HBox customLevelHBox;
    // 行输入框
    @FXML
    private TextField rowsTextField;
    // 列输入框
    @FXML
    private TextField columnsTextField;
    // 炸弹数输入框
    @FXML
    private TextField bombsTextField;
    // 剩余棋子数，用来绑定
    private IntegerProperty flagsProperty = new SimpleIntegerProperty();
    // 计数器，用来绑定
    private IntegerProperty timeProperty = new SimpleIntegerProperty(0);
    // 主窗体，用来动态变动宽高
    private Stage stage;

    // 有参构造，给主窗体赋值
    public MainController(Stage stage) {
        this.stage = stage;
    }

    // 控制器初始化
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 为游戏状态设置初始值
        faceButton.setGraphic(new ImageView(SMILE_IMAGE));
        // 初始化时先创建默认难度的棋盘
        createAndInitBoard();
        // 绑定棋子数量
        flagNumberLabel.textProperty().bind(flagsProperty.asString("%03d"));
        // 绑定计数器
        timeNumberLabel.textProperty().bind(gameTimerModel.gameTimeProperty().asString("%03d"));
        // 窗体焦点绑定
        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (minesweeper.getGameState() == GameState.PLAYING && !isNewGame) {
                if (newValue.booleanValue()) {
                    gameTimerModel.restart();
                } else {
                    gameTimerModel.pause();
                }
            }
        });
        // 数字过滤器
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            if (change.getControlNewText().matches("^[1-9]\\d*$")) {
                return change;
            } else if (change.getControlNewText().isEmpty()) {
                return change;
            }
            return null;
        };
        // 添加过滤器
        rowsTextField.setTextFormatter(new TextFormatter<>(integerFilter));
        columnsTextField.setTextFormatter(new TextFormatter<>(integerFilter));
        bombsTextField.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    // 初始化游戏区域
    private void initGameArea(Square[][] board) {
        // 清空游戏区域的旧按钮
        gameArea.getChildren().clear();
        // 遍历棋盘
        for (int i = 1; i < board.length - 1; i++) {
            for (int j = 1; j < board[i].length - 1; j++) {
                // 创建按钮
                ToggleButton button = new ToggleButton();
                button.setPrefSize(30, 30);
                int row = i;
                int col = j;
                // 添加点击事件
                button.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        // 左键点击
                        if (isNewGame) {
                            // 如果是新游戏，开始放置炸弹
                            minesweeper.placeBombs(row, col);
                            isNewGame = false;
                            // 启动计时器
                            gameTimerModel.start();
                        }
                        // 打开方格
                        minesweeper.exposeSquare(row, col);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        // 右键点击
                        // 放置棋子
                        minesweeper.placeFlag(row, col);
                        // 设置当前棋子数量
                        flagsProperty.set(minesweeper.getFlags());
                    }
                    // 重绘棋盘
                    drawGameArea(minesweeper.getBoard());
                    // 重置焦点
                    stage.requestFocus();
                    // 如果游戏状态不是正在进行中，结束游戏
                    if (minesweeper.getGameState() != GameState.PLAYING) {
                        gameOver();
                    }
                });
                // 将按钮添加进游戏区域
                gameArea.add(button, j, i);
            }
        }
    }

    // 游戏结束
    private void gameOver() {
        // 阻止用户继续操作
        coverLabel.setVisible(true);
        // 重置焦点
        faceButton.requestFocus();
        // 停止计时器
        gameTimerModel.pause();
    }

    // 重绘棋盘
    private void drawGameArea(Square[][] board) {
        for (int i = 1; i < board.length - 1; i++) {
            for (int j = 1; j < board[i].length - 1; j++) {
                // 计算集合下标
                int index = (i - 1) * minesweeper.getGameLevel().getColumns() + j - 1;
                // 获取指定按钮
                ToggleButton button = (ToggleButton) gameArea.getChildren().get(index);
                Square square = board[i][j];
                // 判断当前格子状态，根据格子状态改变按钮形态
                switch (square.getState()) {
                    case COVERED -> changeToggleButton(button, false, null, null);
                    case FLAGGED -> changeToggleButton(button, false, new ImageView(FLAG_IMAGE), null);
                    case EXPOSED -> {
                        if (square.getBombs() == Square.BOMB_ID) {
                            changeToggleButton(button, true, new ImageView(BOMB_IMAGE), null);
                        } else if (square.getBombs() == Square.BLANK_ID) {
                            changeToggleButton(button, true, null, null);
                        } else {
                            changeToggleButton(button, true, null, String.valueOf(square.getBombs()));
                        }
                    }
                }
            }
        }
    }

    // 改变按钮形态
    private void changeToggleButton(ToggleButton button, boolean isSelected, Node graphic, String text) {
        button.setSelected(isSelected);
        button.setGraphic(graphic);
        button.setText(text);
    }

    // 改变游戏难度
    @FXML
    private void changeCustomLevel() {
        Rectangle2D screenRectangle2D = Screen.getPrimary().getBounds();
        int rows = Integer.parseInt(rowsTextField.getText());
        if (rows > (screenRectangle2D.getHeight() - 120) / 30) {
            rows = (int) ((screenRectangle2D.getHeight() - 120) / 30 - 5);
        }
        int columns = Integer.parseInt(columnsTextField.getText());
        if (columns > (screenRectangle2D.getWidth() - 36) / 30) {
            columns = (int) ((screenRectangle2D.getWidth() - 36) / 30 - 5);
        }
        int bombs = Integer.parseInt(bombsTextField.getText());
        if (bombs > rows * columns) {
            bombs = (int) (rows * columns * 0.2);
            bombs = bombs >= 999 ? 999 : bombs;
        }
        minesweeper.setGameLevel(new GameLevel(rows, columns, bombs));
        // 先重新设置窗体的宽和高
        GameLevel gameLevel = minesweeper.getGameLevel();
        stage.setWidth(gameLevel.getColumns() * 30 + 36);
        stage.setHeight(gameLevel.getRows() * 30 + 120);
        // 重新生成棋盘以及重绘棋盘
        createAndInitBoard();
    }

    // 切换游戏难度
    @FXML
    private void changeGameLevel() {
        // 获得当前选中的按钮
        RadioMenuItem radioMenuItem = (RadioMenuItem) gameDifficultyGroup.getSelectedToggle();
        // 暂时先用按钮文本充当标识
        String level = radioMenuItem.getText();
        // 根据文本标识切换难度
        switch (level) {
            case "中等" -> {
                minesweeper.setGameLevel("MEDIUM");
                customLevelHBox.setVisible(false);
            }
            case "困难" -> {
                minesweeper.setGameLevel("HARD");
                customLevelHBox.setVisible(false);
            }
            case "自定义" -> {
                customLevelHBox.setVisible(true);
                minesweeper.setGameLevel(new GameLevel(15, 15, 30));
            }
            default -> {
                minesweeper.setGameLevel("EASY");
                customLevelHBox.setVisible(false);
            }
        }
        // 先重新设置窗体的宽和高
        GameLevel gameLevel = minesweeper.getGameLevel();
        stage.setWidth(gameLevel.getColumns() * 30 + 36);
        stage.setHeight(gameLevel.getRows() * 30 + 120);
        // 重新生成棋盘以及重绘棋盘
        createAndInitBoard();
    }

    // 游戏状态按钮，点击生成新游戏
    @FXML
    private void faceButtonOnAction() {
        // 开始新一局游戏
        faceButton.setGraphic(new ImageView(SMILE_IMAGE));
        // 重新生成棋盘以及重绘棋盘
        createAndInitBoard();
    }

    // 重新生成棋盘以及重绘棋盘
    private void createAndInitBoard() {
        isNewGame = true;
        minesweeper.initBoard();
        initGameArea(minesweeper.getBoard());
        coverLabel.setVisible(false);
        flagsProperty.set(minesweeper.getFlags());
        gameTimerModel.resetTime();
    }
}
