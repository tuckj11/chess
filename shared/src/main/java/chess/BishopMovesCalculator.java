package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int rowIndex = myPosition.getRow() - 1;
        int colIndex = myPosition.getColumn() - 1;
        ChessGame.TeamColor color = board.getBoard()[rowIndex][colIndex].getTeamColor();

        ArrayList<ChessMove> moves = new ArrayList<>();

        int tempRowIndex = rowIndex - 1;
        int tempColIndex = colIndex - 1;
        while(tempRowIndex > -1 && tempColIndex > -1) {
            if(board.getBoard()[tempRowIndex][tempColIndex] == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRowIndex + 1, tempColIndex + 1), null));
            }
            else if(board.getBoard()[tempRowIndex][tempColIndex] != null && board.getBoard()[tempRowIndex][tempColIndex].pieceColor != color) {
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRowIndex + 1, tempColIndex + 1), null));
                break;
            }
            else {
                break;
            }

            tempRowIndex--;
            tempColIndex--;
        }

        tempRowIndex = rowIndex - 1;
        tempColIndex = colIndex + 1;
        while(tempRowIndex > -1 && tempColIndex < 8) {
            if(board.getBoard()[tempRowIndex][tempColIndex] == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRowIndex + 1, tempColIndex + 1), null));
            }
            else if(board.getBoard()[tempRowIndex][tempColIndex] != null && board.getBoard()[tempRowIndex][tempColIndex].pieceColor != color) {
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRowIndex + 1, tempColIndex + 1), null));
                break;
            }
            else {
                break;
            }

            tempRowIndex--;
            tempColIndex++;
        }

        tempRowIndex = rowIndex + 1;
        tempColIndex = colIndex + 1;
        while(tempRowIndex < 8 && tempColIndex < 8) {
            if(board.getBoard()[tempRowIndex][tempColIndex] == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRowIndex + 1, tempColIndex + 1), null));
            }
            else if(board.getBoard()[tempRowIndex][tempColIndex] != null && board.getBoard()[tempRowIndex][tempColIndex].pieceColor != color) {
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRowIndex + 1, tempColIndex + 1), null));
                break;
            }
            else {
                break;
            }

            tempRowIndex++;
            tempColIndex++;
        }

        tempRowIndex = rowIndex + 1;
        tempColIndex = colIndex - 1;
        while(tempRowIndex < 8 && tempColIndex > -1) {
            if(board.getBoard()[tempRowIndex][tempColIndex] == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRowIndex + 1, tempColIndex + 1), null));
            }
            else if(board.getBoard()[tempRowIndex][tempColIndex] != null && board.getBoard()[tempRowIndex][tempColIndex].pieceColor != color) {
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRowIndex + 1, tempColIndex + 1), null));
                break;
            }
            else {
                break;
            }

            tempRowIndex++;
            tempColIndex--;
        }
        return moves;
    }
}
