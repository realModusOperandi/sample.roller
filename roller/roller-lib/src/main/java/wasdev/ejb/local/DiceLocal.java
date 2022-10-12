package wasdev.ejb.local;

import javax.ejb.Local;

@Local
public interface DiceLocal {
	public String hello();
	public int rollDice(int sides, int quantity);
}