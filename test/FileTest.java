
import org.junit.Before;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class FileTest {
    String dir = "parent_dir";
    private system fs;

    @Before
    public void setUp() {
        fs = new system();
        File parentDir = new File(dir);
        if (!parentDir.exists())
            parentDir.mkdir();
    }

    @Test
    public void SingleTest() {
        String filename = UUID.randomUUID().toString() + ".txt";
        long time = System.currentTimeMillis();
        Object[] result = makeFileInfo(true, false, true, dir + "/" + filename, time, true);
        boolean readable = true;
        boolean writable = false;
        boolean executable = true;
        String long_out = (String) result[0];
        File file = (File) result[1];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String long_info = String.format("%s%s%s %s %s %s", file.canRead() ? "r" : "-", file.canWrite() ? "w" : "-", file.canExecute() ? "x" : "-", getPrintSize(new File(filename).length()), format.format(time), filename);
        assertEquals(long_info, long_out);
        result = makeFileInfo(true, false, true, dir + "/" + filename, time, false);
        String short_out = (String) result[0];
        file = (File) result[1];
        String short_info = String.format("%d%d%d %d %s %s", file.canRead() ? 1 : 0, file.canWrite() ? 1 : 0,file.canExecute() ? 1 : 0, new File(filename).length(), format.format(time), filename);
        assertEquals(short_info, short_out);
        cleanParent();
    }
    @Test
    public void ReverseTest() throws InterruptedException {
        cleanParent();
        StringBuilder sb = new StringBuilder();
        boolean readable;
        boolean writable;
        boolean executable;
        Map<String, String> recordMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String filename = UUID.randomUUID().toString();
            long time = System.currentTimeMillis();
            readable = getTrueOrFlase();
            writable = getTrueOrFlase();
            executable = getTrueOrFlase();
            Object[] result =  makeFileInfo(readable, writable, executable, dir + "/" + filename, time, true);
            File file = (File) result[1];
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String long_info = String.format("%s%s%s %s %s %s", file.canRead() ? "r" : "-", file.canWrite() ? "w" : "-", file.canExecute() ? "x" : "-", getPrintSize(new File(filename).length()), format.format(time), filename);
            recordMap.put(filename, long_info);
        }
        File file;
        for (int i = new File(dir).listFiles().length - 1; i >= 0; i--) {
            file = new File(dir).listFiles()[i];
            sb.append(recordMap.get(file.getName())).append("\n");
        }
        String raw_info = fs.fileDictory(new File(dir), true, true);
        assertEquals(sb.toString(), raw_info);
        cleanParent();
    }

    @Test
    public void OutputFileTest() throws Exception {
        cleanParent();
        String outPut = "out11111.txt";
        File outFile = new File(outPut);
        if (outFile.exists())
            outFile.delete();
        StringBuilder sb = new StringBuilder();
        boolean readable;
        boolean writable;
        boolean executable;
        Map<String, String> recordLMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            long time = System.currentTimeMillis();
            String filename = UUID.randomUUID().toString();
            readable = getTrueOrFlase();
            writable = getTrueOrFlase();
            executable = getTrueOrFlase();
            Object[] result = makeFileInfo(readable, writable, executable, dir + "/" + filename, time, true);
            File file = (File) result[1];
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String long_info = String.format("%s%s%s %s %s %s", file.canRead()? "r" : "-", file.canWrite() ? "w" : "-", file.canExecute() ? "x" : "-", getPrintSize(new File(filename).length()), format.format(time), filename);
            recordLMap.put(filename, long_info);
        }
        for (File file : new File(dir).listFiles()) {
            sb.append(recordLMap.get(file.getName())).append("\n");
        }
        fs.commandParse(new String[]{"-h", "-o", outPut, dir});
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        FileReader fr = new FileReader(outFile);
        BufferedReader br = new BufferedReader(fr);
        while ((line = br.readLine()) != null)
            stringBuilder.append(line).append("\n");
        br.close();
        fr.close();
        assertEquals(sb.toString(), stringBuilder.toString());
        cleanParent();
    }


    public boolean getTrueOrFlase() {
        Random random = new Random();
        return random.nextInt(2) == 1;
    }

    public Object[] makeFileInfo(boolean readable, boolean writable, boolean executable, String filename, Long time, boolean isLong) {
        File file;
        try {
            file = new File(filename);
            if (!file.exists())
                file.createNewFile();
            file.setLastModified(time);
            System.out.println("0 " + filename + "  " + readable + "  " + writable + "  " + executable);
            System.out.println("1 " + filename + "  " + file.canRead() + "  " + file.canWrite() + "  " + file.canExecute());
            return new Object[]{fs.getFile(file, isLong), file};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cleanParent() {
        File parent = new File(dir);
        for (File f : parent.listFiles()) {
            f.delete();
        }
    }


    private String getPrintSize(long size) {
        double value = (double) size;
        if (value < 1024)
            return String.valueOf(value) + "B";
        else
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

        if (value < 1024)
            return String.valueOf(value) + "KB";
        else
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        if (value < 1024)
            return String.valueOf(value) + "MB";
        else {
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return String.valueOf(value) + "GB";
        }
    }
}