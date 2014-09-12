package hr.bergear;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class FilePicker extends JPanel implements ActionListener {

	private JFileChooser chooser;
	private JButton button;
	private JTextField textField;
	private Component parent;

	public FilePicker(Component parent, String buttonText) {
		this.parent = parent;
		button = new JButton(buttonText);
		chooser = new JFileChooser();
		textField = new JTextField(20);
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		button.addActionListener(this);

		add(textField);
		add(button);
	}

	public void setEnabled(boolean enable) {
		textField.setEnabled(enable);
		button.setEnabled(enable);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(parent)) {
			textField.setText(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	public String getSelectedFile() {
		return chooser.getSelectedFile().getAbsolutePath();
	}
	
	public JFileChooser getFileChooser() {
		return chooser;
	}
	
}
