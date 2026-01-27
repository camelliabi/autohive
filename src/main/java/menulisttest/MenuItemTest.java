package menulisttest;

import java.util.HashMap;
import java.util.Map;

public class MenuItemTest {
	
	private int itemId;
	private String itemName;
	private String itemDesc;
	private double itemPrice;
	private boolean isSoldout;
	//picture for item
	private String itemPictureUrl;

	Map<String, Double> options = new HashMap<>();
	Map<String, Double> notes = new HashMap<>();
	
	
	public MenuItemTest() {
		
	}

	public MenuItemTest(int itemId, String itemName, double itemPrice) {
		this.itemId = itemId;
		this.itemName = itemName;	
		this.itemPrice = itemPrice;
	}

	public MenuItemTest(int itemId, String itemName, double itemPrice, Map<String, Double> options, Map<String, Double> notes) {
		this.itemId = itemId;
		this.itemName = itemName;	
		this.itemPrice = itemPrice;
		this.options = options;
		this.notes = notes;
	}
	
	//getters and setters
	
	public int getItemId() {
		return itemId;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public String getItemDesc() {
		return itemDesc;
	}
	
	public double getItemPrice() {
		return itemPrice;
	}
	
	public boolean isSoldout() {
		return isSoldout;
	}
	
	public void setItemId(int id) {
		this.itemId = id;
	}
	
	public void setItemDesc(String desc) {
		this.itemDesc = desc;
	}
	
	public void setItemPrice(double price) {
		this.itemPrice = price;
	}
	
	public void setSoldout(boolean soldout) {
		this.isSoldout = soldout;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemPictureUrl() {
		return itemPictureUrl;
	}

	public void setItemPictureUrl(String itemPictureUrl) {
		this.itemPictureUrl = itemPictureUrl;
	}

	public Map<String, Double> getOptions() {
		return options;
	}
	public void setOptions(Map<String, Double> options) {
		this.options = options;
	}
	public Map<String, Double> getNotes() {
		return notes;
	}
	public void setNotes(Map<String, Double> notes) {
		this.notes = notes;
	}
	public void addOption(String optionName, double optionPrice) {
		this.options.put(optionName, optionPrice);
	}
	public void addNote(String noteName, double notePrice) {
		this.notes.put(noteName, notePrice);
	}
}