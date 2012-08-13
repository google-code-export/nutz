package org.nutz.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Files;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;

public class CleanCode {

    public static void main(String[] args) {
        CleanCode.removeTabs("tools");
    }
    
    public static int removeTabs(String path) {
        final int[] re = new int[1];
        Disks.visitFile(new File(path), new FileVisitor() {
            
            public void visit(File file) {
                if (file.isDirectory())
                    return;
                if (!file.getName().endsWith(".java"))
                    return;
                String str = Files.read(file);
                if (!str.contains("\t"))
                    return;
                str = str.replaceAll("\t", "    ");
                Files.write(file, str);
                re[0]++;
            }
        }, null);
        return re[0];
    }
}

class ListList {
    
    private List<Object[]> list = new ArrayList<Object[]>();
    
    public void add(Object ...objs) {
        list.add(objs);
    }
    
    public String toJson(JsonFormat format) {
        return Json.toJson(list, format);
    }
    
    public String toString() {
        return list.toString();
    }
}
