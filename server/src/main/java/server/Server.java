package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import org.eclipse.jetty.server.Authentication;
import service.ClearService;
import service.GameService;
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
        ClearService clearService = new ClearService(userDao, gameDao, authDao);
        GameService gameService = new GameService(userDao, gameDao, authDao);



        javalin.post("/user", ctx -> {
            try {
                UserService.RegisterRequest req = gson.fromJson(ctx.body(), UserService.RegisterRequest.class);
                if(isInvalid(req.username()) || isInvalid(req.password()) || isInvalid(req.email())) {
                    ctx.status(400);
                    ctx.result(gson.toJson(new UserService.RegisterResult(null, null, "Error: Bad Request")));
                    return;
                }
                UserService.RegisterResult res = userService.register(req);
                if(res.username() != null) {
                    ctx.status(200);
                }
                else if(res.message().contains("already taken")) {
                    ctx.status(403);
                }
                else {
                    ctx.status(500);
                }
                ctx.result(gson.toJson(res));
            }
            catch(com.google.gson.JsonSyntaxException e) {
                ctx.status(400);
                ctx.result(gson.toJson(new UserService.RegisterResult(null, null, "Error: Bad Request")));
            }
            catch(Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(new UserService.RegisterResult(null, null, "Error: " + e.getMessage())));
            }
        });

        javalin.post("/session", ctx -> {
                    try {
                        UserService.LoginRequest req = gson.fromJson(ctx.body(), UserService.LoginRequest.class);
                        if (isInvalid(req.username()) || isInvalid(req.password())) {
                            ctx.status(400);
                            ctx.result(gson.toJson(new UserService.LoginResult(null, null, "Error: Bad Request")));
                            return;
                        }
                        UserService.LoginResult res = userService.login(req);
                        if(res.username() != null) {
                            ctx.status(200);
                        }
                        else if(res.message().contains("unauthorized")) {
                            ctx.status(401);
                        }
                        else {
                            ctx.status(500);
                        }
                        ctx.result(gson.toJson(res));

                    }
                    catch(com.google.gson.JsonSyntaxException e) {
                        ctx.status(400);
                        ctx.result(gson.toJson(new UserService.LoginResult(null, null, "Error: Bad Request")));
                    }
                    catch(Exception e) {
                        ctx.status(500);
                        ctx.result(gson.toJson(new UserService.LoginResult(null, null, "Error: " + e.getMessage())));
                    }
                });

        javalin.delete("/session", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                UserService.LogoutResult res = userService.logout(new UserService.LogoutRequest(authToken));
                if(res.message() == null) {
                    ctx.status(200);
                }
                else {
                    ctx.status(401);
                }
                ctx.result(gson.toJson(res));
            }
            catch(Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(new UserService.LogoutResult("Error: " + e.getMessage())));
            }

        });

        javalin.get("/game", ctx -> {
            try{
                String authToken = ctx.header("Authorization");
                GameService.ListResult res = gameService.listGames(new GameService.ListRequest(authToken));
                if(res.message() == null) {
                    ctx.status(200);
                }
                else {
                    ctx.status(401);
                }
                ctx.result(gson.toJson(res));
            }
            catch(Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(new GameService.ListResult(null, "Error: " + e.getMessage())));
            }
        });



        javalin.delete("/db", ctx -> {
            try{
                ClearService.ClearResult res = clearService.clear();
                ctx.status(200);
                ctx.result(gson.toJson(res));
            }
            catch(Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(new ClearService.ClearResult("Error: " + e.getMessage())));
            }
        });
    }



    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private boolean isInvalid(String str){
        return str == null || str.isEmpty();
    }
}
