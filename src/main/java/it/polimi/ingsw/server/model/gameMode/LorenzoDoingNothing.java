package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.turn.TurnLogic;

/**
 * This class is set by the class GameMode when there is a MultiPlayers Game.
 */
public class LorenzoDoingNothing implements Lorenzo{
    /**
     * This method do nothing
     *
     * @return false
     * @param turnLogic is the TurnLogic reference
     */
    @Override
    public boolean play(TurnLogic turnLogic) {
        return false;
    }

    /**
     * Get method that
     *
     * @return the nickname of the Player
     */
    @Override
    public String getNickname() {
        return "Lorenzo il Magnifico doing nothing";
    }

    /**
     * Method used for testing
     *
     * @return the Solo Action Token extract
     */
    @Override
    public SoloActionToken extractToken() {
        return null;
    }
}