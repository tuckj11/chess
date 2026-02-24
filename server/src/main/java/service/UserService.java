package service;

record RegisterRequest(String username, String password, String email) {}
record RegisterResult(String username, String authToken, String message) {}

record LoginRequest(String username, String password) {}
record LoginResult(String username, String authToken) {}

record LogoutRequest(String authToken) {}
record LogoutResult() {}

public class UserService {
}
