
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Set;


public class Server {

    static int PORT = 9999;

    Set<GameController> threads = Collections.synchronizedSet(new HashSet<>());
    static ServerSocket server;

    public Server() {
        try {
            server = new ServerSocket(PORT);
            System.out.println("Server is listening on " + server.getLocalSocketAddress());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        //noinspection InfiniteLoopStatement
        for (; ; ) {
            try {
                // Create a server-game based version of TicTacToe.
                GameController game = new GameController();
                threads.add(game);
                game.start();

                } catch (InputMismatchException e) {
                    e.printStackTrace();
                    System.err.println("The gamed failed to start.");

                }

        }
    }

    public static void main(String[] args) {

        Server server = new Server();
        server.listen();

    }
}


