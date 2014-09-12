package hr.bergear;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultEditorKit;

@SuppressWarnings("serial")
public class GUI extends JFrame {
	
	private static final int FILE = 0;
	private static final int INPLACE = 1;

	private JTextArea textArea;
	private CompoundUndoManager um;
	private JButton generateBtn;
	
	private Parser parser;
	private JCheckBox clipboardCB;
	private Clipboard clipboard;
	private FilePicker filePicker;
	
	private int outputMethod = INPLACE;


	public GUI() {
		parser = new Parser();
		initGUI();
	}

	

	private void initGUI() {
		
		setTitle("BeanGenerator v0.1");

		setPreferredSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		textArea = new JTextArea();
		textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
				(KeyStroke) generateBtnAction.getValue(Action.ACCELERATOR_KEY),
				"myAction");

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
			String javaBeanSourceCode = BeanGenerator.generateBean(parser.getBeanName(), parser.getProperties());

			if (clipboardCB.isSelected()) {
				StringSelection stringSelection = new StringSelection(javaBeanSourceCode);
				clipboard.setContents(stringSelection, null);
			}

			outputSourceCode(javaBeanSourceCode);
			
//			try {
//				JsonParser.parse(textArea.getText());
//			} catch (JsonProcessingException e1) {
//				System.out.println("Json parsing problem: " + e1.getMessage());
//				return;
//			} catch (IOException e2) {
//				System.out.println("IO problem (JSON)");
//				return;
//			}
//			
//			System.out.println(textArea.getText());
		}
	};

	private JPanel createPanel() {
		
		
		JPanel panel = new JPanel();
		panel.setMaximumSize(new Dimension(40, 10));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel label = new JLabel("Output method:");

		JRadioButton fileRb = new JRadioButton("File");
		JRadioButton inplaceRb = new JRadioButton("In place");

		clipboardCB = new JCheckBox("Automatically copy to clipboard", false);

		filePicker = new FilePicker(this, "Browse...");
		filePicker.getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		filePicker.getFileChooser().setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));
		
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

		inplaceRb.setSelected(true);
		filePicker.setEnabled(false);

		ButtonGroup group = new ButtonGroup();
		group.add(fileRb);
		group.add(inplaceRb);

		panel.add(label);
		panel.add(inplaceRb);
		panel.add(fileRb);
		panel.add(filePicker);
		panel.add(clipboardCB);

		

//		JPanel panel = new JPanel();
//		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//		filePicker = new FilePicker(this, "Browse...");
//		panel.add(filePicker);
		panel.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		
//		JRadioButton fileOutput = new JRadioButton("File");
//		JRadioButton fileInplace = new JRadioButton("In place");

		return panel;

	}
	
	
	private void outputSourceCode(String javaBeanSourceCode) {
		switch (outputMethod) {
		case FILE:
			String path = filePicker.getSelectedFile();

			File file = new File(path + System.getProperty("file.separator") + parser.getBeanName() + ".java");
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

		parser.parseInput(input);
		
		if (parser.isJustName()) {
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

		menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
		menuItem.setText("Paste");
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		editMenu.add(menuItem);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		return menuBar;
	}
	
	public static void main(String[] args) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					try {
						// Set cross-platform Java L&F (also called "Metal")
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
					gui.setExtendedState(gui.getExtendedState()
							| JFrame.MAXIMIZED_BOTH);

				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	

}
