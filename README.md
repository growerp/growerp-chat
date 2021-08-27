## GrowERP chat

Under development!

this is companion for flutter GrowERP to enable simple chatting in a room or privately between company employees and customers/suppliers. All messages are exchanged in JSON format.

It is a component which will run independently of the Moqui backend system
but will communicate over a REST interface for user information and authorization.

to build: ./gradlew build
to run in a jetty server locally: ./gradlew appRun
to access in a browser on http://localhost:8080 for several different clients.

### Relevant articles where this component is based on:

- [A Guide to the Java API for WebSocket](https://www.baeldung.com/java-websockets)
- [A nice simple introduction](https://learn.vonage.com/blog/2018/10/22/create-websocket-server-java-api-dr/#)

The second one is easy to create locally.
The first one a bit more difficult but was the basis of this component.

We also need:
a simple REST client: https://www.javacodegeeks.com/2012/09/simple-rest-client-in-java.html
encode/decode json: https://www.w3schools.in/json/json-java/