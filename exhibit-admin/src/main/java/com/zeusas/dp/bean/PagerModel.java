package com.zeusas.dp.bean;

import java.io.Serializable;
import java.util.List;

public class PagerModel<T> implements Serializable {
	/** serialVersionUID */
	private static final long serialVersionUID = -1289669936363867894L;
	private int total;
	private List<T> rows;

	public int getTotal() {
		return this.total;
	}

	public void setTotal(int Total) {
		this.total = Total;
	}

	public List<T> getRows() {
		return this.rows;
	}

	public void setRows(List<T> Rows) {
		this.rows = Rows;
	}
}
