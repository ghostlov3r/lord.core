package lord.core.minigame;

import dev.ghostlov3r.beengine.block.utils.DyeColor;
import dev.ghostlov3r.beengine.form.CustomForm;
import dev.ghostlov3r.beengine.form.Form;
import dev.ghostlov3r.beengine.form.ModalForm;
import dev.ghostlov3r.beengine.form.SimpleForm;
import dev.ghostlov3r.beengine.item.Items;
import dev.ghostlov3r.beengine.utils.TextFormat;
import dev.ghostlov3r.beengine.world.World;
import dev.ghostlov3r.beengine.world.WorldManager;
import lord.core.minigame.data.GameMap;
import lord.core.minigame.data.MapTeam;
import lord.core.minigame.data.WeakLocation;

import java.util.ArrayList;
import java.util.List;

public class Wizard {

	protected MGGamer gamer;
	protected GameMap map;

	public Wizard (MGGamer creator) {
		gamer = creator;
		map = gamer.manager.instantiateMap(gamer.world().folderName());
		continueCreateMap();
	}

	protected void continueCreateMap() {
		gamer.inventory().remove(Items.STICK());

		gamer.inventory().setItem(0, Items.SUGAR().onInteract((g, b) -> {
			SimpleForm form = Form.simple();

			if (!map.teams.isEmpty()) {
				form.button("Показать команды ("+map.teams.size()+" шт.)", __ -> {
					SimpleForm teamsForm = Form.simple();
					for (int i = 0; i < map.teams.size(); i++) {
						MapTeam team = map.teams.get(i);
						DyeColor color = Colors.COLORS.get(i);
						teamsForm.button(Colors.asFormat(color) + color.name(), ___ -> {
							SimpleForm teamForm = Form.simple();

							List<WeakLocation> locations = team.locations();
							for (int j = 0; j < locations.size(); j++) {
								WeakLocation loc = locations.get(j);
								form.button("Спаун-локация #"+j, ____ -> {
									SimpleForm locForm = Form.simple();
									locForm.button("Удалить", _____ -> {
										team.locations().remove(loc);
										gamer.sendMessage("Локация удалена, теперь вы в редакторе команды "+Colors.asFormat(color)+color);
										editTeam(team);
									});
									locForm.button("Телепорт", _____ -> {
										gamer.teleport(loc.asLocation(gamer.world()));
										gamer.sendMessage("Телепортирован на локацию #"+locations.indexOf(loc)+" команды "+Colors.asFormat(color)+color);
									});
									gamer.sendForm(locForm);
								});
							}

							gamer.sendForm(teamForm);
						});
					}
					gamer.sendForm(teamsForm);
				});
			}

			if (map.teams.size() < Colors.COLORS.size()) {
				form.button("Добавить команду", __ -> {
					editTeam(null);
				});
			}

			arenaCreationWindow(form);

			form.button("Переименовать ("+map.displayName+")", __ -> {
				CustomForm renameForm = Form.custom();
				renameForm.input("Новое имя", "", map.displayName);
				renameForm.onSubmit((___, resp) -> {
					String newName = resp.getInput(0);
					if (!newName.isBlank()) {
						map.displayName = newName;
					}
				});
				gamer.sendForm(renameForm);
			});
			form.button("Закончить", __ -> {
				List<String> cannotReasons = new ArrayList<>();
				addUnableFinishArenaCreationReasons(cannotReasons);
				if (!cannotReasons.isEmpty()) {
					ModalForm cancelForm = Form.modal();
					cancelForm.content(TextFormat.RED+"Создание арены будет отменено, так как: \n- "+String.join("\n- ", cannotReasons));
					cancelForm.button1(TextFormat.RED+"Да, отменить");
					cancelForm.button2("Продолжить создание");
					cancelForm.onSubmit((___, cancel) -> {
						if (cancel) {
							gamer.inventory().remove(Items.SUGAR());
							gamer.sendMessage(TextFormat.RED+"Создание арены было отменено");
						}
					});
					gamer.sendForm(cancelForm);
				}
				else {
					gamer.inventory().remove(Items.SUGAR());
					gamer.manager.maps().put(map.key(), map);
					map.save();
					World world = gamer.world();
					gamer.teleport(WorldManager.get().defaultWorld().getSpawnPosition(), () -> {
						gamer.sendMessage(TextFormat.GREEN+"Арена успешно создана и готова к игре!");
						WorldManager.get().unloadWorld(world);
						gamer.manager.updateTypes();
					});
				}
			});
			gamer.sendForm(form);
		}));
	}

	protected void arenaCreationWindow (SimpleForm form) {
		// NOOP
	}

	protected void addUnableFinishArenaCreationReasons (List<String> reasons) {
		if (map.teams.size() < 2) {
			reasons.add("команд должно быть минимум 2");
		}
	}

	protected void editTeam (MapTeam t) {
		MapTeam team;
		boolean isNewTeam = t == null;
		if (isNewTeam) {
			team = map.instantiateTeam();
		} else {
			team = t;
			map.teams.remove(t);
		}

		gamer.inventory().remove(Items.SUGAR());
		gamer.inventory().setItem(0, Items.STICK().onInteract((g, b) -> {
			SimpleForm form = Form.simple();
			if (team.locations().size() < map.teams.get(0).locations().size()) {
				form.button("Добавить спаун-позицию здесь", __ -> {
					team.locations().add(new WeakLocation(g.x, g.y + 1, g.z, g.yaw, g.pitch));
					gamer.sendMessage("Отлично, позиция отмечена!");
				});
			}

			teamCreationWindow(form, team);

			if (canFinishTeamCreation(team)) {
				form.button("Закончить с этой командой", __ -> {
					map.teams.add(team);
					gamer.sendMessage("Отлично, команда "+(isNewTeam ? "добавлена" : "отредактирована")+"!");
					continueCreateMap();
				});
			}
			gamer.sendForm(form);
		}));
	}

	protected void teamCreationWindow (SimpleForm form, MapTeam team) {
		// NOOP
	}

	protected boolean canFinishTeamCreation(MapTeam team) {
		return (map.teams.isEmpty() && !team.locations().isEmpty())

				|| (!map.teams.isEmpty() && team.locations().size() == map.teams.get(0).locations().size());
	}
}
