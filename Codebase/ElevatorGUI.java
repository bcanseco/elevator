package project;

/******************************************************************************
 *  Elevator Term Project
 *  
 *  Algorithms & Data Structures
 *  Florida Institute of Technology
 *  
 *  Luke Wiskowski
 *  Borja Canseco
 *  Liandra Bennett
 *  
 *  Fall 2015
 ******************************************************************************/

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Time;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;

public class ElevatorGUI {

    private JFrame frmElevator;
    private JTextField floorsEntry;
    private JTextField passengersEntry;
    private JTextField capacityEntry;
    private JTextField maxWeightEntry;
    private JTextField doorDeltaEntry;
    private JTextField maxSecondsEntry;
    private JTextField floorDeltaEntry;
    private JComboBox<String> startingTimeComboBox;
    private JCheckBoxMenuItem chckbxmntmVerbose;
    private JCheckBoxMenuItem chckbxmntmUnlockConsole;
    private JCheckBoxMenuItem chckbxmntmShowVisualization;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JRadioButton rdbtnBaseline;
    private JRadioButton rdbtnAdvanced;
    private JMenuItem mntmSaveLog;
    public static Thread visual;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ElevatorGUI window = new ElevatorGUI();
                    window.frmElevator.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public ElevatorGUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmElevator = new JFrame();
        frmElevator.setResizable(false);
        frmElevator.setTitle("Elevator Project - CSE 2010");
        frmElevator.setBounds(100, 100, 560, 435);
        frmElevator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmElevator.getContentPane().setLayout(null);
        
        JButton btnRunSimulation = new JButton("Run Simulation");
        btnRunSimulation.setBounds(14, 373, 127, 23);
        frmElevator.getContentPane().add(btnRunSimulation);
        
        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(151, 373, 80, 23);
        frmElevator.getContentPane().add(btnReset);
        
