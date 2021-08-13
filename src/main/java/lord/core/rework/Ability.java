package lord.core.rework;

import cn.nukkit.Player;
import lord.core.gamer.Gamer;

public class Ability {
	
	public static final int REGION_1 = 1;
	public static final int REGION_2 = 1;
	public static final int REGION_3 = 1;
	public static final int REGION_4 = 1;
	
	public static final int REGION_MEMBERS_1 = 1;
	public static final int REGION_MEMBERS_2 = 1;
	
	public static final int HOME_1 = 1;
	public static final int HOME_2 = 1;
	public static final int HOME_3 = 1;
	
	public static final int TELEPORT_REQUEST = 1;
	public static final int TELEPORT_REQUEST_LESS_WAIT = 1;
	public static final int TELEPORT_REQUEST_LESS_WAIT_2 = 1;
	public static final int TELEPORT_REQUEST_FREE = 1;
	
	public static final int TELEPORT_LESS_DELAY = 1;
	public static final int TELEPORT_NO_DELAY = 1;
	
	public static final int WARP_1 = 1;
	
	public static final int KIT_START = 1;
	public static final int KIT_BONUS = 1;
	public static final int KIT_EXTRA = 1;
	
	public static final int SHOP_BUY = 1;
	public static final int SHOP_SELL = 1;
	
	public static final int USE_BANK = 1;
	public static final int USE_CHAT = 1;
	public static final int USE_JACK = 1;
	
	public static final int GO_SPAWN = 1;
	
	private static Ability[] levels = new Ability[25];
	public static int max = 0;
	
	static {
		init();
	}
	
	public static void award (Gamer gamer, int exp)
	{
		if (gamer.data.rank >= max) {
			gamer.data.rankExp += exp;
			return;
		}
		
		int expected = getExpectedExp(gamer.data.rank);
		int sum = gamer.data.rankExp + exp;
		
		if (sum >= expected) {
			//
			Ability ability = levels[gamer.data.rank];
			Player player = gamer.getPlayer();
			
			player.sendMessage("Ваш уровень повышен!");
			
			int money = ability.money;
			if (money > 0) {
				gamer.addMoney(money);
				player.sendMessage("Награда получена: " + money + " Koins");
			}
			levels[gamer.data.rank].onGet();
			
			award(gamer, sum - expected);
		}
		else {
			gamer.data.rankExp += exp;
		}
	}
	
	// сколько опыта нужно
	// чтобы получить следущий уровень
	public static int getExpectedExp (int currentLevel)
	{
		return levels[currentLevel+1].needExp;
	}
	
	private static void register (int level, int needExp, int money)
	{
		Ability ability = new Ability(level, needExp, money);
		levels[ability.level] = ability;
		max++;
	}
	
	
	private Ability (int level, int needExp, int money)
	{
		this.level = level;
		this.needExp = needExp;
		this.money = money;
	}
	
	private int level;
	private int needExp;
	private int money;
	
	private void onGet () {}
	
	
	private static void init ()
	{
		register(1, 50, 50);
		register(2, 100, 50);
		register(3, 150, 50);
		register(4, 200, 50);
		register(5, 250, 50);
		register(6, 300, 50);
		register(7, 350, 50);
		register(8, 400, 50);
		register(9, 450, 50);
		register(10, 500, 50);
		register(11, 550, 50);
		register(12, 600, 50);
		register(13, 650, 50);
		register(14, 700, 50);
		register(15, 750, 50);
		register(16, 800, 50);
		register(17, 850, 50);
		register(18, 900, 50);
		register(19, 950, 50);
		register(20, 1000, 50);
		register(21, 1100, 50);
		register(22, 1200, 50);
		register(23, 1300, 50);
		register(24, 1400, 50);
		register(25, 1500, 50);
	}
}
