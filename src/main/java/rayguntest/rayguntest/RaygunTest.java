package rayguntest.rayguntest;

import java.util.ArrayList;
import com.mindscapehq.raygun4java.core.RaygunClient;

public class RaygunTest {
    public static void main(String[] args) {
        RaygunClient client = new RaygunClient("1rS8GbPdmDlVsMI2DbxQ");
        client.send(new Exception("my first error from rayguntest project"));
        
        // NullPointerException example
        try {
            String s = null;
            System.out.println(s.length()); 
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
        
        // ConcurrentModificationException
        try {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(1);
            list.add(2);

            for (int x : list) {
                list.add(999);
            }

            System.out.println("finished loop (no exception?)");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("caught: " + e.getClass().getName());
            client.send(e);
        }
        
        System.out.println("Sent to Raygun!");
    }
}
