package com.dd.game.network.handler.request;

import com.dd.game.core.GameEngine;
import com.dd.game.core.ThreadPoolManager;
import com.dd.game.entity.player.Player;
import com.dd.game.entity.player.PlayerManager;
import com.dd.game.module.event.EventDispatcher;
import com.dd.game.module.event.EventType;
import com.dd.game.network.handler.AbstractRequestHandler;
import com.dd.game.utils.ConstantsPush;
import com.dd.protobuf.PBStructProtocol.PBPlayerInfo;
import com.dd.protobuf.PlayerProtocol.*;
import com.dd.server.annotation.MsgHandler;
import com.dd.server.annotation.MsgHandlerParse;
import com.dd.server.annotation.MultiHandler;
import com.dd.server.request.Request;
import com.google.protobuf.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Stack;

@MultiHandler
@MsgHandler(id = "player", name = "玩家信息", parses = {@MsgHandlerParse(id = PlayerRequestHandler.REQUEST_GET_INFO, clazz = CSPlayerInfo.class), @MsgHandlerParse(id = PlayerRequestHandler.REQUEST_SET_INFO, clazz = CSPlayerInfo.class), @MsgHandlerParse(id = PlayerRequestHandler.REQUEST_INFOS, clazz = CSPlayerInfos.class), @MsgHandlerParse(id = PlayerRequestHandler.REQUEST_REGISTER, clazz = CSRegister.class)})
public class PlayerRequestHandler extends AbstractRequestHandler {
    protected static final String REQUEST_GET_INFO = "get.info";
    protected static final String REQUEST_SET_INFO = "set.info";
    protected static final String REQUEST_INFOS = "infos";
    protected static final String REQUEST_REGISTER = "register";
    private static final Logger logger = LoggerFactory.getLogger(PlayerRequestHandler.class);

    private static int getNameLength(String name) {
        int ret = 0;
        if (StringUtils.isNotBlank(name)) {
            // 泰文 0E00-0E7F
            // 126-1535各种拉丁文ascii
            try {
                int length = name.length();
                StringBuilder compressedName = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    char c = name.charAt(i);
                    if (126 <= (int) c && 1535 >= (int) c) {
                        ret += 1; // 拉丁系字母占两个长度
                    } else {
                        compressedName.append(c);
                    }
                }
                ret += compressedName.toString().getBytes("gbk").length;
                // ret = name.getBytes("gbk").length;
            } catch (UnsupportedEncodingException e) {
                logger.error("get name:{} length error!", name, e);
            }
        }
        return ret;
    }

    private static boolean checkName(String str) {
        char[] searchChar = {'{', '}'};
        if (!StringUtils.containsAny(str, searchChar)) {
            return true;
        }
        Stack<Character> stack = new Stack<>();
        char[] charArray = str.toCharArray();
        boolean ret = true;
        for (int index = 0; index < charArray.length; index++) {
            if (charArray[index] == '{') {
                stack.push('{');
            } else if (charArray[index] == '}') {
                if (!stack.isEmpty()) {
                    stack.pop();
                } else {
                    ret = false;
                    break;
                }
            }
        }
        if (ret && !stack.isEmpty()) {
            ret = false;
        }
        return ret;
    }

    @Override
    public Message handle(Player player, Request msg) throws Exception {
        switch (msg.getMultiHandlerRequestId()) {
            case REQUEST_GET_INFO: {
                CSPlayerInfo req = msg.getBody();
                SCPlayerInfo.Builder resp = SCPlayerInfo.newBuilder();
                if (req.getUid() != null) {
                    Player p = PlayerManager.getInstance().getPlayer(Long.parseLong(req.getUid()));
                    if (p != null) {
                        resp.setPlayerInfo(p.toSimpleProtoBuf());
                    }
                } else {
                    resp.setPlayerInfo(player.toSimpleProtoBuf());
                }
                return resp.build();
            }
            case REQUEST_INFOS: {
                ThreadPoolManager.execute(() -> {
                    CSPlayerInfos req = msg.getBody();
                    SCPlayerInfos.Builder resp = SCPlayerInfos.newBuilder();
                    for (PBPlayerInfo info : req.getPlayersList()) {
                        try {
                            Player p = PlayerManager.getInstance().getPlayer(Long.valueOf(info.getUserId()));
                            if (p != null) {
                                resp.addPlayerInfo(p.toSimpleProtoBuf());
                            }
                        } catch (Exception e) {
                            logger.error("get player {}, sid {} info error!", info.getUserId(), info.getServerId(), e);
                        }
                    }
                    player.send(msg.getId(), resp.build());
                });
                return null;
            }
            case REQUEST_SET_INFO: {
                CSPlayerInfo req = msg.getBody();
                if (req.getGaid() != null) {
                    player.setGaid(req.getGaid());
                    if (StringUtils.isBlank(player.getGaid0())) {
                        player.setGaid0(req.getGaid());
                    }
                    GameEngine.getEData().update(player);
                }
                if (req.getCountry() != null) {
                    player.setNation(req.getCountry());
                    player.updateRedisCache();
                    EventDispatcher.fire(EventType.PLAYER_ALTER_NATION, player);
                }
                if (req.getPlatform() != null) player.setPlatform(req.getPlatform());
                if (req.getCountry() != null || req.getPlatform() != null) player.update(true);
                player.send(ConstantsPush.PLAYER_INFO, player.toSimpleProtoBuf());
                return CSPlayerInfo.newBuilder().build();
            }
            case REQUEST_REGISTER: {
                player.register();
                return SCRegister.newBuilder().setPlayerInfo((PBPlayerInfo) player.toProtoBuf()).build();
            }
            default:
                break;
        }
        return null;
    }
}
