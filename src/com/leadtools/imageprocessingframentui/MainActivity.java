package com.leadtools.imageprocessingframentui;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import com.leadtools.imageprocessingframentui.ProcessListFragment.OnImageProcessorListUpdatedListener;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.media.*;

import leadtools.ILeadStream;
import leadtools.LeadStreamFactory;
import leadtools.RasterImage;
import leadtools.RasterSupport;
import leadtools.codecs.RasterCodecs;
import leadtools.controls.ImageViewerPanZoomInteractiveMode;
import leadtools.controls.ImageViewerSizeMode;
import leadtools.controls.RasterImageViewer;

import leadtools.demos.DeviceUtils;
import leadtools.demos.OpenFileDialog;
import leadtools.demos.Utils;
import leadtools.imageprocessing.RasterCommand;

public class MainActivity extends Activity implements OnImageProcessorListUpdatedListener {

	private RasterImageViewer mViewer;
	private RasterCodecs codecs;
	private RasterImage image;
	private ProcessListFragment listFragment;
	private static final int IMAGE_GALLERY = 0x0001;
	private static final int IMAGE_CAPTURE = 0x0002;
	private boolean imageLoaded = false;
	private static final String CAPTURED_IMAGE_TEMP_DIRECTORY = Environment.getExternalStorageDirectory() + "/LEADTOOLS IMAGE PROCESSING DEMO/";
	private Uri mImageCaptureUri;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		RasterSupport.initialize(this);
		try {
			mViewer = (RasterImageViewer) findViewById(R.id.imageViewer);
			codecs = new RasterCodecs(Utils.getSharedLibsPath(this));
			
			InputStream path = getAssets().open("loader.png");
					
			image = codecs.load(LeadStreamFactory.create(path,true));
			
			if(image != null)			
				mViewer.setImage(image);
			else
				Toast.makeText(getParent(),"Null Image", Toast.LENGTH_LONG).show();
			
		} catch (Exception e) {
			Toast.makeText(getParent(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		addFragment();
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId() == R.id.load_image_gallery){
			Intent gallery = new Intent(Intent.ACTION_PICK,Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(gallery,IMAGE_GALLERY);
		}
		else if(item.getItemId() == R.id.load_image_camera){
			Utils.createDirectory(CAPTURED_IMAGE_TEMP_DIRECTORY);
			
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mImageCaptureUri = Utils.getExtFileUri("", ".jpg", CAPTURED_IMAGE_TEMP_DIRECTORY);
			camera.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);			
			startActivityForResult(camera,IMAGE_CAPTURE);
			
		}
		
		else if(item.getItemId() == R.id.load_image_system){
			OpenFileDialog.OnFileSelectedListener onFileSelectedListener = new OpenFileDialog.OnFileSelectedListener() {
				
			@Override
			public void onFileSelected(String fileName) {
				File file = new File(fileName);
					if(file.exists())	{
						loadImage(Uri.fromFile(file));							
					}
				}				
			};
			
			OpenFileDialog ofd = new OpenFileDialog(this,Utils.getSupportedImagesFormatFilter(),onFileSelectedListener);
			ofd.show();
		}
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode == RESULT_OK)	{
			if(requestCode == IMAGE_GALLERY)	{
				Uri imageURI = data.getData();
				loadImage(imageURI);

			}
			else if(requestCode == IMAGE_CAPTURE){
				File file = new File(mImageCaptureUri.getPath());
				if(file.exists())
					loadImage(mImageCaptureUri);
			}
		}
	}
	
	private void loadImage(Uri imageUri){
		try	{
			ILeadStream stream = LeadStreamFactory.create(getContentResolver().openInputStream(imageUri),true);
			
			if(codecs == null)
				return;
			else	{
				image = codecs.load(stream);
				mViewer.setImage(image);
				imageLoaded = true;
				mViewer.setSizeMode(ImageViewerSizeMode.FIT_WIDTH);
				mViewer.setTouchInteractiveMode(new ImageViewerPanZoomInteractiveMode());
			}
				
		}
		catch(Exception ex) {
               Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show(); 
        }
	}
	
	public void addFragment(){
		
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		
		listFragment = new ProcessListFragment();
		
		transaction.add(R.id.fragmentContainer, listFragment);
		
		transaction.commit();
	}

	@Override
	public void onImageProcessListUpdated(List<RasterCommand> commands) {
		// TODO Auto-generated method stub
		Log.d("Process App", commands.toString());
	}
	
}

















