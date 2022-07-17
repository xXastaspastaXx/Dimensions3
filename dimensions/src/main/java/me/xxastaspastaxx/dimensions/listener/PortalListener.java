package me.xxastaspastaxx.dimensions.listener;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsSettings;
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;

public class PortalListener implements Listener {

	//private Dimensions pl;
	
	private PacketListener packetListener1;
	//private PacketListener packetListener2;
	
	public PortalListener(Dimensions pl) {
		//this.pl = pl;
		//PacketType.Play.Server.UNLOAD_CHUNK = new PacketType(Protocol.PLAY, Sender.SERVER, 0x22, "MapChunk", "SPacketChunkData");
		
		packetListener1 = new PacketAdapter(pl, ListenerPriority.LOW, PacketType.Play.Server.MAP_CHUNK) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK) {
					for (CompletePortal complete : Dimensions.getCompletePortalManager().getCompletePortals(event.getPlayer().getWorld(), event.getPacket().getIntegers().read(0), event.getPacket().getIntegers().read(1))) {
						complete.fill(event.getPlayer());
					}
					
				}
			}
		};
	
		
		/*packetListener2 = new PacketAdapter(pl, ListenerPriority.NORMAL, PacketType.Play.Server.UNLOAD_CHUNK) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {

					System.out.println("TTtttt");
					for (CompletePortal complete : Dimensions.getCompletePortalManager().getCompletePortals(event.getPacket().getIntegers().read(0), event.getPacket().getIntegers().read(1))) {
						System.out.println(complete.getWorld()+", "+complete.getCenter());
						complete.destroy(event.getPlayer());
					}
				}
			}
		};*/
		

		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener1);
		//ProtocolLibrary.getProtocolManager().addPacketListener(packetListener2);
		
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerWalk(PlayerMoveEvent e) {
		if (e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY() && e.getTo().getBlockZ() == e.getFrom().getBlockZ()) return;
		Player p = e.getPlayer();
		

		CompletePortal complTo = Dimensions.getCompletePortalManager().getCompletePortal(e.getTo(), false, false);
		
		CompletePortal complFrom = Dimensions.getCompletePortalManager().getCompletePortal(e.getFrom(), false, false);
		if (complFrom!=null && complFrom.hasInHold(p)) {

			if (complTo!=null && complFrom.equals(complTo)) return;
			complFrom.removeFromHold(p);
		}
		
		if (complTo!=null) complTo.handleEntity(p);
		
			
	}
	

	HashMap<Player,Long> clicked = new HashMap<Player,Long>();
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPortalInteract(PlayerInteractEvent e) {
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			try {
				int rad = (int) ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK)?Math.min(Math.ceil(e.getClickedBlock().getLocation().distance(e.getPlayer().getEyeLocation())),5):5);
				List<Block> los = e.getPlayer().getLineOfSight(null, rad);
				for (Block block : los) {
					if (Dimensions.getCompletePortalManager().getCompletePortal(block.getLocation(), false, false)!=null) {
						e.setCancelled(true);
						break;
					}
				}
			} catch (IllegalStateException ex) {}
		}
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        	//if (e.getItem() == null) return;
        	Block block = e.getClickedBlock().getRelative(e.getBlockFace());
        	if (Dimensions.getCompletePortalManager().getCompletePortal(block.getLocation(), false, false)!=null) return;
        	for (CustomPortal portals : Dimensions.getCustomPortalManager().getCustomPortals()) {
    			if (portals.tryIgnite(e.getPlayer(), e.getItem(), block.getLocation()) != null) {
    				e.setCancelled(true);
					if (e.getPlayer().getGameMode()!=GameMode.CREATIVE && DimensionsSettings.consumeItems) {
						ItemStack item = e.getItem();
						if (item.getType().toString().contains("BUCKET") && item.getType()!=Material.BUCKET) {
							item.setType(Material.BUCKET);
						} else if (item.getItemMeta() instanceof Damageable) {
							Damageable dmg = (Damageable) item.getItemMeta();
							dmg.setDamage(dmg.getDamage()+1);
							item.setItemMeta((ItemMeta) dmg);
							if (dmg.getDamage()>=item.getType().getMaxDurability()) {
								item.setAmount(item.getAmount()-1);
							}
						} else {
							item.setAmount(item.getAmount()-1);
						}
							
					}
            		clicked.put(e.getPlayer(), System.currentTimeMillis());
        		}
    		}
        }
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerClick(PlayerAnimationEvent e) {
		Player p = e.getPlayer();

		if (clicked.containsKey(p)) {
			if (System.currentTimeMillis()-clicked.get(p)<500) {
				return;
			} else {
				clicked.remove(p);
			}
		}
		if (e.getAnimationType()==PlayerAnimationType.ARM_SWING) {
			try {
				List<Block> los = p.getLineOfSight(null, 5);
				for (Block block : los) {
					if (!DimensionsUtils.isAir(block)) break;
					CompletePortal portal = Dimensions.getCompletePortalManager().getCompletePortal(block.getLocation(), false, false);
					if (portal!=null) {
						Dimensions.getCompletePortalManager().removePortal(portal, CustomPortalDestroyCause.PLAYER_INSIDE, p);
						break;
					}
				}
			} catch (IllegalStateException ex) {}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		e.setCancelled(bucketEvent(e.getPlayer(), e.getBlockClicked().getRelative(e.getBlockFace())));
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBucketFill(PlayerBucketFillEvent e) {
		e.setCancelled(bucketEvent(e.getPlayer(), e.getBlockClicked().getRelative(e.getBlockFace())));
	}
	
	
	public boolean bucketEvent(Player p, Block eventBlock) {
		try {
			int rad = (int) Math.ceil(eventBlock.getLocation().distance(p.getEyeLocation()));
			List<Block> los = p.getLineOfSight(null, rad);
			for (Block block : los) {
				if (Dimensions.getCompletePortalManager().getCompletePortal(block.getLocation(), false, false)!=null) {
					return true;
				}
			}
		} catch (IllegalStateException ex) {}
		return false;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onExplode(ExplosionPrimeEvent e) {
		Entity exploder = e.getEntity();
		for (CompletePortal portal : Dimensions.getCompletePortalManager().getNearestPortals(exploder.getLocation(),(int) (e.getRadius()+2))) {
			if (!Dimensions.getCompletePortalManager().removePortal(portal,CustomPortalDestroyCause.ENTITY, exploder)) {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLiquidFlow(BlockFromToEvent e) {
		if (Dimensions.getCompletePortalManager().getCompletePortal(e.getBlock().getLocation(), false, false)!=null || Dimensions.getCompletePortalManager().getCompletePortal(e.getToBlock().getLocation(), false, false)!=null) {
			e.setCancelled(true);
		}
	}
	
	//BLOCK CHANGE EVENT VVVV
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(BlockFadeEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(BlockGrowEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(BlockBurnEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(BlockPistonExtendEvent e) {
		onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.PISTON);
		for (Block block : e.getBlocks()) {
			if (onBlockChange(block,null,CustomPortalDestroyCause.PISTON)) {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(BlockPistonRetractEvent e) {
		onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.PISTON);
		for (Block block : e.getBlocks()) {
			if (onBlockChange(block,null,CustomPortalDestroyCause.PISTON)) {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(BlockRedstoneEvent e) {
		onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(CauldronLevelChangeEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(FluidLevelChangeEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(FurnaceBurnEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(LeavesDecayEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(MoistureChangeEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(SpongeAbsorbEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(BlockPhysicsEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(BlockBreakEvent e) {
		e.setCancelled(onBlockChange(e.getBlock(),e.getPlayer(),CustomPortalDestroyCause.PLAYER));
	}
	
	//BLOCK CHANGE EVENT ^^^^^
	
	public boolean onBlockChange(Block block, Entity ent, CustomPortalDestroyCause cause) {

		List<CompletePortal> portals = Dimensions.getCompletePortalManager().getCompletePortals(block.getLocation(), true, false);
		boolean cancel = false;
        for (CompletePortal portal : portals) {
        	cancel = !Dimensions.getCompletePortalManager().removePortal(portal, cause, ent) || cancel;
        }
		return cancel;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageEvent e) {
		DamageCause cause = e.getCause();
		if (!((e.getEntity() instanceof LivingEntity) && (cause.equals(DamageCause.SUFFOCATION) || cause.equals(DamageCause.LAVA) || cause.equals(DamageCause.DROWNING) || cause.equals(DamageCause.HOT_FLOOR)))) return;
		if (Dimensions.getCompletePortalManager().getCompletePortal(e.getEntity().getLocation(), false ,false)!=null) e.setCancelled(true);
	}
	
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		CompletePortal compl = Dimensions.getCompletePortalManager().getCompletePortal(e.getPlayer().getLocation(), false, false);
		if (compl!=null) {
			compl.pushToHold(e.getPlayer());
		}
	}
	
	long lastSave = System.currentTimeMillis();
	@EventHandler
	public void onSave(WorldSaveEvent e) {
		if (System.currentTimeMillis()-lastSave<5000) return;
		lastSave = System.currentTimeMillis();
		//Dimensions.getCompletePortalManager().save(false);
	}
}
