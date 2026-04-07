package server;
import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
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
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> handleConnectCommand(ctx, command);
                case MAKE_MOVE -> handleMove(ctx, command);
                case LEAVE -> handLeave(ctx, command);
                case RESIGN -> handleResign(ctx, command);
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
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        if(!userService.verifyAuth(authToken)) {
            ctx.send("Error: " + "unauthorized");
            return;
        }
        ChessGame game = gameService.getGame(gameID);
        gameSessions.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(ctx);
        ctx.send(gson.toJson(new LoadGameMessage(game)));
    }

    private void broadcastToGame(int gameId, String message) {
        Set<WsContext> sessions = gameSessions.getOrDefault(gameId, ConcurrentHashMap.newKeySet());
        for (WsContext session : sessions) {
            sendNotification(session, message);
        }
    }

    // sends a message to everyone in the game except the sender
    private void broadcastToOthers(int gameId, WsContext sender, String message) {
        Set<WsContext> sessions = gameSessions.getOrDefault(gameId, ConcurrentHashMap.newKeySet());
        for (WsContext session : sessions) {
            if(!session.equals(sender)) {
                sendNotification(session, message);
            }
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
}
