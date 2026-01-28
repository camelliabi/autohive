package badtrain;
// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN502 assignment.
// You may not distribute it in any other way without permission.

// Code for SWEN502, Assignment W2

import java.util.*;

/**
 * TrainLine
 * Information about a Train Line.
 * Note, we treat the outbound train line as a different from the inbound line.
 * This means that the Johnsonville-Wellington line is a different train line from 
 * the Wellington-Johnsonville line.
 * Although they have the same stations, the stations will be in opposite orders.
 *
 * A TrainLine contains 
 * - the name of the TrainLine (originating station - terminal station, eg Wellington-Melling)
 * - The list of stations on the line
 * - a list of TrainServices running on the line (eg the 10:00 am service from Upper-Hutt to Wellington)
 *   (in order of time - services earlier in the list are always earlier times (at any station) than later services  )
 */

public class TrainLine{
    //Fields
    private String name;
    private List<Station> stations = new ArrayList<Station>();             // list of stations on the line
    private List<TrainService> trainServices = new ArrayList<TrainService>(); // set of TrainServices running on the line
    private Map<Station, List<Integer>> timeTableByStation = new HashMap<Station, List<Integer>>();
    
    //Constructor
    public TrainLine(String name){
        this.name = name;
    }

    
    // Methods to add values to the TrainLine
    
    //map stations to arriving time
    public void mapStationTime() {
    	
		int index = -1;
		for(Station s : stations) {
			index = getStations().indexOf(s);
			
			List<Integer> times = new ArrayList<Integer>(); // times at this station
			
			for(TrainService serv : trainServices) {
				times.add(serv.getTimes().get(index));
			}
			
			timeTableByStation.put(s, times);
		}
    }
    
    
    /**
     * Add a TrainService to the set of TrainServices for this line
     */
   
    public void addTrainService(TrainService train){
        trainServices.add(train);
    }

    /**
     * Add a Station to the list of Stations on this line
     */
    public void addStation(Station station){
        stations.add(station);
    }

    //Getters
    public String getName(){
        return name;
    }

    public List<Station> getStations(){
        return Collections.unmodifiableList(stations); // an unmodifiable version of the list of stations
    }

    public List<TrainService> getTrainServices(){
        return Collections.unmodifiableList(trainServices); // an unmodifiable version of the list of trainServices
    }
    
    public Map<Station, List<Integer>> getTimeTable(){
    	return timeTableByStation;
    }

    //Reused methods for planning trips
    public boolean containsStation(Station s) {
    	return stations.contains(s);
    }
    
    public int indexOfStation(Station s) {
    	return stations.indexOf(s);
    }
    
    public int timeOfService(Station s, TrainService serv) {
    	int index = indexOfStation(s);
    	if(index < 0 || index >= serv.getTimes().size()) return -1;
    	return serv.getTimes().get(index);
    	
    }
    
    /**
     * String contains name of the train line name plus number of stations and number of services
     */
    public String toString(){
        return (name+" ("+stations.size()+" stations, "+trainServices.size()+" services)");
    }

}
