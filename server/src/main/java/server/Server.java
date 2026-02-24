package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import org.eclipse.jetty.server.Authentication;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        Gson gson = new Gson();

        UserDao userDao = new MemoryUserDao();
        GameDao gameDao = new MemoryGameDao();
        AuthDao authDao = new MemoryAuthDao();

        UserService userService = new UserService(userDao, authDao);



        javalin.post("/user", ctx -> {
            UserService.RegisterRequest req = gson.fromJson(ctx.body(), UserService.RegisterRequest.class);
            userService.register(req);
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
