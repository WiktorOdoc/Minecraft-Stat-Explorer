package com.wt.mse.services;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class StatsFileService
{
    public File[] getFilesBySize(Path dirPath) //gets all files from folder. Orders by descending file size so most active players are first.
    {
        File folder = dirPath.toFile();
        File[] allFiles = folder.listFiles();
        if(allFiles == null || allFiles.length == 0)
        {
            return new File[0];
        }

        File temp;
        int j;
        for(int i = 1; i < allFiles.length; i++) //insertion sort
        {
            temp = allFiles[i];
            for(j = i; j > 0 && temp.length() > allFiles[j - 1].length(); j--)
            {
                allFiles[j] = allFiles[j - 1];
            }
            allFiles[j] = temp;
        }

        return allFiles;
    }

    public String getJsonString(String fileName, Path dirPath) throws IOException //reads json file into string
    {
        return Files.readString(dirPath.resolve(fileName), StandardCharsets.UTF_8);
    }
}
