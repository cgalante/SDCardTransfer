package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.os.storage.*;
import java.io.File;
import org.apache.commons.io.*;
import java.io.*;
import android.util.*;
import java.text.*;
import java.util.*;
import javax.security.auth.*;
import android.content.res.*;

public class MainActivity extends Activity
{
	private static final int PROGRESS = 0x1;
	MainActivity mActivity;
 	private ProgressBar mProgress;
	private int mProgressStatus = 0;
//	TextView loading;	
	Handler mHandler;	
	
    boolean mExternalStorageAvailable = false; 
	boolean mExternalStorageWriteable = false; 

	public static String[] DIRECTORY_DCIM = {"/DCIM,","/dcim","/SD/dcim","/MicroSD/dcim","/SD/DCIM","/MicroSD/DCIM"};
	public static String[] DIRECTORY_DCIM2 = {"/DCIM","/dcim"};
	
	public static String PATH = "";
	public static String DEST = "/sdcard";
	public static String[] ALTPATH = {"/Removable","/sdcard2"};
	private static File EXTERNAL_STORAGE_DESTINATION_DIRECTORY = getDirectory("EXTERNAL_STORAGE2",DEST );
    private static File EXTERNAL_STORAGE_DIRECTORY = getDirectory("EXTERNAL_STORAGE3",PATH);
	
	EditText editText;
	EditText editDestText;
	
	CharSequence text; 
	
	int duration = Toast.LENGTH_SHORT;
	
