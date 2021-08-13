package lord.core.gamer;

import cn.nukkit.network.SourceInterface;
import lombok.Getter;
import lombok.experimental.Accessors;
import lord.core.LordCore;
import lord.core.mgrbase.LordPlugin;
import lord.core.mgrbase.manager.LordManF;
import lord.core.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static lord.core.gamer.GamerData.NOT_MUTED;

/**
 * Этот класс служит для загрузки
 * и временного кеширования обьектов GamerData,
 * а также для копии мапы с игроками,
 * т.к. в накките это реализовано уебищьно
 *
 * @author ghostlov3r
 */
@Getter @Accessors(fluent = true)
public abstract class GamerMan<GData extends GamerData, Plug extends LordPlugin, G extends Gamer>
	extends LordManF<GData, Plug> {
	
	/** Класс игрока */
	private Class<G> playerClass;
	
	@SuppressWarnings("unchecked")
	public GamerMan () {
		super("gamers");
		this.playerClass = (Class<G>) Util.superGenericClass(this, 3);
		
		getLogger().enabled();
	}
	
	/** Создает новые данные игрока */
	@SuppressWarnings("unchecked")
	public GData newDataFor (G gamer) {
		GData data = Util.newInstance(this.getEntryClass());
		
		if (data != null) {
			data.mutedUntil = NOT_MUTED;
			data.finup(gamer.getName(), this);
		}
		
		return data;
	}
	
	/* ======================================================================================= */
	
	/*
	 * Эти мапы теряют игроков onQuit
	 * -> gamers    получает игрока onSuccessAuth
	 * -> allGamers получает игрока onJoin
	 */
	
	/** Мапа авторизованных игроков */
	private Map<String, G> gamers = new HashMap<>();
	
	/** Мапа всех игроков */
	private Map<String, G> allGamers = new HashMap<>();
	
	public void addGamer (G gamer) {
		allGamers.put(gamer.getName(), gamer);
	}
	
	public void addAuthorizedGamer (G gamer) {
		gamers.put(gamer.getName(), gamer);
	}
	
	@Nullable
	public G getGamer (String name) {
		return allGamers.get(name);
	}
	
	/* ======================================================================================= */
	
	/* ================================================================================= */
	/* ================================================================================= */
	/* ================================================================================= */
	
	private static class TestGamer extends Gamer<TestData, TestMgr> {
		public TestGamer (SourceInterface interfaz, Long clientID, String ip, int port) {
			super(interfaz, clientID, ip, port);
		}
	}
	
	private static class TestData extends GamerData<TestMgr, TestGamer> {
	
	}
	
	public static class TestMgr extends GamerMan<TestData, LordCore, TestGamer> {
		public TestMgr () {
			super();
		}
	}
	
	public static class TestMgr2 extends GamerMan<GamerData, LordPlugin, Gamer> {
		public TestMgr2 () {
			super();
		}
	}

}
