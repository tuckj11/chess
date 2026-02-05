package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor turn;
    ChessBoard board;
    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> tempMoves;
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null) {
            return null;
        }

        else {
            tempMoves = piece.pieceMoves(board, startPosition);
        }
        ChessGame.TeamColor color = piece.getTeamColor();
        ArrayList<ChessMove> moves = new ArrayList<>(tempMoves);
        ChessBoard copy;
        for (int i = moves.size() - 1; i >= 0; i--) {
            copy = new ChessBoard(board);
            copy.addPiece(moves.get(i).getEndPosition(), piece);
            copy.addPiece(moves.get(i).getStartPosition(), null);
            if(isInCheckCopy(color, copy)) {
                //System.out.println("Bad" + moves.get(i));
                moves.remove(i);
            }
            //else {
                //System.out.println("Good" + moves.get(i));
            //}
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null) {
            throw new InvalidMoveException("There is no piece there");
        }
        ChessGame.TeamColor color = piece.getTeamColor();
        if(color != turn) {
            throw new InvalidMoveException("It is not your turn");
        }
        Collection<ChessMove> moves = validMoves(startPosition);
        if(!moves.contains(move)) {
            throw new InvalidMoveException("That is not a valid move");
        }
        if(move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(turn, move.getPromotionPiece()));
        }
        else {
            board.addPiece(move.getEndPosition(), piece);
        }
        board.addPiece(move.getStartPosition(), null);
        if(turn == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
        }
        else {
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> enemyMoves = new ArrayList<>();
        ChessPosition kingPosition = null;

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    continue;
                }
                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPosition = position;
                }
                if (piece.getTeamColor() != teamColor) {
                    if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        Collection<ChessMove> pawnMoves = piece.pieceMoves(board, position);
                        for (ChessMove pawnMove : pawnMoves) {
                            if (pawnMove.getStartPosition().getColumn() != pawnMove.getEndPosition().getColumn()) {
                                enemyMoves.add(pawnMove);
                            }
                        }
                    } else {
                        enemyMoves.addAll(piece.pieceMoves(board, position));
                    }
                }
            }
        }
        for (ChessMove move : enemyMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInCheckCopy(TeamColor teamColor, ChessBoard copy) {
        Collection<ChessMove> enemyMoves = new ArrayList<>();
        ChessPosition kingPosition = null;

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = copy.getPiece(position);
                if (piece == null) {
                    continue;
                }
                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPosition = position;
                }
                if (piece.getTeamColor() != teamColor) {
                    if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        Collection<ChessMove> pawnMoves = piece.pieceMoves(copy, position);
                        for (ChessMove pawnMove : pawnMoves) {
                            if (pawnMove.getStartPosition().getColumn() != pawnMove.getEndPosition().getColumn()) {
                                enemyMoves.add(pawnMove);
                            }
                        }
                    } else {
                        enemyMoves.addAll(piece.pieceMoves(copy, position));
                    }
                }
            }
        }
        for (ChessMove move : enemyMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for(int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor) {
                    possibleMoves.addAll(validMoves(position));
                }
            }
        }
        return possibleMoves.isEmpty() && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for(int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor) {
                    possibleMoves.addAll(validMoves(position));
                }
            }
        }
        return possibleMoves.isEmpty() && !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, getBoard());
    }
}
