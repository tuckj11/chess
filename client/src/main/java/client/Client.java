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

    }
}
