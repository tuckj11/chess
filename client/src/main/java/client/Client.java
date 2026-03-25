package client;

import server.ServerFacade;

import java.util.Scanner;

public class Client {
    private final ServerFacade serverFacade;
    private final Scanner scan;

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
                case "quit" -> quit();
                case "help" -> help();
                default -> {
                    System.out.println("Sorry that is an unrecognized command. Please try one of the following");
                    help();
                }
            }
        }
    }

    private void register() {

    }

    private void login() {

    }

    private void quit() {

    }

    private void help() {

    }
}
