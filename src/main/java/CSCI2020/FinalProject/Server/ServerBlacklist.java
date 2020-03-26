package CSCI2020.FinalProject.Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ServerBlacklist {
	public static void ReloadBlacklist() {
		//Empty the blacklist
		blacklist.clear();
		
		try {
			//Open file with buffered reader
			BufferedReader fileInput = new BufferedReader(new FileReader("Blacklist.txt"));
			
			//Read line-by-line, adding to blacklist.
			String line = fileInput.readLine();
			while (line != null) {
				blacklist.add(line);
				line = fileInput.readLine();
			}
			
			fileInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean IsBlacklisted(String _address) {
		System.out.println(String.format("Searching blacklist for '%s'", _address));
		for (String s : blacklist) {
			//Check if the full form address (/ip:port) contains a blacklisted ip address
			if (_address.contains(s)) {
				return true;
			}
		}
		return false;
	}
	
	private static ArrayList<String> blacklist = new ArrayList<String>();
}
