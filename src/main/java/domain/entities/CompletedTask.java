package domain.entities;

import javax.persistence.*;

/*
 * Class represented Tasks that has been calculated
 */
@Entity
@Table(name = "history")
public class CompletedTask extends AbstractTask {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

	@Column(nullable = false)
    private int res;

	public CompletedTask() {}

	public CompletedTask(ActiveTask activeTask) {
		super(activeTask);
	}

	public void calculate() {
		res = getAction().calculate(getA(), getB(), getC());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
