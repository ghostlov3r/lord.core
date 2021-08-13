package lord.core.minigame;

import lombok.Getter;
import lombok.Setter;
import lord.core.mgrbase.LordPlugin;
import lord.core.mgrbase.manager.LordManFC;
import lord.core.minigame.arena.ArenaState;
import lord.core.minigame.arena.LordArena;
import lord.core.minigame.arena.TeamColors;
import lord.core.util.file.AdvFolder;

import java.util.function.Consumer;

/**
 * Менеджер арен для миниигр.
 *
 * @param <Arena> Тип арены
 * @param <Plug> Тип плагина-владельца
 * @param <MGConfig> Тип конфигурации
 * @author ghostlov3r
 */
@Getter
public abstract class LordArenaMan<Arena extends LordArena, Plug extends LordPlugin, MGConfig extends MiniGameConfig>
	extends LordManFC<Arena, Plug, MGConfig> {
	
	@Setter private String gameName;
	
	@Setter private String prefix;
	
	private AdvFolder copiesFolder;
	
	private AdvFolder worldsFolder;
	
	private TeamColors teamColors;
	
	public LordArenaMan () {
		super();
		init();
	}
	
	public LordArenaMan (String folder) {
		super(folder);
		init();
	}
	
	public LordArenaMan (AdvFolder folder) {
		super(folder);
		init();
	}
	
	private void init () {
		gameName = getCore().config().getBoldName();
		prefix = getCore().config().getPrefix();
		
		copiesFolder = getFolder().mkdirChild("copies");
		worldsFolder = AdvFolder.get(getCore().getServer().getDataPath()).getChild("worlds");
		
		teamColors = new TeamColors(getFolder().getChild("colors"));
		ArenaState.init(this);
		
		prettyJson();
		loadAll(); // сделать проверку перед загрузкой
		checkArenas();
		getLogger().loaded();
	}
	
	private void checkArenas () {
		// Сделать проверку на адекватность загруженных данных
	}
	
	/* ===================================================================================== */
	
	
	
	/* ===================================================================================== */
	
	/** */
	protected Consumer<Arena> onGamerVsGamer;
	
	/** */
	protected Consumer<Arena> onGamerVsEntity;
	
	/** */
	protected Consumer<Arena> onGamerDamaged;
	
	/** */
	protected Consumer<Arena> onGamerJustLastDamage;
	
	/** */
	protected Consumer<Arena> onGamerVsGamerLastDamage;
	
	/** */
	protected Consumer<Arena> onGamerVsEntityLastDamage;
	
	/* ===================================================================================== */
	
	/** */
	protected Consumer<Arena> onGamerInteractInGame;
	
	/** */
	protected Consumer<Arena> onGamerInteractWhileWait;
	
	/** */
	protected Consumer<Arena> onGamerBlockBreakInGame;
	
	/** */
	protected Consumer<Arena> onGamerBlockPlaceInGame;
	
	/** */
	protected Consumer<Arena> onGamerDropItemInGame;
	
}
