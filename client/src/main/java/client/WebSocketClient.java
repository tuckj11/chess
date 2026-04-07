package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketClient extends Endpoint {
    public Session session;
    private final Client client;
    private final String color;
    private final int gameID;
    private final String authToken;
    private ChessGame game;
    private final Gson gson = new Gson();

    public WebSocketClient(Client client, String color, int gameID, String authToken) {
        this.client = client;
        this.color = color;
        this.gameID = gameID;
        this.authToken = authToken;

        try {
            URI uri = new URI("ws://localhost:8080" + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, uri);

            this.session.addMessageHandler((MessageHandler.Whole<String>) this::handleMessage);

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

    public void makeMove(String startPos, String endPos, String promotionPiece) {
        try {
            int startCol = client.convertColToInt(startPos.substring(0, 1));
            int startRow = (Character.isDigit(startPos.charAt(1)) && startPos.charAt(1) >= '1'
                    && startPos.charAt(1) <= '8') ? Character.getNumericValue(startPos.charAt(1)) : -1;
            if (startCol == -1 || startRow == -1) {
                System.out.println("Invalid input. Please try again");
                return;
            }

            int endCol = client.convertColToInt(endPos.substring(0, 1));
            int endRow = (Character.isDigit(endPos.charAt(1)) && endPos.charAt(1) >= '1' &&
                    endPos.charAt(1) <= '8') ? Character.getNumericValue(endPos.charAt(1)) : -1;
            if (endCol == -1 || endRow == -1) {
                System.out.println("Invalid input. Please try again");
                return;
            }

            ChessPiece.PieceType promotionPieceType = convertToPieceType(promotionPiece);

            ChessMove move = new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), promotionPieceType);
            sendCommand(new MakeMoveCommand(move, authToken, gameID));
        } catch (IOException e) {
            System.out.println("Lost connection to the server. Please try again");
        }
    }

    public void resign() {
        try {
            sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
        } catch (IOException e) {
            System.out.println("Error: lost connection to server please try again");
        }
    }

    private void sendCommand(UserGameCommand command) throws IOException {
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    private ChessPiece.PieceType convertToPieceType(String promotionPiece) {
        switch (promotionPiece) {
            case "BISHOP" -> {return ChessPiece.PieceType.BISHOP;}
            case "ROOK" -> {return ChessPiece.PieceType.ROOK;}
            case "QUEEN" -> {return ChessPiece.PieceType.QUEEN;}
            case "KNIGHT" -> {return ChessPiece.PieceType.KNIGHT;}
            default -> {return null;}
        }
    }

    public ChessGame getGame() {
        return game;
    }

    public String getColor() {
        return color;
    }
}
