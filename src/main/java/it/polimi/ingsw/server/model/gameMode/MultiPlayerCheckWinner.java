package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.player.Player;

import java.util.List;

/**
 * This class contains the methods to check the End of The Game and
 * to find the Winner, in fact it implements the interface ICheckWinner that extends the interface EndGameObserver.
 * It is Observer of the following classes:
 * DevelopmentCardsGrid, FirstOfFaithTrack and PersonalBoard.
 */
public class MultiPlayerCheckWinner implements ICheckWinner {
    private boolean gameOver = false;
    private final List<Player> players;

    public MultiPlayerCheckWinner(List<Player> players) {
        this.players = players;
    }

    /**
     * This method is called by the class FirstOfFaithTrack
     * when a player reaches the End of the Faith Track.
     * Set the attribute gameOver true.
     *
     * @param winner is the reference of the Player
     *               that reaches the last space of the Faith Track.
     */
    @Override
    public void update(PlayerInterface winner) {
        gameOver = true;
    }

    /**
     * This method is called by the class PersonalBoard
     * when a Player buys his seventh Development Card.
     * Set the attribute gameOver true.
     *
     * @param lorenzoWin is set true if Lorenzo is the winner, false otherwise.
     */
    @Override
    public void update(boolean lorenzoWin) {
        if (!lorenzoWin)
            gameOver = true;
    }

    /**
     * Method that
     *
     * @return true if there was a condition of End of the Game
     */
    @Override
    public boolean isTheGameOver() {
        return gameOver;
    }

    /**
     * Method that collects the Victory Points of all the players
     * and declares the Winner.
     *
     * @return the Winner
     */
    @Override
    public PlayerInterface getWinner() {
        int maxPoints = 0;
        PlayerInterface winner = null;

        for (Player player : players) {
            if (player.getPersonalBoard().getPoints(player) > maxPoints) {
                maxPoints = player.getPersonalBoard().getPoints(player);
                winner = player;
            } else if (maxPoints != 0 && player.getPersonalBoard().getPoints(player) == maxPoints &&
                    player.getPersonalBoard().getWarehouse().getAllResources().size() >
                            winner.getPersonalBoard().getWarehouse().getAllResources().size())
                winner = player;
        }
        return winner;
    }
}