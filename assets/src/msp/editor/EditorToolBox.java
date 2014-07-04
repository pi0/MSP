package msp.editor;

import framework.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.function.Consumer;

public class EditorToolBox extends JPanel {

    private final static int width = 100;

    GridBagConstraints gridC = new GridBagConstraints();

    GResource resources = GResource.getInstance();
    ArrayList<EditorToolBoxTab> tabs = new ArrayList();

    Editor editor;

    EditorToolBoxTab toolsTab, bgTab;

    public EditorToolBox(Editor editor) {

        setLayout(new GridBagLayout());
        gridC.gridx = gridC.gridy = 0;
        gridC.weightx = 1;
        gridC.fill = GridBagConstraints.HORIZONTAL;
        gridC.anchor = GridBagConstraints.SOUTH;
        this.editor = editor;

        setComponents();
        setPreferredSize(new Dimension(width, 0));
    }

    private void setComponents() {

        addTools();
        addEntities();
        addBackgrounds();

        //Fit all to top
        gridC.weighty = 1;
        add(Box.createVerticalGlue(), gridC);
    }

    void addBackgrounds() {
        bgTab = new EditorToolBoxTab("Backgrounds");
        ArrayList backgrounds = resources.getProperty("properties").getArray("backgrounds");
        for (final Object bg : backgrounds) {
            JButton b = new JButton(new ImageIcon(GUtils.resizeImage(resources.getImage(bg + ".summer").getImage(),30,30)));
            //JButton b = new JButton(bg);
            b.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.map.currentBG = new GMapBlock(bg + ",0", 0, 0);
                    editor.map.newEntity = null;
                }
            });
            bgTab.addButton(b);
        }
        addTab(bgTab);
    }

    void addTools() {
        toolsTab = new EditorToolBoxTab("Tools");
        JButton arrow = new JButton("Arrow");
        arrow.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editor.onToolItemSelected(null);
            }
        });
        arrow.setPreferredSize(new Dimension(1,40));
        toolsTab.addButton(arrow);
        toolsTab.toggle(true);
        addTab(toolsTab);
    }

        void addEntities() {

        GEntity.entityDefaultProperties.keySet().forEach(new Consumer<String>() {
            @Override
            public void accept(String entityName) {
                GProperty entity = GEntity.entityDefaultProperties.get(entityName);

                if (!entity.getBool("editor"))
                    return;//don't accept :)

                String type = entity.getStr("type");

                //Search for tab
                EditorToolBoxTab curr = null;
                for (EditorToolBoxTab tab : tabs)
                    if (tab.getName().equals(type)) {
                        curr = tab;
                        break;
                    }
                //If tab not found ...
                if (curr == null)
                    addTab(curr = new EditorToolBoxTab(type));

                //Add entity to tab
                curr.addItem(entityName, entity.getStr("editorIcon"));
            }
        });

    }

    void addTab(EditorToolBoxTab tab) {
        tabs.add(tab);
        add(tab.container, gridC);
        gridC.gridy++;
    }

    class EditorToolBoxTab {

        //Anim constants
        final static int step = +1;
        final static int sleep = 1;

        JButton tabButton;
        JPanel pnl = new JPanel(new GridBagLayout());
        GridBagConstraints pnlC = new GridBagConstraints();

        JPanel container = new JPanel(new BorderLayout());

        EditorToolBoxTab(String title) {

            pnl.setBorder(new BevelBorder(BevelBorder.LOWERED));

            pnlC.fill = GridBagConstraints.HORIZONTAL;
            pnlC.weighty = pnlC.weightx = 1;
            pnlC.gridx = pnlC.gridy = 0;

            tabButton = new JButton(title);


            container.add(tabButton, BorderLayout.NORTH);
            container.add(pnl, BorderLayout.CENTER);

            tabButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toggle();
                }
            });

            toggle(true);
        }

        void toggle() {
            toggle(!pnl.isVisible());
        }

        synchronized void toggle(final boolean visible) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    int step = EditorToolBoxTab.step;
                    if (!visible) {
                        //Hide
                        step *= -1;
                        while (pnl.getHeight() > 0) {
                            pnl.setSize(pnl.getWidth(), pnl.getHeight() + step);
                            repaint();
                            GUtils.sleep(sleep);
                        }
                        pnl.setVisible(false);
                    } else {
                        //Show
                        pnl.setVisible(true);
                        while (pnl.getHeight() < pnl.getPreferredSize().getHeight()) {
                            pnl.setSize(pnl.getWidth(), pnl.getHeight() + step);
                            repaint();
                            GUtils.sleep(sleep);
                        }
                    }
                }
            }).start();
        }

        void addItem(String name, String icon) {
            final JButton btn = new JButton
                    (resources.getImage(icon).getImageIcon());
            btn.setToolTipText(name);

            btn.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.onToolItemSelected(btn.getToolTipText());
                }
            });

            addButton(btn);
        }

        void addButton(JButton btn) {
            btn.setFocusPainted(false);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            pnl.add(btn, pnlC);
            pnlC.gridy++;

        }


        String getName() {
            return tabButton.getText();
        }

    }

}






