package de.ybeta.spigotapi.util;

import com.google.common.base.Preconditions;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    public enum BuilderType {
        CREATE,
        MODIFY;
    }

    private ItemStack itemStack;

    public ItemBuilder(ItemStack itemStack) {
        this(BuilderType.CREATE, itemStack);
    }

    public ItemBuilder(BuilderType builderType, ItemStack itemStack) {
        Preconditions.checkArgument(builderType != null, "BuilderType can not be null");
        if (builderType == BuilderType.CREATE) {
            this.itemStack = new ItemStack(itemStack);
        } else if (builderType == BuilderType.MODIFY) {
            this.itemStack = itemStack;
        }
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        this.itemStack.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder setType(Material material) {
        this.itemStack.setType(material);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setMaterialData(MaterialData materialData) {
        this.itemStack.setData(materialData);
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatUtil.color(displayName));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(itemFlags);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addAttributeModifier(attribute, attributeModifier);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLocalizedName(String localizedName) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLocalizedName(localizedName);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(List<String> list) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(ChatUtil.colorList(list));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(String... strings) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(ChatUtil.colorList(Arrays.asList(strings)));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    @Deprecated
    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) itemStack.getItemMeta();
            im.setOwner(owner);
            itemStack.setItemMeta(im);
        } catch (ClassCastException ignored) {}
        return this;
    }

    public ItemBuilder setSkullOwningPlayer(OfflinePlayer offlinePlayer) {
        try {
            SkullMeta im = (SkullMeta) itemStack.getItemMeta();
            im.setOwningPlayer(offlinePlayer);
            itemStack.setItemMeta(im);
        } catch (ClassCastException ignored) {}
        return this;
    }

    public ItemBuilder setDamage(int damage) {
        try {
            Damageable im = (Damageable) itemStack.getItemMeta();
            im.setDamage(damage);
            itemStack.setItemMeta(im);
        } catch (ClassCastException ignored) {}
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) itemStack.getItemMeta();
            im.setColor(color);
            itemStack.setItemMeta(im);
        } catch (ClassCastException ignored) {}
        return this;
    }

    public ItemBuilder setArmorTrim(ArmorTrim armorTrim) {
        try {
            ArmorMeta im = (ArmorMeta) itemStack.getItemMeta();
            im.setTrim(armorTrim);
            itemStack.setItemMeta(im);
        } catch (ClassCastException ignored) {}
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }

    public ItemStack toItemStack() {
        return this.itemStack;
    }

}
