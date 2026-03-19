#version 330 core

layout(location = 0) in vec2 aPos;

out vec3 FragPos;

uniform vec2 CamPos;

uniform float CamZoom;
uniform float ScreenRatio;

void main()
{
    vec2 pos = ((vec2(aPos.x/ScreenRatio,aPos.y)+CamPos)*CamZoom);
    FragPos = vec3(pos.x/ScreenRatio,pos.y,0);
    gl_Position = vec4(pos.x,pos.y,0,1);
}