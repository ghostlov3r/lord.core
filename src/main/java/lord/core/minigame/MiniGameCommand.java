package lord.core.minigame;

import dev.ghostlov3r.beengine.Beengine;
import dev.ghostlov3r.beengine.block.blocks.BlockSign;
import dev.ghostlov3r.beengine.form.CustomForm;
import dev.ghostlov3r.beengine.form.Form;
import dev.ghostlov3r.beengine.form.ModalForm;
import dev.ghostlov3r.beengine.form.SimpleForm;
import dev.ghostlov3r.beengine.item.Items;
import dev.ghostlov3r.beengine.scheduler.Scheduler;
import dev.ghostlov3r.beengine.utils.TextFormat;
import dev.ghostlov3r.beengine.world.World;
import dev.ghostlov3r.beengine.world.WorldManager;
import dev.ghostlov3r.common.DiskEntry;
import dev.ghostlov3r.common.IntHolder;
import lombok.SneakyThrows;
import lord.core.LordCore;
import lord.core.gamer.Gamer;
import lord.core.minigame.arena.Arena;
import lord.core.minigame.data.ArenaType;
import lord.core.minigame.data.GameMap;
import lord.core.util.LordCommand;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

@SuppressWarnings("rawtypes, unchecked")

public class MiniGameCommand extends LordCommand {

	MiniGame game;

	public MiniGameCommand(MiniGame game) {
		super("mg");
		this.game = game;
	}

	@Override
	public void execute(Gamer gamer, String[] args) {
		SimpleForm form = Form.simple();
		form.title(LordCore.instance().config().getBoldName());
		form.button("Типы", p -> {
			showTypes((MGGamer) gamer);
		});
		form.button("Карты", p -> {
			showMaps((MGGamer) gamer);
		});
		form.button("Новая карта", p -> {
			newMap((MGGamer) gamer);
		});
		form.button("Арены", p -> {
			showArenas((MGGamer) gamer);
		});
		gamer.sendForm(form);
	}

	private void showTypes (MGGamer gamer) {
		SimpleForm form = Form.simple();

		game.arenaTypes().values().forEach(type -> {
			form.button(type.teamSlots() + "x" + type.teamCount(), p -> {
				SimpleForm typePage = Form.simple();
				typePage.title(type.teamSlots() + "x" + type.teamCount());
				typePage.content((type.teamSlots() == 1
						? "Соло "+type.teamSlots()+" игроков"
						: (
							type.teamCount() + " команд по "+type.teamSlots()+" игроков\n" +
							"Цвета: "+String.join(", ",
								type.colors().stream().map(color ->
									Colors.asFormat(color)+color.name()+ TextFormat.RESET).toList())
						)) + "\n\n" +
								(type.maps().isEmpty() ?
										TextFormat.RED+"Нет ни одной карты этого типа"
										: TextFormat.GREEN+"Карты этого типа "+type.maps().size()+"шт.:")
						);
				injectMapButtons(type, typePage);
				gamer.sendForm(typePage);
			});
		});

		gamer.sendForm(form);
	}

	private void showMaps (MGGamer gamer) {
		SimpleForm form = Form.simple();
		form.content((game.maps().isEmpty() ?
				TextFormat.RED+"Нет ни одной карты "
				: TextFormat.GREEN+"Карты "+game.maps().size()+"шт.:"));
		injectMapButtons(null, form);
		gamer.sendForm(form);
	}

	private void injectMapButtons (ArenaType type, SimpleForm form) {
		(type != null
				? type.maps()
				: game.maps().values())
				.forEach(map -> {
					form.button(map.key() + " | " + map.worldName + " | "+type.teamSlots() + "x" + type.teamCount(), p -> {

						SimpleForm mapPage = Form.simple();
						mapPage.button("Удалить", pp -> {
							ModalForm confirmDeletePage = Form.modal();
							confirmDeletePage.button1(TextFormat.RED+"Удалить безвозвратно");
							confirmDeletePage.button2(TextFormat.GREEN+"Не удалять");
							confirmDeletePage.onSubmit((ppp, confirmed) -> {
								if (confirmed) {
									ppp.sendMessage("Не реализовано");
								}
								else {
									ppp.sendForm(mapPage);
								}
							});
							pp.sendForm(confirmDeletePage);
						});
						mapPage.button("Телепорт", pp -> {
							WorldManager.get().loadWorld(map.worldName).onResolve(promise -> {
								pp.teleport(promise.result().getSpawnPosition());
							});
						});
						p.sendForm(form);
					});
				});
	}

