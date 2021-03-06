package droid64.gui;

import javax.swing.table.AbstractTableModel;

import droid64.d64.Utility;

/**
 * Table model used by HexViewFrame.
 * @author Henrik
 * @see HexViewDialog
 */
public class HexTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private int bytesPerRow = 16;
	private byte[] data = null;
	private int length;

	/**
	 * Constructor
	 * @param data the data to render
	 * @param length the length of the data
	 */
	public HexTableModel(byte[] data, int length) {
		this.data = data;
		if (data == null) {
			this.length = 0;
		} else {
			this.length = length < data.length ? length : data.length;
		}
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Address";
		} else if (column <= bytesPerRow) {
			return Integer.toHexString(column - 1).toUpperCase();
		} else if (column == bytesPerRow + 1) {
			return "ASCII";
		} else {
			return "";
		}
	}

	@Override
	public int getRowCount() {
		if (data==null || data.length <1) {
			return 0;
		} else {
			return (length + bytesPerRow  - 1) / bytesPerRow ;
		}
	}

	@Override
	public int getColumnCount() {
		return bytesPerRow + 2;
	}

	@Override
	public String getValueAt(int rowIndex, int columnIndex) {
		if (data == null) {
			return "";
		} else {
			if (columnIndex == 0) {
				return Utility.getIntHexString(rowIndex * bytesPerRow);
			} else if (columnIndex == bytesPerRow + 1) {
				return getDumpRowString(rowIndex);
			} else {
				int addr = (rowIndex * bytesPerRow) + columnIndex -1;
				if (addr < data.length && addr < length) {
					return Utility.getByteString(data[addr]);
				} else {
					return "";
				}
			}
		}
	}

	public Integer getByteAt(int rowIndex, int columnIndex) {
		if (data == null || columnIndex == 0 || columnIndex == bytesPerRow + 1) {
			return null;
		} else {
			int addr = (rowIndex * bytesPerRow) + columnIndex -1;
			if (addr < data.length && addr < length) {
				return Integer.valueOf(data[addr] & 0xff);
			} else {
				return null;
			}
		}
	}

	/**
	 * Get an ASCII dump of the bytes displayed at rowIndex in data.
	 * @param rowIndex int
	 * @return String
	 */
	private String getDumpRowString(int rowIndex) {
		int start = rowIndex * bytesPerRow;
		if (data == null) {
			return "x";
		}
		StringBuilder buf = new StringBuilder();
		for (int i=start; i < start + bytesPerRow; i++) {
			if (i < data.length) {
				byte b = data[i];
				if (b < 0x20 || b > 0x7e) {
					buf.append('.');
				} else {
					buf.append((char)b);
				}
			} else {
				buf.append(' ');
			}
		}
		return buf.toString();
	}

}
