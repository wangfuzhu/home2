import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class system {
    public String getFile(File file,boolean isLong){
        boolean executable = file.canExecute();
        boolean writable = file.canWrite();
        boolean readable = file.canRead();
        long lastTime = file.lastModified();
        long size = file.length();
        String name = file.getName();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastOutTime = format1.format(lastTime);
        if (isLong)
            return String.format("%s%s%s %s %s %s",readable ? "r" : "-",
                    writable ? "w" : "-",executable? "x":"-");
        else
            return String.format("%s%s%s %s %s %s",readable ? 1 : 0,
                    writable ? 1 : 0,executable? 1:0);

    }
    private String getPrintSize(long size){
        double value = (double) size;
        if (value < 1024)
            //if smaller than 1024,we should use "B"
            return String.valueOf(value) + "B";
        else
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        if (value < 1024)
            //after that ,if value smaller than 1024 we need to use kb (2048b /1024 = 2kb
            return String.valueOf(value) + "KB";
        else
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        if (value < 1024)
            //after that ,if value smaller than 1024 we need to use kb (2048b /1024 = 2kb
            return String.valueOf(value) + "MB";
        else {
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return String.valueOf(value) + "GB";
        }
    }

    public String fileDictory(File file,boolean isLong,boolean isReverse){
        StringBuilder sb = new StringBuilder();
        if (file.isFile()){
            sb.append(getFile(file,isLong)).append("\n");
        } else {
            File[] files;
            if ((files = file.listFiles()) != null) {
                if (isReverse)
                    for (int i = files.length - 1; i >= 0; i--)
                        sb.append(getFile(files[i], isLong)).append("\n");
                else
                    for (int i = 0; i <= files.length - 1; i++)
                        sb.append(getFile(files[i], isLong)).append("\n");
            }
        }
        return sb.toString();
    }
    private void writeToFile(String contert, File outFiles){
        FileWriter fw;
        try{
            fw = new FileWriter(outFiles);
            fw.write(contert);
            fw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void commandParse(String[] args) throws Exception {
        Map<String, String> map1 = new HashMap<>();
        String arg;
        for (int i = 0; i < args.length - 1; i++) {
            arg = args[i];
            if (arg.equals("-l"))
                if (map1.containsKey("-h"))
                    throw new Exception("command error");
                else
                    map1.put("-l", "");
            else if (arg.equals("-h"))
                if (map1.containsKey("-l"))
                    throw new Exception("command error");
                else
                    map1.put("-h", "");
            else if (arg.equals("-r"))
                map1.put("-r", "");
            else if (arg.equals("-o")) {
                if (i < args.length - 2) {
                    String outPutF = args[i + 1];
                    i++;
                    map1.put("-o", outPutF);
                } else
                    throw new Exception("command error");
            }
        }
        String file = args[args.length - 1];
        if (file.startsWith("-")) {
            throw new Exception("command error");
        }
        if (!map1.containsKey("-l") && !map1.containsKey("-h"))
            throw new Exception("command error");
        boolean islong = map1.containsKey("-h");
        boolean isReverse = map1.containsKey("-r");
        File outFile = null;
        if (map1.containsKey("-o")) {
            String name = map1.get("-o");
            outFile = new File(name);
        }
        String out = fileDictory(new File(file), islong, isReverse);
        if (outFile == null)
            System.out.println(out);
        else {
            if (!outFile.exists()) {
                try {
                    boolean ceate = outFile.createNewFile();
                    if (ceate)
                        writeToFile(out, outFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}