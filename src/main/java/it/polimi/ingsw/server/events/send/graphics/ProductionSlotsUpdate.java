package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductionSlotsUpdate implements PersonalUpdate {
    @Override
    public void addUpdateTo(PersonalBoardUpdate personalBoardUpdate, Player player) {
        personalBoardUpdate.setNickname(player.getNickname());
        List<List<String>> slots = player.getPersonalBoard().getVisibleDevelopmentCardsIDs().stream().map(slot -> new ArrayList<String>() {{
            add(slot);
        }}).collect(Collectors.toList());
        personalBoardUpdate.setProductionBoard(slots);
    }
}
