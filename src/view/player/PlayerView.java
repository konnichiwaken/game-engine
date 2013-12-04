package view.player;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import parser.JSONParser;
import controllers.GameManager;
import controllers.WorldManager;
import view.GameView;


@SuppressWarnings("serial")
public class PlayerView extends GameView {
    private GameManager myManager;

    public PlayerView () {
    }

    public PlayerView (GameManager manager) {
        myManager = manager;
    }

    @Override
    protected JMenuBar createMenuBar (JFrame frame) {
        JMenuBar menuBar = new JMenuBar();

        // first menu
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(gameMenu);
        // add menu items
        JMenuItem loadGame = new JMenuItem("Load Game");
        gameMenu.add(loadGame);
        // add action listeners
        loadGame.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                loadGame();
            }
        });

        return menuBar;
    }

    protected void loadGame () {
        JPanel loadPanel = new JPanel();
        loadPanel.setLayout(new GridLayout(0, 2));
        JLabel gameNames = new JLabel("Choose Game Name:");
        JComboBox<String> gameNamesMenu = new JComboBox<>();
        File savesDir = new File("JSONs/saves");
        for (File child : savesDir.listFiles()) {
            gameNamesMenu.addItem(child.getName().split("\\.")[0]);
        }
        loadPanel.add(gameNames);
        loadPanel.add(gameNamesMenu);

        int value = JOptionPane.showConfirmDialog(this, loadPanel,
                                                  "Choose Game", JOptionPane.OK_CANCEL_OPTION);
        if (value == JOptionPane.OK_OPTION) {
            String game = (String) gameNamesMenu.getSelectedItem();
            JSONParser p = new JSONParser();
            WorldManager newWM = p.createObject("saves/" + game,
                                                controllers.WorldManager.class);
            myManager = new GameManager(newWM, this);

            super.clearWindow();
            this.remove(myBackground);

            this.setTitle(myManager.getGameName());
        }
        revalidate();
        repaint();
        doTurn();
    }

    public void doTurn () {
        myManager.beginTurn();
        myManager.doUntilHumanTurn();
        remove(myBackground);
        StagePlayerPanel sp = new StagePlayerPanel(myManager);
        add(sp);
        revalidate();
        repaint();
    }

    public void endTurn () {
        getContentPane().removeAll();
    }

    public static void main (String[] args) {
        new PlayerView();
    }
}
