package requests;

public class Requests {
    public record ListRequest(String authToken) {}
    public record CreateRequest(String authToken, String gameName) {}
    public record JoinRequest(String authToken, String playerColor, Integer gameID) {}
    public record RegisterRequest(String username, String password, String email) {}
    public record LoginRequest(String username, String password) {}
    public record LogoutRequest(String authToken) {}

}
