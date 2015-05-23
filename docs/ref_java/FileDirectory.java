package com.cable.dctvcloud.demo;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by erickou on 2015/5/1.
 */
public class FileDirectory {

        String name;
        char nodeType;
        String fileClass;
        String fileId;
        String fileUrl;
        long fileSize;
        FileDirectory nodeParent;
        ArrayList<FileDirectory> children;

        public FileDirectory(String name1,
                          char nodeType1,
                          String fileClass1,
                          String fileId1,
                          String fileUrl0,
                          long fileSize1,
                          FileDirectory parentNode)
        {
            name=name1;
            nodeType=nodeType1;
            fileClass=fileClass1;
            fileId=fileId1;
            fileUrl=fileUrl0;
            fileSize=fileSize1;
            nodeParent=parentNode;
        }


    public static FileDirectory rootDir=new FileDirectory("Root", 'D', null, null, null, 0, null);
    static class DirString{
        String directoryName;
        String directoryFiles;
        public DirString(String dn, String sDF)
        {
            directoryName=dn;
            directoryFiles=sDF;
        }
    }

    public static FileDirectory getRoot()
    {
        return rootDir;
    }

    public static void buildNewDirectory(String jString) {

        String[] dirs = jString.split("@Name"); //file type under root
        if (dirs.length < 2) return;

        if (rootDir.children==null) rootDir.children=new ArrayList<FileDirectory>();
        ArrayList<FileDirectory> aDir = rootDir.children;
        Vector<DirString> allDir = new Vector<DirString>();
        for (int i = 1; i < dirs.length; i++) {
            int iCol = dirs[i].indexOf(":");
            int i0 = ++iCol;
            while (i0 < dirs[i].length() && dirs[i].charAt(i0) != '"') {
                i0++;
            }
            int i9 = ++i0;
            while (i9 < dirs[i].length() && dirs[i].charAt(i9) != '"') {
                i9++;
            }
            String dirName = dirs[i].substring(i0, i9);
            String dirJason = dirs[i].substring(i9);
            DirString aDS = new DirString(dirName, dirJason);
            allDir.add(aDS);
        }
        for (int i = 0; i < allDir.size(); i++) {
            String jDir = allDir.get(i).directoryName;
            String fileClass = "Others";
            if (jDir.contains("Videos")) fileClass = "Videos";
            if (jDir.indexOf("Picture") >= 0) fileClass = "Images";
            if (jDir.indexOf("Music") >= 0) fileClass = "Audios";
            if (jDir.indexOf("Documents") >= 0) fileClass = "Documents";


            FileDirectory newDir = new FileDirectory(jDir, 'D', null, null, null, 0, rootDir);
            newDir.children = new ArrayList<FileDirectory>();
            rootDir.children.add(newDir);
            FileDirectory parent=newDir;



            String jFiles = allDir.get(i).directoryFiles;

            String[] files = jFiles.split("\"ID\"");

            if (files.length < 1) {

                continue;
            }


            for (int k = 1; k < files.length; k++) {
                MyJson aJson = new MyJson("ID\"" + files[k]);
                String fID = aJson.scanNameForData("ID");
                if (fID == null) continue;
                String size = aJson.scanNameForData("Size");
                String fNm = aJson.scanNameForData("Name");
                String ext = aJson.scanNameForData("Extension");
                String tmb64 = aJson.scanNameForData("URL");
                String thumb = null;
                if (tmb64 != null) {
                    thumb = MyJson.getBase64Msg(tmb64);
                }
                //if (aDir.files == null) aDir.files
                String fileName = fNm;
                if (ext != null && ext.length() > 0) {
                    fileName = fNm + "." + ext;
                }

                FileDirectory aFile = new FileDirectory(fileName, 'F', fileClass, fID, thumb, Long.parseLong(size), parent);

                aFile.children = new ArrayList<FileDirectory>();
                parent.children.add(aFile);

            }
        }
    }

}
