package it.polimi.ingsw.server.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.events.receive.*;
import it.polimi.ingsw.server.model.gameMode.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class ServerParser extends Parser {
    public static final String SINGLE_FAITH_TOKEN_TYPE = "SingleFaithTrackProgressToken";
    public static final String DOUBLE_FAITH_TOKEN_TYPE = "DoubleFaithTrackProgressToken";
    public static final String DISCARD_CARD_TOKEN_TYPE = "DiscardDevCardsToken";


    private static final Map<String, Object> eventFromClientTypes = new HashMap<String, Object>() {{
        put(BUY_ACTION_TYPE, BuyEventFromClient.class);
        put(CARD_PLACEMENT_ACTION_TYPE, PlaceDevelopmentCardEventFromClient.class);
        put(SETUP_ACTION_TYPE, SetupEventFromClient.class);
        put(END_TURN_ACTION_TYPE, EndTurnEventFromClient.class);
        put(LEADER_ACTION_TYPE, LeaderHandEventFromClient.class);
        put(MARKET_ACTION_TYPE, MarketEventFromClient.class);
        put(PRODUCTION_ACTION_TYPE, ProductionEventFromClient.class);
        put(RESOURCES_PLACEMENT_ACTION_TYPE, PlaceResourcesEventFromClient.class);
        put(TRANSFORMATION_ACTION_TYPE, TransformationEventFromClient.class);
        put(RECONNECT_TYPE, ReconnectEventFromClient.class);
        put(DISCONNECT_TYPE, DisconnectEventFromClient.class);
        put(CHEAT_TYPE, CheatEventFromClient.class);
    }};

    private static final Map<String, Object> tokensType = new HashMap<String, Object>() {{
        put(SINGLE_FAITH_TOKEN_TYPE, SingleFaithTrackProgressTokenNoShuffle.class);
        put(DOUBLE_FAITH_TOKEN_TYPE, DoubleFaithTrackProgressToken.class);
        put(DISCARD_CARD_TOKEN_TYPE, DiscardDevCardsToken.class);
    }};

    public static SoloActionToken getTokenFromJsonElement(JsonElement message) {
        try {
            JsonObject token = message.getAsJsonObject();
            Type eventType = (Type) tokensType.get(token.get(TYPE_ID).getAsString());
            return gson.fromJson(token, eventType);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EventFromClient getEventFromClient(JsonElement message) {
        try {
            JsonObject action = message.getAsJsonObject();
            Type eventType = (Type) eventFromClientTypes.get(action.get(TYPE_ID).getAsString());
            return gson.fromJson(action, eventType);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
