package msp.editor;

import framework.*;
import msp.game.MSPGame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.awt.event.WindowEvent.WINDOW_CLOSING;

public class Editor extends GWindow {

    EditorMap map;
    MSPGame game;
    EditorToolBox toolBox;

    private JMenuBar menuBar;
    JMenu menuItem;
    JMenuItem saveFileMenuItem;
    JMenuItem LoadFileMenuItem;
    JMenuItem exitFileMunJMenuItem;

    JOptionPane saveDialog;

    private Container c;

    public Editor() {
        setup();
        setupComponents();


       setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
       addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent e) {
               saveDialog = new JOptionPane();
               saveDialog.setSize(200,100);
               saveDialog.setLayout(new GridBagLayout());
               int s = saveDialog.showConfirmDialog(Editor.this,"Do you want to save ?","Exit editor",JOptionPane.YES_NO_CANCEL_OPTION);
               add(saveDialog);
                switch(s){
                    case 0 : saveMap();break;
                    case 1 :
                        Editor.this.setVisible(false);
                        Editor.this.dispose();
                        break;
                }
           }
       });
        enableFullscreenMode();
    }

    private void setup() {
        setTitle("MSP Strategy - Map Editor");
        setSize(800, 600);
        setLocationRelativeTo(null);
        c = getContentPane();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void setupComponents() {

        c.setLayout(new BorderLayout());

        //GGame and map
        game = new MSPGame();
        game.disableDayNight();
        game.map = map = new EditorMap(game, this);

        game.load(GResource.instance.getMap("default"));

        c.add(map, BorderLayout.CENTER);

        //Toolbox
        toolBox = new EditorToolBox(this);
        c.add(new JScrollPane(toolBox), BorderLayout.EAST);

        //menu bar
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(menuItem = new JMenu("File"));
        menuItem.add(saveFileMenuItem = new JMenuItem("Save Map"));
        saveFileMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMap();
            }
        });
        menuItem.add(LoadFileMenuItem = new JMenuItem("Load Map"));
        LoadFileMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadMap();
            }
        });

        menuItem.add(exitFileMunJMenuItem=new JMenuItem("Exit"));
        exitFileMunJMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WindowEvent wev = new WindowEvent(Editor.this, WINDOW_CLOSING);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);

            }
        });

    }

    public static void main(String[] args) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        new Editor().open();
    }

    public void open() {
        setVisible(true);
    }


    public void onToolItemSelected(String name) {
        if (name != null)
            map.setEntity(GEntity.inflate(name, game));
        else
            map.newEntity = null;
    }

    public void saveMap() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("MSPGameWindow map files", "map.json"));
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                GUtils.writeAllFile(new File(chooser.getSelectedFile().getPath()), map.toJson());
            } catch (IOException e) {
                GDB.e("Unable to save map");
            }
        }
    }

    public void LoadMap() {
        File mapFile = new File("assets");
        mapFile.mkdirs();
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("MSP map files", "map.json"));
        chooser.setCurrentDirectory(mapFile);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String s = GUtils.readAllFile(new File(chooser.getSelectedFile().getPath()));
            GProperty m = new GProperty(s);
            GProperty mD = new GProperty((HashMap) ((ArrayList) m.get("root")).get(0));
            game.load(mD);
        }

    }
}







