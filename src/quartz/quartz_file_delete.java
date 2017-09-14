package quartz;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
public class quartz_file_delete {
	private static final ResourceBundle RESOURCE_BUNDLE=ResourceBundle
			.getBundle("export_project",Locale.getDefault());
	private static void deleteallfile(File file) {
		if(!file.exists()) {
			return;
		}
		if(file.isDirectory()&&
				!file.getName().equals(new SimpleDateFormat("yyyyMMdd").format(new Date()))) {
			File file_list []=file.listFiles();
			for(int i=0;i<file_list.length;i++) {
				deleteallfile(file_list[i]);
			}
			file.delete();
		}else if (file.isFile()) {
			file.delete();
		}
	}
	public void excute() {
		deleteallfile(new File(RESOURCE_BUNDLE.getString("export_project_path")));
	}
}
