package client;

import chess.*;
import model.GameData;
import requests.Requests;
import results.Results;
import websocket.commands.UserGameCommand;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Client {
    private final ServerFacade serverFacade;
    private final Scanner scan;
    private String authToken;
    private final List<Integer> gameIds = new ArrayList<>();

    public Client() {
        serverFacade = new ServerFacade("http://localhost:8080");
        scan = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Welcome to Chess! Let's get started");
        preLoginLoop();
    }

    private void preLoginLoop() {
        while(true) {
            System.out.println("Type Help to see your options.");
            String input = scan.nextLine().trim().toLowerCase();
            switch (input) {
                case "register" -> register();
                case "login" -> login();
                case "quit" -> {
                     return;
                }
                case "help" -> preLoginHelp();
                default -> {
                    System.out.println("Sorry that is an unrecognized command. Please try one of the following");
                    preLoginHelp();
                }
            }
        }
    }

    private void register() {
        System.out.println("Let's get you registered. Please enter the following");
        System.out.print("Username: ");
        String username = scan.nextLine();
        System.out.print("Password: ");
        String password = scan.nextLine();
        System.out.print("Email: ");
        String email = scan.nextLine();
        try {
            Results.RegisterResult res = serverFacade.register(new Requests.RegisterRequest(username, password, email));
            authToken = res.authToken();
            System.out.println("Registered Successfully!");
            postLoginLoop();
        }
        catch (ResponseException e) {
            if(e.getStatus() == 403) {
                System.out.println("Username is already taken. Please try again");
            }
            else if(e.getStatus() == 400) {
                System.out.println("Invalid registration details. Please try again");
            }
            else {
                System.out.println("Something went wrong, please try again.");
            }
        }
    }

    private void login() {
        System.out.println("Let's get you logged in. Please enter the following");
        System.out.print("Username: ");
        String username = scan.nextLine();
        System.out.print("Password: ");
        String password = scan.nextLine();
        try {
            Results.LoginResult res = serverFacade.login(new Requests.LoginRequest(username, password));
            authToken = res.authToken();
            postLoginLoop();
        }
        catch (ResponseException e) {
            if (e.getStatus() == 400) {
                System.out.println("Invalid login details. Please try again");
            }
            if (e.getStatus() == 401) {
                System.out.println("Either your username or password is invalid. Please try again!");
            }
            else {
                System.out.println("Something went wrong, please try again.");
            }
        }
    }

    private void preLoginHelp() {
        System.out.println("""
                Here are your possible commands
                register - register a new account
                login - login to existing account
                quit - quit the client
                help - view your options""");
    }

    private void postLoginLoop() {
        System.out.println("Congrats on getting logged in.");
        while(true) {
            System.out.println("Type Help for more options.");
            String input = scan.nextLine().trim().toLowerCase();
            switch (input) {
                case "logout" -> {
                    if (logout()) {
                        return;
                    }
                }
                case "create" -> createGame();
                case "list" -> listGames();
                case "join" -> joinGame();
                case "observe" -> observeGame();
                case "help" -> postLoginHelp();
                default -> {
                    System.out.println("Sorry that is an unrecognized command. Please try one of the following");
                    postLoginHelp();
                }
            }
        }
    }

    private boolean logout() {
        try {
            serverFacade.logout(new Requests.LogoutRequest(authToken));
            authToken = null;
            System.out.println("Logged out Successfully!");
            return true;
        }
        catch (ResponseException e) {
            System.out.println("Something went wrong! Please try again!");
            return false;
        }
    }

    private void createGame() {
        System.out.print("Let's create a game. Please enter a game name: ");
        String name = scan.nextLine();
        try {
            serverFacade.createGame(new Requests.CreateRequest(authToken, name));
            System.out.println("Game successfully made! Please type List to see further details");
        }
        catch (ResponseException e) {
            if (e.getStatus() == 400) {
                System.out.println("Invalid create details. Please try again");
            }
            else {
                System.out.println("Something went wrong! Please try again later!");
            }
        }
    }

    private void listGames() {
        System.out.println("Let's see what games are available");
        try{
            Results.ListResult res = serverFacade.listGames(new Requests.ListRequest(authToken));
            if(res.games().isEmpty()) {
                System.out.println("There are no available games!");
                return;
            }
            gameIds.clear();
            int i = 1;
            for(GameData game: res.games()) {
                gameIds.add(game.gameID());
                System.out.println(i + ". " + game.gameName() + " White Player: " + game.whiteUsername() + " Black Player: " + game.blackUsername());
                  i++;
            }

        }
        catch (ResponseException e) {
            System.out.println("Something went wrong! Please try again");
        }
    }

    private void joinGame() {
        if(gameIds.isEmpty()) {
            System.out.println("There are no games available or you have not typed List yet");
            return;
        }
        System.out.println("Let's help you join a game. Please enter the following information");
        System.out.print("What game number do you want to join? ");
        String strId = scan.nextLine();
        try {
            int listNumber = Integer.parseInt(strId.trim());
            int id = gameIds.get(listNumber - 1);
            System.out.println("What color did you want to play as? Please type either BLACK or WHITE!");
            String color = scan.nextLine();
            serverFacade.joinGame(new Requests.JoinRequest(authToken, color, id));
            System.out.println("Successfully joined! Let's take you to the game");
            WebSocketClient ws = new WebSocketClient(this, color, id, authToken);
            gameLoop(ws);
        }
        catch (NumberFormatException e) {
            System.out.println("You did not enter just a number. Please try again!");
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid game number. Please run list again.");
        }
        catch (ResponseException e) {
            if (e.getStatus() == 400) {
                System.out.println("Invalid join details. Please try again");
            }
            else if(e.getStatus() == 403) {
                System.out.println("That color is already taken! Please try again.");
            }
            else {
                System.out.println("Something went wrong! Please try again.");
            }
        }


    }

    private void observeGame() {
        if(gameIds.isEmpty()) {
            System.out.println("There are no games available or you have not typed List yet");
            return;
        }
        System.out.print("Let's observe a game! What game number did you want to observe: ");
        String strId = scan.nextLine();
        try {
            int listNumber = Integer.parseInt(strId.trim());
            int id = gameIds.get(listNumber - 1);
            System.out.println("Successfully joined! Let's take you to the game");
            WebSocketClient ws = new WebSocketClient(this, "WHITE", id, authToken);
            observeLoop(ws);
        }
        catch (NumberFormatException e) {
            System.out.println("You did not enter just a number. Please try again!");
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid game number. Please run list again.");
        }
        catch (ResponseException e) {
            System.out.println("Something went wrong! Please try again.");
        }

    }

    private void postLoginHelp() {
        System.out.println("""
            Here are your possible commands
            create - create a new game
            list - list current games
            join - join a game
            observe - observe a game
            logout - logout of account
            help - see possible commands""");
    }

    private void gameLoop(WebSocketClient ws) {
        System.out.println("Welcome to the Game!");
        while(true) {
            System.out.println("Type Help for more options.");
            String input = scan.nextLine().trim().toLowerCase();
            switch (input) {
                case "redraw" -> drawBoard(ws.getColor(), ws.getGame(), null, null);
                case "leave" -> {
                    ws.leave();
                    return;
                }
                case "move" -> movePiece(ws);
                case "resign" -> resign();
                case "highlight" -> highlight(ws);
                case "help" -> gameHelp();
                default -> {
                    System.out.println("Sorry that is an unrecognized command. Please try one of the following");
                    gameHelp();
                }
            }
        }
    }

    private void movePiece(WebSocketClient ws) {
        System.out.println("Lets move a piece");
        System.out.println("What piece do you want to move? Type the square. e.g. a3");
        String startPos = scan.nextLine();
        System.out.println("Where do you want to move it to? Type the square. e.g. a4");
        String endPos = scan.nextLine();
        System.out.println("What do you want to promote to? Type the piece e.g. BISHOP. Leave blank if n.a.");
        String promotionPiece = scan.nextLine();
        ws.makeMove(startPos, endPos, promotionPiece);
    }

    private void resign() {

    }

    private void highlight(WebSocketClient ws) {
        System.out.println("Please enter the square of the piece you want to check e.g a3");
        String piece = scan.nextLine();
        int col = convertColToInt(piece.substring(0, 1));
        int row = (Character.isDigit(piece.charAt(1)) && piece.charAt(1) >= '1' && piece.charAt(1) <= '8') ? Character.getNumericValue(piece.charAt(1)) : -1;
        if (col == -1 || row == -1) {
            System.out.println("Invalid input. Please try again");
            return;
        }
        ChessPosition pos = new ChessPosition(row, col);
        ChessGame game = ws.getGame();
        Collection<ChessMove> possibleMoves = game.validMoves(pos);
        if (possibleMoves == null) {
            System.out.println("There is not a piece there. Please try again");
            return;
        }
        drawBoard(ws.getColor(), ws.getGame(), possibleMoves, pos);
    }

    private void gameHelp() {
        System.out.println("""
            Here are your possible commands
            redraw - redraw the chess board
            leave - leave the current game
            move - make a move
            resign - resign the current game
            highlight - see possible moves for a given piece
            help - see possible commands""");
    }

    private void observeLoop(WebSocketClient ws) {
        System.out.println("Welcome to the Game!");
        while(true) {
            System.out.println("Type Help for more options.");
            String input = scan.nextLine().trim().toLowerCase();
            switch (input) {
                case "redraw" -> drawBoard(ws.getColor(), ws.getGame(), null, null);
                case "leave" -> {
                    ws.leave();
                    return;
                }
                case "help" -> observeHelp();
                default -> {
                    System.out.println("Sorry that is an unrecognized command. Please try one of the following");
                    gameHelp();
                }
            }
        }
    }

    private void observeHelp() {
        System.out.println("""
            Here are your possible commands
            redraw - redraw the chess board
            leave - leave the current game
            help - see possible commands""");
    }

    public void drawBoard(String color, ChessGame game, Collection<ChessMove> highlights, ChessPosition current) {
        ChessBoard board = game.getBoard();
        if (color.equals("WHITE")) {
            System.out.println("  a b c d e f g h");
            for (int row = 8; row >= 1; row--) {
                fillInPieces(board, row, false, highlights, current);
            }
            System.out.println("  a b c d e f g h");
        } else {
            System.out.println("  h g f e d c b a");
            for (int row = 1; row <= 8; row++) {
                fillInPieces(board, row, true, highlights, current);
            }
            System.out.println("  h g f e d c b a");
        }
    }

    private void fillInPieces(ChessBoard board, int row, boolean isBlack, Collection<ChessMove> highlights, ChessPosition current) {
        System.out.print(row + " ");
        int colStart = isBlack ? 8 : 1;
        int colEnd = isBlack ? 1 : 8;
        int colStep = isBlack ? -1 : 1;

        for (int col = colStart; col != colEnd + colStep; col += colStep) {
            ChessPosition pos = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(pos);

            int currentCol = col;
            boolean isHighlighted = highlights != null && highlights.stream()
                    .anyMatch(move -> move.getEndPosition().getRow() == row
                            && move.getEndPosition().getColumn() == currentCol);

            boolean isCurrent = current != null && current.getColumn() == col && current.getRow() == row;

            boolean isLight = (row + col) % 2 != 0;
            String bg;
            if (isHighlighted) {
                bg = isLight ? "\u001B[102m" : "\u001B[42m";
            }
            else if (isCurrent) {
                bg = isLight ? "\u001B[101m" : "\u001B[41m";
            }else {
                bg = isLight ? "\u001B[47m" : "\u001B[100m";
            }

            String text = piece == null ? "  " : getPieceSymbol(piece);
            System.out.print(bg + text + "\u001B[0m");
        }
        System.out.println(" " + row);
    }

    private String getPieceSymbol(ChessPiece piece) {
        String color = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "\u001B[97m" : "\u001B[34m";
        String letter = switch (piece.getPieceType()) {
            case KING ->   "K";
            case QUEEN ->  "Q";
            case ROOK ->   "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN ->   "P";
        };
        return color + letter + " \u001B[0m";
    }

    public int convertColToInt(String col) {
        switch (col) {
            case "a" -> {return 1;}
            case "b" -> {return 2;}
            case "c" -> {return 3;}
            case "d" -> {return 4;}
            case "e" -> {return 5;}
            case "f" -> {return 6;}
            case "g" -> {return 7;}
            case "h" -> {return 8;}
            default -> {return -1;}
        }
    }
}
