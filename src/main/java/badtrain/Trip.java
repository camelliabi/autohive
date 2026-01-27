package badtrain;
import java.util.*;

public class Trip {
	public List<Section> sections = new ArrayList<Section>();
	public int finalTime;
	public int departTime;
		
	
	public Trip() {
		this.sections = new ArrayList<>();
		this.finalTime = 9999;
		this.departTime = 0000;
	}
	
	
	public Trip(List<Section> sections, int departTime, int finalTime) {
		this.sections = new ArrayList<>(sections);
		this.departTime = departTime;
		this.finalTime = finalTime;
	}
	

	
	
}
