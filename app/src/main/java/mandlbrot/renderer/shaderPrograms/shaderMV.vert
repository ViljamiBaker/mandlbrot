#version 330 core

layout(location = 0) in vec2 aPos;

out vec3 FragPos;

uniform vec2 CamPos;

uniform float CamZoom;
uniform float ScreenRatio;

void main()
{
   vec2 pos = (aPos/CamZoom-CamPos);
    FragPos = vec3(pos.x*ScreenRatio,pos.y,0);
    gl_Position = vec4(aPos.x, aPos.y, 0, 1.0);
}