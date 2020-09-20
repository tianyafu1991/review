import java.io.File;

public class DeleteMaven {

    public static void delete(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File filepath : files) {
                delete(filepath);
            }
        }else{
            if("_remote.repositories".equals(file.getName())){
                file.delete();
                return;
            }
        }
    }


    public static void main(String[] args) {
        String path = "F:\\maven";

        File rootPath = new File(path);

        delete(rootPath);


    }
}
