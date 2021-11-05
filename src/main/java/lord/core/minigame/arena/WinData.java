package lord.core.minigame.arena;

public class WinData<TArena extends Arena, TTeam extends Team> {

	private final TArena arena;

	private final TTeam winnerTeam;

	public WinData(TArena arena, TTeam winnerTeam) {
		this.arena = arena;
		this.winnerTeam = winnerTeam;
	}
}
