package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int rowIndex = myPosition.getRow() - 1;
        int colIndex = myPosition.getColumn() - 1;
        ChessGame.TeamColor color = board.getBoard()[rowIndex][colIndex].getTeamColor();

        ArrayList<ChessMove> moves = new ArrayList<>();
        if(color == ChessGame.TeamColor.BLACK) {
            if(rowIndex>1) {
                if(board.getBoard()[rowIndex - 1][colIndex] == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 1), null));
                    if(rowIndex == 6 && board.getBoard()[rowIndex - 2][colIndex] == null) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex - 1, colIndex + 1), null));
                    }
                }
                if(colIndex + 1 < 8 && board.getBoard()[rowIndex - 1][colIndex + 1] != null && board.getBoard()[rowIndex - 1][colIndex + 1].pieceColor != color) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 2), null));
                }
                if(colIndex - 1 > -1 && board.getBoard()[rowIndex - 1][colIndex - 1] != null && board.getBoard()[rowIndex - 1][colIndex - 1].pieceColor != color) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex), null));
                }
            }
            else {
                if(board.getBoard()[rowIndex - 1][colIndex] == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 1), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 1), ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 1), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 1), ChessPiece.PieceType.QUEEN));
                }
                if(colIndex + 1 < 8 && board.getBoard()[rowIndex - 1][colIndex + 1] != null && board.getBoard()[rowIndex - 1][colIndex + 1].pieceColor != color) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 2), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 2), ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 2), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 2), ChessPiece.PieceType.QUEEN));
                }
                if(colIndex - 1 > -1 && board.getBoard()[rowIndex - 1][colIndex - 1] != null && board.getBoard()[rowIndex - 1][colIndex - 1].pieceColor != color) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex), ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex), ChessPiece.PieceType.QUEEN));
                }
            }
        }

        else if(color == ChessGame.TeamColor.WHITE) {
            if(rowIndex<6) {
                if(board.getBoard()[rowIndex + 1][colIndex] == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 1), null));
                    if(rowIndex == 1 && board.getBoard()[rowIndex + 2][colIndex] == null) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 3, colIndex + 1), null));
                    }
                }
                if(colIndex + 1 < 8 && board.getBoard()[rowIndex + 1][colIndex + 1] != null && board.getBoard()[rowIndex + 1][colIndex + 1].pieceColor != color) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 2), null));
                }
                if(colIndex - 1 > -1 && board.getBoard()[rowIndex + 1][colIndex - 1] != null && board.getBoard()[rowIndex + 1][colIndex - 1].pieceColor != color) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex), null));
                }
            }
            else {
                if(board.getBoard()[rowIndex + 1][colIndex] == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 1), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 1), ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 1), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 1), ChessPiece.PieceType.QUEEN));
                }
                if(colIndex + 1 < 8 && board.getBoard()[rowIndex + 1][colIndex + 1] != null && board.getBoard()[rowIndex + 1][colIndex + 1].pieceColor != color) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 2), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 2), ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 2), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 2), ChessPiece.PieceType.QUEEN));
                }
                if(colIndex - 1 > -1 && board.getBoard()[rowIndex + 1][colIndex - 1] != null && board.getBoard()[rowIndex + 1][colIndex - 1].pieceColor != color) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex), ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex), ChessPiece.PieceType.QUEEN));
                }
            }
        }
        return moves;
    }
}
