package lord.core.util.file;

import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.var;
import lord.core.LordCore;
import lord.core.util.logger.ILordLogger;
import lord.core.util.logger.LordLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AdvFolder {
	
	/* ======================================================================================= */
	
	private static ILordLogger logger;
	
	private static void onEnable (LordCore core) {
		logger = LordLogger.get(AdvFolder.class);
	}
	
	public static AdvFolder get (String path) {
		return new AdvFolder(path);
	}
	
	public static AdvFolder get (AdvFolder parent, String folder) {
		return new AdvFolder(parent, folder);
	}
	
	public static AdvFolder get (String parent, String folder) {
		return new AdvFolder(parent, folder);
	}
	
	public static String addLastSlash (String path) {
		return path.charAt(path.length() - 1) == '/' ? path : path + "/";
	}
	
	/* ======================================================================================= */
	
	/** Путь к папке */
	private final String path;
	
	/** Java File */
	private final File javaFile;
	
	public AdvFolder (String parentFolder, String folder) {
		this(addLastSlash(parentFolder) + folder);
	}
	
	public AdvFolder (AdvFolder parentFolder, String folder) {
		this(parentFolder.getPath() + folder);
	}
	
	public AdvFolder (String path) {
		this.path = addLastSlash(path);
		this.javaFile = new File(this.path);
	}
	
	/* ======================================================================================= */
	
	/** @return True, если папка есть */
	public boolean exists () {
		return this.javaFile.exists();
	}
	
	/** Создает папку
	 * @return True, если успешно */
	public boolean mkdir () {
		return this.javaFile.mkdir();
	}
	
	/** Создает папку если ее нет
	 * @return True, если успешно или папка уже есть */
	public boolean mkdirIfNot () {
		return this.javaFile.exists() || this.mkdir();
	}
	
	/** Удаляет папку
	 * @return True, если успешно или папки не было */
	public boolean delete () {
		return !this.exists() || this.javaFile.delete();
	}
	
	public boolean fullDelete () {
		var files = javaFile.listFiles();
		if (files == null) return false;
		if (files.length == 0) return delete();
		
		for (File file : files) {
			if (file.isFile()) {
				if (!file.delete()) {
					logger.error("Unable to delete dir (" + this.path + "), because unable to delete File: (" + file.toString() + ")");
					return false;
				}
			}
			if (file.isDirectory()) {
				var folder = AdvFolder.get(file.getPath());
				if (!folder.fullDelete()) {
					logger.error("Unable to delete dir (" + this.path + "), because unable to delete Dir: (" + file.toString() + ")");
					return false;
				}
			}
		}
		delete();
		return true;
	}
	
	public void copyError (AdvFolder target, String reason) {
		logger.error("Unable to copy folder content (" + this.path + ") to the (" + target.path + "), because " + reason);
	}
	
	/** Копирует эту папку и контент внутрь указанной папки
	 * @return True, если операция закончилась успехом */
	public boolean copyFolderIn (AdvFolder folder) {
		return copyContentIn(folder.getChild(this.javaFile.getName()));
	}
	
	/**
	 * @return True, если операция закончилась успехом */
	public boolean copyContentIn (AdvFolder folder) {
		if (folder.path.equals(this.path)) {
			copyError(folder, "it is the same folder");
			return false;
		}
		if (folder.exists()) {
			var files = folder.javaFile.listFiles();
			if (files == null) {
				copyError(folder, "listFiles of target returned null");
				return false;
			}
			if (files.length != 0) {
				copyError(folder, "folder exists and not empty");
				return false;
			}
		} else {
			folder.mkdirIfNot();
		}
		
		var thisFiles = this.javaFile.listFiles();
		if (thisFiles == null) {
			copyError(folder, "listFiles of source returned null");
			return false;
		}
		
		if (thisFiles.length == 0) return true;
		
		for (File file : thisFiles) {
			if (file.isDirectory()) {
				var oldFolder = AdvFolder.get(file.getPath());
				var newFolder = folder.getChild(oldFolder.javaFile.getName());
				if (!oldFolder.copyContentIn(newFolder)) {
					return false;
				}
			}
			if (file.isFile()) {
				var adv = this.getFile(file.getName());
				if (!adv.copyTo(folder)) {
					copyError(folder, "error while copying file " + adv.getJavaFile().getPath());
					return false;
				}
			}
		}
		
		return true;
	}
	
	/** Создает файл в этой папке
	 * @return Созданный файл или Null, если не вышло */
	public AdvFile addFile (String name) {
		var file = AdvFile.get(this.path, name);
		return file.create() ? file : null;
	}
	
	/**
	 * Возвращает экземпляр AdvFile, основанный на этой папке
	 * @param name Имя без расширения
	 * @param ext Расширение файла
	 */
	public AdvFile getFile (String name, String ext) {
		return getFile(name + "." + ext);
	}
	
	/** Возвращает экземпляр AdvFile, основанный на этой папке */
	public AdvFile getFile (String name) {
		return AdvFile.get(this.path, name);
	}
	
	@Nullable
	public File[] getAllJavaFiles () {
		return getAllJavaFiles(null);
	}
	
	@Nullable
	public File[] getAllJavaFiles (@Nullable String ext) {
		return ext == null ? javaFile.listFiles() : javaFile.listFiles(ExtensionFilter.of(ext));
	}
	
	/** @return List всех файлов в этой папке */
	@Nullable
	public List<AdvFile> getAllFiles () {
		return this.getAllFiles(null);
	}
	
	/** @return  List файлов в этой папке
	 * @param ext Расширение */
	@Nullable
	public List<AdvFile> getAllFiles (@Nullable String ext) {
		var files = getAllJavaFiles(ext);
		if (files == null) return null;
		
		var advFiles = new ArrayList<AdvFile>();
		if (files.length == 0) return advFiles;
		
		for (File file : files) {
			if (file != null && file.isFile()) advFiles.add(this.getFile(file.getName()));
		}
		return advFiles;
	}
	
	/**
	 * Возвращет все десеариализованные объекты
	 * из файлов JSON в этой папке
	 * @param clazz Класс обьектов
	 * @return Null означает взлом жопы, папка не папка, шутдаун в таком случае
	 */
	@Nullable
	public <T> List<T> readAllJsons (@NotNull Class<T> clazz) {
		var files = this.getAllFiles("json");
		if (files == null) return null;
		
		var objects = new ArrayList<T>();
		files.forEach(file -> {
			objects.add(file.readJson(clazz));
		});
		return objects;
	}
	
	/** Есть ли в этой папке указанный файл */
	public boolean hasFile (String name) {
		return AdvFile.get(this.path, name).exists();
	}
	
	/** Возвращает AdvFolder дочерней папки */
	public AdvFolder getChild (String folder) {
		return get(this, folder);
	}
	
	/** Возвращает AdvFolder дочерней папки и создает ее */
	public AdvFolder mkdirChild (String folder) {
		var advf = this.getChild(folder);
		advf.mkdirIfNot();
		return advf;
	}
	
	/** Возвращает Config из файла в этой папке
	 ** Если файла нет, вернет Null */
	public Config getConfig(String filename) {
		return this.getConfig(filename, Config.YAML);
	}
	
	/** Возвращает Config из файла в этой папке
	 ** Если файла нет, вернет Null */
	public Config getConfig(String filename, int type) {
		AdvFile configFile = this.getFile(filename);
		if (configFile.exists()) {
			return new Config(this.path + filename, type);
		}
		return null;
	}
	
	/** Создает новый файл конфига в этой папке */
	public Config createConfig(String filename) {
		return this.createConfig(filename, Config.YAML);
	}
	
	/** Создает новый файл конфига в этой папке */
	public Config createConfig(String filename, int type) {
		return new Config(this.path + filename, type);
	}
	
}
