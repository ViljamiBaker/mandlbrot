#version 330 core

float PI = 3.14159265358979;

out vec4 FragColor;

in vec3 FragPos;

uniform int iterations;
uniform float bailout;


// if x of one of these is -1 then X is used to para it
// if x of one of these is -2 then Y is used to para it
// if x of one of these is  0 then it's y is used to para it

uniform vec2 ZSetR;
uniform vec2 ZSetI;

uniform vec2 ESetR;
//uniform vec2 ESetI;

uniform vec2 CSetR;
uniform vec2 CSetI;

// equ is written as
// z_n+1 = z_n^E + C
// z_0 = Z
// E = 2 for now

vec2 Step( vec2 z, vec2 c, float E);
vec2 ImagToReal(vec2 ab, float n);
vec3 RGBFROMHSV(vec3 hsv);
//vec2 compExp(vec2 a, vec2 b);

void main()
{
    vec2 Z = vec2(0);
    vec2 C = vec2(0);
    float E = 0;

    bool xIsUsed = false;
    bool yIsUsed = false;

    if(ZSetR.x == -1){
        xIsUsed = true;
        Z.x = FragPos.x;
    }else if(ZSetR.x == -2){
        yIsUsed = true;
        Z.x = FragPos.y;
    }else{
        Z.x = ZSetR.y;
    }

    if(ZSetI.x == -1){
        if(xIsUsed){FragColor = vec4(0,1,0,1); return;}
        xIsUsed = true;
        Z.y = FragPos.x;
    }else if(ZSetI.x == -2){
        if(yIsUsed){FragColor = vec4(0,0,1,1); return;}
        yIsUsed = true;
        Z.y = FragPos.y;
    }else{
        Z.y = ZSetI.y;
    }

    if(CSetR.x == -1){
        if(xIsUsed){FragColor = vec4(0,1,0,1); return;}
        xIsUsed = true;
        C.x = FragPos.x;
    }else if(CSetR.x == -2){
        if(yIsUsed){FragColor = vec4(0,0,1,1); return;}
        yIsUsed = true;
        C.x = FragPos.y;
    }else{
        C.x = CSetR.y;
    }

    if(CSetI.x == -1){
        if(xIsUsed){FragColor = vec4(0,1,0,1); return;}
        xIsUsed = true;
        C.y = FragPos.x;
    }else if(CSetI.x == -2){
        if(yIsUsed){FragColor = vec4(0,1,1,1); return;}
        yIsUsed = true;
        C.y = FragPos.y;
    }else{
        C.y = CSetI.y;
    }

    if(ESetR.x == -1){
        if(xIsUsed){FragColor = vec4(0,1,0,1); return;}
        xIsUsed = true;
        E = FragPos.x;
    }else if(ESetR.x == -2){
        if(yIsUsed){FragColor = vec4(1.0, 0.0, 0.87, 1.0); return;}
        yIsUsed = true;
        E = FragPos.y;
    }else{
        E = ESetR.y;
    }

    if(!xIsUsed||!yIsUsed){FragColor = vec4(1,0,0,1); return;}

    int stepsUntilEscape = -1;

    vec2 z = vec2(Z);

    for(int i = 0; i < iterations; i++) {
        z = Step(z,C, E);
        if(length(z)>bailout){
            stepsUntilEscape = i;
            break;
        }
    }

    if(stepsUntilEscape==-1){
        FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }

    float ratio = (float(stepsUntilEscape)/100.0);



    FragColor = vec4(RGBFROMHSV(vec3(mod(ratio,1.0),1.0,1.0)),1.0);
    //FragColor = vec4(1.0);
}

vec2 Step(vec2 z, vec2 c, float E){
    if(E == 2.0f){
        return vec2(pow(z.x,2.0)-pow(z.y,2)+c.x,2.0*z.x*z.y+c.y);
    }
    vec2 ztothee = ImagToReal(z,E);

    return ztothee + c;
}

vec3 RGBFROMHSV(vec3 hsv){
    float h = hsv.x;
    float s = hsv.y;
    float v = hsv.z;

    if (h == 1.0) {h = 0.0;}
    int i = int(h*6.0); 
    float f = h*6.0 - i;
    
    float w = v * (1.0 - s);
    float q = v * (1.0 - s * f);
    float t = v * (1.0 - s * (1.0 - f));
    
    if (i==0) {return vec3(v, t, w);}
    if (i==1) {return vec3(q, v, w);}
    if (i==2) {return vec3(w, v, t);}
    if (i==3) {return vec3(w, q, v);}
    if (i==4) {return vec3(t, w, v);}
    if (i==5) {return vec3(v, w, q);}
    return vec3(0,0,0);
}

vec2 ImagToReal(vec2 ab, float n){
    float a = ab.x;
    float b = ab.y;
    float r = sqrt(a*a+b*b);
    float theta = atan(b/a);
    return vec2(pow(r,n)*cos(n*theta),pow(r,n)*sin(n*theta));
}

/*vec2 compExp(vec2 e, vec2 z){
    vec2 r = vec2(0,0);
    float l = exp(z.x);
    r.x = l * cos(z.y);
    r.y = l * sin(z.y);
    return r;
}*/