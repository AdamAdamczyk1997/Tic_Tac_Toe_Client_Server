import database.GamesEntity;
import database.HibernateMongoSessionUtils;
import database.Move;
import database.UserEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;


class GameController extends Thread {
    private TicTacToe game;
    private Scanner scanner, scanner2;

    private String nickPlayer1, nickPlayer2;
    private BufferedReader firstPlayerInStream, secondPlayerInStream;
    private DataOutputStream firstPlayerOutStream, secondPlayerOutStream;
    private PrintWriter firstPlayerPrintWriter, secondPlayerPrintWriter;

    // Functions to create a session and save information in mongodb
    public static Session getSession() throws HibernateException {
        return HibernateMongoSessionUtils.getInstance().openSession();
    }

    public void addUser(String nickPlayer1){
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        UserEntity user = new UserEntity(nickPlayer1);

        session.save(user);

        tx.commit();

    }

    public void addGame(){
        Session session = getSession();
        Transaction txy = session.beginTransaction();
        GamesEntity match = new GamesEntity();

        match.setId_u1(nickPlayer1);
        match.setId_u2(nickPlayer2);
        match.setResult(game.result());
        Date date= new Date();
        match.setDate(date);
        System.out.println(match);
        session.save(match);

        txy.commit();
        session.close();
    }

    public void addMove(int id, int field){
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        Move move = new Move();
        move.setId_u(id);
        move.setField(field);
        Date date= new Date();
        move.setCreated_at(date);

        session.save(move);

        tx.commit();
        session.close();
    }

    // Establish and begin a new game of TicTacToe.
    public void start() {

        connectPlayers();
        playGame();

    }

