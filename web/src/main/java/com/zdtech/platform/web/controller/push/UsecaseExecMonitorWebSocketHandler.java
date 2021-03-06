package com.zdtech.platform.web.controller.push;

import com.zdtech.platform.framework.rbac.ShiroUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LogWebSocketHandler
 *
 * @author panli
 * @date 2016/9/4
 */
public class UsecaseExecMonitorWebSocketHandler implements WebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(UsecaseExecMonitorWebSocketHandler.class);
    private static Map<String,WebSocketSession> userMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        ShiroUser user = (ShiroUser) webSocketSession.getAttributes().get("shiroUser");
        String batchId = (String) webSocketSession.getAttributes().get("batchId");
        if (user != null && batchId != null){
            Long userId = user.getUserId();
            addUser(userId,batchId,webSocketSession);
            logger.info("日志监控连接建立，用户：{}，用例执行批次Id：{}",userId,batchId);
        }
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if(webSocketSession.isOpen()){
            webSocketSession.close();
        }
        Long userId = getUserId(webSocketSession);
        if (userId != null){
            delUser(userId);
            logger.info("日志监控连接传输错误，删除连接，用户：{}",userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        Long userId = getUserId(webSocketSession);
        if (userId != null){
            delUser(userId);
            logger.info("日志监控连接关闭，用户：{}",userId);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendMessageToUser(Long simInsId,TextMessage message){
        List<String> list = getUsers(simInsId);
        if (list != null && list.size() > 0){
            for (String id:list){
                try {
                    userMap.get(id).sendMessage(message);
                    logger.info("日志监控推送成功，用户：{}，消息：{}",id,message.toString());
                } catch (IOException e) {
                    logger.error("日志监控推送失败，用户id：{}，消息：{},error:{}",id,message.toString(),e.getMessage());
                    e.printStackTrace();
                } catch (Exception e){
                    logger.error("日志监控推送失败，用户id：{}，消息：{},error:{}",id,message.toString(),e.getMessage());
                    e.printStackTrace();
                }
            }

        }else {
            logger.info("日志监控，未推送给任何用户，用例执行批次Id：{}",simInsId);
        }
    }


    public void sendMessageToUser(Long userId,Long simInsId, TextMessage message){
        WebSocketSession session = getUser(userId,simInsId);
        if (session != null){
            try {
                session.sendMessage(message);
                logger.info("日志监控推送成功，用户：{}，消息：{}",userId,message.toString());
            } catch (IOException e) {
                logger.error("日志监控推送失败，用户id：{}，消息：{},error:{}",userId,message.toString(),e.getMessage());
                e.printStackTrace();
            } catch (Exception e){
                logger.error("日志监控推送失败，用户id：{}，消息：{},error:{}",userId,message.toString(),e.getMessage());
                e.printStackTrace();
            }
        }else {
            logger.info("日志监控推送失败，未找到用户session，用户：{},session map:{}",userId,userMap);
        }
    }

    public void sendMessageToUsers(List<Long> userIds,Long simInsId,TextMessage message) {
        if(userIds == null || userIds.size() < 1){
            logger.warn("日志监控，未找到要推送的用户");
            return;
        }
        for (Long id:userIds){
            logger.info("日志监控推送给用户id：{}",id);
            sendMessageToUser(id,simInsId,message);
        }
    }

    private static void addUser(Long user,String batchId,WebSocketSession session){
        String userId = user.toString();
        delUser(user);
        userMap.put(String.format("%s_%s",userId,batchId),session);
        logger.info("日志监控用户接入，用户：{}，用例执行批次Id：{}",user,batchId);
    }
    private static void delUser(Long user){
        String userId = user.toString();
        List<String> delList = new ArrayList<>();
        for (String id:userMap.keySet()){
            String[] ids = id.split("_");
            if (ids[0].equals(userId)){
                delList.add(id);
            }
        }
        for (String id:delList){
            userMap.remove(id);
        }
    }
    private static WebSocketSession getUser(Long user,Long simIns){
        //String userId = user.toString();
        //String simInsId = simIns.toString();
        String id = String.format("%d_%d",user,simIns);
        return userMap.get(id);
    }

    private static List<String> getUsers(Long simIns){
        String simInsId = simIns.toString();
        List<String> list = new ArrayList<>();
        for (String e:userMap.keySet()){
            String[] ids = e.split("_");
            if (ids[1].equals(simInsId)){
                list.add(e);
            }
        }
        return list;
    }
    private Long getUserId(WebSocketSession webSocketSession){
        ShiroUser user = (ShiroUser) webSocketSession.getAttributes().get("shiroUser");
        if (user == null){
            return null;
        }
        return user.getUserId();
    }
}
