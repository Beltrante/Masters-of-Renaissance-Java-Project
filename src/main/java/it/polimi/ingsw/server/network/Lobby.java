package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.virtualView.VirtualView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Lobby {

    protected static final int NOT_DECIDED = -1;
    protected static final int MAX_PLAYERS = 4;
    protected static final int MIN_PLAYERS = 1;
    private static Lobby instance = null;
    private int numberOfPlayers = NOT_DECIDED;
    private final Set<PlayerData> playersData;
    private Controller controller;

    private Lobby() {
        playersData = new HashSet<>();
    }

    public static synchronized Lobby getLobby() {
        if (instance == null) {
            instance = new Lobby();
        }
        return instance;
    }

    public boolean setNumberOfPlayers(int numberOfPlayers) {
        if(numberOfPlayers>MAX_PLAYERS || numberOfPlayers<MIN_PLAYERS){
            return false;
        }
        if(this.numberOfPlayers == NOT_DECIDED){
            this.numberOfPlayers = numberOfPlayers;
            //if singleplayer
            if(isFull()){
                playersData.forEach(player -> player.getClientHandler().moveToGame());
                startGame();
            }
            return true;
        }
        return false;
    }

    public boolean isFull(){
        if(this.numberOfPlayers == NOT_DECIDED){
            return false;
        }
        return !(getOnlinePlayersNumber() < this.numberOfPlayers);
    }

    public double getOnlinePlayersNumber(){
        return playersData.stream().filter(PlayerData::isOnline).count();
    }

    public PlayerData getPlayerDataByNickname(String nickname){
        return playersData.stream().filter(p -> p.getUsername().equals(nickname)).findFirst().orElse(null);
    }

    public void addPlayerData(PlayerData playerData) {
        broadcastInfoMessage(playerData.getUsername() + " joined!");
        playersData.add(playerData);
        if(isFull()){
            playersData.forEach(player -> player.getClientHandler().moveToGame());
            startGame();
        }
    }

    public void removePlayerData(String nickname) {
        playersData.remove(getPlayerDataByNickname(nickname));
    }

    public boolean isFirstInLobby() {
        return numberOfPlayers == NOT_DECIDED;
    }

    public void broadcastInfoMessage(String message) {
        for(PlayerData playerData : playersData){
            if(playerData.isOnline()) {
                playerData.getClientHandler().sendInfoMessage(message);
            }
        }
    }

    private void startGame(){
        broadcastInfoMessage("Game is starting...");
        controller = new Controller(playersData.stream().map(PlayerData::getUsername).collect(Collectors.toList()));
        List<VirtualView> virtualViews = new ArrayList<>();
        playersData.forEach(playerData -> virtualViews.add(new VirtualView(playerData)));
        controller.setupObservers(virtualViews);
    }
}