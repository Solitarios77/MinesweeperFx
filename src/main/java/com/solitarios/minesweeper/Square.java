package com.solitarios.minesweeper;

public class Square {
	public static final int BOMB_ID = -1; // 炸弹
	public static final int BLANK_ID = 0; // 空地
	private SquareState state; // 格子状态
	private int bombs; // 炸弹数
	
	public Square() {
		this.state = SquareState.COVERED;
		this.bombs = 0;
	}

	public Square(SquareState state, int bombs) {
		super();
		this.state = state;
		this.bombs = bombs;
	}

	public SquareState getState() {
		return state;
	}

	public void setState(SquareState state) {
		this.state = state;
	}

	public int getBombs() {
		return bombs;
	}
	
	public void setBombs(int value) {
		// 判断值合不合法
		if (value > 8 || value < -1) {
			return;
		}
		this.bombs = value;
	}

	@Override
	public String toString() {
		return "Square [state=" + state + ", bombs=" + bombs + "]";
	}
}
