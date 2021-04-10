//Reekoye Gopal
//
//07/04/2021
//
//UID.java
//
//

package data;
import java.util.ArrayList;
import java.util.Collections;

//Handles generation of unique id's to identify each client
public class UID
{
	public static int RANGE = 10000;
	public static ArrayList<Integer> ids = new ArrayList<Integer>();
	public static int index = 0;

	//fill array with id's
	//
	static
	{
		for(int i=0; i< RANGE ; i++)
		{
			ids.add(i);
		}
		//shuffle the list
		//
		Collections.shuffle(ids);
	}

	public static int getIdentifier()
	{
		if(index>ids.size()-1)index =0;
		return ids.get(index++);
	}
}
