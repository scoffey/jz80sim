package ar.edu.itba.it.obc.jz80.view;

import javax.swing.table.AbstractTableModel;

import ar.edu.itba.it.obc.jz80.api.Memory;

public class JZ80MemoryTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Memory memory;

	public JZ80MemoryTableModel(Memory m) {
		memory = m;
	}

	public int getColumnCount() {
		return 18;
	}

	public int getRowCount() {
		return memory.getSize() / 16;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return String.format("%04X", rowIndex * 16);
		}
		if (columnIndex == 17) {
			byte[] array = memory.readByteArrayAt(rowIndex * 16, 16);
			char[] characters = new char[16];
			for (int i = 0; i < 16; i++) {
				characters[i] = (char) array[i];
				if (Character.isISOControl(characters[i]))
					characters[i] = '\u25A1'; // white square
			}
			return String.valueOf(characters);
		}
		int value = memory.readByteAt(rowIndex * 16 + columnIndex - 1);
		return String.format("%02X", value & 0xFF);
	}

	public String getColumnName(int column) {
		if (column < 1 || column >= 17) {
			return "";
		}
		return Integer.toHexString(column - 1).toUpperCase();
	}

	public int findColumn(String columnName) {
		if (columnName == "") {
			return 17;
		}
		try {
			return Integer.parseInt(columnName, 16);
		} catch (Exception e) {
			return -1;
		}
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex > 0 && columnIndex < 17);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		byte value;
		if (!isCellEditable(rowIndex, columnIndex)) {
			return;
		}
		try {
			int v = Integer.parseInt(aValue.toString(), 16);
			value = (byte) (((v & 0xF0) >> 4) * 16 + (v & 0x0F));
		} catch (Exception e) {
			value = 0;
		}
		memory.writeByteAt(rowIndex * 16 + columnIndex - 1, value);
		super.setValueAt(aValue, rowIndex, columnIndex);
	}

}
