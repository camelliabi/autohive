package badtrain;

import java.util.*;

import com.mindscapehq.raygun4java.core.RaygunClient;

import java.io.File;
import java.io.IOException;

public class Main {
	ArrayList<Station> stations = new ArrayList<Station>();
	Set<TrainLine> lines = new HashSet<TrainLine>();
	Map<TrainLine, List<TrainService>> lineServices = new HashMap<>();

	public Main() {
		loadAll();
		userInterface();
	}

	public void userInterface() {
		RaygunClient client = new RaygunClient("1rS8GbPdmDlVsMI2DbxQ");
		try {
			Scanner scMain = new Scanner(System.in);

			// User input:
			System.out.println("Enter 1: Stations of a train line; ");
			System.out.println("Enter 2: Train lines that go through a station; ");
			System.out.println("Enter 3: Train lines that goes from Station A to Station B; ");
			System.out.println("Enter 4: Next train service for each line at a station; ");
			System.out.println("Enter 5: Next direct trip from Station A to Station B; ");
			System.out
					.println("Enter 6: Plan trip: Station A -> B, depart after a given time (Allow exchange); ");
			System.out
					.println("Enter 7: Plan trip: Station A -> B, arrive before a given time (Allow exchange); ");
			System.out.println(
					"Enter 8: Plan trip: Station A -> B -> A, depart after a given time (with exchange, cancel & delay);");
			System.out.println(
					"Enter 9: Plan trip: Station A -> B -> A, return before a given time (with exchange, cancel & delay);");

			System.out.print("Enter number: ");
			if (!scMain.hasNextInt()) {
				System.out.println("Please only input integer. ");
				return;
			}
			int n = scMain.nextInt();

			if (n == 1)
				lineInfo();
			else if (n == 2)
				stationInfo();
			else if (n == 3)
				findLine();
			else if (n == 4)
				findNextTrain();
			else if (n == 5)
				findNextDirectTrip();
			else if (n == 6)
				planTripBetween();
			else if (n == 7)
				planTripBefore();
			else if (n == 8)
				tripAfterCancel();
			else if (n == 9)
				tripBeforeCancel();

			else
				System.out.println("Input incorrect. ");

			System.out.println("------------------------------------");
			System.out.println("Enter 1: Cancel service and redo 1-9; ");
			System.out.println("Enter 2: Delay service and redo 1-9; ");
			System.out.println("Enter 0: return to user interface; ");
			System.out.print("Enter number: ");

			if (!scMain.hasNextInt()) {
				System.out.println("Please only input integer. ");
				return;
			}
			int m = scMain.nextInt();
			if (m == 0)
				userInterface();
			else if (m == 1) {
				cancelServiceLine();
				userInterface();
			} else if (m == 2) {
				delayServiceLine();
				userInterface();
			} else {
				System.out.println("Input incorrect. ");
				userInterface();
			}
		} catch (Exception e) {
			client.send(e);
		}

	}

