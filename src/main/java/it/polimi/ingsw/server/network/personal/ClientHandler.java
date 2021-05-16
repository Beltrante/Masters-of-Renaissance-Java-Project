package it.polimi.ingsw.server.network.personal;

import com.google.gson.*;
import it.polimi.ingsw.server.events.receive.*;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.Server;

import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    private String nickname;
    private StatusEnum status;
    private final ConnectionToClient connectionToClient;
    private final Gson gson;
    private VirtualView virtualView;
    private static final String TYPE_QUIT = "quit";
    private static final String TYPE_LOBBY_NUMBER_CHOICE = "lobbyChoice";
    private static final String TYPE_MATCHMAKING= "matchmaking";
    private static final String TYPE_LOGIN = "login";
    private static final String NICKNAME_REGEXP = "^[a-zA-Z0-9_-]{3,15}$";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEXP);

    private final Map<String, Object> receiveEventByJsonType = new HashMap<String, Object>() {{
        put("setupAction", SetupReceiveEvent.class);
        put("leaderAction", LeaderReceiveEvent.class);
        put("buyAction", BuyReceiveEvent.class);
        put("marketAction", MarketReceiveEvent.class);
        put("productionAction", ProductionReceiveEvent.class);
        put("cardPlacementAction", PlaceDevCardReceiveEvent.class);
        put("resourcesPlacementAction", PlaceResourcesReceiveEvent.class);
        put("transformationAction", TransformationReceiveEvent.class);
        put("endTurnAction", EndTurnReceiveEvent.class);
    }};

    public ClientHandler(Socket socket) {
        this.connectionToClient = new ConnectionToClient(socket);
        this.gson = new Gson();
    }

    @Override
    public void run() {
        status = StatusEnum.LOGIN;

        sendInfoMessage("Welcome to Maestri del Rinascimento");

        // 1- Wait for valid nickname and password
        login();

        // 3- In game/
        game();

    }

    private void login() {
        while (status == StatusEnum.LOGIN) {

            sendSpecificTypeMessage(TYPE_LOGIN);

            if (Lobby.getLobby().isFull()) {
                sendErrorMessage("cannot join: Server is full");
                kill(true);
                return;
            }
            String message = connectionToClient.getMessage();
            JsonObject jsonObject = getAsJsonObject(message);
            if (jsonObject == null) {
                continue;
            }
            String type = jsonObject.get("type").getAsString();
            if (type.equals(TYPE_QUIT)) {
                kill(true);
                return;
            }
            //check if message is not a login valid json
            if (!type.equals(TYPE_LOGIN) || !jsonObject.has("nickname") || !jsonObject.has("password")) {
                sendErrorMessage("invalid login json");
                continue;//go back to reading a new message
            }
            String nickname = jsonObject.get("nickname").getAsString();
            String password = jsonObject.get("password").getAsString();
            //check if username and password are acceptable
            if (!checkCredentials(nickname, password)) {
                sendErrorMessage("nickname/password not permitted");
                continue;
            }
            virtualView = Lobby.getLobby().getVirtualViewByNickname(nickname);
            //new player
            if (virtualView == null) {
                //check if a game is ongoing
                if (Lobby.getLobby().isGameStarted()) {
                    sendErrorMessage("a game is currently ongoing");
                    kill(true);
                    return;
                }
                virtualView = new VirtualView(nickname, password, this);
                //addVirtualView is synchronized and recheck for new player to be multiThread safe
                if (!Lobby.getLobby().addVirtualView(virtualView)) {
                    sendErrorMessage("how unlucky! this nickname was taken a moment ago");
                    continue;//go back to reading message
                }
                this.nickname = nickname;
                sendInfoMessage("Joining lobby... ");
                //try to start game
                //synchronized segment for first in lobby(further testing on what to synchronize on is required)
                synchronized (Server.getServer()) {
                    if (Lobby.getLobby().isFirstInLobby()) {
                        String answer;
                        boolean stillDeciding = true;
                        while (stillDeciding) {
                            sendSpecificTypeMessage(TYPE_LOBBY_NUMBER_CHOICE, "between " + Lobby.MIN_PLAYERS + " and " + Lobby.MAX_PLAYERS);
                            answer = connectionToClient.getMessage();
                            JsonObject jsonAnswerObject = getAsJsonObject(answer);
                            if (jsonAnswerObject == null) {
                                continue;
                            }
                            String answerType = jsonAnswerObject.get("type").getAsString();
                            if (answerType.equals(TYPE_QUIT)) {
                                kill(true);
                                return;
                            }
                            if (!answerType.equals(TYPE_LOBBY_NUMBER_CHOICE) || !jsonAnswerObject.has("size")) {
                                continue;//go back to reading a new message
                            }
                            int choice = jsonAnswerObject.get("size").getAsInt();
                            if (Lobby.getLobby().setNumberOfPlayers(choice, nickname)) {
                                stillDeciding = false;
                                status = StatusEnum.GAME;
                            }
                        }
                    } else if (virtualView.isOnline()) {
                        clearMessageStack();
                    }
                    status = StatusEnum.GAME;
                    if(!Lobby.getLobby().isGameStarted()) {
                        sendSpecificTypeMessage(TYPE_MATCHMAKING);
                    }
                    Lobby.getLobby().updateLobbyState();
                }
                //try to start game
            } else if (virtualView.isOnline()) {
                //nickname already in use
                sendErrorMessage("Nickname already in use");
            } else {
                //is trying to reconnect
                if (password.equals(virtualView.getPassword())) {
                    //todo code below is severely incomplete
                    this.nickname = nickname;
                    virtualView.reconnect(this);
                    sendInfoMessage("You are now reconnected");
                    status = StatusEnum.GAME;
                } else {
                    sendErrorMessage("Incorrect password");
                }
            }
        }
    }

    private void game() {
        status = StatusEnum.GAME;
        String message;
        Lobby.getLobby().getVirtualViewByNickname(nickname).startPingPong();

        while (status == StatusEnum.GAME) {
            message = connectionToClient.getMessage();

            if (message.equals("pong")) {
                continue;
            }
            try {
                //todo use function getAsJsonObject
                JsonObject jsonObject = getAsJsonObject(message);
                if(jsonObject == null){
                    continue;
                }
                String type = jsonObject.get("type").getAsString();
                Type eventType = (Type) receiveEventByJsonType.get(type);
                if (type.equals(TYPE_QUIT)) {
                    kill(false);
                } else if (virtualView == null) {
                    sendErrorMessage("waitForGameToStart");
                } else if (eventType != null) {
                    ReceiveEvent event = gson.fromJson(message, eventType);
                    if(event.getNickname()==null){
                        sendErrorMessage("you can't play with a null nickname");
                        continue;
                    }
                    if (event.getNickname().equals(nickname)) {
                        virtualView.notifyObservers(event);
                    } else {
                        sendErrorMessage("you can't play for another player");
                    }
                } else {
                    sendErrorMessage("not existing action");
                }
            } catch (JsonSyntaxException e) {
                sendErrorMessage("invalid message");
            }
        }
    }

    public void sendJsonMessage(String message) {
        connectionToClient.sendMessage(message);
    }

    public void sendInfoMessage(String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "info");
        jsonObject.addProperty("payload", message);
        sendJsonMessage(jsonObject.toString());
    }

    public void sendErrorMessage(String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "error");
        jsonObject.addProperty("payload", message);
        sendJsonMessage(jsonObject.toString());
    }

    public void sendSpecificTypeMessage(String type) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        sendJsonMessage(jsonObject.toString());
    }

    public void sendSpecificTypeMessage(String type, String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("payload", message);
        sendJsonMessage(jsonObject.toString());
    }

    //deleteData = false if player should be able to reconnect
    //deleteData = true if disconnection equals data deletion
    public void kill(boolean deleteData) {

        VirtualView virtualView = Lobby.getLobby().getVirtualViewByNickname(nickname);

        if (virtualView != null) {
            //stop PingPong
            virtualView.stopPingPong();

            sendInfoMessage("quitting");
            //deleteData = remove virtualView from Lobby
            if (deleteData) {
                Lobby.getLobby().removeVirtualView(nickname);
            }
            //virtualView must remain saved and player set as offline
            else {
                //todo add code to save state in game quit
                virtualView.setOnline(false);
            }
        }
        status = StatusEnum.EXIT;
        connectionToClient.close();
        Thread.currentThread().interrupt();
    }

    //todo might be removed
    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    public JsonObject getAsJsonObject(String message) {
        try {
            JsonElement jsonElement = JsonParser.parseString(message);
            if (!jsonElement.isJsonObject()) {
                sendErrorMessage("not a json message");
                return null;//go back to reading a new message
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (!jsonObject.has("type")) {
                sendErrorMessage("not a valid json");
                return null;//go back to reading a new message
            }
            return jsonObject;
        } catch (JsonSyntaxException e) {
            sendErrorMessage("invalid message structure");
            return null;
        }
    }

    //the nickname must be a minimum of 3 and a maximum of 15 alpha-numeric characters (plus -,_ symbols)
    public boolean checkCredentials(String nickname, String password) {
        return nickname != null && password != null && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    public void clearMessageStack() {
        connectionToClient.clearStack();
    }

    public void sendPing() {
        connectionToClient.sendMessage("ping");
    }

    public ConnectionToClient getConnection() {
        return connectionToClient;
    }




}