    private void connectPlayers() {
        try {
            Socket clientSocket = Server.server.accept();
            firstPlayerInStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            firstPlayerOutStream = new DataOutputStream(clientSocket.getOutputStream());
            firstPlayerPrintWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("A user has connected from " + clientSocket.getInetAddress());
            System.out.println("Waiting for another player");
            String message = "Wait...";
            firstPlayerPrintWriter.println(message + "\n");

            Socket clientSocket2 = Server.server.accept();
            secondPlayerInStream = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
            secondPlayerOutStream = new DataOutputStream(clientSocket2.getOutputStream());
            secondPlayerPrintWriter = new PrintWriter(clientSocket2.getOutputStream(), true);
            System.out.println("A user has connected from " + clientSocket2.getInetAddress());
            secondPlayerPrintWriter.println(message + "\n");

            nickPlayer1 = firstPlayerInStream.readLine();
            nickPlayer2 = secondPlayerInStream.readLine();

            message = "The game is beginning!";
            firstPlayerPrintWriter.println(message);
            secondPlayerPrintWriter.println(message);

            sleep(10);

            firstPlayerPrintWriter.println(nickPlayer2);
            secondPlayerPrintWriter.println(nickPlayer1);

            addUser(nickPlayer1);
            addUser(nickPlayer2);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void playGame() {

        scanner = new Scanner(firstPlayerInStream);
        scanner2 = new Scanner(secondPlayerInStream);

        game = new TicTacToe(nickPlayer1, nickPlayer2);

        System.out.println("The game is on: " + nickPlayer1 + " vs " + nickPlayer2 );

        if( game.chooseFirstPlayer() == 1){
            firstPlayerPrintWriter.println(0);
            secondPlayerPrintWriter.println(1);
        } else {
            secondPlayerPrintWriter.println(0);
            firstPlayerPrintWriter.println(1);
        }

        sleep(10);

        // Play until the game is over!
        while (game.isOver())
        {
            // If the player2 has the first move...
            if (game.getFirstTurn() == game.getPLAYER2_TURN())
            {
                try {
                    doPlayer2Turn();
                    sleep(10);
                    secondPlayerOutStream.writeBytes(game.drawBoard());
                    firstPlayerOutStream.writeBytes(game.drawBoard());

                } catch (IOException | CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                if (game.isOver()) {
                    try {
                        doPlayerTurn();
                        sleep(10);
                        firstPlayerOutStream.writeBytes(game.drawBoard());
                        secondPlayerOutStream.writeBytes(game.drawBoard());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else // Otherwise, the player1 has the first move...
            {
                try {
                    doPlayerTurn();
                    sleep(10);
                    firstPlayerOutStream.writeBytes(game.drawBoard());
                    secondPlayerOutStream.writeBytes(game.drawBoard());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (game.isOver()) {
                    try {
                        doPlayer2Turn();
                        sleep(10);
                        secondPlayerOutStream.writeBytes(game.drawBoard());
                        firstPlayerOutStream.writeBytes(game.drawBoard());
                    } catch (IOException | CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Determine how the game ended and alert user.
        handleEndGame();

        // Ascertain if user would like to play again.
        handleEndGameDecision();
    }

    private void handleEndGame() {
        switch (game.result()) {
            // Player1 has won.
            case 1 -> {
                System.out.println("The " + nickPlayer1 + " has won the game.");
                try {
                    firstPlayerOutStream.writeBytes("#P\n");
                    secondPlayerOutStream.writeBytes("#C\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Player2 has won.
            case 2 -> {
                System.out.println("The " + nickPlayer2 + " has won the game.");
                try {
                    firstPlayerOutStream.writeBytes("#C\n");
                    secondPlayerOutStream.writeBytes("#P\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Game is a tie.
            case 3 -> {
                System.out.println("The game is a tie.");
                try {
                    firstPlayerOutStream.writeBytes("#T\n");
                    secondPlayerOutStream.writeBytes("#T\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleEndGameDecision() {
        addGame();
        System.out.println("Determining if users would like to play another game...");

        String fpDecision = "";
        String spDecision = "";
        boolean stop = false;

        while ( !stop ) {
            try {
                fpDecision = firstPlayerInStream.readLine();
                System.err.println(" Message from " + nickPlayer1 + " " + fpDecision);
                    if (fpDecision.equals("#NG")) {
                        secondPlayerPrintWriter.println("rematch\n");
                        firstPlayerPrintWriter.println("Wait...\n");
                        stop = true;
                    } else if (fpDecision.equals("#CG")) {
                        secondPlayerPrintWriter.println("close\n");
                        System.out.println("The user " + nickPlayer1 + " would NOT like to play another game.");
                        closeStreams();
                        start();
                    }

                spDecision = secondPlayerInStream.readLine();
                System.err.println(" Message from " + nickPlayer2 + " " + fpDecision);

                     if (spDecision.equals("#NG")) {
                        firstPlayerPrintWriter.println("rematch\n");
                        secondPlayerPrintWriter.println("Wait...\n");
                        stop = true;
                    }
                     else if (spDecision.equals("#CG")) {
                        firstPlayerPrintWriter.println("close\n");
                        System.out.println("The user " + nickPlayer2 + " would NOT like to play another game.");
                        closeStreams();
                        start();
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

            // If applicable, restart the game for the user.
            if (fpDecision.equals("#NG") && spDecision.equals("#NG"))
            {
                firstPlayerPrintWriter.println("start\n");
                secondPlayerPrintWriter.println("start\n");
                System.out.println("The users would like to play another game.");
                this.playGame();
            } else { // Otherwise, lose the streams and exit back to Server.
                closeStreams();
                start();
            }

    }

    private void closeStreams() {
        try {
            firstPlayerInStream.close();
            firstPlayerOutStream.close();
            secondPlayerInStream.close();
            secondPlayerOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // / Run through the player's turn.
    public void doPlayerTurn() throws IOException {

        if(game.getWhoseTurn() != game.getPLAYER_TURN() ) {
            game.setWhoseTurn(game.getPLAYER_TURN());

            int userMove = -1;

            // Ask the user for his or her move until he or she enters a valid one.
            while (game.legalMove(userMove)) {

                // Get the user's desired move.
                try {
                    userMove = scanner.nextInt();
                }

                // If the user enters a nonInt value, set his or her move equal to -1 (to force reentry).
                // Reset the scanner to avoid endless looping.
                catch (InputMismatchException e) {
                    userMove = -1;
                    scanner = new Scanner(firstPlayerInStream);
                    System.out.println(nickPlayer1 + " wrong move!");
                }
            }

            // Make the user's legal move.
            game.placePiece(game.getPLAYER_TURN(), userMove);

            System.out.println(nickPlayer1 + " has moved to " + userMove);
            addMove(1,userMove);

        } else {
            System.out.println("Something wrong with queue!");
        }


    }

    public void doPlayer2Turn() throws IOException, CloneNotSupportedException {

        if (game.getWhoseTurn() != game.getPLAYER2_TURN()) {
            game.setWhoseTurn(game.getPLAYER2_TURN());

            int userMove = -1;

            while (game.legalMove(userMove)) {

                try {
                    userMove = scanner2.nextInt();


                } catch (InputMismatchException e) {
                    userMove = -1;
                    scanner2 = new Scanner(secondPlayerInStream);
                    System.out.println(nickPlayer2 + " Wykonał ruch niedozwolony!");
                }
            }

            // Make the user's legal move.
            game.placePiece(game.getPLAYER2_TURN(), userMove);
            addMove(2,userMove);

            System.out.println(nickPlayer2 + " has moved to " + userMove);
        }

        else {
            System.out.println("Something is missing!");
        }
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
