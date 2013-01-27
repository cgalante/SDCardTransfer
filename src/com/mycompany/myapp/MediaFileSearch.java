package com.mycompany.myapp;
import java.io.*;
import java.util.*;
import android.util.*;
import android.app.*;
import android.content.res.*;
import android.os.*;


public class MediaFileSearch
{   

	public File[] listFilesAsArray(File directory, FilenameFilter[] filter,
									   int recurse) {
			Collection<File> files = listFiles(directory, filter, recurse);

			File[] arr = new File[files.size()];
			return files.toArray(arr);
		}

		public Collection<File> listFiles(File directory,
										  FilenameFilter[] filter, int recurse) {

			Vector<File> files = new Vector<File>();

			File[] entries = directory.listFiles();

			if (entries != null) {
				for (File entry : entries) {
					for (FilenameFilter filefilter : filter) {
						if (filter == null
                            || filefilter
							.accept(directory, entry.getName())) {
							files.add(entry);
							Log.v("MediaFileSearch", "Added: "
								  + entry.getName());
						}
					}
					if ((recurse <= -1) || (recurse > 0 && entry.isDirectory())) {
						recurse--;
						files.addAll(listFiles(entry, filter, recurse));
						recurse++;
					}
				}
			}
			return files;
		}
	
}
