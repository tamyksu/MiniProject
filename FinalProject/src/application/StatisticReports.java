package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;

public class StatisticReports  implements Initializable {
	public static StatisticReports instance;
	
	public Double medianALL(ArrayList<Integer> arr)
	{


	{
	
		if(!arr.isEmpty())
		{
			
	
			if(arr.size()%2==1)
			{
				
			return arr.get((arr.size()/2))/1.0;
			
			}
			else
			{
				int sum=arr.get(arr.size()/2)+arr.get(arr.size()/2-1);
				return (((Double)(sum/2.0)));
				
			}
		}
		return 0.0;
			
			
		}}
	
	
	public Double standard_deviation(ArrayList<Integer> arr)
	{
		
		
		if(!arr.isEmpty())
		{
		
		int sum=0;
		for(int i=0;i<arr.size();i++) 
		{
			
			sum+=arr.get(i);
		}
		double avg=sum/arr.size();
		sum=0;
		for(int i=0;i<arr.size();i++) 
		{
			
			sum+=Math.pow(arr.get(i)-avg,2);
			System.out.println("active sum what in root"+sum);
		}
		 return (Math.sqrt(sum/arr.size()));
		}
		return 0.0;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		instance = this;	
	}

}
