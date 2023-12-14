package me.xxastaspastaxx.dimensions.commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalLoader;

public class PortalsCommand extends DimensionsCommand implements Listener {

	private Inventory mainInventory;
	private HashMap<Player, Inventory> portalsInventory = new HashMap<Player, Inventory>();
	private HashMap<Player, Inventory> browseInventory = new HashMap<Player, Inventory>();
	
	private ArrayList<CachedPortal> cachedPortals = new ArrayList<CachedPortal>();
	private long lastUpdate = 0;
	
	private Gson gson;
	
	public PortalsCommand(String command, String args, String[] aliases, String description, String permission, boolean adminCommand) {
		super(command,args,aliases,description, permission, adminCommand);
		
		setupMenu();
		
		gson = new GsonBuilder().create();
		
		Bukkit.getPluginManager().registerEvents(this, Dimensions.getInstance());
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (sender instanceof Player) {
			((Player) sender).openInventory(mainInventory);
		} else {
			String msg = "§7[§cDimensions§7] Portals list:";
			for (CustomPortal portal : Dimensions.getCustomPortalManager().getCustomPortals()) {
				msg +="\n["+(portal.isEnabled()?"§aEnabled":"§cDisabled")+"§7] "+ portal.getPortalId();
			}
			
			sender.sendMessage(msg);
		}
	}
	
	private void setupMenu() {

		mainInventory = Bukkit.createInventory(null, 9, "§cDimensions");
		
		ItemStack decor = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta decorMeta = decor.getItemMeta();
		decorMeta.setDisplayName("§6");
		decor.setItemMeta(decorMeta);
		
		for (int i=0;i<9;i++) mainInventory.setItem(i, decor);
		
		ItemStack myPortals = new ItemStack(Material.PAPER);
		ItemMeta myPortalsMeta = myPortals.getItemMeta();
		myPortalsMeta.setDisplayName("§6My Portals");
		myPortalsMeta.setLore(Arrays.asList(new String[] {"§7Click to view your portals"}));
		myPortals.setItemMeta(myPortalsMeta);
		
		mainInventory.setItem(3, myPortals);
		
		ItemStack browsePortals = new ItemStack(Material.CHEST);
		ItemMeta browsePortalsMeta = browsePortals.getItemMeta();
		browsePortalsMeta.setDisplayName("§bBrowse portals online");
		browsePortalsMeta.setLore(Arrays.asList(new String[] {"§7Click to browse portals online", "§7Use §nShift+Click§7 to forcfully load portals"}));
		browsePortals.setItemMeta(browsePortalsMeta);
		
		mainInventory.setItem(5, browsePortals);
		
	}
	
	private void updatePortalsMenu(Player p, int page) {
		ArrayList<CustomPortal> portals = Dimensions.getCustomPortalManager().getCustomPortals();
		
		Inventory inv = Bukkit.createInventory(null, 54, "My portals | Page §4"+(page+1)+"§c/"+((int)Math.ceil(portals.size()/45f)));
		
		for (int i = page*45;i<(page+1)*45;i++) {
			if (i>=portals.size()) break;
			CustomPortal portal = portals.get(i);
			ItemStack portalItem = new ItemStack(portal.getOutsideMaterial());
			ItemMeta itemMeta = portalItem.getItemMeta();
			itemMeta.setDisplayName("§6"+portal.getPortalId());
			itemMeta.setLore(Arrays.asList(new String[] {"§6§l"+portal.getDisplayName(), "§7Click for more details"}));
			portalItem.setItemMeta(itemMeta);
			inv.addItem(portalItem);
		}
		
		ItemStack decor = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta decorMeta = decor.getItemMeta();
		decorMeta.setDisplayName("§6");
		decor.setItemMeta(decorMeta);

		for (int i=46;i<=52;i++) inv.setItem(i, decor);
		
		ItemStack goBack = new ItemStack(page==0?Material.BARRIER:Material.ARROW);
		ItemMeta goBackMeta = goBack.getItemMeta();
		goBackMeta.setDisplayName("§6Previous page");
		goBackMeta.setLore(Arrays.asList(new String[] {"§7Go to page "+((int)Math.max(1, page))}));
		goBack.setItemMeta(goBackMeta);
		inv.setItem(45, goBack);
		
		ItemStack goMprosta = new ItemStack((page+1)*45>=portals.size()?Material.BARRIER:Material.ARROW);
		ItemMeta goMprostaMeta = goBack.getItemMeta();
		goMprostaMeta.setDisplayName("§6Next page");
		goMprostaMeta.setLore(Arrays.asList(new String[] {"§7Go to page "+((int)Math.min(Math.ceil(portals.size()/45f), page+2))}));
		goMprosta.setItemMeta(goMprostaMeta);
		inv.setItem(53, goMprosta);
		
		portalsInventory.put(p, inv);
		p.openInventory(inv);
	}
	
