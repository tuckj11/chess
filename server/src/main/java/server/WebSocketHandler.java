package server;
import chess.*;
import chess.ChessPiece.PieceType;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler{
    private final Map<Integer, Set<WsContext>> gameSessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    private final GameService gameService;
    private final UserService userService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        System.out.println("Client connected");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> handleConnectCommand(ctx, command);
                case MAKE_MOVE -> handleMove(ctx, gson.fromJson(ctx.message(), MakeMoveCommand.class));
                case LEAVE -> handleLeave(ctx, command);
                //case RESIGN -> handleResign(ctx, command);
            }
        }
        catch (Exception e) {
            sendError(ctx, "Error: Unauthorized");
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {

    }

    public void handleConnectCommand(WsContext ctx, UserGameCommand command) {
        try {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();
            AuthData auth = userService.verifyAuth(authToken);
            if (auth == null) {
                sendError(ctx, "Error: Unauthorized");
                return;
            }
            GameData game = gameService.getGame(gameID);
            gameSessions.computeIfAbsent(gameID, _ -> ConcurrentHashMap.newKeySet()).add(ctx);
            sendLoadGame(ctx, game.game());

            String username = auth.username();
            String color;
            if (username.equals(game.whiteUsername())) {
                color = "WHITE";
            } else if (username.equals(game.blackUsername())) {
                color = "BLACK";
            } else {
                color = "OBSERVER";
            }
            broadcastToOthers(gameID, ctx, username + " joined as " + color);
        } catch (Exception e) {
            sendError(ctx, e.getMessage());
        }
    }

    public void handleMove(WsContext ctx, MakeMoveCommand command) {
        try {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();
            AuthData auth = userService.verifyAuth(authToken);
            if (auth == null) {
                sendError(ctx, "Error: Unauthorized");
                return;
            }
            GameData game = gameService.getGame(gameID);
            String username = auth.username();
            ChessMove move = command.getMove();

            ChessGame.TeamColor color;
            if (username.equals(game.whiteUsername())) {
                color = ChessGame.TeamColor.WHITE;
            } else if (username.equals(game.blackUsername())) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                color = null;
            }

            ChessPiece piece = game.game().getBoard().getPiece(move.getStartPosition());
            if(piece == null) {
                sendError(ctx,"There is no piece there!");
                return;
            }
            ChessGame.TeamColor pieceColor = piece.getTeamColor();
            if (color != pieceColor) {
                sendError(ctx, "That is not a piece of your color!");
                return;
            }
            game.game().makeMove(move);
            sendLoadGame(ctx, game.game());
            gameService.makeMove(gameID, game.game());

            if (move.getPromotionPiece() == null) {
                broadcastToOthers(gameID, ctx, username + " (" + color + ") " + convertPosToCor(move.getStartPosition()) + " to " + convertPosToCor(move.getEndPosition()));
            }
            else {
                broadcastToOthers(gameID, ctx, username + " (" + color + ") " + convertPosToCor(move.getStartPosition()) + " to " + convertPosToCor(move.getEndPosition()) + " Promotion: " + move.getPromotionPiece());
            }

            if (game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
                broadcastToGame(gameID, game.whiteUsername() + " (WHITE) is in Check!");
            }
            if (game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
                broadcastToGame(gameID, game.blackUsername() +" (BLACK) is in Check!");
            }
            if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
                broadcastToGame(gameID, game.whiteUsername() + " (WHITE) is in Checkmate!");
            }
            if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
                broadcastToGame(gameID, game.blackUsername() +" (BLACK) is in Checkmate!");
            }

        } catch (Exception e) {
            sendError(ctx, e.getMessage());
        }

    }

    public void handleLeave(WsContext ctx, UserGameCommand command) {
        try {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();
            AuthData auth = userService.verifyAuth(authToken);
            if (auth == null) {
                sendError(ctx, "Error: Unauthorized");
                return;
            }
            gameSessions.getOrDefault(gameID, ConcurrentHashMap.newKeySet()).remove(ctx);

            GameData game = gameService.getGame(gameID);
            String username = auth.username();
            String color;
            if (username.equals(game.whiteUsername())) {
                color = "WHITE";
            } else if (username.equals(game.blackUsername())) {
                color = "BLACK";
            } else {
                color = "OBSERVER";
            }
            if(color.equals("WHITE") || color.equals("BLACK")) {
                gameService.updateGameColor(gameID, color);
            }
            broadcastToOthers(gameID, ctx, username + " left the game as " + color);
        } catch (Exception e) {
            sendError(ctx, e.getMessage());
        }
    }

    private void broadcastToGame(int gameId, String message) {
        Set<WsContext> sessions = gameSessions.getOrDefault(gameId, ConcurrentHashMap.newKeySet());
        for (WsContext session : sessions) {
            sendNotification(session, message);
        }
    }

    private void broadcastToOthers(int gameId, WsContext sender, String message) {
        Set<WsContext> sessions = gameSessions.getOrDefault(gameId, ConcurrentHashMap.newKeySet());
        for (WsContext session : sessions) {
            if(!session.equals(sender)) {
                sendNotification(session, message);
            }
        }
    }

    private void sendLoadGameToAll(int gameID, ChessGame game) {
        Set<WsContext> sessions = gameSessions.getOrDefault(gameID, ConcurrentHashMap.newKeySet());
        for (WsContext session : sessions) {
            sendLoadGame(session, game);
        }
    }

    private void sendLoadGame(WsContext ctx, ChessGame game) {
        ctx.send(gson.toJson(new LoadGameMessage(game)));
    }

    private void sendNotification(WsContext ctx, String notificationMessage) {
        ctx.send(gson.toJson(new NotificationMessage(notificationMessage)));
    }

    private void sendError(WsContext ctx, String errorMessage) {
        ctx.send(gson.toJson(new ErrorMessage(errorMessage)));
    }

    private String convertPosToCor(ChessPosition pos) {
        String let = switch (pos.getColumn()) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> "z";
        };
        return let + pos.getRow();
    }
}
