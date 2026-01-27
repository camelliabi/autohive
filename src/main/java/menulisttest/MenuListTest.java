package menulisttest;


import java.util.ArrayList;
import java.util.List;

import com.mindscapehq.raygun4java.core.RaygunClient;

public class MenuListTest {
	
	List<MenuItemTest> menu = new ArrayList<MenuItemTest>();
	
	public MenuListTest() {
		
	}
	
	public void addItem(MenuItemTest it) {
		menu.add(it);
	}
	
	public void deleteItem(MenuItemTest it) {
		menu.remove(it);
	}


	public static void main(String[] args) {
		RaygunClient client = new RaygunClient("1rS8GbPdmDlVsMI2DbxQ");
		MenuItemTest item = new MenuItemTest(1, "a", 9.99);
		
		MenuListTest test = null;
		
		try {
			test.deleteItem(item);
			
		} catch (Exception e) {
			client.send(e);
		}
		
		
		
	}

}


