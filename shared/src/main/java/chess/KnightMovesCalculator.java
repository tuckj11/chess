package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int rowIndex = myPosition.getRow() - 1;
        int colIndex = myPosition.getColumn() - 1;
        ChessGame.TeamColor color = board.getBoard()[rowIndex][colIndex].getTeamColor();

        ArrayList<ChessMove> moves = new ArrayList<>();
        if (rowIndex - 2 > -1 && colIndex - 1 > -1 && (board.getBoard()[rowIndex - 2][colIndex - 1] == null || board.getBoard()[rowIndex - 2][colIndex - 1].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex - 1, colIndex), null));
        }
        if (rowIndex - 2 > -1 && colIndex + 1 < 8 && (board.getBoard()[rowIndex - 2][colIndex + 1] == null || board.getBoard()[rowIndex - 2][colIndex + 1].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex - 1, colIndex + 2), null));
        }
        if (rowIndex - 1 > -1 && colIndex + 2 < 8 && (board.getBoard()[rowIndex - 1][colIndex + 2] == null || board.getBoard()[rowIndex - 1][colIndex + 2].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex + 3), null));
        }
        if (rowIndex + 1 < 8 && colIndex + 2 < 8 && (board.getBoard()[rowIndex + 1][colIndex + 2] == null || board.getBoard()[rowIndex + 1][colIndex + 2].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex + 3), null));
        }
        if (rowIndex + 2 < 8 && colIndex + 1 < 8 && (board.getBoard()[rowIndex + 2][colIndex + 1] == null || board.getBoard()[rowIndex + 2][colIndex + 1].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 3, colIndex + 2), null));
        }
        if (rowIndex + 2 < 8 && colIndex - 1 > -1 && (board.getBoard()[rowIndex + 2][colIndex - 1] == null || board.getBoard()[rowIndex + 2][colIndex - 1].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 3, colIndex), null));
        }
        if (rowIndex + 1 < 8 && colIndex - 2 > -1 && (board.getBoard()[rowIndex + 1][colIndex - 2] == null || board.getBoard()[rowIndex + 1][colIndex - 2].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex + 2, colIndex - 1), null));
        }
        if (rowIndex - 1 > -1 && colIndex - 2 > -1 && (board.getBoard()[rowIndex - 1][colIndex - 2] == null || board.getBoard()[rowIndex - 1][colIndex - 2].getTeamColor() != color)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(rowIndex, colIndex - 1), null));
        }
        return moves;
    }
}
