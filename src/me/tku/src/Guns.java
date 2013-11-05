package me.tku.src;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Guns extends JavaPlugin implements Listener
{
	List<String> names = new ArrayList<String>();
	int reload = 0;
	int ammo = 10;
	boolean reloading = false;
	boolean currentlyReloading = false;
	Inventory shop = Bukkit.createInventory(null, 9, "Shop");

	public void onEnable()
	{
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	public boolean onCommand(CommandSender s, Command c, String l, String[] args)
	{
		Player p = (Player) s;
		if (l.equalsIgnoreCase("gun"))
		{
			if (args.length != 1)
			{
				p.sendMessage(ChatColor.RED + "Nu! It's /gun normal");
			} else if (args[0].equalsIgnoreCase("normal"))
			{
				p.getInventory().clear();
				p.getInventory().setItem(0, giveWeapon(ChatColor.GOLD + "Budder Gun", p));
				names.add(p.getName());
				p.sendMessage(ChatColor.RED + "Don't do too much damage ;-)");
			} else if(args[0].equalsIgnoreCase("shop"))
			{
				p.getInventory().clear();
				shop.setItem(0, shopItem1());
				p.openInventory(shop);
			}
		}
		return true;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		Player p = (Player) e.getWhoClicked();
		if(e.getCurrentItem().getType() == Material.GOLD_INGOT)
		{
			p.getInventory().setItem(0, giveWeapon(ChatColor.GOLD + "Budder Gun", p));
			p.closeInventory();
			names.add(e.getWhoClicked().getName());
			p.sendMessage(ChatColor.RED + "Don't do too much damage ;-)");
		}
		
		if(shop.contains(e.getCurrentItem()))
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if (names.contains(e.getPlayer().getName()))
		{
			if (e.getAction() == Action.RIGHT_CLICK_AIR)
			{
				if (e.getPlayer().getItemInHand().getType() == Material.GOLD_INGOT)
				{
					if (ammo <= 0 && !currentlyReloading)
					{
						removeZoom(e.getPlayer());
						e.getPlayer().sendMessage(ChatColor.RED + "Sorry, it seams your out of ammo. " + ammo);
						e.getPlayer().sendMessage(ChatColor.RED + "Left click your gun to reload!");
						reloading = true;
					} else if(currentlyReloading)
					{
						e.getPlayer().sendMessage(ChatColor.BLUE + "You are reloading!");
					}else
					{
						e.getPlayer().launchProjectile(Snowball.class);
						e.getPlayer().sendMessage("Your ammo is: " + ammo);

						if (ammo >= 1)
						{
							ammo--;
						}
					}
				}
			}

			if (e.getAction() == Action.LEFT_CLICK_AIR)
			{
				if (e.getPlayer().getItemInHand().getType() == Material.GOLD_INGOT)
				{
					if (!reloading)
					{
						reload++;
						if (reload == 1)
						{
							e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6000, 3));
						} else if (reload != 1)
						{
							removeZoom(e.getPlayer());
							reload = 0;
						}
					} else
					{
						currentlyReloading = true;
						e.getPlayer().sendMessage(ChatColor.BLUE + "Reloading...");
						runReloading(e.getPlayer());
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerThrow(ProjectileLaunchEvent e)
	{
		if (e.getEntity() instanceof Snowball)
		{
			e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(3));
		}
	}
	
	/*@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
		if(e.getEntity() instanceof Snowball)
		{
			e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 10f, false);
		}
	}*/

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Snowball)
		{
			e.setDamage(20.0);
		}
	}

	public void runReloading(final Player p)
	{
		Bukkit.getScheduler().runTaskLater(this, new Runnable()
		{
			public void run()
			{
				ammo = 10;
				reloading = false;
				reload = 0;
				currentlyReloading = false;
				p.sendMessage(ChatColor.RED + "Your gun has been reloaded.");
			}
		}, 100L);
	}
	
	public void removeZoom(Player p)
	{
		for (PotionEffect pe : p.getActivePotionEffects())
		{
			p.removePotionEffect(pe.getType());
		}
	}
	
	public ItemStack giveWeapon(String name, Player p)
	{
		ItemStack gun = new ItemStack(Material.GOLD_INGOT);
		ItemMeta im = gun.getItemMeta();
		ArrayList<String> lores = new ArrayList<String>();
		im.setDisplayName(name);
		lores.add(ChatColor.WHITE + "" + ChatColor.ITALIC + "Yes, this is a budder gun!");
		im.setLore(lores);
		gun.setItemMeta(im);
		return gun;
	}
	
	public ItemStack shopItem1()
	{
		ItemStack gun = new ItemStack(Material.GOLD_INGOT);
		ItemMeta im = gun.getItemMeta();
		ArrayList<String> lores = new ArrayList<String>();
		im.setDisplayName(ChatColor.RED + "Budder Gun");
		lores.add(ChatColor.WHITE + "" + ChatColor.ITALIC + "Yes, this is a budder gun!");
		im.setLore(lores);
		gun.setItemMeta(im);
		return gun;
	}
}