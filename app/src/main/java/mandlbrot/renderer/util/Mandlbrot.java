package mandlbrot.renderer.util;

import org.joml.Vector2d;
import org.joml.Vector2f;

public class Mandlbrot {
    public static float[] CalcMandlbrot(Vector2f iPos, Vector2f ZSetR, Vector2f ZSetI, Vector2f CSetR, Vector2f CSetI, Vector2f ESetR, int iterations, float bailout){
        
		Vector2d Z = new Vector2d();
		Vector2d C = new Vector2d();
		float E = 0;

		boolean xIsUsed = false;
		boolean yIsUsed = false;

		if(ZSetR.x == -1){
			xIsUsed = true;
			Z.x = iPos.x;
		}else if(ZSetR.x == -2){
			yIsUsed = true;
			Z.x = iPos.y;
		}else{
			Z.x = ZSetR.y;
		}

		if(ZSetI.x == -1){
			if(xIsUsed){return new float[0];}
			xIsUsed = true;
			Z.y = iPos.x;
		}else if(ZSetI.x == -2){
			if(yIsUsed){return new float[0];}
			yIsUsed = true;
			Z.y = iPos.y;
		}else{
			Z.y = ZSetI.y;
		}

		if(CSetR.x == -1){
			if(xIsUsed){return new float[0];}
			xIsUsed = true;
			C.x = iPos.x;
		}else if(CSetR.x == -2){
			if(yIsUsed){return new float[0];}
			yIsUsed = true;
			C.x = iPos.y;
		}else{
			C.x = CSetR.y;
		}

		if(CSetI.x == -1){
			if(xIsUsed){return new float[0];}
			xIsUsed = true;
			C.y = iPos.x;
		}else if(CSetI.x == -2){
			if(yIsUsed){return new float[0];}
			yIsUsed = true;
			C.y = iPos.y;
		}else{
			C.y = CSetI.y;
		}

		if(ESetR.x == -1){
			if(xIsUsed){return new float[0];}
			xIsUsed = true;
			E = iPos.x;
		}else if(ESetR.x == -2){
			if(yIsUsed){return new float[0];}
			yIsUsed = true;
			E = iPos.y;
		}else{
			E = ESetR.y;
		}

		if(!xIsUsed||!yIsUsed){return new float[0];}

		int stepsUntilEscape = iterations;

		Vector2d z = new Vector2d(Z);

        float[] poses = new float[iterations*2];

		for(int i = 0; i < iterations; i++) {
			z = Step(z,C, E);
            poses[i*2] = (float)z.x;
            poses[i*2+1] = (float)z.y;
			if(z.length()>bailout){
				stepsUntilEscape = i+1;
				break;
			}
		}
        if(stepsUntilEscape<iterations){
			z = Step(z,C, E);
            poses[stepsUntilEscape*2] = (float)z.x;
            poses[stepsUntilEscape*2+1] = (float)z.y;
            stepsUntilEscape++;

        }
        float[] retval = new float[stepsUntilEscape*2];
        for (int i = 0; i < stepsUntilEscape*2; i++) {
            retval[i] = poses[i];
        }
        return retval;
    }

    public static Vector2d Step(Vector2d z, Vector2d c, float E){
        if(E == 2.0f){
            return new Vector2d(Math.pow(z.x,2.0)-Math.pow(z.y,2)+c.x,2.0f*z.x*z.y+c.y);
        }
        Vector2d ztothee = ImagToReal(z,E);
    
        return ztothee.add(c);
    }
    
    public static Vector2d ImagToReal(Vector2d ab, float n){
        double a = ab.x;
        double b = ab.y;
        double r = Math.sqrt(a*a+b*b);
        double theta = Math.atan(b/a);
        return new Vector2d(Math.pow(r,n)*Math.cos(n*theta),Math.pow(r,n)*Math.sin(n*theta));
    }
}
