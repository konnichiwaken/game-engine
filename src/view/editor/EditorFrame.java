package view.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import controllers.WorldManager;


public class EditorFrame extends GameView {

    /**
     * 
     */
    private static final long serialVersionUID = -8550671173122103688L;

    private ArrayList<StagePanel> myStagePanelList = new ArrayList<StagePanel>();
    private JMenuBar myMenuBar;
    private JTabbedPane stageTabbedPane;

    public EditorFrame () {
        super("Omega_Nu Game Editor");
    }

    @Override
    protected void initializeWindow () {
        super.initializeWindow();

    }

    /**
     * adding menu bar to frame
     * 
     * @param frame
     * @return
     */
    @Override
    protected JMenuBar createMenuBar (JFrame frame) {
        myMenuBar = new JMenuBar();

        // first menu
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_F);
        myMenuBar.add(gameMenu);
        // add menu items
        JMenuItem newGame = new JMenuItem("New Game");
        newGame.setAccelerator(KeyStroke.getKeyStroke("control G"));
        gameMenu.add(newGame);
        JMenuItem loadGame = new JMenuItem("Load Game");
        gameMenu.add(loadGame);
        JMenuItem saveGame = new JMenuItem("Save Game");
        gameMenu.add(saveGame);
        JMenuItem addStage = new JMenuItem("Add Stage");
        gameMenu.add(addStage);
        // add action listeners
        addStage.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                addStagePanel();
            }
        });
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                newGame();
            }
        });
        loadGame.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                loadGame();
            }
        });

        // second menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        myMenuBar.add(editMenu);
        // add menu items
        JMenuItem undo = new JMenuItem("Undo");
        editMenu.add(undo);
        JMenuItem redo = new JMenuItem("Redo");
        editMenu.add(redo);

        return myMenuBar;
    }

    private void newGame () {
        JPanel newGamePanel = new JPanel();
        newGamePanel.setLayout(new GridLayout(1, 2));
        JLabel gameNameLabel = new JLabel("Game Name:");
        JTextField gameNameTextField = new JTextField(25);
        newGamePanel.add(gameNameLabel);
        newGamePanel.add(gameNameTextField);
        int value =
                JOptionPane.showConfirmDialog(this, newGamePanel, "New Game!!",
                                              JOptionPane.OK_CANCEL_OPTION);
        if (value == JOptionPane.OK_OPTION) {
            stageTabbedPane = new JTabbedPane();
            stageTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            this.remove(myBackground);
            this.add(stageTabbedPane, BorderLayout.CENTER);
            this.revalidate();
            this.repaint();
            String gameName = gameNameTextField.getText();
            this.setTitle(gameName);
            myWorldManager = new WorldManager(gameName);
            addStagePanel();
            JMenu stageMenu = new JMenu("Stage");
            stageMenu.setMnemonic(KeyEvent.VK_S);
            myMenuBar.add(stageMenu);
            // add menu items
            JMenuItem objective = new JMenuItem("Set Objective");
            objective.setAccelerator(KeyStroke.getKeyStroke("control O"));
            stageMenu.add(objective);

        }
    }

    /**
     * adds new stage panel to main editor frame after asking
     * for information through dialog box.
     */
    private void addStagePanel () {
        JPanel stageInfoPanel = new JPanel();
        stageInfoPanel.setLayout(new GridLayout(0, 2));
        JLabel stageNameLabel = new JLabel("Stage Name:");
        JLabel xLabel = new JLabel("Width:");
        JLabel yLabel = new JLabel("Height:");
        JTextField stageNameTextField = new JTextField(25);
        JTextField xTextField = new JTextField(6);
        JTextField yTextField = new JTextField(6);
        JLabel imageLabel = new JLabel("Default Tile");
        JComboBox<String> imageMenu = new JComboBox<String>();
        List<String> tileNames = myWorldManager.get("Tile");
        for (String s : tileNames) {
            imageMenu.addItem(s);
        }
        stageInfoPanel.add(stageNameLabel, BorderLayout.NORTH);
        stageInfoPanel.add(stageNameTextField);
        stageInfoPanel.add(xLabel);
        stageInfoPanel.add(xTextField);
        stageInfoPanel.add(yLabel);
        stageInfoPanel.add(yTextField);
        stageInfoPanel.add(imageLabel);
        stageInfoPanel.add(imageMenu);

        int value =
                JOptionPane.showConfirmDialog(this, stageInfoPanel, "Enter Stage Information",
                                              JOptionPane.OK_CANCEL_OPTION);
        if (value == JOptionPane.OK_OPTION) {
            String stageName = stageNameTextField.getText();
            int gridWidth = Integer.parseInt(xTextField.getText());
            int gridHeight = Integer.parseInt(yTextField.getText());
            String image = (String) imageMenu.getSelectedItem();
            int stageID =
                    myWorldManager.addStage(gridWidth, gridHeight, tileNames.indexOf(image),
                                            stageName);// ****
            // fix
            StagePanel sp = new StagePanel(stageName, myWorldManager);
            myStagePanelList.add(sp);
            stageTabbedPane.addTab(stageName, sp);
            stageTabbedPane.setSelectedIndex(myStagePanelList.size() - 1);
            stageTabbedPane.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged (ChangeEvent e) {
                    switchActiveStage();
                }
            });
            this.repaint();
        }

    }

    private void switchActiveStage () {
        myWorldManager.setActiveStage(stageTabbedPane.getSelectedIndex());
    }

    public static void main (String[] args) {
        new EditorFrame();
    }
}
