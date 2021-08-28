package org.growerp.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.growerp.model.Message;
import org.growerp.rest.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ServerEndpoint(value = "/chat/{username}/{apiKey}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {
    private Session session;
    private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();


    @OnOpen
    public void onOpen(Session session,
        @PathParam("username") String username,
        @PathParam("apiKey") String apiKey) throws IOException, EncodeException {
        Logger logger = LoggerFactory.getLogger(ChatEndpoint.class);
        logger.info("New connection received with username:" + username + " apiKey:" + apiKey );
        // validate connection
        RestClient restClient = new RestClient("http://localhost:8080/rest/s1/growerp/100/");
        if (restClient.validate(apiKey)) {
            logger.info("New connection accepted with username:" + username);

            this.session = session;
            chatEndpoints.add(this);
            users.put(session.getId(), username);

            Message message = new Message();
            message.setFrom(username);
            message.setContent("Connected!");
            broadcast(message);
        } else logger.info("Connection with username:" + username + " ignored");
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        message.setFrom(users.get(session.getId()));
        if (message.getFrom() == null) broadcast(message);
        else chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    Boolean found = false;
                    if (endpoint.session.getId() == message.getFrom()) {
                        endpoint.session.getBasicRemote()
                            .sendObject(message);
                        found = true;
                    }
                    if (found == false) {
                        Message messageError = new Message();
                        messageError.setTo(session.getId());
                        messageError.setFrom("System");
                        messageError.setContent("Connected!");                        message.setContent("User " + session.getId() + " not found");
                        session.getBasicRemote().sendObject(message);
                    }
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        chatEndpoints.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(Message message) throws IOException, EncodeException {
        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote()
                        .sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
