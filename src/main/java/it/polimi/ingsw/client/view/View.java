package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.model.PersonalBoard;

import java.util.List;
import java.util.Map;

public interface View {
    void setNickname(String nickname);

    String getNickname();

    void setIsPlaying(boolean isPlaying);

    boolean isThisClientTurn();

    void showWaitAnimation();

    void startNetwork();

    void printInfoMessage(String info);

    void printErrorMessage(String error);

    void setOnLogin();

    void setOnChooseNumberOfPlayers(String payload);

    void setOnMatchMaking();

    void setOnSetup(List<String> leaderCardsID, int numberOfResource);

    void setOnYourTurn();

    void setOnWaitForYourTurn(String currentPlayer);

    void setOnDevelopmentCardPlacement(String newCardID);

    void setOnResourcesPlacement();

    void setOnTransformation(int numberOfTransformation, List<String> possibleTransformations);

    void setOnEndTurn();

    void setOnEndGame(String winner, Map<String, Integer> playersPoints);

    void marketUpdate();

    void gridUpdate(String iD);

    void faithTracksUpdate();

    /*
    void activeLeadersUpdate(PersonalBoard personalBoard);
    void warehouseUpdate(PersonalBoard personalBoard);
    void productionBoardUpdate(PersonalBoard updatingPersonalBoard);

     */

    void personalBoardUpdate(PersonalBoard updatingPersonalBoard);
}