	private void updateBrowseInventory(Player p, int page, boolean forceUpdate) {

		Inventory inv = Bukkit.createInventory(null, 54, "Browse portals | Page §4"+(page+1)+"§c/"+((int)Math.ceil(cachedPortals.size()/45f)));
		
		if (forceUpdate || System.currentTimeMillis()-lastUpdate>=108000000) {
			p.closeInventory();
			lastUpdate = System.currentTimeMillis();
			cachedPortals.clear();
			p.sendMessage("Fetching portals...");
			
			HashMap<String, HashMap<String, Object>> portals = null;
			
			try {
				portals = gson.fromJson(readStringFromURL("https://astaspasta.alwaysdata.net/api/portalData.php?all="+p.getUniqueId().toString().replace("-", "")),new TypeToken<HashMap<String, HashMap<String, Object>>>() {}.getType());
			} catch (JsonSyntaxException | IOException e) {
				p.sendMessage("There was an issue while trying to fetch portals");
				e.printStackTrace();
				return;
			}
			
			portals.forEach((id, map) -> cachedPortals.add(CachedPortal.create(id, map)));
		}
		
		
		for (int i = page*45;i<(page+1)*45;i++) {
			if (i>=cachedPortals.size()) break;
			CachedPortal portal = cachedPortals.get(i);
			inv.addItem(portal.getItemStack());
		}
		
		
		ItemStack decor = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta decorMeta = decor.getItemMeta();
		decorMeta.setDisplayName("§6");
		decor.setItemMeta(decorMeta);

		for (int i=46;i<=52;i++) inv.setItem(i, decor);
		
		ItemStack goBack = new ItemStack(page==0?Material.BARRIER:Material.ARROW);
		ItemMeta goBackMeta = goBack.getItemMeta();
		goBackMeta.setDisplayName("§6Previous page");
		goBackMeta.setLore(Arrays.asList(new String[] {"§7Go to page "+((int)Math.max(1, page))}));
		goBack.setItemMeta(goBackMeta);
		inv.setItem(45, goBack);
		
		ItemStack goMprosta = new ItemStack((page+1)*45>=cachedPortals.size()?Material.BARRIER:Material.ARROW);
		ItemMeta goMprostaMeta = goBack.getItemMeta();
		goMprostaMeta.setDisplayName("§6Next page");
		goMprostaMeta.setLore(Arrays.asList(new String[] {"§7Go to page "+((int)Math.min(Math.ceil(cachedPortals.size()/45f), page+2))}));
		goMprosta.setItemMeta(goMprostaMeta);
		inv.setItem(53, goMprosta);

		p.openInventory(inv);
		browseInventory.put(p, inv);
	}
	
