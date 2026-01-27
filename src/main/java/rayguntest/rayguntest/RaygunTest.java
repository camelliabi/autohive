package rayguntest.rayguntest;




import com.mindscapehq.raygun4java.core.RaygunClient;

public class RaygunTest {
    public static void main(String[] args) {
        RaygunClient client = new RaygunClient("1rS8GbPdmDlVsMI2DbxQ");
        client.send(new Exception("my first error from rayguntest project"));
        
        //null pointer
        try {
            String s = null;
            System.out.println(s.length()); 
            
            String[] arr = new String[5];
            System.out.println(arr[0]);
            
        } catch (Exception e) {
            client.send(e);
            System.out.println("Exception sent to Raygun");
        }
        
        
        System.out.println("Sent to Raygun!");
        
    }
}
