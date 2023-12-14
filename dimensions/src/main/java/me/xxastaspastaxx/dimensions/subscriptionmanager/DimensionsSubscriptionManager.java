package me.xxastaspastaxx.dimensions.subscriptionmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsDebbuger;

public class DimensionsSubscriptionManager {

	private Dimensions pl;
	private String serverUnique;
	private boolean FULL_ACCESS = false;
	private boolean DEVELOPMENT_ACCESS = false;
	
	private final String VALIDATION_URL = "https://astaspasta.alwaysdata.net/verifyPatreon.php?serverUnique=%1$s";
	
	public DimensionsSubscriptionManager(Dimensions main) {
		this.pl = main;
		
		serverUnique = sha256(Bukkit.getServer().getIp()+"!@#"+Bukkit.getServer().getName()+"#@!"+Bukkit.getServer().getName()+"@!#"+Bukkit.getPort());
		validateSubscriptionOnline(null);
		
		if (!FULL_ACCESS) {
			DEVELOPMENT_ACCESS = Bukkit.getServer().getOnlinePlayers().size() <= 10;
		}
	}
	
	private void validateSubscriptionOnline(CommandSender sender) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(getValidateSubscriptionURL()).openStream(), "UTF-8"));
			Gson gson = new Gson();
			try {
				HashMap<String, String> mapString = gson.fromJson(reader.readLine(), new TypeToken<HashMap<String, String>>() {}.getType());
				
				FULL_ACCESS = mapString.get("valid").equals("true");
			} catch (IllegalStateException e) {
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (sender!=null)
					sender.sendMessage("§7Server's response: §8Subscription valid is "+FULL_ACCESS);
				else 
					DimensionsDebbuger.DEBUG.print("§7Server's response: §8Subscription valid is "+FULL_ACCESS);
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void validateSubscriptionOnlineAsync(CommandSender sender) {
		Bukkit.getScheduler().runTaskAsynchronously(pl, new Runnable() {
			
			@Override
			public void run() {
				validateSubscriptionOnline(sender);
			}
		});
	}
	
	public String getValidateSubscriptionURL() {
		return String.format(VALIDATION_URL, new Object[] {serverUnique});
	}
	
	
	private String sha256(final String base) {
	    try{
	        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        final byte[] hash = digest.digest(base.getBytes("UTF-8"));
	        final StringBuilder hexString = new StringBuilder();
	        for (int i = 0; i < hash.length; i++) {
	            final String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) 
	              hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    } catch(Exception ex){
	       throw new RuntimeException(ex);
	    }
	}

	public void stopDevelopmentMode() {
		DEVELOPMENT_ACCESS = false;
		if (!FULL_ACCESS && Dimensions.getAddonManager()!=null) {
			Dimensions.getAddonManager().unloadAll();
		}
	}

	public boolean canLoadAddons() {
		return FULL_ACCESS || DEVELOPMENT_ACCESS;
	}

	public boolean isOnlyDevMode() {
		return DEVELOPMENT_ACCESS && !FULL_ACCESS;
	}

	public boolean isFullAccess() {
		return FULL_ACCESS;
	}
	
	public boolean isDevMode() {
		return DEVELOPMENT_ACCESS;
	}
}
