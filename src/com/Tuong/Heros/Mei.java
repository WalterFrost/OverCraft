package com.Tuong.Heros;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.Tuong.Arena.Arena;
import com.Tuong.OverCraftCore.Core;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_10_R1.EnumParticle;
import net.minecraft.server.v1_10_R1.PacketPlayOutWorldParticles;

public class Mei
  implements Listener
{
	//Hi 
  private Player player;
  private Arena arena;
  private int ammo;
  private float ultimate_charge;
  private double shootdamage;
  private double maxREGENERATIONth;
  private boolean start;
  private boolean msg;
  private boolean reloading;
  private boolean shoot;
  private boolean shift;
  private HashMap<Player, Integer> freeze;
  private ItemStack skull;
  public Mei(Player player, Arena arena)
  {
	player.removeAchievement(Achievement.OPEN_INVENTORY);
	skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
	SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
	skullMeta.setDisplayName("Demn Son Where U Get This From");
	skullMeta.setOwner("Claush");
	skull.setItemMeta(skullMeta);
	player.getInventory().clear();
    player.getInventory().setHeldItemSlot(8);
    player.getInventory().setItem(8, getShuriken());
    if (((String)arena.team.get(player)).equals("BLUE")) {
      player.getInventory().setChestplate(arena.getBlueChestplate());
    } else {
      player.getInventory().setChestplate(arena.getRedChestplate());
    }
    this.player = player;
    this.arena = arena;
    this.start = true;
    this.reloading = false;
    this.shoot = false;
    this.ammo = 300;
    this.shift = false;
    this.shootdamage = 5;
    this.maxREGENERATIONth = 25.0D;
    this.freeze = new HashMap<Player,Integer>();
    player.setMaxHealth(this.maxREGENERATIONth);
    player.setHealth(player.getMaxHealth());
    Bukkit.getPluginManager().registerEvents(this, Core.plugin);
    if(Core.t) player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMEN_HURT, 1, 1);
    recharge();
  }
  private ItemStack getShuriken()
  {
    ItemStack shuriken = new ItemStack(Material.EMERALD, 1);
    ItemMeta meta = shuriken.getItemMeta();
    shuriken.setItemMeta(meta);
    meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.GRAY + "Ice Blaster");
    meta.setLore(Arrays.asList(new String[] { ChatColor.GOLD + "Type: " + ChatColor.GRAY + ChatColor.ITALIC + "Beam", 
      ChatColor.GOLD + "Damage: " + ChatColor.GRAY + ChatColor.ITALIC + "22 - 75", 
      ChatColor.GOLD + "Projectile speed: " + ChatColor.GRAY + ChatColor.ITALIC + "88.88m/Second", 
      ChatColor.GOLD + "Rate of fire: " + ChatColor.GRAY + ChatColor.ITALIC + "1 Shot/Second", 
      ChatColor.GOLD + "Ammo: " + ChatColor.GRAY + ChatColor.ITALIC + "300", 
      ChatColor.GOLD + "Reload time: " + ChatColor.GRAY + ChatColor.ITALIC + "1 Second", 
      ChatColor.LIGHT_PURPLE + "Description: " + ChatColor.RED + ChatColor.ITALIC + "Mei can also use her blaster to shoot icicle-like projectiles at medium range." }));
    shuriken.setItemMeta(meta);
    return shuriken;
  }
  
  @EventHandler
  public void pick(PlayerPickupArrowEvent e)
  {
    if (e.getPlayer().equals(this.player)) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void pick(PlayerPickupItemEvent e)
  {
    if (e.getPlayer().equals(this.player)) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void bomb(PlayerDropItemEvent e)
  {
    if (e.getPlayer().equals(this.player))
    {
      e.setCancelled(true);
      if ((e.getPlayer().equals(this.player)) && (this.arena.death.contains(this.player))) {
        return;
      }
      if (this.ultimate_charge == 1.0F) {
        this.ultimate_charge = 0.0F;
        if(Core.t) player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
        ArmorStand amm = player.getWorld().spawn(player.getEyeLocation(), ArmorStand.class);
        Location loc = e.getPlayer().getLocation();
        amm.setMetadata(Mei.this.player.getName(), new FixedMetadataValue(Core.plugin, "Mei"));
	    amm.setVisible(false);
	    amm.setCollidable(false);
	    amm.setHelmet(skull);
        new BukkitRunnable() {
        	int t = 0;
			@Override
			public void run() {
                t++;
                if(t == 20 || start == false){
                	amm.remove();
                	this.cancel();	
                } 
                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FIREWORKS_SPARK, true, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 8, 4, 8, 50, 80, new int[0]);
                PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(EnumParticle.WATER_DROP, true, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 8, 4, 8, 50, 80, new int[0]);
                for(Player p : arena.playerList.keySet()) {
                	((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet2);
                }
                for(Entity en : amm.getNearbyEntities(8, 4, 8)) if(en instanceof Player) addFreeze((Player)en,true);
			}
		}.runTaskTimer(Core.plugin, 0, 5);
      }
    }
  }
  
  public static ArrayList<Location> circle(Location center, double radius, int amount)
  {
      World world = center.getWorld();
      double increment = (2 * Math.PI) / amount;
      ArrayList<Location> locations = new ArrayList<Location>();
      for(int i = 0;i < amount; i++)
      {
          double angle = i * increment;
          double x = center.getX() + (radius * Math.cos(angle));
          double z = center.getZ() + (radius * Math.sin(angle));
          locations.add(new Location(world, x, center.getY(), z));
      }
      return locations;
  }
  
  @EventHandler
  public void respawn(PlayerRespawnEvent e)
  {
    if (!e.getPlayer().equals(this.player)) {
      return;
    }
    this.player.setMaxHealth(this.maxREGENERATIONth);
    this.player.setMaxHealth(this.player.getMaxHealth());
  }
  
  public void recharge()
  {
    new BukkitRunnable()
    {
      public void run()
      {
        if (!Mei.this.start) {
          cancel();
        }
        if (Mei.this.ultimate_charge + 0.01D >= 1.0D)
        {
          Mei.this.ultimate_charge = 1.0F;
          if (!Mei.this.msg)
          {
            Core.sendTitle(Mei.this.player, Integer.valueOf(5), Integer.valueOf(51), Integer.valueOf(1), ChatColor.GOLD + "ULTIMATE", ChatColor.GREEN + "Ready to launch");
            if(Core.t) player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMEN_STARE, 1, 1);
            Mei.this.msg = true;
          }
        }
        else
        {
          Mei tmp169_166 = Mei.this;tmp169_166.ultimate_charge = ((float)(tmp169_166.ultimate_charge + 0.01D));
        }
        if (Mei.this.start) {
          Mei.this.player.setLevel(Mei.this.ammo);
        }
        if (Mei.this.start) {
          Mei.this.player.setExp(Mei.this.ultimate_charge);
        }
      }
    }.runTaskTimer(Core.plugin, 0L, 52L);
  }
  
  @EventHandler
  public void explode(EntityExplodeEvent e)
  {
    if (e.getEntity().hasMetadata(this.player.getName())) {
    	e.blockList().clear();
    }
  }
  
  @EventHandler
  public void kill(EntityDeathEvent e)
  {
    if ((e.getEntity().getKiller() != null) && (e.getEntity().getKiller().equals(this.player)) && (!e.getEntity().equals(this.player)))
    {
      this.player.removePotionEffect(PotionEffectType.UNLUCK);
      if (this.ultimate_charge + 0.4D >= 1.0D)
      {
        if (this.start) {
          this.player.setLevel(this.ammo);
        }
        this.ultimate_charge = 1.0F;
        if (!this.msg)
        {
          if(Core.t) player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMEN_STARE, 1, 1);
          Core.sendTitle(this.player, Integer.valueOf(5), Integer.valueOf(51), Integer.valueOf(1), ChatColor.GOLD + "ULTIMATE", ChatColor.GREEN + "Ready to launch");
          this.msg = true;
        }
      }
      else
      {
        this.ultimate_charge = ((float)(this.ultimate_charge + 0.4D));
      }
      if (this.start) {
        this.player.setExp(this.ultimate_charge);
      }
    }
  }
  
  @EventHandler
  public void shoot(final PlayerInteractEvent e)
  {
    if ((e.getPlayer().equals(this.player)) && ((e.getAction() == Action.RIGHT_CLICK_BLOCK) || (e.getAction() == Action.RIGHT_CLICK_AIR)))
    {
      if ((e.getPlayer().equals(this.player)) && ((this.arena.death.contains(this.player)) || (this.shoot) || shift || arena.freezed.contains(player) || reloading))
      {
        e.setCancelled(true);
        return;
      }
      e.setCancelled(true);
      if (this.ammo > 0)
      {
        this.ammo -= 25;
        player.setLevel(ammo);
        if (this.start) {
          this.player.setLevel(this.ammo);
        }
        this.shoot = true;
        new BukkitRunnable()
        {
          public void run()
          {
            Mei.this.shoot = false;
            Snowball snow = (Snowball)Mei.this.player.getWorld().spawn(Mei.this.player.getEyeLocation().add(0.0D, 0.3D, 0.0D), Snowball.class);
            snow.setVelocity(Mei.this.player.getLocation().getDirection().multiply(3));
            snow.setShooter(Mei.this.player);
            snow.setMetadata(Mei.this.player.getName(), new FixedMetadataValue(Core.plugin, "Mei"));
          }
        }.runTaskLater(Core.plugin, 10L);
      }
      else
      {
        e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_OFF, 1.0F, 1.0F);
        if (this.reloading) {
          return;
        }
        this.reloading = true;
        new BukkitRunnable()
        {
          public void run()
          {
            e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0F, 1.0F);
            Mei.this.ammo = 300;
            if (Mei.this.start) {
              Mei.this.player.setLevel(Mei.this.ammo);
            }
            Mei.this.reloading = false;
          }
        }.runTaskLater(Core.plugin, 25L);
      }
    }
    else if ((e.getPlayer().equals(this.player)) && ((e.getAction() == Action.LEFT_CLICK_BLOCK) || (e.getAction() == Action.LEFT_CLICK_AIR)))
    {
      if ((e.getPlayer().equals(this.player)) && ((this.arena.death.contains(this.player)) || (this.shoot) || shift || arena.freezed.contains(player) || reloading))
      {
        e.setCancelled(true);
        return;
      }
      if (this.ammo > 0)
      {
        this.ammo -= 25;
        player.setLevel(ammo);
        new BukkitRunnable()
        {
          Location loc = Mei.this.player.getLocation();
          Vector direction = this.loc.getDirection().normalize();
          double t = 1.0D;
          
          public void run()
          {
            this.t += 1.0D;
            double x = this.direction.getX() * this.t;
            double y = this.direction.getY() * this.t + 1.5D;
            double z = this.direction.getZ() * this.t;
            this.loc.add(x, y, z);
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.WATER_DROP, true, (float)this.loc.getX(), (float)this.loc.getY(), (float)this.loc.getZ(), 0.0F, 0.0F, 0.0F, 15.0F, 35, new int[0]);
            for(Player p : arena.playerList.keySet()) ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
            this.loc.subtract(x, y, z);
            if (this.t > 4.0D) {
              cancel();
            }
          }
        }.runTaskTimer(Core.plugin, 0L, 1L);
        Entity en = Core.getNearestEntityInSight(player, 8);
        if ((en != null) && ((en instanceof Player))) {
          addFreeze((Player)en,true);
        }
      }
      else
      {
        e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_OFF, 1.0F, 1.0F);
        if (this.reloading) {
          return;
        }
        this.reloading = true;
        new BukkitRunnable()
        {
          public void run()
          {
            e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0F, 1.0F);
            Mei.this.ammo = 300;
            if (Mei.this.start) {
              Mei.this.player.setLevel(Mei.this.ammo);
            }
            Mei.this.reloading = false;
          }
        }.runTaskLater(Core.plugin, 25L);
      }
    }
  }
  @EventHandler 
  public void antiSwap(PlayerSwapHandItemsEvent e){
	  e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_OFF, 1.0F, 1.0F);
      if (this.reloading) {
        return;
      }
      this.reloading = true;
      new BukkitRunnable()
      {
        public void run()
        {
          e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0F, 1.0F);
          Mei.this.ammo = 300;
          if (Mei.this.start) {
            Mei.this.player.setLevel(Mei.this.ammo);
          }
          Mei.this.reloading = false;
        }
      }.runTaskLater(Core.plugin, 25L);
  }
  public void addFreeze(final Player p,boolean b)
  {
    if (this.arena.freezed.contains(this.player) || arena.isAlly(p, player) || p.equals(player)) {
      return;
    }
    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 90, 1));
    if(b) p.damage(1.0, player);
    if (this.freeze.containsKey(p))
    {
      int level = ((Integer)this.freeze.get(p)).intValue();
      this.freeze.remove(p);
      if (level >= 5)
      {
        this.arena.freezed.add(p);
        new BukkitRunnable()
        {
          public void run()
          {
        	  if(start) arena.freezed.remove(p);
          }
        }.runTaskLater(Core.plugin, 40);
      }
      else
      {
        this.freeze.put(p, Integer.valueOf(level + 1));
      }
    }
    else
    {
      this.freeze.put(p, Integer.valueOf(1));
    }
  }
  
  @EventHandler
  public void click(InventoryClickEvent e)
  {
    if (e.getWhoClicked().equals(this.player)) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void move(PlayerMoveEvent e)
  {
    if ((e.getPlayer().equals(this.player)) && (this.shift) && ((e.getTo().getX() != e.getFrom().getX()) || (e.getTo().getZ() != e.getFrom().getZ()) || (e.getTo().getY() > e.getFrom().getY()))) {
      e.setCancelled(true);
    }
    if ((this.freeze.containsKey(e.getPlayer())) && (!e.getPlayer().hasPotionEffect(PotionEffectType.SLOW))) {
      this.freeze.remove(e.getPlayer());
    }
    if ((arena.freezed.contains(e.getPlayer())) && ((e.getTo().getX() != e.getFrom().getX()) || (e.getTo().getZ() != e.getFrom().getZ()) || (e.getTo().getY() > e.getFrom().getY()))) {
      e.setCancelled(true);
    }
  }
  
  public Vector getDirection(double yaw, double pitch)
  {
    yaw %= 360.0D;
    Vector vector = new Vector();
    
    double rotX = yaw;
    double rotY = pitch;
    
    vector.setY(-Math.sin(Math.toRadians(rotY)));
    
    double xz = Math.cos(Math.toRadians(rotY));
    
    vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
    vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
    
    return vector;
  }
  
  
  @EventHandler
  public void changebooster(PlayerToggleSneakEvent e)
  {
    if (e.getPlayer().equals(this.player))
    {
      if ((e.getPlayer().equals(this.player)) && (this.arena.death.contains(this.player)) && shift) {
        return;
      }
      if (!this.player.hasPotionEffect(PotionEffectType.UNLUCK))
      {
    	player.teleport(player.getLocation().getBlock().getLocation());
        Block block = this.player.getLocation().getBlock();
        Block block1 = this.player.getLocation().add(0.0D, 1.0D, 0.0D).getBlock();
        if ((block.getType() != Material.AIR) || (block1.getType() != Material.AIR))
        {
          this.player.sendMessage(ChatColor.RED + "Bad location to freeze yourself");
          return;
        }
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 160, 1));
        this.shift = true;
        this.player.getLocation().getBlock().setType(Material.ICE);
        this.player.getLocation().add(0.0D, 1.0D, 0.0D).getBlock().setType(Material.ICE);
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 2));
        if(Core.t) player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_SPIDER_DEATH, 1, 1);
        player.teleport(player.getLocation().getBlock().getLocation());
        new BukkitRunnable()
        {
          public void run()
          {
            Mei.this.shift = false;
            block.setType(Material.AIR);
            block1.setType(Material.AIR);
          }
        }.runTaskLater(Core.plugin, 80L);
      }
    }
  }
  
  @EventHandler
  public void fall(EntityDamageEvent e)
  {
    if ((e.getEntity().equals(this.player)) && (e.getCause() == EntityDamageEvent.DamageCause.FALL)) {
      e.setCancelled(true);
    }
    if ((e.getEntity().equals(this.player)) && (this.shift)) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void nodamage(EntityDamageByEntityEvent e)
  {
    if ((e.getDamager().hasMetadata(this.player.getName())) && ((e.getEntity() instanceof Player)))
    {
      if (this.arena.isAlly(this.player, (Player)e.getEntity())) {
        e.setCancelled(true);
      } else {
        e.setDamage(this.shootdamage);
      }
    }
    else if ((e.getDamager().equals(this.player)) && ((e.getEntity() instanceof Player))) {
        e.setDamage(1);
    	addFreeze(((Player)e.getEntity()),false);
    }
  }
  
  public void stop()
  {
	player.setWalkSpeed(0.2F);
    this.start = false;
    HandlerList.unregisterAll(this);
    this.player.setLevel(0);this.player.setExp(0.0F);
    this.player.setLevel((int)((float[])this.arena.expStore.get(this.player))[0]);
    this.player.setExp(((float[])this.arena.expStore.get(this.player))[1]);
    this.player.setAllowFlight(false);
    this.freeze.clear();
    for (Entity en : this.player.getWorld().getEntities()) {
      if (en.hasMetadata(this.player.getName())) {
        en.remove();
      }
    }
  }
}
