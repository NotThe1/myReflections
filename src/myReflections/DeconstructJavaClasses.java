package myReflections;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import appLogger.AppLogger;


public class DeconstructJavaClasses {
	
	AppLogger log =  AppLogger.getInstance();

	private JFrame frame;
	private JTextPane txtLog;
	private String title = "DeconstructJavaClasses     0.1";
	private JSplitPane splitPane;
	private JPanel panelStatus;
	private JPanel toolBar;
	private Component verticalStrut;
	private Component verticalStrut_1;
	private JPanel panelLeft;
	private JLabel lblCurrentProject;
	ImageIcon getTargetsIcon = new ImageIcon(
			Toolkit.getDefaultToolkit().getImage(DeconstructJavaClasses.class.getResource("edit-find-10.png")));
	ImageIcon getClassFilesIcon = new ImageIcon(
			Toolkit.getDefaultToolkit().getImage(DeconstructJavaClasses.class.getResource("enumlist.png")));

	private DefaultListModel<String> targetClasses;
	private JButton btnGetTargetProject;
	private JButton btnGetClassFiles;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DeconstructJavaClasses window = new DeconstructJavaClasses();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}//run
		});
	}//main
	
	private void doGetNewProject() {
		JFileChooser fc = new JFileChooser(lblCurrentProject.getText());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
			return;
		} // if
		lblCurrentProject.setText(fc.getSelectedFile().toString());
//		targetClasses = new DefaultListModel<String>();
//		targetClasses.clear();
//		File targetDir = new File(lblCurrentProject.getText());
//		loadTargetClasses(targetDir, targetClasses);
	}//doGetNewProject
	
	private void doGetClassFiles() {
		targetClasses = new DefaultListModel<String>();
		targetClasses.clear();
		File targetDir = new File(lblCurrentProject.getText());
		loadTargetClasses(targetDir, targetClasses);

	}//doGetClassFiles
	

	
	private void loadTargetClasses(File folder, DefaultListModel targetClasses) {
		/* Find all the sub directories in this directory */
		File[] directories = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File fileContent) {
				return fileContent.isDirectory();
			}// accept
		});

		if (directories != null && directories.length > 0) {
			for (File dir : directories) {
				loadTargetClasses(dir,  targetClasses);
			} // if
		} // for
		
		/* Find all the java files in this directory */
		File[] files = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File fileContent) {
				return fileContent.getName().toLowerCase().endsWith(".java");
			}// accept
		});
		
		if (files != null && files.length > 0) {
			for (File file : files) {
				log.infof("Dir: %-30s File:  %s%n", folder.getName(),file.getName());
			} // if
		} // for
	


	}// loadTargetClasses

	/**
	 * Create the application.
	 */
	
	private Preferences getPreferences() {
		return Preferences.userNodeForPackage(DeconstructJavaClasses.class).node(this.getClass().getSimpleName());
	}//getPreferences
	
	private void appClose() {
		Preferences myPrefs =  getPreferences();
		Dimension dim = frame.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frame.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.putInt("Divider", splitPane.getDividerLocation());
		myPrefs.put("CurrentProject", lblCurrentProject.getText());
		myPrefs = null;
	}//appClose

	private void appInit() {
		frame.setBounds(100, 100, 736, 488);

		Preferences myPrefs =  getPreferences();
		frame.setSize(myPrefs.getInt("Width", 761), myPrefs.getInt("Height", 693));
		frame.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		splitPane.setDividerLocation(myPrefs.getInt("Divider", 250));
		lblCurrentProject.setText(myPrefs.get("CurrentProject", "C:\\Users\\admin\\git"));
		myPrefs = null;
		
		log.setTextPane(txtLog);
		log.setDoc(txtLog.getStyledDocument());
		log.addTimeStamp("Starting....");

	}// appInit
	public DeconstructJavaClasses() {
		initialize();
		appInit();
	}//Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