	private File DCIMFile;
	private File DCIMDestFile;
	
 
  /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
	{
      
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      
	    Context context = getApplicationContext();		
        
		checkSourcePath();
		
		mProgress = (ProgressBar) findViewById(R.id.progress_bar);
	    mActivity = this;	
        retrieveImageDir();
    	
		editText = (EditText)findViewById(R.id.sdcardsourceentry); 
		editText.setText(PATH, TextView.BufferType.EDITABLE);  
		editDestText = (EditText)findViewById(R.id.destinationentry); 
		editDestText.setText(DEST, TextView.BufferType.EDITABLE);  

		final Button cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {

				public void onClick(View p1)
				{
					// TODO: Implement this method
					finish();
					System.exit(0);
					
				}

		
		});
		
	
		final Button button = (Button) findViewById(R.id.ok);
		button.setOnClickListener(new View.OnClickListener() {
				
				
				public void onClick(View v) {
					// Perform action on click
					Context context = getApplicationContext();
		            boolean isFullPath = false;
					boolean isAvailableDCIMPATH = false;
					DEST = editDestText.getText().toString();
					PATH = editText.getText().toString();		       
					EXTERNAL_STORAGE_DESTINATION_DIRECTORY = getDirectory("EXTERNAL_STORAGE2",DEST );
					EXTERNAL_STORAGE_DIRECTORY = getDirectory("EXTERNAL_STORAGE3",PATH);
					File file = null;
					
					for(int i = 0;i<DIRECTORY_DCIM.length;i++) {
						String fullPath = PATH + DIRECTORY_DCIM[i];
						file = new File((PATH + DIRECTORY_DCIM[i]));
						if(file.exists()) {
							isAvailableDCIMPATH =true;
							Toast isAvailableToast = Toast.makeText(context, "This directory availability is: " + isAvailableDCIMPATH + " " + DIRECTORY_DCIM[i] + " " + PATH, 10000); 
							isAvailableToast.show();
				            break;
	    			}
				
						
			      }
					
					
		//		if(isAvailableDCIMPATH==false) {
			//			DIRECTORY_DCIM = {PATH, ""};
				//	}
					retrieveImageDir();
					Toast isAvailableToast = Toast.makeText(context, "This directory DCIMFIL path is: " + DCIMFile.toString(), 10000); 
					isAvailableToast.show();
					
				    List<String> fTypes	= getFileTypes(file.getAbsolutePath(),isFullPath);
					
					
					if (fTypes.size() > 0) {
						Log.d("onclick-FirstImage","First Value: " + fTypes.get(0).toString());
						Toast copyToast = Toast.makeText(context,"First Value: " + fTypes.get(0).toString() , 10000); 
						copyToast.show();
				    	
				        File root = getDirectory("Directory",DEST);
								
					    mProgress.setMax((int)FileUtils.sizeOfDirectory(DCIMFile));
//					 
						if (root.getFreeSpace() > FileUtils.sizeOfDirectory(DCIMFile)) {
						    onOk();
							
						} else {
							//Replace is Dialogbox
							Toast sizeToast = Toast.makeText(context, "There is no space left on you tablets disk." , 10000); 
							sizeToast.show();
							Toast comparesizeToast = Toast.makeText(context, "The size of the picture dump is " + FileUtils.sizeOfDirectory(DCIMFile) + "And the size remaining in the destination drive is " + root.getFreeSpace() + "." , 10000); 
							comparesizeToast.show();
							
						}
						
					 } else {
						Toast copyToast = Toast.makeText(context, "There are no pictures on: " + EXTERNAL_STORAGE_DIRECTORY, 10000); 
						copyToast.show();
						
					} 
					
				}
			});
			
    }
    public void setPATH(String changedPath) {
		
	}
    public void onOk() {
		Context context = getApplicationContext();		
		checkstate();
    	try
		{
		    
			Log.d("Checking SDCard State","Checkstate: " + new Boolean(checkstate()).toString());
			Toast checktoast = Toast.makeText(context, "Checkstate: " + new Boolean(checkstate()).toString(), duration);
			checktoast.show();
	 
			copyToDest();
	        
		} catch (Exception e) {
			Toast toast = Toast.makeText(context, "SHIT!!!!", duration);
			toast.show();	
		}
		
	}                                                       
    public boolean checkstate() {
		String state = Environment.getExternalStorageState();
		Context context = getApplicationContext(); 
		
		if (Environment.MEDIA_MOUNTED.equals(state)) { 
			// We can read and write the media 
			mExternalStorageAvailable = mExternalStorageWriteable = true; 
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) { 
			// We can only read the media 
			mExternalStorageAvailable = true; 
			mExternalStorageWriteable = false; 
		} 
		else { // Something else is wrong. It may be one of many other states, but all we need 
			// to know is we can neither read nor write 

			mExternalStorageAvailable = mExternalStorageWriteable = false; 
		}
		return mExternalStorageAvailable;
	}
	

	
	public int copyToDest() {
			
		
	    //Context context = getApplicationContext();	
		
   

	    mHandler = new Handler();
		
		Context context = getApplicationContext();		
		
		   	DCIMDestFile.setWritable(true);
		    DCIMDestFile.setReadable(true);
			DCIMDestFile.setExecutable(true);
			
	    	Log.d("copyToDest()","WRITABLE: SOURCE " + DCIMFile.toString() + " DESTINATION: " + DCIMDestFile.toString());
			Toast copyToast = Toast.makeText(context,"WRITABLE: SOURCE " + DCIMFile.toString() + " DESTINATION: " + DCIMDestFile.toString() , 10000); 
			copyToast.show();
		//    loading = (TextView)findViewById(R.id.loading);
	    	//loading.setVisibility(0);
		
		    
			// Start lengthy operation in a background thread
		    new Thread(new Runnable() {
		    		public void run() {
						while (mProgressStatus < 1) {
							
							try
							{
								FileUtils.copyDirectory(DCIMFile, DCIMDestFile, true);
								
								
							}
							catch (IOException e)
							{}
                            mProgressStatus = 1;
							// Update the progress bar
							mHandler.post(new Runnable() {
									public void run() {
										mProgress.setProgress(mProgress.getMax());
						
									}
							});
							
					     }
				    }
	              }).start();	
		
		new Thread(new Runnable() {
				public void run() {
					
				    while (mProgressStatus <= (int)FileUtils.sizeOfDirectory(DCIMFile)) {
						
					   mProgress.setProgress((int)FileUtils.sizeOfDirectory(DCIMDestFile));
						
						if ((int)FileUtils.sizeOfDirectory(DCIMFile)==(int)FileUtils.sizeOfDirectory(DCIMDestFile)) {
							
							Looper.prepare(); 
							mActivity.initCompletedBox();
							Looper.loop();
							Looper.myLooper().quit();
							//finish();
							//System.exit(0);
						}
						
		
					}
					
					
				}
				
			}).start();				
		
		
		return 1;
	}
	public void retrieveImageDir() {

		Context context = getApplicationContext();		
		
		DCIMFile = getExternalStoragePublicDirectory(DIRECTORY_DCIM);
	    //Context context = getApplicationContext();			

		Log.d("Is the external folder writable","WRITABLE: SOURCE " + DCIMFile.toString());
		Toast copyToast = Toast.makeText(context,"WRITABLE: SOURCE " + DCIMFile.toString(), 10000); 
		copyToast.show();

		DCIMDestFile = getExternalStoragePublicDestinationDirectory(DIRECTORY_DCIM2);

    }	
	public static File getExternalStoragePublicDirectory(String[] type) {
        File file = null;
		
		for(int i=0; i < type.length; i++) {
			file = new File(getExternalStorageDirectory(), type[i]);
			if (file.exists()) {
				break;
			}
		}
		
		return file;
    }
	public static File getExternalStorageDirectory() {
        return EXTERNAL_STORAGE_DIRECTORY;
    }
	public static File getExternalStoragePublicDestinationDirectory(String[] type) {
		File file = null;
	    Date dtm = new Date();

	    Format sdf = new SimpleDateFormat("MMddyyyy-hh:mm:ss");
		//	DIRECTORY_DCIM2 = {"/dcim/"+ sdf.format(dtm),"/DCIM/" + sdf.format(dtm)};
		
		for(int i=0; i < type.length; i++) {
			file = new File(getExternalStorageDestinationDirectory(), type[i] + "/" + sdf.format(dtm));
			if (file.exists()) {
				break;
			}
		}

		return file;
    }
	public static File getExternalStorageDestinationDirectory() {
        return EXTERNAL_STORAGE_DESTINATION_DIRECTORY;
    }
	
	static File getDirectory(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? new File(defaultPath) : new File(path);
    }  
	
	private List<String> getFileTypes(String DIRECTORY, boolean fullPath) {
		final List<String> tFileList = new ArrayList<String>();


    	//The below needs to be passed in
		Resources resources = getResources();
        // array of valid audio file extensions
		String[] imageTypes = resources.getStringArray(R.array.images);

    	FilenameFilter[] filter = new FilenameFilter[imageTypes.length];

		int i = 0;
        for (final String type : imageTypes) {
            filter[i] = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("." + type);
                }
            };
            i++;
        }

        MediaFileSearch fileUtils = new MediaFileSearch();
        File[] allMatchingFiles = fileUtils.listFilesAsArray(
			new File(DIRECTORY), filter, -1);
        for (File f : allMatchingFiles) {
            if (fullPath) {
                tFileList.add(f.getAbsolutePath());
            }
            else {
                tFileList.add(f.getName());
            }
        }
		
        return tFileList;
    }
 public void initCompletedBox() {
	 // prepare the alert box
	 AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

	 // set the message to display
	 alertbox.setMessage("Card to drive transfer completed successfully!");

	 // add a neutral button to the alert box and assign a click listener
	 alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

			 // click listener on the alert box
			 public void onClick(DialogInterface arg0, int arg1) {
				 // the button was clicked
				 finish();
				 System.exit(0);
			 }
		 });

	 // show it
	 alertbox.show();
 }	
 private void checkSourcePath() {
	 File file = null;
     
	 for(int i=0; i < ALTPATH.length; i++) {
		 file = new File(ALTPATH[i]);
		 if (file.exists()) {
			 PATH = ALTPATH[i];
		 }
	 }

	 
 }
}
