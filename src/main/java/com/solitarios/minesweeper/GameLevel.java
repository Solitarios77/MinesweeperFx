package com.solitarios.minesweeper;

public class GameLevel {
	private int rows; // 行数
	private int columns; // 列数
	private int bombs; // 炸弹数
	
	public GameLevel() {
	}

	public GameLevel(int rows, int columns, int bombs) {
		this.rows = rows;
		this.columns = columns;
		this.bombs = bombs;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public int getBombs() {
		return bombs;
	}

	public void setBombs(int bombs) {
		this.bombs = bombs;
	}
}