	// Cancel service
	public void cancelServiceLine() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Line Name: ");
		String lineName = scanner.nextLine();
		System.out.print("Enter Station Name: ");
		String stationName = scanner.nextLine();
		System.out.print("Service Time at Station: ");
		int time = scanner.nextInt();
		cancelService(lineByName(lineName), stationByName(stationName), time);
	}

	public void cancelService(TrainLine line, Station s, int time) {
		List<TrainService> existing = lineServices.get(line);
		if (existing == null) {
			System.out.println("No services found for line: " + (line == null ? "<null>" : line.getName()));
			return;
		}

		ArrayList<TrainService> services = new ArrayList<>(existing);
		boolean removed = services.removeIf(ser -> line.timeOfService(s, ser) == time);
		if (removed) {
			System.out.println("Service at " + time + " cancelled.");
		} else {
			System.out.println("No matching service to cancel.");
		}
		lineServices.put(line, services);
	}

	// Delay Service
	public void delayServiceLine() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Line Name: ");
		String lineName = scanner.nextLine();
		System.out.print("Enter Station Name: ");
		String stationName = scanner.nextLine();
		System.out.print("Service Time at Station: ");
		int time = scanner.nextInt();
		System.out.print("Delayed time (hhmm): ");
		int delayed = scanner.nextInt();

		delayService(lineByName(lineName), stationByName(stationName), time, delayed);
	}

	public void delayService(TrainLine line, Station s, int time, int delayed) {
		List<TrainService> existing = lineServices.get(line);
		if (existing == null) {
			System.out.println("No services found for line: " + (line == null ? "<null>" : line.getName()));
			return;
		}

		ArrayList<TrainService> services = new ArrayList<TrainService>();
		for (TrainService serv : existing) {
			services.add(serv);
		}

		for (TrainService ser : existing) {
			if (line.timeOfService(s, ser) == time) {

				int delayedIndex = services.indexOf(ser);

				ArrayList<Integer> newTimes = new ArrayList<Integer>();
				for (int t : ser.getTimes()) {

					newTimes.add(addTime(t, delayed));
				}

				TrainService newServ = new TrainService(line);
				if (!newTimes.isEmpty()) {
					newServ.addTime(newTimes.get(0), true);
					for (int i = 1; i < newTimes.size(); i++) {
						newServ.addTime(newTimes.get(i), false);
					}
				}
				services.set(delayedIndex, newServ);

				System.out.println(
						"Service " + ser + " has a " + delayed + " minutes delay.");
			}
		}
		lineServices.put(line, services);
	}

	// Plan return trip BEFORE
	public void tripBeforeCancel() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter start station: ");
		String startName = scanner.nextLine();
		System.out.print("Enter destination station: ");
		String destName = scanner.nextLine();
		System.out.print("Time spent at destination(hhmm): ");
		int stayTime = scanner.nextInt();
		System.out.print("Return before: ");
		int timeBefore = scanner.nextInt();
		Station start = stationByName(startName);
		Station dest = stationByName(destName);

		planReturnTripBefore(start, dest, stayTime, timeBefore);

		// Allow cancel below
		System.out.println("Any service cancelled or delayed? 1 = cancelled, 2 = delayed, 0 = no");
		System.out.println("Input number: ");
		int num = scanner.nextInt();

		if (num == 1) {
			cancelServiceLine();
			System.out.println("--- Replanned Trip ---");
			planReturnTripBefore(start, dest, stayTime, timeBefore);
		} else if (num == 2) {
			delayServiceLine();
			System.out.println("--- Replanned Trip ---");
			planReturnTripBefore(start, dest, stayTime, timeBefore);
		}

	}

	public void planReturnTripBefore(Station start, Station dest, int stayTime, int timeBefore) {
		// return trip
		Trip bestR = findBestTripBefore(dest, start, timeBefore);
		if (bestR == null) {
			System.out.println("No trip found.");
			return;
		}

		int timeArrBefore = addTime(bestR.departTime, ((-1) * stayTime));

		Trip bestTo = findBestTripBefore(start, dest, timeArrBefore);
		if (bestTo == null) {
			System.out.println("No trip found.");
			return;
		}

		for (int i = bestTo.sections.size() - 1; i >= 0; i--) {
			if (i + 1 < bestTo.sections.size() && bestTo.sections.get(i).line != bestTo.sections.get(i + 1).line) {
				System.out.println("[Exchange here]");
			}

			System.out.println(bestTo.sections.get(i).line.getName() + ": " + bestTo.sections.get(i).A.getName() + " "
					+ bestTo.sections.get(i).timeA + " - " + bestTo.sections.get(i).B.getName() + " "
					+ bestTo.sections.get(i).timeB);
		}
		System.out.println("Arriving at destination before: " + timeArrBefore);

		System.out.println("--- Return Trip ---");
		for (int i = bestR.sections.size() - 1; i >= 0; i--) {
			if (i + 1 < bestR.sections.size() && bestR.sections.get(i).line != bestR.sections.get(i + 1).line) {
				System.out.println("[Exchange here]");
			}

			System.out.println(bestR.sections.get(i).line.getName() + ": " + bestR.sections.get(i).A.getName() + " "
					+ bestR.sections.get(i).timeA + " - " + bestR.sections.get(i).B.getName() + " "
					+ bestR.sections.get(i).timeB);
		}

	}

	// Plan return trip AFTER, cancel and delay allowed
	public void tripAfterCancel() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter start station: ");
		String startName = scanner.nextLine();
		System.out.print("Enter destination station: ");
		String destName = scanner.nextLine();
		System.out.print("Time spent at destination (hhmm): ");
		int stayTime = scanner.nextInt();

		System.out.print("Depart after: ");
		int timeAfter = scanner.nextInt();

		Station start = stationByName(startName);
		Station dest = stationByName(destName);

		planReturnTripAfter(start, dest, stayTime, timeAfter);

		// Allow cancel & delay below
		System.out.println("Any service cancelled or delayed? 1 = cancelled, 2 = delayed, 0 = no");
		System.out.println("Input number: ");
		int num = scanner.nextInt();
		if (num == 1) {
			cancelServiceLine();
			System.out.println("--- Replanned Trip ---");
			planReturnTripAfter(start, dest, stayTime, timeAfter);
		} else if (num == 2) {
			delayServiceLine();
			System.out.println("--- Replanned Trip ---");
			planReturnTripAfter(start, dest, stayTime, timeAfter);
		}
	}

	public void planReturnTripAfter(Station start, Station dest, int stayTime, int timeAfter) {

		Trip bestTo = findBestTripAfter(start, dest, timeAfter);
		if (bestTo == null) {
			System.out.println("No trip found.");
			return;
		}

		int timeRStartAfter = addTime(bestTo.finalTime, stayTime);// return trip starts after

		// return trip
		Trip bestR = findBestTripAfter(dest, start, timeRStartAfter);

		for (int i = 0; i < bestTo.sections.size(); i++) {
			System.out.println(bestTo.sections.get(i).line.getName() + ": " + bestTo.sections.get(i).A.getName() + " "
					+ bestTo.sections.get(i).timeA + " - " + bestTo.sections.get(i).B.getName() + " "
					+ bestTo.sections.get(i).timeB);
			if (i + 1 < bestTo.sections.size() && bestTo.sections.get(i).line != bestTo.sections.get(i + 1).line) {
				System.out.println("[Exchange here]");
			}
		}

		System.out.println("Return trip after: " + timeRStartAfter);

		System.out.println("--- Return ---");

		if (bestR == null) {
			System.out.println("No return trip found.");
			return;
		}

		for (int i = 0; i < bestR.sections.size(); i++) {

			System.out.println(bestR.sections.get(i).line.getName() + ": " + bestR.sections.get(i).A.getName() + " "
					+ bestR.sections.get(i).timeA + " - " + bestR.sections.get(i).B.getName() + " "
					+ bestR.sections.get(i).timeB);
			if (i + 1 < bestR.sections.size() && bestR.sections.get(i).line != bestR.sections.get(i + 1).line) {
				System.out.println("[Exchange here]");
			}
		}
	}

	// Find best trip between two stations BEFORE a given time
	public void planTripBefore() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter start station: ");
		String startName = scanner.nextLine();
		System.out.print("Enter destination station: ");
		String destName = scanner.nextLine();
		System.out.print("Arrival time before: ");
		int timeBefore = scanner.nextInt();

		Station start = stationByName(startName);
		Station dest = stationByName(destName);

		Trip bestT = findBestTripBefore(start, dest, timeBefore);
		if (bestT == null) {
			System.out.println("No trip found.");
			return;
		}

		for (int i = bestT.sections.size() - 1; i >= 0; i--) {
			if (i + 1 < bestT.sections.size() && bestT.sections.get(i).line != bestT.sections.get(i + 1).line) {
				System.out.println("[Exchange here]");
			}

			System.out.println(bestT.sections.get(i).line.getName() + ": " + bestT.sections.get(i).A.getName() + " "
					+ bestT.sections.get(i).timeA + " - " + bestT.sections.get(i).B.getName() + " "
					+ bestT.sections.get(i).timeB);
		}

		System.out.println("Fare zone number: " + Math.abs(start.getZone() - dest.getZone()));

	}

	public Trip findBestTripBefore(Station start, Station dest, int timeBefore) {
		Map<Station, Integer> bestTimeBefore = new HashMap<Station, Integer>();

		Trip currentT = new Trip();

		return bestTripBefore(start, dest, timeBefore, bestTimeBefore, currentT, null);
	}

	public Trip bestTripBefore(Station start, Station currentS, int earlierThan, Map<Station, Integer> bestTime, Trip currentTrip,
			Trip best) {
		// starting Station
		if (currentS == start) {
			int departure;
			if (currentTrip.sections.size() == 0) {
				departure = earlierThan;
			} else {
				departure = currentTrip.sections.get(currentTrip.sections.size() - 1).timeA;
			}

			if (best == null || departure > best.departTime) {
				best = new Trip(currentTrip.sections, departure, 9999);
			}
			return best;
		}

		if (bestTime.containsKey(currentS) && bestTime.get(currentS) >= earlierThan) {
			return best;
		}

		bestTime.put(currentS, earlierThan);

		for (TrainLine line : currentS.getTrainLines()) {
			int iCurrent = line.indexOfStation(currentS);

			for (int j = iCurrent - 1; j >= 0; j--) {
				Station prevS = line.getStations().get(j);

				Section sec = bestSectionBefore(line, prevS, currentS, earlierThan);

				if (best != null && sec != null && sec.timeA <= best.departTime)
					continue;

				currentTrip.sections.add(sec); // add section to trip, try for best trip
				if (sec != null)
					best = bestTripBefore(start, prevS, sec.timeA, bestTime, currentTrip, best);

				currentTrip.sections.remove(currentTrip.sections.size() - 1); // if not best, remove section from trip
			}
		}
		return best;

	}

	public Section bestSectionBefore(TrainLine line, Station a, Station b, int timeBefore) {
		int indexA = line.indexOfStation(a);
		int indexB = line.indexOfStation(b);
		if (indexA < 0 || indexB < 0 || indexA >= indexB)
			return null;

		Section bestSec = null;
		int bestTimeA = 0000;

		List<TrainService> services = lineServices.get(line);
		if (services == null) {
			return null;
		}

		for (TrainService serv : services) {
			int timeA = line.timeOfService(a, serv);
			int timeB = line.timeOfService(b, serv);

			if (timeA > 0 && timeA < timeB && timeB <= timeBefore && timeA >= bestTimeA) {
				bestTimeA = timeA;
				bestSec = new Section(a, b, line, serv, bestTimeA, timeB);
			}
		}
		return bestSec;
	}

	// Find best trip between two stations AFTER a given time
	public void planTripBetween() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter start station: ");
		String startName = scanner.nextLine();
		System.out.print("Enter destination station: ");
		String destName = scanner.nextLine();
		System.out.print("Departure time after: ");
		int timeAfter = scanner.nextInt();

		Station start = stationByName(startName);
		Station dest = stationByName(destName);

		Trip bestT = findBestTripAfter(start, dest, timeAfter);
		if (bestT == null) {
			System.out.println("No trip found.");
			return;
		}

		for (int i = 0; i < bestT.sections.size(); i++) {
			if (i - 1 >= 0 && bestT.sections.get(i).line != bestT.sections.get(i - 1).line) {
				System.out.println("[Exchange here]");
			}
			System.out.println(bestT.sections.get(i).line.getName() + ": " + bestT.sections.get(i).A.getName() + " "
					+ bestT.sections.get(i).timeA + " - " + bestT.sections.get(i).B.getName() + " "
					+ bestT.sections.get(i).timeB);
		}
		System.out.println("Fare zone number: " + Math.abs(start.getZone() - dest.getZone()));
	}

	public Trip findBestTripAfter(Station start, Station dest, int timeAfter) {
		Map<Station, Integer> bestTime = new HashMap<Station, Integer>();

		Trip currentT = new Trip();

		return bestTrip(start, dest, timeAfter, bestTime, currentT, null);
	}

	public Trip bestTrip(Station currentS, Station dest, int laterThan, Map<Station, Integer> bestTime, Trip currentTrip,
			Trip best) {

		if (currentS == dest) {
			int arrival;
			if (currentTrip.sections.size() == 0) { // start = dest
				arrival = laterThan;
			} else {
				arrival = currentTrip.sections.get(currentTrip.sections.size() - 1).timeB;
			}

			if (best == null || arrival < best.finalTime) {
				best = new Trip(currentTrip.sections, 0, arrival);
			}
			return best;
		}

		if (bestTime.containsKey(currentS) && bestTime.get(currentS) <= laterThan) {
			return best; // if current station has a better arriving time, abandon current try
		}

		bestTime.put(currentS, laterThan);

		for (TrainLine line : currentS.getTrainLines()) {
			int iCurrent = line.indexOfStation(currentS);

			for (int j = iCurrent + 1; j < line.getStations().size(); j++) {
				Station nextS = line.getStations().get(j);

				Section sec = bestSection(line, currentS, nextS, laterThan);

				// if section arrives later than best arrival, abandon this try
				if (best != null && sec != null && sec.timeB >= best.finalTime)
					continue;

				currentTrip.sections.add(sec); // add section to trip, try for best trip
				if (sec != null)
					best = bestTrip(nextS, dest, sec.timeB, bestTime, currentTrip, best);
				currentTrip.sections.remove(currentTrip.sections.size() - 1); // if not successful, remove section from trip
			}
		}
		return best;
	}

	// best trip without exchange; best section of a trip after given time

	public Section bestSection(TrainLine line, Station a, Station b, int timeAfter) {
		int indexA = line.indexOfStation(a);
		int indexB = line.indexOfStation(b);
		if (indexA < 0 || indexB < 0 || indexA >= indexB)
			return null;

		Section bestSec = null;
		int bestTimeB = 9999;

		List<TrainService> services = lineServices.get(line);
		if (services == null) {
			return null;
		}

		for (TrainService serv : services) {
			int timeA = line.timeOfService(a, serv);
			int timeB = line.timeOfService(b, serv);

			if (timeA > 0 && timeA < timeB && timeA >= timeAfter && timeB <= bestTimeB) {
				bestTimeB = timeB;
				bestSec = new Section(a, b, line, serv, timeA, bestTimeB);
			}
		}
		return bestSec;
	}

	// Find the next trip between stations
	public void findNextDirectTrip() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter start station: ");
		String startName = scanner.nextLine();
		System.out.print("Enter destination station: ");
		String destName = scanner.nextLine();

		System.out.print("Current Time: ");
		int currentTime = scanner.nextInt();

		for (TrainLine line : lines) {

			int i = line.getStations().stream().map(sta -> sta.getName()).toList().indexOf(startName);
			int j = line.getStations().stream().map(sta -> sta.getName()).toList().indexOf(destName);
			if (i < j && i >= 0) {
				System.out.println(line.getName());

				Station a = line.getStations().get(i);
				Station b = line.getStations().get(j);

				int timeIndex = -1;
				for (int time : line.getTimeTable().get(a)) {
					if (time >= currentTime) {
						System.out.println("Departure time: " + time);
						timeIndex = line.getTimeTable().get(a).indexOf(time);
						break;
					}
				}

				if (timeIndex >= 0) {
					System.out.println("Arriving time: " + line.getTimeTable().get(b).get(timeIndex));
				} else {
					System.out.println("No service after " + currentTime);
				}
				System.out.println("Number of fare zones: " + Math.abs(a.getZone() - b.getZone()));
			}
		}
	}

	// Find the next train at a station
	public void findNextTrain() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter station: ");
		String stationName = scanner.nextLine();

		System.out.print("Current Time: ");
		int currentTime = scanner.nextInt();

		for (Station s : stations) {
			if (s.getName().contains(stationName)) {

				for (TrainLine line : s.getTrainLines()) {
					Integer nextTrainTime = null;
					System.out.print(line.getName() + " - ");
					for (int t : line.getTimeTable().get(s)) {
						if (t >= currentTime) {
							nextTrainTime = t;
							break;
						}
					}
					if (nextTrainTime != null) {
						System.out.println("Next train arriving at: " + nextTrainTime);
					} else {
						System.out.println("No train after: " + currentTime);
					}
				}
			}
		}
	}

	// Find train line by start & destination
	public void findLine() {
		boolean found = false;
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter current station: ");
		String startName = scanner.nextLine();

		System.out.print("Enter Destination: ");
		String destName = scanner.nextLine();

		for (TrainLine l : lines) {
			if (lineContains(l, startName, destName)) {
				System.out.println(l.getName());
				found = true;
			}
		}
		if (!found)
			System.out.println("No direct Train Line connection");
	}

	public boolean lineContains(TrainLine line, String startName, String destName) {
		int startIndex = -1;
		int destIndex = -1;
		for (Station s : line.getStations()) {
			if (s.getName().equals(startName)) {
				startIndex = line.getStations().indexOf(s);
			}
			if (s.getName().equals(destName)) {
				destIndex = line.getStations().indexOf(s);
			}
		}
		return startIndex < destIndex && startIndex >= 0;
	}

	// Find all train lines that pass a station
	public void stationInfo() {
		RaygunClient client = new RaygunClient("1rS8GbPdmDlVsMI2DbxQ");
		try {
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter Station: ");
			String staName = scanner.nextLine();
			for (Station s : stations) {
				if (s.getName().equals(staName)) {
					System.out.println(s.getTrainLines());
				}
			}
		} catch (Exception e) {
			client.send(e);
		}
	}

	// Find all stations a train line passes
	public void lineInfo() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter Train Line: ");
		String lineName = scanner.nextLine();
		for (TrainLine l : lines) {
			if (l.getName().equals(lineName)) {
				System.out.println(l.getStations());
			}
		}

	}

	// Load documents
	public void loadAll() {
		loadStations();
		loadLines();
		for (TrainLine l : lines) {
			loadLineStations(l);
			loadService(l);
			l.mapStationTime();
		}

		for (TrainLine line : lines) {
			lineServices.put(line, line.getTrainServices());
		}

		System.out.println("All stations:");
		System.out.println(stations);
		System.out.println("All train lines:");
		System.out.println(lines);
		System.out.println("--------------------------------");
	}

	public void loadStations() {
		try {
			Scanner scStations = new Scanner(new File("stations.data"));
			while (scStations.hasNext()) {
				String name = scStations.next();
				int zone = scStations.nextInt();
				double distance = scStations.nextDouble();
				Station s = new Station(name, zone, distance);
				stations.add(s);
			}
		} catch (IOException e) {
			System.out.printf("File failure %s\n", e);
		}
	}

	public void loadLines() {
		try {
			Scanner scLines = new Scanner(new File("train-lines.data"));
			while (scLines.hasNext()) {
				String name = scLines.next();
				TrainLine l = new TrainLine(name);
				lines.add(l);
			}

		} catch (IOException e) {
			System.out.printf("File failure %s\n", e);
		}
	}

	public void loadLineStations(TrainLine l) {
		String docName = l.getName() + "-stations.data";
		try {
			Scanner scLineSta = new Scanner(new File(docName));
			while (scLineSta.hasNext()) {
				String name = scLineSta.next();
				for (Station s : stations) {
					if (s.getName().equals(name)) {
						l.addStation(s);
						s.addTrainLine(l);
					}
				}
			}
		} catch (IOException e) {
			System.out.printf("File failure %s\n", e);
		}
	}

	public void loadService(TrainLine l) {
		RaygunClient client = new RaygunClient("1rS8GbPdmDlVsMI2DbxQ");
		String docName = l.getName() + "-services.data";
		try {
			Scanner sc = new Scanner(new File(docName));
			while (sc.hasNextLine()) {
				TrainService serv = new TrainService(l); // one new service for each line
				Scanner scServ = new Scanner(sc.nextLine());
				if (scServ.hasNextInt()) {
					serv.addTime(scServ.nextInt(), true);
					while (scServ.hasNextInt()) {
						serv.addTime(scServ.nextInt(), false);
					}
					l.addTrainService(serv);
				}
			}
		} catch (IOException e) {
			client.send(e);
			System.out.printf("File failure %s\n", e);
		}
	}

	// Reused methods
	public Station stationByName(String name) {
		for (Station s : stations) {
			if (s.getName().equals(name))
				return s;
		}
		throw new IllegalArgumentException("Unknown station: " + name);

	}

	public TrainLine lineByName(String name) {
		for (TrainLine l : lines) {
			if (l.getName().equals(name))
				return l;
		}

		System.out.println("No line found");
		return null;
	}

	public int addTime(int t1, int t2) {
		int minutes1 = (t1 / 100) * 60 + (t1 % 100);
		int minutes2 = (t2 / 100) * 60 + (t2 % 100);

		int totalMinutes = (minutes1 + minutes2) % (24 * 60);
		if (totalMinutes < 0)
			totalMinutes += 24 * 60;

		int hours = totalMinutes / 60;
		int minutes = totalMinutes % 60;
		return hours * 100 + minutes;
	}

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			new RaygunClient("1rS8GbPdmDlVsMI2DbxQ").send(new Exception(throwable));

		});

		new Main();
	}
}
