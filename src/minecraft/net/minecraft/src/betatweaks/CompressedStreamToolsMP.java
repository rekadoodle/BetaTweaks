package net.minecraft.src.betatweaks;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;

public class CompressedStreamToolsMP extends CompressedStreamTools {

	public static NBTTagCompound func_35622_a(File file)
	        throws IOException
	    {
	        if(!file.exists())
	        {
	            return null;
	        }
	        DataInputStream datainputstream = new DataInputStream(new FileInputStream(file));
	        try
	        {
	            NBTTagCompound nbttagcompound = func_1141_a(datainputstream);
	            return nbttagcompound;
	        }
	        finally
	        {
	            datainputstream.close();
	        }
	    }
	
	public static NBTTagCompound func_1141_a(DataInput datainput)
	        throws IOException
	    {
	        NBTBase nbtbase = NBTBase.readTag(datainput);
	        if(nbtbase instanceof NBTTagCompound)
	        {
	            return (NBTTagCompound)nbtbase;
	        } else
	        {
	            throw new IOException("Root tag must be a named compound tag");
	        }
	    }

	public static void func_35621_a(NBTTagCompound nbttagcompound, File file)
	        throws IOException
	    {
	        File file1 = new File((new StringBuilder()).append(file.getAbsolutePath()).append("_tmp").toString());
	        if(file1.exists())
	        {
	            file1.delete();
	        }
	        func_35620_b(nbttagcompound, file1);
	        if(file.exists())
	        {
	            file.delete();
	        }
	        if(file.exists())
	        {
	            throw new IOException((new StringBuilder()).append("Failed to delete ").append(file).toString());
	        } else
	        {
	            file1.renameTo(file);
	            return;
	        }
	    }

	    public static void func_35620_b(NBTTagCompound nbttagcompound, File file)
	        throws IOException
	    {
	        DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file));
	        try
	        {
	            func_1139_a(nbttagcompound, dataoutputstream);
	        }
	        finally
	        {
	            dataoutputstream.close();
	        }
	    }
	    
	    public static void func_1139_a(NBTTagCompound nbttagcompound, DataOutput dataoutput)
	            throws IOException
	        {
	            NBTBase.writeTag(nbttagcompound, dataoutput);
	        }

}
