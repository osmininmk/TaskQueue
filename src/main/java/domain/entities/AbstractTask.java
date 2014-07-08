package domain.entities;

import domain.Action;

import javax.persistence.*;
import java.util.Date;
/*
 * Base Class represented Task
 */
@MappedSuperclass
abstract public class AbstractTask {
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateAdded;

	@Column(nullable = false)
	private int a;

	@Column(nullable = false)
	private int b;

	@Column(nullable = false)
	private int c;

	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private Action action;

	public AbstractTask() {}

	public AbstractTask(ActiveTask activeTask) {
		setA(activeTask.getA());
		setB(activeTask.getB());
		setC(activeTask.getC());
		setAction(activeTask.getAction());
		setDateAdded(activeTask.getDateAdded());
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
