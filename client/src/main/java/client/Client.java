package client;

import server.ServerFacade;
import service.GameService;
import service.UserService;
import io.javalin.http.HttpResponseException;


import java.util.Scanner;

public class Client {
    private final ServerFacade serverFacade;
    private final Scanner scan;
    private String authToken;

    public Client() {
        serverFacade = new ServerFacade("http://localhost:8080");
        scan = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Welcome to Chess! Let's get started");
        preLoginLoop();
    }

    private void preLoginLoop() {
        System.out.println("Type Help to see your options.");
        while(true) {
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
            UserService.RegisterResult res = serverFacade.register(new UserService.RegisterRequest(username, password, email));
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
            UserService.LoginResult res = serverFacade.login(new UserService.LoginRequest(username, password));
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
        System.out.println("Congrats on getting logged in. Type Help for more options");
        while(true) {
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
            serverFacade.logout(new UserService.LogoutRequest(authToken));
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
        System.out.print("Let's create a game. Please enter a game name:");
        String name = scan.nextLine();
        try {
            serverFacade.createGame(new GameService.CreateRequest(authToken, name));
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

    }

    private void joinGame() {

    }

    private void observeGame() {

    }

    private void postLoginHelp() {
        System.out.println("Here are your possible commands\ncreate - create a new game\nlist - list current games\njoin - join a game\nobserve - observe a game\nlogout - logout of account\nhelp - see possible commands");
    }
}
