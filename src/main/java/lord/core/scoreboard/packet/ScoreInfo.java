package lord.core.scoreboard.packet;

public class ScoreInfo {
	
	public static final int TYPE_PLAYER = 1;
	public static final int TYPE_ENTITY = 2;
	public static final int TYPE_FAKE_PLAYER = 3;
	
	public long scoreboardId;
	
	public String objectiveName;
	
	public int score;
	
	public byte addType;
	
	public long entityId;
	
	public String fakePlayer;
	
	public ScoreInfo () {
	}
	
	public ScoreInfo (int number, String objectiveName, int scoreboardId) {
		this(number, objectiveName, scoreboardId, "");
	}
	
	public ScoreInfo (int number, String objectiveName, int scoreboardId, String text) {
		this.score = number;
		this.objectiveName = objectiveName;
		this.scoreboardId = scoreboardId;
		this.addType = TYPE_FAKE_PLAYER;
		this.fakePlayer = text;
	}
}