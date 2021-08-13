package lord.core.util.file;

import lombok.Getter;
import lombok.var;
import lord.core.LordCore;
import lord.core.util.Util;
import lord.core.util.logger.ILordLogger;
import lord.core.util.logger.LordLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdvFile {
	
	/* ======================================================================================= */
	
	private static ILordLogger logger;
	
	private static void onEnable (LordCore core) {
		logger = LordLogger.get(AdvFile.class);
	}
	
	public static final String JSON = "json";
	public static final String TXT = "txt";
	public static final String YAML = "yml";
	
	/**
	 * Получить новый экземпляр AdvFile
	 * @param folder Путь к папке
	 * @param name Название файла
	 */
	public static AdvFile get (String folder, String name) {
		return new AdvFile(folder, name);
	}
	
	/**
	 * Получить новый экземпляр AdvFile
	 * @param folder Папка - Экземпляр AdvFolder
	 * @param name Название файла
	 */
	public static AdvFile get (AdvFolder folder, String name) {
		return new AdvFile(folder, name);
	}
	
	/* ======================================================================================= */
	
	/** Путь к папке */
	@Getter private final String folderPath;
	
	/** Имя файла */
	@Getter private final String name;
	
	/** Java File */
	@Getter private File javaFile;
	
	private Scanner reader = null;
	
	private FileWriter writer = null;
	
	public AdvFile (AdvFolder folder, String name) {
		this.name = name;
		this.folderPath = folder.getPath();
		this.javaFile = new File(this.folderPath, this.name);
	}
	
	public AdvFile (String folder, String name) {
		this.name = name;
		this.folderPath = AdvFolder.addLastSlash(folder);
		this.javaFile = new File(this.folderPath, this.name);
	}
	
	/* ======================================================================================= */
	
	public String getBaseName () {
		int pos = this.name.lastIndexOf(".");
		if (pos == -1) return this.name;
		return this.name.substring(0, pos);
	}
	
	/** Есть ли файл */
	public boolean exists() {
		return this.javaFile.exists();
	}
	
	/** Создает файл, если его нету
	 * @return Успешно ли создан */
	public boolean create () {
		boolean success = false;
		try {
			success = this.javaFile.createNewFile();
		}
		catch (IOException e) {
			logger.error("CANNOT CREATE FILE \"" + this.name + "\" IN DIR \"" + this.folderPath + "\"");
			e.printStackTrace();
		}
		return success;
	}
	
	/** Удаляет файл
	 * @return Успешно ли удалён */
	public boolean delete () {
		if (exists()) {
			boolean success = this.javaFile.delete();
			if (!success) {
				logger.error("CANNOT DELETE FILE \"" + this.name + "\" IN DIR \"" + this.folderPath + "\"");
			}
			return success;
		}
		return true;
	}
	
	public boolean copyTo (AdvFolder folder) {
		return copyTo(folder, this.name);
	}
	
	public boolean copyTo (AdvFolder folder, String name) {
		return copyTo(folder, name, true);
	}
	
	public boolean copyTo (AdvFolder folder, boolean deleteIfExists) {
		return copyTo(folder, this.name, deleteIfExists);
	}
	
	public boolean copyTo (AdvFolder folder, String name, boolean deleteIfExists) {
		if (!this.exists()) {
			logger.error("Unable to copy file (" + this.getJavaFile().getPath() + ") to (" + folder.getPath() + name +
				") because source is not exists");
			return false;
		}
		folder.mkdirIfNot();
		
		var currentFolder = AdvFolder.get(this.folderPath);
		if (folder.getPath().equals(currentFolder.getPath()) && this.name.equals(name)) {
			logger.error("Unable to copy file (" + this.getJavaFile().getPath() + ") to (" + folder.getPath() + name +
				") because is is the same file");
			return false;
		}
		
		var file = folder.getFile(name);
		if (file.exists()) {
			if (!deleteIfExists) {
				return true;
			}
		}
		
		String data = this.readFast();
		file.writeFast(data);
		
		return true;
	}
	
	/** Быстрая запись */
	public void writeFast(String text) {
		this.writeFast(text, false);
	}
	
	/** Быстрая запись */
	public void writeFast(String text, boolean append) {
		this.startWriting(append);
		this.write(text);
		this.finish();
	}
	
	/** Быстрая запись строк */
	public void writeFast(String[] lines) {
		this.startWriting();
		boolean isFirstLine = true;
		
		for (String line : lines) {
			if (isFirstLine) {
				isFirstLine = false;
			}
			else {
				this.enter();
			}
			this.write(line);
		}
		this.finish();
	}
	
	/** Быстрая запись строк, дополнительная реализация */
	public void writeFast(Object[] lines) {
		this.startWriting();
		boolean isFirstLine = true;
		
		for (Object line : lines) {
			if (isFirstLine) {
				isFirstLine = false;
			}
			else {
				this.enter();
			}
			this.write(String.valueOf(line));
		}
		this.finish();
	}
	
	/** Быстрая запись строк */
	public void writeFast(ArrayList<String> lines) {
		this.writeFast(lines.toArray(new String[0]));
	}
	
	/**
	 * Быстрое полное чтение
	 * @return Текст. Если неудачно то NULL
	 */
	public String readFast() {
		String text = null;
		try {
			text = Files.readString(this.javaFile.toPath());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}
	
	/** Быстрое чтение строк в массив */
	public List<String> readAllLines() {
		List<String> lines = new ArrayList<>();
		this.startReading();
		while (this.hasNextLine()) {
			lines.add(this.nextLine());
		}
		this.finish();
		return lines;
	}
	
	/** Быстрое чтение строк в массив */
	public String[] readAllLines(boolean array) {
		return this.readAllLines().toArray(new String[0]);
	}
	
	/**
	 * Открывает поток чтения файла
	 * @return Успешно ли
	 */
	public boolean startReading () {
		boolean success = true;
		try {
			this.reader = new Scanner(this.javaFile);
		}
		catch (FileNotFoundException e) {
			logger.error("CANNOT READ FILE \"" + this.name + "\" IN DIR \"" + this.folderPath + "\" BECAUSE NOT EXISTS");
			success = false;
		}
		return success;
	}
	
	/** Возвращает следующую линию файла */
	public String nextLine() {
		return this.reader.nextLine();
	}
	
	/** TRUE если еще есть линии в файле */
	public boolean hasNextLine() {
		return this.reader.hasNextLine();
	}
	
	/**
	 * Открыть поток записи (APPEND FALSE)
	 * @return Удалось ли открыть поток
	 */
	public boolean startWriting () {
		return this.startWriting(false);
	}
	
	/**
	 * Открыть поток записи
	 * @param append Дописывать вместо перезаписи
	 * @return Удалось ли открыть поток
	 */
	public boolean startWriting (boolean append) {
		boolean success = true;
		try {
			this.writer = new FileWriter(this.javaFile, append);
		}
		catch (IOException e) {
			logger.error("CANNOT INIT WRITER FOR FILE \"" + this.name + "\" IN DIR \"" + this.folderPath + "\"");
			success = false;
		}
		return success;
	}
	
	/** Писать в файл */
	public AdvFile write(String text) {
		try {
			this.writer.write(text);
		}
		catch (IOException e) {
			logger.error("CANNOT WRITE TO FILE \"" + this.name + "\" IN DIR \"" + this.folderPath + "\"");
			e.printStackTrace();
		}
		return this;
	}
	
	public AdvFile write(int text) {
		return this.write(String.valueOf(text));
	}
	
	public AdvFile write(long text) {
		return this.write(String.valueOf(text));
	}
	
	public AdvFile write(boolean text) {
		return this.write(String.valueOf(text));
	}
	
	/**
	 * Записать текст и затем перевести линию
	 */
	public AdvFile writeLN(String text) {
		this.write(text);
		this.enter();
		return this;
	}
	
	public AdvFile writeLN(int text) {
		return this.writeLN(String.valueOf(text));
	}
	
	public AdvFile writeLN(long text) {
		return this.writeLN(String.valueOf(text));
	}
	
	public AdvFile writeLN(boolean text) {
		return this.writeLN(String.valueOf(text));
	}
	
	/**
	 * Перевести линию и записать текст
	 */
	public AdvFile writeNewLine(String text) {
		this.enter();
		this.write(text);
		return this;
	}
	
	public AdvFile writeNewLine(int text) {
		return this.writeNewLine(String.valueOf(text));
	}
	
	public AdvFile writeNewLine(long text) {
		return this.writeNewLine(String.valueOf(text));
	}
	
	public AdvFile writeNewLine(boolean text) {
		return this.writeNewLine(String.valueOf(text));
	}
	
	/**
	 * Перевод строки при записи
	 */
	public AdvFile enter() {
		this.write(System.lineSeparator());
		return this;
	}
	
	/**
	 * Закрыть поток чтения или записи
	 */
	public void finish() {
		if (this.reader != null) {
			this.reader.close();
			this.reader = null;
		}
		if (this.writer != null) {
			try {
				this.writer.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			this.writer = null;
		}
	}
	
	/**
	 * Читает и десериализует данные
	 * @param clazz Класс требуемого объекта
	 * @return Null, если была ошибка при чтении
	 */
	public <T> T readJson (Class<T> clazz) {
		String json = this.readFast();
		if (json == null) {
			return null;
		}
		return Util.getGson().fromJson(json, clazz);
	}
	
	/** Сохраняет обьект как Json в этом файле */
	public void writeJson (Object object) {
		this.writeFast(Util.getGson().toJson(object));
	}
	
	/** Сохраняет обьект как красивый Json в этом файле */
	public void writePrettyJson (Object object) {
		this.writeFast(Util.getPrettyGson().toJson(object));
	}
	
}
