package net.vdcraft.arvdc.timemanager.mainclass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import net.vdcraft.arvdc.timemanager.MainTM;

public class CopyFilesHandler extends MainTM {

	/** 
	 * Export files from the .jar
	 */ 
	public static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0) {
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

	/** 
	 * Easy use of the method copy() from any another external class
	 */ 
	public static void copyAnyFile(String copyFileName, File copyFileYaml) {
		copy(MainTM.getInstance().getResource(copyFileName), copyFileYaml);
	};
}