        JLabel lblElevatorSettings = new JLabel("Elevator settings:");
        lblElevatorSettings.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblElevatorSettings.setBounds(14, 28, 127, 23);
        frmElevator.getContentPane().add(lblElevatorSettings);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 554, 21);
        frmElevator.getContentPane().add(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmRunSimulation = new JMenuItem("Run Simulation");
        mnFile.add(mntmRunSimulation);
        
        JMenuItem mntmReset = new JMenuItem("Reset");
        mnFile.add(mntmReset);
        
        mnFile.addSeparator();
        
        mntmSaveLog = new JMenuItem("Save Log");
        mntmSaveLog.setEnabled(false);
        mntmSaveLog.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mntmSaveLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String fileName = JOptionPane.showInputDialog(mntmSaveLog, "Enter a name for the log file.", "Save as...", JOptionPane.QUESTION_MESSAGE);
                if (fileName != null) saveAs(fileName);
            }
        });
        mnFile.add(mntmSaveLog);
        
        mnFile.addSeparator();
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        mnFile.add(mntmExit);
        
        JMenu mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);
        
        chckbxmntmShowVisualization = new JCheckBoxMenuItem("Show visual demo");
        chckbxmntmShowVisualization.setSelected(false);
        mnEdit.add(chckbxmntmShowVisualization);
        
        chckbxmntmUnlockConsole = new JCheckBoxMenuItem("Unlock console");
        chckbxmntmUnlockConsole.setSelected(false);
        chckbxmntmUnlockConsole.setEnabled(false);
        mnEdit.add(chckbxmntmUnlockConsole);
        
        JMenu mnInfo = new JMenu("Help");
        menuBar.add(mnInfo);
        
        chckbxmntmVerbose = new JCheckBoxMenuItem("Verbose");
        chckbxmntmVerbose.setSelected(true);
        mnInfo.add(chckbxmntmVerbose);
        
        mnInfo.addSeparator();
        
        JMenuItem mntmAbout = new JMenuItem("About");
        mnInfo.add(mntmAbout);
        
        floorsEntry = new JTextField();
        floorsEntry.setToolTipText("Enter the number of floors in the elevator shaft. (int > 1)");
        floorsEntry.setHorizontalAlignment(SwingConstants.RIGHT);
        floorsEntry.setText("9");
        floorsEntry.setBounds(14, 56, 33, 20);
        frmElevator.getContentPane().add(floorsEntry);
        floorsEntry.setColumns(10);
        
        JLabel lblFloors = new JLabel("Floors");
        lblFloors.setToolTipText("");
        lblFloors.setBounds(51, 59, 58, 14);
        frmElevator.getContentPane().add(lblFloors);
        
        JLabel lblPassengers = new JLabel("Passengers");
        lblPassengers.setToolTipText("");
        lblPassengers.setBounds(51, 204, 80, 14);
        frmElevator.getContentPane().add(lblPassengers);
        
        passengersEntry = new JTextField();
        passengersEntry.setToolTipText("Enter the number of passengers in the building. (int > 0)");
        passengersEntry.setHorizontalAlignment(SwingConstants.RIGHT);
        passengersEntry.setText("15");
        passengersEntry.setColumns(10);
        passengersEntry.setBounds(14, 201, 33, 20);
        frmElevator.getContentPane().add(passengersEntry);
        
        JLabel lblCapacity = new JLabel("Capacity");
        lblCapacity.setToolTipText("");
        lblCapacity.setBounds(51, 87, 80, 14);
        frmElevator.getContentPane().add(lblCapacity);
        
        capacityEntry = new JTextField();
        capacityEntry.setToolTipText("Enter the weight limit of the elevator. (int >= maxWeight)");
        capacityEntry.setHorizontalAlignment(SwingConstants.RIGHT);
        capacityEntry.setText("1000");
        capacityEntry.setColumns(10);
        capacityEntry.setBounds(14, 84, 33, 20);
        frmElevator.getContentPane().add(capacityEntry);
        
        maxWeightEntry = new JTextField();
        maxWeightEntry.setToolTipText("Enter the upper bound for the RNG that calculates passenger weights. (int > 0)");
        maxWeightEntry.setText("250");
        maxWeightEntry.setHorizontalAlignment(SwingConstants.RIGHT);
        maxWeightEntry.setColumns(10);
        maxWeightEntry.setBounds(14, 229, 33, 20);
        frmElevator.getContentPane().add(maxWeightEntry);
        
        JLabel lblMaxWeight = new JLabel("Max weight");
        lblMaxWeight.setToolTipText("");
        lblMaxWeight.setBounds(51, 232, 80, 14);
        frmElevator.getContentPane().add(lblMaxWeight);
        
        JLabel lblPassengerSettings = new JLabel("Passenger settings:");
        lblPassengerSettings.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPassengerSettings.setBounds(14, 173, 181, 23);
        frmElevator.getContentPane().add(lblPassengerSettings);
        
        JLabel lblDoorDelta = new JLabel("Door delta");
        lblDoorDelta.setToolTipText("");
        lblDoorDelta.setBounds(51, 115, 80, 14);
        frmElevator.getContentPane().add(lblDoorDelta);
        
        doorDeltaEntry = new JTextField();
        doorDeltaEntry.setToolTipText("Enter the time it takes to open and close doors. (int >= 0)");
        doorDeltaEntry.setText("15");
        doorDeltaEntry.setHorizontalAlignment(SwingConstants.RIGHT);
        doorDeltaEntry.setColumns(10);
        doorDeltaEntry.setBounds(14, 112, 33, 20);
        frmElevator.getContentPane().add(doorDeltaEntry);
        
        JLabel lblmaxSeconds = new JLabel("Max seconds");
        lblmaxSeconds.setToolTipText("");
        lblmaxSeconds.setBounds(51, 260, 80, 14);
        frmElevator.getContentPane().add(lblmaxSeconds);
        
        maxSecondsEntry = new JTextField();
        maxSecondsEntry.setToolTipText("Enter the upper bound for the RNG that calculates the time between button presses. (int > 0)");
        maxSecondsEntry.setText("200");
        maxSecondsEntry.setHorizontalAlignment(SwingConstants.RIGHT);
        maxSecondsEntry.setColumns(10);
        maxSecondsEntry.setBounds(14, 257, 33, 20);
        frmElevator.getContentPane().add(maxSecondsEntry);
        
        JLabel lblfloordelta = new JLabel("Floor delta");
        lblfloordelta.setToolTipText("");
        lblfloordelta.setBounds(51, 143, 119, 14);
        frmElevator.getContentPane().add(lblfloordelta);
        
        floorDeltaEntry = new JTextField();
        floorDeltaEntry.setToolTipText("Enter the time it takes to move to another floor. (int > 0)");
        floorDeltaEntry.setText("10");
        floorDeltaEntry.setHorizontalAlignment(SwingConstants.RIGHT);
        floorDeltaEntry.setColumns(10);
        floorDeltaEntry.setBounds(14, 140, 33, 20);
        frmElevator.getContentPane().add(floorDeltaEntry);
        
        startingTimeComboBox = new JComboBox<String>();
        startingTimeComboBox.setBounds(149, 289, 58, 20);
        frmElevator.getContentPane().add(startingTimeComboBox);
        startingTimeComboBox.addItem("8:00");
        startingTimeComboBox.addItem("9:00");
        startingTimeComboBox.addItem("10:00");
        startingTimeComboBox.addItem("11:00");
        startingTimeComboBox.addItem("12:00");
        startingTimeComboBox.addItem("1:00");
        startingTimeComboBox.addItem("2:00");
        startingTimeComboBox.addItem("3:00");
        startingTimeComboBox.addItem("4:00");
        startingTimeComboBox.addItem("5:00");
        startingTimeComboBox.addItem("6:00");
        
        JLabel lblStartTheSimulation = new JLabel("Start the simulation at:");
        lblStartTheSimulation.setHorizontalAlignment(SwingConstants.LEFT);
        lblStartTheSimulation.setBounds(14, 292, 139, 14);
        frmElevator.getContentPane().add(lblStartTheSimulation);
        
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(1,5,1,5));
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setBackground(new Color(1,1,1, (float) 0.01));
        scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(241, 30, 303, 368);
        frmElevator.getContentPane().add(scrollPane);
        System.setOut(new PrintStream(new EchoConsole(System.out, textArea)));
        
        ButtonGroup elevatorTypes = new ButtonGroup();
        
        rdbtnAdvanced = new JRadioButton("Advanced elevator");
        rdbtnAdvanced.setBounds(10, 341, 160, 23);
        frmElevator.getContentPane().add(rdbtnAdvanced);
        
        elevatorTypes.add(rdbtnAdvanced);
        
        rdbtnBaseline = new JRadioButton("Baseline elevator");
        rdbtnBaseline.setBounds(10, 316, 131, 23);
        frmElevator.getContentPane().add(rdbtnBaseline);
        
        elevatorTypes.add(rdbtnBaseline);
        rdbtnBaseline.setSelected(true);
        
        final JMenu aboutPage = new JMenu();
        mntmAbout.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JOptionPane.showMessageDialog(aboutPage,
                        "This is the Elevator control project for"
                        + "\ngroup #4 in Algorithms & Data Structures.    "
                        + "\n\nGroup members:"
                        + "\n- Luke Wiskowski"
                        + "\n- Liandra Bennett"
                        + "\n- Borja Canseco"
                        + "\n\nFall 2015  ©  Florida Institute of Technology",
                        "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        chckbxmntmUnlockConsole.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textArea.setEditable(!textArea.isEditable());
            }
        });
        
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButton();
            }
        });
        
        mntmReset.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButton();
            }
        });
        
        btnRunSimulation.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButton();
            }
        });
        
        mntmRunSimulation.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButton();
            }
        });
    }
    
    int floors;
    int passengers;
    int capacity;
    int maxWeight;
    int doorDelta;
    int maxSeconds;
    int floorDelta;
    boolean verbose;
    boolean clearImage = true;
    boolean clearText = true;
    boolean space = false;
    boolean visualRan = false;
    Time start;
    public static Queue<PassengerRequest> elevatorQueue = new LinkedList<PassengerRequest>();
    
    public void resetButton() {
        floorsEntry.setText("9");
        passengersEntry.setText("15");
        capacityEntry.setText("1000");
        maxWeightEntry.setText("250");
        doorDeltaEntry.setText("15");
        maxSecondsEntry.setText("200");
        floorDeltaEntry.setText("10");
        startingTimeComboBox.setSelectedItem("8:00");
        textArea.setText(null);
        space = false;
        rdbtnBaseline.setSelected(true);
        rdbtnAdvanced.setSelected(false);
        clearText = true;
        mntmSaveLog.setEnabled(false);
    }
    
    @SuppressWarnings("deprecation")
    public void runButton() {
        floors = Integer.parseInt(floorsEntry.getText());
        passengers = Integer.parseInt(passengersEntry.getText());
        capacity = Integer.parseInt(capacityEntry.getText());
        maxWeight = Integer.parseInt(maxWeightEntry.getText());
        doorDelta = Integer.parseInt(doorDeltaEntry.getText());
        maxSeconds = Integer.parseInt(maxSecondsEntry.getText());
        floorDelta = Integer.parseInt(floorDeltaEntry.getText());
        verbose = chckbxmntmVerbose.isSelected();
        
        String time = (String) startingTimeComboBox.getSelectedItem();
        int colon = time.indexOf(':');
        int hour = Integer.parseInt(time.substring(0, colon));
        start = new Time(hour, 0, 0);
        
        if (clearImage) { // used to clear landing image (only once)
            textArea.setBackground(Color.WHITE);
            clearImage = false;
        }
        
        if (clearText) { // used to clear text if user hit reset button
            textArea.setText(null);
            clearText = false;
        } else {
            System.out.println();
        }
        
        boolean badInput = false;
        if (floors < 2) {
            System.out.println("ERROR: Floors must be greater than one.");
            badInput = true;
        }
        if (capacity < maxWeight) {
            System.out.println("ERROR: Capacity must be greater than or equal to Max weight.");
            badInput = true;
        }
        if (doorDelta < 0) {
            System.out.println("ERROR: Door delta must be greater than or equal to zero.");
            badInput = true;
        }
        if (floorDelta < 1) {
            System.out.println("ERROR: Floor delta must be greater than zero.");
            badInput = true;
        }
        if (passengers < 1) {
            System.out.println("ERROR: Passengers must be greater than zero.");
            badInput = true;
        }
        if (maxWeight < 1) {
            System.out.println("ERROR: Max weight must be greater than zero.");
            badInput = true;
        }
        if (maxSeconds < 1) {
            System.out.println("ERROR: Max seconds must be greater than zero.");
            badInput = true;
        }
        if (badInput) {
            return;
        }
        
        generatePassengers();
        Elevator elevator;
        Queue<Integer> visualQueue;
        if (rdbtnBaseline.isSelected()) { // "Baseline" button is toggled
            // Baseline Elevator
            elevator = new BaselineElevator(
                    capacity, floorDelta, floors, doorDelta, verbose, start);
            visualQueue = ((BaselineElevator) elevator).visualQueue;

        } else { // "Advanced" button is toggled
            // Advanced Elevator
            elevator = new AdvancedElevator(
                    capacity, floorDelta, floors, doorDelta, verbose, start);
            visualQueue = ((AdvancedElevator) elevator).visualQueue;
        }
        elevator.initialize(elevatorQueue);
        elevator.operate();
        System.out.println(elevator);
        
        mntmSaveLog.setEnabled(true); // enable logging
        chckbxmntmUnlockConsole.setEnabled(true);
        
        if (chckbxmntmShowVisualization.isSelected()) {
            if (visualRan && visual.isAlive()) {
                visual.interrupt();
            }

            visual = new Thread(new Runnable() {
                public void run()
                {
                    try {
                        visualRan = true;
                        Visual.simulation(floors, elevatorQueue, visualQueue);
                    } catch (InterruptedException e) {}
                }});
            
            visual.start();
        }
    }
    
    public void generatePassengers() {
        Random random = new Random(0);
        long currentTime = start.getTime();
        
        elevatorQueue.clear();
        for (int i = 0; i < passengers; i++) {
            int floor_from = random.nextInt(floors) + 1;
            int floor_to   = random.nextInt(floors) + 1; 
            
            if (floor_from == floor_to) {
                if (floor_from == 1) {
                    floor_to++;
                } else {
                    floor_to--;
                }
            }
            
            int weight = random.nextInt(maxWeight);
            int seconds = random.nextInt(maxSeconds);
            
            currentTime += seconds * 1000;

            PassengerRequest request = new PassengerRequest
                    (new Time(currentTime), floor_from, floor_to, weight);
            
            elevatorQueue.add(request);
        }
    }
    
    public void saveAs(String fileName) {
        String log = textArea.getText();
        BufferedWriter output;
        try {
            output = new BufferedWriter(new FileWriter(fileName + ".txt"));
            output.write(log);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Saved log to " + fileName + ".txt");
    }
}

// from http://stackoverflow.com/a/4422930
class EchoConsole extends FilterOutputStream {
    private final JTextArea text;

    public EchoConsole(OutputStream out, JTextArea text) {
        super(out); // creates an output stream connected to our JTextArea ('textArea')
        this.text = text;
    }

    public void write(int b) throws IOException {
        super.write(b);
        text.append(Character.toString((char) b));
    }
}