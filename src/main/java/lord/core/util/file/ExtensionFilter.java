package lord.core.util.file;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;

@RequiredArgsConstructor
public class ExtensionFilter implements FilenameFilter {
	
	public static final ExtensionFilter JSON = of(AdvFile.JSON);
	public static final ExtensionFilter TXT  = of(AdvFile.TXT);
	public static final ExtensionFilter YAML = of(AdvFile.YAML);
	
	private final String extension;
	
	@Override
	public boolean accept (File dir, String name) {
		int index = name.lastIndexOf('.');
		if (index == -1) return false;
		return extension.equals(name.substring(index + 1));
	}
	
	public static ExtensionFilter of (@NotNull String ext) {
		if (ext.equals(AdvFile.JSON)) return JSON;
		if (ext.equals(AdvFile.TXT))  return TXT;
		if (ext.equals(AdvFile.YAML)) return YAML;
		
		return new ExtensionFilter(ext);
	}
	
}
