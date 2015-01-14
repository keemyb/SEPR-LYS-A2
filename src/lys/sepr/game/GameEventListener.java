package lys.sepr.game;

public interface GameEventListener {
	public void gameWon();
	public void contractCompleted();
	public void contractFailed();
	public void contractChoose();
	public void turnBegin();
	public void update();
	
}
