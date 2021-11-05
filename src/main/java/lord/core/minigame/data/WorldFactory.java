package lord.core.minigame.data;

import dev.ghostlov3r.beengine.world.World;
import dev.ghostlov3r.beengine.world.WorldManager;
import dev.ghostlov3r.beengine.world.format.io.WorldProviderManager;
import dev.ghostlov3r.beengine.world.format.io.WritableWorldProvider;
import dev.ghostlov3r.common.concurrent.Promise;

import javax.annotation.Nullable;
import java.nio.file.Path;

public class WorldFactory {

	private Path path;

	@Nullable
	private Promise<WritableWorldProvider> provider;

	private int counter;

	boolean keep;

	public WorldFactory (Path path) {
		this.path = path;
	}

	public Promise<World> createWorld () {
		load();

		Promise<World> promise = new Promise<>();

		provider.onResolve(__ -> {
			promise.resolve(WorldManager.get().createUsingProvider(provider.result()));
		});

		++counter;

		return promise;
	}

	public void closeWorld (World world) {
		WorldManager.get().unloadWorld(world);
		--counter;
		maybeUnload();
	}

	private void maybeUnload () {
		if (keep) {
			return;
		}
		if (counter == 0) {
			WorldProviderManager.get().close(path);
			provider = null;
		}
	}

	private void load () {
		if (provider == null) {
			if (counter != 0) {
				throw new IllegalStateException("Counter is "+counter);
			}
			provider = WorldProviderManager.get().open(path);
		}
	}

	public void keep () {
		keep = true;
		load();
	}

	public void unkeep () {
		keep = false;
		maybeUnload();
	}
}
