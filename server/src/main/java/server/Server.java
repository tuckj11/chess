package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        Gson gson = new Gson();

        try {
            DatabaseManager.createDatabase();
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        UserDao userDao = new SQLUserDao();
        GameDao gameDao = new SQLGameDao();
        AuthDao authDao = new SQLAuthDao();

        UserService userService = new UserService(userDao, authDao);
        ClearService clearService = new ClearService(userDao, gameDao, authDao);
        GameService gameService = new GameService(userDao, gameDao, authDao);

        register(gson, userService);
        login(gson, userService);
        logout(gson, userService);
        listGames(gson, gameService);
        createGame(gson, gameService);
        joinGame(gson, gameService);
        clear(gson, clearService);
    }

    private void register(Gson gson, UserService userService) {
        javalin.post("/user", ctx -> {
            try {
                UserService.RegisterRequest req = gson.fromJson(ctx.body(), UserService.RegisterRequest.class);
                if (isInvalid(req.username()) || isInvalid(req.password()) || isInvalid(req.email())) {
                    ctx.status(400);
                    ctx.result(gson.toJson(new UserService.RegisterResult(null, null, "Error: Bad Request")));
                    return;
                }
                UserService.RegisterResult res = userService.register(req);
                if (res.username() != null) {
                    ctx.status(200);
                } else if (res.message().contains("already taken")) {
                    ctx.status(403);
                } else {
                    ctx.status(500);
                }
                ctx.result(gson.toJson(res));
            } catch (com.google.gson.JsonSyntaxException e) {
                ctx.status(400);
                ctx.result(gson.toJson(new UserService.RegisterResult(null, null, "Error: Bad Request")));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(new UserService.RegisterResult(null, null, "Error: " + e.getMessage())));
            }
        });
    }

    private void login(Gson gson, UserService userService) {
        javalin.post("/session", ctx -> {
            try {
                UserService.LoginRequest req = gson.fromJson(ctx.body(), UserService.LoginRequest.class);
                if (isInvalid(req.username()) || isInvalid(req.password())) {
                    ctx.status(400);
                    ctx.result(gson.toJson(new UserService.LoginResult(null, null, "Error: Bad Request")));
                    return;
                }
                UserService.LoginResult res = userService.login(req);
                if (res.username() != null) {
                    ctx.status(200);
                } else if (res.message().contains("unauthorized")) {
                    ctx.status(401);
                } else {
                    ctx.status(500);
                }
                ctx.result(gson.toJson(res));
            } catch (com.google.gson.JsonSyntaxException e) {
                ctx.status(400);
                ctx.result(gson.toJson(new UserService.LoginResult(null, null, "Error: Bad Request")));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(new UserService.LoginResult(null, null, "Error: " + e.getMessage())));
            }
        });
    }

    private void logout(Gson gson, UserService userService) {
        javalin.delete("/session", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                UserService.LogoutResult res = userService.logout(new UserService.LogoutRequest(authToken));
                ctx.status(res.message() == null ? 200 : 401);
                ctx.result(gson.toJson(res));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(new UserService.LogoutResult("Error: " + e.getMessage())));
            }
        });
    }

    private void listGames(Gson gson, GameService gameService) {
        javalin.get("/game", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                GameService.ListResult res = gameService.listGames(new GameService.ListRequest(authToken));
                ctx.status(res.message() == null ? 200 : 401);
                ctx.result(gson.toJson(res));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(new GameService.ListResult(null, "Error: " + e.getMessage())));
            }
        });
    }

    private void createGame(Gson gson, GameService gameService) {
        javalin.post("/game", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                GameService.CreateRequest req = gameService.addAuthToCreateRequest(authToken,
                        gson.fromJson(ctx.body(), GameService.CreateRequest.class));
                if (isInvalid(req.gameName())) {
                    ctx.status(400);
                    ctx.result(gson.toJson(new GameService.CreateResult(null, "Error: Bad Request")));
                    return;
                }
                GameService.CreateResult res = gameService.createGame(req);
                if (res.gameID() != null) {
                    ctx.status(200);
                } else if (res.message().contains("unauthorized")) {
                    ctx.status(401);
                } else {
                    ctx.status(500);
                }
                ctx.result(gson.toJson(res));
            } catch (com.google.gson.JsonSyntaxException e) {
                ctx.status(400);
                ctx.result(gson.toJson(new GameService.CreateResult(null, "Error: Bad Request")));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(new GameService.CreateResult(null, "Error: " + e.getMessage())));
            }
        });
    }

    private void joinGame(Gson gson, GameService gameService) {
        javalin.put("/game", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                GameService.JoinRequest req = gameService.addAuthToJoinRequest(authToken,
                        gson.fromJson(ctx.body(), GameService.JoinRequest.class));
                if (req.playerColor() == null || (!req.playerColor().equals("WHITE") && !req.playerColor().equals("BLACK"))) {
                    ctx.status(400);
                    ctx.result(gson.toJson(new GameService.JoinResult("Error: Bad Request")));
                    return;
                }
                GameService.JoinResult res = gameService.joinGame(req);
                if (res.message() == null) {
                    ctx.status(200);
                } else if (res.message().contains("unauthorized")) {
                    ctx.status(401);
                } else if (res.message().contains("Request")) {
                    ctx.status(400);
                } else if (res.message().contains("Taken")) {
                    ctx.status(403);
                } else {
                    ctx.status(500);
                }
                ctx.result(gson.toJson(res));
            } catch (com.google.gson.JsonSyntaxException e) {
                ctx.status(400);
                ctx.result(gson.toJson(new GameService.JoinResult("Error: Bad Request")));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(new GameService.JoinResult("Error: " + e.getMessage())));
            }
        });
    }

    private void clear(Gson gson, ClearService clearService) {
        javalin.delete("/db", ctx -> {
            try {
                ClearService.ClearResult res = clearService.clear();
                ctx.status(200);
                ctx.result(gson.toJson(res));
            } catch (Exception e) {
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