//		ImageIcon getTargetsIcon = new ImageIcon(
//				Toolkit.getDefaultToolkit().getImage(DeconstructJavaClasses.class.getResource("/enumList.png")));

		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}//windowClosing
		});
		frame.setBounds(100, 100, 736, 488);
		frame.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		toolBar = new JPanel();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.fill = GridBagConstraints.BOTH;
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		frame.getContentPane().add(toolBar, gbc_toolBar);
		GridBagLayout gbl_toolBar = new GridBagLayout();
		gbl_toolBar.columnWidths = new int[]{0, 0, 0, 0};
		gbl_toolBar.rowHeights = new int[]{0, 0};
		gbl_toolBar.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_toolBar.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		toolBar.setLayout(gbl_toolBar);
		
		verticalStrut = Box.createVerticalStrut(20);
		verticalStrut.setPreferredSize(new Dimension(20, 20));
		verticalStrut.setMinimumSize(new Dimension(20, 20));
		verticalStrut.setMaximumSize(new Dimension(20, 20));
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 0, 5);
		gbc_verticalStrut.fill = GridBagConstraints.VERTICAL;
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 0;
		toolBar.add(verticalStrut, gbc_verticalStrut);
		
		btnGetTargetProject = new JButton(getTargetsIcon);
		btnGetTargetProject.setToolTipText("Get Target Project");
		GridBagConstraints gbc_btnGetTargetProject = new GridBagConstraints();
		gbc_btnGetTargetProject.insets = new Insets(0, 0, 0, 5);
		gbc_btnGetTargetProject.gridx = 1;
		gbc_btnGetTargetProject.gridy = 0;
		toolBar.add(btnGetTargetProject, gbc_btnGetTargetProject);
		
		btnGetClassFiles = new JButton(getClassFilesIcon);
		btnGetClassFiles.setToolTipText("Get Class Files");
		GridBagConstraints gbc_btnGetClassFiles = new GridBagConstraints();
		gbc_btnGetClassFiles.gridx = 2;
		gbc_btnGetClassFiles.gridy = 0;
		toolBar.add(btnGetClassFiles, gbc_btnGetClassFiles);

		
		splitPane = new JSplitPane();
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;
		frame.getContentPane().add(splitPane, gbc_splitPane);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
		JLabel lblNewLabel = new JLabel("Application Log");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(Color.GREEN);
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		scrollPane.setColumnHeaderView(lblNewLabel);
		
		txtLog = new JTextPane();
		txtLog.setFont(new Font("Courier New", Font.PLAIN, 14));
		scrollPane.setViewportView(txtLog);
		
		panelLeft = new JPanel();
		panelLeft.setPreferredSize(new Dimension(100, 10));
		splitPane.setLeftComponent(panelLeft);
		GridBagLayout gbl_panelLeft = new GridBagLayout();
		gbl_panelLeft.columnWidths = new int[]{0};
		gbl_panelLeft.rowHeights = new int[]{0};
		gbl_panelLeft.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_panelLeft.rowWeights = new double[]{Double.MIN_VALUE};
		panelLeft.setLayout(gbl_panelLeft);
		
		panelStatus = new JPanel();
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.fill = GridBagConstraints.BOTH;
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 2;
		frame.getContentPane().add(panelStatus, gbc_panelStatus);
		GridBagLayout gbl_panelStatus = new GridBagLayout();
		gbl_panelStatus.columnWidths = new int[]{0, 0, 0};
		gbl_panelStatus.rowHeights = new int[]{0, 0};
		gbl_panelStatus.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panelStatus.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelStatus.setLayout(gbl_panelStatus);
		
		verticalStrut_1 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_1 = new GridBagConstraints();
		gbc_verticalStrut_1.insets = new Insets(0, 0, 0, 5);
		gbc_verticalStrut_1.gridx = 0;
		gbc_verticalStrut_1.gridy = 0;
		panelStatus.add(verticalStrut_1, gbc_verticalStrut_1);
		
		lblCurrentProject = new JLabel("Target Directory");
		lblCurrentProject.setForeground(Color.BLUE);
		lblCurrentProject.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		GridBagConstraints gbc_lblCurrentProject = new GridBagConstraints();
		gbc_lblCurrentProject.gridx = 1;
		gbc_lblCurrentProject.gridy = 0;
		panelStatus.add(lblCurrentProject, gbc_lblCurrentProject);
	}//initialize

}//class DeconstructJavaClasses
