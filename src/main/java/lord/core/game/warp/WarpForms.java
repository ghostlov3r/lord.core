package lord.core.game.warp;

import dev.ghostlov3r.beengine.form.CustomForm;
import dev.ghostlov3r.beengine.form.Form;
import dev.ghostlov3r.beengine.form.SimpleForm;
import lombok.RequiredArgsConstructor;
import lord.core.gamer.Gamer;

@RequiredArgsConstructor
public class WarpForms {
	
	private final WarpMan manager;
	
	/**
	 * Главная форма варпов
	 */
	public void main (Gamer gamer) {
		SimpleForm form = Form.simple()
						 .button("Выбрать точку телепортации", player -> chooseWarp((Gamer) player))
						 .button("Создать точку телепортации", player -> createWarp((Gamer) player))
						 .button("Мои точки телепортации", player -> createWarp((Gamer) player));
		gamer.sendForm(form);
	}
	
	/**
	 * Форма выбора варпа
	 */
	public void chooseWarp (Gamer gamer) {
		SimpleForm form = Form.simple();
		manager.values().forEach(warp -> {
			if (warp.isOpened()) {
				form.button(warp.key());
			}
		});
		form.onSubmit((player, resp) -> {
			manager.teleportOrMess(player, resp.getButton().getText());
		});
		gamer.sendForm(form);
	}
	
	public void createWarp (Gamer gamer) {
		CustomForm form = Form.custom()
						 .input("Укажите имя точки телепортации")
						 .label("Если данная опция выключена, телепортироваться смогут только игроки из разрешенного списка")
						 .toggle("Разрешать всем телепортироваться")
						 .label("Учтите, что точка будет создана там, где вы сейчас находитесь");
		form.onSubmit((player, resp) -> {
			String name = resp.getInput(1).trim();
			if (name.isEmpty()) {
				player.sendMessage("Вы не указали имя точки телепортации");
				return;
			}
			Warp warp = manager.create(name, player, resp.getToggle(3));
			manager.add(warp);
			manager.onWarpCreate().accept(player, warp);
			warpCreated((Gamer) player, warp);
		});
		gamer.sendForm(form);
	}
	
	public void warpCreated (Gamer gamer, Warp warp) {
		SimpleForm form = Form.simple()
						 .content("Точка телепортации " + warp.key() +
						   " успешно создана там, где вы стоите.\n Чтобы телепортироваться сюда, используйте меню /warp или команду /warp " + warp.key())
						 ;
		gamer.sendForm(form);
	}
	
	public void myWarpsList (Gamer gamer) {
		var names = manager.playerWarpNames().apply(gamer);
		var form = Form.simple()
						 .content("Здесь находятся все точки телепортации, которые созданы вами");
		names.forEach(form::button);
		form.onSubmit((player, resp) -> {
		
		});
	}
	
	public void myWarpPage (Gamer gamer, String warpName) {
		Warp warp = manager.get(warpName);
		if (warp == null) {
			gamer.sendMessage("Кажется, с вашей точкой телепортации что-то не так. Сообщите администрации.");
			return;
		}
		myWarpPage(gamer, warp);
	}
	
	public void myWarpPage (Gamer gamer, Warp warp) {
		SimpleForm form = Form.simple()
						 .content("Страница точки телепортации " + warp.key() + "\n Разрешено всем: " + (warp.isOpened() ? "да" : "нет") +
						   "\nВ разрешенном списке: " + warp.getWhiteList().size() + " игроков")
						 .button("Телепортироваться сюда", warp::teleport)
						 .button("Открыть разрешенный список", player -> warpWhiteList(gamer, warp))
						 .button("Разрешить/запретить другим", player -> {
					   		warp.opened = !warp.opened;
					   		myWarpPage(gamer, warp);
					   })
						 .button("Удалить эту точку телепортации", player -> warpDeleteConfirm(gamer, warp))
						 ;
		gamer.sendForm(form);
	}
	
	public void warpWhiteList (Gamer gamer, Warp warp) {
		// todo
	}
	
	public void warpWhiteListAdd (Gamer gamer, Warp warp) {
	
	}
	
	public void warpDeleteConfirm (Gamer gamer, Warp warp) {
		// при удалении юзать лямбду из менеджера чтобы удалить из списка
	}
	
	public void fromWarpWhiteRemove (Gamer gamer, Warp warp, String name) {
	
	}
	
}
