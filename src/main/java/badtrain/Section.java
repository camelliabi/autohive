package badtrain;

public class Section {
	public Station A;
	public Station B;
	public TrainLine line;
	public TrainService serv;
	public int timeA;
	public int timeB;
	
	public Section(Station start, Station dest, TrainLine line, TrainService serv, int timeA, int timeB){
		this.A = start;
		this.B = dest;
		this.line = line;
		this.serv = serv;
		this.timeA = timeA;
		this.timeB = timeB;	
	}	
	
	
}