	@SneakyThrows
	private void newMap (MGGamer gamer) {
		SimpleForm form = Form.simple();
		World gamerWorld = gamer.world();

		Files.list(Beengine.WORLDS_PATH)
				.map(Path::getFileName)
				.map(Objects::toString)
				.forEach(worldName -> {
					boolean add = true;

					if (worldName.equals(WorldManager.get().defaultWorld().folderName()) || worldName.equals(game.waitLobby().folderName())) {
						add = false;
					}
					else {
						for (GameMap map : game.maps().values()) {
							if (map.worldName.equals(worldName)) {
								add = false;
								break;
							}
						}
					}

					if (add) {
						form.button(worldName + (worldName.equals(gamerWorld.folderName()) ? " (Вы сейчас в этом мире)" : ""), p -> {
							WorldManager.get().loadWorld(worldName).onResolve(promise -> {
								p.teleport(promise.result().getSpawnPosition(), () -> {
									if (gamerWorld != promise.result()) {
										Scheduler.delay(50, () -> {
											gamer.manager.startWizard(gamer);
										});
									}
									else {
										gamer.manager.startWizard(gamer);
									}
								});
							});
						});
					}
				});

		gamer.sendForm(form);
	}

	private void showArenas (MGGamer gamer) {
		SimpleForm form = Form.simple();

		form.button(TextFormat.GREEN+"Новая арена", __ -> {
			CustomForm newArenaForm = Form.custom();
			Integer nextId = 0;
			while (gamer.manager.arenas().containsKey(nextId)) {
				++nextId;
			}
			newArenaForm.input("Числовой ID новой арены", "", nextId.toString());
			newArenaForm.dropdown("Тип новой арены", gamer.manager.arenaTypes().keySet().stream().toList());
			newArenaForm.onSubmit((___, resp) -> {
				int newId;
				try {
					newId = Integer.parseInt(resp.getInput(0));
				}
				catch (NumberFormatException e) {
					gamer.sendMessage("ID должен быть числом");
					return;
				}
				if (gamer.manager.arenas().containsKey(newId)) {
					gamer.sendMessage("ID уже занят: "+newId);
					return;
				}
				gamer.manager.addAvailableArena(gamer.manager.arenaTypes().get(resp.getDropdown(0).getOption()), newId);
				gamer.sendMessage("Арена с ID "+newId+" и типом "+resp.getDropdown(0).getOption()+" успешно создана");
			});
			gamer.sendForm(newArenaForm);
		});

		for (Object o : gamer.manager.arenas().values()) {
			Arena arena = (Arena) o;
			form.button("#"+arena.id()+" ("+arena.type().key()+") "+arena.gamersCount()+"/"+arena.type().maxPlayers()+ " | "+arena.state(), __ -> {
				SimpleForm arenaForm = Form.simple();
				arenaForm.content("ID: "+arena.id()+"\n Тип: "+arena.type().key()+"\n "+"Состояние: "+arena.state());
				arenaForm.button("Удалить", ___ -> {
					gamer.sendMessage("Не реализовано");
				});
				arenaForm.button("Добавить табличку", ___ -> {
					gamer.inventory().addItem(Items.FEATHER().onInteract((____, block) -> {
						if (block instanceof BlockSign sign) {
							Arena a = gamer.manager.getArenaBySign(sign);
							if (a != null) {
								gamer.sendMessage("Табличка занята");
							} else {
								gamer.manager.addStateSign(arena, sign);
								gamer.sendMessage("Табличка успешно присовена арене #"+arena.id());
							}
						} else {
							gamer.inventory().remove(Items.FEATHER());
						}
					}));

					gamer.sendMessage("Вам выдан делатель табличек для арены #"+arena.id());
					gamer.sendMessage("Тапните им по табличке. Предмет многоразовый.");
					gamer.sendMessage("Чтобы избавиться от него, тапните им по любому другому блоку");
				});
				gamer.sendForm(arenaForm);
			});
		}

		gamer.sendForm(form);
	}
}
