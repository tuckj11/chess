package client;

import chess.ChessGame;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;

public class WebSocketClient extends Endpoint {
    public Session session;
    private final Client client;
    private final String color;
    private int gameID;
    private String authToken;
    private ChessGame game;
    private final Gson gson = new Gson();

    public WebSocketClient(Client client, String color, int gameID, String authToken) {
        this.client = client;
        this.color = color;
        this.gameID = gameID;
        this.authToken = authToken;

        try {
            System.out.println("here1");
            URI uri = new URI("ws://localhost:8080" + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });

            sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    private void handleMessage(String message) {
        ServerMessage base = gson.fromJson(message, ServerMessage.class);
        switch (base.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGame = gson.fromJson(message, LoadGameMessage.class);
                game = loadGame.getGame();
                client.drawBoard(color, game, null, null);
            }
            case NOTIFICATION -> {
                NotificationMessage notification = gson.fromJson(message, NotificationMessage.class);
                System.out.println(notification.getMessage());
            }
            case ERROR -> {
                ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                System.out.println(error.getErrorMessage());
            }
        }
    }

    public void leave() {
        try {
            sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
        } catch (IOException e) {
            System.out.println("Error: lost connection to server please try again");
        }
    }

    private void sendCommand(UserGameCommand command) throws IOException {
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public ChessGame getGame() {
        return game;
    }

    public String getColor() {
        return color;
    }
}
