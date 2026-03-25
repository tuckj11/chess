package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import io.javalin.http.HttpResponseException;
import requests.Requests;
import results.Results;


import java.util.ArrayList;
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
        catch (HttpResponseException e) {
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
        catch (HttpResponseException e) {
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
        System.out.println("Here are your possible commands\nregister - register a new account\nlogin - login to existing account\nquit - quit the client\nhelp - view your options");
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
        catch (HttpResponseException e) {
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
        catch (HttpResponseException e) {
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
        catch (HttpResponseException e) {
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
            drawBoard(color, serverFacade.listGames(new Requests.ListRequest(authToken)).games().get(listNumber - 1).game());
        }
        catch (NumberFormatException e) {
            System.out.println("You did not enter just a number. Please try again!");
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid game number. Please run list again.");
        }
        catch (HttpResponseException e) {
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
            ChessGame game = serverFacade.listGames(new Requests.ListRequest(authToken)).games().get(listNumber - 1).game();
            System.out.println("Successfully joined! Let's take you to the game");
            drawBoard("WHITE", game);
        }
        catch (NumberFormatException e) {
            System.out.println("You did not enter just a number. Please try again!");
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid game number. Please run list again.");
        }
        catch (HttpResponseException e) {
            System.out.println("Something went wrong! Please try again.");
        }

    }

    private void postLoginHelp() {
        System.out.println("Here are your possible commands\ncreate - create a new game\nlist - list current games\njoin - join a game\nobserve - observe a game\nlogout - logout of account\nhelp - see possible commands");
    }

    private void drawBoard(String color, ChessGame game) {
        ChessBoard board = game.getBoard();
        if (color.equals("WHITE")) {
            System.out.println("  a b c d e f g h");
            for (int row = 8; row >= 1; row--) {
                fillInPieces(board, row, false);
            }
            System.out.println("  a b c d e f g h");
        }
        else {
            System.out.println("  h g f e d c b a");
            for (int row = 1; row <= 8; row++) {
                fillInPieces(board, row, true);
            }
            System.out.println(" h g f e d c b a");
        }
    }

    private void fillInPieces(ChessBoard board, int row, boolean isBlack) {
        System.out.print(row + " ");
        int colStart = isBlack ? 8 : 1;
        int colEnd = isBlack ? 1 : 8;
        int colStep = isBlack ? -1 : 1;

        for (int col = colStart; col != colEnd + colStep; col += colStep) {
            ChessPosition pos = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(pos);

            boolean isLight = (row + col) % 2 != 0;
            String bg = isLight ? "\u001B[47m" : "\u001B[100m";
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
}
