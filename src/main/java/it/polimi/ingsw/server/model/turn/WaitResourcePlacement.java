package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.client.events.receive.PlaceResourcesReceiveEvent;
import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.events.send.choice.EndTurnChoiceEvent;
import it.polimi.ingsw.server.events.send.choice.PlaceResourcesChoiceEvent;
import it.polimi.ingsw.server.events.send.graphics.FaithTracksUpdate;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.events.send.graphics.PersonalBoardUpdate;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;
import java.util.List;

public class WaitResourcePlacement extends State {
    public WaitResourcePlacement(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Reorder the warehouse and change the state of the game to EndTurn. If the Player has some remaining resource
     * to store increases the FaithProgress of the other players.
     *
     * @param swapPairs List of all the swaps to be applied
     * @return true if the warehouse reordering is legal
     * @throws InvalidEventException      if the swaps cannot be applied
     * @throws InvalidIndexException      if a swap contains a negative position
     * @throws EmptySlotException         if a swap involves an empty slot
     * @throws NonAccessibleSlotException if one of swap involves a slot that's not accessible
     */
    @Override
    public boolean placeResourceAction(List<Integer> swapPairs,boolean isFinal) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        //todo ricordarsi che le risorse dal market possono essere anche tolte (in caso di not legal)
        //todo rivedere cosa va mandato in caso di invalid!!!!!
        if (swapPairs.size() % 2 != 0) {
            //resend place choice
            turnLogic.setLastEventSent(new PlaceResourcesChoiceEvent(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
            throw new InvalidEventException("every swap should always have an initPosition and a finalPosition"); //a swap should always have an initPosition and a finalPosition
        }
        Warehouse warehouse = turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse();

        for (int i = 0; i < swapPairs.size(); i = i + 2)
            if (!warehouse.swap(swapPairs.get(i), swapPairs.get(i + 1))) {
                //send current state of whole warehouse
                GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
                graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
                turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
                //resend choice
                turnLogic.setLastEventSent(new PlaceResourcesChoiceEvent(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
                throw new InvalidEventException("the swap cannot be applied"); //the swap cannot be applied
            }
        if (isFinal && warehouse.isProperlyOrdered()) {
            //faith progress for other players based on the number of remaining resources
            GameBoard.getGameBoard().faithProgressForOtherPlayers(turnLogic.getCurrentPlayer(), warehouse.getNumberOfRemainingResources());

            //graphic update of player's warehouse and players faithTracks
            GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
            graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
            graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
            turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
            turnLogic.setLastEventSent(null);
            turnLogic.setCurrentState(turnLogic.getEndTurn());
            turnLogic.getModelInterface().notifyObservers(new EndTurnChoiceEvent(turnLogic.getCurrentPlayer().getNickname()));
            return true;
        }

        //graphic update of player's illegal/not final warehouse
        GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
        graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
        turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);

        PlaceResourcesChoiceEvent placeResourcesReceiveEvent = new PlaceResourcesChoiceEvent(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse());
        //if not final simply resend the choice event
        if(!isFinal){
            turnLogic.getModelInterface().notifyObservers(placeResourcesReceiveEvent);
            return true;
        }
        //if final send an error message of illegal warehouse reordering
        //prepare placeEvent that will be sent after error message
        turnLogic.setLastEventSent(placeResourcesReceiveEvent);
        throw new InvalidEventException("Illegal Warehouse reordering");
    }
}
