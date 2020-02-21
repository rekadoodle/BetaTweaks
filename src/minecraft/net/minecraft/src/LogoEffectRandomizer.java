//Straight outta 1.3
//Used in mod_oldCustomLogo

package net.minecraft.src;

import java.util.Random;

class LogoEffectRandomizer
{

    public LogoEffectRandomizer(int i, int j, Random rand)
    {
    	a = b = (double)(10 + j) + rand.nextDouble() * 32D + (double)i;
    }

    public void update()
    {
        b = a;
        if(a > 0.0D)
        {
            c -= 0.59999999999999998D;
        }
        a += c;
        c *= 0.90000000000000002D;
        if(a < 0.0D)
        {
            a = 0.0D;
            c = 0.0D;
        }
    }

    public double a;
    public double b;
    private double c;
}
