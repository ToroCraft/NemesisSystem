package net.torocraft.nemesissystem.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.nbt.NBTTagCompound;
import scala.annotation.meta.field;

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
				writeFieldToCompound(c, f, v);
			}
		} catch (Exception e) {
			throwError(f, o, e);
		}
		f.setAccessible(accessible);
	}

	private static void readField(NBTTagCompound c, Object o, Field field) {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		switch (field.getType().getName()) {
		case "java.lang.Long":
			readLong(c, field, o);
			return;
		case "java.lang.Integer":
			readInteger(c, field, o);
			return;
		}
		field.setAccessible(accessible);
	}

	private static void readLong(NBTTagCompound c, Field f, Object o) {
		try {
			if (c.hasKey(f.getName())) {
				f.setLong(o, c.getLong(f.getName()));
			} else {
				f.set(o, null);
			}
		} catch (Exception e) {
			throwError(f, o, e);
		}
	}

	private static void writeLong(NBTTagCompound c, Field f, Object o) {
		try {
			Long v = (Long) f.get(o);
			if (v == null) {
				c.removeTag(f.getName());
			} else {
				c.setLong(f.getName(), v);
			}
		} catch (Exception e) {
			throwError(f, o, e);
		}
	}

	private static void readInteger(NBTTagCompound c, Field f, Object o) {
		try {
			if (c.hasKey(f.getName())) {
				f.setInt(o, c.getInteger(f.getName()));
			} else {
				f.set(o, null);
			}
		} catch (Exception e) {
			throwError(f, o, e);
		}
	}

	private static void writeInteger(NBTTagCompound c, Field f, Object o) {
		try {
			Integer v = (Integer) f.get(o);
			if (v == null) {
				c.removeTag(f.getName());
			} else {
				c.setInteger(f.getName(), v);
			}
		} catch (Exception e) {
			throwError(f, o, e);
		}
	}

	private static void writeFieldToCompound(NBTTagCompound c, Field f, Object value) {
		switch (f.getType().getName()) {
		case "java.lang.Long":
			c.setLong(f.getName(), (Long) value);
			return;
		case "java.lang.Integer":
			c.setInteger(f.getName(), (Integer)value);
			return;
		}
	}

	private static void throwError(Field f, Object o, Exception e) {
		System.out.println("NBT serializer error Field[" + f.getName() + "] Object[" + o.getClass().getName() + "]");
		e.printStackTrace();
	}

}
