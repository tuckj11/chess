package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int rowIndex = myPosition.getRow() - 1;
        int colIndex = myPosition.getColumn() - 1;
        ChessGame.TeamColor color = board.getBoard()[rowIndex][colIndex].getTeamColor();

        ArrayList<ChessMove> moves = new ArrayList<>();
        if (rowIndex - 1 > 0 && colIndex - 1 > 0 && (board.getBoard()[rowIndex - 1][colIndex - 1] == null || board.getBoard()[rowIndex - 1][colIndex - 1].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex), null));
        }
        if (rowIndex - 1 > 0 && (board.getBoard()[rowIndex - 1][colIndex] == null || board.getBoard()[rowIndex - 1][colIndex].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 1), null));
        }
        if (rowIndex - 1 > 0 && colIndex + 1 < 8 && (board.getBoard()[rowIndex - 1][colIndex + 1] == null || board.getBoard()[rowIndex - 1][colIndex + 1].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 2), null));
        }
        if (colIndex + 1 < 8 && (board.getBoard()[rowIndex][colIndex + 1] == null || board.getBoard()[rowIndex][colIndex + 1].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 1, colIndex + 2), null));
        }
        if (rowIndex + 1 < 8 && colIndex + 1 < 8 && (board.getBoard()[rowIndex + 1][colIndex + 1] == null || board.getBoard()[rowIndex + 1][colIndex + 1].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 2), null));
        }
        if (rowIndex + 1 < 8 && (board.getBoard()[rowIndex + 1][colIndex] == null || board.getBoard()[rowIndex + 1][colIndex].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 1), null));
        }
        if (rowIndex + 1 < 8 && colIndex - 1 > -1 && (board.getBoard()[rowIndex + 1][colIndex - 1] == null || board.getBoard()[rowIndex + 1][colIndex - 1].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex), null));
        }
        if (colIndex - 1 > -1 && (board.getBoard()[rowIndex][colIndex - 1] == null || board.getBoard()[rowIndex][colIndex - 1].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 1, colIndex), null));
        }
        return moves;
    }
}
