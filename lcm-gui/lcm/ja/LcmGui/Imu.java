/* LCM type definition class file
 * This file was automatically generated by lcm-gen
 * DO NOT MODIFY BY HAND!!!!
 */

package LcmGui;
 
import java.io.*;
import java.util.*;
import lcm.lcm.*;
 
public final class Imu implements lcm.lcm.LCMEncodable
{
    public LcmGui.Header header;
    public double accel[];
    public double angular_rate[];
 
    public Imu()
    {
        accel = new double[3];
        angular_rate = new double[3];
    }
 
    public static final long LCM_FINGERPRINT;
    public static final long LCM_FINGERPRINT_BASE = 0xccf297d55d257707L;
 
    static {
        LCM_FINGERPRINT = _hashRecursive(new ArrayList<Class<?>>());
    }
 
    public static long _hashRecursive(ArrayList<Class<?>> classes)
    {
        if (classes.contains(LcmGui.Imu.class))
            return 0L;
 
        classes.add(LcmGui.Imu.class);
        long hash = LCM_FINGERPRINT_BASE
             + LcmGui.Header._hashRecursive(classes)
            ;
        classes.remove(classes.size() - 1);
        return (hash<<1) + ((hash>>63)&1);
    }
 
    public void encode(DataOutput outs) throws IOException
    {
        outs.writeLong(LCM_FINGERPRINT);
        _encodeRecursive(outs);
    }
 
    public void _encodeRecursive(DataOutput outs) throws IOException
    {
        this.header._encodeRecursive(outs); 
 
        for (int a = 0; a < 3; a++) {
            outs.writeDouble(this.accel[a]); 
        }
 
        for (int a = 0; a < 3; a++) {
            outs.writeDouble(this.angular_rate[a]); 
        }
 
    }
 
    public Imu(byte[] data) throws IOException
    {
        this(new LCMDataInputStream(data));
    }
 
    public Imu(DataInput ins) throws IOException
    {
        if (ins.readLong() != LCM_FINGERPRINT)
            throw new IOException("LCM Decode error: bad fingerprint");
 
        _decodeRecursive(ins);
    }
 
    public static LcmGui.Imu _decodeRecursiveFactory(DataInput ins) throws IOException
    {
        LcmGui.Imu o = new LcmGui.Imu();
        o._decodeRecursive(ins);
        return o;
    }
 
    public void _decodeRecursive(DataInput ins) throws IOException
    {
        this.header = LcmGui.Header._decodeRecursiveFactory(ins);
 
        this.accel = new double[(int) 3];
        for (int a = 0; a < 3; a++) {
            this.accel[a] = ins.readDouble();
        }
 
        this.angular_rate = new double[(int) 3];
        for (int a = 0; a < 3; a++) {
            this.angular_rate[a] = ins.readDouble();
        }
 
    }
 
    public LcmGui.Imu copy()
    {
        LcmGui.Imu outobj = new LcmGui.Imu();
        outobj.header = this.header.copy();
 
        outobj.accel = new double[(int) 3];
        System.arraycopy(this.accel, 0, outobj.accel, 0, 3); 
        outobj.angular_rate = new double[(int) 3];
        System.arraycopy(this.angular_rate, 0, outobj.angular_rate, 0, 3); 
        return outobj;
    }
 
}

