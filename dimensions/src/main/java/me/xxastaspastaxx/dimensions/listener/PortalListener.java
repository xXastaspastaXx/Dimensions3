package me.xxastaspastaxx.dimensions.listener;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
import org.bukkit.event.player.PlayerTeleportEvent;
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
import me.xxastaspastaxx.dimensions.DimensionsUtils;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;
import me.xxastaspastaxx.dimensions.completePortal.PortalEntitySolid;
import me.xxastaspastaxx.dimensions.customportal.CustomPortal;
import me.xxastaspastaxx.dimensions.customportal.CustomPortalDestroyCause;
import me.xxastaspastaxx.dimensions.settings.DimensionsSettings;


public class PortalListener implements Listener {

	//private Dimensions pl;
	
	private PacketListener packetListener1;
	//private PacketListener packetListener2;
	
	public PortalListener(Dimensions pl) {
		//this.pl = pl;
		//PacketType.Play.Server.UNLOAD_CHUNK = new PacketType(Protocol.PLAY, Sender.SERVER, 0x22, "MapChunk", "SPacketChunkData");
		
		packetListener1 = new PacketAdapter(pl, ListenerPriority.MONITOR, PacketType.Play.Server.MAP_CHUNK) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK) {
					for (CompletePortal complete : Dimensions.getCompletePortalManager().getCompletePortals(event.getPlayer().getWorld(), event.getPacket().getIntegers().read(0), event.getPacket().getIntegers().read(1))) {
						complete.fill(event.getPlayer());
					}
					
				}
			}
		};
		
		if (DimensionsSettings.enableEntitiesTeleport) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
				
				@Override
				public void run() {
					for (CompletePortal portal : Dimensions.getCompletePortalManager().getCompletePortals()) {
						portal.updatePortal();
					}
				}
			}, 0, DimensionsSettings.updateEveryTick);
		}
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
		handlePositionChange(e.getPlayer(), e.getFrom(), e.getTo());
		
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPortalTeleport(PlayerTeleportEvent e) {
		handlePositionChange(e.getPlayer(), e.getFrom(), e.getTo());
	}
	
	private void handlePositionChange(Player p, Location from, Location to) {
		if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) return;
		

		CompletePortal complTo = Dimensions.getCompletePortalManager().getCompletePortal(to, false, false);
		
		CompletePortal complFrom = Dimensions.getCompletePortalManager().getCompletePortal(from, false, false);
		
		if (DimensionsSettings.enableNetherPortalEffect && complTo!=null) {
			p.sendBlockChange(to, DimensionsUtils.getNetherPortalEffect(complTo.getPortalGeometry().iszAxis()));
		}
		if (DimensionsSettings.enableNetherPortalEffect && complFrom!=null) {
			if (complFrom.getPortalEntities().get(0) instanceof PortalEntitySolid) {
				p.sendBlockChange(from, complFrom.getCustomPortal().getInsideBlockData(complFrom.getPortalGeometry().iszAxis()));
			} else {
				p.sendBlockChange(from, from.getBlock().getBlockData());
			}
		}
		
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
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		e.setCancelled(bucketEvent(e.getPlayer(), e.getBlockClicked().getRelative(e.getBlockFace())));
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
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
	public void onBlockFade(BlockFadeEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockGrow(BlockGrowEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_BEHAVIOUR));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPistonExtend(BlockPistonExtendEvent e) {
		handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.PISTON);
		for (Block block : e.getBlocks()) {
			if (handleBlockChange(block,null,CustomPortalDestroyCause.PISTON)) {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPistonRetract(BlockPistonRetractEvent e) {
		handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.PISTON);
		for (Block block : e.getBlocks()) {
			if (handleBlockChange(block,null,CustomPortalDestroyCause.PISTON)) {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onRedstone(BlockRedstoneEvent e) {
		handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.REDSTONE);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onCauldronChange(CauldronLevelChangeEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_BEHAVIOUR));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onFluidChange(FluidLevelChangeEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.FLUID));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onFurnaceBurn(FurnaceBurnEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_BEHAVIOUR));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLeavesDecay(LeavesDecayEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onMoistureChange(MoistureChangeEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onSpongeAbsorb(SpongeAbsorbEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_BEHAVIOUR));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),null,CustomPortalDestroyCause.BLOCK_PHYSICS));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		e.setCancelled(handleBlockChange(e.getBlock(),e.getPlayer(),CustomPortalDestroyCause.PLAYER));
	}
	
	//BLOCK CHANGE EVENT ^^^^^
	
	public boolean handleBlockChange(Block block, Entity ent, CustomPortalDestroyCause cause) {
		
		if (clicked.containsKey(ent)) {
			if (System.currentTimeMillis()-clicked.get(ent)<500) {
				return false;
			} else {
				clicked.remove(ent);
			}
		}
		if (!DimensionsSettings.listenToEvents.contains(cause.name())) return false;
		
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
