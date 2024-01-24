package chessCode;

enum Player {
    WHITE,
    BLACK;

    public Player opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}

enum Rank {
	KING,
	QUEEN,
	BISHOP,
	ROOK,
	KNIGHT,
	PAWN,
}

public class ChessPiece {
	private final int col;
	private final int row;
	private final Player player;
	private final Rank rank;
	private final String imgName;
	private boolean hasMoved;
	
	public ChessPiece(int col, int row, Player player, Rank rank, String imgName) {
		super();
		this.col = col;
		this.row = row;
		this.player = player;
		this.rank = rank;
		this.imgName = imgName;
		this.hasMoved = false;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	public Player getPlayer() {
		return player;
	}

	public Rank getRank() {
		return rank;
	}

	public String getImgName() {
		return imgName;
	}
	
	public boolean hasMoved() {
        return hasMoved;
    }

    // Method to set the piece as moved
    public void setMoved() {
        this.hasMoved = true;
    }
}
