package it.polimi.ingsw.server.model;

import it.polimi.ingsw.TestGameGenerator;
import it.polimi.ingsw.server.exceptions.InvalidEventException;
import it.polimi.ingsw.server.exceptions.InvalidSetupException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.StorableResource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SetupManagerTest {

    @Test
    void setupAction() throws InvalidEventException, InvalidSetupException {
        TestGameGenerator game = new TestGameGenerator();
        ModelInterface modelInterface = game.modelInterfaceGenerator(false);
        modelInterface.startSetup();

        List<Integer> chosenLeaderCardIndexes = new ArrayList<>();
        List<String> chosenResources = new ArrayList<>();
        List<Resource> correctResources = new ArrayList<>();

        int invalidIndex = 4;  //(0 =< index <= 3)
        int negativeIndex = -1;

        //not existing nickname
        chosenLeaderCardIndexes.add(1);
        chosenLeaderCardIndexes.add(3);
        assertThrows(InvalidEventException.class, () -> modelInterface.setupAction("NonExistingPlayer", chosenLeaderCardIndexes, chosenResources));

        //invalid chosenLeaderCardIndexes
        chosenLeaderCardIndexes.clear();
        chosenLeaderCardIndexes.add(1);
        chosenLeaderCardIndexes.add(invalidIndex);
        assertThrows(InvalidSetupException.class, () -> modelInterface.setupAction("first", chosenLeaderCardIndexes, chosenResources));
        chosenLeaderCardIndexes.add(negativeIndex);
        assertThrows(InvalidSetupException.class, () -> modelInterface.setupAction("first", chosenLeaderCardIndexes, chosenResources));

        //too many chosen indexes (max 2)
        chosenLeaderCardIndexes.add(3);
        chosenLeaderCardIndexes.add(0);
        assertThrows(InvalidSetupException.class, () -> modelInterface.setupAction("first", chosenLeaderCardIndexes, chosenResources));

        //invalid chosenResources number: first player cannot choose any resources
        chosenLeaderCardIndexes.clear();
        chosenLeaderCardIndexes.add(3);
        chosenLeaderCardIndexes.add(0);
        chosenResources.add("blue");
        assertThrows(InvalidSetupException.class, () -> modelInterface.setupAction("first", chosenLeaderCardIndexes, chosenResources));

        //cannot choose NonStorableResources (WHITE or RED)
        chosenResources.clear();
        chosenResources.add("red");
        assertThrows(InvalidSetupException.class, () -> modelInterface.setupAction("second", chosenLeaderCardIndexes, chosenResources));

        //cannot choose a not existing resource's type
        chosenResources.clear();
        chosenResources.add("gold"); //not existing resource's type
        assertThrows(InvalidSetupException.class, () -> modelInterface.setupAction("second", chosenLeaderCardIndexes, chosenResources));

        //correct setup for the first player: 2 LeaderCards and 0 resources
        chosenResources.clear();
        modelInterface.setupAction("first", chosenLeaderCardIndexes, chosenResources);
            //first player should now have 2 LeaderCards and 0 resources
        assertEquals(2, modelInterface.getPlayerByNickname("first").getLeaderHand().size());
        assertEquals(0, modelInterface.getPlayerByNickname("first").getPersonalBoard().getWarehouse().getAllResources().size());

        //correct setup for second and third players: 2 LeaderCards and 1 resources
        chosenResources.add("yellow");
        modelInterface.setupAction("second", chosenLeaderCardIndexes, chosenResources);
        modelInterface.setupAction("third", chosenLeaderCardIndexes, chosenResources);
            //second and third players should now have 2 LeaderCards and 1 resources (YELLOW)
        correctResources.add(new StorableResource(ResourceEnum.YELLOW));
        assertEquals(2, modelInterface.getPlayerByNickname("second").getLeaderHand().size());
        assertEquals(correctResources, modelInterface.getPlayerByNickname("second").getPersonalBoard().getWarehouse().getAllResources());
        assertEquals(2, modelInterface.getPlayerByNickname("third").getLeaderHand().size());
        assertEquals(correctResources, modelInterface.getPlayerByNickname("third").getPersonalBoard().getWarehouse().getAllResources());
            //third player should also have 1 FaithPoint
        assertEquals(0, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getPlayerByNickname("first")).getFaithMarker());
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getPlayerByNickname("third")).getFaithMarker());

        //first player cannot do another setup action
        assertThrows(InvalidEventException.class, () -> modelInterface.setupAction("first", chosenLeaderCardIndexes, chosenResources));

        //correct setup for fourth player: 2 LeaderCards and 1 resources and 1 FaithPoint
        chosenResources.add("purple");
        modelInterface.setupAction("fourth", chosenLeaderCardIndexes, chosenResources);
        correctResources.add(new StorableResource(ResourceEnum.PURPLE));
        assertEquals(2, modelInterface.getPlayerByNickname("fourth").getLeaderHand().size());
        assertEquals(correctResources, modelInterface.getPlayerByNickname("fourth").getPersonalBoard().getWarehouse().getAllResources());
        assertEquals(1, GameBoard.getGameBoard().getFaithTrackOfPlayer(modelInterface.getPlayerByNickname("fourth")).getFaithMarker());

        //now no player have to do the setup action
        assertThrows(InvalidEventException.class, () -> modelInterface.setupAction("second", chosenLeaderCardIndexes, chosenResources));
        assertThrows(InvalidEventException.class, () -> modelInterface.setupAction("third", chosenLeaderCardIndexes, chosenResources));
        assertThrows(InvalidEventException.class, () -> modelInterface.setupAction("fourth", chosenLeaderCardIndexes, chosenResources));

    }
}