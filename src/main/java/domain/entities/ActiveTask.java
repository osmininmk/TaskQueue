package domain.entities;

import domain.Action;

import javax.persistence.*;
import java.util.Date;
import java.util.Random;
/*
 * Class represented Active Task
 */
@Entity
@Table(name = "Queue")
public class ActiveTask extends AbstractTask {
    private static Random rn = new Random();

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Column(nullable = false)
    private int processId;

	public ActiveTask() {
		setDateAdded(new Date());
		setA(getRandomValue());
		setB(getRandomValue());
		setC(getRandomValue());
		setAction(Action.values()[rn.nextInt(Action.values().length)]);
	}

    private int getRandomValue() {
        return rn.nextInt(201) - 100;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
