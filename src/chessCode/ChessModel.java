package chessCode;

import java.util.HashSet;
import java.util.Set;

public class ChessModel {
	private Set<ChessPiece> piecesBox = new HashSet<ChessPiece>();
	private Player playerInTurn = Player.WHITE;
	
	public Player getPlayerInTurn() {
	    return playerInTurn;
	}

	
	void reset() {
		piecesBox.removeAll(piecesBox);
		
		for (int i = 0; i < 2; i++) {
			piecesBox.add(new ChessPiece(0 + i * 7, 7, Player.BLACK, Rank.ROOK, ChessConstants.bRook));
			piecesBox.add(new ChessPiece(0 + i * 7, 0, Player.WHITE, Rank.ROOK, ChessConstants.wRook));

			piecesBox.add(new ChessPiece(1 + i * 5, 7, Player.BLACK, Rank.KNIGHT, ChessConstants.bKnight));
			piecesBox.add(new ChessPiece(1 + i * 5, 0, Player.WHITE, Rank.KNIGHT, ChessConstants.wKnight));

			piecesBox.add(new ChessPiece(2 + i * 3, 7, Player.BLACK, Rank.BISHOP, ChessConstants.bBishop));
			piecesBox.add(new ChessPiece(2 + i * 3, 0, Player.WHITE, Rank.BISHOP, ChessConstants.wBishop));
		}
		
		for (int i = 0; i < 8; i++) {
			piecesBox.add(new ChessPiece(i, 6, Player.BLACK, Rank.PAWN, ChessConstants.bPawn));
			piecesBox.add(new ChessPiece(i, 1, Player.WHITE, Rank.PAWN, ChessConstants.wPawn));
		}
		
		piecesBox.add(new ChessPiece(3, 7, Player.BLACK, Rank.QUEEN, ChessConstants.bQueen));
		piecesBox.add(new ChessPiece(3, 0, Player.WHITE, Rank.QUEEN, ChessConstants.wQueen));
		piecesBox.add(new ChessPiece(4, 7, Player.BLACK, Rank.KING, ChessConstants.bKing));
		piecesBox.add(new ChessPiece(4, 0, Player.WHITE, Rank.KING, ChessConstants.wKing));
		
		playerInTurn = Player.WHITE;
	}
	
	boolean isValidMove(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
	    // First check: if the destination is occupied by a friendly piece, the move is invalid.
	    ChessPiece destinationPiece = pieceAt(toCol, toRow);
	    if (destinationPiece != null && destinationPiece.getPlayer() == piece.getPlayer()) {
	        return false; // Cannot capture your own piece.
	    }
	    
	 // Simulate the move
	    Set<ChessPiece> backupPiecesBox = new HashSet<>(piecesBox);
	    executeMove(fromCol, fromRow, toCol, toRow, piece);

	    // Check if the move gets the king out of check
	    boolean isInCheckAfterMove = isKingInCheck(piece.getPlayer());

	    // Undo the simulated move
	    piecesBox = backupPiecesBox;

	    // If the king is still in check after the move, it's invalid
	    if(isInCheckAfterMove) {
	    	return false; 
	    }
	    // Now check based on the type of piece and ensure the path is valid for that piece.
	    switch (piece.getRank()) {
	        case PAWN:
	            return isValidPawnMove(piece, fromCol, fromRow, toCol, toRow);
	        case ROOK:
	            return isValidRookMove(piece, fromCol, fromRow, toCol, toRow);
	        case KNIGHT:
	            return isValidKnightMove(piece, fromCol, fromRow, toCol, toRow);
	        case BISHOP:
	            return isValidBishopMove(piece, fromCol, fromRow, toCol, toRow);
	        case QUEEN:
	            return isValidQueenMove(piece, fromCol, fromRow, toCol, toRow);
	        case KING:
	            return isValidKingMove(piece, fromCol, fromRow, toCol, toRow);
	        default:
	            return false;
	    }
	}

	
	boolean isValidRookMove(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
	    // Rook moves either horizontally or vertically
	    if (fromCol != toCol && fromRow != toRow) {
	        return false;
	    }

	    // Check if the path is clear
	    int colStep = Integer.compare(toCol, fromCol);
	    int rowStep = Integer.compare(toRow, fromRow);

	    int currentCol = fromCol + colStep;
	    int currentRow = fromRow + rowStep;
	    while (currentCol != toCol || currentRow != toRow) {
	        if (pieceAt(currentCol, currentRow) != null) {
	            return false;
	        }
	        currentCol += colStep;
	        currentRow += rowStep;
	    }

	    return true;
	}
	
