import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class main {

	public static void main(String[] args) throws Exception {

		File currentDir = new File("Directory");
		List<File> files = getFiles(currentDir);
		for (File file : files) {
			System.out.println(file.getName());
			DataClass.main(file);
			LargeClass.main(file);
			LongMethod.main(file);
			LongParameterList.main(file);
			MessageChains.main(file);
			MiddleMan.main(file);
		}

	}

	public static List<File> getFiles(File dir) throws Exception {
		List<File> returnFiles = new ArrayList<File>();
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					returnFiles.addAll(getFiles(file));
				} else {
					returnFiles.add(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnFiles;
	}
}
