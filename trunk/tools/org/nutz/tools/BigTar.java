package org.nutz.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

public class BigTar {

    public static void main(String[] args) throws Throwable {
        write();
        read();
        System.out.println(Long.parseLong("7w4g7wnu", 36));
    }

    public static void write() throws Throwable {
        FileOutputStream fos = new FileOutputStream("C:\\X2.tgz");
        GZIPOutputStream out = new GZIPOutputStream(fos);
        TarArchiveOutputStream tos = new TarArchiveOutputStream(out);
        tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        // byte[] buf = Streams.readBytes(new FileInputStream("C:\\wendal.ts"));
        for (int i = 0; i < 2; i++) {
            try {
                ArchiveEntry entry = tos.createArchiveEntry(new File("C:\\Q.zip"),
                                                            Strings.dup('A', 500) + i);
                tos.putArchiveEntry(entry);
                // tos.write(buf);
            tos.closeArchiveEntry();
            }
            catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
            }
        }
        tos.flush();
        tos.finish();
        tos.close();
    }

    public static void read() throws Throwable {
        FileInputStream fis = new FileInputStream("C:\\X2.tgz");
        TarArchiveInputStream tis = new TarArchiveInputStream(new GZIPInputStream(fis));
        while (true) {
            TarArchiveEntry entry = tis.getNextTarEntry();
            if (entry == null)
                break;
            System.out.println(entry.getName());
        }
    }
}
