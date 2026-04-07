package server;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
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
            ctx.send("Error: " + e.getMessage());
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {

    }
}
