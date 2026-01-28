package badtrain;

//error: missing import
import java.util.*;

public class Trip {
	public ArrayList<Section> sections = new ArrayList<Section>();
	public int finalTime;
	public int departTime;
		
	
	public Trip() {
		this.sections = new ArrayList<>();
		
		//error: type
		this.finalTime = 9999;
		this.departTime = 0000;
	}
	
	
	public Trip(List<Section> sections, int departTime, int finalTime) {
		this.sections = new ArrayList<>(sections);
		this.departTime = departTime;
		this.finalTime = finalTime;
	}
	
	
}
