package droid64.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import droid64.d64.DiskImage;
import droid64.d64.Utility;
import droid64.db.DaoFactory;
import droid64.db.DaoFactoryImpl;

/**<pre style='font-family:Sans,Arial,Helvetica'>
 * Created on 30.06.2004
 *
 *   droiD64 - A graphical filemanager for D64 files
 *   Copyright (C) 2004 Wolfram Heyer
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *   eMail: wolfvoz@users.sourceforge.net
 *   http://droid64.sourceforge.net
 *  </pre>
 * @author wolf
 */
public class SettingsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final String LBL_FOREGROUND = Settings.getMessage(Resources.DROID64_SETTINGS_FOREGROUND);
	private static final String LBL_BACKGROUND = Settings.getMessage(Resources.DROID64_SETTINGS_BACKGROUND);

	private JComboBox<String> colourBox;
	private JCheckBox exitConfirmCheckBox;
	private JSlider distSlider;
	private JSlider distSlider2;
	private JTextField winSizePosField;


	private JTextField fileExtD64;
	private JTextField fileExtD67;
	private JTextField fileExtD71;
	private JTextField fileExtD81;
	private JTextField fileExtT64;
	private JTextField fileExtD80;
	private JTextField fileExtD82;
	private JTextField fileExtLNX;

	private JTextField fileExtD64gz;
	private JTextField fileExtD67gz;
	private JTextField fileExtD71gz;
	private JTextField fileExtD81gz;
	private JTextField fileExtT64gz;
	private JTextField fileExtD80gz;
	private JTextField fileExtD82gz;
	private JTextField fileExtLNXgz;

	private final JButton colorFgButton = new JButton(LBL_FOREGROUND);
	private final JButton colorBgButton = new JButton(LBL_BACKGROUND);
	private final JButton colorCpmFgButton = new JButton(LBL_FOREGROUND);
	private final JButton colorCpmBgButton = new JButton(LBL_BACKGROUND);
	private final JButton colorLocalFgButton = new JButton(LBL_FOREGROUND);
	private final JButton colorLocalBgButton = new JButton(LBL_BACKGROUND);

	// Plugin settings
	private JTextField[] pluginLabelTextField = new JTextField[Settings.MAX_PLUGINS];
	private JTextField[] pluginCommandTextField= new JTextField[Settings.MAX_PLUGINS];
	private JTextArea[] pluginArgumentTextField= new JTextArea[Settings.MAX_PLUGINS];
	private JTextArea[] pluginDescriptionTextField = new JTextArea[Settings.MAX_PLUGINS];
	private JCheckBox[] forkThreadCheckBox = new JCheckBox[Settings.MAX_PLUGINS];

	// Database settings
	private JCheckBox useJdbcCheckBox;
	private JTextField jdbcDriver;
	private JTextField jdbcUrl;
	private JTextField jdbcUser;
	private JPasswordField jdbcPassword;
	private JFormattedTextField maxRows;
	private JComboBox<String> lookAndFeelBox;
	private JComboBox<String> limitTypeBox;
	/** Colors */
	private static final String[] COLORS = { "gray", "red", "green", "blue", "light-blue", "dark-grey", "cyan" };


	private JTextField status = new JTextField();

	private static final String ARGUMENT_TOOLTIP =
			"<html>Arguments to file to be executed. " +
					"These are keywords which may be used in the arguments:<ul>" +
					"<li><i>{Image}</i> - The filename of the disk image.</li>" +
					"<li><i>{Files}</i> - The names of the selected files.</li>" +
					"<li><i>{ImageFiles}</i> - Similar to <i>{Files}</i> but each file is prefixed by the <i>image</i>:.</li>" +
					"<li><i>{Target}</i> - The path of the the other disk pane.</li>" +
					"<li><i>{NewFile}</i> - Opens a file dialog asking for a name for a new disk image.</li>" +
					"<li><i>{ImageType}</i> - Type of image (D64, D67, D71, D80, D81 D82, T64, LNX).</li>" +
					"<li><i>{DriveType}</i> - Type of drive used for the image (1541, 1571, 1581, 4250, 8250).</li>" +
					"</ul></html>";
	private static String sqlSetupScript = null;

	/**
	 * Constructor
	 * @param topText String
	 * @param mainPanel MainPanel
	 */
	public SettingsDialog (String topText, MainPanel mainPanel) {
		setTitle(topText);
		setModal(true);

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab(Settings.getMessage(Resources.DROID64_SETTINGS_TAB_GUI), drawGuiPanel(mainPanel));
		tabPane.addTab(Settings.getMessage(Resources.DROID64_SETTINGS_TAB_FILES), drawFilesPanel());
		tabPane.addTab(Settings.getMessage(Resources.DROID64_SETTINGS_TAB_COLORS), drawColorPanel());
		tabPane.addTab(Settings.getMessage(Resources.DROID64_SETTINGS_TAB_DATABASE), drawDatabasePanel(mainPanel));
		String pluginTitle = Settings.getMessage(Resources.DROID64_SETTINGS_TAB_PLUGIN) + " ";
		JPanel[] pluginPanel = drawPluginPanel(mainPanel);
		for (int i = 0; i < Settings.MAX_PLUGINS; i++) {
			tabPane.addTab(pluginTitle+(i+1), pluginPanel[i]);
		}

		cp.add(new JScrollPane(tabPane), BorderLayout.CENTER);

		JPanel generalPanel = drawGeneralPanel(mainPanel);
		cp.add(generalPanel, BorderLayout.SOUTH);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( (int)((dim.width - getSize().getWidth()) / 3),	(int)((dim.height - getSize().getHeight()) / 3)	);
		pack();
		setVisible(mainPanel != null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * Setup up general settings panel
	 * @return JPanel
	 */
	private JPanel drawGeneralPanel(final MainPanel mainPanel) {
		JPanel generalPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		final JButton okButton = new JButton(Settings.getMessage(Resources.DROID64_SETTINGS_OK));
		okButton.setToolTipText(Settings.getMessage(Resources.DROID64_SETTINGS_OK_TOOLTIP));
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if ( event.getSource()==okButton ) {
					copyValuesToSettings(mainPanel);
					Settings.parseExternalPrograms();
					Settings.saveSettingsToFile();
					dispose();
				}
			}
		});
		buttonPanel.add(okButton);
		JPanel txtPanel = new JPanel();
		txtPanel.setAlignmentX(CENTER_ALIGNMENT);
		txtPanel.add(new JLabel(Settings.getMessage(Resources.DROID64_SETTINGS_SAVEMESSAGE)));
		generalPanel.add(txtPanel, BorderLayout.NORTH);
		generalPanel.add(buttonPanel, BorderLayout.SOUTH);
		return generalPanel;
	}

	private JPanel drawFilesPanel() {
		JPanel guiPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;

		final JTextField defaultImgDir = new JTextField(Settings.getDefaultImageDir());
		final JTextField defaultImgDir2 = new JTextField(Settings.getDefaultImageDir2());
		final JButton defaultImgButton = new JButton("..");
		final JButton defaultImgButton2 = new JButton("..");

		ActionListener defaultImgDirListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if ( event.getSource() == defaultImgButton ) {
					String choosen = openImgDirDialog(defaultImgDir.getText());
					if (choosen != null) {
						defaultImgDir.setText(choosen);
						Settings.setDefaultImageDir(choosen);
					}
				}
				if ( event.getSource() == defaultImgButton2 ) {
					String choosen = openImgDirDialog(defaultImgDir2.getText());
					if (choosen != null) {
						defaultImgDir2.setText(choosen);
						Settings.setDefaultImageDir2(choosen);
					}
				}
			}
		};
		defaultImgButton.addActionListener(defaultImgDirListener);
		defaultImgButton2.addActionListener(defaultImgDirListener);

		JPanel imgDirPanel = new JPanel(new BorderLayout());
		imgDirPanel.add(defaultImgDir, BorderLayout.CENTER);
		imgDirPanel.add(defaultImgButton, BorderLayout.EAST);

		JPanel imgDirPanel2 = new JPanel(new BorderLayout());
		imgDirPanel2.add(defaultImgDir2, BorderLayout.CENTER);
		imgDirPanel2.add(defaultImgButton2, BorderLayout.EAST);

		createImgExtFields();

		addToGridBag(0, 0, 0.0, 0.0, 1, gbc, guiPanel, new JLabel(Settings.getMessage(Resources.DROID64_SETTINGS_LEFTDIR)));
		addToGridBag(1, 0, 1.0, 0.0, 2, gbc, guiPanel, imgDirPanel);
		addToGridBag(2, 0, 0.0, 0.0, 1, gbc, guiPanel, new JPanel());

		addToGridBag(0, 1, 0.0, 0.0, 1, gbc, guiPanel, new JLabel(Settings.getMessage(Resources.DROID64_SETTINGS_RIGHTDIR)));
		addToGridBag(1, 1, 1.0, 0.0, 2, gbc, guiPanel, imgDirPanel2);
		addToGridBag(2, 1, 0.0, 0.0, 1, gbc, guiPanel, new JPanel());

		addFields(2, Resources.DROID64_SETTINGS_EXT_D64, fileExtD64, fileExtD64gz, guiPanel, gbc);
		addFields(3, Resources.DROID64_SETTINGS_EXT_D67, fileExtD67, fileExtD67gz, guiPanel, gbc);
		addFields(4, Resources.DROID64_SETTINGS_EXT_D71, fileExtD71, fileExtD71gz, guiPanel, gbc);
		addFields(5, Resources.DROID64_SETTINGS_EXT_D80, fileExtD80, fileExtD80gz, guiPanel, gbc);
		addFields(6, Resources.DROID64_SETTINGS_EXT_D81, fileExtD81, fileExtD81gz, guiPanel, gbc);
		addFields(7, Resources.DROID64_SETTINGS_EXT_D82, fileExtD82, fileExtD82gz, guiPanel, gbc);
		addFields(8, Resources.DROID64_SETTINGS_EXT_T64, fileExtT64, fileExtT64gz, guiPanel, gbc);
		addFields(9, Resources.DROID64_SETTINGS_EXT_LNX, fileExtLNX, fileExtLNXgz, guiPanel, gbc);

		addToGridBag(0, 10, 1.0, 0.8, 4, gbc, guiPanel, new JPanel());
		return guiPanel;
	}

	private void addFields(int row, String propertyKey, JComponent field1, JComponent field2, JPanel panel, GridBagConstraints gbc) {
		addToGridBag(0, row, 0.0, 0.0, 1, gbc, panel, new JLabel(Settings.getMessage(propertyKey)));
		addToGridBag(1, row, 0.5, 0.0, 1, gbc, panel, field1);
		addToGridBag(2, row, 0.5, 0.0, 1, gbc, panel, field2);
		addToGridBag(3, row, 1.0, 0.0, 1, gbc, panel, new JPanel());
	}

	private String getWindowSizePosString() {
		int[] sizeLocation = Settings.getWindow();
		if (sizeLocation.length < 4) {
			return "";
		}
		return String.format("%d:%d,%d:%d", sizeLocation[0], sizeLocation[1], sizeLocation[2], sizeLocation[3]);
	}

	/**
	 * Setup panel with GUI settings
	 * @param mainPanel
	 * @return JPanel
	 */
	private JPanel drawGuiPanel(final MainPanel mainPanel) {
		JPanel guiPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;

		exitConfirmCheckBox = new JCheckBox(Settings.getMessage(Resources.DROID64_SETTINGS_CONFIRMEXIT));
		exitConfirmCheckBox.setToolTipText(Settings.getMessage(Resources.DROID64_SETTINGS_CONFIRMEXIT_TOOLTIP));
		exitConfirmCheckBox.setSelected(Settings.getAskQuit());

		lookAndFeelBox = new JComboBox<>(MainPanel.getLookAndFeelNames());
		lookAndFeelBox.setToolTipText(Settings.getMessage(Resources.DROID64_SETTINGS_LOOKFEEL_TOOLTIP));
		lookAndFeelBox.setEditable(false);
		lookAndFeelBox.setSelectedIndex(0);
		lookAndFeelBox.setSelectedIndex(Settings.getLookAndFeel() < MainPanel.getLookAndFeelNames().length ? Settings.getLookAndFeel() : 0);

		distSlider = createSlider(Settings.getRowHeight(), Settings.getMessage(Resources.DROID64_SETTINGS_GRIDSPACING_TOOLTIP));
		distSlider2 = createSlider(Settings.getLocalRowHeight(), Settings.getMessage(Resources.DROID64_SETTINGS_GRIDSPACING_TOOLTIP));

		final JSpinner fontSizeSpinner = new JSpinner(new SpinnerNumberModel(Settings.getFontSize(), 8, 72, 1));
		final JSpinner localFontSizeSpinner = new JSpinner(new SpinnerNumberModel(Settings.getLocalFontSize(), 8, 72, 1));
		fontSizeSpinner.setToolTipText(Settings.getMessage(Resources.DROID64_SETTINGS_FONTSIZE_TOOLTIP));
		localFontSizeSpinner.setToolTipText(Settings.getMessage(Resources.DROID64_SETTINGS_FONTSIZE_TOOLTIP));

		ChangeListener fontSizeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				try {
					if (event.getSource() == fontSizeSpinner) {
						int fs = Integer.parseInt(fontSizeSpinner.getValue().toString());
						Settings.setFontSize(fs);
					} else if (event.getSource() == localFontSizeSpinner) {
						int fs = Integer.parseInt(localFontSizeSpinner.getValue().toString());
						Settings.setLocalFontSize(fs);
					}
				} catch (NumberFormatException e) { /* don't care */ }
			}
		};
		fontSizeSpinner.addChangeListener(fontSizeListener);
		localFontSizeSpinner.addChangeListener(fontSizeListener);

		winSizePosField = new JTextField(getWindowSizePosString());
		JButton getWinSizeButton = new JButton(Settings.getMessage(Resources.DROID64_SETTINGS_WINDOWSIZE_FETCH));
		getWinSizeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Dimension size = mainPanel.getParent().getSize();
				Point location = mainPanel.getParent().getLocation();
				String str = String.format("%d:%d,%d:%d", (int)size.getWidth(), (int)size.getHeight(), (int)location.getX(), (int)location.getY());
				winSizePosField.setText(str);
			}
		});

		JPanel winSizePanel = new JPanel(new BorderLayout());
		winSizePanel.add(winSizePosField, BorderLayout.CENTER);
		winSizePanel.add(getWinSizeButton, BorderLayout.EAST);

		JPanel fontSizePanel1 = new JPanel(new BorderLayout());
		fontSizePanel1.add(fontSizeSpinner, BorderLayout.WEST);
		fontSizePanel1.add(new JPanel(), BorderLayout.CENTER);

		JPanel fontSizePanel2 = new JPanel(new BorderLayout());
		fontSizePanel2.add(localFontSizeSpinner, BorderLayout.WEST);
		fontSizePanel2.add(new JPanel(), BorderLayout.CENTER);

		addToGridBag(0, 0, 0.0, 0.0, 1, gbc, guiPanel, new JPanel());
		addToGridBag(1, 0, 0.5, 0.0, 1, gbc, guiPanel, exitConfirmCheckBox);
		addToGridBag(2, 0, 0.0, 0.0, 1, gbc, guiPanel, new JPanel());

		addField(1, Resources.DROID64_SETTINGS_LOOKFEEL, lookAndFeelBox, guiPanel, gbc);
		addField(2, Resources.DROID64_SETTINGS_GRIDSPACING_IMAGE, distSlider, guiPanel, gbc);
		addField(3, Resources.DROID64_SETTINGS_GRIDSPACING_LOCAL, distSlider2, guiPanel, gbc);
		addField(4, Resources.DROID64_SETTINGS_FONTSIZE_IMAGE, fontSizePanel1, guiPanel, gbc);
		addField(5, Resources.DROID64_SETTINGS_FONTSIZE_LOCAL, fontSizePanel2, guiPanel, gbc);
		addField(6, Resources.DROID64_SETTINGS_WINDOWSIZE, winSizePanel, guiPanel, gbc);
		addToGridBag(0, 7, 0.5, 0.8, 3, gbc, guiPanel, new JPanel());
		return guiPanel;
	}

	private void addField(int row, String propertyKey, JComponent component, JPanel panel, GridBagConstraints gbc) {
		addToGridBag(0, row, 0.0, 0.0, 1, gbc, panel, new JLabel(Settings.getMessage(propertyKey)));
		addToGridBag(1, row, 1.0, 0.0, 1, gbc, panel, component);
		addToGridBag(2, row, 0.0, 0.0, 1, gbc, panel, new JPanel());
	}

	private JSlider createSlider(int rowHeight, String toolTip) {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 8, 20, rowHeight);
		slider.setMajorTickSpacing(2);
		slider.setMinorTickSpacing(1);
		slider.setSnapToTicks(true);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setToolTipText(toolTip);
		return slider;
	}

	private void createImgExtFields() {
		fileExtD64 = new JTextField(Settings.getFileExtensions(DiskImage.D64_IMAGE_TYPE, false));
		fileExtD67 = new JTextField(Settings.getFileExtensions(DiskImage.D67_IMAGE_TYPE, false));
		fileExtD71 = new JTextField(Settings.getFileExtensions(DiskImage.D71_IMAGE_TYPE, false));
		fileExtD81 = new JTextField(Settings.getFileExtensions(DiskImage.D81_IMAGE_TYPE, false));
		fileExtT64 = new JTextField(Settings.getFileExtensions(DiskImage.T64_IMAGE_TYPE, false));
		fileExtD80 = new JTextField(Settings.getFileExtensions(DiskImage.D80_IMAGE_TYPE, false));
		fileExtD82 = new JTextField(Settings.getFileExtensions(DiskImage.D82_IMAGE_TYPE, false));
		fileExtLNX = new JTextField(Settings.getFileExtensions(DiskImage.LNX_IMAGE_TYPE, false));
		// Compressed
		fileExtD64gz = new JTextField(Settings.getFileExtensions(DiskImage.D64_IMAGE_TYPE, true));
		fileExtD67gz = new JTextField(Settings.getFileExtensions(DiskImage.D67_IMAGE_TYPE, true));
		fileExtD71gz = new JTextField(Settings.getFileExtensions(DiskImage.D71_IMAGE_TYPE, true));
		fileExtD81gz = new JTextField(Settings.getFileExtensions(DiskImage.D81_IMAGE_TYPE, true));
		fileExtT64gz = new JTextField(Settings.getFileExtensions(DiskImage.T64_IMAGE_TYPE, true));
		fileExtD80gz = new JTextField(Settings.getFileExtensions(DiskImage.D80_IMAGE_TYPE, true));
		fileExtD82gz = new JTextField(Settings.getFileExtensions(DiskImage.D82_IMAGE_TYPE, true));
		fileExtLNXgz = new JTextField(Settings.getFileExtensions(DiskImage.LNX_IMAGE_TYPE, true));
	}

	private int[] parseWindowSizePos(String str) {
		String[] strArr = str.trim().split("\\s*[,:]\\s*");
		if (strArr.length < 4) {
			return null;
		}
		try {
			int[] intArr = new int[4];
			for (int i=0; i < intArr.length; i++) {
				intArr[i] = Integer.valueOf(strArr[i]);
			}
			return intArr;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private void copyValuesToSettings(final MainPanel mainPanel) {
		Settings.setAskQuit(exitConfirmCheckBox.isSelected());
		Settings.setColourChoice(colourBox.getSelectedIndex());
		Settings.setRowHeight(distSlider.getValue());
		Settings.setLookAndFeel(lookAndFeelBox.getSelectedIndex());
		Settings.setLocalRowHeight(distSlider2.getValue());
		Settings.setUseDb(useJdbcCheckBox.isSelected());
		Settings.setJdbcDriver(jdbcDriver.getText());
		Settings.setJdbcUrl(jdbcUrl.getText());
		Settings.setJdbcUser(jdbcUser.getText());
		Settings.setJdbcPassword(new String(jdbcPassword.getPassword()));
		Settings.setJdbcLimitType(limitTypeBox.getSelectedIndex());
		Settings.setWindow(parseWindowSizePos(winSizePosField.getText()));

		Settings.setFileExtensions(DiskImage.D64_IMAGE_TYPE, fileExtD64.getText(), false);
		Settings.setFileExtensions(DiskImage.D67_IMAGE_TYPE, fileExtD67.getText(), false);
		Settings.setFileExtensions(DiskImage.D71_IMAGE_TYPE, fileExtD71.getText(), false);
		Settings.setFileExtensions(DiskImage.D80_IMAGE_TYPE, fileExtD80.getText(), false);
		Settings.setFileExtensions(DiskImage.D81_IMAGE_TYPE, fileExtD81.getText(), false);
		Settings.setFileExtensions(DiskImage.D82_IMAGE_TYPE, fileExtD82.getText(), false);
		Settings.setFileExtensions(DiskImage.LNX_IMAGE_TYPE, fileExtLNX.getText(), false);
		Settings.setFileExtensions(DiskImage.T64_IMAGE_TYPE, fileExtT64.getText(), false);
		Settings.setFileExtensions(DiskImage.D64_IMAGE_TYPE, fileExtD64gz.getText(), true);
		Settings.setFileExtensions(DiskImage.D67_IMAGE_TYPE, fileExtD67gz.getText(), true);
		Settings.setFileExtensions(DiskImage.D71_IMAGE_TYPE, fileExtD71gz.getText(), true);
		Settings.setFileExtensions(DiskImage.D80_IMAGE_TYPE, fileExtD80gz.getText(), true);
		Settings.setFileExtensions(DiskImage.D81_IMAGE_TYPE, fileExtD81gz.getText(), true);
		Settings.setFileExtensions(DiskImage.D82_IMAGE_TYPE, fileExtD82gz.getText(), true);
		Settings.setFileExtensions(DiskImage.LNX_IMAGE_TYPE, fileExtLNXgz.getText(), true);
		Settings.setFileExtensions(DiskImage.T64_IMAGE_TYPE, fileExtT64gz.getText(), true);

		try {
			Settings.setMaxRows(Integer.parseInt(maxRows.getText()));
		} catch (NumberFormatException e) {
			maxRows.setValue(25L);
		}

		for (int i = 0; i < pluginCommandTextField.length; i++) {
			mainPanel.setPluginButtonLabel(i, pluginLabelTextField[i].getText());
			Settings.setExternalProgram(i,
					pluginCommandTextField[i].getText(),
					pluginArgumentTextField[i].getText(),
					pluginDescriptionTextField[i].getText(),
					pluginLabelTextField[i].getText(),
					forkThreadCheckBox[i].isSelected());
		}
	}

	private void setupColorButton(final JButton fgButton, final JButton bgButton, final String fg, final String bg) {
		fgButton.setForeground(Settings.getColorParam(fg));
		fgButton.setBackground(Settings.getColorParam(bg));
		bgButton.setForeground(Settings.getColorParam(fg));
		bgButton.setBackground(Settings.getColorParam(bg));
		ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				if ( event.getSource()==fgButton ) {
					Color c = JColorChooser.showDialog(fgButton, LBL_FOREGROUND, fgButton.getForeground());
					fgButton.setForeground(c);
					bgButton.setForeground(c);
					Settings.setColorParam(fg, fgButton.getForeground());
					Settings.setColorParam(bg, bgButton.getBackground());
				} else if ( event.getSource()==bgButton ) {
					Color c = JColorChooser.showDialog(bgButton, LBL_BACKGROUND, bgButton.getBackground());
					fgButton.setBackground(c);
					bgButton.setBackground(c);
					Settings.setColorParam(fg, fgButton.getForeground());
					Settings.setColorParam(bg, bgButton.getBackground());
				}
			}
		};
		fgButton.addActionListener(listener);
		bgButton.addActionListener(listener);
	}

	/**
	 * Setup Color settings
	 * @return JPanel
	 */
	private JPanel drawColorPanel() {

		colourBox = new JComboBox<>(COLORS);
		colourBox.setToolTipText(Settings.getMessage(Resources.DROID64_SETTINGS_COLOR_SCHEME_TOOLTIP));
		colourBox.setEditable(false);
		colourBox.setSelectedIndex(Settings.getColourChoice()<COLORS.length ? Settings.getColourChoice() : 0);

		setupColorButton(colorFgButton, colorBgButton, Settings.SETTING_DIR_FG, Settings.SETTING_DIR_BG);
		setupColorButton(colorCpmFgButton, colorCpmBgButton, Settings.SETTING_DIR_CPM_FG, Settings.SETTING_DIR_CPM_BG);
		setupColorButton(colorLocalFgButton, colorLocalBgButton, Settings.SETTING_DIR_LOCAL_FG, Settings.SETTING_DIR_LOCAL_BG);

		final JButton colorActiveBorderButton = new JButton(Settings.getMessage(Resources.DROID64_SETTINGS_COLOR_ACTIVE));
		colorActiveBorderButton.setBorder(BorderFactory.createLineBorder(Settings.getActiveBorderColor(), 3));
		colorActiveBorderButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				if ( event.getSource()==colorActiveBorderButton ) {
					Color c = JColorChooser.showDialog(colorActiveBorderButton, LBL_FOREGROUND, Settings.getActiveBorderColor());
					colorActiveBorderButton.setBorder(BorderFactory.createLineBorder(c));
					Settings.setActiveBorderColor(c);
				}
			}
		});
		final JButton colorInactiveBorderButton = new JButton(Settings.getMessage(Resources.DROID64_SETTINGS_COLOR_INACTIVE));
		colorInactiveBorderButton.setBorder(BorderFactory.createLineBorder(Settings.getInactiveBorderColor(), 3));
		colorInactiveBorderButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				if ( event.getSource()==colorInactiveBorderButton ) {
					Color c = JColorChooser.showDialog(colorInactiveBorderButton, LBL_BACKGROUND, Settings.getInactiveBorderColor());
					colorInactiveBorderButton.setBorder(BorderFactory.createLineBorder(c));
					Settings.setInactiveBorderColor(c);
				}
			}
		});

		final JButton colorResetButton = new JButton(Settings.getMessage(Resources.DROID64_SETTINGS_COLOR_RESET));
		colorResetButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				if ( event.getSource()==colorResetButton ) {
					resetButtonColors(colorActiveBorderButton, colorInactiveBorderButton);
				}
			}
		});

		JPanel colorPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;

		addFields(0, Resources.DROID64_SETTINGS_COLOR_THEME, colourBox, new JPanel(), colorPanel, gbc);
		addFields(1, "", colorResetButton, new JPanel(), colorPanel, gbc);
		addFields(2, Resources.DROID64_SETTINGS_COLOR_CBM, colorFgButton, colorBgButton, colorPanel, gbc);
		addFields(3, Resources.DROID64_SETTINGS_COLOR_CPM, colorCpmFgButton, colorCpmBgButton, colorPanel, gbc);
		addFields(4, Resources.DROID64_SETTINGS_COLOR_LOCAL, colorLocalFgButton, colorLocalBgButton, colorPanel, gbc);
		addFields(5, Resources.DROID64_SETTINGS_COLOR_BORDER, colorActiveBorderButton, colorInactiveBorderButton, colorPanel, gbc);

		addToGridBag(0, 6, 0.5, 0.8, 1, gbc, colorPanel, new JPanel());
		return colorPanel;
	}

	private void resetButtonColors(JButton colorActiveBorder, JButton colorInactiveBorder) {
		colorFgButton.setForeground(Settings.DIR_FG_COLOR_C64);
		colorFgButton.setBackground(Settings.DIR_BG_COLOR_C64);
		colorBgButton.setForeground(Settings.DIR_FG_COLOR_C64);
		colorBgButton.setBackground(Settings.DIR_BG_COLOR_C64);
		colorCpmFgButton.setForeground(Settings.DIR_FG_COLOR_CPM);
		colorCpmFgButton.setBackground(Settings.DIR_BG_COLOR_CPM);
		colorCpmBgButton.setForeground(Settings.DIR_FG_COLOR_CPM);
		colorCpmBgButton.setBackground(Settings.DIR_BG_COLOR_CPM);
		colorLocalFgButton.setForeground(Settings.DIR_FG_COLOR_LOCAL);
		colorLocalFgButton.setBackground(Settings.DIR_BG_COLOR_LOCAL);
		colorLocalBgButton.setForeground(Settings.DIR_FG_COLOR_LOCAL);
		colorLocalBgButton.setBackground(Settings.DIR_BG_COLOR_LOCAL);
		colorActiveBorder.setBorder(BorderFactory.createLineBorder(Settings.ACTIVE_BORDER_COLOR));
		colorInactiveBorder.setBorder(BorderFactory.createLineBorder(Settings.INACTIVE_BORDER_COLOR));
		Settings.setDirColors(colorBgButton.getBackground(), colorFgButton.getForeground());
		Settings.setDirCpmColors(colorCpmBgButton.getBackground(), colorCpmFgButton.getForeground());
		Settings.setDirLocalColors(colorLocalBgButton.getBackground(), colorLocalFgButton.getForeground());
		Settings.setActiveBorderColor(Settings.ACTIVE_BORDER_COLOR);
		Settings.setInactiveBorderColor(Settings.INACTIVE_BORDER_COLOR);
	}

	/**
	 * Create the panel with database settings.
	 * @return JPanel
	 */
	private JPanel drawDatabasePanel(final MainPanel mainPanel) {
		JPanel dbPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;

		useJdbcCheckBox = new JCheckBox(Settings.getMessage(Resources.DROID64_SETTINGS_JDBC_USEDB));
		useJdbcCheckBox.setSelected(Settings.getUseDb());
		jdbcDriver = new JTextField(Settings.getJdbcDriver());
		jdbcUrl = new JTextField(Settings.getJdbcUrl());
		jdbcUser = new JTextField(Settings.getJdbcUser());
		jdbcPassword = new JPasswordField(Settings.getJdbcPassword());
		final JButton testConnectionButton = new JButton(Settings.getMessage(Resources.DROID64_SETTINGS_JDBC_TEST));
		status.setFont(new Font("Verdana", Font.PLAIN, status.getFont().getSize()));
		status.setEditable(false);
		maxRows = SearchDialog.getNumericField(Settings.getMaxRows(), 8);
		useJdbcCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() == useJdbcCheckBox) {
					boolean enabled = useJdbcCheckBox.isSelected();
					jdbcDriver.setEnabled(enabled);
					jdbcUrl.setEnabled(enabled);
					jdbcUser.setEnabled(enabled);
					jdbcPassword.setEnabled(enabled);
					maxRows.setEnabled(enabled);
					testConnectionButton.setEnabled(enabled);
				}
			}
		});

		testConnectionButton.setToolTipText(Settings.getMessage(Resources.DROID64_SETTINGS_JDBC_TEST_TOOLTIP));
		testConnectionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() == testConnectionButton) {
					String errStr = DaoFactoryImpl.testConnection(jdbcDriver.getText(), jdbcUrl.getText(), jdbcUser.getText(), new String(jdbcPassword.getPassword()));
					status.setText(errStr == null ? Settings.getMessage(Resources.DROID64_SETTINGS_OK) : errStr);
				}
			}
		});

		boolean jdbcEnabled = useJdbcCheckBox.isSelected();
		jdbcDriver.setEnabled(jdbcEnabled);
		jdbcUrl.setEnabled(jdbcEnabled);
		jdbcUser.setEnabled(jdbcEnabled);
		jdbcPassword.setEnabled(jdbcEnabled);
		maxRows.setEnabled(jdbcEnabled);
		testConnectionButton.setEnabled(jdbcEnabled);

		limitTypeBox = new JComboBox<>(DaoFactory.getLimitNames());
		limitTypeBox.setToolTipText(Settings.getMessage(Resources.DROID64_SETTINGS_JDBC_LIMIT_TOOLTIP));
		limitTypeBox.setEditable(false);
		limitTypeBox.setSelectedIndex(0);
		limitTypeBox.setSelectedIndex(Settings.getJdbcLimitType() < DaoFactory.getLimitNames().length ? Settings.getJdbcLimitType() : 0);

		String sqlString =
				"\nThe database feature requires access to a SQL database and a JDBC driver for the database in the class path.\n" +
						"MySQL ConnectJ ("+ Settings.getMessage(Resources.JDBC_MYSQL_URL) +") and PostgreSQL (" +
						Settings.getMessage(Resources.JDBC_POSTGRESQL_URL) + ") has been found working with DroiD64.";

		final String sql = getSqlSetupScript(mainPanel);
		final JButton viewSqlButton = new JButton(Settings.getMessage(Resources.DROID64_SETTINGS_JDBC_SQL));
		final SettingsDialog thisDialog = this;
		viewSqlButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() == viewSqlButton) {
					new TextViewDialog(thisDialog, "DroiD64","SQL database setup", sql, true, Utility.MIMETYPE_TEXT, mainPanel);
				}
			}
		});

		final JTextArea messageTextArea = new JTextArea(10,45);
		messageTextArea.setBackground(new Color(230,230,230));
		messageTextArea.setEditable(false);
		messageTextArea.setWrapStyleWord(true);
		messageTextArea.setLineWrap(true);
		messageTextArea.setText(sqlString);
		messageTextArea.setCaretPosition(0);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(viewSqlButton);
		buttonPanel.add(testConnectionButton);
		buttonPanel.add(new JPanel());

		addToGridBag(0, 0, 0.0, 0.0, 1, gbc, dbPanel, new JPanel());
		addToGridBag(1, 0, 0.5, 0.0, 1, gbc, dbPanel, useJdbcCheckBox);
		addToGridBag(2, 0, 0.0, 0.0, 1, gbc, dbPanel, new JPanel());

		addField(1, Resources.DROID64_SETTINGS_JDBC_CLASS, jdbcDriver, dbPanel, gbc);
		addField(2, Resources.DROID64_SETTINGS_JDBC_URL, jdbcUrl, dbPanel, gbc);
		addField(3, Resources.DROID64_SETTINGS_JDBC_USER, jdbcUser, dbPanel, gbc);
		addField(4, Resources.DROID64_SETTINGS_JDBC_PASS, jdbcPassword, dbPanel, gbc);
		addField(5, Resources.DROID64_SETTINGS_JDBC_ROWS, maxRows, dbPanel, gbc);
		addField(6, Resources.DROID64_SETTINGS_JDBC_LIMIT, limitTypeBox, dbPanel, gbc);

		addToGridBag(0, 7, 0.0, 0.0, 1, gbc, dbPanel, new JPanel());
		addToGridBag(1, 7, 0.5, 0.0, 2, gbc, dbPanel, buttonPanel);

		addField(8, Resources.DROID64_SETTINGS_JDBC_STATUS, status, dbPanel, gbc);

		addToGridBag(0, 9, 0.0, 0.0, 1, gbc, dbPanel, new JPanel());
		gbc.fill = GridBagConstraints.BOTH;
		addToGridBag(1, 9, 0.5, 0.9, 1, gbc, dbPanel, new JScrollPane(messageTextArea));

		return dbPanel;
	}

	/**
	 * Setup MAX_PLUGINS of panels for plugins.
	 * @param mainPanel
	 * @return JPanel[]
	 */
	private JPanel[] drawPluginPanel(final MainPanel mainPanel){
		JPanel[] pluginPanel = new JPanel[Settings.MAX_PLUGINS];
		final JButton[] browseButtons = new JButton[Settings.MAX_PLUGINS];
		ActionListener browseButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				for (int i=0; i<browseButtons.length; i++) {
					if (browseButtons[i] != null && event.getSource() == browseButtons[i]) {
						browseCommand(browseButtons[i], mainPanel, pluginCommandTextField[i]);
					}
				}
			}
		};
		for (int i = 0; i < Settings.MAX_PLUGINS; i++) {
			pluginPanel[i] = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;

			pluginLabelTextField[i] = getTextField(Resources.DROID64_SETTINGS_EXE_LABEL_TOOLTIP);
			pluginCommandTextField[i] = getTextField(Resources.DROID64_SETTINGS_EXE_CHOOSE_TOOLTIP);
			pluginArgumentTextField[i] = getTextArea(ARGUMENT_TOOLTIP);
			pluginDescriptionTextField[i] = getTextArea(Settings.getMessage(Resources.DROID64_SETTINGS_EXE_DESCR_TOOLTIP));
			forkThreadCheckBox[i] = new JCheckBox("", true);
			forkThreadCheckBox[i].setToolTipText(Settings.getMessage(Resources.DROID64_SETTINGS_EXE_FORK_TOOLTIP));
			if (i < Settings.getExternalPrograms().length && Settings.getExternalPrograms()[i] != null) {
				ExternalProgram prg = Settings.getExternalPrograms()[i];
				pluginLabelTextField[i].setText(prg.getLabel());
				pluginCommandTextField[i].setText(prg.getCommand());
				pluginArgumentTextField[i].setText(prg.getArguments());
				pluginDescriptionTextField[i].setText(prg.getDescription());
				forkThreadCheckBox[i].setSelected(prg.isForkThread());
			}

			browseButtons[i] = new JButton("...");
			browseButtons[i].addActionListener(browseButtonListener);

			JPanel cmdPanel = new JPanel(new BorderLayout());
			cmdPanel.add(pluginCommandTextField[i], BorderLayout.CENTER);
			cmdPanel.add(browseButtons[i], BorderLayout.EAST);

			addToGridBag(0, 0, 0.0, 0.0, 1, gbc, pluginPanel[i], new JLabel(Settings.getMessage(Resources.DROID64_SETTINGS_EXE_LABEL)));
			addToGridBag(1, 0, 0.5, 0.0, 1, gbc, pluginPanel[i], pluginLabelTextField[i]);
			addToGridBag(0, 1, 0.0, 0.0, 1, gbc, pluginPanel[i], new JLabel(Settings.getMessage(Resources.DROID64_SETTINGS_EXE_COMMAND)));
			addToGridBag(1, 1, 0.5, 0.0, 1, gbc, pluginPanel[i], cmdPanel);
			addToGridBag(0, 2, 0.0, 0.0, 1, gbc, pluginPanel[i], new JLabel(Settings.getMessage(Resources.DROID64_SETTINGS_EXE_ARGS)));
			addToGridBag(1, 2, 0.5, 0.0, 1, gbc, pluginPanel[i], pluginArgumentTextField[i]);
			addToGridBag(0, 3, 0.0, 0.0, 1, gbc, pluginPanel[i], new JLabel(Settings.getMessage(Resources.DROID64_SETTINGS_EXE_DESCR)));
			addToGridBag(1, 3, 0.5, 0.0, 1, gbc, pluginPanel[i], pluginDescriptionTextField[i]);
			addToGridBag(0, 4, 0.0, 0.0, 1, gbc, pluginPanel[i], new JLabel(Settings.getMessage(Resources.DROID64_SETTINGS_EXE_FORK)));
			addToGridBag(1, 4, 0.5, 0.0, 1, gbc, pluginPanel[i], forkThreadCheckBox[i]);
			addToGridBag(0, 5, 0.5, 0.9, 1, gbc, pluginPanel[i], new JPanel());
		}
		return pluginPanel;
	}

	private void browseCommand(final JButton browseButton, final MainPanel mainPanel, final JTextField textField) {
		String dir = browseButton.getText();
		if (dir!= null && !dir.isEmpty()) {
			File f = new File(dir);
			if (f.exists()) {
				dir = f.getParent();
			} else {
				dir = null;
			}
		}
		if (dir == null) {
			dir = System.getProperty("user.dir");
		}
		String cmd = FileDialogHelper.openTextFileDialog(Settings.getMessage(Resources.DROID64_SETTINGS_EXE_CHOOSE), dir, null, false, null);
		if (cmd != null && !cmd.isEmpty()) {
			if (new File(cmd).canExecute()) {
				textField.setText(cmd);
			} else {
				mainPanel.appendConsole("File '" + cmd + "' is not executeable!");
			}
		}
	}

	private JTextArea getTextArea(String toolTip) {
		JTextArea area = new JTextArea(4, 20);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setToolTipText(toolTip);
		area.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		area.setText("");
		return area;
	}

	private JTextField getTextField(String toolTipPropertyKey) {
		JTextField field = new JTextField();
		field.setToolTipText(Settings.getMessage(toolTipPropertyKey));
		field.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		field.setText("");
		return field;
	}

	private static String getSqlSetupScript(final MainPanel mainPanel) {
		if (sqlSetupScript == null) {
			try (InputStream in = SettingsDialog.class.getResourceAsStream("/setup_database.sql");
					BufferedReader input = new BufferedReader(new InputStreamReader(in)))
			{
				StringBuilder buf = new StringBuilder();
				for (String line = input.readLine(); line != null; line = input.readLine()) {
					buf.append(line);
					buf.append("\n");
				}
				sqlSetupScript = buf.toString();
			} catch (IOException e) {	//NOSONAR
				mainPanel.appendConsole("Failed to find SQL setup script.\n"+e.getMessage());
			}
		}
		return sqlSetupScript;
	}

	/**
	 * Open a directory browser dialog.
	 * @param directory String with default directory
	 * @return String with selected directory, or null if nothing was selected.
	 */
	private String openImgDirDialog(String directory) {
		JFileChooser chooser = new JFileChooser(directory);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile()+"";
		}
		return null;
	}

	/**
	 * Wrapper to add a JComponent to a GridBagConstraints.
	 * @param x column
	 * @param y row
	 * @param weightx column weight
	 * @param weighty row weight
	 * @param gbc GridBagConstaints
	 * @param parent Parent JComponent to which to add a component
	 * @param component the new component to add
	 */
	private void addToGridBag(int x, int y, double weightx, double weighty, int wdt, GridBagConstraints gbc, JComponent parent, JComponent component) {
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = wdt;
		parent.add(component, gbc);
	}

}
