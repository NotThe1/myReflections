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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import appLogger.AppLogger;

public class ExploreReflection {

	AppLogger log = AppLogger.getInstance();

	private JFrame frame;
	private JTextPane txtLog;
	private String title = "ExploreReflection     0.1";
	private JSplitPane splitPane;
	private JPanel panelStatus;
	private JPanel toolBar;
	private Component verticalStrut;
	private Component verticalStrut_1;
	private JPanel panelLeft;
	private Component verticalStrut_2;
	private JButton btnClassObjects;
	private JButton btnModifiersAndTypes;
	private Component verticalStrut_3;
	private JTextField txtClass1;
	private Component verticalStrut_4;
	private JButton btnDiscoverClassMembers;
	private JTextField txtClass2;

	private Component verticalStrut_5;
	private JButton btnGetNewProject;
	private JLabel lblCurrentProject;

	private DefaultListModel<String> targetClasses;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExploreReflection window = new ExploreReflection();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}// run
		});
	}// main

	private static void printAncestor(Class<?> c, List<Class> l) {
		Class<?> ancestor = c.getSuperclass();
		if (ancestor != null) {
			l.add(ancestor);
			printAncestor(ancestor, l);
		} // if
	}// printAncestor

	private void printMembers(Member[] mbrs, String s) {
		log.post(Color.GREEN, "%s:%n", s);
		for (Member mbr : mbrs) {
			if (mbr instanceof Field)
				log.infof("  %s%n", ((Field) mbr).toGenericString());
			else if (mbr instanceof Constructor)
				log.infof("  %s%n", ((Constructor) mbr).toGenericString());
			else if (mbr instanceof Method)
				log.infof("  %s%n", ((Method) mbr).toGenericString());
		} // for
		if (mbrs.length == 0)
			log.infof("  -- No %s --%n", s);
		log.infof("%n");
	}// printMembers

	private void printClasses(Class<?> c) {
		log.post(Color.GREEN, "Classes:%n");
		Class<?>[] clss = c.getClasses();
		for (Class<?> cls : clss)
			log.infof("  %s%n", cls.getCanonicalName());
		if (clss.length == 0)
			log.infof("  -- No member interfaces, classes, or enums --%n");
		log.infof("%n");
	}// printClasses

	private void printDeclaredClasses(Class<?> c) {
		log.post(Color.GREEN, "Classes:%n");
		Class<?>[] clss = c.getDeclaredClasses();
		for (Class<?> cls : clss)
			log.infof("  %s%n", cls.getCanonicalName());
		if (clss.length == 0)
			log.infof("  -- No member interfaces, classes, or enums --%n");
		log.infof("%n");
	}// printClasses

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
		return Preferences.userNodeForPackage(ExploreReflection.class).node(this.getClass().getSimpleName());
	}// getPreferences

	private void appClose() {
		Preferences myPrefs = getPreferences();
		Dimension dim = frame.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frame.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.putInt("Divider", splitPane.getDividerLocation());
		myPrefs.put("CurrentProject", lblCurrentProject.getText());
		myPrefs = null;
	}// appClose

	private void appInit() {
		frame.setBounds(100, 100, 736, 488);

		Preferences myPrefs = getPreferences();
		frame.setSize(myPrefs.getInt("Width", 761), myPrefs.getInt("Height", 693));
		frame.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		splitPane.setDividerLocation(myPrefs.getInt("Divider", 250));
		lblCurrentProject.setText(myPrefs.get("CurrentProject", "C:\\Users\\admin\\git"));
		myPrefs = null;

		log.setTextPane(txtLog);
		log.setDoc(txtLog.getStyledDocument());
		log.addTimeStamp("Starting....");

	}// appInit

	public ExploreReflection() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}// windowClosing
		});
		frame.setBounds(100, 100, 736, 488);
		frame.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		toolBar = new JPanel();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.fill = GridBagConstraints.BOTH;
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		frame.getContentPane().add(toolBar, gbc_toolBar);
		GridBagLayout gbl_toolBar = new GridBagLayout();
		gbl_toolBar.columnWidths = new int[] { 0, 0 };
		gbl_toolBar.rowHeights = new int[] { 0, 0 };
		gbl_toolBar.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_toolBar.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		toolBar.setLayout(gbl_toolBar);

		verticalStrut = Box.createVerticalStrut(20);
		verticalStrut.setMaximumSize(new Dimension(0, 20));
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.fill = GridBagConstraints.VERTICAL;
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 0;
		toolBar.add(verticalStrut, gbc_verticalStrut);

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
		txtLog.setFont(new Font("Courier New", Font.PLAIN, 16));
		scrollPane.setViewportView(txtLog);

		panelLeft = new JPanel();
		splitPane.setLeftComponent(panelLeft);
		GridBagLayout gbl_panelLeft = new GridBagLayout();
		gbl_panelLeft.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelLeft.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelLeft.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelLeft.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelLeft.setLayout(gbl_panelLeft);

		verticalStrut_2 = Box.createVerticalStrut(20);
		verticalStrut_2.setPreferredSize(new Dimension(20, 20));
		verticalStrut_2.setMinimumSize(new Dimension(20, 20));
		verticalStrut_2.setMaximumSize(new Dimension(20, 20));
		GridBagConstraints gbc_verticalStrut_2 = new GridBagConstraints();
		gbc_verticalStrut_2.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_2.gridx = 0;
		gbc_verticalStrut_2.gridy = 0;
		panelLeft.add(verticalStrut_2, gbc_verticalStrut_2);

		btnClassObjects = new JButton("Class Objects");
		btnClassObjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AdapterForExploreReflection adapterForExploreReflection = new AdapterForExploreReflection();
				Class c = adapterForExploreReflection.getClass();
				log.post(Color.BLUE, "%n%n%s%n", "Object.getClass()");

				log.infof("c.getName() = %s%n", c.getName());
				log.infof("c.getPackage() =   %s%n", c.getPackage());
				log.infof("c.getSimpleName() =   %s%n", c.getSimpleName());
				log.addNL();

				byte[] bytes = new byte[12];
				c = bytes.getClass();
				log.infof("c.getSimpleName() = %s%n", c.getSimpleName());

				Set<String> s = new HashSet<String>();
				c = s.getClass();
				log.infof("c.getSimpleName() = %s%n", c.getSimpleName());
				log.post(Color.BLUE, "%n%n%s%n", " the .class Syntax");

				c = boolean.class;
				log.infof("c.getSimpleName() = %s%n", c.getSimpleName());
				c = java.io.PrintStream.class;
				log.infof("c.getSimpleName() = %s%n", c.getSimpleName());
				c = int[][][].class;
				log.infof("c.getSimpleName() = %s%n", c.getSimpleName());

				log.post(Color.BLUE, "%n%n%s%n", "Methods that return Classes");
				c = javax.swing.JButton.class.getSuperclass();
				log.infof("c.getSimpleName() = %s%n", c.getSimpleName());
				log.addNL();
				Class<?>[] cs = Character.class.getClasses();
				for (Class<?> c1 : cs) {
					log.infof("c1.getSimpleName() = %s%n", c1.getSimpleName());
				} // for
				log.addNL();
				cs = Character.class.getDeclaredClasses();
				for (Class<?> c1 : cs) {
					log.infof("c1.getSimpleName() = %s%n", c1.getSimpleName());
				} // for

				log.post(Color.BLUE, "%n%n%s%n", "Field, Method & Constructor");
				try {
					Field f = System.class.getField("out");
					c = f.getDeclaringClass();
					log.infof("c.getSimpleName() = %s%n", c.getSimpleName());
				} catch (NoSuchFieldException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SecurityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} // try

			}// actionPerformed
		});
		GridBagConstraints gbc_btnClassObjects = new GridBagConstraints();
		gbc_btnClassObjects.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnClassObjects.insets = new Insets(0, 0, 5, 5);
		gbc_btnClassObjects.gridx = 0;
		gbc_btnClassObjects.gridy = 1;
		panelLeft.add(btnClassObjects, gbc_btnClassObjects);

		verticalStrut_3 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_3 = new GridBagConstraints();
		gbc_verticalStrut_3.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_3.gridx = 0;
		gbc_verticalStrut_3.gridy = 2;
		panelLeft.add(verticalStrut_3, gbc_verticalStrut_3);

		btnModifiersAndTypes = new JButton("Modifiers & Types");
		btnModifiersAndTypes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Class<?> c = Class.forName(txtClass1.getText());
					log.infof("Class:%n  %s%n%n", c.getCanonicalName());
					log.infof("Modifiers:%n  %s%n%n", Modifier.toString(c.getModifiers()));

					log.infof("Type Parameters:%n");
					TypeVariable<?>[] tv = c.getTypeParameters();
					if (tv.length != 0) {
						log.infof("  ");
						for (TypeVariable<?> t : tv)
							log.infof("%s ", t.getName());
						log.infof("%n%n");
					} else {
						log.infof("  -- No Type Parameters --%n%n");
					}

					log.infof("Implemented Interfaces:%n");
					Type[] intfs = c.getGenericInterfaces();
					if (intfs.length != 0) {
						for (Type intf : intfs)
							log.infof("  %s%n", intf.toString());
						log.infof("%n");
					} else {
						log.infof("  -- No Implemented Interfaces --%n%n");
					}

					log.infof("Inheritance Path:%n");
					List<Class> l = new ArrayList<Class>();
					printAncestor(c, l);
					if (l.size() != 0) {
						for (Class<?> cl : l)
							log.infof("  %s%n", cl.getCanonicalName());
						log.infof("%n");
					} else {
						log.infof("  -- No Super Classes --%n%n");
					}

					log.infof("Annotations:%n");
					Annotation[] ann = c.getAnnotations();
					if (ann.length != 0) {
						for (Annotation a : ann)
							log.infof("  %s%n", a.toString());
						log.infof("%n");
					} else {
						log.infof("  -- No Annotations --%n%n");
					}

					// production code should handle this exception more gracefully
				} catch (ClassNotFoundException x) {
					x.printStackTrace();
				}

			}// actionPerformed

		});
		GridBagConstraints gbc_btnModifiersAndTypes = new GridBagConstraints();
		gbc_btnModifiersAndTypes.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnModifiersAndTypes.insets = new Insets(0, 0, 5, 5);
		gbc_btnModifiersAndTypes.gridx = 0;
		gbc_btnModifiersAndTypes.gridy = 3;
		panelLeft.add(btnModifiersAndTypes, gbc_btnModifiersAndTypes);

		txtClass1 = new JTextField();
		GridBagConstraints gbc_txtClass1 = new GridBagConstraints();
		gbc_txtClass1.insets = new Insets(0, 0, 5, 0);
		gbc_txtClass1.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtClass1.gridx = 1;
		gbc_txtClass1.gridy = 3;
		panelLeft.add(txtClass1, gbc_txtClass1);
		txtClass1.setColumns(10);

		verticalStrut_4 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_4 = new GridBagConstraints();
		gbc_verticalStrut_4.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_4.gridx = 0;
		gbc_verticalStrut_4.gridy = 4;
		panelLeft.add(verticalStrut_4, gbc_verticalStrut_4);

		btnDiscoverClassMembers = new JButton("Class Members");
		btnDiscoverClassMembers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {
					Class<?> c = Class.forName(txtClass2.getText());
					log.post(Color.BLUE, "Class:%n  %s%n%n", c.getCanonicalName());

					Package p = c.getPackage();
					log.post(Color.BLUE, "Package:%n  %s%n%n", (p != null ? p.getName() : "-- No Package --"));
					printMembers(c.getConstructors(), "Constuctors");
					printMembers(c.getFields(), "Fields");
					printMembers(c.getMethods(), "Methods");
					printMembers(c.getDeclaredMethods(), " Declared Methods");
					printClasses(c);
					log.addNL();
					printDeclaredClasses(c);

				} catch (ClassNotFoundException x) {
					x.printStackTrace();
				} // try

			}// actionPerformed
		});
		GridBagConstraints gbc_btnDiscoverClassMembers = new GridBagConstraints();
		gbc_btnDiscoverClassMembers.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDiscoverClassMembers.insets = new Insets(0, 0, 5, 5);
		gbc_btnDiscoverClassMembers.gridx = 0;
		gbc_btnDiscoverClassMembers.gridy = 5;
		panelLeft.add(btnDiscoverClassMembers, gbc_btnDiscoverClassMembers);

		txtClass2 = new JTextField();
		txtClass2.setColumns(10);
		GridBagConstraints gbc_txtClass2 = new GridBagConstraints();
		gbc_txtClass2.insets = new Insets(0, 0, 5, 0);
		gbc_txtClass2.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtClass2.gridx = 1;
		gbc_txtClass2.gridy = 5;
		panelLeft.add(txtClass2, gbc_txtClass2);

		verticalStrut_5 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_5 = new GridBagConstraints();
		gbc_verticalStrut_5.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_5.gridx = 0;
		gbc_verticalStrut_5.gridy = 6;
		panelLeft.add(verticalStrut_5, gbc_verticalStrut_5);

		btnGetNewProject = new JButton("Get Project");
		btnGetNewProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(lblCurrentProject.getText());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
					return;
				} // if
				lblCurrentProject.setText(fc.getSelectedFile().toString());
				targetClasses = new DefaultListModel<String>();
				targetClasses.clear();
				File targetDir = new File(lblCurrentProject.getText());
				loadTargetClasses(targetDir, targetClasses);
			}// actionPerformed
		});
		GridBagConstraints gbc_btnGetNewProject = new GridBagConstraints();
		gbc_btnGetNewProject.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnGetNewProject.insets = new Insets(0, 0, 0, 5);
		gbc_btnGetNewProject.gridx = 0;
		gbc_btnGetNewProject.gridy = 7;
		panelLeft.add(btnGetNewProject, gbc_btnGetNewProject);

		lblCurrentProject = new JLabel("New label");
		GridBagConstraints gbc_lblCurrentProject = new GridBagConstraints();
		gbc_lblCurrentProject.gridx = 1;
		gbc_lblCurrentProject.gridy = 7;
		panelLeft.add(lblCurrentProject, gbc_lblCurrentProject);

		panelStatus = new JPanel();
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.fill = GridBagConstraints.BOTH;
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 2;
		frame.getContentPane().add(panelStatus, gbc_panelStatus);
		GridBagLayout gbl_panelStatus = new GridBagLayout();
		gbl_panelStatus.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelStatus.rowHeights = new int[] { 0, 0 };
		gbl_panelStatus.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelStatus.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelStatus.setLayout(gbl_panelStatus);

		verticalStrut_1 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_1 = new GridBagConstraints();
		gbc_verticalStrut_1.insets = new Insets(0, 0, 0, 5);
		gbc_verticalStrut_1.gridx = 0;
		gbc_verticalStrut_1.gridy = 0;
		panelStatus.add(verticalStrut_1, gbc_verticalStrut_1);
	}// initialize

	class AdapterForExploreReflection implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

		}// actionPerformed

	}// class AdapterForExploreReflection

	enum ClassMember {
		CONSTRUCTOR, FIELD, METHOD, CLASS, ALL
	}

}// class ExploreReflection
