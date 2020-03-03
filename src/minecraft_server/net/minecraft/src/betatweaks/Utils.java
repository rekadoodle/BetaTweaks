package net.minecraft.src.betatweaks;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class Utils {

	public static MinecraftServer mc = ModLoader.getMinecraftServerInstance();
	
	public static int clearBlockID(Block block) {
		return clearBlockID(block.blockID);
	}
	
	public static int clearBlockID(int id) {
		Block.blocksList[id] = null;
		return id;
	}
	
	public static void replaceBlock(Block newBlock, String ...fields) {
		EasyField<Block> blockField = new EasyField<Block>(Block.class, fields);
		blockField.removeFinalModifier();
		blockField.set(newBlock);
		Block.blocksList[newBlock.blockID] = newBlock;
	}
	
	public static void logError(String... lines) {
		System.out.println(new StringBuilder().append("BETATWEAKS ERROR: ").append(lines[0]).toString());
		for (String message : lines) {
			if(message == lines[0]) continue;
			System.out.println(new StringBuilder().append('\t').append(message).toString());
		}
	}
	
	public static Entity getEntityFromID(int id, World world) {
		for (Object obj : world.loadedEntityList) {
			Entity entity = (Entity)obj;
			if(entity.entityId == id)
				return entity;
		}
		return null;
	}
	
	public static class EasyField<T> {

		private static final EasyField<Integer> modifiersField = new EasyField<Integer>(Field.class, "modifiers");
		public final Field field;
		
		public EasyField(Class<?> target, String... names) {
			for (Field field : target.getDeclaredFields()) {
				for (String name : names) {
					if (field.getName() == name) {
						field.setAccessible(true);
						this.field = field;
						return;
					}
				}
			}
			this.field = null;
			logError("Failed to located field " + names[0] + " in class " + target.getSimpleName());
		}
		
		public boolean exists() {
			return field != null;
		}
		
		@SuppressWarnings("unchecked")
		public T get(Object instance) {
			try {
				return (T) field.get(instance);
			}
			catch (Exception e) { e.printStackTrace(); }
			return null;
		}
		
		public T get() {
			return this.get(null);
		}
		
		public void set(Object instance, T value) {
			try {
				field.set(instance, value);
			} 
			catch (Exception e) { e.printStackTrace(); }
		}
		
		public void set(T value) {
			this.set(null, value);
		}
		
		public void removeFinalModifier() {
			modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
		}
		
	}
}
