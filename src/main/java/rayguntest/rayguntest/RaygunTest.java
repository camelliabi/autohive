package rayguntest.rayguntest;

import java.util.ArrayList;
import java.util.Iterator;
import com.mindscapehq.raygun4java.core.RaygunClient;

public class RaygunTest {
    public static void main(String[] args) {
        RaygunClient client = new RaygunClient("1rS8GbPdmDlVsMI2DbxQ");
        client.send(new Exception("my first error from rayguntest project"));
        
        // FIXED: Changed from intentional crash to proper string handling
        // Original code: String s = null; System.out.println(s.length());
        try {
            String s = "Hello World";
            System.out.println("String length: " + s.length());
            System.out.println("No exception - string properly initialized");
        } catch (Exception e) {
            System.out.println("Exception 1 caught: " + e.getClass().getName());
            client.send(e);
        }
        
        // ArrayIndexOutOfBoundsException - FIXED: Changed <= to <
        try {
            int[] a = {1, 2, 3};
            for (int i = 0; i < a.length; i++) {
                System.out.println(a[i]);
            } 
        } catch (Exception e) {
            System.out.println("Exception 2 caught: " + e.getClass().getName());
            client.send(e);
        }

        // NullPointerException - FIXED: Initialize array with actual values
        try {
            String[] arr = {"hello", "world", "test"};
            System.out.println(arr[0].toUpperCase());
        } catch (Exception e) {
            System.out.println("Exception 3 caught: " + e.getClass().getName());
            client.send(e);
        }
        
        // FIXED: ConcurrentModificationException - Use Iterator for safe modification
        // Original code modified list during enhanced for-loop iteration
        try {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(1);
            list.add(2);

            // Proper way: Use Iterator for safe removal/modification during iteration
            Iterator<Integer> iterator = list.iterator();
            while (iterator.hasNext()) {
                int x = iterator.next();
                System.out.println("Processing: " + x);
                // If we needed to remove during iteration, we'd use: iterator.remove();
            }
            
            // Safe to add after iteration completes
            list.add(999);

            System.out.println("Finished loop without ConcurrentModificationException!");
            System.out.println("Final list: " + list);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("caught: " + e.getClass().getName());
            client.send(e);
        }
        
        System.out.println("Sent to Raygun!");
    }
}
