package hr.bergear;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultEditorKit;

@SuppressWarnings("serial")
public class GUI extends JFrame {

	private static final int FILE = 0;
	private static final int INPLACE = 1;
	
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);

	private JTextArea textArea;
	private CompoundUndoManager um;
	private JButton generateBtn;

	private Parser parser;
	private JCheckBox clipboardCB;
	private Clipboard clipboard;
	private FilePicker filePicker;

	private int outputMethod = INPLACE;

	private JRadioButton csvRadio;
	private JRadioButton jsonRadio;
	private JRadioButton xmlRadio;

	public GUI() {
		parser = CsvParser.getInstance();
		initGUI();
	}

	private void initGUI() {

		setTitle("BeanGenerator v0.1");

		setPreferredSize(dimension);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
		setMinimumSize(dimension);

		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		textArea = new JTextArea();
		textArea.setTabSize(4);
		textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK), "none");
		
		um = new CompoundUndoManager(textArea);

		JToolBar toolbar = new JToolBar();
		JButton undo = new JButton(um.getUndoAction());
		JButton redo = new JButton(um.getRedoAction());
		
		generateBtn = new JButton(generateBtnAction);
		// configure the Action with the accelerator (aka: short cut)
		generateBtnAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK));

		// manually register the accelerator in the button's component input map
		generateBtn.getActionMap().put("myAction", generateBtnAction);
		generateBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				(KeyStroke) generateBtnAction.getValue(Action.ACCELERATOR_KEY), "myAction");

		setJMenuBar(createMenuBar());
		add(textArea, BorderLayout.CENTER);
		toolbar.add(undo);
		toolbar.add(redo);
		add(toolbar, BorderLayout.NORTH);

		add(generateBtn, BorderLayout.SOUTH);
		add(createPanel(), BorderLayout.EAST);

		pack();
	}

	private Action generateBtnAction = new AbstractAction("Generate") {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (!parseInput()) {
				return;
			}
			String javaBeanSourceCode = BeanGenerator.generateBean(parser.getBeanInfo());

			if (clipboardCB.isSelected()) {
				StringSelection stringSelection = new StringSelection(javaBeanSourceCode);
				clipboard.setContents(stringSelection, null);
			}

			outputSourceCode(javaBeanSourceCode);
		}
	};

	private JPanel createPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5, 1));

		filePicker = new FilePicker(this, "Browse...");
		filePicker.getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		filePicker.getFileChooser().setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));
		filePicker.setEnabled(false);

		csvRadio = new JRadioButton("CSV");
		jsonRadio = new JRadioButton("JSON");
		xmlRadio = new JRadioButton("XML");
		csvRadio.setSelected(true);

		ButtonGroup groupType = new ButtonGroup();
		groupType.add(csvRadio);
		groupType.add(jsonRadio);
		groupType.add(xmlRadio);

		JPanel inputTypePanel = new JPanel();
		inputTypePanel.setPreferredSize(new Dimension(100, 100));
		GridLayout gl = new GridLayout(3, 1);
		gl.setHgap(3);
		gl.setVgap(5);

		inputTypePanel.setLayout(gl);
		inputTypePanel.setBorder(BorderFactory.createTitledBorder("Input format"));
		inputTypePanel.add(csvRadio);
		inputTypePanel.add(jsonRadio);
		inputTypePanel.add(xmlRadio);

		csvRadio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				parser = CsvParser.getInstance();
			}
		});

		jsonRadio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				parser = JsonParser.getInstance();
			}
		});

		xmlRadio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				parser = XmlParser.getInstance();
			}
		});

		JRadioButton fileRb = new JRadioButton("File");
		JRadioButton inplaceRb = new JRadioButton("In place");
		inplaceRb.setSelected(true);

		JPanel outputMethodPanel = new JPanel();
		GridLayout gl1 = new GridLayout(3, 1);
		outputMethodPanel.setLayout(gl1);
		outputMethodPanel.setBorder(BorderFactory.createTitledBorder("Output method:"));
		outputMethodPanel.add(inplaceRb);
		outputMethodPanel.add(fileRb);
		outputMethodPanel.add(filePicker);

		ButtonGroup group = new ButtonGroup();
		group.add(fileRb);
		group.add(inplaceRb);

		fileRb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				filePicker.setEnabled(true);
				outputMethod = FILE;
			}
		});

		inplaceRb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				filePicker.setEnabled(false);
				outputMethod = INPLACE;
			}
		});

		clipboardCB = new JCheckBox("Automatically copy to clipboard", false);

		panel.add(outputMethodPanel);
		panel.add(inputTypePanel);
		panel.add(clipboardCB);

		panel.setBorder(BorderFactory.createLoweredSoftBevelBorder());

		return panel;

	}

	private void outputSourceCode(String javaBeanSourceCode) {
		switch (outputMethod) {
		case FILE:
			String path = filePicker.getSelectedFile();

			File file = new File(path + System.getProperty("file.separator") + parser.getBeanInfo().getName() + ".java");
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
				writer.write(javaBeanSourceCode);
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(GUI.this, "File not found", "File problem", JOptionPane.ERROR_MESSAGE);
				break;
			} catch (IOException e2) {
				JOptionPane.showMessageDialog(GUI.this, "Cannot generate the bean!", "Writing problem",
						JOptionPane.ERROR_MESSAGE);
			} finally {
				try {
					writer.close();
				} catch (IOException ex) {
					// ignorable?
				}
			}
			break;
		case INPLACE:
			textArea.setText(javaBeanSourceCode);
			break;
		}
	}

	/**
	 * Parses input from text area.
	 */
	private boolean parseInput() {
		String input = textArea.getText();
		if (input.trim().isEmpty()) {
			JOptionPane.showMessageDialog(GUI.this, "Input is empty", "Empty input", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		try {
			parser.parse(input);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(GUI.this, e.getMessage(), "Parsing error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (parser.getBeanInfo().getProperties() == null) {
			JOptionPane.showMessageDialog(GUI.this, "Trying to generate an empty bean", "Empty bean warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

	/**
	 * Creates menu bar with menus, menu items and corresponding actions.
	 * 
	 * @return initialized menu bar
	 */
	public JMenuBar createMenuBar() {
		JMenuItem menuItem = null;
		JMenuBar menuBar = new JMenuBar();
		JMenu editMenu = new JMenu("Edit");
		JMenu fileMenu = new JMenu("File");
		editMenu.setMnemonic(KeyEvent.VK_E);
		fileMenu.setMnemonic(KeyEvent.VK_F);

		menuItem = new JMenuItem();
		menuItem.setText("Exit");
		menuItem.setMnemonic(KeyEvent.VK_K);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
		fileMenu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
		menuItem.setText("Cut");
		menuItem.setMnemonic(KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		editMenu.add(menuItem);

		menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
		menuItem.setText("Copy");
		menuItem.setMnemonic(KeyEvent.VK_C);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		editMenu.add(menuItem);

		menuItem = new JMenuItem(pasteAction);
		menuItem.setText("Paste");
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		editMenu.add(menuItem);
		
		editMenu.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		
		menuItem = new JMenuItem(um.getUndoAction());
		editMenu.add(menuItem);
		
		menuItem = new JMenuItem(um.getRedoAction());
		editMenu.add(menuItem);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		return menuBar;
	}

	private AbstractAction pasteAction = new AbstractAction("Paste") {

		Action paste = new DefaultEditorKit.PasteAction();

		@Override
		public void actionPerformed(ActionEvent e) {
			paste.actionPerformed(e);
			String text = textArea.getText().trim();

			if (text.startsWith("<")) {
				xmlRadio.doClick();
			} else if (text.startsWith("{")) {
				jsonRadio.doClick();
			} else {
				csvRadio.doClick();
			}

		}
	};

	public static void main(String[] args) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (UnsupportedLookAndFeelException e) {
						// handle exception
					} catch (ClassNotFoundException e) {
						// handle exception
					} catch (InstantiationException e) {
						// handle exception
					} catch (IllegalAccessException e) {
						// handle exception
					}

					GUI gui = new GUI();
					gui.setVisible(true);
					gui.setExtendedState(gui.getExtendedState() | JFrame.MAXIMIZED_BOTH);

				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
