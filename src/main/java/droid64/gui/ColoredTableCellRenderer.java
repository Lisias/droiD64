package droid64.gui;

/**<pre style='font-family:sans-serif;'>
 * Created on 21.06.2004
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
 *</pre>
 * @author wolf
 */
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

public class ColoredTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	private static final Color USED_COLOR = new Color(255,200,200);	// red
	private static final Color FREE_COLOR = new Color(200,255,200);	// green
	private static final Color INVALID_COLOR = new Color(100,100,100);	// grey
	private static final String USED = "x";
	private static final String FREE = "-";
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setHorizontalAlignment(SwingConstants.CENTER);
		
		//Label erzeugen
		JLabel label = new JLabel(value.toString());
		label.setOpaque(true);
		Border b = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		label.setBorder(b);
		label.setFont(table.getFont());
		label.setForeground(table.getForeground());
		label.setBackground(table.getBackground());
		//Angezeigte Spalte in Modellspalte umwandeln
		column = table.convertColumnIndexToModel(column);    
//      if (column == 1) {
//        int numpages = Integer.parseInt((String)value);
//        if (numpages >= 250) {
//          label.setBackground(Color.red);
//        } else if (numpages >= 200) {
//          label.setBackground(Color.orange);
//        } else {
//          label.setBackground(Color.yellow);
//        }
//      }
		
		if (value instanceof String) {
			if (USED.equals(value)) {
				label.setBackground(USED_COLOR);
			} else if (FREE.equals(value)) {
				label.setBackground(FREE_COLOR);
			} else if (column > 0) {
				label.setBackground(INVALID_COLOR);				
			}
			value = " ";
		}
		return label;
	}
}
