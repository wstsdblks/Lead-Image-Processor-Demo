/**
 * 
 */
package com.leadtools.imageprocessingframentui;

import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import leadtools.imageprocessing.RasterCommand;

/**
 * @author LawrenceSL
 *
 */
public class ProcessListFragment extends ListFragment {

	List<RasterCommand> commands;
	private String[] processorList = {"Invert","AutoBinarize","Deskew"};
	
	@Override
	public void onCreate(Bundle savedInstanceState)	{
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)	{
		
		super.onActivityCreated(savedInstanceState);
		
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_activated_1,processorList
		));
		
	}
	
	public void onListItemClick(ListView list, View view, int pos, long id)	{
		Log.d("Process App",String.format("Position: %s",pos));
	}
	
	
	public interface OnImageProcessorListUpdatedListener	{
		public void onImageProcessListUpdated(List<RasterCommand> commands);
	}

	
	
}





















