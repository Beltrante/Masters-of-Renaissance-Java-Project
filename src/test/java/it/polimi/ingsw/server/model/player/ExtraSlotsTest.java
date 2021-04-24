package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExtraSlotsTest {

    ExtraSlots extraSlots = new ExtraSlots();
    @Test
    void simplyExtraSlotTest() {
        Resource correctResource = new StorableResource(ResourceEnum.YELLOW);

        assertFalse(extraSlots.getIsActivated());

        extraSlots.activateExtraSlots(correctResource);
        assertEquals(extraSlots.getResourceType(), correctResource);

        extraSlots.setResource(0, new StorableResource(ResourceEnum.BLUE));
        assertFalse(extraSlots.isLegal());

        extraSlots.setResource(0, correctResource);
        assertTrue(extraSlots.isLegal());

        assertNull(extraSlots.getResource(1)); //empty slot
        assertNull(extraSlots.takeResource(1)); //empty slot

        assertEquals(extraSlots.getResource(0), correctResource);
        assertEquals(extraSlots.takeResource(0), correctResource);

        //after taking a resource, the slot is empty
        assertNull(extraSlots.getResource(0));
        assertNull(extraSlots.takeResource(0));

    }
}