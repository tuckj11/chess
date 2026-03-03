package chess;

import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        RookMovesCalculator rook = new RookMovesCalculator();
        BishopMovesCalculator bishop = new BishopMovesCalculator();
        Collection<ChessMove> rookMoves = rook.pieceMoves(board, myPosition);
        Collection<ChessMove> bishopMoves = bishop.pieceMoves(board, myPosition);
        rookMoves.addAll(bishopMoves);
        return rookMoves;
    }
}
