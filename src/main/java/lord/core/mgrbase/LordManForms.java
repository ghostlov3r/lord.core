package lord.core.mgrbase;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.core.gamer.Gamer;
import lord.core.mgrbase.manager.LordMan;

/**
 * Формы, относящиеся к определенному менеджеру
 * @param <Mgr> Тип менеджера
 */
@Getter
@RequiredArgsConstructor
public abstract class LordManForms<Mgr extends LordMan> {
	
	private final Mgr manager;
	
	// todo кеш статичных форм
	
	public abstract void mainForm (Gamer gamer);
	
}
