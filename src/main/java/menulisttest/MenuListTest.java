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
        
        // FIXED: Initialize MenuListTest properly instead of setting to null
        MenuListTest test = new MenuListTest();
        
        try {
            test.deleteItem(item);
            System.out.println("Item deletion attempted successfully");
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getClass().getName());
            client.send(e);
        }
        
        System.out.println("MenuListTest completed without crashes!");
    }

}

