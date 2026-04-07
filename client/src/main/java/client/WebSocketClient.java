package client;

import chess.ChessGame;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketClient extends Endpoint {
    public Session session;
    private ChessGame game;
    private final Gson gson = new Gson();

    public WebSocketClient() {
        URI uri = new URI("http://localhost:8080" + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                handleMessage(serverMessage);
            }
        });
    }
}
