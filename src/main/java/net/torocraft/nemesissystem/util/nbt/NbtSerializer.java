package net.torocraft.nemesissystem.util.nbt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;

public class NbtSerializer {

	/**
	 * read data from the NBT to the object
	 */
	public static void read(NBTTagCompound c, Object o) {
		fields(o).forEach((Field f) -> readField(c, o, f));
	}

	/**
	 * Write date from the object to the NBT
	 */
	public static void write(NBTTagCompound c, Object o) {
		fields(o).forEach((Field f) -> writeField(c, o, f));
	}

	private static List<Field> fields(Object o) {
		return Arrays.stream(o.getClass().getDeclaredFields()).filter(NbtSerializer::isAnnotated).collect(Collectors.toList());
	}

	private static boolean isAnnotated(Field field) {
		return field.isAnnotationPresent(NbtField.class);
	}

	private static void writeField(NBTTagCompound c, Object o, Field f) {
		boolean accessible = f.isAccessible();
		f.setAccessible(true);
		try {
			Object v = f.get(o);
			if (v == null) {
				c.removeTag(f.getName());
			} else {
				NBTBase nbt = toCompound(v);
				if (nbt != null) {
					c.setTag(f.getName(), nbt);
				}
			}
		} catch (Exception e) {
			throwError(f, o, e);
		}
		f.setAccessible(accessible);
	}

	private static void readField(NBTTagCompound c, Object o, Field f) {
		boolean accessible = f.isAccessible();
		f.setAccessible(true);
		try {
			if (c.hasKey(f.getName())) {
				NBTBase nbt = c.getTag(f.getName());
				f.set(o, fromCompound(f, f.getType(), nbt));
			} else {
				try {
					f.set(o, null);
				} catch (Exception e) {
					// TODO function to null/clear all types
				}
			}
		} catch (Exception e) {
			throwError(f, o, e);
		}

		f.setAccessible(accessible);
	}

	@SuppressWarnings("unchecked")
	private static NBTBase toCompound(Object value) {
		if (value == null) {
			return null;
		}

		switch (value.getClass().getName()) {
		case "java.lang.Long":
		case "long":
			return new NBTTagLong((Long) value);

		case "java.lang.Boolean":
		case "boolean":
			return new NBTTagByte((byte) (((Boolean) value) ? 1 : 0));

		case "java.lang.Integer":
		case "int":
			return new NBTTagInt((Integer) value);
		case "java.lang.String":
			return new NBTTagString((String) value);
		case "java.util.UUID":
			return new NBTTagString(value.toString());
		}

		if (value instanceof Enum) {
			return new NBTTagString(value.toString());
		}

		if (value instanceof ItemStack) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			((ItemStack)value).writeToNBT(nbttagcompound);
			return nbttagcompound;
		}

		if (value instanceof Map) {
			Map<String, ?> map = (Map<String, ?>) value;
			NBTTagCompound c = new NBTTagCompound();
			for (Entry<String, ?> e : map.entrySet()) {
				c.setTag(e.getKey(), toCompound(e.getValue()));
			}
			return c;
		}

		if (value instanceof List) {
			// TODO only support array lists
			List<?> list = (List<?>) value;
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < list.size(); ++i) {
				NBTBase nbt = toCompound(list.get(i));
				// TODO is slot needed for itemStacks? nbttagcompound.setByte("Slot", (byte) i);
				nbttaglist.appendTag(nbt);
			}
			return nbttaglist;
		}

		NBTTagCompound c = new NBTTagCompound();
		write(c, value);
		return c;
	}

	@SuppressWarnings("unchecked")
	private static Object fromCompound(Field f, Class type, NBTBase value) {
		if (value == null) {
			return null;
		}

		Class genericType;

		switch (value.getClass().getName()) {
		case "net.minecraft.nbt.NBTTagInt":
			return ((NBTTagInt) value).getInt();
		case "net.minecraft.nbt.NBTTagLong":
			return ((NBTTagLong) value).getLong();
		case "net.minecraft.nbt.NBTTagByte":
			return ((NBTTagByte) value).getByte() != 0;
		case "net.minecraft.nbt.NBTTagString":
			String s = ((NBTTagString) value).getString();
			if (type.getTypeName().equals("java.util.UUID")) {
				return UUID.fromString(s);
			} else if (Enum.class.isAssignableFrom(type)) {
				return Enum.valueOf(type, s);
			}
			return s;
		case "net.minecraft.nbt.NBTTagList":
			NBTTagList nbtList = (NBTTagList) value;
			List<Object> list;
			genericType = f.getAnnotation(NbtField.class).genericType();

			if (NonNullList.class.isAssignableFrom(type)) {
				list = NonNullList.withSize(nbtList.tagCount(), ItemStack.EMPTY);
			} else {
				list = new ArrayList<>();
				for(int i = 0; i < nbtList.tagCount(); i++){
					list.add(null);
				}
			}

			for (int i = 0; i < nbtList.tagCount(); i++) {

				if (genericType.isAssignableFrom(ItemStack.class)) {
					list.set(i, new ItemStack((NBTTagCompound)nbtList.get(i)));
				} else {
					list.set(i, fromCompound(f, genericType, nbtList.get(i)));
				}


			}

			return list;
		case "net.minecraft.nbt.NBTTagCompound":
			if (Map.class.isAssignableFrom(type)) {
				genericType = f.getAnnotation(NbtField.class).genericType();
				NBTTagCompound c = (NBTTagCompound) value;
				Map<String, Object> map = new HashMap<>();
				for (String key : c.getKeySet()) {
					map.put(key, fromCompound(f, genericType, c.getTag(key)));
				}
				return map;
			}

			try {
				Object o = type.newInstance();
				read((NBTTagCompound) value, o);
				return o;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	private static void throwError(Field f, Object o, Exception e) {
		System.out.println("NBT serializer error Field[" + f.getName() + "] Object[" + o.getClass().getName() + "]");
		e.printStackTrace();
	}


	private static void logUnsupportedValue(Field f, Object value) {
		System.out.println("Unsupported Type[" + f.getType().getName() + "] Field[" + f + "] Value[" + value + "]");
	}

}
