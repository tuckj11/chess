package client;

import server.ServerFacade;
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
                case "help" -> help();
                default -> {
                    System.out.println("Sorry that is an unrecognized command. Please try one of the following");
                    help();
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
        UserService.LoginResult res = serverFacade.login(new UserService.LoginRequest(username, password));
        authToken = res.authToken();
        postLoginLoop();
    }

    private void help() {
        System.out.println("Here are your possible commands\nregister - register a new account\nlogin - login to existing account\nquit - quit the client\nhelp - view your options");
    }

    private void postLoginLoop() {

    }
}