	public static String readStringFromURL(String requestURL) throws IOException
	{
	    try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
	            StandardCharsets.UTF_8.toString()))	
	    {
	        scanner.useDelimiter("\\A");
	        return scanner.hasNext() ? scanner.next() : "";
	    }
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClick(InventoryClickEvent e) {
		if (e.getCurrentItem()==null || e.getInventory()==null || !(e.getWhoClicked() instanceof Player)) return;
		ItemStack item = e.getCurrentItem();
		Player p = (Player) e.getWhoClicked();
		if (e.getInventory().equals(mainInventory)) {
			e.setCancelled(true);
			if (e.getClickedInventory()!=e.getInventory()) return;
			if (item.getType()==Material.PAPER) updatePortalsMenu(p,0);
			if (item.getType()==Material.CHEST) if (p.hasPermission("dimensions.forceupdatebrowser")) {updateBrowseInventory(p, 0, e.isShiftClick());} else { p.sendMessage("§7[§cDimensions§7] §cYou do not have the §ndimensions.forceupdatebrowser§c permission to do perform that action");};
		} else if (portalsInventory.containsKey(p) && e.getInventory().equals(portalsInventory.get(p))) {
			e.setCancelled(true);
			if (e.getClickedInventory()!=e.getInventory()) return;
			String name = item.getItemMeta().getDisplayName();
			if (name.equalsIgnoreCase("§6")) {
				return;
			} else if (name.contentEquals("§6Previous page") || name.contentEquals("§6Next page")) {
				updatePortalsMenu(p,Integer.parseInt(item.getItemMeta().getLore().get(0).substring(13))-1);
				return;
			} else {
				CustomPortal portal = Dimensions.getCustomPortalManager().getCustomPortal(item.getItemMeta().getDisplayName().substring(2));
				if (portal!=null) {
					p.sendMessage("§7[§cDimensions§7] "+portal.getDisplayName()+":§7 Is built from §c"+portal.getOutsideMaterial()+"§7, is ignited using §c"+portal.getLighterMaterial()+"§7 and goes to §c"+portal.getWorld().getName()+"§7.");
				} else {
					p.sendMessage("§7[§cDimensions§7] There was a problem, please try reloading the plugin.");
				}
			}
		} else if (browseInventory.containsKey(p) && e.getInventory().equals(browseInventory.get(p))) {
			e.setCancelled(true);
			if (e.getClickedInventory()!=e.getInventory()) return;
			String name = item.getItemMeta().getDisplayName();
			if (name.equalsIgnoreCase("§6")) {
				return;
			} else if (name.contentEquals("§6Previous page") || name.contentEquals("§6Next page")) {
				updateBrowseInventory(p,Integer.parseInt(item.getItemMeta().getLore().get(0).substring(13))-1, false);
				return;
			} else {
				CachedPortal cached = cachedPortals.stream().filter(portal -> name.equals("§6"+portal.getFile())).findAny().orElseGet(null);
				if (cached!=null)
					if (e.isShiftClick()) {
						try {
							if (cached.download(p)) {
								p.sendMessage("§7[§cDimensions§7] §aThe portal has been succesfully downloaded, please use §n/dim reload§a to apply changes.");
							}
						} catch (IOException e1) {
							p.sendMessage("§7[§cDimensions§7] §cThere was an issue while trying to download the file.");
							e1.printStackTrace();
						}
					} else {
						p.sendMessage("§7[§cDimensions§7] §aLink to portal: "+cached.getLink());
					}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClose(InventoryCloseEvent e) {
		if (e.getInventory().equals(portalsInventory.get(e.getPlayer()))) {
			portalsInventory.remove(e.getPlayer());
		}
		
		if (e.getInventory().equals(browseInventory.get(e.getPlayer()))) {
			browseInventory.remove(e.getPlayer());
		}
	}
	
}

final class CachedPortal {
	
	private String id;
	private String file;
	private String creator;
	private int likes;
	private Material block;
	private String yml;
	
	private CachedPortal(String id, String file, String creator, int likes, Material block, String yml) {
		this.id = id;
		this.file = file;
		this.creator = creator;
		this.likes = likes;
		this.block = block;
		this.yml = yml;
	}
	
	public String getFile() {
		return file;
	}
	
	public ItemStack getItemStack() {
		ItemStack item = new ItemStack(block);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6"+file);
		meta.setLore(Arrays.asList(new String[] {"§6by §n"+creator, "§6§n"+likes+"§6 likes", "", "§6Click to get link to portal","§6Shift+Click to download portal","","§6§oportal id: "+id}));
		item.setItemMeta(meta);
		return item;
	}
	
	public String getLink() {
		return "https://astaspasta.alwaysdata.net/editor/portal/?portal="+id;
	}
	
	public boolean download(Player p) throws IOException {
		
		File f = new File(CustomPortalLoader.DIRECTORY_PATH+"/"+file+".yml");
		if (f.exists()) {
			p.sendMessage("§7[§cDimensions§7] A portal with the same name already exists.");
			return false;
		}
		
		Path file = f.toPath();
		Files.write(file, Arrays.asList(new String[] {yml}), Charset.forName("UTF-8"));
		
		return true;
	}
	
	public static CachedPortal create(String id, HashMap<String, Object> map) {
		return new CachedPortal(id,
				(String) map.get("file"),
				(String) map.get("creator"),
				(int) ((double) map.get("likes")),
				Material.valueOf(((String) map.get("block")).toUpperCase()),
				(String) map.get("yml")
				);
	}
}