	boolean isValidBishopMove(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
	    // Bishop moves diagonally, so absolute change in x and y should be equal
	    if (Math.abs(fromCol - toCol) != Math.abs(fromRow - toRow)) {
	        return false;
	    }

	    // Check if the path is clear
	    int colStep = Integer.compare(toCol, fromCol);
	    int rowStep = Integer.compare(toRow, fromRow);

	    int currentCol = fromCol + colStep;
	    int currentRow = fromRow + rowStep;
	    while (currentCol != toCol || currentRow != toRow) {
	        if (pieceAt(currentCol, currentRow) != null) {
	            return false;
	        }
	        currentCol += colStep;
	        currentRow += rowStep;
	    }

	    return true;
	}
	
	boolean isValidQueenMove(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
	    // Queen moves like a Rook or a Bishop
	    return isValidRookMove(piece, fromCol, fromRow, toCol, toRow) || 
	           isValidBishopMove(piece, fromCol, fromRow, toCol, toRow);
	}

	boolean isValidKnightMove(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
	    // Calculate the absolute difference in columns and rows
	    int colDiff = Math.abs(fromCol - toCol);
	    int rowDiff = Math.abs(fromRow - toRow);

	    // Check for L-shape: 2 squares one way and 1 square another way
	    return (colDiff == 2 && rowDiff == 1) || (colDiff == 1 && rowDiff == 2);
	}
	
	boolean isValidKingMove(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
	    // Standard move: one square in any direction
	    int colDiff = Math.abs(fromCol - toCol);
	    int rowDiff = Math.abs(fromRow - toRow);

	    if (colDiff <= 1 && rowDiff <= 1) {
	        return true;
	    }

	    // Castling move
	    return isValidCastling(piece, fromCol, fromRow, toCol, toRow);
	}

	
	boolean isValidCastling(ChessPiece king, int fromCol, int fromRow, int toCol, int toRow) {
	    // The king must not have moved, and must not be in check
	    if (king.hasMoved() || isKingInCheck(king.getPlayer())) {
	        return false;
	    }

	    // Determine the rook's position and the direction of the castling
	    int rookCol = toCol == 6 ? 7 : 0;
	    int rookDestCol = toCol == 6 ? 5 : 3;
	    int step = toCol == 6 ? 1 : -1;

	    // The destination column must be two columns over from the king's starting position
	    if (Math.abs(toCol - fromCol) != 2 || fromRow != toRow) {
	        return false;
	    }

	    ChessPiece rook = pieceAt(rookCol, fromRow);
	    // The rook must be present, must not have moved, and must be on the correct row
	    if (rook == null || rook.hasMoved() || rook.getRank() != Rank.ROOK || rook.getRow() != fromRow) {
	        return false;
	    }

	    // There must be no pieces between the king and the rook, and the squares must not be under attack
	    for (int col = fromCol + step; col != rookCol; col += step) {
	        if (pieceAt(col, fromRow) != null || isSquareUnderAttack(col, fromRow, king.getPlayer().opposite())) {
	            return false;
	        }
	    }

	    // The destination square for the king must not be under attack
	    if (isSquareUnderAttack(toCol, toRow, king.getPlayer().opposite())) {
	        return false;
	    }

	    return true;
	}


