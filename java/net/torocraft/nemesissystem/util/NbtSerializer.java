package net.torocraft.nemesissystem.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagString;

public class NbtSerializer {

	/**
	 * read data from the NBT to the object
	 */
	public static void read(NBTTagCompound c, Object o) throws Exception {
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

	private static NBTBase toCompound(Object value) {
		if (value == null) {
			return null;
		}

		switch (value.getClass().getName()) {
		case "java.lang.Long":
		case "long":
			return new NBTTagLong((Long) value);
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

		if (value instanceof List) {
			// TODO only support array lists
			List<?> list = (List<?>) value;
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < list.size(); ++i) {
				nbttaglist.appendTag(toCompound(list.get(i)));
			}
			return nbttaglist;
		}

		System.out.println("toCompound: " + value.getClass().getName());

		NBTTagCompound c = new NBTTagCompound();
		write(c, value);
		return c;
		//logUnsupportedValue(, value);
	}

	private static Object fromCompound(Field f, Class type, NBTBase value) {
		if (value == null) {
			return null;
		}

		switch (value.getClass().getName()) {
		case "net.minecraft.nbt.NBTTagInt":
			return ((NBTTagInt) value).getInt();
		case "net.minecraft.nbt.NBTTagLong":
			return ((NBTTagLong) value).getLong();
		case "net.minecraft.nbt.NBTTagString":
			String s = ((NBTTagString) value).getString();
			if(type.getTypeName().equals("java.util.UUID")){
				return UUID.fromString(s);
			} else if (Enum.class.isAssignableFrom(type)) {
				return Enum.valueOf(type, s);
			}
			return s;
		case "net.minecraft.nbt.NBTTagList":
			Class genericType = f.getAnnotation(NbtField.class).genericType();
			NBTTagList nbtList = (NBTTagList) value;
			List<Object> list = new ArrayList<>();
			for (NBTBase nbt : nbtList) {
				list.add(fromCompound(f, genericType, nbt));
			}
			return list;
		}
		//logUnsupportedValue(, value);

		System.out.println("fromCompound: " + value.getClass().getName());
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
