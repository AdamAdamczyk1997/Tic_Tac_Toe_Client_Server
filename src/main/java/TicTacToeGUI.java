import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class TicTacToeGUI extends JFrame
{

    private String username, opponentUsername;

    private int winCount = 0, lossCount = 0, tieCount = 0;

    private final JFrame frame;
    private final Container content;
    private final JPanel nickPanel, waitingPanel, buttonPanel, optionsPanel, infoPanel;

    public JButton[] buttons;
    private final JLabel optionLabel = new JLabel();
    private final JLabel infoLabel = new JLabel();

    private final JTextField userTextField = new JTextField();
    private final JButton resetButton = new JButton("RESET");
    private final JButton newGameButton = new JButton("rematch");

    private Socket socket;
    private PrintWriter printWriterOut;
    private BufferedReader bufferedReaderIn;

    private boolean isLive = false, turn, isLoggedIn = false, rematch = false;

    public void run() throws HeadlessException{
        showGUI();
        start();
    }

    public void showGUI() throws HeadlessException {
        showLoginPanel();
        frame.setVisible(true);
    }

    private void start(){

        while(!isLoggedIn) {
            sleep(250);
        }

        // Connect to the TicTacToe server.
        connectServer();

        waitingForAnotherPlayer();

        game();


    }

    private void connectServer() {
        boolean isConnected = false;
        do
        {
            try {
                String SERVER_IP = "127.0.0.1";
                int SERVER_PORT = 9999;
                socket = new Socket(SERVER_IP, SERVER_PORT);
                printWriterOut = new PrintWriter(socket.getOutputStream(), true);
                bufferedReaderIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isConnected = true;
                System.out.println("You " + username + " are connected! " );
                printWriterOut.println(username);

            } catch (IOException ee) {
                sleep(10000);
                System.err.println("The specified server host could not be found.");
            }
        }while(!isConnected);
    }

    // Waiting for the opponent
    private void waitingForAnotherPlayer() {
        while ( !isLive ) {
            try {
                String serverCommand;
                serverCommand = bufferedReaderIn.readLine();

                switch (serverCommand) {
                    case "Wait..." -> {
                        System.err.println("Message from server: " + serverCommand);
                        System.out.println("Waiting for another player...");
                        showWaitingPanel();
                    }
                    case "The game is beginning!" -> {
                        System.err.println("Message from server: " + serverCommand);
                        knowTheOpponent();
                        showGrid();
                        isLive = true;
                    }
                    case "start" -> {
                        System.err.println("Message from server: " + serverCommand);
                        resetGrid();
                        showGrid();
                        isLive = true;
                        rematch = false;
                        game();
                    }
                }
            } catch (IOException e) {
                sleep(10);
                e.printStackTrace();
            }
        }

    }

    private void knowTheOpponent(){
        // Knowing the opponent
        try {
            String serverCommand;
            serverCommand = bufferedReaderIn.readLine();
            System.out.println(" You are fighting with " + serverCommand);
            opponentUsername = serverCommand;

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void game(){
        // Asking server whose first
        whoFirst();

        // Allow the user to play the game.
        while (isLive) {
            turn();
        }

        decisionFromOptionPanel();
    }

    private void whoFirst(){
        int who = 0;
        while(who == 0 ) {
            try {
                String serverCommand;
                serverCommand = bufferedReaderIn.readLine();
                System.err.println("  Message from server about who's first: " + serverCommand);

                if (serverCommand.equals("0")) {
                    turn = true;
                    who = 1;

                } else if (serverCommand.equals("1")) {
                    turn = false;
                    who = 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void turn(){

        String info;
        if (!turn) {
            buttonPanel.setEnabled(false);
            System.out.println("Wait for your turn... ");
            info = " Wait for your turn... ";
            infoLabel.setText(info);
            processServerCommands();
            turn = true;
        }
        else {
            buttonPanel.setEnabled(true);
            System.out.println("==> It's your turn.");
            info = " It's your turn. ";
            infoLabel.setText(info);
            processServerCommands();
            turn = false;
        }
        sleep(10);


    }

    // Process any commands sent by the server (which should be a gridStatus string).
    private void processServerCommands() {
            try {

                String serverCommand;
                serverCommand = bufferedReaderIn.readLine();
                System.err.println("Message from server: " + serverCommand);

                // Process any gridStatus strings.
                if (serverCommand.charAt(0) != '#') {
                    updateGrid(serverCommand);
                }

                // The game has ended.  Show the user his or her statistics.
                else {

                    String title, text;

                    String textOptionPanels;
                    if (serverCommand.equals("#T")) {
                        textOptionPanels = " You draw. So what now? ";
                        tieCount++;
                        title = "will and fear are balanced.!";
                        text = "This battle ended in a draw.";
                    } else if (serverCommand.equals("#P")) {
                        textOptionPanels = " You win. So what now? " ;
                        winCount++;
                        title = "the force of will triumphs!";
                        text = "You have won this battle.";
                    } else {
                        textOptionPanels = " You lose. So what now? " ;
                        lossCount++;
                        title = "you have given into fear!";
                        text = "You have lost this battle.";
                    }

                    text += "\nwins: " + winCount + ", ties: " + tieCount + ", losses: " + lossCount;
                    isLive = false;
                    JOptionPane.showMessageDialog(null, text, title, JOptionPane.PLAIN_MESSAGE);
                    showOptions(textOptionPanels);

                }


            } catch (IOException e) {
                System.err.println("Error reading commands from the server.");
            }

    }

    private void decisionFromOptionPanel() {
        while (!rematch) {
            {
                try {

                    String serverCommand;
                    serverCommand = bufferedReaderIn.readLine();
                    System.err.println(" Message from server: " + serverCommand);

                    switch (serverCommand) {
                        case "rematch" -> {
                            rematch = true;
                            optionLabel.setText(opponentUsername + " want a rematch! ");
                        }
                        case "close" -> {
                            optionLabel.setText(opponentUsername + " ran away! Game over!");
                            optionsPanel.remove(newGameButton);
                            rematch = true;
                        }
                        case "Wait..." -> showWaitingPanel();
                    }
                } catch (IOException e) {
                    sleep(10);
                    e.printStackTrace();
                }
            }
            sleep(250);
        }
        resetGrid();
        waitingForAnotherPlayer();

    }

    // For show login panel with add to container and with set visibility
    private void showLoginPanel() {

        content.add(nickPanel);
        nickPanel.setVisible(true);
        nickPanel.updateUI();

    }

    private void showWaitingPanel() {

        content.remove(nickPanel);
        content.remove(optionsPanel);
        content.add(waitingPanel);
        waitingPanel.setVisible(true);
        waitingPanel.updateUI();

    }

     //Updates the GUI to show the "game over" screen with options to close the game or start a new one.
    private void showOptions( String textOptionPanels) {
        optionLabel.setText(textOptionPanels);
        content.remove(buttonPanel);
        content.remove(infoPanel);
        content.add(optionsPanel);
        optionsPanel.setVisible(true);
        optionsPanel.updateUI();

    }

     //Updates the GUI to show the grid of buttons (and hide the optionsPanel if it is on screen).
    private void showGrid() {
        content.remove(waitingPanel);
        content.remove(nickPanel);
        content.remove(optionsPanel);
        content.setLayout(new BorderLayout());
        content.add(buttonPanel, BorderLayout.CENTER);
        content.add(infoPanel, BorderLayout.SOUTH);

        buttonPanel.setVisible(true);
        infoPanel.setVisible(true);
        //Resets the UI property with a value from the current look and feel.
        buttonPanel.updateUI();
        infoPanel.updateUI();

    }

    private void updateGrid(String gridState) {
        for(int i = 0; i < 9; i++)
        {
            JButton button = buttons[i];
            char state = gridState.charAt(i);

            if(state == '-')
            {
                button.setEnabled(true);
            }

            else
            {
                String icon;
                String PLAYER_ICON = "O";
                String PLAYER2_ICON = "X";
                if(state == '1') icon = PLAYER_ICON;
                else icon = PLAYER2_ICON;

                button.setText(icon);
                button.setEnabled(false);
            }
        }
    }

    private void resetGrid(){
        for(int i = 0; i < 9; i++) {
            JButton button = buttons[i];
            button.setEnabled(true);
            button.setText("");

        }
    }

    TicTacToeGUI() {

        frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        frame.setSize(400, 400);

        nickPanel = new JPanel();
        nickPanel.setLayout(null);
        nickPanel.setBounds(10,10,400,400);

        JLabel nickLabel = new JLabel("USERNAME");
        nickLabel.setBounds(100, 50, 200, 50);
        nickLabel.setHorizontalAlignment(JTextField.CENTER);
        userTextField.setBounds(100, 150, 200, 50);
        userTextField.setHorizontalAlignment(JTextField.CENTER);

       userTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    username = userTextField.getText();
                    isLoggedIn = true;
                }
            }
        });

        JButton loginButton = new JButton("LOGIN");
        loginButton.setBounds(50, 300, 125, 50);

        resetButton.setBounds(225, 300, 125, 50);

        nickPanel.add(nickLabel);
        nickPanel.add(userTextField);
        nickPanel.add(loginButton);
        nickPanel.add(resetButton);

        loginButton.setActionCommand("login");
        //Coding Part of LOGIN button
        //Coding Part of RESET button ... using getSource it's the similar to set ActionCommand and comparing to equal
        ActionListener loginClickListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                handleLoginPanel(actionEvent);
            }
        };
        loginButton.addActionListener(loginClickListener);
        resetButton.addActionListener(loginClickListener);
        nickPanel.setVisible(false);

        // Panel showing buttons allowing user to start/decline new game.
        waitingPanel = new JPanel();
        waitingPanel.setLayout(new GridLayout(1, 1));

        JLabel infoTillWait = new JLabel(" Wait for your opponent! ");
        infoTillWait.setHorizontalAlignment(SwingConstants.CENTER);

        waitingPanel.add(infoTillWait);
        waitingPanel.setVisible(false);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3));
        buttons = new JButton[9];
        for(int i = 0; i < buttons.length; i++)
        {
            JButton button = new JButton("");
            button.setFont(new Font("Arial", Font.PLAIN, 60));
            button.setActionCommand(i + "");
            // Handles a click on an enabled grid button.
            // Transmit the user's desired grid to the server.
            ActionListener gridClickListener = new ActionListener() {
                // Transmit the user's desired grid to the server.
                public void actionPerformed(ActionEvent actionEvent) {
                    handleButtonPanel(actionEvent);
                }
            };
            button.addActionListener(gridClickListener);

            buttons[i] = button;
            buttonPanel.add(button);
        }

        buttonPanel.setVisible(false);

        infoPanel = new JPanel();
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(infoLabel);
        infoPanel.setVisible(false);


        // Panel showing buttons allowing user to start/decline new game.
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(3, 1));

        newGameButton.setActionCommand("new");
        // Otherwise, the user desires a new game.
        // Send the new game string to the server and refresh the grid of buttons.
        ActionListener optionsClickListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                handleOptionPanel(actionEvent);
            }
        };
        newGameButton.addActionListener(optionsClickListener);

        JButton closeGameButton = new JButton("close game");
        closeGameButton.setActionCommand("close");
        closeGameButton.addActionListener(optionsClickListener);
        optionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        optionsPanel.add(optionLabel);
        optionsPanel.add(newGameButton);
        optionsPanel.add(closeGameButton);
        optionsPanel.setVisible(false);

        // Prepare the content panel of the frame.
        content = frame.getContentPane();

        frame.setLocationRelativeTo(null);
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleOptionPanel(ActionEvent actionEvent){
        String buttonCommand = actionEvent.getActionCommand();

        if (buttonCommand.equals("close")) {
            try {
                printWriterOut.println("#CG\n");
                printWriterOut.close();
                bufferedReaderIn.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Error disconnecting from the TTT server.");
            }

            System.exit(0);
        }

        // Otherwise, the user desires a new game.
        // Send the new game string to the server and refresh the grid of buttons.
        if (buttonCommand.equals("new")) {

            printWriterOut.println("#NG\n");
            rematch = true;

        }
}

    private void handleButtonPanel(ActionEvent actionEvent){
    String buttonNumber = actionEvent.getActionCommand();

    System.out.println("Sending to server: " + buttonNumber);
    printWriterOut.println(buttonNumber + "\n");
}

    private void handleLoginPanel(ActionEvent actionEvent){
    //Coding Part of LOGIN button
    String loginButton = actionEvent.getActionCommand();

    if (loginButton.equals("login")) {
        username = userTextField.getText();
        isLoggedIn = true;

    }
    //Coding Part of RESET button ... using getSource it's the similar to set ActionCommand and comparing to equal
    if (actionEvent.getSource() == resetButton) {
        userTextField.setText("");
    }
}

}


