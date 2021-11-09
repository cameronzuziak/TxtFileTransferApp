package frontEnd;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import udp.GoBackNClient;
import udp.StopAndWaitClient;

public class UI implements ActionListener {

    private String[] path = new String[1];
    private String filePath;
    private JLabel label;
    private JButton button;
    private  JButton buttonGBN;
    private JButton bHome;
    private JButton buttonToSAW;
    private JButton buttonToGBN;
    private JButton setValue;
    private JFrame frame;
    private Color color1 = new Color(10, 243, 255);
    private StopAndWaitClient client = new StopAndWaitClient();
    private GoBackNClient clientGBN;
    private JLabel imageLoader;
    private ImageIcon loading;
    private GridBagConstraints gbc;
    private LineBorder lineBorder = new LineBorder(Color.WHITE);
    private BackgroundPanel homePage;
    private BackgroundPanel clientPanel;
    private JTextField rateInput;
    public int dropRate;
    private JLabel ratesOut;

    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }


    public UI(){
        clientPanel = SetButton();
        clientPanel.setVisible(true);

        frame = new JFrame();
        frame.setSize(600,600);
        frame.setLayout(new BorderLayout());
        frame.add(clientPanel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("FILE SENDER");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public BackgroundPanel SetButton(){
        BackgroundPanel setPanel = new BackgroundPanel();
        setPanel.setBorder(new EmptyBorder(10, 10, 100, 10));
        setPanel.setLayout(new GridBagLayout());

        // set initial grid-bag constraints
        gbc = new GridBagConstraints();
        gbc.ipady = 20;
        gbc.ipadx = 20;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10,0,0,0);
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel setLabel = new JLabel("<html>Enter Drop Rate.</html>");
        setLabel.setFont(new Font("Optima", Font.BOLD, 30));
        setLabel.setForeground(Color.WHITE);
        setPanel.add(setLabel, gbc);

        gbc.gridy = 1;

        rateInput = new JTextField();
        Dimension size = new Dimension(100,20);
        rateInput.setPreferredSize(size);
        rateInput.setFont(new Font("Optima", Font.BOLD, 20));
        rateInput.setHorizontalAlignment(SwingConstants.CENTER);
        rateInput.setForeground(Color.WHITE);
        rateInput.setCaretColor(Color.WHITE);
        rateInput.setBackground(color1);
        rateInput.setBorder(lineBorder);

        setPanel.add(rateInput, gbc);

        gbc.gridy = 2;

        setValue = new JButton("<html>Set Value.</html>");
        setValue.addActionListener(this::actionPerformed);
        setValue.setPreferredSize(new Dimension(100, 20));
        setValue.setFont(new Font("Optima", Font.BOLD, 20));
        setValue.setBackground(color1);
        setValue.setForeground(Color.WHITE);
        setValue.setBorder(lineBorder);
        setValue.setFocusPainted(false);
        setValue.setVisible(true);
        setPanel.add(setValue, gbc);

        return setPanel;
    }

    public BackgroundPanel HomePage(){
        // create new background panel
        BackgroundPanel homePanel = new BackgroundPanel();
        homePanel.setBorder(new EmptyBorder(10, 10, 100, 10));
        homePanel.setLayout(new GridBagLayout());

        // set initial grid-bag constraints
        gbc = new GridBagConstraints();
        gbc.ipady = 20;
        gbc.ipadx = 20;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10,0,0,0);
        gbc.anchor = GridBagConstraints.NORTH;

        // create new Jlabel, add to panel with constraints.
        JLabel welcome = new JLabel("<html>Welcome.</html>");
        welcome.setFont(new Font("Optima", Font.BOLD, 60));
        welcome.setForeground(Color.WHITE);
        homePanel.add(welcome, gbc);

        gbc.gridy = 1;
        buttonToSAW = new JButton();
        buttonToSAW.addActionListener(this::actionPerformed);
        buttonToSAW.setText("<html>Send File Through SAW.</html>");
        buttonToSAW.setFont(new Font("Optima", Font.BOLD, 30));
        buttonToSAW.setBackground(color1);
        buttonToSAW.setForeground(Color.WHITE);
        buttonToSAW.setBorder(lineBorder);
        buttonToSAW.setFocusPainted(false);
        buttonToSAW.setVisible(true);
        homePanel.add(buttonToSAW, gbc);

        gbc.gridy = 2;
        buttonToGBN = new JButton();
        buttonToGBN.addActionListener(this::actionPerformed);
        buttonToGBN.setText("<html>Send File Through GBN.</html>");
        buttonToGBN.setFont(new Font("Optima", Font.BOLD, 30));
        buttonToGBN.setBackground(color1);
        buttonToGBN.setForeground(Color.WHITE);
        buttonToGBN.setBorder(lineBorder);
        buttonToGBN.setFocusPainted(false);
        buttonToGBN.setVisible(true);
        homePanel.add(buttonToGBN, gbc);
        return homePanel;
    }

    public BackgroundPanel SAWClient(){
        gbc = new GridBagConstraints();
        gbc.ipady = 20;
        gbc.ipadx = 20;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,0,20,0);

        BackgroundPanel panel = new BackgroundPanel();
        panel.setBorder(new EmptyBorder(10,10,100,10));
        panel.setLayout(new GridBagLayout());
        panel.setBackground(color1);
        panel.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)
                    evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        path[0] = file.getAbsolutePath();
                        String fileName = file.getName();
                        filePath = path[0];
                        button.setVisible(true);
                        String labelFile = new String();
                        labelFile = "<html>" + fileName + "</html>";
                        label.setText(labelFile);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        label = new JLabel("<html>Drop File Here.</html>", JLabel.CENTER);
        label.setFont(new Font("Optima", Font.BOLD, 45));
        label.setForeground(Color.WHITE);
        label.setVisible(true);
        panel.add(label,gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(150,0,0,0);
        button = new JButton();
        button.addActionListener(this);
        button.setText("Send File.");
        button.setFont(new Font("Optima", Font.BOLD, 30));
        button.setBackground(color1);
        button.setForeground(Color.WHITE);
        button.setBorder(lineBorder);
        button.setFocusPainted(false);
        button.setVisible(false);
        panel.add(button, gbc);

        loading = new ImageIcon("src/frontEnd/Spin-1s-200px.gif");
        imageLoader = new JLabel(loading, JLabel.CENTER);
        imageLoader.setOpaque(false);
        imageLoader.setVisible(false);
        panel.add(imageLoader);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = .5;
        gbc.weighty = .5;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10,0,0,0);

        ImageIcon home = new ImageIcon("src/frontEnd/homeIcon.png");
        ImageIcon homeLoaded = new ImageIcon(getScaledImage(home.getImage(), 60, 60));
        bHome = new JButton(homeLoaded);
        Dimension preferredSize = new Dimension(60,60);
        bHome.setPreferredSize(preferredSize);
        bHome.addActionListener(this::actionPerformed);
        bHome.setFocusPainted(true);
        bHome.setBorderPainted(false);
        bHome.setBackground(color1);
        bHome.setVisible(true);
        panel.add(bHome, gbc);

        return panel;
    }

    public BackgroundPanel GBNClient(){
        gbc = new GridBagConstraints();
        gbc.ipady = 20;
        gbc.ipadx = 20;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,0,20,0);

        BackgroundPanel panel = new BackgroundPanel();
        panel.setBorder(new EmptyBorder(10,10,100,10));
        panel.setLayout(new GridBagLayout());
        panel.setBackground(color1);
        panel.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        path[0] = file.getAbsolutePath();
                        String fileName = file.getName();
                        filePath = path[0];
                        buttonGBN.setVisible(true);
                        String labelFile = "<html>" + fileName + "</html>";
                        label.setText(labelFile);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        label = new JLabel("<html>Drop File Here.</html>", JLabel.CENTER);
        label.setFont(new Font("Optima", Font.BOLD, 45));
        label.setForeground(Color.WHITE);
        label.setVisible(true);
        panel.add(label,gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(150,0,0,0);
        buttonGBN = new JButton();
        buttonGBN.addActionListener(this);
        buttonGBN.setText("Send File.");
        buttonGBN.setFont(new Font("Optima", Font.BOLD, 30));
        buttonGBN.setBackground(color1);
        buttonGBN.setForeground(Color.WHITE);
        buttonGBN.setBorder(lineBorder);
        buttonGBN.setFocusPainted(false);
        buttonGBN.setVisible(false);
        panel.add(buttonGBN, gbc);

        loading = new ImageIcon("src/frontEnd/Spin-1s-200px.gif");
        imageLoader = new JLabel(loading, JLabel.CENTER);
        imageLoader.setOpaque(false);
        imageLoader.setVisible(false);
        panel.add(imageLoader);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = .5;
        gbc.weighty = .5;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10,0,0,0);

        ImageIcon home = new ImageIcon("src/frontEnd/homeIcon.png");
        ImageIcon homeLoaded = new ImageIcon(getScaledImage(home.getImage(), 60, 60));
        bHome = new JButton(homeLoaded);
        Dimension preferredSize = new Dimension(60,60);
        bHome.setPreferredSize(preferredSize);
        bHome.addActionListener(this::actionPerformed);
        bHome.setFocusPainted(true);
        bHome.setBorderPainted(false);
        bHome.setBackground(color1);
        bHome.setVisible(true);
        panel.add(bHome, gbc);

        return panel;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == buttonToSAW){
            clientPanel = SAWClient();
            frame.add(clientPanel);
            clientPanel.setVisible(true);
            frame.remove(homePage);
            frame.pack();
        }

        if(source == buttonToGBN){
            clientPanel = GBNClient();
            frame.add(clientPanel);
            clientPanel.setVisible(true);
            frame.remove(homePage);
            frame.pack();
        }

        if (source == button){
            imageLoader.setVisible(true);
            button.setVisible(false);
            bHome.setVisible(false);
            label.setVisible(false);
            new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    client = new StopAndWaitClient();
                    try {
                        client.init(filePath, dropRate);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    imageLoader.setVisible(false);
                    bHome.setVisible(true);
                    label.setText("<html>"+ "File Uploaded." + "<BR>" + "Total Packets Sent: " + client.totalSent + "<BR>"
                            + "Total Packets Dropped: " + client.totalDrops + "<BR>"
                            + "Total Time Elapsed: " + client.totalTime + "s" + "</html>");
                    label.setVisible(true);
                }
            }.execute();
        }

        if (source == buttonGBN){
            imageLoader.setVisible(true);
            buttonGBN.setVisible(false);
            bHome.setVisible(false);
            label.setVisible(false);
            new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    clientGBN = new GoBackNClient();
                    try {
                        clientGBN.init(filePath, dropRate);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    imageLoader.setVisible(false);
                    bHome.setVisible(true);
                    label.setText("<html>"+ "File Uploaded." + "<BR>" + "Total Packets Sent: " + clientGBN.totalSent + "<BR>"
                            + "Total Packets Dropped: " + clientGBN.totalDrops + "<BR>"
                            + "Total Time Elapsed: " + clientGBN.totalTime + "s" + "</html>");
                    label.setVisible(true);
                }
            }.execute();
        }

        if(source == bHome){
            homePage = HomePage();
            frame.add(homePage);
            homePage.setVisible(true);
            frame.remove(clientPanel);
            frame.pack();
        }

        if(source == setValue){
            dropRate = Integer.parseInt(rateInput.getText());
            homePage = HomePage();
            frame.add(homePage);
            homePage.setVisible(true);
            frame.remove(clientPanel);
            frame.pack();
        }

    }
}
