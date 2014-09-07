package io.github.rob__.performancer.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import io.github.rob__.performancer.R;

public class InfoCPU {
	
	List<String> info;
	ArrayAdapter<String> adapter;
	
	SharedPreferences prefs;
	public void init(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(!prefs.contains("coreCount")){ prefs.edit().putInt   ("coreCount",   getCoreCount())                 .apply(); }
		if(!prefs.contains("maxFreq"))  { prefs.edit().putInt   ("maxFreq",     getMaxFreq())                   .apply(); }
		if(!prefs.contains("minFreq"))  { prefs.edit().putInt   ("minFreq",     getMinFreq())                   .apply(); }
		if(!prefs.contains("os.arch"))  { prefs.edit().putString("os.arch",     System.getProperty("os.arch"))  .apply(); }
		if(!prefs.contains("model"))    { prefs.edit().putString("model",       getModel())                     .apply(); }
	}
	
	/**
	 * Returns OS name.
	 */
	public String getOSName(){
		return System.getProperty("os.name");
	}
	
	/**
	 * Returns OS version
	 */
	public String getOSVersion(){
		return System.getProperty("os.version");
	}
	
	/**
	 * Returns OS architecture.
	 */
	public String getOSArch(){
		return System.getProperty("os.arch");
	}
	
	/**
     * Returns a SystemProperty
     *
     * @param propName The property to retrieve
     * @return Property value (null if n/a).
     */
    public static String getSystemProperty(String propName) {
        String pvalue;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            pvalue = input.readLine();
            input.close();
        }
        catch (IOException e) {
            // Can't read shell output.
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pvalue;
    }
    
    /**
     * Returns cpu information (parsed).
     */
	public ArrayAdapter<String> populateListView(Context context){
    	if(prefs.getBoolean("advanced", false)){
    		adapter = null;
			info = new ArrayList<String>();
			RandomAccessFile reader = null;
    		try {
        		reader = new RandomAccessFile("/proc/cpuinfo", "r");
        		String line;
        		while ((line = reader.readLine()) != null) {
        			if(!line.equals("")) {
						info.add(line);
					}
        		}
        		reader.close();
        		adapter = new ArrayAdapter<String>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
        		return adapter;
    		} catch (IOException e) {
        		e.printStackTrace();
    		}
    		return adapter;
    	} else {
			// not advance
    		adapter = null;
			info = new ArrayList<String>();
			
			info.add("Cores: " + String.valueOf(prefs.getInt("coreCount", getCoreCount())));
			int core = 0;
			int coreOnline = 0;
			int online = getCoreOnlineCount();
			do{
				//info.add("  > Core " + String.valueOf(core) + ": Online, " + String.valueOf(usageCPU.getCpuUsage(core)) + "%");
				info.add("  > Core " + String.valueOf(core) + ": Online");
				coreOnline++;
				core++;
			}while(coreOnline != online);
			do{
				info.add("  > Core " + String.valueOf(core) + ": Offline");
				core++;
			}while(core != getCoreCount());
			info.add("Max Clock Speed: "    + String.valueOf(prefs.getInt("maxFreq", getMaxFreq())) + " MHz");
			info.add("Min Clock Speed: "    + String.valueOf(prefs.getInt("minFreq", getMinFreq())) + " MHz");
			info.add("Architecture: "       + prefs.getString("os.arch", System.getProperty("os.arch")));
			info.add("Model: "              + prefs.getString("model", getModel()));
			
			adapter = new ArrayAdapter<String>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
	    	return adapter;
    	}
    }
	
	/**
	 * Get model.
	 */
	public String getModel(){
		RandomAccessFile reader = null;
		String line;
		try {
			reader = new RandomAccessFile("/proc/cpuinfo", "r");
			while ((line = reader.readLine()) != null) {
				if(line.contains("processor")){
					reader.readLine();
				} else if(!line.equals("") && !line.contains("implementer") && !line.contains("architecture") && !line.contains("variant") && !line.contains("part") && !line.contains("Revision") && !line.contains("revision") && !line.contains("Serial") && !line.contains("Features") && !line.contains("Processor")) {
					line.replace("Hardware: ", "");
					reader.close();
					return line;
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
    
    /**
     * Gets the number of cores in the device.
     * 
     */
    public int getCoreCount(){
    	int cores = 0;
    	do{
    		cores++;
    	}while(new File("/sys/devices/system/cpu/cpu" + String.valueOf(cores)).exists());
    	return cores;
    }
    
    /**
     * Gets the max clock rate of the CPUs (in MHz)
     * 
     */
    public int getMaxFreq(){
    	try {
			RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
			int freq = Integer.parseInt(reader.readLine()) / 1000;
			reader.close();
			return freq;
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
    	return 0;
    }
    
    /**
     * Gets the min clock rate of the CPUs (in MHz)
     * 
     */
    public int getMinFreq(){
    	try {
			RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq", "r");
			int freq = Integer.parseInt(reader.readLine()) / 1000;
			reader.close();
			return freq;
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
    	return 0;
    }
    
    /**
     * Checks all cores and checks how many are online.
     * 
     */
    public int getCoreOnlineCount(){
    	RandomAccessFile reader;
    	try{
    		int online = 0;
    		int x = 0;
    		do{
    			reader = new RandomAccessFile("/sys/devices/system/cpu/cpu" + String.valueOf(x) + "/online", "r");
    			if(Integer.parseInt(reader.readLine()) == 1){
    				online++;
    			}
    			reader.close();
    			x++;
    		}while(x != getCoreCount());
    		reader.close();
    		return online;
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	return 0;
    }
    
    /**
     * Gets CPU temperature.
     */
    public double getCpuTemp(){
    	RandomAccessFile reader;
    	try{
    		reader = new RandomAccessFile("/sys/devices/virtual/thermal/thermal_zone13/temp", "r");
    		double temp = Double.parseDouble(reader.readLine());
    		reader.close();
    		return temp;
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	return 0.0;
    }
    
    /**
     * Get cpu usage.
     * 
     */
    public int getCpuPercentage(){
    	try{
    		Process process = Runtime.getRuntime().exec("top -m 1 -n 1 -d 1");
    		InputStreamReader isr = new InputStreamReader(process.getInputStream());
    		BufferedReader reader = new BufferedReader(isr);
    		String line;
    		while ((line = reader.readLine()) != null) {
				if(line.contains("%")){
					String[] info = line.split(" ");
		    		int user    = Integer.parseInt(info[1].replace("%", "").replace(",", ""));
		    		int system  = Integer.parseInt(info[3].replace("%", "").replace(",", ""));
		    		int IOW     = Integer.parseInt(info[5].replace("%", "").replace(",", ""));
		    		int IRQ     = Integer.parseInt(info[7].replace("%", "").replace(",", ""));
                    // parsed cpu info via shell top w/o recursion
                    // handler posts 2000 ms due to delay between exec and output
		    		return user + system + IOW + IRQ;
				}
			}
    		process.destroy();
    		isr.close();
    		reader.close();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	return 0;
    }
}
