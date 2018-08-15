/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aim5.gui;

import aim5.config.SimConfig;
import aim5.sim.setup.SimSetup;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author Philip
 */
public class Viewer extends JFrame implements ActionListener, KeyListener,
                                              MouseListener, ItemListener
                                               {
  // ///////////////////////////////
  // CONSTANTS
  // ///////////////////////////////
  /** The serial version ID for serialization */
  private static final long serialVersionUID = 1L;
  /**
   * Whether or not the current simulation time is shown on screen.
   */
  public static final boolean IS_SHOW_SIMULATION_TIME = true;
  /**
   * Whether or not the simulator shows the vin of the vehicles on screen.
   */
  public static final boolean IS_SHOW_VIN_BY_DEFAULT = false;
  /**
   * Whether or not the IM Shapes are shown by default.
   */
  public static final boolean IS_SHOW_IM_DEBUG_SHAPES_BY_DEFAULT = false;
  // ///////////////////////////////
  // CONSTANTS
  // ///////////////////////////////
  // simulation speed
  /**
   * The number of simulation seconds per GUI second. If it is larger than or
   * equal to <code>TURBO_SIM_SPEED</code>, the simulation will run as fast as
   * possible.
   */
  public static final double DEFAULT_SIM_SPEED = 15.0;
  /**
   * The number of screen updates per GUI second. If it is larger than or
   * equal to SimConfig.CYCLES_PER_SECOND, the screen will be updated at
   * every time step of the simulation.
   */
  public static final double DEFAULT_TARGET_FRAME_RATE = 20.0;
  /**
   * The simulation speed (simulation seconds per GUI second) at or beyond which
   * the turbo mode is on (i.e., the simulation will run as fast as possible)
   */
  public static final double TURBO_SIM_SPEED = 15.0;
  /**
   * The String to display in the title bar of the main app.
   */
  private static final String TITLEBAR_STRING = "AIM Viewer";
  /**
   * Preferred maximum width for the canvas, in pixels. {@value} pixels.
   */
  private static final int PREF_MAX_CANVAS_WIDTH = 650;
  /**
   * Preferred maximum height for the canvas, in pixels. {@value} pixels.
   */
  private static final int PREF_MAX_CANVAS_HEIGHT = 650;
  /**
   * The width of the start/pause/resume button and the step buttons. {@value}
   * pixels.
   */
  private static final int DEFAULT_BUTTON_WIDTH = 100; // px
  /**
   * The height of the status pane. {@value} pixels.
   */
  private static final int DEFAULT_STATUS_PANE_HEIGHT = 200; // px
  /**
   * The inset size of the setup panels
   */
  private static final int SIM_SETUP_PANE_GAP = 50;
  
    /////////////////////////////////
  // NESTED CLASSES
  /////////////////////////////////
  //
  // TODO: SimThread should be a SwingWorker; but it works fine now.
  // http://java.sun.com/docs/books/tutorial/uiswing/concurrency/worker.html
  //
  /**
   * The simulation thread that holds the simulation process.
   */
  public class SimThread implements Runnable {

    // ///////////////////////////////
    // PRIVATE FIELDS
    // ///////////////////////////////
    /** The simulation thread */
    private volatile Thread blinker;
    /** Whether the turbo mode is on */
    private boolean isTurboMode;
    /**
     * In the turbo mode, it is the duration of each execution period In
     * the non turbo mode, it is the time period between simulation steps
     */
    private long timeDelay;
    /** Whether the stepping mode is on */
    private boolean isSteppingMode;
    /** Whether the simulation is stopped */
    private boolean isStopped;

    // ///////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////
    /**
     * Create a simulation thread.
     *
     * @param isTurboMode  Whether the turbo mode is on
     * @param timeDelay    The time delay
     */
    public SimThread(boolean isTurboMode, long timeDelay) {
      this.blinker = null;
      this.isTurboMode = isTurboMode;
      this.timeDelay = timeDelay;
      this.isSteppingMode = false;
      this.isStopped = false;
    }

    // ///////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////

    // information retrieval

    /**
     * Whether the thread is stopped.
     *
     * @return Whether the thread is stopped.
     */
    public boolean isPaused() {
      return isStopped;
    }

    /**
     * Whether the thread is in the turbo mode.
     *
     * @return Whether the thread is in the turbo mode.
     */
    public boolean isTurboMode() {
      return isTurboMode;
    }

    // Settings

    /**
     * Set whether the turbo mode is on
     *
     * @param isTurboMode  Whether the turbo mode is on
     */
    public synchronized void setTurboMode(boolean isTurboMode) {
      this.isTurboMode = isTurboMode;
    }

    /**
     * Set whether the stepping mode is on
     *
     * @param isSteppingMode  Whether the stepping mode is on
     */
    public synchronized void setSteppingMode(boolean isSteppingMode) {
      this.isSteppingMode = isSteppingMode;
    }

    /**
     * Set the time delay.
     *
     * @param timeDelay  the time delay.
     */
    public synchronized void setTimeDelay(long timeDelay) {
      this.timeDelay = timeDelay;
    }

    // ///////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////

    // thread control

    /**
     * Start the simulation thread.
     */
    public synchronized void start() {
      assert blinker == null;
      this.blinker = new Thread(this, "AIM4 Simulator Thread");
      blinker.start();
    }

    /**
     * Terminate/Kill this simulation thread.
     */
    public synchronized void terminate() {
      assert blinker != null;
      blinker = null;
    }

    /**
     * Pause this simulation thread
     */
    public void pause() {
      // must have no synchronized keyword in order to avoid
      // funny behavior when the user clicks the "Pause" button.
      assert !isStopped;
      isStopped = true;
    }

    /**
     * Resume this simulation thread
     */
    public synchronized void resume() {
      assert isStopped;
      isStopped = false;
    }

    // ///////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////

    // the run() function of the thread

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
      Thread thisThread = Thread.currentThread();
      while (blinker == thisThread) {
        if (isStopped) {
          try {
            Thread.sleep(10L); // just sleep for a very short moment
          } catch (InterruptedException e) {
            // do nothing
          }
        } else if (isTurboMode) {
          runTurboMode();
        } else {
          runNormalMode();
        }
        // in any case, give other threads a chance to execute
        Thread.yield();
      }
      System.err.printf("The simulation has terminated.\n");
    }

    /**
     * Run the thread in the turbo mode.
     */
    private synchronized void runTurboMode() {
      double nextFastRunningStepTime = System.currentTimeMillis() + timeDelay;
      while (!isStopped) {
        runSimulationStep();
        // give GUI a chance to update the screen
        if (!updateScreenForOneStepInFastRunningMode()) {
          break;
        }
        // only one simulation step in stepping mode
        if (isSteppingMode) {
          break;
        }
        // check to see whether the time is up
        if (System.currentTimeMillis() >= nextFastRunningStepTime) {
          break;
        }
      }
      // give GUI a chance to update the screen
      updateScreenInTurboMode();
      // if in stepping mode, just stop until resume() is called
      if (isSteppingMode) {
        isStopped = true;
      }
    }

    /**
     * Run the thread in the normal mode.
     */
    private synchronized void runNormalMode() {
      long nextInvokeTime = System.currentTimeMillis() + timeDelay;
      // Advance the simulation for one step
      runSimulationStep();
      // give GUI a chance to update the screen
      updateScreenInNormalMode();
      // if in stepping mode, just stop until resume() is called
      if (isSteppingMode) {
        isStopped = true;
      } else {
        // else may sleep for a while
        long t = nextInvokeTime - System.currentTimeMillis();
        if (t > 0) {
          try {
            Thread.sleep(t);
          } catch (InterruptedException e) {
            // do nothing
          }
        } else {
          // System.err.printf("Warning: Simulation is slower than GUI\n");
        }
      }
    }

  }
  
  // ///////////////////////////////
  // PRIVATE FIELDS
  // ///////////////////////////////

  /** The initial configuration of the simulation */
  private SimSetup initSimSetup;
  /** The Simulator running in this Viewer. */
  private Simulator sim;
  /** The simulation's thread */
  private SimThread simThread;
  /** The target simulation speed */
  private double targetSimSpeed;
  /** The target frame rate */
  private double targetFrameRate;
  /** The time of the next screen update in millisecond */
  private long nextFrameTime;
  // recording
  // TODO: reset imageCounter after reset the simulator
  /** Whether or not to save the screen during simulation */
  boolean recording;
  /** Image's direction */
  String imageDir;
  /** The number of generated images */
  int imageCounter;
  // GUI Items
  /** The main pane */
  private JPanel mainPanel;
  /** The card layout for the canvas */
  private CardLayout canvasCardLayout;
  /** The canvas on which to draw the state of the simulator. */
  private Canvas canvas;
  /** The simulation setup pane */
  private SimSetupPanel simSetupPanel;
  /** The status pane on which to display statistics. */
  private StatusPanelContainer statusPanel;
  /** The Start/Pause/Resume Button */
  private JButton startButton;
  /** The Step Button */
  private JButton stepButton;
  /** The frame for showing a vehicle information */
  private VehicleInfoFrame vehicleInfoFrame;
  // Menu Items
  /** Menu item "Autonomous Vehicles Only" */
  // private JCheckBoxMenuItem autoOnlySimTypeMenuItem;
  /** Menu item "Human Drivers Only" */
  // private JCheckBoxMenuItem humanOnlySimTypeMenuItem;
  /** Menu item "Human Drivers Only" */
  // private JCheckBoxMenuItem mixedSimTypeMenuItem;
  /** Menu item "Start Simulation Process" */
  private JMenuItem startMenuItem;
  /** Menu item "Step" */
  private JMenuItem stepMenuItem;
  /** Menu item "Reset" */
  private JMenuItem resetMenuItem;
  /** Menu item "Dump Data Collection Lines' Data" */
  private JMenuItem dumpDataMenuItem;
  /** Menu item for activating recording. */
  private JMenuItem startRecordingMenuItem;
  /** Menu item for deactivating recording. */
  private JMenuItem stopRecordingMenuItem;
  /** Menu item for starting the UDP listener */
  private JMenuItem startUdpListenerMenuItem;
  /** Menu item for stopping the UDP listener */
  private JMenuItem stopUdpListenerMenuItem;
  /** Menu item for controlling whether to show the simulation time */
  private JCheckBoxMenuItem showSimulationTimeMenuItem;
  /** Menu item for controlling whether to show VIN numbers */
  private JCheckBoxMenuItem showVinMenuItem;
  /** Menu item for controlling whether to show debug shapes */
  private JCheckBoxMenuItem showIMShapesMenuItem;
  /** Menu item for clearing simulator's debug point */
  private JMenuItem clearDebugPointsMenuItem;
  
  
  // ///////////////////////////////
  // CLASS CONSTRUCTORS
  // ///////////////////////////////

  /**
   * Create a new viewer object.
   *
   * @param initSimSetup  the initial simulation setup
   */
  public Viewer(SimSetup initSimSetup) {
    this(initSimSetup, false);
  }

  /**
   * Create a new viewer object.
   *
   * @param initSimSetup  the initial simulation setup
   * @param isRunNow      whether or not the simulation is run immediately
   */
  public Viewer(final SimSetup initSimSetup, final boolean isRunNow) {
    super(TITLEBAR_STRING);
    this.initSimSetup = initSimSetup;
    this.sim = null;
    this.simThread = null;

    targetSimSpeed = DEFAULT_SIM_SPEED;
    // the frame rate cannot be not larger than the simulation cycle
    targetFrameRate =
        Math.min(DEFAULT_TARGET_FRAME_RATE, SimConfig.CYCLES_PER_SECOND);
    this.nextFrameTime = 0; // undefined yet.

    this.recording = false;
    this.imageDir = null;
    this.imageCounter = 0;

    // for debugging
    //Debug.viewer = this;

    // Lastly, schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        createAndShowGUI(initSimSetup, isRunNow);
      }

    });
  }
  
  /**
   * Create a new GUI and show it.
   *
   * @param initSimSetup  the initial simulation setup
   * @param isRunNow      whether or not the simulation is run immediately
   */
  private void createAndShowGUI(SimSetup initSimSetup, boolean isRunNow) {
    // Apple specific property.
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
        "AIM Viewer");
    // Make sure that the program quits when we close the window
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Build the GUI
    createMenuBar();
    createComponents();
    setComponentsLayout();
    pack(); // pick the layout and show it
    setVisible(true);
    initGUIsetting();

    // Make self key listener
    setFocusable(true);
    requestFocusInWindow();
    addKeyListener(this);

    if (isRunNow) {
      startButtonHandler(initSimSetup);
      canvas.requestFocusInWindow();
    }
  }
  
   /**
   * Create the menu system.
   */
  private void createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu;
    JMenuItem menuItem;

    setJMenuBar(menuBar);

    // The File menu exists only on non-Mac OS X environment
    if (!System.getProperty("os.name").equals("Mac OS X")) {
      // File
      menu = new JMenu("File");
      menuBar.add(menu);
      // File->Quit
      menuItem = new JMenuItem("Quit AIM");
      menuItem.addActionListener(this);
      menu.add(menuItem);
    }

    // Simulation
    menu = new JMenu("Simulator");
    menuBar.add(menu);

    // Simulator->Start Simulation
    startMenuItem = new JMenuItem("Start");
    startMenuItem.addActionListener(this);
    menu.add(startMenuItem);
    // Simulator->Step
    stepMenuItem = new JMenuItem("Step");
    stepMenuItem.addActionListener(this);
    menu.add(stepMenuItem);
    // Simulator->Reset
    resetMenuItem = new JMenuItem("Reset");
    resetMenuItem.addActionListener(this);
    menu.add(resetMenuItem);

    // Data
    menu = new JMenu("Data");
    menuBar.add(menu);
    // Data->Dump Data Collection Lines' Data
    dumpDataMenuItem = new JMenuItem("Dump Data Collection Lines' Data");
    dumpDataMenuItem.addActionListener(this);
    menu.add(dumpDataMenuItem);

    // Recording
    menu = new JMenu("Recording");
    menuBar.add(menu);
    // Recording->Start
    startRecordingMenuItem = new JMenuItem("Start");
    startRecordingMenuItem.addActionListener(this);
    menu.add(startRecordingMenuItem);
    // Recording->Stop
    stopRecordingMenuItem = new JMenuItem("Stop");
    stopRecordingMenuItem.addActionListener(this);
    menu.add(stopRecordingMenuItem);

    // UDP
    menu = new JMenu("UDP");
    menuBar.add(menu);
    // UDP->Start Listening
    startUdpListenerMenuItem = new JMenuItem("Start Listening");
    startUdpListenerMenuItem.addActionListener(this);
    startUdpListenerMenuItem.setEnabled(false);
    menu.add(startUdpListenerMenuItem);
    // UDP->Stop Listening
    stopUdpListenerMenuItem = new JMenuItem("Stop Listening");
    stopUdpListenerMenuItem.addActionListener(this);
    stopUdpListenerMenuItem.setEnabled(false);
    menu.add(stopUdpListenerMenuItem);

    // View
    menu = new JMenu("View");
    menuBar.add(menu);
    // View->Show simulation time
    showSimulationTimeMenuItem = new JCheckBoxMenuItem("Show Simulation Time",
        IS_SHOW_SIMULATION_TIME);
    showSimulationTimeMenuItem.addItemListener(this);
    menu.add(showSimulationTimeMenuItem);
    // View->Show VIN numbers
    showVinMenuItem = new JCheckBoxMenuItem("Show VINs",
        IS_SHOW_VIN_BY_DEFAULT);
    showVinMenuItem.addItemListener(this);
    menu.add(showVinMenuItem);
    // View->Show IM Shapes
    showIMShapesMenuItem = new JCheckBoxMenuItem("Show IM Shapes", false);
    showIMShapesMenuItem.addItemListener(this);
    menu.add(showIMShapesMenuItem);

    // Debug
    menu = new JMenu("Debug");
    menuBar.add(menu);
    // Debug->Clear Debug Points
    clearDebugPointsMenuItem = new JMenuItem("Clear Debug Points");
    clearDebugPointsMenuItem.addActionListener(this);
    menu.add(clearDebugPointsMenuItem);
  }
  
  /**
   * Create all components in the viewer
   */
  private void createComponents() {
    mainPanel = new JPanel();
    canvas = new Canvas(this);
    simSetupPanel = new SimSetupPanel(initSimSetup);
    statusPanel = new StatusPanelContainer(this);
    startButton = new JButton("Start");
    startButton.addActionListener(this);
    stepButton = new JButton("Step");
    stepButton.setEnabled(false);
    stepButton.addActionListener(this);
  }

  /**
   * Set the layout of the viewer
   */
  private void setComponentsLayout() {
    // set the card layout for the layered pane
    canvasCardLayout = new CardLayout();
    mainPanel.setLayout(canvasCardLayout);
    mainPanel.setPreferredSize(new Dimension(PREF_MAX_CANVAS_WIDTH,
        PREF_MAX_CANVAS_HEIGHT));

    // create the pane for containing the sim setup pane
    JPanel panel1 = new JPanel();
    panel1.setBackground(Canvas.GRASS_COLOR);
    panel1.setLayout(new GridBagLayout());
    GridBagConstraints c1 = new GridBagConstraints();
    c1.gridx = 0;
    c1.gridy = 0;
    c1.fill = GridBagConstraints.BOTH;
    c1.weightx = 1.0;
    c1.weighty = 1.0;
    c1.insets = new Insets(SIM_SETUP_PANE_GAP,
        SIM_SETUP_PANE_GAP,
        SIM_SETUP_PANE_GAP,
        SIM_SETUP_PANE_GAP);
    panel1.add(simSetupPanel, c1);
    // add the panel to the top layer
    mainPanel.add(panel1, "SIM_SETUP_PANEL");

    // add the canvas to the second layer
    mainPanel.add(canvas, "CANVAS");

    // set the group layout
    Container pane = getContentPane();
    GroupLayout layout = new GroupLayout(pane);
    pane.setLayout(layout);
    // Turn on automatically adding gaps between components
    layout.setAutoCreateGaps(false);
    // Turn on automatically creating gaps between components that touch
    // the edge of the container and the container.
    layout.setAutoCreateContainerGaps(false);
    // layout for the horizontal axis
    layout.setHorizontalGroup(layout.createParallelGroup(
        GroupLayout.Alignment.LEADING).addComponent(mainPanel).addGroup(layout.createSequentialGroup().addGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
        startButton,
        DEFAULT_BUTTON_WIDTH,
        GroupLayout.DEFAULT_SIZE,
        DEFAULT_BUTTON_WIDTH).addComponent(
        stepButton, DEFAULT_BUTTON_WIDTH,
        GroupLayout.DEFAULT_SIZE,
        DEFAULT_BUTTON_WIDTH)).addComponent(
        statusPanel)));
    // layout for the vertical axis
    layout.setVerticalGroup(
        layout.createSequentialGroup().addComponent(mainPanel).addGroup(
        layout.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(layout.createSequentialGroup().addComponent(
        startButton).addComponent(stepButton)).addComponent(statusPanel,
        DEFAULT_STATUS_PANE_HEIGHT,
        GroupLayout.DEFAULT_SIZE,
        DEFAULT_STATUS_PANE_HEIGHT)));
  }
   // ///////////////////////////////
  // PUBLIC METHODS
  // ///////////////////////////////

  /**
   * Get the simulator object.
   *
   * @return the simulator object; null if the simulator object has not been
   *         created.
   */
  public Simulator getSimulator() {
    return sim;
  }

  // ///////////////////////////////
  // GUI settings
  // ///////////////////////////////

  /**
   * Initialize the GUI setting.
   */
  private void initGUIsetting() {
    resetButtonMenuItem();
    startRecordingMenuItem.setEnabled(true);
    stopRecordingMenuItem.setEnabled(false);
    startUdpListenerMenuItem.setEnabled(false);
    stopUdpListenerMenuItem.setEnabled(false);
    showSimulationTimeMenuItem.setSelected(IS_SHOW_SIMULATION_TIME);
    showVinMenuItem.setSelected(IS_SHOW_VIN_BY_DEFAULT);
    showIMShapesMenuItem.setSelected(IS_SHOW_IM_DEBUG_SHAPES_BY_DEFAULT);
  }

  /**
   * Use the simulation start GUI setting.
   */
  private void setSimStartGUIsetting() {
    canvasCardLayout.show(mainPanel, "CANVAS");
    canvas.initWithGivenMap(sim.getMap());
    statusPanel.init();

    // update the buttons
    startButton.setText("Pause");
    stepButton.setEnabled(false);
    // update the menu items
    /*
    autoOnlySimTypeMenuItem.setEnabled(false);
    humanOnlySimTypeMenuItem.setEnabled(false);
    mixedSimTypeMenuItem.setEnabled(false);
    */
    startMenuItem.setText("Pause");
    stepMenuItem.setEnabled(false);
    resetMenuItem.setEnabled(true);
    dumpDataMenuItem.setEnabled(true);
    startUdpListenerMenuItem.setEnabled(true);
    clearDebugPointsMenuItem.setEnabled(true);
  }

  /**
   * Use the simulation reset GUI setting.
   */
  private void setSimResetGUIsetting() {
    canvas.cleanUp();
    statusPanel.clear();
    resetButtonMenuItem();
  }

  /**
   * Reset the button menu items.
   */
  private void resetButtonMenuItem() {
    canvasCardLayout.show(mainPanel, "SIM_SETUP_PANEL");
    // update the buttons
    startButton.setText("Start");
    stepButton.setEnabled(false);
    // update the menu items
    /*
    autoOnlySimTypeMenuItem.setSelected(true);
    humanOnlySimTypeMenuItem.setSelected(false);
    mixedSimTypeMenuItem.setSelected(false);
    */
    startMenuItem.setText("Start");
    stepMenuItem.setEnabled(false);
    resetMenuItem.setEnabled(false);
    dumpDataMenuItem.setEnabled(false);
    startUdpListenerMenuItem.setEnabled(false);
    clearDebugPointsMenuItem.setEnabled(false);
  }
}
