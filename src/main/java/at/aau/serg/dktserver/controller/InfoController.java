package at.aau.serg.dktserver.controller;

import at.aau.serg.dktserver.communication.InfoJsonObject;
import at.aau.serg.dktserver.communication.Wrapper;
import at.aau.serg.dktserver.communication.enums.Info;
import at.aau.serg.dktserver.communication.enums.Request;
import at.aau.serg.dktserver.communication.utilities.WrapperHelper;
import at.aau.serg.dktserver.model.domain.GameInfo;
import at.aau.serg.dktserver.model.domain.PlayerData;
import at.aau.serg.dktserver.websocket.handler.WebSocketHandlerImpl;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InfoController {
    private GameManager gameManager;
    private WebSocketHandlerImpl webSocket;
    private Gson gson;

    public InfoController(){
        this.gameManager = GameManager.getInstance();
        this.webSocket = WebSocketHandlerImpl.getInstance();
        this.gson = new Gson();
    }
    public void receiveInfo(Info info, int gameId, String fromPlayername){
        switch (info){
            case GAME_LIST -> receiveGameList(fromPlayername);
            case CONNECTED_PLAYERNAMES -> receiveConnectedPlayers(gameId, fromPlayername);
        }
    }


    private void receiveGameList(String fromPlayername){
        System.out.println("receiveGameList() -> called!");
        // Todo
        List<GameInfo> gameInfos = gameManager.getGamesAndPlayerCount2();

        InfoJsonObject infoJsonObject = new InfoJsonObject(Info.GAME_LIST, gameInfos);
        PlayerData playerData = webSocket.getPlayerByUsername(fromPlayername);
        Wrapper wrapper = new Wrapper(infoJsonObject.getClass().getSimpleName(), playerData == null ? -1 : playerData.getGameId(), Request.INFO, infoJsonObject);

        webSocket.sendToUser(fromPlayername, gson.toJson(wrapper));
    }

    private void receiveConnectedPlayers(int gameId, String fromPlayername) {
        List<GameInfo> gameInfos = new ArrayList<>();
        GameInfo gameInfo = new GameInfo(gameId, null, gameManager.getPlayerNames(gameId));
        gameInfos.add(gameInfo);
        InfoJsonObject infoJsonObject = new InfoJsonObject(Info.CONNECTED_PLAYERNAMES, gameInfos);
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.INFO, infoJsonObject);

        webSocket.sendToUser(fromPlayername, msg);
    }
}