	boolean isSquareUnderAttack(int col, int row, Player attacker) {
	    for (ChessPiece piece : piecesBox) {
	        if (piece.getPlayer() == attacker) {
	            if (isValidAttackMove(piece, piece.getCol(), piece.getRow(), col, row)) {
	                return true;
	            }
	            // Check for the opponent king's attack range
	            if (piece.getRank() == Rank.KING && Math.abs(piece.getCol() - col) <= 1 && Math.abs(piece.getRow() - row) <= 1) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	boolean isValidAttackMove(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
	    switch (piece.getRank()) {
	        case PAWN:
	            // Handle pawn attack logic, which is different from its normal move
	            return isValidPawnAttack(piece, fromCol, fromRow, toCol, toRow);
	        case ROOK:
	            return isValidRookMove(piece, fromCol, fromRow, toCol, toRow);
	        case KNIGHT:
	            return isValidKnightMove(piece, fromCol, fromRow, toCol, toRow);
	        case BISHOP:
	            return isValidBishopMove(piece, fromCol, fromRow, toCol, toRow);
	        case QUEEN:
	            // Queen moves like a Rook or a Bishop
	            return isValidRookMove(piece, fromCol, fromRow, toCol, toRow) || 
	                   isValidBishopMove(piece, fromCol, fromRow, toCol, toRow);
	        // We exclude the KING case to avoid recursion
			default:
				break;
	    }
	    return false;
	}

	boolean isValidPawnAttack(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
	    int direction = piece.getPlayer() == Player.WHITE ? 1 : -1;
	    // Pawns attack diagonally one square
	    return Math.abs(fromCol - toCol) == 1 && (fromRow + direction == toRow);
	}




	
	boolean isValidPawnMove(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
	    int direction = piece.getPlayer() == Player.WHITE ? 1 : -1;

	    // Normal move
	    if (fromCol == toCol && pieceAt(toCol, toRow) == null) {
	        // Move one square forward
	        if (fromRow + direction == toRow) {
	            return true;
	        }
	        // Move two squares forward from start
	        if ((piece.getPlayer() == Player.WHITE && fromRow == 1 || piece.getPlayer() == Player.BLACK && fromRow == 6) && 
	            fromRow + 2 * direction == toRow && pieceAt(toCol, fromRow + direction) == null) {
	            return true;
	        }
	    }

	    // Capture move
	    if (Math.abs(fromCol - toCol) == 1 && fromRow + direction == toRow) {
	        ChessPiece target = pieceAt(toCol, toRow);
	        if (target != null && target.getPlayer() != piece.getPlayer()) {
	            return true;
	        }
	    }

	    return false;
	}
	
//	boolean isCheckmate(Player player) {
//	    // If the player is not in check, it can't be checkmate.
//	    if (!isPlayerInCheck(player)) {
//	        return false;
//	    }
//
//	    // Try all moves for all pieces of the current player.
//	    for (ChessPiece piece : piecesBox) {
//	        if (piece.getPlayer() == player) {
//	            int fromCol = piece.getCol();
//	            int fromRow = piece.getRow();
//	            
//	            // Generate all possible moves for this piece
//	            for (int toCol = 0; toCol < 8; toCol++) {
//	                for (int toRow = 0; toRow < 8; toRow++) {
//	                    if (isValidMove(piece, fromCol, fromRow, toCol, toRow)) {
//	                        // Simulate the move
//	                        Set<ChessPiece> piecesBoxCopy = new HashSet<>(piecesBox); // Backup the state
//	                        ChessPiece simulatedPiece = new ChessPiece(toCol, toRow, piece.getPlayer(), piece.getRank(), piece.getImgName());
//	                        piecesBox.remove(piece); // Remove from original position
//	                        ChessPiece capturedPiece = pieceAt(toCol, toRow);
//	                        if (capturedPiece != null) {
//	                            piecesBox.remove(capturedPiece); // Capture opponent piece if present
//	                        }
//	                        piecesBox.add(simulatedPiece); // Add to the new position
//	                        
//	                        // If this move gets the king out of check, it's not checkmate.
//	                        if (!isPlayerInCheck(player)) {
//	                            piecesBox = piecesBoxCopy; // Undo the move
//	                            return false;
//	                        }
//	                        piecesBox = piecesBoxCopy; // Restore the state
//	                    }
//	                }
//	            }
//	        }
//	    }
//
//	    // No moves get the king out of check, so it's checkmate.
//	    return true;
//	}



	
	void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
	    ChessPiece movingPiece = pieceAt(fromCol, fromRow);
	    
	    // Check for a valid piece and that it's this player's turn
	    if (movingPiece == null || movingPiece.getPlayer() != playerInTurn) {
	        return; // Not a valid piece or not the player's turn.
	    }

	    // Check if the move is valid
	    if (!isValidMove(movingPiece, fromCol, fromRow, toCol, toRow)) {
	        return; // The move is invalid, do not change turn
	    }

	    // Backup the state before the move
	    Set<ChessPiece> backupPiecesBox = new HashSet<>(piecesBox);
	    ChessPiece capturedPiece = pieceAt(toCol, toRow);

	    // Perform the move temporarily
	    piecesBox.remove(movingPiece);
	    if (capturedPiece != null) {
	        piecesBox.remove(capturedPiece);
	    }
	    ChessPiece newPiece = new ChessPiece(toCol, toRow, movingPiece.getPlayer(), movingPiece.getRank(), movingPiece.getImgName());
	    piecesBox.add(newPiece);

	    // Check if the move puts the own king in check
	    if (isKingInCheck(movingPiece.getPlayer())) {
	        // Undo the move since it's invalid
	        piecesBox = backupPiecesBox;
	        return; // Still the same player's turn since the move was invalid
	    }

	    // The move is valid and doesn't put the king in check, so finalize it
	    newPiece.setMoved();

	    // If castling was performed, move the rook as well
	    if (movingPiece.getRank() == Rank.KING && Math.abs(fromCol - toCol) == 2) {
	        performCastling(newPiece, toCol);
	    }

	    // Update the turn only after a valid move
	    playerInTurn = playerInTurn.opposite();
	}



	
	
	
	private void executeMove(int fromCol, int fromRow, int toCol, int toRow, ChessPiece movingPiece) {
	    // Remove any piece that is currently at the destination square
	    ChessPiece pieceAtDestination = pieceAt(toCol, toRow);
	    if (pieceAtDestination != null) {
	        piecesBox.remove(pieceAtDestination);
	    }

	    // Move the piece from the starting square to the destination square
	    piecesBox.remove(movingPiece);
	    movingPiece = new ChessPiece(toCol, toRow, movingPiece.getPlayer(), movingPiece.getRank(), movingPiece.getImgName());
	    piecesBox.add(movingPiece);
	}

	
	boolean isKingInCheck(Player player) {
	    ChessPiece king = findKing(player);
	    if (king == null) return false;
	    return isSquareUnderAttack(king.getCol(), king.getRow(), player.opposite());
	}
	
	ChessPiece findKing(Player player) {
	    for (ChessPiece piece : piecesBox) {
	        if (piece.getPlayer() == player && piece.getRank() == Rank.KING) {
	            return piece;
	        }
	    }
	    return null;
	}
	boolean isPlayerInCheck(Player player) {
	    ChessPiece king = findKing(player);
	    if (king == null) return false; // Should never happen if the king is always present
	    return isSquareUnderAttack(king.getCol(), king.getRow(), player.opposite());
	}


	
	private void performCastling(ChessPiece king, int toCol) {
	    // Direction towards the rook for king's side or queen's side
	    int step = toCol == 6 ? 1 : -1;
	    int rookCol = toCol == 6 ? 7 : 0;
	    int rookDestCol = toCol == 6 ? 5 : 3;

	    // Move the rook
	    ChessPiece rook = pieceAt(rookCol, king.getRow());
	    piecesBox.remove(rook);
	    piecesBox.add(new ChessPiece(rookDestCol, king.getRow(), rook.getPlayer(), rook.getRank(), rook.getImgName()));

	    // Move the king
	    piecesBox.remove(king);
	    piecesBox.add(new ChessPiece(toCol, king.getRow(), king.getPlayer(), king.getRank(), king.getImgName()));

	    // Set the moved flag on both the king and the rook
	    king.setMoved();
	    rook.setMoved();
	}

	ChessPiece pieceAt(int col, int row) {
		for (ChessPiece chessPiece : piecesBox) {
			if (chessPiece.getCol() == col && chessPiece.getRow() == row) {
				return chessPiece;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		String desc = "";
		
		for (int row = 7; row >= 0; row--) {
			desc += "" + row;
			for (int col = 0; col < 8; col++) {
				ChessPiece p = pieceAt(col, row);
				if (p == null) {
					desc += " .";
				} else {
					desc += " ";
					switch (p.getRank()) {
					case KING: 
						desc += p.getPlayer() == Player.WHITE ? "k" : "K";
						break;
					case QUEEN: 
						desc += p.getPlayer() == Player.WHITE ? "q" : "Q";
						break;
					case BISHOP: 
						desc += p.getPlayer() == Player.WHITE ? "b" : "B";
						break;
					case ROOK: 
						desc += p.getPlayer() == Player.WHITE ? "r" : "R";
						break;
					case KNIGHT: 
						desc += p.getPlayer() == Player.WHITE ? "n" : "N";
						break;
					case PAWN: 
						desc += p.getPlayer() == Player.WHITE ? "p" : "P";
						break;
					}
				}
			}
			desc += "\n";
		}
		desc += "  0 1 2 3 4 5 6 7";
		
		return desc;
	}
}
